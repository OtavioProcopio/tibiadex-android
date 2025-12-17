package com.byteunion.tibiadex.ui.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Spell;

import java.util.List;

public class SpellAdapter extends RecyclerView.Adapter<SpellAdapter.ViewHolder> {

    private final List<Spell> spells;

    public SpellAdapter(List<Spell> spells) {
        this.spells = spells;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spell, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Spell s = spells.get(position);

        holder.name.setText(s.name);
        holder.formula.setText(s.formula.isEmpty() ? "—" : s.formula);
        holder.levelMana.setText("Nível " + s.level + " • " + s.mana + " mana");
        holder.price.setText(s.price + " gp");

        // Badges de grupo
        StringBuilder groups = new StringBuilder();
        if (s.groupAttack) groups.append("⚔️ Ataque ");
        if (s.groupHealing) groups.append("❤️ Cura ");
        if (s.groupSupport) groups.append("🛡️ Suporte ");
        holder.groups.setText(groups.toString().trim());

        // Badges de tipo
        StringBuilder types = new StringBuilder();
        if (s.typeInstant) types.append("⚡ Instantânea ");
        if (s.typeRune) types.append("📜 Runa ");
        if (s.premiumOnly) types.append("⭐ Premium");
        holder.types.setText(types.toString().trim());

        // Vocações (se disponível)
        if (s.hasDetailedInfo && s.vocations != null && !s.vocations.isEmpty()) {
            holder.vocations.setVisibility(View.VISIBLE);
            holder.vocations.setText("Vocações: " + String.join(", ", s.vocations));
        } else {
            holder.vocations.setVisibility(View.GONE);
        }

        // Cooldown (se disponível)
        if (s.hasDetailedInfo && s.cooldownAlone > 0) {
            holder.cooldown.setVisibility(View.VISIBLE);
            holder.cooldown.setText("Cooldown: " + s.cooldownAlone + "s");
        } else {
            holder.cooldown.setVisibility(View.GONE);
        }

        // Imagem (se disponível)
        if (s.hasDetailedInfo && s.imageUrl != null && !s.imageUrl.isEmpty()) {
            holder.image.setVisibility(View.VISIBLE);
            ImageRequest imgReq = new ImageRequest(
                    s.imageUrl,
                    holder.image::setImageBitmap,
                    0, 0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    error -> holder.image.setVisibility(View.GONE)
            );
            Volley.newRequestQueue(holder.itemView.getContext()).add(imgReq);
        } else {
            holder.image.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return spells.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, formula, levelMana, price, groups, types, vocations, cooldown;
        ImageView image;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSpellName);
            formula = itemView.findViewById(R.id.tvFormula);
            levelMana = itemView.findViewById(R.id.tvLevelMana);
            price = itemView.findViewById(R.id.tvPrice);
            groups = itemView.findViewById(R.id.tvGroups);
            types = itemView.findViewById(R.id.tvTypes);
            vocations = itemView.findViewById(R.id.tvVocations);
            cooldown = itemView.findViewById(R.id.tvCooldown);
            image = itemView.findViewById(R.id.imgSpell);
        }
    }
}
