package com.example.assignmentgroup.encyclopedia.model;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {

    public List<Content> contents;

    public GeminiRequest(String promptText) {
        Part part = new Part();
        part.text = promptText;
        Content content = new Content();
        content.parts = Collections.singletonList(part);
        this.contents = Collections.singletonList(content);
    }

    public static class Content {
        public List<Part> parts;
    }

    public static class Part {
        public String text;
    }
}
