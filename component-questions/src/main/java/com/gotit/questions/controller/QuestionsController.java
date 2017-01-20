package com.gotit.questions.controller;

import java.util.Map;

import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gotit.entity.Question;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@Controller
@RequestMapping("/api/questions")
public class QuestionsController implements ElasticSearchConstants {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;

	@RequestMapping(value = "/question/save", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveQuestions(@RequestBody Question questionEntity)
			throws ValidationException {
		Log.i(">>QuestionController.saveQuestion()");
		validateQuestion(questionEntity);
		return elasticSearchService.saveQuestion(questionEntity);
	}

	private void validateQuestion(Question question) throws ValidationException {
		if (question.getQuestionId() == null) {
			question.setQuestionId(String.valueOf(System.currentTimeMillis()));
		}
		if (question.getAnswer() == null || question.getAnswer().length == 0)
			throw new ValidationException("question does not contain an answer");
		if (question.getChoices() == null || question.getChoices().length == 0)
			throw new ValidationException("question does not contain choices");
		if (question.getTargetId() == null)
			throw new ValidationException("targetId is not given");
		try{
			if (ObjectUtils.isEmpty(elasticSearchService.searchById(INDEX_TARGET, TYPE_TARGET, question.getTargetId())))
				throw new ValidationException("invalid target");
		} catch(IndexNotFoundException e){
			throw new ValidationException("invalid target");
		}
			
		if (question.getMarks() == 0)
			throw new ValidationException("marks cannot be 0");
		if (question.getQuestionBody() == null)
			throw new ValidationException("question is mising a questionBody");
	}

	@RequestMapping(value = "/question/questionId/{questionId}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getQuestionById(@PathVariable("questionId") String questionId) {
		Map<String, Object> responseMap = elasticSearchService.searchByAliasName(INDEX_QUESTION.replace(TARGET, "*"),
				TYPE_QUESTION, questionId);
		return responseMap;
	}

	@RequestMapping(value = "/question/{category}/{questionId}", method = RequestMethod.GET)
	public @ResponseBody Question getQuestionBCategoryAndId(@PathVariable("questionId") String questionId,
			@PathVariable("category") String category) throws IndexNotFoundException {
		String indexName = questionUtil.generateQuestionIndexName(category);
		Map<String, Object> elasticResponse = elasticSearchService.searchById(indexName, TYPE_QUESTION, questionId);
		Question questionEntity = questionUtil.createObjectFromMap(elasticResponse, Question.class);
		return questionEntity;
	}

	@RequestMapping(value = "/question/search/{category}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> searchQuestions(
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
			@RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "random", defaultValue = "false") String random,
			@PathVariable(value = "category") String category) {
		String indexName = questionUtil.generateQuestionIndexName(category);
		return elasticSearchService.searchByKeyword(indexName, TYPE_QUESTION, keyword, query,
				Integer.valueOf(pageNumber), Integer.valueOf(pageSize), sort, Boolean.parseBoolean(random));
	}
}
