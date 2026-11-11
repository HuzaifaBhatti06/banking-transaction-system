package com.bankingsystem.service;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;

public class TransactionValidator {

    private AccountManager accountManager;

    public TransactionValidator(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    public String validate(Transaction t) {
        if (t.getAmount() <= 0) {
            return "Invalid amount";
        }

        if (t.getType().equals(Transaction.DEPOSIT)) {
            if (!isUsable(t.getDestinationAccount())) {
                return "Destination account not found or inactive";
            }
            return "";
        }
        if (t.getType().equals(Transaction.WITHDRAWAL)) {
            if (!isUsable(t.getSourceAccount())) {
                return "Source account not found or inactive";
            }
            Account source = accountManager.findAccount(t.getSourceAccount());
            if (source.getBalance() < t.getAmount()) {
                return "Insufficient funds";
            }
            return "";
        }

        if (t.getType().equals(Transaction.TRANSFER)) {
            if (!isUsable(t.getSourceAccount())) {
                return "Source account not found or inactive";
            }
            if (!isUsable(t.getDestinationAccount())) {
                return "Destination account not found or inactive";
            }
            if (t.getSourceAccount().equals(t.getDestinationAccount())) {
                return "Source and destination must be different";
            }
            Account source = accountManager.findAccount(t.getSourceAccount());
            if (source.getBalance() < t.getAmount()) {
                return "Insufficient funds";
            }
            return "";
        }

        return "Unknown transaction type";
    }

    private boolean isUsable(String number) {
        if (number == null) {
            return false;
        }
        Account a = accountManager.findAccount(number);
        return a != null && a.isActive();
    }
}