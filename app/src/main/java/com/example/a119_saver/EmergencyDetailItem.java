package com.example.a119_saver;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class EmergencyDetailItem {

    @Element(name = "hpid", required = false)
    private String hospitalId;

    @Element(name = "dutyName", required = false)
    private String hospitalName;

    @Element(name = "dutyAddr", required = false)
    private String address;

    @Element(name = "dutyTel1", required = false)
    private String dutyTel1;

    @Element(name = "dutyTel3", required = false)
    private String emergencyTel;

    @Element(name = "wgs84Lat", required = false)
    private String latitude;

    @Element(name = "wgs84Lon", required = false)
    private String longitude;

    @Element(name = "hvec", required = false)
    private String emergencyBeds;

    @Element(name = "hvoc", required = false)
    private String operationBeds;

    @Element(name = "hvgc", required = false)
    private String generalBeds;

    // 기타 필요한 필드 추가 가능

    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getAddress() {
        return address;
    }

    public String getDutyTel1() {
        return dutyTel1;
    }

    public String getEmergencyTel() {
        return emergencyTel;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getEmergencyBeds() {
        return emergencyBeds;
    }

    public String getOperationBeds() {
        return operationBeds;
    }

    public String getGeneralBeds() {
        return generalBeds;
    }

    @Override
    public String toString() {
        return "EmergencyDetailItem{" +
                "hospitalId='" + hospitalId + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", address='" + address + '\'' +
                ", dutyTel1='" + dutyTel1 + '\'' +
                ", emergencyTel='" + emergencyTel + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", emergencyBeds='" + emergencyBeds + '\'' +
                ", operationBeds='" + operationBeds + '\'' +
                ", generalBeds='" + generalBeds + '\'' +
                '}';
    }
}


