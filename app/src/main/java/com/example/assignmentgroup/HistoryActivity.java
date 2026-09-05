package com.example.assignmentgroup;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "inventory_prefs";
    private static final String KEY_ITEMS = "items_json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        EdgeToEdgeUtil.apply(this);

        List<Item> items = loadItems();

        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        TextView emptyText = findViewById(R.id.tvHistoryEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new HistoryAdapter(items));

        if (items.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        BottomNavHelper.setup(this, BottomNavHelper.Tab.HISTORY);
    }

    private List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_ITEMS, null);
        if (json == null) return items;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                items.add(new Item(
                        obj.getString("name"),
                        obj.getInt("qty"),
                        obj.optString("category", ""),
                        obj.optLong("timestamp", System.currentTimeMillis())
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }
}
