package com.amazonaws.uwriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("deprecation")
public class FitnessDataCollector {
	public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
	public static final String CLIENT_ID= "597732289824-9rtvo7abi4j7uv68aeukket4s1f080kf.apps.googleusercontent.com";
	public static final String CLIENT_SECRET= "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";
	
	

	

	public static int fitcheck(String token) throws IOException {
		
		int fitnessScore = 0;
		
//		HttpTransport HTTP_TRANSPORT = new HttpTransport();
//		
//		GoogleCredential credential = createCredentialWithRefreshToken(
//		        HTTP_TRANSPORT, JSON_FACTORY, new TokenResponse().setRefreshToken(refreshToken));
//
//		credential.refreshToken(); 
//
//		String newAccessToken = credential.getAccessToken();

		
		
		
		String accessToken = "Bearer " + token;
		String userHeartPoints = "derived:com.google.heart_minutes:com.google.android.gms:merge_heart_minutes";
		String userEnergySpent = "derived:com.google.calories.expended:com.google.android.gms:platform_calories_expended";
		String userStepCount = "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps";
		String userDistance = "derived:com.google.distance.delta:com.google.android.gms:merge_distance_delta";
		
		List<Integer> steps = new ArrayList<>();
		List<Double> EnergyCal = new ArrayList<>();
		List<Integer> HeartPoints = new ArrayList<>();
		List<Double> Distance = new ArrayList<>();
		
		
		int bucketItems = 1;
		
		OkHttpClient client = new OkHttpClient();

		
		//extracting no. of steps of user
		
		RequestBody body = RequestBody.create(JSON, makeJson(userStepCount));
		Request request = new Request.Builder()
		                     .url("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate")
		                     .header("Authorization", accessToken)
		                     .post(body)
		                     .build();
		
		Response response = client.newCall(request).execute();
		String jsonData = response.body().string();
		//System.out.println(jsonData);
		
		
		try {
			JSONObject Jobject = new JSONObject(jsonData);
			JSONArray bucket = Jobject.getJSONArray("bucket");
			
			bucketItems = bucket.length();
			
			for(int i=0;i<bucket.length();i++) {
				JSONArray jsonPoints = (JSONArray) bucket.getJSONObject(i).getJSONArray("dataset").getJSONObject(0)
						.getJSONArray("point");
				
				if(jsonPoints!=null && jsonPoints.length()>0) {
					
				
				JSONObject jsonObject = jsonPoints.getJSONObject(0).getJSONArray("value").getJSONObject(0);
				//System.out.println(jsonObject.getInt("intVal"));
				steps.add(jsonObject.getInt("intVal"));
				
				}
			}
			
		
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		
		
		// extracting calories spent by user
		
		RequestBody body2 = RequestBody.create(JSON, makeJson(userEnergySpent));
		Request request2 = new Request.Builder()
		                     .url("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate")
		                     .header("Authorization", accessToken)
		                     .post(body2)
		                     .build();
		
		Response response2 = client.newCall(request2).execute();
		String jsonData2 = response2.body().string();
		//System.out.println(jsonData2);
		
		try {
			JSONObject Jobject = new JSONObject(jsonData2);
			JSONArray bucket = Jobject.getJSONArray("bucket");
			
			for(int i=0;i<bucket.length();i++) {
				JSONArray jsonPoints = (JSONArray) bucket.getJSONObject(i).getJSONArray("dataset").getJSONObject(0)
						.getJSONArray("point");
				
				if(jsonPoints!=null && jsonPoints.length()>0) {
					
				
				JSONObject jsonObject = jsonPoints.getJSONObject(0).getJSONArray("value").getJSONObject(0);
				
				EnergyCal.add(jsonObject.getDouble("fpVal"));
			}
			}
			
			
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		
		// extracting total distance ran/cycled by user
		
		RequestBody body3 = RequestBody.create(JSON, makeJson(userDistance));
		Request request3 = new Request.Builder()
		                     .url("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate")
		                     .header("Authorization", accessToken)
		                     .post(body3)
		                     .build();
		
		Response response3 = client.newCall(request3).execute();
		String jsonData3 = response3.body().string();
		//System.out.println(jsonData3);
		
		try {
			JSONObject Jobject = new JSONObject(jsonData3);
			JSONArray bucket = Jobject.getJSONArray("bucket");
			
			for(int i=0;i<bucket.length();i++) {
				JSONArray jsonPoints = (JSONArray) bucket.getJSONObject(i).getJSONArray("dataset").getJSONObject(0)
						.getJSONArray("point");
				
				if(jsonPoints!=null && jsonPoints.length()>0) {
					
				
				JSONObject jsonObject = jsonPoints.getJSONObject(0).getJSONArray("value").getJSONObject(0);
				
				Distance.add(jsonObject.getDouble("fpVal"));
				
				}
			}
			
			
			
		} catch (JSONException e) {
			
			System.out.println(e);
		}
		
		
		// extracting heart points by user
		
				RequestBody body4 = RequestBody.create(JSON, makeJson(userHeartPoints));
				Request request4 = new Request.Builder()
				                     .url("https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate")
				                     .header("Authorization", accessToken)
				                     .post(body4)
				                     .build();
				
				Response response4 = client.newCall(request4).execute();
				String jsonData4 = response4.body().string();
				//System.out.println(jsonData4);
				
				
				try {
					JSONObject Jobject = new JSONObject(jsonData4);
					JSONArray bucket = Jobject.getJSONArray("bucket");
					
					for(int i=0;i<bucket.length();i++) {
						JSONArray jsonPoints = (JSONArray) bucket.getJSONObject(i).getJSONArray("dataset").getJSONObject(0)
								.getJSONArray("point");
						
						if(jsonPoints!=null && jsonPoints.length()>0) {
							
						
						JSONObject jsonObject = jsonPoints.getJSONObject(0).getJSONArray("value").getJSONObject(1);
						//System.out.println(jsonObject.getInt("intVal"));
						HeartPoints.add(jsonObject.getInt("intVal"));
						
						}
					}
					
					
					
				} catch (JSONException e) {
					
					System.out.println(e);
				}
				
				int totalSteps = 0;
				for(int j : steps) {
					totalSteps +=j;
				}
				
				float avgSteps = (float)totalSteps/bucketItems;
				
				System.out.printf("Average steps: %.4f", avgSteps);
				System.out.println();
				
				double totalErg = 0;
				for(double j : EnergyCal) {
					totalErg +=j;
				}
				
				float avgErg = (float)totalErg/bucketItems;
				
				System.out.printf("Average Energy Spent: %.4f", avgErg);
				System.out.println();
				
				double totalDist = 0;
				for(double j : Distance) {
					totalDist +=j;
				}
				
				float avgDist = (float)totalDist/bucketItems;
				
				System.out.printf("Average Distance travelled per day (meters): %.4f", avgDist);
				System.out.println();
				
				int totalHP = 0;
				for(int j : HeartPoints) {
					totalHP +=j;
				}
				
				float avgHP = (float)totalHP/bucketItems;
				
				System.out.printf("Average Heart Points per day: %.4f", avgHP);
				System.out.println();
				
				
				if(avgSteps>=4000)
					fitnessScore+=10;
				if(avgErg>=1000)
					fitnessScore+=10;
				if(avgDist>=3000)
					fitnessScore+=10;
				if(avgHP>=18)
					fitnessScore+=10;
				
				return fitnessScore;
				
				
				
				
		
	}
	
	public static String makeJson(String prompt) {
		
		Date date = new Date();
		long millisNow = date.getTime();
		
		String json = "{\r\n"
				+ "  \"aggregateBy\": [\r\n"
				+ "    {\r\n"
				+ "      \"dataSourceId\": \""+prompt +"\"\r\n"
				+ "    }\r\n"
				+ "  ],\r\n"
				+ "  \"endTimeMillis\": "+millisNow+",\r\n"
				+ "  \"startTimeMillis\": 1654799400000,\r\n"  //April 1st 12:00AM ?
				+ "  \"bucketByTime\": {\r\n"
				+ "    \"durationMillis\": 86400000\r\n"
				+ "  }\r\n"
				+ "}";
		
		return json;
		
	}
	
	public static GoogleCredential createCredentialWithRefreshToken(HttpTransport transport, 
	        JsonFactory jsonFactory, TokenResponse tokenResponse) {
	    return new GoogleCredential.Builder().setTransport(transport)
	        .setJsonFactory(jsonFactory)
	        .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
	        .build()
	        .setFromTokenResponse(tokenResponse);
	}
}
