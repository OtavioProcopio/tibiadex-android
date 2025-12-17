package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.HighscoreEntry;

import java.util.List;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(HighscoreEntry entry);
    }

    private final List<HighscoreEntry> entries;
    private final OnItemClick listener;

    public HighscoreAdapter(List<HighscoreEntry> entries, OnItemClick listener) {
        this.entries = entries;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_highscore, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HighscoreEntry entry = entries.get(position);

        holder.rank.setText("#" + entry.rank);
        holder.name.setText(entry.name);
        holder.vocation.setText(entry.vocation);
        holder.level.setText("Level: " + entry.level);
        holder.value.setText("Valor: " + entry.value);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(entry);
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rank, name, vocation, level, value;

        ViewHolder(View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.tvRank);
            name = itemView.findViewById(R.id.tvName);
            vocation = itemView.findViewById(R.id.tvVocation);
            level = itemView.findViewById(R.id.tvLevel);
            value = itemView.findViewById(R.id.tvValue);
        }
    }
}
