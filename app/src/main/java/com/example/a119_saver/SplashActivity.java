package com.example.a119_saver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.os.Looper;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 스플래시 화면 유지 시간
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 메인 액티비티로 전환
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // 스플래시 액티비티 종료
        }, 1000); // 2초 후 전환
    }
}