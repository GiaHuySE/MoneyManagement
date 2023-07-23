package com.example.doan.entity;

import java.util.Date;

public class GiaoDichNap {
    private String uuid;
    private  String tenHu;
    private Double tienNap;
    private Date ngayNap;
    private String moTa;
    private String loaiGiaoDich;

    public GiaoDichNap() {
    }

//    public GiaoDichNap(Double tienNap, Date ngayNap, String moTa) {
//        this.tienNap = tienNap;
//        this.ngayNap = ngayNap;
//        this.moTa = moTa;
//    }


    public GiaoDichNap(String uuid, String tenHu, Double tienNap, Date ngayNap, String moTa, String loaiGiaoDich) {
        this.uuid = uuid;
        this.tenHu = tenHu;
        this.tienNap = tienNap;
        this.ngayNap = ngayNap;
        this.moTa = moTa;
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public String getTenHu() {
        return tenHu;
    }

    public void setTenHu(String tenHu) {
        this.tenHu = tenHu;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public Double getTienNap() {
        return tienNap;
    }

    public void setTienNap(Double tienNap) {
        this.tienNap = tienNap;
    }

    public Date getNgayNap() {
        return ngayNap;
    }

    public void setNgayNap(Date ngayNap) {
        this.ngayNap = ngayNap;
    }



    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}
