package com.example.vipayee;

public class Balance {
    private int balanceID;
    private double amount;
    private String userID;

    public Balance(double amount, String userID) {
        this.balanceID = 0; // API will auto-generate
        this.amount = amount;
        this.userID = userID; // Assign the generated UserID
    }

    public int getBalanceID() { return balanceID; }
    public double getAmount() { return amount; }
    public String getUserID() { return userID; }
}
