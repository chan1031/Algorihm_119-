package com.example.a119_saver;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EmergencyMedicalAPI {
    @GET("getEmrrmRltmUsefulSckbdInfoInqire")
    Call<EmergencyListResponse> getEmergencyList(
            @Query("serviceKey") String apiKey,
            @Query("STAGE1") String city,
            @Query("STAGE2") String district,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("type") String type  // XML 응답을 위한 타입 추가
    );

    @GET("getEgytBassInfoInqire")
    Call<EmergencyDetailResponse> getEmergencyDetail(
            @Query("serviceKey") String apiKey,
            @Query("HPID") String hospitalId,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("type") String type  // XML 응답을 위한 타입 추가
    );
}
