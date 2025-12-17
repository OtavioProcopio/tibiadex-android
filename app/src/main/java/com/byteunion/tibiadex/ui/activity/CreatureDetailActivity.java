package com.byteunion.tibiadex.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreatureDetailActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private LinearLayout layoutContent;
    private ImageView imgCreature;
    private TextView tvName, tvHP, tvXP;
    private TextView tvDescription, tvBehaviour, tvLoot, tvCharacteristics;
    private TextView tvImmune, tvStrong, tvWeakness;
    private MaterialCardView cardDescription, cardBehaviour, cardLoot, cardCharacteristics;
    private LinearLayout layoutImmune, layoutStrong, layoutWeakness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creature_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        layoutContent = findViewById(R.id.layoutContent);
        imgCreature = findViewById(R.id.imgCreature);
        tvName = findViewById(R.id.tvName);
        tvHP = findViewById(R.id.tvHP);
        tvXP = findViewById(R.id.tvXP);
        
        cardDescription = findViewById(R.id.cardDescription);
        tvDescription = findViewById(R.id.tvDescription);
        
        cardBehaviour = findViewById(R.id.cardBehaviour);
        tvBehaviour = findViewById(R.id.tvBehaviour);
        
        layoutImmune = findViewById(R.id.layoutImmune);
        tvImmune = findViewById(R.id.tvImmune);
        
        layoutStrong = findViewById(R.id.layoutStrong);
        tvStrong = findViewById(R.id.tvStrong);
        
        layoutWeakness = findViewById(R.id.layoutWeakness);
        tvWeakness = findViewById(R.id.tvWeakness);
        
        cardLoot = findViewById(R.id.cardLoot);
        tvLoot = findViewById(R.id.tvLoot);
        
        cardCharacteristics = findViewById(R.id.cardCharacteristics);
        tvCharacteristics = findViewById(R.id.tvCharacteristics);

        String creatureRace = getIntent().getStringExtra("creature_race");
        String creatureName = getIntent().getStringExtra("creature_name");

        if (creatureRace != null) {
            tvName.setText(creatureName);
            fetchCreatureDetails(creatureRace);
        } else {
            Toast.makeText(this, "Erro: criatura não especificada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchCreatureDetails(String race) {
        progressLoading.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, ApiConstants.creature(race), null,
                response -> {
                    try {
                        JSONObject creature = response.getJSONObject("creature");

                        tvName.setText(creature.getString("name"));
                        
                        // Carregar imagem
                        Picasso.get()
                                .load(creature.getString("image_url"))
                                .placeholder(R.color.tibia_surface)
                                .error(R.color.tibia_surface)
                                .into(imgCreature);

                        // Stats
                        tvHP.setText(String.valueOf(creature.optInt("hitpoints", 0)));
                        tvXP.setText(String.valueOf(creature.optInt("experience_points", 0)));

                        // Descrição
                        String description = creature.optString("description", "");
                        if (!description.isEmpty()) {
                            tvDescription.setText(description);
                            cardDescription.setVisibility(View.VISIBLE);
                        }

                        // Comportamento
                        String behaviour = creature.optString("behaviour", "");
                        if (!behaviour.isEmpty()) {
                            tvBehaviour.setText(behaviour);
                            cardBehaviour.setVisibility(View.VISIBLE);
                        }

                        // Resistências e fraquezas
                        String immune = joinArray(creature.optJSONArray("immune"));
                        if (!immune.equals("Nenhum")) {
                            tvImmune.setText(immune);
                            layoutImmune.setVisibility(View.VISIBLE);
                        }
                        
                        String strong = joinArray(creature.optJSONArray("strong"));
                        if (!strong.equals("Nenhum")) {
                            tvStrong.setText(strong);
                            layoutStrong.setVisibility(View.VISIBLE);
                        }
                        
                        String weakness = joinArray(creature.optJSONArray("weakness"));
                        if (!weakness.equals("Nenhum")) {
                            tvWeakness.setText(weakness);
                            layoutWeakness.setVisibility(View.VISIBLE);
                        }

                        // Loot
                        String loot = joinArray(creature.optJSONArray("loot_list"));
                        if (!loot.equals("Nenhum")) {
                            tvLoot.setText(loot);
                            cardLoot.setVisibility(View.VISIBLE);
                        }

                        // Características booleanas
                        List<String> characteristics = new ArrayList<>();
                        if (creature.optBoolean("be_paralysed", false))
                            characteristics.add("• Pode ser paralisado");
                        if (creature.optBoolean("be_summoned", false))
                            characteristics.add("• Pode ser summonado");
                        if (creature.optBoolean("be_convinced", false))
                            characteristics.add("• Pode ser convencido");
                        if (creature.optBoolean("see_invisible", false))
                            characteristics.add("• Vê invisível");
                        if (creature.optBoolean("is_lootable", true))
                            characteristics.add("• Lootável");
                        
                        if (!characteristics.isEmpty()) {
                            tvCharacteristics.setText(String.join("\n", characteristics));
                            cardCharacteristics.setVisibility(View.VISIBLE);
                        }

                        progressLoading.setVisibility(View.GONE);
                        layoutContent.setVisibility(View.VISIBLE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar dados", Toast.LENGTH_LONG).show();
                        progressLoading.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.GONE);
                }
            )
        );
    }

    private String joinArray(JSONArray array) {
        if (array == null || array.length() == 0) return "Nenhum";
        
        List<String> items = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                items.add(array.getString(i));
            }
        } catch (Exception e) {}
        
        return String.join(", ", items);
    }
}
