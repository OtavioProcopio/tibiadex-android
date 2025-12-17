package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.HighscoreEntry;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.HighscoreAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HighscoresActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private RecyclerView recyclerHighscores;
    private HighscoreAdapter adapter;
    
    private Spinner spinnerWorld;
    private Spinner spinnerCategory;
    private Spinner spinnerVocation;
    
    private Button btnPrevPage;
    private Button btnNextPage;
    private TextView tvPageInfo;
    
    private final List<HighscoreEntry> entries = new ArrayList<>();
    
    private int currentPage = 1;
    private int totalPages = 1;
    private int totalRecords = 0;
    
    private String selectedWorld = "";
    private String selectedCategory = "experience";
    private String selectedVocation = "all";
    
    // Arrays para mapeamento
    private final List<String> worldsApi = new ArrayList<>();
    private String[] categoriesApi;
    private String[] categoriesDisplay;
    private String[] vocationsApi;
    private String[] vocationsDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        recyclerHighscores = findViewById(R.id.recyclerHighscores);
        recyclerHighscores.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HighscoreAdapter(entries, entry -> {
            Intent i = new Intent(this, CharactersActivity.class);
            i.putExtra("character_name", entry.name);
            startActivity(i);
        });
        recyclerHighscores.setAdapter(adapter);

        spinnerWorld = findViewById(R.id.spinnerWorld);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerVocation = findViewById(R.id.spinnerVocation);
        
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        tvPageInfo = findViewById(R.id.tvPageInfo);

        setupSpinners();
        setupPagination();
        
        fetchWorlds();
    }

    private void fetchWorlds() {
        progressLoading.setVisibility(View.VISIBLE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, ApiConstants.WORLDS, null,
                response -> {
                    try {
                        JSONObject worlds = response.getJSONObject("worlds");
                        JSONArray regularWorlds = worlds.getJSONArray("regular_worlds");
                        
                        worldsApi.clear();
                        for (int i = 0; i < regularWorlds.length(); i++) {
                            JSONObject world = regularWorlds.getJSONObject(i);
                            worldsApi.add(world.getString("name"));
                        }
                        
                        if (!worldsApi.isEmpty()) {
                            selectedWorld = worldsApi.get(0);
                            updateWorldSpinner();
                            fetchHighscores();
                        } else {
                            Toast.makeText(this, "Nenhum mundo encontrado", Toast.LENGTH_SHORT).show();
                            progressLoading.setVisibility(View.GONE);
                        }
                        
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar mundos", Toast.LENGTH_LONG).show();
                        progressLoading.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Toast.makeText(this, "Erro de conexão ao buscar mundos", Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.GONE);
                }
            )
        );
    }

    private void updateWorldSpinner() {
        String[] worldsArray = worldsApi.toArray(new String[0]);
        ArrayAdapter<String> worldAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, worldsArray);
        worldAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerWorld.setAdapter(worldAdapter);
    }

    private void setupSpinners() {
        // Categories
        categoriesDisplay = new String[]{
            "Experiência", "Pesca", "Conquistas", "Machado", "Clava", 
            "Distância", "Charm Points", "Loyalty Points", "Magic Level",
            "Escudo", "Espada", "Goshnar's Taint", "Drome Score", "Boss Points"
        };
        categoriesApi = new String[]{
            "experience", "fishing", "achievements", "axefighting", "clubfighting",
            "distancefighting", "charmpoints", "loyaltypoints", "magiclevel",
            "shielding", "swordfighting", "goshnarstaint", "dromescore", "bosspoints"
        };
        
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, categoriesDisplay);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        
        // Vocations
        vocationsDisplay = new String[]{"Todas", "Knights", "Paladins", "Sorcerers", "Druids"};
        vocationsApi = new String[]{"all", "knights", "paladins", "sorcerers", "druids"};
        
        ArrayAdapter<String> vocationAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, vocationsDisplay);
        vocationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerVocation.setAdapter(vocationAdapter);
        
        // Listeners
        spinnerWorld.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!worldsApi.isEmpty() && position < worldsApi.size()) {
                    selectedWorld = worldsApi.get(position);
                    currentPage = 1;
                    fetchHighscores();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoriesApi[position];
                currentPage = 1;
                fetchHighscores();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        
        spinnerVocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVocation = vocationsApi[position];
                currentPage = 1;
                fetchHighscores();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPagination() {
        btnPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                fetchHighscores();
            }
        });
        
        btnNextPage.setOnClickListener(v -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchHighscores();
            }
        });
    }

    private void fetchHighscores() {
        // Validação: não chamar API se world estiver vazio
        if (selectedWorld == null || selectedWorld.isEmpty()) {
            return;
        }
        
        String url = ApiConstants.highscores(
            selectedWorld, selectedCategory, selectedVocation, currentPage
        );
        
        progressLoading.setVisibility(View.VISIBLE);
        
        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject hs = response.getJSONObject("highscores");
                        JSONArray list = hs.getJSONArray("highscore_list");
                        JSONObject pagination = hs.getJSONObject("highscore_page");
                        
                        currentPage = pagination.getInt("current_page");
                        totalPages = pagination.getInt("total_pages");
                        totalRecords = pagination.getInt("total_records");
                        
                        entries.clear();
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject e = list.getJSONObject(i);
                            entries.add(new HighscoreEntry(
                                e.getInt("rank"),
                                e.getString("name"),
                                e.getString("vocation"),
                                e.getString("world"),
                                e.getInt("level"),
                                e.getInt("value")
                            ));
                        }
                        
                        adapter.notifyDataSetChanged();
                        updatePaginationUI();
                        progressLoading.setVisibility(View.GONE);
                        
                    } catch (Exception ex) {
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

    private void updatePaginationUI() {
        tvPageInfo.setText(String.format("Página %d de %d (%d registros)", 
            currentPage, totalPages, totalRecords));
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);
    }
}
