package com.example.studentmanager;

public class Student {
    private int mssv;
    private String name;
    private String birthday;
    private String email;
    private String address;
    private boolean tag;

    public Student(int mssv, String name, String birthday, String email, String address) {
        this.mssv = mssv;
        this.name = name;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
        this.tag = false;
    }

    public int getMssv() {
        return mssv;
    }

    public void setMssv(int mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setNgaySinh(String birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setQueQuan(String address) {
        this.address = address;
    }

    public boolean getTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }
}
