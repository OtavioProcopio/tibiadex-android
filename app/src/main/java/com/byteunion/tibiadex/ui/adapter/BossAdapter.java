package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Boss;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BossAdapter extends RecyclerView.Adapter<BossAdapter.ViewHolder> {

    private final List<Boss> bosses;

    public BossAdapter(List<Boss> bosses) {
        this.bosses = bosses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_boss, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Boss boss = bosses.get(position);
        holder.name.setText(boss.name);

        Picasso.get()
                .load(boss.imageUrl)
                .placeholder(R.color.tibia_surface)
                .error(R.color.tibia_surface)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return bosses.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvBossName);
            image = itemView.findViewById(R.id.imgBoss);
        }
    }
}
