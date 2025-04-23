package com.example.vipayee;

import java.util.UUID;

public class User {
    private String userID;
    private String fullName;
    private String phoneNumber;
    private String pin;
    private String createdAt;
    private Balance balance;

    // Constructor: Generate a unique UserID before sending to API
    public User(String fullName, String phoneNumber, String pin) {
        this.userID = UUID.randomUUID().toString();  // Generate a unique ID
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.pin = pin;
        this.createdAt = null;
        this.balance = new Balance(0, this.userID);
    }

    public String getUserID() { return userID; }
    public String getFullName() { return fullName; }
    public String getPhoneNumber() { return phoneNumber; }

    public String getPin() { return pin; }
    public String getCreatedAt() { return createdAt; }
    public Balance getBalance() { return balance; }
}
