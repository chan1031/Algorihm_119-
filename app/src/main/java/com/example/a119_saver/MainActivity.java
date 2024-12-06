    package com.example.a119_saver;

    import android.graphics.Color;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Toast;
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
    import com.kakao.vectormap.label.LabelTextBuilder;
    import com.kakao.vectormap.label.LabelTextStyle;
    import com.kakao.vectormap.route.RouteLine;
    import com.kakao.vectormap.route.RouteLineLayer;
    import com.kakao.vectormap.route.RouteLineOptions;
    import com.kakao.vectormap.route.RouteLineSegment;
    import com.kakao.vectormap.route.RouteLineStyle;
    import com.kakao.vectormap.route.RouteLineStyles;
    import com.kakao.vectormap.route.RouteLineStylesSet;

    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.Collections;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.stream.Collectors;

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
        //Emergency API 키
        private static final String BASE_URL = "https://apis.data.go.kr/B552657/ErmctInfoInqireService/";
        private static final String API_KEY = "1hRYYyBuSV8rOBXAbBB9X2M9kEqZD35qoxI1YaYwIGPXAsX0Mecd/EcaEAB5MeITQvnc3XI1SpkDow+v4YCjrg==";
        private AtomicInteger pendingCoordinates;
        private int startZoomLevel = 15;
        private LatLng startPosition = LatLng.from(37.5873, 126.9930);
        private static final String TAG = "TEST";
        //카카오 네비게이션
        private KakaoNavigation kakaoNavigation;
        //hospital class
        public class Hospital {
            String hospital_name;
            String hpid;
            String bed_num;
            String x;
            String y;

            //생성자
            public Hospital(String hospital_name, String hpid, String bed_num) {
                this.hospital_name = hospital_name;
                this.hpid = hpid;
                this.bed_num = bed_num;
            }

            public void setCoordinates(String x, String y) {
                this.x = x;
                this.y = y;
            }
        }

        //병원 클래스 배열
        ArrayList<Hospital> global_hospitals = new ArrayList<>();

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.mapview);

            kakaoNavigation = new KakaoNavigation();
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
                                pendingCoordinates = new AtomicInteger(hospitals.size());

                                for(EmergencyListItem hospital : hospitals) {

                                    global_hospitals.add(new Hospital(hospital.getHospitalName(),
                                            hospital.getHospitalId(),
                                            hospital.getHvec()));

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

        private List<EmergencyDetailItem> allHospitals = new ArrayList<>();
        //병원들의 좌표를 가지고 오는 함수
        private void searchDetailEmergency(String Hpid) {
            Call<EmergencyDetailResponse> call = api.getEmergencyDetail(API_KEY, Hpid, 1, 10,"XML");
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

                                for(Hospital h : global_hospitals) {
                                    if(h.hpid.equals(item.getHospitalId())) {
                                        h.setCoordinates(item.getLatitude(), item.getLongitude());
                                        break;
                                    }
                                }
                                //지도에 표시
                                addMarkerToMap(item);
                                //경로 출력
                                if (pendingCoordinates.decrementAndGet() == 0) {
                                    calculateAllHospitalRoutes(global_hospitals);
                                }
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

        // 마커 정보 업데이트 메서드 (기존 addMarkerToMap 메서드 수정)
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

        // EmergencyDetailItem을 받아서 경로 계산하는 메서드
        private void calculateAllHospitalRoutes(List<Hospital> hospitals) {
            if (kakaoNavigation == null) {
                Log.e("MainActivity", "kakaoNavigation is null");
                return;
            }
            if (hospitals == null || hospitals.isEmpty()) {
                Log.e("MainActivity", "hospitals list is null or empty");
                return;
            }

            kakaoNavigation.calculateAllRoutes(hospitals, new KakaoNavigation.AllRoutesCallback() {
                @Override
                public void onSuccess(List<KakaoNavigation.RouteInfo> routes, KakaoNavigation.Result bestPath) {
                    if(bestPath != null){
                        // 최선 경로 처리
                        String pathString = String.join(" -> ", bestPath.path);
                        Log.d("bestPath", "Best Path: " + pathString);
                        Log.d("bestPath", String.format("Total Time: %d분 %d초",
                                bestPath.totalTime / 60, bestPath.totalTime % 60));
                    }

                    // 최선 경로의 모든 vertex 가져오기
                    List<KakaoNavigation.Vertex> bestVertices = kakaoNavigation.getBestPathVertices(bestPath);
                    Log.d("bestPath", "Retrieved all navigation vertices for the best path");
                    drawRouteOnMap(bestVertices);
                    // vertex들의 위도, 경도 출력
                    Log.d("bestPath", "Navigation Route Vertices:");
                    for (int i = 0; i < bestVertices.size(); i++) {
                        KakaoNavigation.Vertex vertex = bestVertices.get(i);
                        Log.d("bestPath", String.format("Vertex %d: (lat: %f, lon: %f)",
                                i + 1, vertex.lat, vertex.lon));
                    }

                    // 모든 결과를 한 번에 출력하기 위해 StringBuilder 사용
                    StringBuilder logBuilder = new StringBuilder();
                    logBuilder.append("=== 병원별 경로 계산 결과 ===\n");

                    // 현재 위치에서 각 병원까지의 경로 출력
                    logBuilder.append("\n■ 현재 위치에서 출발하는 경로:\n");
                    for (KakaoNavigation.RouteInfo route : routes) {
                        if (route.fromName.equals("현재 위치")) {
                            logBuilder.append(String.format("  ▶ %s까지: 거리 %.1fkm, 예상 소요시간 %d분\n",
                                    route.toName,
                                    route.distance / 1000.0,
                                    route.duration / 60));
                        }
                    }

                    // 모든 병원 이름 수집 (현재 위치 제외)
                    Set<String> hospitalNames = new HashSet<>();
                    for (KakaoNavigation.RouteInfo route : routes) {
                        if (!route.fromName.equals("현재 위치")) {
                            hospitalNames.add(route.fromName);
                        }
                    }

                    // 각 병원별로 다른 병원까지의 경로 정보 출력
                    for (String fromHospital : hospitalNames) {
                        logBuilder.append(String.format("\n■ %s에서 출발하는 경로:\n", fromHospital));
                        for (KakaoNavigation.RouteInfo route : routes) {
                            if (route.fromName.equals(fromHospital) && !route.toName.equals("현재 위치")) {
                                logBuilder.append(String.format("  ▶ %s까지: 거리 %.1fkm, 예상 소요시간 %d분\n",
                                        route.toName,
                                        route.distance / 1000.0,
                                        route.duration / 60));
                            }
                        }
                    }

                    logBuilder.append("\n========================");
                    // 전체 로그를 한 번에 출력
                    Log.d("Routes", logBuilder.toString());

                    printRouteVertices();
                }

                @Override
                public void onError(String message) {
                    Log.e("Routes", "Error calculating routes: " + message);
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this,
                                    "경로 계산 중 오류 발생: " + message,
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            });
        }

        private void handleApiError(String message) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Log.e("API", message);
        }

        public void printRouteVertices() {
            List<KakaoNavigation.Vertex> vertices = kakaoNavigation.getRouteVertices("현재 위치", "서울대학교병원");

            if (vertices != null) {
                Log.d(TAG, "===== 현재 위치 -> 서울대학교병원 경로 정보 =====");
                Log.d(TAG, "총 경로 포인트 수: " + vertices.size());

                int pointCount = 0;
                for (KakaoNavigation.Vertex vertex : vertices) {
                    Log.d(TAG, String.format("Point %d - 위도: %f, 경도: %f",
                            ++pointCount, vertex.lat, vertex.lon));
                }
                Log.d(TAG, "=======================================");
            } else {
                Log.e(TAG, "경로 정보를 찾을 수 없습니다.");
            }
        }

        private void drawRouteOnMap(List<KakaoNavigation.Vertex> vertices) {
            if (kakaoMap == null || vertices == null || vertices.isEmpty()) {
                Log.e("MAP", "지도 또는 경로 데이터가 없습니다.");
                return;
            }

            try {
                // 1. RouteLineLayer 가져오기
                RouteLineLayer layer = kakaoMap.getRouteLineManager().getLayer();

                // 2. RouteLineStylesSet 생성하기 (파란색 경로 스타일)
                RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
                        RouteLineStyles.from(RouteLineStyle.from(16, Color.BLUE)));


                // 3. Vertex 리스트를 LatLng 리스트로 변환
                List<LatLng> routeCoords = vertices.stream()
                        .map(vertex -> LatLng.from(vertex.lat, vertex.lon))
                        .collect(Collectors.toList());

                // 4. RouteLineSegment 생성 및 스타일 설정
                RouteLineSegment segment = RouteLineSegment.from(routeCoords)
                        .setStyles(stylesSet.getStyles("navigationRoute"));

                // 5. RouteLineOptions 생성
                RouteLineOptions options = RouteLineOptions.from(segment)
                        .setStylesSet(stylesSet);

                // 6. RouteLine 생성 및 추가
                RouteLine routeLine = layer.addRouteLine(options);

                Log.d("MAP", "경로를 성공적으로 그렸습니다. 포인트 수: " + vertices.size());

            } catch (Exception e) {
                Log.e("MAP", "경로 그리기 실패: " + e.getMessage(), e);
            }
        }
    }