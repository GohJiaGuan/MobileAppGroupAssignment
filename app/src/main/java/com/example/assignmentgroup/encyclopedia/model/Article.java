package com.example.assignmentgroup.encyclopedia.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articles")
public class Article {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull public String title;
    @NonNull public String category;
    @NonNull public String summary;
    @NonNull public String content;
    public String readTime;

    public int viewCount;

    public Article() {
        this.title = ""; this.category = ""; this.summary = ""; this.content = "";
    }

    public Article(@NonNull String title, @NonNull String category,
                    @NonNull String summary, @NonNull String content, String readTime) {
        this.title = title; this.category = category; this.summary = summary;
        this.content = content; this.readTime = readTime;
    }
}
