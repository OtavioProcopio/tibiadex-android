package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.data.model.News;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.NewsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private RecyclerView recyclerNews;
    private NewsAdapter adapter;
    private Spinner spinnerDays;
    
    private Button btnFilterDev, btnFilterTech, btnFilterComm, btnFilterAll;
    private Button btnTypeNews, btnTypeTicker, btnTypeAll;
    
    private final List<News> allNews = new ArrayList<>();
    private final List<News> filteredNews = new ArrayList<>();
    
    private int selectedDays = 30;
    private String selectedCategory = "all";
    private String selectedType = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        spinnerDays = findViewById(R.id.spinnerDays);
        
        btnFilterDev = findViewById(R.id.btnFilterDev);
        btnFilterTech = findViewById(R.id.btnFilterTech);
        btnFilterComm = findViewById(R.id.btnFilterComm);
        btnFilterAll = findViewById(R.id.btnFilterAll);
        
        btnTypeNews = findViewById(R.id.btnTypeNews);
        btnTypeTicker = findViewById(R.id.btnTypeTicker);
        btnTypeAll = findViewById(R.id.btnTypeAll);

        recyclerNews = findViewById(R.id.recyclerNews);
        recyclerNews.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NewsAdapter(filteredNews, news -> {
            Intent i = new Intent(this, NewsDetailActivity.class);
            i.putExtra("news_id", news.id);
            startActivity(i);
        });
        recyclerNews.setAdapter(adapter);

        setupDaysSpinner();
        setupCategoryFilters();
        setupTypeFilters();
        fetchNews();
    }

    private void setupDaysSpinner() {
        String[] daysOptions = {"Hoje (1)", "Semana (7)", "Mês (30)", "3 Meses (90)"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(
            this, R.layout.spinner_item, daysOptions);
        daysAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDays.setAdapter(daysAdapter);
        spinnerDays.setSelection(2); // Default: 30 dias
        
        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDays = new int[]{1, 7, 30, 90}[position];
                fetchNews();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCategoryFilters() {
        btnFilterAll.setOnClickListener(v -> {
            selectedCategory = "all";
            updateCategoryButtons();
            applyFilters();
        });
        
        btnFilterDev.setOnClickListener(v -> {
            selectedCategory = "development";
            updateCategoryButtons();
            applyFilters();
        });
        
        btnFilterTech.setOnClickListener(v -> {
            selectedCategory = "technical";
            updateCategoryButtons();
            applyFilters();
        });
        
        btnFilterComm.setOnClickListener(v -> {
            selectedCategory = "community";
            updateCategoryButtons();
            applyFilters();
        });
        
        updateCategoryButtons();
    }

    private void setupTypeFilters() {
        btnTypeAll.setOnClickListener(v -> {
            selectedType = "all";
            updateTypeButtons();
            applyFilters();
        });
        
        btnTypeNews.setOnClickListener(v -> {
            selectedType = "news";
            updateTypeButtons();
            applyFilters();
        });
        
        btnTypeTicker.setOnClickListener(v -> {
            selectedType = "ticker";
            updateTypeButtons();
            applyFilters();
        });
        
        updateTypeButtons();
    }

    private void updateCategoryButtons() {
        btnFilterAll.setBackgroundTintList(getColorStateList(
            selectedCategory.equals("all") ? R.color.tibia_gold : R.color.tibia_surface));
        btnFilterDev.setBackgroundTintList(getColorStateList(
            selectedCategory.equals("development") ? R.color.tibia_gold : R.color.tibia_surface));
        btnFilterTech.setBackgroundTintList(getColorStateList(
            selectedCategory.equals("technical") ? R.color.tibia_gold : R.color.tibia_surface));
        btnFilterComm.setBackgroundTintList(getColorStateList(
            selectedCategory.equals("community") ? R.color.tibia_gold : R.color.tibia_surface));
    }

    private void updateTypeButtons() {
        btnTypeAll.setBackgroundTintList(getColorStateList(
            selectedType.equals("all") ? R.color.tibia_gold : R.color.tibia_surface));
        btnTypeNews.setBackgroundTintList(getColorStateList(
            selectedType.equals("news") ? R.color.tibia_gold : R.color.tibia_surface));
        btnTypeTicker.setBackgroundTintList(getColorStateList(
            selectedType.equals("ticker") ? R.color.tibia_gold : R.color.tibia_surface));
    }

    private void fetchNews() {
        progressLoading.setVisibility(View.VISIBLE);
        allNews.clear();
        filteredNews.clear();
        adapter.notifyDataSetChanged();
        
        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.newsArchive(selectedDays), null,
                response -> {
                    try {
                        JSONArray newsArray = response.getJSONArray("news");
                        
                        for (int i = 0; i < newsArray.length(); i++) {
                            JSONObject n = newsArray.getJSONObject(i);
                            
                            allNews.add(new News(
                                n.getInt("id"),
                                n.getString("date"),
                                n.getString("news"),
                                n.getString("category"),
                                n.getString("type"),
                                n.getString("url")
                            ));
                        }
                        
                        applyFilters();
                        progressLoading.setVisibility(View.GONE);
                        
                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar notícias", Toast.LENGTH_LONG).show();
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

    private void applyFilters() {
        filteredNews.clear();
        
        for (News n : allNews) {
            boolean categoryMatch = selectedCategory.equals("all") || n.category.equals(selectedCategory);
            boolean typeMatch = selectedType.equals("all") || n.type.equals(selectedType);
            
            if (categoryMatch && typeMatch) {
                filteredNews.add(n);
            }
        }
        
        adapter.notifyDataSetChanged();
    }
}
