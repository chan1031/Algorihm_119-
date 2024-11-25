package com.example.a119_saver;

import com.google.gson.annotations.SerializedName;
import java.util.List;

// 검색 결과 데이터 클래스
public class SearchKeywordResult {
    @SerializedName("documents")
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public static class Place {
        @SerializedName("place_name")
        private String placeName;

        @SerializedName("x")
        private double x;

        @SerializedName("y")
        private double y;

        public String getPlaceName() {
            return placeName;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}
