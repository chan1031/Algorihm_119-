package com.example.a119_saver;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.kakao.vectormap.label.LabelTextBuilder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.LabelTextStyle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    String BASE_URL = "https://dapi.kakao.com/";
    String API_KEY = "KakaoAK 4fe14cc438d05a5f47a6ce7b0b85c638";
    double latitude = 37.5873;
    double longitude = 126.9930;

    private KakaoMap kakaoMap;
    private int startZoomLevel = 15;
    private LatLng startPosition = LatLng.from(37.5873, 126.9930); // 판교역

    private KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull KakaoMap map) {
            Toast.makeText(getApplicationContext(), "Map Start!", Toast.LENGTH_SHORT).show();
            Log.i("k3f", "startPosition: " + map.getCameraPosition().getPosition().toString());
            Log.i("k3f", "startZoomLevel: " + map.getZoomLevel());

            searchKeyword("응급실", 5000);
            kakaoMap = map;
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

    private void searchKeyword(String keyword, int radius) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoLocalApi api = retrofit.create(KakaoLocalApi.class);

        Call<SearchKeywordResult> call = api.searchKeyword(API_KEY, keyword, longitude, latitude, radius, 10);

        call.enqueue(new Callback<SearchKeywordResult>() {
            @Override
            public void onResponse(Call<SearchKeywordResult> call, Response<SearchKeywordResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SearchKeywordResult searchResult = response.body();

                    List<SearchKeywordResult.Place> filteredPlaces = new ArrayList<>();
                    // 검색 결과 출력
                    for (SearchKeywordResult.Place place : searchResult.getPlaces()) {
                        // 제외할 조건: "국물떡볶이" 또는 "소아응급센터" 포함
                        if (!place.getPlaceName().contains("국물떡볶이") &&
                                !place.getPlaceName().contains("소아응급센터")) {
                            filteredPlaces.add(place);
                        } else {
                            Log.d("FILTERED_OUT", "필터링된 장소: " + place.getPlaceName());
                        }
                        Log.d("API_RESPONSE", "Place Name: " + place.getPlaceName());
                        Log.d("API_RESPONSE", "Latitude: " + place.getY());
                        Log.d("API_RESPONSE", "Longitude: " + place.getX());
                    }

                    // 필터링된 장소만 라벨 추가
                    if (!filteredPlaces.isEmpty()) {
                        addMarkersWithLabels(filteredPlaces, kakaoMap);
                    } else {
                        Log.e("API_RESPONSE", "필터링 후 남은 검색 결과가 없습니다.");
                    }
                } else {
                    Log.e("API_RESPONSE", "응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchKeywordResult> call, Throwable t) {
                Log.e("TEST", "통신 실패: " + t.getMessage());
            }
        });
    }

    private void addMarkersWithLabels(List<SearchKeywordResult.Place> filteredPlaces, KakaoMap kakaoMap) {
        if (kakaoMap == null) {
            Log.e("MAP", "KakaoMap 객체가 초기화되지 않았습니다.");
            return;
        }

        if (filteredPlaces == null || filteredPlaces.isEmpty()) {
            Log.e("MAP", "필터링된 장소가 없습니다.");
            return;
        }

        LabelLayer labelLayer = kakaoMap.getLabelManager().getLayer();
        if (labelLayer == null) {
            Log.e("MAP", "LabelLayer를 가져오지 못했습니다.");
            return;
        }

        for (SearchKeywordResult.Place place : filteredPlaces) {
            LabelTextStyle textStyle = LabelTextStyle.from(24, Color.BLACK);
            LabelStyle textLabelStyle = LabelStyle.from().setTextStyles(textStyle);
            LabelStyles textLabelStyles = kakaoMap.getLabelManager()
                    .addLabelStyles(LabelStyles.from(textLabelStyle));

            LabelTextBuilder textBuilder = new LabelTextBuilder()
                    .setTexts(place.getPlaceName());
            LabelStyle iconLabelStyle = LabelStyle.from(R.drawable.map_icon);

            LabelOptions labelOptions = LabelOptions.from(LatLng.from(place.getY(), place.getX()))
                    .setStyles(kakaoMap.getLabelManager().addLabelStyles(LabelStyles.from(iconLabelStyle)))
                    .setTexts(textBuilder);

            Label textLabel = labelLayer.addLabel(labelOptions);

            if (textLabel == null) {
                Log.e("MAP", "텍스트 라벨 추가 실패: " + place.getPlaceName());
            } else {
                Log.d("MAP", "텍스트 라벨 추가 성공: " + place.getPlaceName());
            }
        }

        Log.d("MAP", "모든 라벨 추가 완료");
    }





}