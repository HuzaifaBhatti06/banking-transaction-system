package com.bankingsystem.model;

public class Account 
{
    private String accountNumber;
    private String accountHolderName;
    private double balance;
    private boolean isActive;



    public Account(String accountNumber, String accountHolderName, double balance)  {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = balance;
        this.isActive = true;
    }

    public String getAccountNumber() {
    return accountNumber;
    }

    public String getAccountHolderName() {
    return accountHolderName;
    }

    public double getBalance() {
    return balance;
    }
    public boolean isActive() {
        return isActive;
    }

    public boolean deposit(double amount)
    {
        if (isActive && amount > 0)
        {
            balance+=amount;
            return true;
        }
        return false;
    }
    

    public boolean withdraw(double amount)
    {
       if (isActive && amount > 0 && balance >= amount)
        {
            balance-=amount;
            return true;
        }
        return false;
       
    }

    public void closeAccount()
    {
        isActive = false;
    }

     public String toString() {
        return "Account " + accountNumber + " | " + accountHolderName + " | Balance: " + balance + " | Active: " + isActive;
    }
}

