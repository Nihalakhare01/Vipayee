package com.example.vipayee;

public class User {
    private int userID;
    private String fullName;
    private String phoneNumber;
    private String pin;

    public User(int userID, String fullName, String phoneNumber, String pin) {
        this.userID = userID;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.pin = pin;
    }

    public int getUserID() { return userID; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPin() { return pin; }
}



