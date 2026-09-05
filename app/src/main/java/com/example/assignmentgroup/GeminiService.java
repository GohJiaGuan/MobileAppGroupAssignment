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
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.7-flash:generateContent?key=";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String PROMPT =
            "You are a waste-sorting assistant. Look closely at this photo of a household waste item " +
            "- read any visible text, labels, or recycling symbols on it. Reply in EXACTLY this format, " +
            "two lines, nothing else:\n" +
            "CATEGORY: <one word from Plastic, Paper, Glass, Metal, E-waste, Organic, Textile, Other>\n" +
            "RECOMMENDATION: <2-3 sentences of specific 5R disposal advice for THIS exact item, " +
            "referencing what you actually see in the photo - e.g. the specific product, packaging type, " +
            "or any recycling code/label visible - rather than generic advice for the category as a whole>";

    private static final OkHttpClient client = new OkHttpClient();

    public interface MaterialCallback {
        void onResult(String material, String recommendation);
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
                    String text = extractResponseText(bodyString);
                    if (text == null) {
                        callback.onError("Could not parse AI response.");
                        return;
                    }
                    String category = extractField(text, "CATEGORY");
                    String recommendation = extractField(text, "RECOMMENDATION");
                    if (category == null) {
                        callback.onError("Could not parse AI response.");
                        return;
                    }
                    callback.onResult(category, recommendation);
                } catch (JSONException e) {
                    callback.onError("Failed to parse response: " + e.getMessage());
                }
            }
        });
    }

    private static String extractResponseText(String responseBody) throws JSONException {
        JSONObject root = new JSONObject(responseBody);
        JSONArray candidates = root.optJSONArray("candidates");
        if (candidates == null || candidates.length() == 0) return null;

        JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
        if (content == null) return null;

        JSONArray parts = content.optJSONArray("parts");
        if (parts == null || parts.length() == 0) return null;

        String text = parts.getJSONObject(0).optString("text", "").trim();
        return text.isEmpty() ? null : text;
    }

    private static String extractField(String text, String label) {
        for (String line : text.split("\\r?\\n")) {
            line = line.trim();
            String prefix = label + ":";
            if (line.regionMatches(true, 0, prefix, 0, prefix.length())) {
                String value = line.substring(prefix.length()).trim();
                return value.isEmpty() ? null : value;
            }
        }
        return null;
    }
}
