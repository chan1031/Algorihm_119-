package com.example.a119_saver;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;

    private int startZoomLevel = 15;
    private LatLng startPosition = LatLng.from(37.5873, 126.9930); // 판교역

    private KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull KakaoMap kakaoMap) {
            Toast.makeText(getApplicationContext(), "Map Start!", Toast.LENGTH_SHORT).show();


            Log.i("k3f", "startPosition: " + kakaoMap.getCameraPosition().getPosition().toString());
            Log.i("k3f", "startZoomLevel: " + kakaoMap.getZoomLevel());
        }

        @NonNull
        @Override
        public LatLng getPosition() {
            return startPosition;
        }

        @Override
        public int getZoomLevel() {
            return startZoomLevel;
        }
    };

    private MapLifeCycleCallback lifeCycleCallback = new MapLifeCycleCallback() {
        @Override
        public void onMapDestroy() {
            Toast.makeText(getApplicationContext(), "onMapDestroy", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapError(Exception error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        mapView = findViewById(R.id.map_view);
        mapView.start(lifeCycleCallback, readyCallback);
    }
}
