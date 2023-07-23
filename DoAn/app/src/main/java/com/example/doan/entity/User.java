package com.example.doan.entity;

public class User {
    private String email;
    private String name;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String email) {
        this.email = email;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
