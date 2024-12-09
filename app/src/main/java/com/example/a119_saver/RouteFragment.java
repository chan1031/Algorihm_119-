package com.example.a119_saver;

import static com.example.a119_saver.MyApplication.getBed_num;
import static com.example.a119_saver.MyApplication.getGoldenTime;

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

        // Arguments에서 데이터 가져오기
        Bundle args = getArguments();
        if (args != null) {
            int time = args.getInt("time", 0);
            int bedNum = args.getInt("bedNum", 0);
            String firstHospitalName = args.getString("hospitalName","응급실");
            updateRouteTime(firstHospitalName,time);
            updateHospitalInfo(firstHospitalName,bedNum);
        }
        // 버튼 클릭 리스너 설정
        detailButton.setOnClickListener(v -> {
            // 상세 경로 버튼 클릭 처리
        });

        startButton.setOnClickListener(v -> {
            // 안내 시작 버튼 클릭 처리
        });

        return view;
    }

    // 병상 수 업데이트 메서드
    private void updateHospitalInfo(String name, int bed_num) {
        if (dataListener != null && hospitalCountText != null) {
            String bedNum = dataListener.getBedNum();
            hospitalCountText.setText(name+ "병상수: " + bed_num + "개");
        }
    }


 public void updateRouteTime(String name, int time) {
      if (routeTimeText != null) {
        routeTimeText.setText(name+"까지 " + time + "분");
    }}

    @Override
    public void onDetach() {
        super.onDetach();
        dataListener = null;
    }
}