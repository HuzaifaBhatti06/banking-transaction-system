package com.bankingsystem.model;

import java.time.LocalDateTime;

public class Transaction {
    
    public static final String DEPOSIT = "Deposit";
    public static final String WITHDRAWAL = "Withdrawal";
    public static final String TRANSFER = "Transfer";

    public static final String SUCCESS = "Success";
    public static final String FAILURE = "Failed";
    public static final String PENDING = "Pending";
    public static final String CANCELLED = "Cancelled";
    public static final String REVERSED = "Reversed";

    private String transactionID;
    private String transactionType;
    private String sourceAccount;
    private String destinationAccount;
    private double amount;
    private LocalDateTime timestamp;
    private String status;
    private String failureReason;


    public Transaction(String transactionID, String transactionType, String sourceAccount, String destinationAccount, double amount) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.status = PENDING;
        this.failureReason = "";
    }

 public String getTransactionId() {
        return transactionID;
    }

    public String getType() {
        return transactionType;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getDestinationAccount() {
        return destinationAccount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String toString() {
        return transactionID + " | " + transactionType + " | From: " + sourceAccount + " | To: " + destinationAccount
                + " | Amount: " + amount + " | " + status + " | " + timestamp;
    }
}
