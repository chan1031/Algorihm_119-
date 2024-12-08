package com.example.a119_saver;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class RouteFragment extends Fragment {
    private TextView routeTimeText;
    private TextView hospitalCountText;
    private Button detailButton;
    private Button startButton;

    // 인터페이스 정의
    public interface RouteDataListener {
        String getBedNum();
    }

    private RouteDataListener dataListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // MainActivity가 인터페이스를 구현했는지 확인
        if (context instanceof RouteDataListener) {
            dataListener = (RouteDataListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement RouteDataListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_route, container, false);

        // UI 요소 초기화
        routeTimeText = view.findViewById(R.id.text_route_time);
        hospitalCountText = view.findViewById(R.id.text_hospital_count);
        detailButton = view.findViewById(R.id.button_detail_route);
        startButton = view.findViewById(R.id.button_start_navigation);

        // 버튼 클릭 리스너 설정
        detailButton.setOnClickListener(v -> {
            // 상세 경로 버튼 클릭 처리
        });

        startButton.setOnClickListener(v -> {
            // 안내 시작 버튼 클릭 처리
        });

        // 병상 수 정보 업데이트
        updateHospitalInfo();

        return view;
    }

    // 병상 수 업데이트 메서드
    private void updateHospitalInfo() {
        if (dataListener != null && hospitalCountText != null) {
            String bedNum = dataListener.getBedNum();
            hospitalCountText.setText("응급실 병상수: " + bedNum + "개");
        }
    }

//    // 경로 시간 업데이트 메서드
//    public void updateRouteTime(String time) {
//        if (routeTimeText != null) {
//            routeTimeText.setText("응급실까지 " + time + "분");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        dataListener = null;
    }
}