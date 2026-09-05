package com.example.assignmentgroup.encyclopedia.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import com.example.assignmentgroup.encyclopedia.model.Article;
import com.example.assignmentgroup.encyclopedia.model.GeminiRequest;
import com.example.assignmentgroup.encyclopedia.model.GeminiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleContentGenerator {

    private static final String TAG = "ArticleContentGenerator";

    private static final String[] CATEGORIES = {
            "Refuse", "Reduce", "Reuse", "Recycle", "Rot", "Plastic", "E-waste", "Metal"
    };

    public interface RefreshCallback {

        void onDone(boolean updated);
    }

    public static void refreshFromWeb(com.example.assignmentgroup.encyclopedia.db.AppDatabase db, RefreshCallback callback) {
        if (!RetrofitClient.hasApiKey()) {
            callback.onDone(false);
            return;
        }

        GeminiApiService service = RetrofitClient.getGeminiApiService();
        service.generateContent(RetrofitClient.getApiKey(), new GeminiRequest(buildPrompt()))
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        String text = (response.isSuccessful() && response.body() != null)
                                ? response.body().firstAnswerText() : null;
                        List<Article> parsed = (text != null) ? parseArticles(text) : null;

                        if (parsed != null && !parsed.isEmpty()) {
                            db.articleDao().replaceAll(parsed);
                            callback.onDone(true);
                        } else {
                            Log.w(TAG, "Gemini returned no usable article JSON (HTTP " + response.code() + ").");
                            callback.onDone(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        Log.w(TAG, "Article refresh failed: " + t.getMessage());
                        callback.onDone(false);
                    }
                });
    }

    private static String buildPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are populating a waste-sorting and sustainability encyclopedia app. ")
          .append("Using your knowledge of current, accurate best practice, write ONE short article ")
          .append("for EACH of these categories: ");
        for (int i = 0; i < CATEGORIES.length; i++) {
            sb.append(CATEGORIES[i]);
            if (i < CATEGORIES.length - 1) sb.append(", ");
        }
        sb.append(". Respond with ONLY a raw JSON array - no markdown code fences, no commentary before ")
          .append("or after it. Each array item must have exactly these keys: ")
          .append("\"title\" (string), \"category\" (must exactly match one of the categories above), ")
          .append("\"summary\" (one short sentence), \"content\" (2-4 plain-text sentences, no markdown), ")
          .append("\"readTime\" (e.g. \"2 min read\").");
        return sb.toString();
    }

    private static List<Article> parseArticles(String text) {
        try {
            String json = extractJsonArray(text);
            ArticleDraft[] drafts = new Gson().fromJson(json, ArticleDraft[].class);
            if (drafts == null) return null;

            List<Article> articles = new ArrayList<>();
            for (ArticleDraft d : drafts) {
                if (d.title == null || d.category == null || d.content == null) continue;
                articles.add(new Article(d.title, d.category,
                        d.summary != null ? d.summary : "", d.content,
                        d.readTime != null ? d.readTime : "2 min read"));
            }
            return articles;
        } catch (JsonSyntaxException e) {
            Log.w(TAG, "Could not parse AI-generated articles as JSON: " + e.getMessage());
            return null;
        }
    }

    private static String extractJsonArray(String text) {
        String t = text.trim();
        int start = t.indexOf('[');
        int end = t.lastIndexOf(']');
        return (start >= 0 && end > start) ? t.substring(start, end + 1) : t;
    }

    private static class ArticleDraft {
        String title;
        String category;
        String summary;
        String content;
        String readTime;
    }
}
