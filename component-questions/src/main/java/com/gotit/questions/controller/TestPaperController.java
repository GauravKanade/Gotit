package com.gotit.questions.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.lucene.index.IndexNotFoundException;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.constants.TestPaperConstants;
import com.gotit.entity.MarksRule;
import com.gotit.entity.Question;
import com.gotit.entity.Section;
import com.gotit.entity.Subject;
import com.gotit.entity.Target;
import com.gotit.entity.TestPaper;
import com.gotit.entity.TestSection;
import com.gotit.entity.Ticket;
import com.gotit.entity.User;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@RestController
@RequestMapping(value = "/api/practicePaper")
public class TestPaperController implements ElasticSearchConstants, TestPaperConstants {
	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;

	@Autowired
	TargetController targetController;

	@Autowired
	MarksRuleController marksRuleController;

	@RequestMapping(value = "/generate/{userId}/{targetId}/{subject}", method = RequestMethod.GET)
	public @ResponseBody TestPaper generatePracticePaper(@PathVariable(value = "userId", required = true) String userId,
			@PathVariable(value = "targetId", required = true) String targetId,
			@PathVariable(value = "subject", required = true) String subject)
			throws ValidationException, IndexNotFoundException {
		// validate user
		User user = validateAndGetUser(userId);
		if (ObjectUtils.isEmpty(user)) {
			Log.i(">>generatePracticePaper() - userId  is invalid");
			throw new ResourceNotFoundException("userId is invalid", null, userId);
		}

		// validate ticket availability
		Ticket ticket = validateAndGetTicket(user);
		if (ObjectUtils.isEmpty(ticket)) {
			Log.i(">>generatePracticePaper() - userId  " + userId + "has no available tickets");
			throw new ValidationException("userId " + userId + " has no available ticket");
		}

		Map<String, Object> targetMap = elasticSearchService.searchById(INDEX_TARGET, TYPE_TARGET, targetId);
		Target target = questionUtil.createObjectFromMap(targetMap, Target.class);
		// generate test paper
		TestPaper testPaper = generateTestPaper(userId, target, subject);
		Log.d(">>generatePracticePaper() - test paper generated successfully");
		// utilize the ticket and save the user information
		ticket.setTestPaperId(testPaper.getTestId());
		ticket.setTicketAvailable(false);
		ticket.setTarget(targetId);
		elasticSearchService.saveTestPaper(testPaper);
		elasticSearchService.saveUser(user);
		Log.d(">>generatePracticePaper() - user information saved");
		return testPaper;
	}

	@RequestMapping(value = "/evaluate", method = RequestMethod.POST)
	public @ResponseBody TestPaper evaluateTestPaper(@RequestBody Map<String, Object> requestMap)
			throws IndexNotFoundException, ValidationException {
		Log.i(">>evaluateTestPaper() - requestMap: " + requestMap);
		if (requestMap.containsKey("testId") && requestMap.containsKey("isAnswered")
				&& requestMap.containsKey("target")) {
			String indexName = questionUtil.generateTestPaperIndexName(requestMap.get("target").toString());

			TestPaper testPaper = elasticSearchService.searchById(indexName, TYPE_TEST_PAPER,
					requestMap.get("testId").toString(), TestPaper.class);

			if (!testPaper.isAnswered())
				testPaper.setAnswered(Boolean.parseBoolean(requestMap.get("isAnswered").toString()));
			if (testPaper.isEvaluated() || !testPaper.isAnswered()) {
				Log.i("Not evaluating paper isAnswered=false or isEvaluated=true");
				return testPaper;
			}

			Target target = targetController.getTargetById(testPaper.getTarget());
			Subject targetSubject = null;
			for (Subject subject : target.getSubjects()) {
				if (subject.getSubjectName().equals(testPaper.getSubject())) {
					targetSubject = subject;
					break;
				}
			}
			evaluateTestPaper(testPaper, targetSubject);

			testPaper.setEvaluated(true);
			testPaper.setCompletedOn(System.currentTimeMillis());
			elasticSearchService.saveTestPaper(testPaper);

			// update information on the corresponding ticket
			updateUserTicket(testPaper);
			Log.i("<<evaluateTestPaper() - evaluation of testpaper " + testPaper.getTestId() + ", completed");
			return testPaper;
		} else {
			ValidationException validationException = new ValidationException(
					"missing feilds - testId or targetId or isAnswered");
			Log.e(">>cant evaluate paper", validationException);
			throw validationException;
		}
	}

