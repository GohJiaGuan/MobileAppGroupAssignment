package com.example.assignmentgroup.encyclopedia.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import com.example.assignmentgroup.encyclopedia.model.ChatMessage;

@Dao
public interface ChatMessageDao {

    @Insert
    long insert(ChatMessage message);

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    List<ChatMessage> getAll();

    @Query("DELETE FROM chat_messages")
    void clearAll();
}
