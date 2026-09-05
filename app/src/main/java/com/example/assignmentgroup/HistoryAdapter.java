package com.example.assignmentgroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Read-only variant of InventoryAdapter for the History screen (no edit/delete controls).
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<Item> items;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public HistoryAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.name.setText(item.name);
        holder.qty.setText(String.valueOf(item.Quantity));

        if (item.Category == null || item.Category.trim().isEmpty()) {
            holder.category.setVisibility(View.GONE);
        } else {
            holder.category.setVisibility(View.VISIBLE);
            holder.category.setText(item.Category);
        }

        holder.date.setText(dateFormat.format(new Date(item.date)));

        holder.incBtn.setVisibility(View.GONE);
        holder.decBtn.setVisibility(View.GONE);
        holder.deleteBtn.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, category, date;
        ImageButton incBtn, decBtn, deleteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.itemName);
            qty = itemView.findViewById(R.id.itemQty);
            category = itemView.findViewById(R.id.itemCategory);
            date = itemView.findViewById(R.id.itemDate);
            incBtn = itemView.findViewById(R.id.incButton);
            decBtn = itemView.findViewById(R.id.decButton);
            deleteBtn = itemView.findViewById(R.id.deleteButton);
        }
    }
}
