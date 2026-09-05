package com.example.assignmentgroup;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private ImageView photoPreview;
    private MaterialButton takePhotoButton, uploadPhotoButton;
    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private String lastAiCategory;
    private String lastAiRecommendation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function1);
        EdgeToEdgeUtil.apply(this);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        nameInput = findViewById(R.id.nameInput);
        qtyInput = findViewById(R.id.qtyInput);
        categoryInput = findViewById(R.id.categoryInput);
        statText = findViewById(R.id.statText);
        photoPreview = findViewById(R.id.photoPreview);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton);

        RecyclerView recyclerView = findViewById(R.id.itemsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(items, this);
        recyclerView.setAdapter(adapter);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) onPhotoReady(bitmap);
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Bitmap bitmap = loadBitmapFromUri(uri);
                        if (bitmap != null) onPhotoReady(bitmap);
                        else Toast.makeText(this, "Could not read that image.", Toast.LENGTH_SHORT).show();
                    }
                });

        findViewById(R.id.addButton).setOnClickListener(v -> onAddClicked());
        findViewById(R.id.button4).setOnClickListener(v -> clearAllItems());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        takePhotoButton.setOnClickListener(v -> cameraLauncher.launch(null));
        uploadPhotoButton.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        loadItems();
        updateStat();
    }

    private Bitmap loadBitmapFromUri(Uri uri) {
        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private void onPhotoReady(Bitmap bitmap) {
        photoPreview.setVisibility(android.view.View.VISIBLE);
        photoPreview.setImageBitmap(bitmap);
        identifyMaterial(bitmap);
    }

    private void setScanButtonsEnabled(boolean enabled) {
        takePhotoButton.setEnabled(enabled);
        uploadPhotoButton.setEnabled(enabled);
    }

    private void identifyMaterial(Bitmap bitmap) {
        setScanButtonsEnabled(false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

        GeminiService.identifyMaterial(BuildConfig.GEMINI_API_KEY, stream.toByteArray(), new GeminiService.MaterialCallback() {
            @Override
            public void onResult(String material, String recommendation) {
                runOnUiThread(() -> {
                    categoryInput.setText(material);
                    lastAiCategory = material;
                    lastAiRecommendation = recommendation;
                    setScanButtonsEnabled(true);
                    Toast.makeText(Function1.this, "Detected: " + material, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    setScanButtonsEnabled(true);
                    Toast.makeText(Function1.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    private boolean addItem(String name, int qty, String category) {
        name = name.trim();
        category = category.trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please insert the product name.", Toast.LENGTH_SHORT).show();
            return false;
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
                return true;
            }
        }

        items.add(new Item(name, qty, category, System.currentTimeMillis()));
        adapter.notifyItemInserted(items.size() - 1);
        saveItems();
        updateStat();
        return true;
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

        if (!addItem(name, qty, category)) return;
        showRecyclingOutcome(name.trim(), category);

        nameInput.setText("");
        qtyInput.setText("");
        categoryInput.setText("");
        nameInput.requestFocus();
    }

    private void showRecyclingOutcome(String name, String category) {
        boolean fromAiScan = lastAiRecommendation != null && category.equalsIgnoreCase(lastAiCategory);
        String tips = fromAiScan ? lastAiRecommendation : RecyclingAdvice.tipsFor(category);

        StringBuilder message = new StringBuilder(tips).append("\n\nNearby centres:\n");
        for (String centre : RecyclingAdvice.mockCentresFor(category)) {
            message.append("- ").append(centre).append("\n");
        }

        String chatPrefill = "I just logged \"" + name + "\" (category: " + category
                + ") in Recycle with AI. " + tips + " Can you tell me more about disposing of it properly?";

        new AlertDialog.Builder(this)
                .setTitle("Disposal recommendation")
                .setMessage(message.toString().trim())
                .setNeutralButton("Ask AI more", (dialog, which) -> openChatWithPrefill(chatPrefill))
                .setPositiveButton("Got it", null)
                .show();

        lastAiCategory = null;
        lastAiRecommendation = null;
    }

    private void openChatWithPrefill(String prefill) {
        Intent intent = new Intent(this, com.example.assignmentgroup.encyclopedia.ChatActivity.class);
        intent.putExtra(com.example.assignmentgroup.encyclopedia.ChatActivity.EXTRA_PREFILL_MESSAGE, prefill);
        startActivity(intent);
    }

    @Override
    public void onAskAi(int position) {
        if (position < 0 || position >= items.size()) return;
        Item item = items.get(position);
        String category = item.Category == null || item.Category.trim().isEmpty() ? "Other" : item.Category;
        String prefill = "I have \"" + item.name + "\" (category: " + category
                + ") logged in Recycle with AI. " + RecyclingAdvice.tipsFor(category)
                + " Can you tell me more about disposing of it properly?";
        openChatWithPrefill(prefill);
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
