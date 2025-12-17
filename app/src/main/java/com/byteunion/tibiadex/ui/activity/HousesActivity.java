package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.House;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.HouseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HousesActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private RecyclerView recyclerHouses;
    private HouseAdapter adapter;
    
    private Spinner spinnerWorld;
    private Spinner spinnerTown;
    private EditText etSearch;
    
    private final List<String> worldsApi = new ArrayList<>();
    private final List<House> allHouses = new ArrayList<>();
    private final List<House> filteredHouses = new ArrayList<>();
    
    private String selectedWorld = "";
    private String selectedTown = "Ab'Dendriel";
    
    private final String[] townsApi = {
        "Ab'Dendriel", "Ankrahmun", "Candia", "Carlin", "Darashia", "Edron",
        "Farmine", "Gray Beach", "Issavi", "Kazordoon", "Liberty Bay", "Moonfall",
        "Port Hope", "Rathleton", "Silvertides", "Svargrond", "Thais", "Venore", "Yalahar"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_houses);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        etSearch = findViewById(R.id.etSearch);
        spinnerWorld = findViewById(R.id.spinnerWorld);
        spinnerTown = findViewById(R.id.spinnerTown);
        
        recyclerHouses = findViewById(R.id.recyclerHouses);
        recyclerHouses.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HouseAdapter(filteredHouses, house -> {
            Intent i = new Intent(this, HouseDetailActivity.class);
            i.putExtra("world", selectedWorld);
            i.putExtra("house_id", house.houseId);
            i.putExtra("house_name", house.name);
            startActivity(i);
        });
        recyclerHouses.setAdapter(adapter);

        setupSearch();
        setupTownSpinner();
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
                            setupWorldSpinner();
                            fetchHouses();
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
                    Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show();
                    progressLoading.setVisibility(View.GONE);
                }
            )
        );
    }

    private void setupWorldSpinner() {
        String[] worldsArray = worldsApi.toArray(new String[0]);
        ArrayAdapter<String> worldAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, worldsArray);
        worldAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerWorld.setAdapter(worldAdapter);
        
        spinnerWorld.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!worldsApi.isEmpty()) {
                    selectedWorld = worldsApi.get(position);
                    fetchHouses();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupTownSpinner() {
        ArrayAdapter<String> townAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, townsApi);
        townAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTown.setAdapter(townAdapter);
        
        spinnerTown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTown = townsApi[position];
                if (!selectedWorld.isEmpty()) {
                    fetchHouses();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchHouses() {
        progressLoading.setVisibility(View.VISIBLE);
        allHouses.clear();
        filteredHouses.clear();
        adapter.notifyDataSetChanged();
        
        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.houses(selectedWorld, selectedTown), null,
                response -> {
                    try {
                        JSONObject houses = response.getJSONObject("houses");
                        JSONArray houseList = houses.getJSONArray("house_list");
                        
                        for (int i = 0; i < houseList.length(); i++) {
                            JSONObject h = houseList.getJSONObject(i);
                            JSONObject auction = h.getJSONObject("auction");
                            
                            allHouses.add(new House(
                                h.getString("name"),
                                h.getInt("house_id"),
                                h.getInt("size"),
                                h.getInt("rent"),
                                h.getBoolean("rented"),
                                h.getBoolean("auctioned"),
                                auction.getInt("current_bid"),
                                auction.getString("time_left")
                            ));
                        }
                        
                        filteredHouses.addAll(allHouses);
                        adapter.notifyDataSetChanged();
                        progressLoading.setVisibility(View.GONE);
                        
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar casas", Toast.LENGTH_LONG).show();
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

    private void setupSearch() {
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHouses(s.toString().toLowerCase());
            }
        });
    }

    private void filterHouses(String query) {
        filteredHouses.clear();
        
        for (House h : allHouses) {
            if (h.name.toLowerCase().contains(query)) {
                filteredHouses.add(h);
            }
        }
        
        adapter.notifyDataSetChanged();
    }
}
