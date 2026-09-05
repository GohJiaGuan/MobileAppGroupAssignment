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
public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    public interface Listener {
        void onIncrease(int position);
        void onDecrease(int position);
        void onDelete(int position);
    }

    private final List<Item> items;
    private final Listener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd", Locale.getDefault());

    public InventoryAdapter(List<Item> items, Listener listener) {
        this.items = items;
        this.listener = listener;
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

        holder.incBtn.setOnClickListener(v -> listener.onIncrease(holder.getAdapterPosition()));
        holder.decBtn.setOnClickListener(v -> listener.onDecrease(holder.getAdapterPosition()));
        holder.deleteBtn.setOnClickListener(v -> listener.onDelete(holder.getAdapterPosition()));
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
