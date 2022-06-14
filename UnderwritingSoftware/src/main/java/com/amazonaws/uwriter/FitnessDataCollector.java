package com.amazonaws.uwriter;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FitnessDataCollector {
	public static void main(String[] args) throws IOException {
		
		String accessToken = "Bearer ya29.a0ARrdaM_yAtmHNuHdRpdX7kNDCRo99oGdmp_pd-n8Es5PWtwFllm-hUg7CjG59Ej3BsGHTAnv3VCVwAekcKasb3fr1sU4VOvxh9UGSkEhg-ZDsP0ctZsR5f1foy6NKqVc5QdL6DLN5h25TiuvGp8WocKBGKfP";
		
		
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
		                     .url("https://www.googleapis.com/fitness/v1/users/me/dataSources")
		                     .header("Authorization", accessToken)
		                     .build();
		
		Response response = client.newCall(request).execute();
		String jsonData = response.body().string();
		System.out.println(jsonData);
		
	}
}
