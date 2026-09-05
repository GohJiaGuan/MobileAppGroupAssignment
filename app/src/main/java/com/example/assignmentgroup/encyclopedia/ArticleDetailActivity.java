package com.example.assignmentgroup.encyclopedia;

import com.example.assignmentgroup.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.assignmentgroup.encyclopedia.db.AppDatabase;
import com.example.assignmentgroup.encyclopedia.model.Article;

public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "extra_article_id";

    private AppDatabase db;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        Toolbar toolbar = findViewById(R.id.toolbarArticle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = AppDatabase.getInstance(this);
        int articleId = getIntent().getIntExtra(EXTRA_ARTICLE_ID, -1);
        db.articleDao().incrementViewCount(articleId);
        article = db.articleDao().getById(articleId);

        TextView title = findViewById(R.id.textDetailTitle);
        TextView meta = findViewById(R.id.textDetailMeta);
        TextView content = findViewById(R.id.textDetailContent);
        Button askFollowUp = findViewById(R.id.buttonAskFollowUp);

        if (article != null) {
            title.setText(article.title);
            meta.setText(article.readTime + "  \u2022  Category: " + article.category);
            content.setText(article.content);
        }

        askFollowUp.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
    }
}
