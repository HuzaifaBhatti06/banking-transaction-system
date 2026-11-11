package com.bankingsystem.service;

import com.bankingsystem.model.Account;

public class AccountManager {

    private Account[] accounts;
    private int count;

    public AccountManager(int capacity) {
        accounts = new Account[capacity];
        count = 0;
    }

    public boolean addAccount(Account a) {
        if (count == accounts.length) {
            return false;
        }
        if (findAccount(a.getAccountNumber()) != null) {
            return false;
        }
        accounts[count] = a;
        count++;
        return true;
    }

    public Account findAccount(String number) {
        for (int i = 0; i < count; i++) {
            if (accounts[i].getAccountNumber().equals(number)) {
                return accounts[i];
            }
        }
        return null;
    }

    public int getAccountCount() {
        return count;
    }

    public Account[] getAllAccounts() {
        Account[] result = new Account[count];
        for (int i = 0; i < count; i++) {
            result[i] = accounts[i];
        }
        return result;
    }
}