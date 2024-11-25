package com.example.a119_saver;

import com.example.a119_saver.SearchKeywordResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

// 키워드 검색 API 정의
public interface KakaoLocalApi {
    @GET("v2/local/search/keyword.json")

    Call<SearchKeywordResult> searchKeyword(
            @Header("Authorization") String key,
            @Query("query") String query,
            @Query("x") double longitude, // 현재 지도 중심의 경도
            @Query("y") double latitude,  // 현재 지도 중심의 위도
            @Query("radius") int radius,  // 검색 반경 (미터 단위, 최대 20000)
            @Query("size") int size       // 결과 개수
    );
}
