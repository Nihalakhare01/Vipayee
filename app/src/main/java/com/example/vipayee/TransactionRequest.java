package com.example.vipayee;

public class TransactionRequest {
    private String senderID;
    private String receiverID;
    private double amount;
    private String payeeName;

    public TransactionRequest(String senderID, String receiverID, double amount, String payeeName) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.amount = amount;
        this.payeeName = payeeName;
    }
}
