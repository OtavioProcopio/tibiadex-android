package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.World;

import java.util.List;

public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.ViewHolder> {

    public interface OnWorldClick {
        void onClick(World world);
    }

    private final List<World> worlds;
    private final OnWorldClick listener;

    public WorldAdapter(List<World> worlds, OnWorldClick listener) {
        this.worlds = worlds;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_world, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        World w = worlds.get(position);

        holder.name.setText(w.name);
        holder.info.setText(
                w.status + " • " +
                        w.playersOnline + " online • " +
                        w.location + " • " +
                        w.pvpType
        );

        holder.itemView.setOnClickListener(v -> listener.onClick(w));
    }

    @Override
    public int getItemCount() {
        return worlds.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvWorldName);
            info = itemView.findViewById(R.id.tvWorldInfo);
        }
    }
}
