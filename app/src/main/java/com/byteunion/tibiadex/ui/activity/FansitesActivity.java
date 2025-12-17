package com.byteunion.tibiadex.ui.activity;

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
import com.byteunion.tibiadex.data.model.Fansite;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;
import com.byteunion.tibiadex.ui.adapter.FansiteAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FansitesActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private RecyclerView recyclerFansites;
    private final List<Fansite> fansites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fansites);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        recyclerFansites = findViewById(R.id.recyclerFansites);
        recyclerFansites.setLayoutManager(new LinearLayoutManager(this));

        FansiteAdapter adapter = new FansiteAdapter(fansites);
        recyclerFansites.setAdapter(adapter);

        fetchFansites(adapter);
    }

    private void fetchFansites(FansiteAdapter adapter) {
        progressLoading.setVisibility(View.VISIBLE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, ApiConstants.FANSITES, null,
                response -> {
                    try {
                        JSONArray promoted = response
                                .getJSONObject("fansites")
                                .getJSONArray("promoted");

                        for (int i = 0; i < promoted.length(); i++) {
                            JSONObject f = promoted.getJSONObject(i);
                            
                            Fansite fansite = new Fansite(
                                f.getString("name"),
                                f.getString("logo_url"),
                                f.getString("homepage"),
                                f.optString("contact", "")
                            );

                            // Content types
                            JSONObject content = f.getJSONObject("content_type");
                            fansite.statistics = content.getBoolean("statistics");
                            fansite.texts = content.getBoolean("texts");
                            fansite.tools = content.getBoolean("tools");
                            fansite.wiki = content.getBoolean("wiki");

                            // Social media
                            JSONObject social = f.getJSONObject("social_media");
                            fansite.discord = social.getBoolean("discord");
                            fansite.facebook = social.getBoolean("facebook");
                            fansite.instagram = social.getBoolean("instagram");
                            fansite.reddit = social.getBoolean("reddit");
                            fansite.twitch = social.getBoolean("twitch");
                            fansite.twitter = social.getBoolean("twitter");
                            fansite.youtube = social.getBoolean("youtube");

                            // Languages
                            JSONArray langs = f.getJSONArray("languages");
                            fansite.languages = new ArrayList<>();
                            for (int j = 0; j < langs.length(); j++) {
                                fansite.languages.add(langs.getString(j));
                            }

                            // Specials
                            JSONArray specs = f.getJSONArray("specials");
                            fansite.specials = new ArrayList<>();
                            for (int j = 0; j < specs.length(); j++) {
                                fansite.specials.add(specs.getString(j));
                            }

                            fansite.fansiteItem = f.getBoolean("fansite_item");
                            fansite.fansiteItemUrl = f.optString("fansite_item_url", "");

                            fansites.add(fansite);
                        }

                        adapter.notifyDataSetChanged();
                        progressLoading.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar fansites", Toast.LENGTH_LONG).show();
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
}
