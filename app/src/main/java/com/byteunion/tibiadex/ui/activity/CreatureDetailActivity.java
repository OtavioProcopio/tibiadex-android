package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreatureDetailActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private ImageView imgCreature;
    private TextView tvName, tvDescription, tvBehaviour, tvHitpoints, tvExperience;
    private TextView tvImmune, tvStrong, tvWeakness, tvLoot;
    private TextView tvParalysed, tvSummoned, tvConvinced, tvSeeInvisible, tvLootable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creature_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        imgCreature = findViewById(R.id.imgCreature);
        tvName = findViewById(R.id.tvName);
        tvDescription = findViewById(R.id.tvDescription);
        tvBehaviour = findViewById(R.id.tvBehaviour);
        tvHitpoints = findViewById(R.id.tvHitpoints);
        tvExperience = findViewById(R.id.tvExperience);
        tvImmune = findViewById(R.id.tvImmune);
        tvStrong = findViewById(R.id.tvStrong);
        tvWeakness = findViewById(R.id.tvWeakness);
        tvLoot = findViewById(R.id.tvLoot);
        tvParalysed = findViewById(R.id.tvParalysed);
        tvSummoned = findViewById(R.id.tvSummoned);
        tvConvinced = findViewById(R.id.tvConvinced);
        tvSeeInvisible = findViewById(R.id.tvSeeInvisible);
        tvLootable = findViewById(R.id.tvLootable);

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

                        // Textos descritivos
                        tvDescription.setText(creature.optString("description", "N/A"));
                        tvBehaviour.setText(creature.optString("behaviour", "N/A"));

                        // Stats
                        tvHitpoints.setText("HP: " + creature.optInt("hitpoints", 0));
                        tvExperience.setText("XP: " + creature.optInt("experience_points", 0));

                        // Resistências e fraquezas
                        tvImmune.setText("Imune: " + joinArray(creature.optJSONArray("immune")));
                        tvStrong.setText("Resistente: " + joinArray(creature.optJSONArray("strong")));
                        tvWeakness.setText("Fraqueza: " + joinArray(creature.optJSONArray("weakness")));

                        // Loot
                        String loot = joinArray(creature.optJSONArray("loot_list"));
                        tvLoot.setText("Loot: " + (loot.isEmpty() ? "Nenhum" : loot));

                        // Características booleanas
                        tvParalysed.setText("Pode ser paralisado: " + (creature.optBoolean("be_paralysed", false) ? "Sim" : "Não"));
                        tvSummoned.setText("Pode ser summonado: " + (creature.optBoolean("be_summoned", false) ? "Sim" : "Não"));
                        tvConvinced.setText("Pode ser convencido: " + (creature.optBoolean("be_convinced", false) ? "Sim" : "Não"));
                        tvSeeInvisible.setText("Vê invisível: " + (creature.optBoolean("see_invisible", false) ? "Sim" : "Não"));
                        tvLootable.setText("Lootável: " + (creature.optBoolean("is_lootable", true) ? "Sim" : "Não"));

                        progressLoading.setVisibility(View.GONE);

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
