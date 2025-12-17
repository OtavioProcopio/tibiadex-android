package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.byteunion.tibiadex.data.model.Creature;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.CreatureAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreaturesActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private RecyclerView recyclerCreatures;
    private CreatureAdapter adapter;
    
    private ImageView imgBoosted;
    private TextView tvBoostedName;
    private TextView tvCreatureCount;
    private TextView tvTotalCreatures;
    private TextView tvFilteredCount;

    private final List<Creature> allCreatures = new ArrayList<>();
    private final List<Creature> filteredCreatures = new ArrayList<>();
    private final List<Creature> visibleCreatures = new ArrayList<>();

    private static final int PAGE_SIZE = 20;
    private int currentPage = 0;
    private boolean isLoading = false;
    
    private TextView etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatures);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        imgBoosted = findViewById(R.id.imgBoosted);
        tvBoostedName = findViewById(R.id.tvBoostedName);
        tvCreatureCount = findViewById(R.id.tvCreatureCount);
        tvTotalCreatures = findViewById(R.id.tvTotalCreatures);
        tvFilteredCount = findViewById(R.id.tvFilteredCount);
        etSearch = findViewById(R.id.etSearch);

        recyclerCreatures = findViewById(R.id.recyclerCreatures);
        recyclerCreatures.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CreatureAdapter(visibleCreatures, creature -> {
            // Click abre tela de detalhes
            Intent i = new Intent(this, CreatureDetailActivity.class);
            i.putExtra("creature_race", creature.race);
            i.putExtra("creature_name", creature.name);
            startActivity(i);
        });
        recyclerCreatures.setAdapter(adapter);

        setupSearch();
        setupScroll();
        fetchCreatures();
    }

    private void fetchCreatures() {
        progressLoading.setVisibility(View.VISIBLE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, ApiConstants.CREATURES, null,
                response -> {
                    try {
                        JSONObject creatures = response.getJSONObject("creatures");
                        
                        // Boosted creature
                        JSONObject boosted = creatures.getJSONObject("boosted");
                        tvBoostedName.setText(boosted.getString("name"));
                        
                        Picasso.get()
                                .load(boosted.getString("image_url"))
                                .placeholder(R.color.tibia_surface)
                                .error(R.color.tibia_surface)
                                .into(imgBoosted);

                        // Creature list - apenas dados básicos
                        JSONArray list = creatures.getJSONArray("creature_list");
                        
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject c = list.getJSONObject(i);
                            allCreatures.add(new Creature(
                                c.getString("name"),
                                c.getString("race"),
                                c.getString("image_url"),
                                c.getBoolean("featured")
                            ));
                        }

                        filteredCreatures.addAll(allCreatures);
                        updateStats();
                        loadNextPage();
                        progressLoading.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar criaturas", Toast.LENGTH_LONG).show();
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

    private void setupScroll() {
        recyclerCreatures.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();

                if (!isLoading &&
                        lm != null &&
                        lm.findLastVisibleItemPosition() >= visibleCreatures.size() - 3) {
                    loadNextPage();
                }
            }
        });
    }

    private void loadNextPage() {
        if (isLoading) return;

        isLoading = true;

        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, filteredCreatures.size());

        if (start < end) {
            visibleCreatures.addAll(filteredCreatures.subList(start, end));
            adapter.notifyDataSetChanged();
            currentPage++;
        }

        isLoading = false;
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCreatures(s.toString().toLowerCase());
            }
        });
    }

    private void filterCreatures(String query) {
        filteredCreatures.clear();
        visibleCreatures.clear();
        currentPage = 0;
        
        for (Creature c : allCreatures) {
            if (c.name.toLowerCase().contains(query)) {
                filteredCreatures.add(c);
            }
        }
        
        updateStats();
        adapter.notifyDataSetChanged();
        loadNextPage();
    }

    private void updateStats() {
        tvCreatureCount.setText(String.format("%d criaturas catalogadas", allCreatures.size()));
        tvTotalCreatures.setText(String.valueOf(allCreatures.size()));
        tvFilteredCount.setText(String.valueOf(filteredCreatures.size()));
    }
}
