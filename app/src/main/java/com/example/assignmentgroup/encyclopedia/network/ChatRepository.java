package com.example.assignmentgroup.encyclopedia.network;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.assignmentgroup.encyclopedia.model.Article;
import com.example.assignmentgroup.encyclopedia.model.GeminiRequest;
import com.example.assignmentgroup.encyclopedia.model.GeminiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {

    private static final String TAG = "ChatRepository";

    public interface ReplyCallback {
        void onReply(String reply);
    }

    private final List<Article> knowledgeBase;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ChatRepository(List<Article> knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public void getReply(String question, ReplyCallback callback) {
        if (!RetrofitClient.hasApiKey()) {
            Log.w(TAG, "No GEMINI_API_KEY configured (local.properties) - using offline knowledge-base fallback.");
            String local = tryLocalKnowledgeBase(question);
            callback.onReply(local != null
                    ? local
                    : "(Ask AI is running in offline mode - add a GEMINI_API_KEY in local.properties "
                      + "for full AI answers.) " + fallbackMessage());
            return;
        }
        callGemini(question, callback);
    }

    private void callGemini(String question, ReplyCallback callback) {
        String prompt = buildPrompt(question);
        GeminiApiService service = RetrofitClient.getGeminiApiService();

        service.generateContent(RetrofitClient.getApiKey(), new GeminiRequest(prompt))
                .enqueue(new Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String answer = response.body().firstAnswerText();
                            if (answer != null && !answer.trim().isEmpty()) {
                                postReply(callback, answer.trim());
                                return;
                            }
                            Log.w(TAG, "Gemini HTTP " + response.code() + " but no candidate text (likely blocked by safety filters).");
                        } else {
                            String errorBody = null;
                            try {
                                if (response.errorBody() != null) errorBody = response.errorBody().string();
                            } catch (IOException ignored) {  }
                            Log.e(TAG, "Gemini HTTP " + response.code() + ": " + errorBody);
                        }
                        String local = tryLocalKnowledgeBase(question);
                        postReply(callback, local != null ? local : fallbackMessage());
                    }

                    @Override
                    public void onFailure(Call<GeminiResponse> call, Throwable t) {
                        Log.e(TAG, "Gemini call failed (network/IO): " + t, t);
                        String local = tryLocalKnowledgeBase(question);
                        postReply(callback, local != null ? local : fallbackMessage());
                    }
                });
    }

    private String buildPrompt(String question) {
        return "You are the Ask AI Assistant inside a sustainability/recycling app called " +
                "Eco-Encyclopedia. Answer the user's question directly and naturally, in 2-5 " +
                "sentences suitable for a mobile chat bubble. You are not limited to any fixed " +
                "article - answer from your own knowledge like a normal AI assistant would.\n\n" +
                "User question: " + question;
    }

    private String tryLocalKnowledgeBase(String question) {
        String q = question.toLowerCase(Locale.getDefault());
        Article best = null;
        int bestScore = 0;
        for (Article a : knowledgeBase) {
            int score = 0;
            String haystack = (a.title + " " + a.summary + " " + a.content).toLowerCase(Locale.getDefault());
            for (String word : q.split("\\s+")) {
                if (word.length() > 3 && haystack.contains(word)) score++;
            }
            if (score > bestScore) {
                bestScore = score;
                best = a;
            }
        }
        if (best == null || bestScore < 1) return null;
        return best.summary + "\n\n" + best.content
                + "\n\n(Offline mode - matched from the Eco-Encyclopedia article \"" + best.title + "\")";
    }

    private void postReply(ReplyCallback callback, String reply) {
        mainHandler.post(() -> callback.onReply(reply));
    }

    private String fallbackMessage() {
        return "I couldn't reach the AI service and didn't find a close match in the local " +
                "Eco-Encyclopedia either. Check your internet connection and API key, or try " +
                "rephrasing your question.";
    }
}
