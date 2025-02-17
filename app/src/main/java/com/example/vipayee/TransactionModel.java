package com.example.vipayee;

public class TransactionModel {
    private String transactionType;
    private String amount;
    private String payeeName;
    private String transactionDate;

    public TransactionModel(String transactionType, String amount, String payeeName, String transactionDate) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.payeeName = payeeName;
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getTransactionDate() {
        return transactionDate;
    }


}
