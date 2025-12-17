package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.byteunion.tibiadex.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCharacters = findViewById(R.id.btnCharacters);
        Button btnCreatures = findViewById(R.id.btnCreatures);
        Button btnBosses = findViewById(R.id.btnBosses);
        Button btnWorlds = findViewById(R.id.btnWorlds);
        Button btnSpells = findViewById(R.id.btnSpells);
        Button btnHighscores = findViewById(R.id.btnHighscores);
        Button btnFansites = findViewById(R.id.btnFansites);
        Button btnHouses = findViewById(R.id.btnHouses);
        Button btnNews = findViewById(R.id.btnNews);

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