package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.byteunion.tibiadex.R;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView btnCharacters = findViewById(R.id.btnCharacters);
        MaterialCardView btnCreatures = findViewById(R.id.btnCreatures);
        MaterialCardView btnBosses = findViewById(R.id.btnBosses);
        MaterialCardView btnWorlds = findViewById(R.id.btnWorlds);
        MaterialCardView btnSpells = findViewById(R.id.btnSpells);
        MaterialCardView btnHighscores = findViewById(R.id.btnHighscores);
        MaterialCardView btnFansites = findViewById(R.id.btnFansites);
        MaterialCardView btnHouses = findViewById(R.id.btnHouses);
        MaterialCardView btnNews = findViewById(R.id.btnNews);

        btnCharacters.setOnClickListener(v ->
                startActivity(new Intent(this, CharactersActivity.class)));

        btnCreatures.setOnClickListener(v ->
                startActivity(new Intent(this, CreaturesActivity.class)));

        btnBosses.setOnClickListener(v ->
                startActivity(new Intent(this, BossesActivity.class)));

        btnWorlds.setOnClickListener(v ->
                startActivity(new Intent(this, WorldsActivity.class)));

        btnSpells.setOnClickListener(v ->
                startActivity(new Intent(this, SpellsActivity.class)));

        btnHighscores.setOnClickListener(v ->
                startActivity(new Intent(this, HighscoresActivity.class)));

        btnFansites.setOnClickListener(v ->
                startActivity(new Intent(this, FansitesActivity.class)));

        btnHouses.setOnClickListener(v ->
                startActivity(new Intent(this, HousesActivity.class)));

        btnNews.setOnClickListener(v ->
                startActivity(new Intent(this, NewsActivity.class)));
    }
}