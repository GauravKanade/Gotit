package com.felisys.gotit.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by gauravkanade on 1/25/17.
 */

public class ObjectMapperUtil {

    static String SEEDDATA_LOCATION = "";

    public static <T> T createObjectFromString(String objectString, Class<T> classType) {
        ObjectMapper objectMapper = getConfiguredObjectMapper();
        try {
            return objectMapper.readValue(objectString, classType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getStringJSONFromObject(Object object) {
        ObjectMapper objectMapper = getConfiguredObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMapFromObject(Object object) {
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
    public static String readFileAsString(String fileName) {
        try {
            ClassLoader classLoader = ObjectMapperUtil.class.getClassLoader();
            File file = new File(classLoader.getResource(SEEDDATA_LOCATION + fileName).getFile());
            return new Scanner(file).useDelimiter("\\Z").next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ObjectMapper getConfiguredObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
}
