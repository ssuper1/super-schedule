package com.example.newbig.web;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.newbig.R;

public class GuideActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_activity);
        // 获取 WebView 组件
        webView = findViewById(R.id.webview_guide);
        // 启用 JavaScript 支持
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置 WebView 客户端
        // webView.setWebViewClient(new WebViewClient());
        // 加载 assets 目录中的 guide.html 文件
        webView.loadUrl("file:///android_asset/guide.html");
    }

}