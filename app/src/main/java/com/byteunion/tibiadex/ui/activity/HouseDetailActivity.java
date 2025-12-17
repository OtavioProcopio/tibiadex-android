package com.byteunion.tibiadex.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.byteunion.tibiadex.R;
import com.byteunion.tibiadex.network.ApiConstants;
import com.byteunion.tibiadex.network.VolleySingleton;

import org.json.JSONObject;

public class HouseDetailActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private ImageView imgHouse;
    private TextView tvName, tvType, tvTown, tvWorld;
    private TextView tvSize, tvBeds, tvRent;
    private TextView tvStatus, tvOwner, tvPaidUntil;
    private TextView tvAuctionInfo, tvCurrentBid, tvAuctionEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        imgHouse = findViewById(R.id.imgHouse);
        tvName = findViewById(R.id.tvName);
        tvType = findViewById(R.id.tvType);
        tvTown = findViewById(R.id.tvTown);
        tvWorld = findViewById(R.id.tvWorld);
        tvSize = findViewById(R.id.tvSize);
        tvBeds = findViewById(R.id.tvBeds);
        tvRent = findViewById(R.id.tvRent);
        tvStatus = findViewById(R.id.tvStatus);
        tvOwner = findViewById(R.id.tvOwner);
        tvPaidUntil = findViewById(R.id.tvPaidUntil);
        tvAuctionInfo = findViewById(R.id.tvAuctionInfo);
        tvCurrentBid = findViewById(R.id.tvCurrentBid);
        tvAuctionEnd = findViewById(R.id.tvAuctionEnd);

        String world = getIntent().getStringExtra("world");
        int houseId = getIntent().getIntExtra("house_id", 0);
        String houseName = getIntent().getStringExtra("house_name");

        if (world != null && houseId > 0) {
            tvName.setText(houseName);
            fetchHouseDetails(world, houseId);
        } else {
            Toast.makeText(this, "Erro: casa não especificada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchHouseDetails(String world, int houseId) {
        progressLoading.setVisibility(View.VISIBLE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.house(world, houseId), null,
                response -> {
                    try {
                        JSONObject house = response.getJSONObject("house");

                        tvName.setText(house.getString("name"));
                        tvWorld.setText("🌍 " + house.getString("world"));
                        tvTown.setText("🏘️ " + house.getString("town"));
                        tvType.setText("🏠 " + (house.getString("type").equals("house") ? "Casa" : "Guildhall"));
                        
                        tvSize.setText("📐 Tamanho: " + house.getInt("size") + " sqm");
                        tvBeds.setText("🛏️ Camas: " + house.getInt("beds"));
                        tvRent.setText("💰 Aluguel: " + formatGold(house.getInt("rent")) + " gold");

                        // Imagem
                        String imgUrl = house.getString("img");
                        if (!imgUrl.isEmpty()) {
                            Picasso.get()
                                    .load(imgUrl)
                                    .placeholder(R.color.tibia_surface)
                                    .error(R.color.tibia_surface)
                                    .into(imgHouse);
                        } else {
                            imgHouse.setVisibility(View.GONE);
                        }

                        // Status
                        JSONObject status = house.getJSONObject("status");
                        boolean isRented = status.getBoolean("is_rented");
                        boolean isAuctioned = status.getBoolean("is_auctioned");

                        if (isRented) {
                            tvStatus.setText("✅ Status: Alugada");
                            
                            JSONObject rental = status.getJSONObject("rental");
                            String owner = rental.getString("owner");
                            String paidUntil = rental.getString("paid_until");
                            
                            tvOwner.setVisibility(View.VISIBLE);
                            tvOwner.setText("👤 Proprietário: " + owner);
                            
                            tvPaidUntil.setVisibility(View.VISIBLE);
                            tvPaidUntil.setText("📅 Pago até: " + formatDate(paidUntil));
                            
                            tvAuctionInfo.setVisibility(View.GONE);
                            tvCurrentBid.setVisibility(View.GONE);
                            tvAuctionEnd.setVisibility(View.GONE);
                            
                        } else if (isAuctioned) {
                            tvStatus.setText("🔨 Status: Em Leilão");
                            
                            JSONObject auction = status.getJSONObject("auction");
                            int currentBid = auction.getInt("current_bid");
                            String currentBidder = auction.getString("current_bidder");
                            String auctionEnd = auction.getString("auction_end");
                            
                            tvOwner.setVisibility(View.GONE);
                            tvPaidUntil.setVisibility(View.GONE);
                            
                            tvAuctionInfo.setVisibility(View.VISIBLE);
                            if (!currentBidder.isEmpty()) {
                                tvAuctionInfo.setText("👤 Maior lance: " + currentBidder);
                            } else {
                                tvAuctionInfo.setText("Sem lances ainda");
                            }
                            
                            tvCurrentBid.setVisibility(View.VISIBLE);
                            tvCurrentBid.setText("💰 Lance atual: " + formatGold(currentBid) + " gold");
                            
                            tvAuctionEnd.setVisibility(View.VISIBLE);
                            tvAuctionEnd.setText("⏰ Termina em: " + formatDate(auctionEnd));
                            
                        } else {
                            tvStatus.setText("🆓 Status: Disponível");
                            tvOwner.setVisibility(View.GONE);
                            tvPaidUntil.setVisibility(View.GONE);
                            tvAuctionInfo.setVisibility(View.GONE);
                            tvCurrentBid.setVisibility(View.GONE);
                            tvAuctionEnd.setVisibility(View.GONE);
                        }

                        progressLoading.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private String formatGold(int amount) {
        if (amount >= 1000000) {
            return String.format("%.1fM", amount / 1000000.0);
        } else if (amount >= 1000) {
            return String.format("%.1fk", amount / 1000.0);
        }
        return String.valueOf(amount);
    }

    private String formatDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "N/A";
        
        try {
            // Simplificar formato: 2025-12-22T07:05:32Z -> 22/12/2025
            String[] parts = isoDate.split("T")[0].split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return isoDate;
        }
    }
}
