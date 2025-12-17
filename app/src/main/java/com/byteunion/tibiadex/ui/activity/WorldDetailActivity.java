package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.KillStatistic;
import com.byteunion.tibiadex.data.model.OnlinePlayer;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.KillStatisticsAdapter;
import com.byteunion.tibiadex.ui.adapter.OnlinePlayerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WorldDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvSubtitle, tvInfo;
    private Button btnTogglePlayers, btnToggleKills;
    private View layoutPlayers, layoutKills;
    
    private EditText etSearchPlayer, etSearchKill;
    private RecyclerView recyclerPlayers, recyclerKills;
    
    private Button btnKnight, btnPaladin, btnSorcerer, btnDruid;
    
    private final List<OnlinePlayer> allPlayers = new ArrayList<>();
    private final List<OnlinePlayer> filteredPlayers = new ArrayList<>();
    private OnlinePlayerAdapter playerAdapter;
    
    private final List<KillStatistic> allKills = new ArrayList<>();
    private final List<KillStatistic> filteredKills = new ArrayList<>();
    private KillStatisticsAdapter killAdapter;
    
    private String selectedVocation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world_detail);

        String worldName = getIntent().getStringExtra("world_name");

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvWorldTitle);
        tvSubtitle = findViewById(R.id.tvWorldSubtitle);
        tvInfo = findViewById(R.id.tvWorldInfo);
        
        btnTogglePlayers = findViewById(R.id.btnTogglePlayers);
        btnToggleKills = findViewById(R.id.btnToggleKills);
        
        layoutPlayers = findViewById(R.id.layoutPlayers);
        layoutKills = findViewById(R.id.layoutKills);
        
        etSearchPlayer = findViewById(R.id.etSearchPlayer);
        etSearchKill = findViewById(R.id.etSearchKill);
        
        recyclerPlayers = findViewById(R.id.recyclerPlayers);
        recyclerKills = findViewById(R.id.recyclerKills);
        
        btnKnight = findViewById(R.id.btnKnight);
        btnPaladin = findViewById(R.id.btnPaladin);
        btnSorcerer = findViewById(R.id.btnSorcerer);
        btnDruid = findViewById(R.id.btnDruid);

        tvTitle.setText(worldName);

        recyclerPlayers.setLayoutManager(new LinearLayoutManager(this));
        playerAdapter = new OnlinePlayerAdapter(filteredPlayers, player -> {
            Intent i = new Intent(this, CharactersActivity.class);
            i.putExtra("character_name", player.name);
            startActivity(i);
        });
        recyclerPlayers.setAdapter(playerAdapter);

        recyclerKills.setLayoutManager(new LinearLayoutManager(this));
        killAdapter = new KillStatisticsAdapter(filteredKills);
        recyclerKills.setAdapter(killAdapter);

        btnTogglePlayers.setOnClickListener(v -> showPlayers());
        btnToggleKills.setOnClickListener(v -> showKills());

        setupVocationFilters();
        setupSearch();
        
        fetchWorldData(worldName);
    }

    private void setupVocationFilters() {
        btnKnight.setOnClickListener(v -> filterByVocation("Knight"));
        btnPaladin.setOnClickListener(v -> filterByVocation("Paladin"));
        btnSorcerer.setOnClickListener(v -> filterByVocation("Sorcerer"));
        btnDruid.setOnClickListener(v -> filterByVocation("Druid"));
    }

    private void filterByVocation(String vocation) {
        if (selectedVocation.equals(vocation)) {
            selectedVocation = "";
            updateVocationButtons();
            applyPlayerFilters(etSearchPlayer.getText().toString());
            return;
        }
        
        selectedVocation = vocation;
        updateVocationButtons();
        applyPlayerFilters(etSearchPlayer.getText().toString());
    }

    private void updateVocationButtons() {
        btnKnight.setBackgroundTintList(getColorStateList(
            selectedVocation.equals("Knight") ? R.color.tibia_gold : R.color.tibia_surface));
        btnPaladin.setBackgroundTintList(getColorStateList(
            selectedVocation.equals("Paladin") ? R.color.tibia_gold : R.color.tibia_surface));
        btnSorcerer.setBackgroundTintList(getColorStateList(
            selectedVocation.equals("Sorcerer") ? R.color.tibia_gold : R.color.tibia_surface));
        btnDruid.setBackgroundTintList(getColorStateList(
            selectedVocation.equals("Druid") ? R.color.tibia_gold : R.color.tibia_surface));
    }

    private void setupSearch() {
        etSearchPlayer.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyPlayerFilters(s.toString().toLowerCase());
            }
        });

        etSearchKill.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyKillFilters(s.toString().toLowerCase());
            }
        });
    }

    private void showPlayers() {
        layoutPlayers.setVisibility(View.VISIBLE);
        layoutKills.setVisibility(View.GONE);
        
        btnTogglePlayers.setBackgroundTintList(getColorStateList(R.color.tibia_gold));
        btnTogglePlayers.setTextColor(getColor(R.color.tibia_text_dark));
        
        btnToggleKills.setBackgroundTintList(getColorStateList(R.color.tibia_surface));
        btnToggleKills.setTextColor(getColor(R.color.tibia_gold));
    }

    private void showKills() {
        layoutPlayers.setVisibility(View.GONE);
        layoutKills.setVisibility(View.VISIBLE);
        
        btnToggleKills.setBackgroundTintList(getColorStateList(R.color.tibia_gold));
        btnToggleKills.setTextColor(getColor(R.color.tibia_text_dark));
        
        btnTogglePlayers.setBackgroundTintList(getColorStateList(R.color.tibia_surface));
        btnTogglePlayers.setTextColor(getColor(R.color.tibia_gold));
    }

    private void fetchWorldData(String worldName) {
        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.world(worldName), null,
                response -> {
                    try {
                        JSONObject world = response.getJSONObject("world");
                        
                        String status = world.getString("status");
                        int playersOnline = world.getInt("players_online");
                        String location = world.getString("location");
                        String pvpType = world.getString("pvp_type");
                        
                        tvSubtitle.setText(status + " • " + playersOnline + " jogadores online");
                        tvInfo.setText("🌍 " + location + " • ⚔️ " + pvpType);

                        // Players
                        JSONArray onlinePlayers = world.getJSONArray("online_players");
                        for (int i = 0; i < onlinePlayers.length(); i++) {
                            JSONObject p = onlinePlayers.getJSONObject(i);
                            allPlayers.add(new OnlinePlayer(
                                p.getString("name"),
                                p.getInt("level"),
                                p.getString("vocation")
                            ));
                        }
                        
                        applyPlayerFilters("");
                        
                        // Fetch kill statistics
                        fetchKillStatistics(worldName);
                        
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar dados", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show()
            )
        );
    }

    private void fetchKillStatistics(String worldName) {
        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.killStatistics(worldName), null,
                response -> {
                    try {
                        JSONObject killstats = response.getJSONObject("killstatistics");
                        JSONArray entries = killstats.getJSONArray("entries");
                        
                        for (int i = 0; i < entries.length(); i++) {
                            JSONObject e = entries.getJSONObject(i);
                            allKills.add(new KillStatistic(
                                e.getString("race"),
                                e.getInt("last_day_killed"),
                                e.getInt("last_day_players_killed"),
                                e.getInt("last_week_killed"),
                                e.getInt("last_week_players_killed")
                            ));
                        }
                        
                        applyKillFilters("");
                        
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao carregar estatísticas", Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show()
            )
        );
    }

    private void applyPlayerFilters(String query) {
        filteredPlayers.clear();
        
        for (OnlinePlayer p : allPlayers) {
            boolean nameMatch = p.name.toLowerCase().contains(query);
            boolean vocationMatch = selectedVocation.isEmpty() || p.vocation.contains(selectedVocation);
            
            if (nameMatch && vocationMatch) {
                filteredPlayers.add(p);
            }
        }
        
        playerAdapter.notifyDataSetChanged();
    }

    private void applyKillFilters(String query) {
        filteredKills.clear();
        
        for (KillStatistic k : allKills) {
            if (k.race.toLowerCase().contains(query)) {
                filteredKills.add(k);
            }
        }
        
        killAdapter.notifyDataSetChanged();
    }
}


