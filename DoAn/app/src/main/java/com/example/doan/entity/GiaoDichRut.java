package com.example.doan.entity;

import java.util.Date;

public class GiaoDichRut {
    private  String id;
    private  String tenHu;
    private Double tienRut;
    private Date ngayRut;

    private String moTa;

    private String loaiGiaoDich;

//    public GiaoDichRut(String tenHu, Double tienRut, Date ngayRut, String moTa) {
//        this.tenHu = tenHu;
//        this.tienRut = tienRut;
//        this.ngayRut = ngayRut;
//        this.moTa = moTa;
//    }


    public GiaoDichRut(String id, String tenHu, Double tienRut, Date ngayRut, String moTa, String loaiGiaoDich) {
        this.id = id;
        this.tenHu = tenHu;
        this.tienRut = tienRut;
        this.ngayRut = ngayRut;
        this.moTa = moTa;
        this.loaiGiaoDich = loaiGiaoDich;
    }

    public GiaoDichRut(String id, String tenHu, Double tienRut, Date ngayRut, String moTa) {
        this.id = id;
        this.tenHu = tenHu;
        this.tienRut = tienRut;
        this.ngayRut = ngayRut;
        this.moTa = moTa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenHu() {
        return tenHu;
    }

    public void setTenHu(String tenHu) {
        this.tenHu = tenHu;
    }

    public Double getTienRut() {
        return tienRut;
    }

    public void setTienRut(Double tienRut) {
        this.tienRut = tienRut;
    }

    public Date getNgayRut() {
        return ngayRut;
    }

    public void setNgayRut(Date ngayRut) {
        this.ngayRut = ngayRut;
    }



    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getLoaiGiaoDich() {
        return loaiGiaoDich;
    }

    public void setLoaiGiaoDich(String loaiGiaoDich) {
        this.loaiGiaoDich = loaiGiaoDich;
    }
}
