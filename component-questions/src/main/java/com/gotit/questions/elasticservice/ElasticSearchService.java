package com.gotit.questions.elasticservice;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.lucene.index.IndexNotFoundException;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.gotit.entity.Question;
import com.gotit.entity.Target;
import com.gotit.entity.TestPaper;
import com.gotit.entity.User;
import com.gotit.questions.util.QuestionUtil;
import com.gotit.util.Log;

@Component
public class ElasticSearchService implements ElasticSearchConstants {

	@Autowired
	QuestionUtil questionUtil;
	@Autowired
	ElasticQueryGenerator elasticQueryGenerator;
	TransportClient client;

	public ElasticSearchService() {
		try {
			if (ObjectUtils.isEmpty(client)) {
				client = new PreBuiltTransportClient(Settings.EMPTY)
						.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9200))
						.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
				Log.i("<<elasticSearchService - client initialized");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applySettingsMapping(String indexName) {
		String settingsJSON = questionUtil.readFileAsString("settings.json");
		Log.i(MessageFormat.format("settings JSON: {0}", settingsJSON));
		boolean indexCreated = client.admin().indices().prepareCreate(indexName).setSettings(settingsJSON).execute()
				.actionGet().isAcknowledged();
		Log.i("Settings applied: " + indexCreated);
		if (indexCreated) {
			String mappingsJSON = questionUtil.readFileAsString("mapping.json");
			Log.i(MessageFormat.format("mappings JSON: {0}", mappingsJSON));
			boolean isMapingApplied = client.admin().indices().preparePutMapping(indexName).setSource(mappingsJSON)
					.setType(TYPE_QUESTION).execute().actionGet().isAcknowledged();
			Log.i("Mappings Applied: " + isMapingApplied);
			if (isMapingApplied) {
				String aliasName = ALIAS_INDEX.replace(INDEX_NAME, indexName);
				boolean isAliasApplied = client.admin().indices().prepareAliases().addAlias(indexName, aliasName).get()
						.isAcknowledged();
				Log.i("Alias Applied: " + isAliasApplied);
			}
		}
	}

	public Map<String, Object> saveQuestion(Question questionEntity) {
		Log.i(">>ElasticSearchService.saveComplaint() - questionEntity: " + questionEntity);
		String indexName = questionUtil.generateQuestionIndexName(questionEntity);
		Log.d(">>ElasticSearhService.saveQuestion() - question Index: " + indexName);
		if (!client.admin().indices().prepareExists(indexName).execute().actionGet().isExists()) {
			applySettingsMapping(indexName);
		}
		Map<String, Object> entityMap = questionUtil.getMapFromObject(questionEntity);
		alterEntity(entityMap);

		String indexJson = questionUtil.getStringJSONFromObject(entityMap);
		IndexResponse indexResponse = index(indexName, TYPE_QUESTION, String.valueOf(questionEntity.getQuestionId()),
				indexJson);
		Log.d(">>ElasticSearhService.saveQuestion() - indexResponse:" + indexResponse);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("questionId", indexResponse.getId());
		responseMap.put("indexName", indexResponse.getIndex());
		responseMap.put("indexType", indexResponse.getType());
		responseMap.put("version", indexResponse.getVersion());

		return responseMap;
	}

	public Map<String, Object> saveTarget(Target target) {
		Log.i(">>saveTarget() - targetEntity: " + target);

		Map<String, Object> entityMap = questionUtil.getMapFromObject(target);
		alterEntity(entityMap);

		String indexJson = questionUtil.getStringJSONFromObject(entityMap);
		IndexResponse indexResponse = index(INDEX_TARGET, TYPE_TARGET, String.valueOf(target.getTargetId()), indexJson);
		Log.d(">>ElasticSearhService.saveTarget() - indexResponse:" + indexResponse);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("targetId", indexResponse.getId());
		responseMap.put("indexName", indexResponse.getIndex());
		responseMap.put("indexType", indexResponse.getType());
		responseMap.put("version", indexResponse.getVersion());

		return responseMap;
	}

	public Map<String, Object> searchByAliasName(String indexName, String indexType, String id) {
		Map<String, Object> responseMap = new HashMap<>();
		List<String> allIndices = new ArrayList<>();
		try {
			ImmutableOpenMap<String, List<AliasMetaData>> immutableDataMap = client.admin().indices()
					.getAliases(new GetAliasesRequest(indexName)).get().getAliases();

			immutableDataMap.keysIt().forEachRemaining(allIndices::add);
			MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();
			for (String physyicalIndexName : allIndices) {
				multiGetRequestBuilder.add(physyicalIndexName, indexType, id);
			}
			MultiGetResponse multiGetResponse = multiGetRequestBuilder.get();
			for (MultiGetItemResponse multiGetItemResponse : multiGetResponse.getResponses()) {
				if (multiGetItemResponse.getResponse().isExists()) {
					Map<String, Object> hitSource = multiGetItemResponse.getResponse().getSource();
					responseMap.put(hitSource.get("category").toString(), hitSource);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMap;
	}

	private IndexResponse index(String indexName, String type, String id, String json) {
		return client.prepareIndex(indexName, type, id).setSource(json).get();
	}

	private void alterEntity(Map<String, Object> entityMap) {
		// nothing to alter
	}

	public Map<String, Object> searchById(String indexName, String indexType, String questionId)
			throws IndexNotFoundException {
		GetResponse getResponse = client.prepareGet(indexName, indexType, questionId).get();
		return getResponse.getSource();
	}

	public Map<String, Object> searchByKeyword(String indexName, String indexType, String keyword, String query,
			int pageNumber, int pageSize, String sort, boolean isRandom) {
		String queryJSON = elasticQueryGenerator.generateElasticQuery(indexName, keyword, query, isRandom);
		Log.d(">>ElasticSearhService.searchByKeyword() - query:" + queryJSON);
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(indexType)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		searchRequestBuilder.setQuery(QueryBuilders.wrapperQuery(queryJSON));

		int start = pageNumber == 0 ? 0 : pageSize * (pageNumber - 1) + 1;
		searchRequestBuilder.setFrom(start).setSize(pageSize);

		elasticQueryGenerator.addSortQuery(searchRequestBuilder, sort);

		SearchResponse response = searchRequestBuilder.get();
		return elasticQueryGenerator.translateElasticResponse(response);
	}

	public Map<String, Object> saveTestPaper(TestPaper testPaper) {
		Log.i(">>saveTestPaper() - testPaper: " + testPaper);
		String indexName = questionUtil.generateTestPaperIndexName(testPaper.getTarget());
		Map<String, Object> entityMap = questionUtil.getMapFromObject(testPaper);
		alterEntity(entityMap);

		String indexJson = questionUtil.getStringJSONFromObject(entityMap);
		IndexResponse indexResponse = index(indexName, TYPE_TEST_PAPER, String.valueOf(testPaper.getTestId()),
				indexJson);
		Log.d(">>ElasticSearhService.saveTestPaper() - indexResponse:" + indexResponse);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("testId", indexResponse.getId());
		responseMap.put("indexName", indexResponse.getIndex());
		responseMap.put("indexType", indexResponse.getType());
		responseMap.put("version", indexResponse.getVersion());

		return responseMap;
	}

	public Map<String, Object> saveUser(User user) {
		Log.i(">>saveUser() - user: " + user);
		Map<String, Object> entityMap = questionUtil.getMapFromObject(user);
		alterEntity(entityMap);

		String indexJson = questionUtil.getStringJSONFromObject(entityMap);
		IndexResponse indexResponse = index(INDEX_USER, TYPE_USER, user.getUserId(), indexJson);
		Log.d(">>ElasticSearhService.saveUser() - indexResponse:" + indexResponse);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("userId", indexResponse.getId());
		responseMap.put("indexName", indexResponse.getIndex());
		responseMap.put("indexType", indexResponse.getType());
		responseMap.put("version", indexResponse.getVersion());

		return responseMap;
	}
}
