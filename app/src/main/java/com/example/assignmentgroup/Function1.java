package com.example.assignmentgroup;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Function1 extends AppCompatActivity  implements InventoryAdapter.Listener{
    private static final String PREFS_NAME = "inventory_prefs";
    private static final String KEY_ITEMS = "items_json";

    private final List<Item> items = new ArrayList<>();
    private InventoryAdapter adapter;
    private SharedPreferences prefs;

    private EditText nameInput, qtyInput, categoryInput;
    private TextView statText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function1);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        nameInput = findViewById(R.id.nameInput);
        qtyInput = findViewById(R.id.qtyInput);
        categoryInput = findViewById(R.id.categoryInput);
        statText = findViewById(R.id.statText);

        RecyclerView recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(items, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addButton).setOnClickListener(v -> onAddClicked());
        findViewById(R.id.button4).setOnClickListener(v -> clearAllItems());

        loadItems();
        updateStat();
    }
    private void addItem(String name, int qty, String category) {
        name = name.trim();
        category = category.trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please insert the product name.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (qty <= 0) qty = 1;

        for (Item existing : items) {
            if (existing.name.equalsIgnoreCase(name)) {
                existing.Quantity += qty;
                if (!category.isEmpty()) existing.Category = category;
                existing.date = System.currentTimeMillis();
                adapter.notifyDataSetChanged();
                saveItems();
                updateStat();
                return;
            }
        }

        items.add(new Item(name, qty, category, System.currentTimeMillis()));
        adapter.notifyItemInserted(items.size() - 1);
        saveItems();
        updateStat();
    }

    private void onAddClicked() {
        String name = nameInput.getText().toString();
        String qtyText = qtyInput.getText().toString().trim();
        String category = categoryInput.getText().toString();

        int qty;
        try {
            qty = qtyText.isEmpty() ? 1 : Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter the quantity of item.", Toast.LENGTH_SHORT).show();
            return;
        }

        addItem(name, qty, category);

        nameInput.setText("");
        qtyInput.setText("");
        categoryInput.setText("");
        nameInput.requestFocus();
    }

    @Override
    public void onIncrease(int position) {
        if (position < 0 || position >= items.size()) return;
        items.get(position).Quantity += 1;
        adapter.notifyItemChanged(position);
        saveItems();
        updateStat();
    }

    @Override
    public void onDecrease(int position) {
        if (position < 0 || position >= items.size()) return;
        Item item = items.get(position);
        item.Quantity -= 1;
        if (item.Quantity <= 0) {
            items.remove(position);
            adapter.notifyItemRemoved(position);
        } else {
            adapter.notifyItemChanged(position);
        }
        saveItems();
        updateStat();
    }

    @Override
    public void onDelete(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        adapter.notifyItemRemoved(position);
        saveItems();
        updateStat();
    }

    private void updateStat() {
        int types = items.size();
        int totalQty = 0;
        for (Item item : items) totalQty += item.Quantity;
        statText.setText(types + " types of item & Total: " + totalQty + " items");
    }

    private void saveItems() {
        JSONArray array = new JSONArray();
        try {
            for (Item item : items) {
                JSONObject obj = new JSONObject();
                obj.put("name", item.name);
                obj.put("qty", item.Quantity);
                obj.put("category", item.Category == null ? "" : item.Category);
                obj.put("timestamp", System.currentTimeMillis());
                array.put(obj);
            }
            prefs.edit().putString(KEY_ITEMS, array.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadItems() {
        String json = prefs.getString(KEY_ITEMS, null);
        if (json == null) return;
        try {
            JSONArray array = new JSONArray(json);
            items.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                items.add(new Item(
                        obj.getString("name"),
                        obj.getInt("qty"),
                        obj.optString("category", ""),
                        obj.optLong("timestamp", System.currentTimeMillis())
                ));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void clearAllItems(){
        new AlertDialog.Builder(this)
                .setTitle("Clear the inventory")
                .setMessage("Are you sure to delete all information")
                .setPositiveButton("Clear", (dialog, which) -> {
                    items.clear();
                    adapter.notifyDataSetChanged();
                    saveItems();
                    updateStat();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
