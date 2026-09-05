package com.example.assignmentgroup.encyclopedia.model;

import java.util.List;

public class GeminiResponse {

    public List<Candidate> candidates;

    public static class Candidate {
        public GeminiRequest.Content content;
    }

    public String firstAnswerText() {
        if (candidates == null || candidates.isEmpty()) return null;
        GeminiRequest.Content content = candidates.get(0).content;
        if (content == null || content.parts == null || content.parts.isEmpty()) return null;
        return content.parts.get(0).text;
    }
}