	private void updateUserTicket(TestPaper testPaper) throws IndexNotFoundException {
		Map<String, Object> userMap = elasticSearchService.searchById(INDEX_USER, TYPE_USER, testPaper.getUserId());
		User user = questionUtil.createObjectFromMap(userMap, User.class);
		for (Ticket ticket : user.getTickets()) {
			if (!ticket.isTicketAvailable() && ticket.getTestPaperId().equals(testPaper.getTestId())) {
				ticket.setCompleted(true);
				break;
			}
		}
		elasticSearchService.saveUser(user);
	}

	public void evaluateTestPaper(TestPaper testPaper, Subject subject) throws IndexNotFoundException {
		testPaper.setMarksObatined(0);
		int wrongAnswerCount = 0;
		double totalMarks = 0;
		for (TestSection testSection : testPaper.getTestSection()) {
			MarksRule marksRule = getMarksRuleForSection(testSection, subject);

			for (Question question : testSection.getQuestions()) {
				question.setMarksObtained(0);
				if (question.getAnswer().length == 0) {
					if (ObjectUtils.isEmpty(question.getAnswerByUser())) {
						question.setMarksObtained(question.getMarks());
					} else {
						wrongAnswerCount++;
					}
				} else {
					if (!ObjectUtils.isEmpty(question.getAnswerByUser())) {
						int[] expectedAnswers = question.getAnswer();
						int[] answerByUser = question.getAnswerByUser();
						// compare and add marks
						int matches = 0;
						for (int expectedAnswerToken : expectedAnswers)
							for (int userAnswer : answerByUser)
								if (expectedAnswerToken == userAnswer)
									matches++;
						if (expectedAnswers.length >= answerByUser.length) {
							if (matches == expectedAnswers.length) {
								question.setMarksObtained(question.getMarks());
							}
							if (matches != 0) {
								if (marksRule.provideFullMarksOnPartialAnswer()) {
									question.setMarksObtained(question.getMarks());
								} else if (marksRule.provideFractionalMarksOnPartialAnswer()) {
									question.setMarksObtained(
											(double) (matches / expectedAnswers.length) * question.getMarks());
								} else {
									wrongAnswerCount++;
								}
							}
						} else if (marksRule.ignoreExtraAnswers()) {
							if (matches == expectedAnswers.length) {
								question.setMarksObtained(question.getMarks());
							} else if (marksRule.provideFullMarksOnPartialAnswer()) {
								question.setMarksObtained(question.getMarks());
							} else if (marksRule.provideFractionalMarksOnPartialAnswer()) {
								question.setMarksObtained(
										(double) (matches / expectedAnswers.length) * question.getMarks());
							} else {
								wrongAnswerCount++;
							}
						} else {
							if (marksRule.assignZeroMarksOnExtraAnswer()) {
								question.setMarksObtained(0);
								wrongAnswerCount++;
							} else if (marksRule.negateFullMarksOnExtraAnswer()) {
								question.setMarksObtained(-1 * question.getMarks());
							}
						}
					}
				}
				totalMarks += question.getMarksObtained();
			}

			// handle negative marking
			Log.i("-> Number of wrong answers: " + wrongAnswerCount);
			if (marksRule.getNumberOfWrongAnswersForNegativeMarking() != 0) {
				int negativeMarkingSet = wrongAnswerCount / marksRule.getNumberOfWrongAnswersForNegativeMarking();
				double negativeMarks = negativeMarkingSet * marksRule.getNegativeMarks();
				Log.i("-> Number of wrong answer sets: " + negativeMarkingSet + ", Negative marks: " + negativeMarks);
				totalMarks -= negativeMarks;
			}

		}
		Log.i("-> Total marks for testId: " + testPaper.getTestId() + " is " + totalMarks);
		testPaper.setMarksObatined(totalMarks);
	}

