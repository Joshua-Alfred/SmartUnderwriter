package com.amazonaws.uwriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FitnessDataCollector {
	public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");

	public static void main(String[] args) throws IOException {
		
		String accessToken = "Bearer ya29.a0ARrdaM-6uOlc-PRWq7BRkXjwPmK_zJT8hsFbWnkKK5FNWITRb9LbICdciSSaVKdQxPvPNjw3LrsjeHFSouwoFRUiaUpQ8WN_os0LVawC6h2gC4HWMzCeK80mdM0xAS2TUYyJaQRErkepE3QNTsoiebu2g9hu";
		
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
				
				System.out.printf("Average steps: %.4f", ((float)totalSteps/bucketItems));
				System.out.println();
				
				double totalErg = 0;
				for(double j : EnergyCal) {
					totalErg +=j;
				}
				
				System.out.printf("Average Energy Spent: %.4f", ((float)totalErg/bucketItems));
				System.out.println();
				
				double totalDist = 0;
				for(double j : Distance) {
					totalDist +=j;
				}
				
				System.out.printf("Average Distance travelled per day (meters): %.4f", ((float)totalDist/bucketItems));
				System.out.println();
				
				int totalHP = 0;
				for(int j : HeartPoints) {
					totalHP +=j;
				}
				
				System.out.printf("Average Heart Points per day: %.4f", ((float)totalHP/bucketItems));
				System.out.println();
				
				
		
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
				+ "  \"startTimeMillis\": 1648751400000,\r\n"  //April 1st 12:00AM
				+ "  \"bucketByTime\": {\r\n"
				+ "    \"durationMillis\": 86400000\r\n"
				+ "  }\r\n"
				+ "}";
		
		return json;
		
	}
}
