package com.example.assignmentgroup.encyclopedia.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.assignmentgroup.encyclopedia.model.Article;
import com.example.assignmentgroup.encyclopedia.model.ChatMessage;

@Database(entities = {Article.class, ChatMessage.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ArticleDao articleDao();
    public abstract ChatMessageDao chatMessageDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "eco_encyclopedia.db")
                            .allowMainThreadQueries()

                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
