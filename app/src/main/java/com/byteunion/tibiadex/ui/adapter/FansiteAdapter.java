package com.byteunion.tibiadex.ui.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Fansite;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FansiteAdapter extends RecyclerView.Adapter<FansiteAdapter.ViewHolder> {

    private final List<Fansite> fansites;

    public FansiteAdapter(List<Fansite> fansites) {
        this.fansites = fansites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fansite, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Fansite f = fansites.get(position);

        holder.name.setText(f.name);
        holder.contact.setText("Contato: " + f.contact);
        
        // Content types
        StringBuilder content = new StringBuilder();
        if (f.statistics) content.append("📊 Stats ");
        if (f.texts) content.append("📝 Textos ");
        if (f.tools) content.append("🛠️ Ferramentas ");
        if (f.wiki) content.append("📚 Wiki ");
        holder.contentTypes.setText(content.toString().trim());

        // Social media
        StringBuilder social = new StringBuilder();
        if (f.discord) social.append("💬 Discord ");
        if (f.facebook) social.append("📘 Facebook ");
        if (f.instagram) social.append("📷 Instagram ");
        if (f.youtube) social.append("▶️ YouTube ");
        if (f.twitch) social.append("🎮 Twitch ");
        if (f.twitter) social.append("🐦 Twitter ");
        if (f.reddit) social.append("🔴 Reddit ");
        holder.socialMedia.setText(social.toString().trim());

        // Languages
        if (f.languages != null && !f.languages.isEmpty()) {
            holder.languages.setVisibility(View.VISIBLE);
            holder.languages.setText("Idiomas: " + String.join(", ", f.languages).toUpperCase());
        } else {
            holder.languages.setVisibility(View.GONE);
        }

        // Specials
        if (f.specials != null && !f.specials.isEmpty()) {
            holder.specials.setVisibility(View.VISIBLE);
            holder.specials.setText("✨ " + String.join("\n✨ ", f.specials));
        } else {
            holder.specials.setVisibility(View.GONE);
        }

        // Logo
        if (f.logoUrl != null && !f.logoUrl.isEmpty()) {
            holder.logo.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(f.logoUrl)
                    .placeholder(R.color.tibia_surface)
                    .error(R.color.tibia_surface)
                    .into(holder.logo);
        } else {
            holder.logo.setVisibility(View.GONE);
            Picasso.get().cancelRequest(holder.logo);
        }

        // Click opens homepage
        holder.itemView.setOnClickListener(v -> {
            if (f.homepage != null && !f.homepage.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(f.homepage));
                holder.itemView.getContext().startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fansites.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView logo;
        TextView name, contact, contentTypes, socialMedia, languages, specials;

        ViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.imgFansiteLogo);
            name = itemView.findViewById(R.id.tvFansiteName);
            contact = itemView.findViewById(R.id.tvContact);
            contentTypes = itemView.findViewById(R.id.tvContentTypes);
            socialMedia = itemView.findViewById(R.id.tvSocialMedia);
            languages = itemView.findViewById(R.id.tvLanguages);
            specials = itemView.findViewById(R.id.tvSpecials);
        }
    }
}
