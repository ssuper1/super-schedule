package com.example.newbig.web;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newbig.R;

public class WebActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "WebPrefs";
    private static final String KEY_URL = "url";
    private WebView webView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        webView = findViewById(R.id.webview);

        // 设置 WebViewClient 来处理网页导航
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 在 WebView 中打开链接
                view.loadUrl(url);
                return true;
            }
        });

        // 检查是否已有保存的网址
        String savedUrl = sharedPreferences.getString(KEY_URL, null);
        if (savedUrl == null) {
            // 如果没有保存的网址，弹出输入框
            showUrlInputDialog();
        } else {
            // 如果有保存的网址，直接加载网页
            loadUrl(savedUrl);
        }
    }

    private void showUrlInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入网址（以后长按按钮可清除）");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("确认", (dialog, which) -> {
            String url = input.getText().toString();
            if (!url.isEmpty()) {
                // 保存网址
                sharedPreferences.edit().putString(KEY_URL, url).apply();
                // 加载网页
                loadUrl(url);
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void loadUrl(String url) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}