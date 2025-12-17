package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.House;

import java.util.List;

public class HouseAdapter extends RecyclerView.Adapter<HouseAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(House house);
    }

    private final List<House> houses;
    private final OnItemClick listener;

    public HouseAdapter(List<House> houses, OnItemClick listener) {
        this.houses = houses;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_house, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        House h = houses.get(position);

        holder.name.setText(h.name);
        holder.size.setText("📐 " + h.size + " sqm");
        holder.rent.setText("💰 " + formatGold(h.rent) + "/mês");
        
        if (h.rented) {
            holder.status.setText("✅ Alugada");
            holder.status.setTextColor(holder.itemView.getContext().getColor(com.byteunion.tibiadex.R.color.tibia_gold_light));
        } else if (h.auctioned) {
            holder.status.setText("🔨 Leilão");
            holder.status.setTextColor(holder.itemView.getContext().getColor(com.byteunion.tibiadex.R.color.tibia_gold));
        } else {
            holder.status.setText("🆓 Disponível");
            holder.status.setTextColor(holder.itemView.getContext().getColor(com.byteunion.tibiadex.R.color.tibia_text_light));
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(h));
    }

    @Override
    public int getItemCount() {
        return houses.size();
    }

    private String formatGold(int amount) {
        if (amount >= 1000000) {
            return String.format("%.1fM", amount / 1000000.0);
        } else if (amount >= 1000) {
            return String.format("%.1fk", amount / 1000.0);
        }
        return String.valueOf(amount);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, size, rent, status;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvHouseName);
            size = itemView.findViewById(R.id.tvSize);
            rent = itemView.findViewById(R.id.tvRent);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}
