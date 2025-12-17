package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.data.model.OnlinePlayer;

import java.util.List;

public class OnlinePlayerAdapter
        extends RecyclerView.Adapter<OnlinePlayerAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(OnlinePlayer player);
    }

    private final List<OnlinePlayer> players;
    private final OnItemClick listener;

    public OnlinePlayerAdapter(List<OnlinePlayer> players, OnItemClick listener) {
        this.players = players;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        OnlinePlayer p = players.get(pos);
        h.title.setText(p.name + " (Lv " + p.level + ")");
        h.subtitle.setText(p.vocation);
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ViewHolder(View v) {
            super(v);
            title = v.findViewById(android.R.id.text1);
            subtitle = v.findViewById(android.R.id.text2);
        }
    }
}
