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

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private KakaoMap kakaoMap;
    private EmergencyMedicalAPI api;
    private static final String BASE_URL = "https://apis.data.go.kr/B552657/ErmctInfoInqireService/";
    private static final String API_KEY = "1hRYYyBuSV8rOBXAbBB9X2M9kEqZD35qoxI1YaYwIGPXAsX0Mecd/EcaEAB5MeITQvnc3XI1SpkDow+v4YCjrg==";

    private int startZoomLevel = 15;
    private LatLng startPosition = LatLng.from(37.5873, 126.9930); // 판교역

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        // Retrofit 초기화
        api = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(EmergencyMedicalAPI.class);

        mapView = findViewById(R.id.map_view);
        mapView.start(lifeCycleCallback, readyCallback);
    }

    private KakaoMapReadyCallback readyCallback = new KakaoMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull KakaoMap map) {
            Toast.makeText(getApplicationContext(), "Map Start!", Toast.LENGTH_SHORT).show();
            kakaoMap = map;
            // 지역 응급실 목록 조회 시작
            searchEmergencyList("서울특별시", "종로구");
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
    // 1. 지역 응급실 목록 조회
    private void searchEmergencyList(String city, String district) {
        Call<EmergencyListResponse> call = api.getEmergencyList(API_KEY, city, district, 1, 10,"XML");
        // URL 로깅 추가
        HttpUrl url = call.request().url();
        Log.d("API_URL", "Request URL: " + url.toString());
        call.enqueue(new Callback<EmergencyListResponse>() {
            @Override
            public void onResponse(Call<EmergencyListResponse> call, Response<EmergencyListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        EmergencyListResponse responseBody = response.body();
                        Log.d("API_RESPONSE", "Response: " + responseBody);

                        if (responseBody.getBody() != null &&
                                responseBody.getBody().getItems() != null) {
                            List<EmergencyListItem> hospitals = responseBody.getBody().getItems();

                            // 응답 결과 로그 출력
                            for(EmergencyListItem hospital : hospitals) {

                                Log.d("API_RESPONSE", "병원명: " + hospital.getHospitalName());
                                Log.d("API_RESPONSE", "응급실전화: " + hospital.getEmergencyTel());
                                Log.d("API_RESPONSE", "병원ID: " + hospital.getHospitalId());
                                Log.d("API_RESPONSE", "병상: " + hospital.getHvec());
                                Log.d("API_RESPONSE", "--------------------");

                                //DetailAPI 호출
                                searchDetailEmergency(hospital.getHospitalId());

                            }

                        } else {
                            Log.e("API_ERROR", "Body or Items is null");
                            handleApiError("데이터가 없습니다");
                        }
                    } else {
                        Log.e("API_ERROR", "Response not successful: " + response.code());
                        handleApiError("응급실 목록 조회 실패: " + response.code());
                    }
            }

            @Override
            public void onFailure(Call<EmergencyListResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
                handleApiError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    private void searchDetailEmergency(String Hpid) {
        Call<EmergencyDetailResponse> call = api.getEmergencyDetail(API_KEY, Hpid, 1, 10,"XML");
        // URL 로깅 추가
        HttpUrl url = call.request().url();
        Log.d("API_URL2", "Request URL: " + url.toString());
        call.enqueue(new Callback<EmergencyDetailResponse>() {
            @Override
            public void onResponse(Call<EmergencyDetailResponse> call, Response<EmergencyDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String rawXml = response.body().toString();  // XML 데이터를 문자열로 확인
                        Log.d("RAW_XML_SUCCESS", rawXml);
                    } catch (Exception e) {
                        Log.e("RAW_XML_ERROR", "Error reading response body", e);
                    }
                    EmergencyDetailResponse responseBody = response.body();
                    Log.d("API_RESPONSE2", "Response: " + responseBody);
                    if (responseBody.getBody() != null &&
                            responseBody.getBody().getItems() != null) {
                        List<EmergencyDetailItem> items = responseBody.getBody().getItems();

                        // 응답 결과 로그 출력
                        for(EmergencyDetailItem item : items) {
                            Log.d("Detail Response", item.toString());
                            Log.d("API_RESPONSE2", "병원명: " + item.getHospitalName());
                            Log.d("API_RESPONSE2", "병원ID: " + item.getHospitalId());
                            Log.d("API_RESPONSE2", "x: " + item.getLatitude());
                            Log.d("API_RESPONSE2", "y: " + item.getLongitude());
                            Log.d("API_RESPONSE2", "--------------------");

                            addMarkerToMap(item);
                        }

                    } else {
                        Log.e("API_ERROR2", "Body or Items is null");
                        handleApiError("데이터가 없습니다");
                        try {

                            String rawResponse = response.errorBody() != null ? response.errorBody().string() : "No error body";
                            Log.e("RAW_XML", rawResponse);
                        } catch (Exception e) {
                            Log.e("RAW_XML_ERROR", "Failed to read raw XML", e);
                        }
                    }
                } else {
                    Log.e("API_ERROR2", "Response not successful: " + response.code());
                    handleApiError("응급실 목록 조회 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<EmergencyDetailResponse> call, Throwable t) {
                Log.e("API_ERROR", "Network error", t);
                handleApiError("네트워크 오류: " + t.getMessage());
            }
        });
    }

    private void addMarkerToMap(EmergencyDetailItem item) {
        if (kakaoMap == null) return;

        try {
            LabelLayer labelLayer = kakaoMap.getLabelManager().getLayer();

            // 위도/경도로 마커 위치 설정
            LatLng position = LatLng.from(
                    Double.parseDouble(item.getLatitude()),
                   Double.parseDouble(item.getLongitude())
            );

            // 라벨 스타일 설정
            LabelTextStyle textStyle = LabelTextStyle.from(24, Color.BLACK);
            LabelStyle textLabelStyle = LabelStyle.from().setTextStyles(textStyle);
            LabelStyles textLabelStyles = kakaoMap.getLabelManager()
                    .addLabelStyles(LabelStyles.from(textLabelStyle));

            // 병원 이름 텍스트 설정
            LabelTextBuilder textBuilder = new LabelTextBuilder()
                    .setTexts(item.getHospitalName());

            // 마커 아이콘 스타일 설정
            LabelStyle iconLabelStyle = LabelStyle.from(R.drawable.map_icon);

            // 라벨 옵션 설정 및 추가
            LabelOptions labelOptions = LabelOptions.from(position)
                    .setStyles(kakaoMap.getLabelManager().addLabelStyles(LabelStyles.from(iconLabelStyle)))
                    .setTexts(textBuilder);

            Label textLabel = labelLayer.addLabel(labelOptions);

            if (textLabel == null) {
                Log.e("MAP", "마커 추가 실패: " + item.getHospitalName());
            } else {
                Log.d("MAP", "마커 추가 성공: " + item.getHospitalName());
            }
        } catch (NumberFormatException e) {
            Log.e("MAP", "좌표 변환 실패: " + item.getHospitalName(), e);
        }
    }

    private void handleApiError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e("API", message);
    }
}