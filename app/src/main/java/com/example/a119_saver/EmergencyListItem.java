package com.example.a119_saver;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item", strict = false)
public class EmergencyListItem {
    @Element(name = "dutyName", required = false)
    private String hospitalName;

    @Element(name = "dutyTel3", required = false)
    private String emergencyTel;

    @Element(name = "hpid", required = false)
    private String hospitalId;

    @Element(name = "hv10", required = false)
    private String hv10;

    @Element(name = "hv11", required = false)
    private String hv11;

    @Element(name = "hv28", required = false)
    private String hv28;

    @Element(name = "hv29", required = false)
    private String hv29;

    @Element(name = "hv30", required = false)
    private String hv30;

    @Element(name = "hv34", required = false)
    private String hv34;

    @Element(name = "hv41", required = false)
    private String hv41;

    @Element(name = "hv42", required = false)
    private String hv42;

    @Element(name = "hv5", required = false)
    private String hv5;

    @Element(name = "hv6", required = false)
    private String hv6;

    @Element(name = "hv7", required = false)
    private String hv7;

    @Element(name = "hvamyn", required = false)
    private String hvamyn;

    @Element(name = "hvangioayn", required = false)
    private String hvangioayn;

    @Element(name = "hvcrrtayn", required = false)
    private String hvcrrtayn;

    @Element(name = "hvctayn", required = false)
    private String hvctayn;

    @Element(name = "hvec", required = false)
    private String hvec;

    @Element(name = "hvecmoayn", required = false)
    private String hvecmoayn;

    @Element(name = "hvgc", required = false)
    private String hvgc;

    @Element(name = "hvhypoayn", required = false)
    private String hvhypoayn;

    @Element(name = "hvicc", required = false)
    private String hvicc;

    @Element(name = "hvidate", required = false)
    private String hvidate;

    @Element(name = "hvincuayn", required = false)
    private String hvincuayn;

    @Element(name = "hvmriayn", required = false)
    private String hvmriayn;

    @Element(name = "hvncc", required = false)
    private String hvncc;

    @Element(name = "hvoc", required = false)
    private String hvoc;

    @Element(name = "hvoxyayn", required = false)
    private String hvoxyayn;

    @Element(name = "hvs01", required = false)
    private String hvs01;

    @Element(name = "hvs02", required = false)
    private String hvs02;

    @Element(name = "hvs03", required = false)
    private String hvs03;

    @Element(name = "hvs04", required = false)
    private String hvs04;

    @Element(name = "hvs08", required = false)
    private String hvs08;

    @Element(name = "hvs12", required = false)
    private String hvs12;

    @Element(name = "hvs15", required = false)
    private String hvs15;

    @Element(name = "hvs17", required = false)
    private String hvs17;

    @Element(name = "hvs22", required = false)
    private String hvs22;

    @Element(name = "hvs25", required = false)
    private String hvs25;

    @Element(name = "hvs26", required = false)
    private String hvs26;

    @Element(name = "hvs27", required = false)
    private String hvs27;

    @Element(name = "hvs28", required = false)
    private String hvs28;

    @Element(name = "hvs29", required = false)
    private String hvs29;

    @Element(name = "hvs30", required = false)
    private String hvs30;

    @Element(name = "hvs31", required = false)
    private String hvs31;

    @Element(name = "hvs32", required = false)
    private String hvs32;

    @Element(name = "hvs33", required = false)
    private String hvs33;

    @Element(name = "hvs34", required = false)
    private String hvs34;

    @Element(name = "hvs35", required = false)
    private String hvs35;

    @Element(name = "hvs38", required = false)
    private String hvs38;

    @Element(name = "hvventiayn", required = false)
    private String hvventiayn;

    @Element(name = "hvventisoayn", required = false)
    private String hvventisoayn;

    @Element(name = "phpid", required = false)
    private String phpid;

    @Element(name = "rnum", required = false)
    private String rnum;

    // Getters
    public String getHospitalName() { return hospitalName; }
    public String getEmergencyTel() { return emergencyTel; }
    public String getHospitalId() { return hospitalId; }
    public String getHv10() { return hv10; }
    public String getHv11() { return hv11; }
    public String getHv28() { return hv28; }
    public String getHv29() { return hv29; }
    public String getHv30() { return hv30; }
    public String getHv34() { return hv34; }
    public String getHv41() { return hv41; }
    public String getHv42() { return hv42; }
    public String getHv5() { return hv5; }
    public String getHv6() { return hv6; }
    public String getHv7() { return hv7; }
    public String getHvamyn() { return hvamyn; }
    public String getHvangioayn() { return hvangioayn; }
    public String getHvcrrtayn() { return hvcrrtayn; }
    public String getHvctayn() { return hvctayn; }
    public String getHvec() { return hvec; }
    public String getHvecmoayn() { return hvecmoayn; }
    public String getHvgc() { return hvgc; }
    public String getHvhypoayn() { return hvhypoayn; }
    public String getHvicc() { return hvicc; }
    public String getHvidate() { return hvidate; }
    public String getHvincuayn() { return hvincuayn; }
    public String getHvmriayn() { return hvmriayn; }
    public String getHvncc() { return hvncc; }
    public String getHvoc() { return hvoc; }
    public String getHvoxyayn() { return hvoxyayn; }
    public String getHvs01() { return hvs01; }
    public String getHvs02() { return hvs02; }
    public String getHvs03() { return hvs03; }
    public String getHvs04() { return hvs04; }
    public String getHvs08() { return hvs08; }
    public String getHvs12() { return hvs12; }
    public String getHvs15() { return hvs15; }
    public String getHvs17() { return hvs17; }
    public String getHvs22() { return hvs22; }
    public String getHvs25() { return hvs25; }
    public String getHvs26() { return hvs26; }
    public String getHvs27() { return hvs27; }
    public String getHvs28() { return hvs28; }
    public String getHvs29() { return hvs29; }
    public String getHvs30() { return hvs30; }
    public String getHvs31() { return hvs31; }
    public String getHvs32() { return hvs32; }
    public String getHvs33() { return hvs33; }
    public String getHvs34() { return hvs34; }
    public String getHvs35() { return hvs35; }
    public String getHvs38() { return hvs38; }
    public String getHvventiayn() { return hvventiayn; }
    public String getHvventisoayn() { return hvventisoayn; }
    public String getPhpid() { return phpid; }
    public String getRnum() { return rnum; }
}
