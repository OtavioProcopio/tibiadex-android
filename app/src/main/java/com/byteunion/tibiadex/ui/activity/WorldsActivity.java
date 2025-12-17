package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.World;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.WorldAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class WorldsActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private final List<World> worlds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worlds);

        progressBar = findViewById(R.id.progressLoading);
        recyclerView = findViewById(R.id.recyclerWorlds);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        WorldAdapter adapter = new WorldAdapter(worlds, world -> {
            Intent i = new Intent(this, WorldDetailActivity.class);
            i.putExtra("world_name", world.name);
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);

        fetchWorlds(adapter);
    }

    private void fetchWorlds(WorldAdapter adapter) {
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, ApiConstants.WORLDS, null,
                response -> {
                    try {
                        JSONArray list = response
                                .getJSONObject("worlds")
                                .getJSONArray("regular_worlds");

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject o = list.getJSONObject(i);
                            worlds.add(new World(
                                    o.getString("name"),
                                    o.getString("status"),
                                    o.getInt("players_online"),
                                    o.getString("location"),
                                    o.getString("pvp_type")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Erro ao processar mundos",
                                Toast.LENGTH_LONG).show();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Erro ao carregar mundos",
                            Toast.LENGTH_LONG).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
