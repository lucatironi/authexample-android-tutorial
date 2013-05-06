package com.savagelook.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class JsonHelper {
	public static JSONArray getJsonArrayFromResource(Context context, int resourceId) throws JSONException, IOException {
		InputStream is = context.getResources().openRawResource(resourceId);
		return new JSONArray(getStringFromInputStream(is));
	}
	
	public static JSONObject getJsonObjectFromResource(Context context, int resourceId) throws JSONException, IOException {
		InputStream is = context.getResources().openRawResource(resourceId);
		return new JSONObject(getStringFromInputStream(is));
	}
	
	public static JSONArray getJsonArrayFromUrl(String url, String authToken) throws MalformedURLException, JSONException, IOException 
	{
		return getJsonArrayFromUrl(url, 0, 0, authToken);
	}
	
	public static JSONArray getJsonArrayFromUrl(String url, int connectTimeout, int readTimeout, String authToken) throws MalformedURLException, JSONException, IOException {
		return new JSONArray(getStringFromUrl(url, connectTimeout, readTimeout, authToken));
	}
	
	public static JSONObject getJsonObjectFromUrl(String url, String authToken) throws MalformedURLException, JSONException, IOException 
	{
		return getJsonObjectFromUrl(url, 0, 0, authToken);
	}
	
	public static JSONObject getJsonObjectFromUrl(String url, int connectTimeout, int readTimeout, String authToken) throws MalformedURLException, JSONException, IOException 
	{
		return new JSONObject(getStringFromUrl(url, connectTimeout, readTimeout, authToken));
	}
	
	static private String getStringFromInputStream(InputStream is) throws IOException {	
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while((line = br.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}
	
	private static String getStringFromUrl(String url, int connectTimeout, int readTimeout, String authToken) throws MalformedURLException, JSONException, IOException {
		URL urlObject = new URL(url);
		HttpURLConnection urlConn = (HttpURLConnection)urlObject.openConnection();
		urlConn.addRequestProperty("Authorization", "Token token=" + authToken);
		String jsonString  = "";
		
		if (connectTimeout != 0) {
			urlConn.setConnectTimeout(connectTimeout);
		}
		if (readTimeout != 0) {
			urlConn.setReadTimeout(readTimeout);
		}
		
		try {
			jsonString = getStringFromInputStream(urlConn.getInputStream());
		} finally {
			urlConn.disconnect();
		}
		return jsonString;
	}
}
