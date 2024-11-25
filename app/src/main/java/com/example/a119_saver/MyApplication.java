// app/src/main/java/com/example/_119_saver/MyApplication.java

package com.example.a119_saver;  // 패키지명은 실제 프로젝트에 맞게 수정

import android.app.Application;
import com.kakao.vectormap.KakaoMapSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 카카오맵 SDK 초기화
        KakaoMapSdk.init(this, "a6a628594619f2c244c653feab94d732");  // 여기에 본인의 API 키를 넣으세요
    }
}