package com.gotit.questions.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexNotFoundException;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.entity.Answer;
import com.gotit.entity.Question;
import com.gotit.entity.Subject;
import com.gotit.entity.Target;
import com.gotit.entity.TestPaper;
import com.gotit.entity.Ticket;
import com.gotit.entity.User;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@RestController
@RequestMapping(value = "/api/practicePaper")
public class PracticeTestController implements ElasticSearchConstants {
	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;

	@RequestMapping(value = "/generate/{userId}/{targetId}/{subject}", method = RequestMethod.GET)
	public @ResponseBody TestPaper generatePracticePaper(@PathVariable(value = "userId", required = true) String userId,
			@PathVariable(value = "targetId", required = true) String targetId,
			@PathVariable(value = "subject", required = true) String subject) throws ValidationException {
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

		// generate test paper
		TestPaper testPaper = generateTestPaper(userId, targetId, subject);
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
	public @ResponseBody TestPaper evaluateTestPaper(@RequestBody TestPaper testPaper) {
		Log.i(">>evaluateTestPaper()");
		elasticSearchService.saveTestPaper(testPaper);
		if (testPaper.isAnswered())
			return testPaper;
		testPaper.setMarksObatined(0);
		if (!ObjectUtils.isEmpty(testPaper.getAnswersByUser())) {
			List<Answer> answers = testPaper.getAnswersByUser();
			Map<String, Question> questionToCorrectAnswerMap = new HashMap<>();
			for (Question question : testPaper.getQuestions()) {
				questionToCorrectAnswerMap.put(question.getQuestionId(), question);
			}

			int totalMarks = 0;

			for (Answer answer : answers) {
				List<String> userAnswerValues = Arrays.asList(answer.getAnswer());
				Question question = questionToCorrectAnswerMap.get(answer.getQuestionId());
				String expectedAnswers[] = question.getAnswer();
				// compare and add marks
				int matches = 0;
				for (String expectedAnswerToken : expectedAnswers)
					if (userAnswerValues.contains(expectedAnswerToken))
						matches++;
				// TODO: Handle negative marking
				if (matches / (double) expectedAnswers.length >= 0.5)
					answer.setMarksObtained(question.getMarks());
				totalMarks += answer.getMarksObtained();
			}
			testPaper.setMarksObatined(totalMarks);
			testPaper.setEvaluated(true);
			Log.i("<<evaluateTestPaper() - evaluation of testpaper " + testPaper.getTestId() + ", completed");
			elasticSearchService.saveTestPaper(testPaper);
		}
		return testPaper;
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
	private TestPaper generateTestPaper(String userId, String targetId, String subject) throws ValidationException {
		// Get the target object from targetId
		try {
			Map<String, Object> targetMap = elasticSearchService.searchById(INDEX_TARGET, TYPE_TARGET, targetId);

			Target target = questionUtil.createObjectFromMap(targetMap, Target.class);

			// get required papaerFormatId associated with the subject
			int numberOfQuestionsRequired = 0;
			for (Subject targetSubject : target.getSubjects())
				if (targetSubject.getSubjectName().equalsIgnoreCase(subject))
					numberOfQuestionsRequired = targetSubject.getNumberOfQuestions();

			// Generate the testPaper object
			TestPaper testPaper = new TestPaper();
			testPaper.setUserId(userId);
			testPaper.setCreatedOn(System.currentTimeMillis());
			testPaper.setTarget(targetId);
			testPaper.setSubject(subject);
			testPaper.setTestId(String.valueOf(System.currentTimeMillis()) + userId);
			List<Question> questionList = new ArrayList<>();

			// get list of questions
			String indexName = questionUtil.generateQuestionIndexName(targetId);
			String query = MessageFormat.format("subject={0}", subject);
			Map<String, Object> questionSearchResponseMap = elasticSearchService.searchByKeyword(indexName,
					TYPE_QUESTION, "", query, 0, numberOfQuestionsRequired, null, true);
			List<Map<String, Object>> searchedQuestions = (List<Map<String, Object>>) questionSearchResponseMap
					.get(SEARCH_RESULT);
			int totalNoQuestionsAvailable = Integer.valueOf(questionSearchResponseMap.get(TOTAL_NO_RECORDS).toString());

			if (totalNoQuestionsAvailable < numberOfQuestionsRequired) {

				ValidationException validationException = new ValidationException(MessageFormat.format(
						">>generateTetsPaper() - Sufficient questions are not available. Required: {0}, Available: {1}, indexName: {2}",
						numberOfQuestionsRequired, totalNoQuestionsAvailable, indexName));
				Log.e(MessageFormat.format(
						">>generateTetsPaper() - Sufficient questions are not available. Required: {0}, Available: {1}, indexName: {2}",
						numberOfQuestionsRequired, totalNoQuestionsAvailable, indexName), validationException);
				throw validationException;
			}
			int totalMarks = 0;
			for (int i = 0; i < searchedQuestions.size(); i++) {
				Question question = questionUtil.createObjectFromMap(searchedQuestions.get(i), Question.class);
				questionList.add(question);
				totalMarks += question.getMarks();
			}
			testPaper.setTotalMarks(totalMarks);
			testPaper.setQuestions(questionList);
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
