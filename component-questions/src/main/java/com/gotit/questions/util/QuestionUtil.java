package com.gotit.questions.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.gotit.entity.Question;
import com.gotit.questions.elasticservice.ElasticSearchConstants;

@Component
public class QuestionUtil implements ElasticSearchConstants {
	public final String SEEDDATA_LOCATION = "elasticJSONS/";

	public <T> T createObjectFromString(String objectString, Class<T> classType) {
		return new Gson().fromJson(objectString, classType);
		/*
		 * ObjectMapper objectMapper = getConfiguredObjectMapper(); try { return
		 * objectMapper.readValue(objectString, classType); } catch (IOException
		 * e) { e.printStackTrace(); } return null;
		 */
	}

	public String getStringJSONFromObject(Object object) {
		return new Gson().toJson(object);
		/*
		 * ObjectMapper objectMapper = getConfiguredObjectMapper(); try { return
		 * objectMapper.writeValueAsString(object); } catch
		 * (JsonProcessingException e) { e.printStackTrace(); } return null;
		 */
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapFromObject(Object object) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(object), Map.class);
		/*
		 * ObjectMapper objectMapper = getConfiguredObjectMapper(); try {
		 * Map<String, Object> map = objectMapper.convertValue(object,
		 * Map.class); return map; } catch (Exception e) { e.printStackTrace();
		 * } return null;
		 */
	}

	@SuppressWarnings("resource")
	public String readFileAsString(String fileName) {
		try {
			ClassLoader classLoader = QuestionUtil.class.getClassLoader();
			File file = new File(classLoader.getResource(SEEDDATA_LOCATION + fileName).getFile());
			return new Scanner(file).useDelimiter("\\Z").next();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> T createObjectFromJSONFile(String fileLocation, Class<T> classType,
			boolean doSerializeDatesAsTimestamps) {
		ObjectMapper objectMapper = getConfiguredObjectMapper();
		if (doSerializeDatesAsTimestamps) {
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		}
		Resource resource = new ClassPathResource(fileLocation);

		try {
			return objectMapper.readValue(resource.getInputStream(), classType);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ObjectMapper getConfiguredObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	public String generateQuestionIndexName(Question questionEntity) {
		return generateQuestionIndexName(questionEntity.getTarget());
	}

	public String generateQuestionIndexName(String target) {
		String indexName = INDEX_QUESTION.replace(TARGET, target).replaceAll(" ", "").toLowerCase();
		return indexName;
	}

	public String generateTestPaperIndexName(String target) {
		return INDEX_TEST_PAPER.replace(TARGET, target).replaceAll(" ", "").toLowerCase();
	}

	public <T> T createObjectFromMap(Map<String, Object> responseMap, Class<T> classType) {
		Gson gson = new Gson();
		JsonElement jsonElement = gson.toJsonTree(responseMap);
		return gson.fromJson(jsonElement, classType);
	}

	public Map<String, Object> merge(Map<String, Object> persistedMap, Map<String, Object> requestMap) {
		for (String key : requestMap.keySet()) {
			persistedMap.replace(key, requestMap.get(key));
		}
		return persistedMap;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] convertToArray(List<T> list, Class<T> classType) {
		Object array[] = new Object[list.size()];
		list.toArray(array);
		return (T[]) array;
	}
}