	private MarksRule getMarksRuleForSection(TestSection testSection, Subject subject) throws IndexNotFoundException {
		for (Section section : subject.getSections()) {
			if (testSection.getSection().equals(section.getSection())) {
				String ruleId = section.getRuleId();
				if (!ObjectUtils.isEmpty(ruleId))
					return marksRuleController.getMarksRuleById(ruleId);
			}
		}
		return getDefaultMarksRule();
	}

	private MarksRule getDefaultMarksRule() {
		MarksRule marksRule = new MarksRule();
		marksRule.setRuleId("Default");
		marksRule.setIgnoreExtraAnswers(true);
		marksRule.setProvideFullMarksOnPartialAnswer(true);
		marksRule.setNumberOfWrongAnswersForNegativeMarking(0);
		return marksRule;
	}

	@RequestMapping(value = "/{target}/{testId}", method = RequestMethod.GET)
	public @ResponseBody TestPaper getTestPaperById(@PathVariable(value = "testId") String testId,
			@PathVariable(value = "target") String target) throws IndexNotFoundException {
		String indexName = questionUtil.generateTestPaperIndexName(target);
		Map<String, Object> elasticResponseMap = elasticSearchService.searchById(indexName, TYPE_TEST_PAPER, testId);
		if (!ObjectUtils.isEmpty(elasticResponseMap))
			return questionUtil.createObjectFromMap(elasticResponseMap, TestPaper.class);
		return null;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveAnswer", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveAnswer(@RequestBody Map<String, Object> answerMap)
			throws IndexNotFoundException, ValidationException {
		Log.i(">>PracticeTestController.saveAnswer() - answerMap:" + answerMap);
		if (answerMap.containsKey("questionId") && answerMap.containsKey("targetId")
				&& answerMap.containsKey("answerByUser") && answerMap.containsKey("testId")) {
			String testId = answerMap.get("testId").toString();
			String questionId = answerMap.get("questionId").toString();
			String targetId = answerMap.get("targetId").toString();
			String indexName = questionUtil.generateTestPaperIndexName(targetId);
			long timeRemaiaing = Long.parseLong(answerMap.get("timeRemaining").toString());
			List<Integer> answersByUserList = (List<Integer>) answerMap.get("answerByUser");
			int[] answersByUser = new int[answersByUserList.size()];
			for (int i = 0; i < answersByUser.length; i++)
				answersByUser[i] = answersByUserList.get(i);
			Map<String, Object> testPaperMap = elasticSearchService.searchById(indexName, TYPE_TEST_PAPER, testId);
			TestPaper testPaper = questionUtil.createObjectFromMap(testPaperMap, TestPaper.class);
			appendAnswerToTestPaper(testPaper, questionId, answersByUser);
			testPaper.setTimeRemaining(timeRemaiaing);
			Log.i("testPaper to be appended: " + testPaper.toString());
			return elasticSearchService.saveTestPaper(testPaper);
		}
		ValidationException validationException = new ValidationException(
				"saveAnswer() does not contain questionId, or targetId or answerByUser");
		Log.e("Gotit.saveAnswer()", validationException);
		throw validationException;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> updateTestPaper(@RequestBody Map<String, Object> requestMap)
			throws ValidationException, IndexNotFoundException {
		Log.i(">>PracticePaper.updateTestPaper() - " + requestMap);
		if (requestMap.containsKey(TEST_ID_TAG) && requestMap.containsKey(TARGET_TAG)) {
			String indexName = questionUtil.generateTestPaperIndexName(requestMap.get(TARGET_TAG).toString());
			Map<String, Object> testPaperMap = elasticSearchService.searchById(indexName, TYPE_TEST_PAPER,
					requestMap.get(TEST_ID_TAG).toString());
			Map<String, Object> updatedTestPaperMap = questionUtil.merge(testPaperMap, requestMap);
			TestPaper updatedTestPaper = questionUtil.createObjectFromMap(updatedTestPaperMap, TestPaper.class);
			return elasticSearchService.saveTestPaper(updatedTestPaper);
		} else {
			ValidationException validationException = new ValidationException(
					"requestMap does not contain testId or target");
			Log.e("exception in processing update", validationException);
			throw validationException;
		}
	}

	/**
	 * 
	 * @param userId
	 * @param page
	 * @param requestMapList
	 * @return
	 * @throws ValidationException
	 * @throws IndexNotFoundException
	 */
	@RequestMapping(value = "/sync", method = RequestMethod.POST)
	public @ResponseBody List<TestPaper> syncTestPapers(@RequestParam(value = "userId", required = true) String userId,
			@RequestParam(value = "page", defaultValue = "0") String page,
			@RequestBody List<Map<String, Object>> requestMapList) throws ValidationException, IndexNotFoundException {
		Log.i(">>PracticePaperController.syncTestPapers() - requestMap: " + requestMapList);
		List<TestPaper> testPaperList = new ArrayList<>();
		Map<String, Object> userMap = elasticSearchService.searchById(INDEX_USER, TYPE_USER, userId);
		User user = questionUtil.createObjectFromMap(userMap, User.class);

		for (Ticket ticket : user.getTickets()) {
			if (!ticket.isTicketAvailable()) {
				String testIndexName = questionUtil.generateTestPaperIndexName(ticket.getTarget());
				Map<String, Object> testPaperMap = elasticSearchService.searchById(testIndexName, TYPE_TEST_PAPER,
						ticket.getTestPaperId());
				TestPaper testPaper = questionUtil.createObjectFromMap(testPaperMap, TestPaper.class);
				// TODO: Add logic to check existing updated time of the test
				// paper
				testPaperList.add(testPaper);
			}
		}

		return testPaperList;
	}

	private void appendAnswerToTestPaper(TestPaper testPaper, String questionId, int[] answersByUser) {
		for (TestSection testSection : testPaper.getTestSection()) {
			for (Question question : testSection.getQuestions()) {
				if (question.getQuestionId().equals(questionId)) {
					question.setAnswerByUser(answersByUser);
					break;
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private TestPaper generateTestPaper(String userId, Target target, String subject) throws ValidationException {
		// Get the target object from targetId
		try {

			// Generate the testPaper object
			TestPaper testPaper = new TestPaper();
			testPaper.setTestId(RandomStringUtils.randomAlphabetic(6) + "-"
					+ String.valueOf(System.currentTimeMillis()).substring(3));
			testPaper.setUserId(userId);
			testPaper.setCreatedOn(System.currentTimeMillis());
			testPaper.setTarget(target.getTargetId());
			testPaper.setSubject(subject);

			// get required sections associated with the subject
			Subject targetSubject = null;
			for (Subject subjectObj : target.getSubjects()) {
				if (subjectObj.getSubjectName().equalsIgnoreCase(subject)) {
					targetSubject = subjectObj;
					break;
				}
			}
			if (ObjectUtils.isEmpty(targetSubject)) {
				ValidationException validationException = new ValidationException("Subject '" + subject + "'not found");
				Log.e("Exception in generating paper", validationException);
				throw validationException;
			}
			testPaper.setTimeInMinutes(targetSubject.getTimeInMinutes());

			String indexName = questionUtil.generateQuestionIndexName(target.getTargetId());
			List<TestSection> testSectionList = new ArrayList<>();
			int totalMarks = 0;
			// get list of sections for the subject
			for (Section section : targetSubject.getSections()) {
				TestSection testSection = new TestSection();
				testSection.setSection(section.getSection());
				testSection.setDescription(section.getDescription());
				List<Question> sectionQuestionList = new ArrayList<>();

				int numberOfQuestionsRequired = section.getNumberOfQuestions();
				String elasticQuery = MessageFormat.format("subject={0} and marks={1}", subject,
						String.valueOf(section.getMarksPerQuestion()));

				String keyword = ObjectUtils.isEmpty(section.getChapters())
						? (ObjectUtils.isEmpty(targetSubject.getChapters()) ? ""
								: String.join(",", targetSubject.getChapters()))
						: String.join(",", section.getChapters());
				Log.d(MessageFormat.format("-> keyword: {0}, query: {1} for section: {2}", keyword, elasticQuery,
						section.getSection()));

				Map<String, Object> questionSearchResponseMap = elasticSearchService.searchByKeyword(indexName,
						TYPE_QUESTION, keyword, elasticQuery, 0, numberOfQuestionsRequired, null, true);
				List<Map<String, Object>> searchedQuestions = (List<Map<String, Object>>) questionSearchResponseMap
						.get(SEARCH_RESULT);

				int totalNoQuestionsAvailable = Integer
						.valueOf(questionSearchResponseMap.get(TOTAL_NO_RECORDS).toString());
				if (totalNoQuestionsAvailable < numberOfQuestionsRequired) {
					ValidationException validationException = new ValidationException(MessageFormat.format(
							">>generateTetsPaper() - Sufficient questions are not available. Required: {0}, Available: {1}, indexName: {2}, subject: {3} section: {4}",
							numberOfQuestionsRequired, totalNoQuestionsAvailable, indexName,
							targetSubject.getSubjectName(), section.getSection()));
					Log.e("Exception in generating test paper", validationException);
					throw validationException;
				}

				for (int i = 0; i < searchedQuestions.size(); i++) {
					Question question = questionUtil.createObjectFromMap(searchedQuestions.get(i), Question.class);
					sectionQuestionList.add(question);
					totalMarks += question.getMarks();
				}
				Question[] questions = new Question[sectionQuestionList.size()];
				sectionQuestionList.toArray(questions);
				testSection.setQuestions(questions);
				testSectionList.add(testSection);
			}

			// set the sectionList to the generated paper

			TestSection[] sectionTestPapers = new TestSection[testSectionList.size()];
			testSectionList.toArray(sectionTestPapers);
			testPaper.setTotalMarks(totalMarks);
			testPaper.setTestSection(sectionTestPapers);
			return testPaper;
		} catch (IndexNotFoundException e) {
			Log.e(">>generateTestPaper(); - something went wrong. May be the index was not found", e);
			e.printStackTrace();
			return null;
		}
	}

	private Ticket validateAndGetTicket(User user) {
		Ticket ticket = null;
		if (!ObjectUtils.isEmpty(user.getTickets())) {
			for (Ticket userTicket : user.getTickets()) {
				if (userTicket.isTicketAvailable())
					ticket = userTicket;
			}
			if (ObjectUtils.isEmpty(ticket)) {
				Log.i(MessageFormat.format(">>validateAndGetTicket() - user with id {0} has no un-used tickets",
						user.getUserId()));
			}
		}
		return ticket;
	}

	private User validateAndGetUser(String userId) {
		try {
			Map<String, Object> userMap = elasticSearchService.searchById(INDEX_USER, TYPE_USER, userId);
			User user = questionUtil.createObjectFromMap(userMap, User.class);
			if (ObjectUtils.isEmpty(user)) {
				Log.i(MessageFormat.format(">>validateAndGetTicket(); - user with id {0} not found", userId));
				return null;
			}
			return user;
		} catch (Exception e) {
			Log.e(">>validateAndGetUser() - user Not found", e);
		}
		return null;
	}
}
