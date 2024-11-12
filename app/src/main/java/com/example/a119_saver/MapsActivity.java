package com.example.a119_saver;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.util.Log;
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SupportMapFragment를 가져와서 지도가 준비되면 알림을 받습니다.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapsActivity", "Map is ready");
        mMap = googleMap;

        LatLng seoulCityHall = new LatLng(37.5873, 126.9930);
        mMap.addMarker(new MarkerOptions().position(seoulCityHall).title("Seoul City Hall"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulCityHall, 15));
    }
}
