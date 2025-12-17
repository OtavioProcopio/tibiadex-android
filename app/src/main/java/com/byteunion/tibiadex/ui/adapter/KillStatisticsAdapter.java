package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.KillStatistic;

import java.util.List;

public class KillStatisticsAdapter
        extends RecyclerView.Adapter<KillStatisticsAdapter.ViewHolder> {

    private final List<KillStatistic> items;

    public KillStatisticsAdapter(List<KillStatistic> items) {
        this.items = items;
    }

    // ----------------------------
    // ViewHolder
    // ----------------------------
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRace;
        TextView tvDay;
        TextView tvWeek;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRace = itemView.findViewById(R.id.tvRace);
            tvDay  = itemView.findViewById(R.id.tvDay);
            tvWeek = itemView.findViewById(R.id.tvWeek);
        }
    }

    // ----------------------------
    // Adapter lifecycle
    // ----------------------------
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kill_statistic, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        KillStatistic k = items.get(position);

        holder.tvRace.setText(k.race);

        holder.tvDay.setText(
                "Hoje: " +
                        k.dayKilled + " mortos • " +
                        k.dayPlayersKilled + " players"
        );

        holder.tvWeek.setText(
                "Semana: " +
                        k.weekKilled + " mortos • " +
                        k.weekPlayersKilled + " players"
        );
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
}

