package com.example.assignmentgroup.encyclopedia.network;

import com.example.assignmentgroup.encyclopedia.model.GeminiRequest;
import com.example.assignmentgroup.encyclopedia.model.GeminiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GeminiApiService {

    @POST("models/gemini-3.1-flash-lite:generateContent")
    Call<GeminiResponse> generateContent(@Header("x-goog-api-key") String apiKey, @Body GeminiRequest request);
}
