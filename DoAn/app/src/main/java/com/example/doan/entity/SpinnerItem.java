package com.example.doan.entity;

public class SpinnerItem {
    private String tenHu;
    private String soTien;

    private int tyle;

    public SpinnerItem(String tenHu, String soTien) {
        this.tenHu = tenHu;
        this.soTien = soTien;
    }

    public String getTenHu() {
        return tenHu;
    }

    public String getSoTien() {
        return soTien;
    }

    public int getTyle() {
        return tyle;
    }
}
