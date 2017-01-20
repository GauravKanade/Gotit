package com.gotit.questions.controller;

import java.util.Map;

import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gotit.entity.Target;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@RestController
@RequestMapping(value = "/api/target")
public class TargetController implements ElasticSearchConstants {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;

	@RequestMapping(value = "/target/save", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveTarget(@RequestBody Target target) throws ValidationException {
		Log.i(">>TargetController.saveTarget() - target: " + target);
		validateTarget(target);
		return elasticSearchService.saveTarget(target);
	}

	private void validateTarget(Target target) throws ValidationException {
		if (target.getTargetId() == null)
			throw new ValidationException("Target object does not contain targetId");
		if (target.getSubjects() == null || target.getSubjects().size() == 0)
			throw new ValidationException("Target does not contain subjects");
		if (target.getTargetName() == null || target.getTargetName().length() == 0)
			throw new ValidationException("Target does not conatin a name");
	}

	@RequestMapping(value = "/target/{targetId}", method = RequestMethod.GET)
	public @ResponseBody Target getTargetById(@PathVariable(value = "targetId") String targetId)
			throws IndexNotFoundException {
		Log.i(">>TargetController.getTargetById() - targetId: " + targetId);
		Map<String, Object> elasticResponse = elasticSearchService.searchById(INDEX_TARGET, TYPE_TARGET, targetId);
		return questionUtil.createObjectFromMap(elasticResponse, Target.class);
	}

}
