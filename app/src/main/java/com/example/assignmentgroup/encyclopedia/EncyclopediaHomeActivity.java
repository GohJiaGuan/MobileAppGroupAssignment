package com.example.assignmentgroup.encyclopedia;

import com.example.assignmentgroup.R;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import com.example.assignmentgroup.encyclopedia.adapter.ArticleAdapter;
import com.example.assignmentgroup.encyclopedia.adapter.CategoryAdapter;
import com.example.assignmentgroup.encyclopedia.db.AppDatabase;
import com.example.assignmentgroup.encyclopedia.model.Article;
import com.example.assignmentgroup.encyclopedia.network.ArticleContentGenerator;
import com.example.assignmentgroup.encyclopedia.util.ArticleSeeder;

public class EncyclopediaHomeActivity extends AppCompatActivity {

    private AppDatabase db;
    private ArticleAdapter articleAdapter;
    private EditText searchInput;
    private String currentCategory = "All";

    private static final List<String> CATEGORIES = Arrays.asList(
            "All", "Refuse", "Reduce", "Reuse", "Recycle", "Rot", "Plastic", "E-waste", "Metal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encyclopedia_home);

        db = AppDatabase.getInstance(this);
        seedDatabaseIfEmpty();

        searchInput = findViewById(R.id.editSearch);
        RecyclerView categoryRecycler = findViewById(R.id.recyclerCategories);
        RecyclerView articleRecycler = findViewById(R.id.recyclerArticles);
        ImageButton askAiButton = findViewById(R.id.buttonAskAi);
        TextView askAiCard = findViewById(R.id.cardAskAiSubtitle);
        ImageButton refreshButton = findViewById(R.id.buttonRefreshArticles);

        CategoryAdapter categoryAdapter = new CategoryAdapter(CATEGORIES, this::loadArticlesForCategory);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryRecycler.setAdapter(categoryAdapter);

        articleAdapter = new ArticleAdapter(this::openArticleDetail);
        articleRecycler.setLayoutManager(new LinearLayoutManager(this));
        articleRecycler.setAdapter(articleAdapter);

        loadArticlesForCategory("All");

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    loadArticlesForCategory(currentCategory);
                } else {
                    articleAdapter.submitList(db.articleDao().search(query));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        View.OnClickListener openChat = v -> startActivity(new Intent(EncyclopediaHomeActivity.this, ChatActivity.class));
        askAiButton.setOnClickListener(openChat);
        askAiCard.setOnClickListener(openChat);

        refreshButton.setOnClickListener(v -> refreshArticlesFromAi(refreshButton));
    }

    private void refreshArticlesFromAi(ImageButton refreshButton) {
        refreshButton.setEnabled(false);
        Toast.makeText(this, R.string.refresh_articles_toast, Toast.LENGTH_SHORT).show();

        ArticleContentGenerator.refreshFromWeb(db, updated -> {
            refreshButton.setEnabled(true);
            if (updated) {
                loadArticlesForCategory(currentCategory);
            } else {
                Toast.makeText(this, R.string.refresh_articles_failed_toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (searchInput == null || searchInput.getText().toString().trim().isEmpty()) {
            loadArticlesForCategory(currentCategory);
        }
    }

    private void seedDatabaseIfEmpty() {
        if (db.articleDao().count() == 0) {
            db.articleDao().insertAll(ArticleSeeder.seedArticles());
        }
    }

    private void loadArticlesForCategory(String category) {
        currentCategory = category;

        List<Article> articles = category.equals("All")
                ? db.articleDao().getAllByPopularity()
                : db.articleDao().getByCategoryPopularity(category);
        articleAdapter.submitList(articles);
    }

    private void openArticleDetail(Article article) {
        Intent intent = new Intent(this, ArticleDetailActivity.class);
        intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.id);
        startActivity(intent);
    }
}
