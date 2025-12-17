package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Creature;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CreatureAdapter extends RecyclerView.Adapter<CreatureAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(Creature creature);
    }

    private final List<Creature> creatures;
    private final OnItemClick listener;

    public CreatureAdapter(List<Creature> creatures, OnItemClick listener) {
        this.creatures = creatures;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_creature, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Creature c = creatures.get(position);

        holder.name.setText(c.name);
        holder.basicInfo.setText(c.featured ? "⭐ Featured" : "");
        
        // Image
        if (c.imageUrl != null && !c.imageUrl.isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(c.imageUrl)
                    .placeholder(R.color.tibia_surface)
                    .error(R.color.tibia_surface)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() {
        return creatures.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, basicInfo;

        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgCreature);
            name = itemView.findViewById(R.id.tvCreatureName);
            basicInfo = itemView.findViewById(R.id.tvBasicInfo);
        }
    }
}
