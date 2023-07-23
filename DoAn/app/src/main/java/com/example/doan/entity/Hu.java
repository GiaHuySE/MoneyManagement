package com.example.doan.entity;

public class Hu {
    private Integer tyLe;
    private Double soTien;

    public Hu(Integer tyLe, Double soTien) {
        this.tyLe = tyLe;
        this.soTien = soTien;
    }

    public Hu() {
    }

    public Hu(Integer tyLe) {
        this.tyLe = tyLe;
    }

    public Hu(Double soTien) {
        this.soTien = soTien;
    }

    public Integer getTyLe() {
        return tyLe;
    }

    public void setTyLe(Integer tyLe) {
        this.tyLe = tyLe;
    }

    public Double getSoTien() {
        return soTien;
    }

    public void setSoTien(Double soTien) {
        this.soTien = soTien;
    }
}
