package com.gotit.questions.controller;

import java.util.Map;

import org.apache.lucene.index.IndexNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gotit.entity.MarksRule;
import com.gotit.exceptions.ValidationException;
import com.gotit.questions.elasticservice.ElasticSearchConstants;
import com.gotit.questions.elasticservice.ElasticSearchService;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@Controller
@RequestMapping("api/marksRule")
public class MarksRuleController implements ElasticSearchConstants {

	@Autowired
	ElasticSearchService elasticSearchService;

	@Autowired
	QuestionUtil questionUtil;
	
	@RequestMapping(value = "/marksRule/save", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> saveMarksRule(@RequestBody MarksRule marksRuleEntity)
			throws ValidationException {
		Log.i(">>MarksRuleController.saveMarksRule() - " + marksRuleEntity);
		return elasticSearchService.saveMarksRule(marksRuleEntity);
	}

	@RequestMapping(value = "/marksRule/{ruleId}", method = RequestMethod.GET)
	public @ResponseBody MarksRule getMarksRuleById(@PathVariable String ruleId)
			throws IndexNotFoundException {
		Log.i(">>MarksRuleController.geMarksRuleById() - ruleId = " + ruleId);
		Map<String, Object> responseMap = elasticSearchService.searchById(INDEX_MARKS_RULE, TYPE_MARKS_RULE, ruleId);
		MarksRule marksRule = questionUtil.createObjectFromMap(responseMap, MarksRule.class);
		return marksRule;
	}

	@RequestMapping(value = "/marksRule/all", method = RequestMethod.GET)
	public @ResponseBody MarksRule[] getAllMarksRules() throws IndexNotFoundException {
		Log.i(">>MarksRuleController.getAllPositiveMarksRules()");
		Map<String, Object> elasticSearchResponse = elasticSearchService.searchByKeyword(INDEX_MARKS_RULE,
				TYPE_MARKS_RULE, "", null, 0, 1000, null, false);
		String marksRuleListString = questionUtil.getStringJSONFromObject(elasticSearchResponse.get(SEARCH_RESULT));
		MarksRule[] marksRulesList = questionUtil.createObjectFromString(marksRuleListString,
				MarksRule[].class);
		return marksRulesList;
	}

}
