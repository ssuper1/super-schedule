package com.example.newbig;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // 使用上面创建的开屏布局

        // 延时2秒后跳转到主界面
        new Handler().postDelayed(() -> {
            // 启动 StartActivity 或者你的目标 Activity
            Intent intent = new Intent(SplashActivity.this, StartActivity.class);
            startActivity(intent);
            finish();  // 结束 SplashActivity，防止用户按返回键回到开屏
        }, 100);  // 设置延时2秒（根据需要调整）
    }
}
