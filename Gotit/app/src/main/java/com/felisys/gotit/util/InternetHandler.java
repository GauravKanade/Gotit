package com.felisys.gotit.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by gauravkanade on 1/25/17.
 */

public class InternetHandler {

    public static <T> T post(String url, Object objectToPost, Class<T> returnType) {
        Log.i(Utility.LOG_TAG, ">>post() - Request URL: " + url + ", body:" + objectToPost);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<T> entity = new HttpEntity(objectToPost, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, returnType);
        Log.d(Utility.LOG_TAG, "<<response from URL: " + responseEntity);
        return responseEntity.getBody();
    }

    public static <T> T get(String url, Class<T> returnType) {
        Log.i(Utility.LOG_TAG, ">>get() - Request URL: " + url);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, returnType);
    }
}
