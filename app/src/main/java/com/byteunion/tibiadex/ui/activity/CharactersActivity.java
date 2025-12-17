package com.byteunion.tibiadex.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;


public class CharactersActivity extends AppCompatActivity {

    private static final String TAG = "CharactersActivity";

    private EditText etCharacterName;
    private View includeCard;

    private TextView tvCharName;
    private TextView tvCharTitle;
    private TextView tvCharMainInfo;
    private TextView tvAccountStatus;
    private TextView tvAchievementPoints;
    private TextView tvCharDetails;
    private TextView tvFormerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);

        etCharacterName = findViewById(R.id.etCharacterName);

        Button btnSearch = findViewById(R.id.btnSearchCharacter);
        Button btnBack   = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        includeCard = findViewById(R.id.characterCard);

        tvCharName          = includeCard.findViewById(R.id.tvCharName);
        tvCharTitle         = includeCard.findViewById(R.id.tvCharTitle);
        tvCharMainInfo      = includeCard.findViewById(R.id.tvCharMainInfo);
        tvAccountStatus     = includeCard.findViewById(R.id.tvAccountStatus);
        tvAchievementPoints = includeCard.findViewById(R.id.tvAchievementPoints);
        tvCharDetails       = includeCard.findViewById(R.id.tvCharDetails);
        tvFormerNames       = includeCard.findViewById(R.id.tvFormerNames);

        includeCard.setVisibility(View.GONE);

        // Check if character name was passed from another activity
        String intentCharName = getIntent().getStringExtra("character_name");
        if (intentCharName != null && !intentCharName.isEmpty()) {
            etCharacterName.setText(intentCharName);
            fetchCharacter(intentCharName);
        }

        btnSearch.setOnClickListener(v -> {
            String name = etCharacterName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this,
                        R.string.type_character_name,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            fetchCharacter(name);
        });
    }

    private void fetchCharacter(String name) {
        try {
            VolleySingleton.getInstance(this).addToRequestQueue(
                    new JsonObjectRequest(
                            Request.Method.GET,
                            ApiConstants.character(name),
                            null,
                            this::handleSuccess,
                            error -> {
                                Toast.makeText(this,
                                        R.string.character_not_found,
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Volley error", error);
                            }
                    )
            );

        } catch (Exception e) {
            Toast.makeText(this,
                    R.string.invalid_name,
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Encoding error", e);
        }
    }

    private void handleSuccess(JSONObject response) {
        try {
            JSONObject c = response
                    .getJSONObject("character")
                    .getJSONObject("character");

            tvCharName.setText(c.getString("name"));

            String title = c.optString("title", "");
            if (!title.isEmpty()) {
                tvCharTitle.setText(title);
                tvCharTitle.setVisibility(View.VISIBLE);
            } else {
                tvCharTitle.setVisibility(View.GONE);
            }

            tvCharMainInfo.setText(
                    getString(
                            R.string.char_main_info,
                            c.getString("vocation"),
                            c.getInt("level"),
                            c.getString("world")
                    )
            );

            tvAccountStatus.setText(c.getString("account_status"));

            tvAchievementPoints.setText(
                    getString(
                            R.string.achievement_points,
                            c.getInt("achievement_points")
                    )
            );

            tvCharDetails.setText(
                    getString(
                            R.string.char_details,
                            c.getString("sex"),
                            c.getString("residence"),
                            c.getString("last_login")
                    )
            );

            JSONArray formerNames = c.optJSONArray("former_names");
            if (formerNames != null && formerNames.length() > 0) {
                StringBuilder sb = new StringBuilder(
                        getString(R.string.former_names)
                ).append("\n");

                for (int i = 0; i < formerNames.length(); i++) {
                    sb.append("• ").append(formerNames.getString(i)).append("\n");
                }

                tvFormerNames.setText(sb.toString());
                tvFormerNames.setVisibility(View.VISIBLE);
            } else {
                tvFormerNames.setVisibility(View.GONE);
            }

            includeCard.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Toast.makeText(this,
                    R.string.error_character,
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Parse error", e);
        }
    }
}
