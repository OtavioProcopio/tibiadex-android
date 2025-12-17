package com.byteunion.tibiadex.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    public interface OnItemClick {
        void onClick(News news);
    }

    private final List<News> newsList;
    private final OnItemClick listener;

    public NewsAdapter(List<News> newsList, OnItemClick listener) {
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        News n = newsList.get(position);

        holder.title.setText(n.news);
        holder.date.setText(formatDate(n.date));
        holder.category.setText(getCategoryEmoji(n.category) + " " + getCategoryName(n.category));
        holder.type.setText(n.type.equals("news") ? "📰" : "📌");

        // Cor do card por categoria
        int color = holder.itemView.getContext().getColor(getCategoryColor(n.category));
        holder.itemView.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> listener.onClick(n));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    private String formatDate(String isoDate) {
        try {
            String[] parts = isoDate.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return isoDate;
        }
    }

    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "development": return "🔧";
            case "technical": return "⚙️";
            case "community": return "👥";
            default: return "📰";
        }
    }

    private String getCategoryName(String category) {
        switch (category.toLowerCase()) {
            case "development": return "Desenvolvimento";
            case "technical": return "Técnico";
            case "community": return "Comunidade";
            default: return category;
        }
    }

    private int getCategoryColor(String category) {
        switch (category.toLowerCase()) {
            case "development": return R.color.tibia_gold;
            case "technical": return R.color.tibia_gold_light;
            case "community": return R.color.tibia_text_light;
            default: return R.color.tibia_surface;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, category, type;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvNewsTitle);
            date = itemView.findViewById(R.id.tvNewsDate);
            category = itemView.findViewById(R.id.tvNewsCategory);
            type = itemView.findViewById(R.id.tvNewsType);
        }
    }
}
