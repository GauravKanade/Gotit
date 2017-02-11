package com.gotit.questions.controller;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.entity.Question;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;

@RestController
public class TestDataController {

	@Autowired
	QuestionUtil questionUtil;

	@Autowired
	ElasticSearchService elasticSearchService;

	@RequestMapping(value = "/api/testdata/generate", method = RequestMethod.POST)
	public void generateTestData(@RequestBody Map<String, Object> requestMap) {
		String targetId = requestMap.get("target").toString();
		String numberOfQuestions = requestMap.get("numberOfQuestions").toString();
		String subject = requestMap.get("subject").toString();
		int marksPerSubject = Integer.parseInt(requestMap.get("marksPerQuestion").toString());
		generateQuestions(subject, targetId, Integer.parseInt(numberOfQuestions), marksPerSubject);
	}

	private void generateQuestions(String subject, String targetId, int numberOfQuestions, int marksPerSubject) {
		for (int i = 1; i <= numberOfQuestions; i++) {
			Question question = new Question();
			String questionId = (String.valueOf(System.currentTimeMillis()).substring(8) + "_" + targetId + "_"
					+ subject + "_" + i).toLowerCase().replace(" ", "");
			question.setQuestionId(questionId);
			question.setTarget(targetId);
			question.setSubject(subject);
			question.setChapter("chapter" + RandomStringUtils.randomNumeric(1));
			question.setQuestionBody("What is the correct answer?");
			if (subject.equalsIgnoreCase("mathematics")) {
				question.setChoices(new String[] { "@equation=$$ax^2 + bx + c = 0$$",
						"@equation=$$\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$$", "@equation=$$x^3$$",
						"None of the above" });
			} else {
				question.setChoices(new String[] { "Option1", "Option2", "Option3", "Option4" });
			}
			question.setAnswer(new int[] { ((int) (Math.random() * 3687643) % 4) + 1 });
			question.setMarks(marksPerSubject);
			elasticSearchService.saveQuestion(question);
		}
	}
}
