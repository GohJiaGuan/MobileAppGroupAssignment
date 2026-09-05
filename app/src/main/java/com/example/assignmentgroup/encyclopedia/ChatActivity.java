package com.example.assignmentgroup.encyclopedia;

import com.example.assignmentgroup.R;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.assignmentgroup.encyclopedia.adapter.ChatAdapter;
import com.example.assignmentgroup.encyclopedia.db.AppDatabase;
import com.example.assignmentgroup.encyclopedia.model.Article;
import com.example.assignmentgroup.encyclopedia.model.ChatMessage;
import com.example.assignmentgroup.encyclopedia.network.ChatRepository;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_PREFILL_MESSAGE = "prefill_message";

    private AppDatabase db;
    private ChatAdapter chatAdapter;
    private ChatRepository chatRepository;
    private RecyclerView recyclerChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = AppDatabase.getInstance(this);
        List<Article> knowledgeBase = db.articleDao().getAll();
        chatRepository = new ChatRepository(knowledgeBase);

        recyclerChat = findViewById(R.id.recyclerChat);
        EditText messageInput = findViewById(R.id.editChatMessage);
        ImageButton sendButton = findViewById(R.id.buttonSend);

        chatAdapter = new ChatAdapter();
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        List<ChatMessage> history = db.chatMessageDao().getAll();
        if (history.isEmpty()) {
            ChatMessage greeting = new ChatMessage(
                    "Hi! Ask me anything about reducing, reusing or recycling \u2013 "
                            + "for example \"Is a milk carton recyclable?\"", false);
            db.chatMessageDao().insert(greeting);
            history = db.chatMessageDao().getAll();
        }
        chatAdapter.submitAll(history);
        scrollToBottom();

        String prefill = getIntent().getStringExtra(EXTRA_PREFILL_MESSAGE);
        if (prefill != null) {
            messageInput.setText(prefill);
            messageInput.setSelection(prefill.length());
        }

        sendButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (text.isEmpty()) return;

            ChatMessage userMessage = new ChatMessage(text, true);
            db.chatMessageDao().insert(userMessage);
            chatAdapter.addMessage(userMessage);
            messageInput.setText("");
            scrollToBottom();

            chatRepository.getReply(text, reply -> {
                ChatMessage aiMessage = new ChatMessage(reply, false);
                db.chatMessageDao().insert(aiMessage);
                chatAdapter.addMessage(aiMessage);
                scrollToBottom();
            });
        });
    }

    private void scrollToBottom() {
        recyclerChat.post(() -> {
            int count = chatAdapter.getItemCount();
            if (count > 0) recyclerChat.smoothScrollToPosition(count - 1);
        });
    }
}
