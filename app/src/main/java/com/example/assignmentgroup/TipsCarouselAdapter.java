package com.example.assignmentgroup;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TipsCarouselAdapter extends RecyclerView.Adapter<TipsCarouselAdapter.TipViewHolder> {

    public static final String[] TIPS = {
            "Refuse single-use plastics whenever you can.",
            "Reduce waste by buying only what you need.",
            "Reuse containers and packaging before tossing them.",
            "Recycle clean paper, glass and metal separately.",
            "Recover energy from waste that can't be reused."
    };

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tip, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        holder.tipText.setText(TIPS[position % TIPS.length]);
    }

    @Override
    public int getItemCount() {
        return TIPS.length;
    }

    static class TipViewHolder extends RecyclerView.ViewHolder {
        TextView tipText;

        TipViewHolder(View itemView) {
            super(itemView);
            tipText = itemView.findViewById(R.id.tipText);
        }
    }
}
