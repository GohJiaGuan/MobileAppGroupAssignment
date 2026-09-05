package com.example.assignmentgroup;

import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PROMPT =
            "You are a waste-sorting assistant. Look at this photo of a household waste item " +
            "and reply with ONLY the material category, 1-3 words, chosen from: " +
            "Plastic, Paper, Glass, Metal, E-waste, Organic, Textile, Other. " +
            "Do not add punctuation or explanation.";

    private static final OkHttpClient client = new OkHttpClient();

    public interface MaterialCallback {
        void onResult(String material);
        void onError(String message);
    }

    public static void identifyMaterial(String apiKey, byte[] jpegBytes, MaterialCallback callback) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            callback.onError("Missing GEMINI_API_KEY. Add it to local.properties.");
            return;
        }

        String base64Image = Base64.encodeToString(jpegBytes, Base64.NO_WRAP);

        JSONObject requestBody;
        try {
            JSONObject textPart = new JSONObject().put("text", PROMPT);
            JSONObject inlineData = new JSONObject()
                    .put("mime_type", "image/jpeg")
                    .put("data", base64Image);
            JSONObject imagePart = new JSONObject().put("inline_data", inlineData);

            JSONArray parts = new JSONArray().put(textPart).put(imagePart);
            JSONObject content = new JSONObject().put("parts", parts);
            JSONArray contents = new JSONArray().put(content);

            requestBody = new JSONObject().put("contents", contents);
        } catch (JSONException e) {
            callback.onError("Failed to build request: " + e.getMessage());
            return;
        }

        Request request = new Request.Builder()
                .url(ENDPOINT + apiKey)
                .post(RequestBody.create(requestBody.toString(), JSON))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (Response resp = response) {
                    String bodyString = resp.body() != null ? resp.body().string() : "";
                    if (!resp.isSuccessful()) {
                        callback.onError("API error " + resp.code() + ": " + bodyString);
                        return;
                    }
                    String material = parseMaterial(bodyString);
                    if (material == null) {
                        callback.onError("Could not parse AI response.");
                    } else {
                        callback.onResult(material);
                    }
                } catch (JSONException e) {
                    callback.onError("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }

    private static String parseMaterial(String responseBody) throws JSONException {
        JSONObject root = new JSONObject(responseBody);
        JSONArray candidates = root.optJSONArray("candidates");
        if (candidates == null || candidates.length() == 0) return null;

        JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
        if (content == null) return null;

        JSONArray parts = content.optJSONArray("parts");
        if (parts == null || parts.length() == 0) return null;

        String text = parts.getJSONObject(0).optString("text", "").trim();
        if (text.isEmpty()) return null;

        String firstLine = text.split("\\r?\\n")[0].trim();
        return firstLine.replaceAll("[.\"']", "");
    }
}
