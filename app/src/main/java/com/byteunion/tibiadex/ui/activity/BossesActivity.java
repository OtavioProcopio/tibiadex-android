package com.byteunion.tibiadex.ui.activity;

import android.graphics.Bitmap;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.Boss;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.BossAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BossesActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private BossAdapter adapter;

    private final List<Boss> allBosses = new ArrayList<>();
    private final List<Boss> visibleBosses = new ArrayList<>();

    private static final int PAGE_SIZE = 10;
    private int currentPage = 0;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bosses);

        progressBar = findViewById(R.id.progressLoading);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerBosses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BossAdapter(visibleBosses);
        recyclerView.setAdapter(adapter);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());



        fetchBosses();
        setupScroll();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // volta para a Activity anterior (MainActivity)
        return true;
    }



    private void fetchBosses() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, ApiConstants.BOOSTABLE_BOSSES, null,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("boostable_bosses");

                        JSONObject boosted = data.getJSONObject("boosted");

                        TextView boostedName = findViewById(R.id.tvBoostedName);
                        ImageView boostedImg = findViewById(R.id.imgBoosted);

                        boostedName.setText(boosted.getString("name"));

                        ImageRequest imgReq = new ImageRequest(
                                boosted.getString("image_url"),
                                boostedImg::setImageBitmap,
                                0, 0,
                                ImageView.ScaleType.CENTER_CROP,
                                Bitmap.Config.RGB_565,
                                error -> Toast.makeText(this,
                                        "Erro ao carregar imagem do boss boostado",
                                        Toast.LENGTH_SHORT).show()
                        );
                        VolleySingleton.getInstance(this).addToRequestQueue(imgReq);

                        JSONArray list = data.getJSONArray("boostable_boss_list");

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject obj = list.getJSONObject(i);
                            allBosses.add(new Boss(
                                    obj.getString("name"),
                                    obj.getString("image_url")
                            ));
                        }

                        loadNextPage();

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Erro ao processar dados da API",
                                Toast.LENGTH_LONG).show();
                    } finally {
                        progressBar.setVisibility(View.GONE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this,
                            "Erro de conexão com a API do Tibia",
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
                        lm.findLastVisibleItemPosition() >= visibleBosses.size() - 3) {
                    loadNextPage();
                }
            }
        });
    }

    private void loadNextPage() {
        if (isLoading) return;

        isLoading = true;

        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allBosses.size());

        if (start < end) {
            visibleBosses.addAll(allBosses.subList(start, end));
            adapter.notifyDataSetChanged();
            currentPage++;
        }

        isLoading = false;
    }
}