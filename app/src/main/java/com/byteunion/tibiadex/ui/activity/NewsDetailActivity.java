package com.byteunion.tibiadex.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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

public class NewsDetailActivity extends AppCompatActivity {

    private ProgressBar progressLoading;
    private TextView tvTitle, tvDate, tvCategory;
    private WebView webView;
    private Button btnOpenBrowser;
    private String newsUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        progressLoading = findViewById(R.id.progressLoading);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvCategory = findViewById(R.id.tvCategory);
        webView = findViewById(R.id.webView);
        btnOpenBrowser = findViewById(R.id.btnOpenBrowser);

        int newsId = getIntent().getIntExtra("news_id", 0);

        if (newsId > 0) {
            fetchNewsDetails(newsId);
        } else {
            Toast.makeText(this, "Erro: notícia não especificada", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnOpenBrowser.setOnClickListener(v -> {
            if (newsUrl != null && !newsUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl));
                startActivity(browserIntent);
            }
        });
    }

    private void fetchNewsDetails(int newsId) {
        progressLoading.setVisibility(View.VISIBLE);

        VolleySingleton.getInstance(this).addToRequestQueue(
            new JsonObjectRequest(Request.Method.GET, 
                    ApiConstants.newsById(newsId), null,
                response -> {
                    try {
                        JSONObject news = response.getJSONObject("news");

                        String content = news.optString("content", "");
                        String contentHtml = news.optString("content_html", content);
                        
                        tvTitle.setText(extractTitle(content));
                        tvDate.setText(formatDate(news.getString("date")));
                        
                        String category = news.getString("category");
                        String type = news.getString("type");
                        tvCategory.setText(getCategoryEmoji(category) + " " + getCategoryName(category) + 
                                         " • " + (type.equals("news") ? "📰 News" : "📌 Ticker"));
                        
                        newsUrl = news.getString("url");

                        // Configurar WebView
                        webView.getSettings().setJavaScriptEnabled(true);
                        webView.setWebViewClient(new WebViewClient());
                        
                        String htmlContent = "<html><head><style>" +
                            "body { background-color: #1B140F; color: #C8B798; font-family: sans-serif; padding: 16px; line-height: 1.6; }" +
                            "a { color: #D4AF37; text-decoration: none; }" +
                            "p { margin-bottom: 12px; }" +
                            "</style></head><body>" + contentHtml + "</body></html>";
                        
                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

                        progressLoading.setVisibility(View.GONE);

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar notícia: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    private String extractTitle(String content) {
        if (content.length() > 60) {
            return content.substring(0, 60) + "...";
        }
        return content;
    }

    private String formatDate(String isoDate) {
        try {
            String[] parts = isoDate.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return isoDate;
        }
    }

    private String getCategoryEmoji(String category) {
        switch (category.toLowerCase()) {
            case "development": return "🔧";
            case "technical": return "⚙️";
            case "community": return "👥";
            default: return "📰";
        }
    }

    private String getCategoryName(String category) {
        switch (category.toLowerCase()) {
            case "development": return "Desenvolvimento";
            case "technical": return "Técnico";
            case "community": return "Comunidade";
            default: return category;
        }
    }
}
