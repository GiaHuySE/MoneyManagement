package com.example.doan.entity;



import com.google.firebase.Timestamp;

import java.sql.Time;

import java.util.Date;

public class LichSuGiaoDich {
    String id;
    private Date thoiGian;
    private  String jarName;
    private double soTien;
    private String loaiGiaoDich;

    public LichSuGiaoDich() {
    }

    public LichSuGiaoDich(String id, Date thoiGian, String jarName, double soTien, String loaiGiaoDich) {
        this.id = id;
        this.thoiGian = thoiGian;
        this.jarName = jarName;
        this.soTien = soTien;
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(Date thoiGian) {
        this.thoiGian = thoiGian;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = soTien;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    @Override
    public String toString() {
        return "LichSuGiaoDich{" +
                "thoiGian=" + thoiGian +
                ", jarName='" + jarName + '\'' +
                ", soTien=" + soTien +
                ", loaiGiaoDich='" + loaiGiaoDich + '\'' +
                '}';
    }
}
