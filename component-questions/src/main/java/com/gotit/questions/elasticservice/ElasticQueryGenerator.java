package com.gotit.questions.elasticservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ElasticQueryGenerator implements ElasticSearchConstants {

	public Map<String, Object> translateElasticResponse(SearchResponse response) {
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put(SEARCH_TIME, response.getTookInMillis());
		List<Map<String, Object>> complaintResponseList = new ArrayList<>();
		for (SearchHit searchHit : response.getHits().getHits()) {
			complaintResponseList.add(searchHit.getSource());
		}
		responseMap.put(SEARCH_RESULT, complaintResponseList);
		responseMap.put(TOTAL_NO_RECORDS, response.getHits().totalHits());
		return responseMap;
	}

	public void addSortQuery(SearchRequestBuilder searchRequestBuilder, String sort) {
		if (!ObjectUtils.isEmpty(sort)) {
			for (String sortRequest : sort.split(",")) {
				String sortAttribute = sortRequest.split("-")[0];
				String sortDirection = "asc";
				if (sortRequest.split("-").length == 2) {
					sortDirection = sortRequest.split("-")[1];
				}
				searchRequestBuilder.addSort(SortBuilders.fieldSort(sortAttribute)
						.order(sortDirection.toLowerCase().equals("desc") ? SortOrder.DESC : SortOrder.ASC));
			}
		}
	}

	public String generateElasticQuery(String indexName, String keyword, String query, boolean isRandom) {
		String shouldQuery = generateShouldQuery(keyword);
		String mustQuery = generateMustQuery(shouldQuery, query);
		String functionScoreQuery = generateFunctionScoreQuery(mustQuery, isRandom);
		return functionScoreQuery;
	}

	private String generateFunctionScoreQuery(String mustQuery, boolean isRandom) {
		String randomScoreQuery = RANDOM_SCORE_QUERY.replace(RANDOM_SCORE, "");
		String boolQuery = BOOL_QUERY.replace(BOOL, mustQuery);
		String query = QUERY_QUERY.replace(QUERY, boolQuery);
		String functionScoreDetails  = query + (isRandom? " , " + randomScoreQuery: "");
		String functionScoreQuery = FUNCTION_SCRORE_QUERY.replace(FUNCTION_SCORE, functionScoreDetails ); 
		return functionScoreQuery;
	}

	public String generateShouldQuery(String keyword) {
		String shouldQueryDetails = "";
		if (!ObjectUtils.isEmpty(keyword)) {
			for (String keywordSingle : keyword.split(" ")) {
				if (!ObjectUtils.isEmpty(shouldQueryDetails))
					shouldQueryDetails += " , ";
				String complaintBodyMatch = MATCH_QUERY.replace(FEILD, "questionBody").replace(VALUE, keywordSingle);
				shouldQueryDetails += complaintBodyMatch;
			}
		}
		return SHOULD_QUERY.replace(SHOULD, shouldQueryDetails);
	}

	public String generateMustQuery(String shouldQuery, String query) {
		String mustQueryDetails = BOOL_QUERY.replace(BOOL, shouldQuery);
		if (!ObjectUtils.isEmpty(query)) {
			for (String queryValue : query.split(" and ")) {
				mustQueryDetails += " , ";
				String key = queryValue.split("=")[0];
				String value = queryValue.split("=")[1];
				mustQueryDetails += MATCH_QUERY.replace(FEILD, key).replace(VALUE, value);
			}
		}
		return MUST_QUERY.replace(MUST, mustQueryDetails);
	}
}
