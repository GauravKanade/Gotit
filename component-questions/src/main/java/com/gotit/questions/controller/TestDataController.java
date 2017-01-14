package com.gotit.questions.controller;

import java.util.Map;

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
		String targetId = requestMap.get("targetId").toString();
		String numberOfQuestions = requestMap.get("numberOfQuestions").toString();
		String subject = requestMap.get("subject").toString();
		generateQuestions(subject, targetId, Integer.parseInt(numberOfQuestions));
	}

	private void generateQuestions(String subject, String targetId, int numberOfQuestions) {
		for (int i = 1; i <= numberOfQuestions; i++) {
			Question question = new Question();
			String questionId = (targetId + "_" + subject + "_" + i).toLowerCase().replace(" ", "");
			question.setQuestionId(questionId);
			question.setTargetId(targetId);
			question.setSubject(subject);
			question.setQuestionBody("What is the correct answer?");
			question.setChoices(new String[] { "Option1", "Option2", "Option3", "Option4" });
			question.setAnswer(new String[] { "Option" + String.valueOf(((int) (Math.random() * 3687643) % 4) + 1) });
			question.setMarks(1);
			elasticSearchService.saveQuestion(question);
		}
	}
}
