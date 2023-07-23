package com.example.doan.entity;

public class GiaoDichChuyenTaiSan {
    private  String tenHuChuyen;
    private Double soTienChuyen;
    private String tenHuNhan;


    public GiaoDichChuyenTaiSan(String tenHuChuyen, Double soTienChuyen, String tenHuNhan) {
        this.tenHuChuyen = tenHuChuyen;
        this.soTienChuyen = soTienChuyen;
        this.tenHuNhan = tenHuNhan;
    }

    public String getTenHuChuyen() {
        return tenHuChuyen;
    }

    public void setTenHuChuyen(String tenHuChuyen) {
        this.tenHuChuyen = tenHuChuyen;
    }

    public Double getSoTienChuyen() {
        return soTienChuyen;
    }

    public void setSoTienChuyen(Double soTienChuyen) {
        this.soTienChuyen = soTienChuyen;
    }

    public String getTenHuNhan() {
        return tenHuNhan;
    }

    public void setTenHuNhan(String tenHuNhan) {
        this.tenHuNhan = tenHuNhan;
    }


}
