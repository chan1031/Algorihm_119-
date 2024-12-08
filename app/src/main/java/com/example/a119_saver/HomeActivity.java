package com.example.a119_saver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    // 응급실 체크박스 상태
    private boolean[] emergencyRoomChecks = new boolean[10];
    // 장비 체크박스 상태
    private boolean[] equipmentChecks = new boolean[11];
    
    private CheckBox[] emergencyRoomCheckboxes = new CheckBox[10];
    private CheckBox[] equipmentCheckboxes = new CheckBox[11];
    private EditText goldenTimeInput;
    private Button findRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupCheckBoxListeners();
        setupFindRouteButton();
    }

    private void initializeViews() {
        // 응급실 체크박스 초기화
        emergencyRoomCheckboxes[0] = findViewById(R.id.checkbox_er_all);
        emergencyRoomCheckboxes[1] = findViewById(R.id.checkbox_er_normal);
        emergencyRoomCheckboxes[2] = findViewById(R.id.checkbox_er_cohort);
        emergencyRoomCheckboxes[3] = findViewById(R.id.checkbox_er_negative);
        emergencyRoomCheckboxes[4] = findViewById(R.id.checkbox_er_isolation);
        emergencyRoomCheckboxes[5] = findViewById(R.id.checkbox_er_trauma);
        emergencyRoomCheckboxes[6] = findViewById(R.id.checkbox_er_pediatric);
        emergencyRoomCheckboxes[7] = findViewById(R.id.checkbox_er_pediatric_negative);
        emergencyRoomCheckboxes[8] = findViewById(R.id.checkbox_er_pediatric_isolation);
        emergencyRoomCheckboxes[9] = findViewById(R.id.checkbox_er_isolation_all);

        // 장비 체크박스 초기화
        equipmentCheckboxes[0] = findViewById(R.id.checkbox_equipment_all);
        equipmentCheckboxes[1] = findViewById(R.id.checkbox_equipment_ventilator);
        equipmentCheckboxes[2] = findViewById(R.id.checkbox_equipment_ventilator_pediatric);
        equipmentCheckboxes[3] = findViewById(R.id.checkbox_equipment_temperature);
        equipmentCheckboxes[4] = findViewById(R.id.checkbox_equipment_oxygen);
        equipmentCheckboxes[5] = findViewById(R.id.checkbox_equipment_angiography);
        equipmentCheckboxes[6] = findViewById(R.id.checkbox_equipment_incubator);
        equipmentCheckboxes[7] = findViewById(R.id.checkbox_equipment_crrt);
        equipmentCheckboxes[8] = findViewById(R.id.checkbox_equipment_ecmo);
        equipmentCheckboxes[9] = findViewById(R.id.checkbox_equipment_ct);
        equipmentCheckboxes[10] = findViewById(R.id.checkbox_equipment_mri);

        goldenTimeInput = findViewById(R.id.edit_golden_time);
        findRouteButton = findViewById(R.id.button_find_route);
    }

    private void setupCheckBoxListeners() {
        // 응급실 전체 선택 리스너
        emergencyRoomCheckboxes[0].setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 1; i < emergencyRoomCheckboxes.length; i++) {
                emergencyRoomCheckboxes[i].setChecked(isChecked);
                emergencyRoomChecks[i] = isChecked;
            }
        });

        // 격리병상 전체 선택 리스너
        emergencyRoomCheckboxes[9].setOnCheckedChangeListener((buttonView, isChecked) -> {
            int[] isolationIndexes = {2, 3, 4, 7, 8};
            for (int index : isolationIndexes) {
                emergencyRoomCheckboxes[index].setChecked(isChecked);
                emergencyRoomChecks[index] = isChecked;
            }
        });

        // 장비 전체 선택 리스너
        equipmentCheckboxes[0].setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (int i = 1; i < equipmentCheckboxes.length; i++) {
                equipmentCheckboxes[i].setChecked(isChecked);
                equipmentChecks[i] = isChecked;
            }
        });

        // 개별 체크박스 리스너 설정
        for (int i = 1; i < emergencyRoomCheckboxes.length; i++) {
            final int index = i;
            emergencyRoomCheckboxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                emergencyRoomChecks[index] = isChecked;
            });
        }

        for (int i = 1; i < equipmentCheckboxes.length; i++) {
            final int index = i;
            equipmentCheckboxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                equipmentChecks[index] = isChecked;
            });
        }
    }

    private void setupFindRouteButton() {
        findRouteButton.setOnClickListener(v -> {
            int goldenTime = 0;
            try {
                goldenTime = Integer.parseInt(goldenTimeInput.getText().toString());
            } catch (NumberFormatException e) {
                // 골든타임 입력값이 없거나 잘못된 경우 처리
            }

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}
