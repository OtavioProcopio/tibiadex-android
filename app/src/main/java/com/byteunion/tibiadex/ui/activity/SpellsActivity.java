package com.byteunion.tibiadex.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Spell;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.SpellAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SpellsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private SpellAdapter adapter;
    private TextView etSearch;
    private HorizontalScrollView layoutFilters;

    private final List<Spell> allSpells = new ArrayList<>();
    private final List<Spell> filteredSpells = new ArrayList<>();
    private final List<Spell> visibleSpells = new ArrayList<>();

    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private boolean isLoading = false;

    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spells);

        progressBar = findViewById(R.id.progressLoading);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerSpells);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SpellAdapter(visibleSpells);
        recyclerView.setAdapter(adapter);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        etSearch = findViewById(R.id.etSearch);
        layoutFilters = findViewById(R.id.layoutFilters);

        setupSearch();
        setupFilters();
        setupScroll();
        fetchSpells();
    }

    private void fetchSpells() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, ApiConstants.SPELLS, null,
                response -> {
                    try {
                        JSONArray list = response
                                .getJSONObject("spells")
                                .getJSONArray("spell_list");

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject obj = list.getJSONObject(i);
                            allSpells.add(new Spell(
                                    obj.getString("name"),
                                    obj.getString("spell_id"),
                                    obj.optString("formula", ""),
                                    obj.getInt("level"),
                                    obj.getInt("mana"),
                                    obj.getInt("price"),
                                    obj.getBoolean("group_attack"),
                                    obj.getBoolean("group_healing"),
                                    obj.getBoolean("group_support"),
                                    obj.getBoolean("type_instant"),
                                    obj.getBoolean("type_rune"),
                                    obj.getBoolean("premium_only")
                            ));
                        }

                        filteredSpells.addAll(allSpells);
                        loadNextPage();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Erro ao processar spells",
                                Toast.LENGTH_LONG).show();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Erro de conexão",
                            Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setupScroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();

                if (!isLoading &&
                        lm != null &&
                        lm.findLastVisibleItemPosition() >= visibleSpells.size() - 3) {
                    loadNextPage();
                }
            }
        });
    }

    private void loadNextPage() {
        if (isLoading) return;

        isLoading = true;

        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredSpells.size());

        if (start < end) {
            List<Spell> newSpells = filteredSpells.subList(start, end);
            visibleSpells.addAll(newSpells);
            adapter.notifyDataSetChanged();
            
            // Enriquecer com dados detalhados
            for (int i = start; i < end; i++) {
                final int position = i;
                fetchSpellDetails(filteredSpells.get(i).spellId, position);
            }
            
            currentPage++;
        }

        isLoading = false;
    }

    private void fetchSpellDetails(String spellId, int position) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, ApiConstants.spell(spellId), null,
                response -> {
                    try {
                        JSONObject spell = response.getJSONObject("spell");
                        
                        if (spell.getBoolean("has_spell_information")) {
                            JSONObject info = spell.getJSONObject("spell_information");
                            
                            List<String> vocations = new ArrayList<>();
                            if (info.has("vocation")) {
                                JSONArray vocArray = info.getJSONArray("vocation");
                                for (int i = 0; i < vocArray.length(); i++) {
                                    vocations.add(vocArray.getString(i));
                                }
                            }
                            
                            List<String> cities = new ArrayList<>();
                            if (info.has("city")) {
                                JSONArray cityArray = info.getJSONArray("city");
                                for (int i = 0; i < cityArray.length(); i++) {
                                    cities.add(cityArray.getString(i));
                                }
                            }
                            
                            // Atualizar spell na posição correta
                            if (position < filteredSpells.size()) {
                                Spell s = filteredSpells.get(position);
                                s.enrichWithDetails(
                                        spell.optString("image_url", ""),
                                        vocations,
                                        cities,
                                        info.optInt("cooldown_alone", 0),
                                        info.optInt("cooldown_group", 0)
                                );
                                
                                // Atualizar UI se item estiver visível
                                int visibleIndex = visibleSpells.indexOf(s);
                                if (visibleIndex >= 0) {
                                    adapter.notifyItemChanged(visibleIndex);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Silenciosamente falhar - manter dados básicos
                    }
                },
                error -> {
                    // Silenciosamente falhar - manter dados básicos
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSpells();
            }
        });
    }

    private void setupFilters() {
        findViewById(R.id.btnAll).setOnClickListener(v -> {
            currentFilter = "all";
            filterSpells();
        });
        
        findViewById(R.id.btnAttack).setOnClickListener(v -> {
            currentFilter = "attack";
            filterSpells();
        });
        
        findViewById(R.id.btnHealing).setOnClickListener(v -> {
            currentFilter = "healing";
            filterSpells();
        });
        
        findViewById(R.id.btnSupport).setOnClickListener(v -> {
            currentFilter = "support";
            filterSpells();
        });
        
        findViewById(R.id.btnInstant).setOnClickListener(v -> {
            currentFilter = "instant";
            filterSpells();
        });
        
        findViewById(R.id.btnRune).setOnClickListener(v -> {
            currentFilter = "rune";
            filterSpells();
        });
    }

    private void filterSpells() {
        String query = etSearch.getText().toString().toLowerCase();
        
        filteredSpells.clear();
        visibleSpells.clear();
        currentPage = 0;
        
        for (Spell s : allSpells) {
            boolean matchesSearch = s.name.toLowerCase().contains(query) ||
                                   s.formula.toLowerCase().contains(query);
            
            boolean matchesFilter = false;
            switch (currentFilter) {
                case "all":
                    matchesFilter = true;
                    break;
                case "attack":
                    matchesFilter = s.groupAttack;
                    break;
                case "healing":
                    matchesFilter = s.groupHealing;
                    break;
                case "support":
                    matchesFilter = s.groupSupport;
                    break;
                case "instant":
                    matchesFilter = s.typeInstant;
                    break;
                case "rune":
                    matchesFilter = s.typeRune;
                    break;
            }
            
            if (matchesSearch && matchesFilter) {
                filteredSpells.add(s);
            }
        }
        
        adapter.notifyDataSetChanged();
        loadNextPage();
    }
}
