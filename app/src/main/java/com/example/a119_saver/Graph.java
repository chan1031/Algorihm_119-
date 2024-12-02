package com.example.a119_saver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<String, Node> nodes; // 노드 저장 (키: 위치이름)
    private Map<String, Map<String, Integer>> adjacencyMap; // 인접노드간 가중치(시간)
    private Map<String, Map<String, List<KakaoNavigation.Vertex>>> vertexMap; // 경로 정보 추가

    public class Node {
        String name; // 위치이름 (현재위치 or 병원이름)
        double lat, lon;
        Integer hvec;


        // 병원용 새로운 생성자
        public Node(String name, double lat, double lon, int hvec) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
            this.hvec = hvec;
        }
        public Integer getHvec() {
            return hvec;
        }
    }

    public Graph() {
        nodes = new HashMap<>();
        adjacencyMap = new HashMap<>();
        vertexMap = new HashMap<>();
    }

    // vertex 정보 저장 메소드 추가
    public void addVertexPath(String from, String to, List<KakaoNavigation.Vertex> vertices) {
        if (!vertexMap.containsKey(from)) {
            vertexMap.put(from, new HashMap<>());
        }
        vertexMap.get(from).put(to, vertices);
    }

    // vertex 정보 조회 메소드
    public List<KakaoNavigation.Vertex> getVertexPath(String from, String to) {
        if (vertexMap.containsKey(from) && vertexMap.get(from).containsKey(to)) {
            return vertexMap.get(from).get(to);
        }
        return null;
    }

    // Graph 클래스의 addNode 메소드도 오버로드
    public void addNode(String name, double lat, double lon, int hvec) {
        Node node = new Node(name, lat, lon, hvec);
        nodes.put(name, node);
        adjacencyMap.put(name, new HashMap<>());
    }

    // 엣지 추가 (양방향)
    public void addEdge(String from, String to, int duration) {
        adjacencyMap.get(from).put(to, duration);
        adjacencyMap.get(to).put(from, duration);
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public Map<String, Map<String, Integer>> getAdjacencyMap() {
        return adjacencyMap;
    }
}
