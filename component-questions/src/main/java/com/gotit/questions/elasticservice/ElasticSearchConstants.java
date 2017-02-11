package com.gotit.questions.elasticservice;

public interface ElasticSearchConstants {
	// index names
	String INDEX_QUESTION = "questions:<TARGET>";
	String TYPE_QUESTION = "question";
	String INDEX_TARGET = "targets";
	String TYPE_TARGET = "target";
	String INDEX_TEST_PAPER = "testpapers:<TARGET>";
	String TYPE_TEST_PAPER = "testpaper";
	String INDEX_USER = "users";
	String TYPE_USER = "user";
	String INDEX_MARKS_RULE = "marksrules";
	String TYPE_MARKS_RULE = "marksrule";
	String INDEX_ORDER = "orders";
	String TYPE_ORDER = "order";
	
	// elastic query templates
	String BOOL_QUERY = "{ \"bool\": { <BOOL> } }";
	String SHOULD_QUERY = "\"should\": [ <SHOULD> ] ";
	String MATCH_QUERY = "{ \"match\": { \"<FEILD>\": \"<VALUE>\"}}";
	String NESTED_MATCH_QUERY = "{ \"nested\": { \"path\": \"<PATH>\", \"query\": { \"bool\": {\"must\": [ { \"match\": { \"<NESTED_FEILD>\": \"<VALUE>\"} } ] } } } }";
	String MUST_QUERY = "\"must\": [ <MUST> ] ";
	String FUNCTION_SCRORE_QUERY = "{ \"function_score\" : { <FUNCTION_SCORE> } } ";
	String RANDOM_SCORE_QUERY = "\"random_score\" : { <RANDOM_SCORE> }";
	String QUERY_QUERY = "\"query\" : <QUERY>";

	// place-holders
	String TARGET = "<TARGET>";
	String ALIAS_INDEX = "<INDEXNAME>:alias";
	String INDEX_NAME = "<INDEXNAME>";
	String FEILD = "<FEILD>";
	String VALUE = "<VALUE>";
	String PATH = "<PATH>";
	String NESTED_PATH = "<NESTED_FEILD>";
	String SHOULD = "<SHOULD>";
	String MUST = "<MUST>";
	String BOOL = "<BOOL>";
	String FUNCTION_SCORE = "<FUNCTION_SCORE>";
	String RANDOM_SCORE = "<RANDOM_SCORE>";
	String QUERY = "<QUERY>";
	
	
	//other constants
	String SEARCH_RESULT = "searchResult";
	String SEARCH_TIME = "searchTime";
	String TOTAL_NO_RECORDS = "totalNoRecords";
}
