package com.example.assignmentgroup.encyclopedia.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import com.example.assignmentgroup.encyclopedia.model.Article;

@Dao
public interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Article> articles);

    @Update
    void update(Article article);

    @Query("SELECT * FROM articles ORDER BY id ASC")
    List<Article> getAll();

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY id ASC")
    List<Article> getByCategory(String category);

    @Query("SELECT * FROM articles ORDER BY viewCount DESC, id ASC")
    List<Article> getAllByPopularity();

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY viewCount DESC, id ASC")
    List<Article> getByCategoryPopularity(String category);

    @Query("UPDATE articles SET viewCount = viewCount + 1 WHERE id = :articleId")
    void incrementViewCount(int articleId);

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' " +
           "OR summary LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    List<Article> search(String query);

    @Query("SELECT COUNT(*) FROM articles")
    int count();

    @Query("SELECT * FROM articles WHERE id = :articleId LIMIT 1")
    Article getById(int articleId);

    @Query("DELETE FROM articles")
    void deleteAll();

    @Transaction
    default void replaceAll(List<Article> articles) {
        deleteAll();
        insertAll(articles);
    }
}
