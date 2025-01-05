package com.example.drivit_v2_frontend.models;

import com.example.drivit_v2_frontend.enums.UserType;

import java.io.Serializable;

public class Users implements Serializable {
    private String userID;
    private String firstName;
    private String lastName;
    private String userName;
    private String passWord;
    private String cin;
    private String email;
    private String phone;
    private UserType status;

    public Users() {
    }
    public Users(String userID,String firstName, String lastName, String userName, String passWord, String cin, String email, String phone, UserType status) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.passWord = passWord;
        this.cin = cin;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserType getStatus() {
        return status;
    }

    public void setStatus(UserType status) {
        this.status = status;
    }
}
