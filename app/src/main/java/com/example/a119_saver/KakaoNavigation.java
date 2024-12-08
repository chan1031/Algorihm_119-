package com.example.a119_saver;

import android.util.Log;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class KakaoNavigation {
    private static final String TAG = "KakaoNavigation";
    private static final String TAG2 = "branchandbound";
    private static final String BASE_URL = "https://apis-navi.kakaomobility.com/";
    private static final String API_KEY = "cb49ad3e6bf961d966a056458a106ea9";
    private final NaviApi naviApi;
    private final double CURRENT_LAT = 37.5873;
    private final double CURRENT_LON = 126.9930;

    public class Result {
        List<String> path;
        int totalTime;
        double totalBound;

        public Result(List<String> path, int totalTime, double totalBound) {
            this.path = new ArrayList<>(path);
            this.totalTime = totalTime;
            this.totalBound = totalBound;
        }
    }

    // bound 계산을 위한 새로운 메서드
    private double calculateBound(String nodeName, int time, int branchLevel) {
        int bedNum = graph.getNodes().get(nodeName).getHvec();

        // 시작점("현재 위치")의 경우 병상수 계산에서 제외
        if (nodeName.equals("현재 위치")) {
            Log.d(TAG2, "시작점의 bound 계산: 0.0");
            return 0.0;
        }

        // 병상 수가 0 이하인 경우 0.1로 조정
        if (bedNum <= 0) {
            Log.d(TAG2, String.format("병상 수(%d)가 0 이하여서 0.1로 조정", bedNum));
            bedNum = 1;  // 1/0.1 = 10으로 설정하기 위해 1로 설정
        }

        double timeFactor = 0.6 * time;
        double bedFactor = 0.4 * (1.0/bedNum);
        double branchFactor = Math.pow(0.1, branchLevel);
        double bound = (timeFactor + bedFactor) * branchFactor;

        Log.d(TAG2, String.format("Bound 계산 - 노드: %s", nodeName));
        Log.d(TAG2, String.format("  시간 요소(0.6 * %d): %.2f", time, timeFactor));
        Log.d(TAG2, String.format("  병상 요소(0.4 * 1/%d): %.2f", bedNum, bedFactor));
        Log.d(TAG2, String.format("  분기 레벨(%d)의 가중치: %.5f", branchLevel, branchFactor));
        Log.d(TAG2, String.format("  최종 bound 값: %.5f", bound));

        return bound;
    }

    private Result bestResult;
    //이 변수는 사용자로부터 입력받아야 하므로 MainActivity.java에서 가져와야할듯
    private int goldenTime;
    public void setGoldenTime(int goldenTime) {
        this.goldenTime = goldenTime;
        Log.d("KakaoNavigation_setGoldenTime", "GoldenTime set to: " + goldenTime);
    }
    //Graph
    private Graph graph;

    public Graph getGraph() {
        return graph;
    }

    private List<Vertex> best_vertex = new ArrayList<>();

    //vertex 조회 메서드
    public List<Vertex> getRouteVertices(String from, String to) {
        if (graph == null) return null;
        return graph.getVertexPath(from, to);
    }

    public List<Vertex> getBestPathVertices(Result bestResult) {
        best_vertex.clear();  // 기존 데이터 초기화

        // bestResult의 경로를 순회하면서 연속된 두 지점 간의 vertex들을 모두 저장
        for (int i = 0; i < bestResult.path.size() - 1; i++) {
            String from = bestResult.path.get(i);
            String to = bestResult.path.get(i + 1);

            List<Vertex> vertices = getRouteVertices(from, to);
            if (vertices != null) {
                best_vertex.addAll(vertices);
                Log.d(TAG, String.format("Adding navigation route from %s to %s", from, to));
                Log.d(TAG, String.format("Added %d vertices", vertices.size()));
            }
        }

        Log.d(TAG, String.format("Total vertices in best path: %d", best_vertex.size()));
        return best_vertex;
    }



    public void printGraphState() {
        // 노드별 정보 출력
        for (String nodeName : graph.getNodes().keySet()) {
            Log.d(TAG, "====== " + nodeName + " ======");

            // hvec 정보 출력 (현재 위치가 아닌 경우만)
            Graph.Node currentNode = graph.getNodes().get(nodeName);
            if (!nodeName.equals("현재 위치") && currentNode.getHvec() != null) {
                Log.d(TAG, "병상 수: " + currentNode.getHvec());
            }

            // 인접 노드와 가중치(시간) 정보 출력
            Map<String, Integer> edges = graph.getAdjacencyMap().get(nodeName);
            Log.d(TAG, "--- 인접 노드까지의 소요 시간 ---");
            for (Map.Entry<String, Integer> edge : edges.entrySet()) {
                String destination = edge.getKey();
                int duration = edge.getValue();
                Log.d(TAG, String.format("-> %s : %d분", destination, duration/60));
            }
            Log.d(TAG, "------------------------");
        }
    }

    public KakaoNavigation() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "KakaoAK " + API_KEY)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        naviApi = retrofit.create(NaviApi.class);
    }

    // API 인터페이스 수정
    public interface NaviApi {
        @GET("v1/directions")
        Call<DirectionsResponse> getDirections(
                @Query("origin") String origin,          // 변경
                @Query("destination") String destination, // 변경
                @Query("priority") String priority
        );
    }

    // 노드 간 경로 정보를 저장할 클래스
    public static class RouteInfo {
        public final String fromName;
        public final String toName;
        public final int distance;  // 미터
        public final int duration;  // 초
        public final List<Vertex> vertices;  // 경로의 모든 좌표점 추가

        public RouteInfo(String fromName, String toName, int distance, int duration, List<Vertex> vertices) {
            this.fromName = fromName;
            this.toName = toName;
            this.distance = distance;
            this.duration = duration;
            this.vertices = vertices;
        }
    }

    // 경로의 각 좌표점을 저장할 클래스
    public static class Vertex {
        public final double lat;
        public final double lon;

        public Vertex(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    // 모든 노드 간 경로 계산을 위한 콜백
    public interface AllRoutesCallback {
        void onSuccess(List<RouteInfo> routes,Result bestPath);
        void onError(String message);
    }

    public void calculateAllRoutes(List<MainActivity.Hospital> hospitals, AllRoutesCallback callback) {
        graph = new Graph();
        List<RouteInfo> allRoutes = new ArrayList<>();
        // 현재 위치 노드 추가 (hvec 없음)
        graph.addNode("현재 위치" ,CURRENT_LAT, CURRENT_LON,99999);

        for (MainActivity.Hospital hospital : hospitals) {
            graph.addNode(
                    hospital.hospital_name,
                    Double.parseDouble(hospital.x),
                    Double.parseDouble(hospital.y),
                    Integer.parseInt(hospital.bed_num)
            );
        }

        List<Point> allPoints = new ArrayList<>();
        allPoints.add(new Point("현재 위치", CURRENT_LAT, CURRENT_LON));
        for (MainActivity.Hospital hospital : hospitals) {
            allPoints.add(new Point(hospital.hospital_name,
                    Double.parseDouble(hospital.x),
                    Double.parseDouble(hospital.y)));
        }

        // API 요청 카운터 추가
        AtomicInteger totalRequests = new AtomicInteger(0);
        AtomicInteger completedRequests = new AtomicInteger(0);

        // 총 요청 수 계산
        for (int i = 0; i < allPoints.size(); i++) {
            for (int j = i + 1; j < allPoints.size(); j++) {
                totalRequests.incrementAndGet();
            }
        }

        // 모든 노드 쌍에 대해 시간 정보 요청
        for (int i = 0; i < allPoints.size(); i++) {
            for (int j = i + 1; j < allPoints.size(); j++) {
                Point origin = allPoints.get(i);
                Point destination = allPoints.get(j);

                Log.d(TAG, String.format("Requesting route from %s(%f,%f) to %s(%f,%f)",
                        origin.name, origin.lat, origin.lon,
                        destination.name, destination.lat, destination.lon));
                naviApi.getDirections(
                        origin.lon + "," + origin.lat,
                        destination.lon + "," + destination.lat,
                        "RECOMMEND"
                ).enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            DirectionsResponse data = response.body();
                            if (data.routes != null && !data.routes.isEmpty()) {
                                Route route = data.routes.get(0);

                                // vertex 정보 추출
                                List<Vertex> vertices = new ArrayList<>();
                                Log.d(TAG, String.format("===== Route Vertices from %s to %s =====",
                                        origin.name, destination.name));

                                if (route.sections != null && !route.sections.isEmpty()) {
                                    Route.Section section = route.sections.get(0);
                                    if (section.roads != null) {
                                        int roadCount = 0;
                                        for (Route.Road road : section.roads) {
                                            Log.d(TAG, String.format("--- Road %d ---", ++roadCount));
                                            if (road.vertexes != null) {
                                                // vertex는 [lon1, lat1, lon2, lat2, ...] 형식으로 제공됨
                                                for (int i = 0; i < road.vertexes.length; i += 2) {
                                                    double lat = road.vertexes[i+1];
                                                    double lon = road.vertexes[i];
                                                    vertices.add(new Vertex(lat, lon));
                                                    Log.d(TAG, String.format("Vertex: (lat: %f, lon: %f)", lat, lon));
                                                }
                                            }
                                        }
                                    }
                                }
                                Log.d(TAG, String.format("Total vertices in route: %d", vertices.size()));
                                Log.d(TAG, "=====================================");

                                RouteInfo routeInfo = new RouteInfo(
                                        origin.name,
                                        destination.name,
                                        route.summary.distance,
                                        route.summary.duration,
                                        vertices
                                );

                                // 그래프에 경로와 vertex 정보 추가
                                graph.addEdge(origin.name, destination.name, route.summary.duration);
                                graph.addVertexPath(origin.name, destination.name, vertices);

                                Log.d(TAG, String.format("Added route: %s -> %s with %d vertices",
                                        origin.name, destination.name, vertices.size()));

                                // 모든 요청이 완료되었는지 확인
                                if (completedRequests.incrementAndGet() == totalRequests.get()) {
                                    Log.d(TAG, "All routes calculated. Calling callback...");
                                    printGraphState();
                                    //최선의 경로
                                    Result result = branch_and_bound(goldenTime);
                                    if (result != null) {
                                        Log.d(TAG2, "최선의 경로:");
                                        StringBuilder pathStr = new StringBuilder();
                                        for (int i = 0; i < result.path.size(); i++) {
                                            pathStr.append(result.path.get(i));
                                            if (i < result.path.size() - 1) {
                                                pathStr.append(" -> ");
                                            }
                                        }
                                        Log.d(TAG2, pathStr.toString());
                                        Log.d(TAG2, String.format("총 소요 시간: %d분 %d초",
                                                result.totalTime / 60, result.totalTime % 60));
                                    } else {
                                        Log.d(TAG2, "가능한 경로를 찾을 수 없습니다.");
                                    }
                                    callback.onSuccess(allRoutes, result);
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        if (completedRequests.incrementAndGet() == totalRequests.get()) {
                            printGraphState();
                        }
                    }
                });
            }
        }
    }

    // Request/Response 클래스들
    private static class DirectionsRequest {
        @SerializedName("origin")
        final Coordinates origin;
        @SerializedName("destination")
        final Coordinates destination;
        @SerializedName("priority")
        final String priority = "RECOMMEND";

        DirectionsRequest(double fromLat, double fromLon, double toLat, double toLon) {
            this.origin = new Coordinates(fromLat, fromLon);
            this.destination = new Coordinates(toLat, toLon);
        }
    }
    private static class DirectionsResponse {
        @SerializedName("routes")
        List<Route> routes;
    }

    private static class Coordinates {
        @SerializedName("x")
        final double x;
        @SerializedName("y")
        final double y;

        Coordinates(double lat, double lon) {
            this.x = lon;  // 경도
            this.y = lat;  // 위도
        }
    }

    private static class Route {
        @SerializedName("summary")
        Summary summary;

        @SerializedName("sections")
        List<Section> sections;

        static class Section {
            @SerializedName("roads")
            List<Road> roads;
        }

        static class Road {
            @SerializedName("vertexes")
            double[] vertexes;  // [lon1, lat1, lon2, lat2, ...]
        }
    }



    private static class Summary {
        @SerializedName("distance")
        int distance;
        @SerializedName("duration")
        int duration;
    }

    private static class Point {
        final String name;
        final double lat;
        final double lon;

        Point(String name, double lat, double lon) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    public Result branch_and_bound(int golden_time) {
        if (graph == null) return null;

        this.goldenTime = golden_time;
        this.bestResult = null;

        List<String> currentPath = new ArrayList<>();
        currentPath.add("현재 위치");

        Set<String> visited = new HashSet<>();
        visited.add("현재 위치");

        // 첫 번째 완전한 경로를 찾기 위한 초기 실행
        branch_and_bound_recursive(currentPath, visited, 0, 0.0, 1, true);

        if (bestResult != null) {
            Log.d(TAG2, "Initial complete path found!");
            Log.d(TAG2, String.format("Initial lower bound: %.5f", bestResult.totalBound));

            // 이제 실제 branch and bound 탐색 시작
            currentPath.clear();
            visited.clear();
            currentPath.add("현재 위치");
            visited.add("현재 위치");
            branch_and_bound_recursive(currentPath, visited, 0, 0.0, 1, false);
        }

        return bestResult;
    }

    //branch and bound 적용 함수
    private void branch_and_bound_recursive(
            List<String> currentPath,
            Set<String> visited,
            int currentTime,
            double currentBound,
            int branchLevel,
            boolean isInitialSearch
    ) {
        // 현재 경로 정보 출력
        StringBuilder pathStr = new StringBuilder();
        for (String node : currentPath) {
            pathStr.append(node).append(" -> ");
        }
        Log.d(TAG2, String.format("\n현재 탐색 정보:"));
        Log.d(TAG2, String.format("경로: %s", pathStr.toString()));
        Log.d(TAG2, String.format("누적 시간: %d분", currentTime));
        Log.d(TAG2, String.format("누적 bound: %.5f", currentBound));
        Log.d(TAG2, String.format("현재 분기 레벨: %d", branchLevel));

        // 모든 노드를 방문한 경우
        if (visited.size() == graph.getNodes().size()) {
            if (bestResult == null || currentBound < bestResult.totalBound) {
                bestResult = new Result(currentPath, currentTime, currentBound);
                Log.d(TAG2, "새로운 최적해 발견!");
                Log.d(TAG2, String.format("최적 bound: %.5f", currentBound));
            }
            return;
        }

        String currentNode = currentPath.get(currentPath.size() - 1);

        // 다음 방문할 노드 탐색
        for (String nextNode : graph.getNodes().keySet()) {
            if (visited.contains(nextNode)) continue;

            Log.d(TAG2, String.format("\n다음 노드 %s 검토 중...", nextNode));

            int moveTime = graph.getAdjacencyMap().get(currentNode).get(nextNode) / 60;
            int nextTotalTime = currentTime + moveTime;

            // 골든타임 체크
            if (nextTotalTime > goldenTime) {
                Log.d(TAG2, String.format("골든타임(%d분) 초과로 가지치기", goldenTime));
                continue;
            }

            double nextBound = calculateBound(nextNode, moveTime, branchLevel);
            double nextTotalBound = currentBound + nextBound;

            // 초기 탐색이 아닐 경우에만 bound 기반 가지치기 수행
            if (!isInitialSearch && bestResult != null && nextTotalBound >= bestResult.totalBound) {
                Log.d(TAG2, String.format("현재 최적 bound(%.5f)보다 큰 값(%.5f)이므로 가지치기",
                        bestResult.totalBound, nextTotalBound));
                continue;
            }

            Log.d(TAG2, "해당 경로로 탐색 진행");
            currentPath.add(nextNode);
            visited.add(nextNode);

            branch_and_bound_recursive(currentPath, visited, nextTotalTime, nextTotalBound, branchLevel + 1, isInitialSearch);

            currentPath.remove(currentPath.size() - 1);
            visited.remove(nextNode);
        }
    }

}