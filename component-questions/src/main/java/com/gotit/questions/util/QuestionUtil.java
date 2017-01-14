package com.gotit.questions.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gotit.entity.Question;
import com.gotit.questions.elasticservice.ElasticSearchConstants;

@Component
public class QuestionUtil implements ElasticSearchConstants {
	public final String SEEDDATA_LOCATION = "elasticJSONS/";

	public <T> T createObjectFromString(String objectString, Class<T> classType) {
		ObjectMapper objectMapper = getConfiguredObjectMapper();
		try {
			return objectMapper.readValue(objectString, classType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getStringJSONFromObject(Object object) {
		ObjectMapper objectMapper = getConfiguredObjectMapper();
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getMapFromObject(Object object) {
		ObjectMapper objectMapper = getConfiguredObjectMapper();
		try {
			Map<String, Object> map = objectMapper.convertValue(object, Map.class);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}

	public String generateQuestionIndexName(Question questionEntity) {
		return generateQuestionIndexName(questionEntity.getTargetId());
	}

	public String generateQuestionIndexName(String target) {
		String indexName = INDEX_QUESTION.replace(TARGET, target).replaceAll(" ", "").toLowerCase();
		return indexName;
	}
	
	public String generateTestPaperIndexName(String target){
		return INDEX_TEST_PAPER.replace(TARGET, target).replaceAll(" ", "").toLowerCase();
	}

	public <T> T createObjectFromMap(Map<String, Object> responseMap, Class<T> classType) {
		String stringVersion = getStringJSONFromObject(responseMap);
		return createObjectFromString(stringVersion, classType);
	}
}
