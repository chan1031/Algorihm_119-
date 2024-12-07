package com.example.a119_saver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RouteActivity extends AppCompatActivity {
    private TextView routeTimeText;
    private Button detailRouteButton;
    private Button startNavigationButton;
    
    private boolean[] emergencyRoomChecks;
    private boolean[] equipmentChecks;
    private int goldenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        // Intent에서 데이터 받기
        emergencyRoomChecks = getIntent().getBooleanArrayExtra("emergencyRoomChecks");
        equipmentChecks = getIntent().getBooleanArrayExtra("equipmentChecks");
        goldenTime = getIntent().getIntExtra("goldenTime", 0);

        initializeViews();
        setupButtons();
        
        // MainActivity의 계산된 경로 시간을 표시
        updateRouteDisplay(30); // 예시값, 실제로는 MainActivity에서 전달받은 값 사용
    }

    private void initializeViews() {
        routeTimeText = findViewById(R.id.text_route_time);
        detailRouteButton = findViewById(R.id.button_detail_route);
        startNavigationButton = findViewById(R.id.button_start_navigation);
    }

    private void setupButtons() {
        detailRouteButton.setOnClickListener(v -> {
            // 상세 경로 보기 처리
        });

        startNavigationButton.setOnClickListener(v -> {
            // 내비게이션 시작 처리
        });
    }

    private void updateRouteDisplay(int routeTimeMinutes) {
        routeTimeText.setText(String.format("응급실까지 %d분", routeTimeMinutes));
    }
}
