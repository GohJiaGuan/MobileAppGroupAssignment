package com.example.assignmentgroup.encyclopedia.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessage {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String text;
    public boolean fromUser;
    public long timestamp;

    public ChatMessage() {}

    public ChatMessage(String text, boolean fromUser) {
        this.text = text;
        this.fromUser = fromUser;
        this.timestamp = System.currentTimeMillis();
    }
}
