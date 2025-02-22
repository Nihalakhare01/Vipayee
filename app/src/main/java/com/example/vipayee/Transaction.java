package com.example.vipayee;

public class Transaction {
    private String transactionType;
    private String payeeName;
    private double amount;
    private String transactionDate; // Date only (dd-MM-yyyy)
    private String transactionTime; // Time only (HH:mm:ss)

    public Transaction(String transactionType, String payeeName, double amount, String transactionDate, String transactionTime) {
        this.transactionType = transactionType;
        this.payeeName = payeeName;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
    }

    // Getters
    public String getTransactionType() { return transactionType; }
    public String getPayeeName() { return payeeName; }

    public double getAmount() { return amount; }
    public String getTransactionDate() { return transactionDate; }
    public String getTransactionTime() { return transactionTime; }
}


