package com.bankingsystem.service;

import com.bankingsystem.datastructure.DoublyLinkedList;
import com.bankingsystem.datastructure.LinkedQueue;
import com.bankingsystem.datastructure.LinkedStack;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;

public class BankingService {

    private AccountManager accountManager;
    private TransactionValidator validator;
    private LinkedQueue pendingQueue;
    private DoublyLinkedList history;
    private DoublyLinkedList successList;
    private DoublyLinkedList failedList;
    private DoublyLinkedList cancelledList;
    private DoublyLinkedList reversedList;
    private LinkedStack reversibleStack;
    private boolean simulateCreditFailure;

    public BankingService(AccountManager accountManager) {
        this.accountManager = accountManager;
        this.validator = new TransactionValidator(accountManager);
        this.pendingQueue = new LinkedQueue();
        this.history = new DoublyLinkedList();
        this.successList = new DoublyLinkedList();
        this.failedList = new DoublyLinkedList();
        this.cancelledList = new DoublyLinkedList();
        this.reversedList = new DoublyLinkedList();
        this.reversibleStack = new LinkedStack();
        this.simulateCreditFailure = false;
    }

    public boolean submitTransaction(Transaction t) {
        if (history.findById(t.getTransactionId()) != null || isWaiting(t.getTransactionId())) {
            markFailed(t, "Duplicate transaction ID");
            return false;
        }
        pendingQueue.enqueue(t);
        return true;
    }

    public boolean cancelTransaction(String id) {
        Transaction[] waiting = pendingQueue.getAll();
        for (int i = 0; i < waiting.length; i++) {
            if (waiting[i].getTransactionId().equals(id)
                    && waiting[i].getStatus().equals(Transaction.PENDING)) {
                waiting[i].setStatus(Transaction.CANCELLED);
                return true;
            }
        }
        return false;
    }

    public Transaction processNext() {
        Transaction t = pendingQueue.dequeue();
        if (t == null) {
            return null;
        }

        if (t.getStatus().equals(Transaction.CANCELLED)) {
            cancelledList.addLast(t);
            history.addLast(t);
            return t;
        }

        String problem = validator.validate(t);
        if (!problem.equals("")) {
            markFailed(t, problem);
            return t;
        }

        String result = execute(t);
        if (result.equals("")) {
            t.setStatus(Transaction.SUCCESS);
            successList.addLast(t);
            history.addLast(t);
            reversibleStack.push(t);
        } else {
            markFailed(t, result);
        }
        return t;
    }

    public void processAll() {
        while (!pendingQueue.isEmpty()) {
            processNext();
        }
    }

    public String reverseLastTransaction() {
        Transaction t = reversibleStack.peek();
        if (t == null) {
            return "Nothing to reverse";
        }
        if (!t.getStatus().equals(Transaction.SUCCESS)) {
            return "Only successful transactions can be reversed";
        }

        Account source = null;
        Account destination = null;
        if (t.getSourceAccount() != null) {
            source = accountManager.findAccount(t.getSourceAccount());
        }
        if (t.getDestinationAccount() != null) {
            destination = accountManager.findAccount(t.getDestinationAccount());
        }

        if (t.getType().equals(Transaction.DEPOSIT)) {
            if (!destination.isActive()) {
                return "Destination account is closed";
            }
            if (destination.getBalance() < t.getAmount()) {
                return "Insufficient balance to reverse the deposit";
            }
            destination.withdraw(t.getAmount());
        } else if (t.getType().equals(Transaction.WITHDRAWAL)) {
            if (!source.isActive()) {
                return "Source account is closed";
            }
            source.deposit(t.getAmount());
        } else {
            if (!source.isActive() || !destination.isActive()) {
                return "An account is closed";
            }
            if (destination.getBalance() < t.getAmount()) {
                return "Insufficient balance to reverse the transfer";
            }
            destination.withdraw(t.getAmount());
            source.deposit(t.getAmount());
        }

        reversibleStack.pop();
        t.setStatus(Transaction.REVERSED);
        successList.removeById(t.getTransactionId());
        reversedList.addLast(t);
        return "";
    }

    private String execute(Transaction t) {
        if (t.getType().equals(Transaction.DEPOSIT)) {
            Account destination = accountManager.findAccount(t.getDestinationAccount());
            if (!destination.deposit(t.getAmount())) {
                return "Deposit failed";
            }
            return "";
        }

        if (t.getType().equals(Transaction.WITHDRAWAL)) {
            Account source = accountManager.findAccount(t.getSourceAccount());
            if (!source.withdraw(t.getAmount())) {
                return "Withdrawal failed";
            }
            return "";
        }

        Account source = accountManager.findAccount(t.getSourceAccount());
        Account destination = accountManager.findAccount(t.getDestinationAccount());

        if (!source.withdraw(t.getAmount())) {
            return "Debit failed";
        }

        boolean credited;
        if (simulateCreditFailure) {
            credited = false;
            simulateCreditFailure = false;
        } else {
            credited = destination.deposit(t.getAmount());
        }

        if (!credited) {
            source.deposit(t.getAmount());
            return "Credit failed, amount returned to source account (rollback)";
        }
        return "";
    }

    private void markFailed(Transaction t, String reason) {
        t.setStatus(Transaction.FAILURE);
        t.setFailureReason(reason);
        failedList.addLast(t);
        history.addLast(t);
    }

    private boolean isWaiting(String id) {
        Transaction[] waiting = pendingQueue.getAll();
        for (int i = 0; i < waiting.length; i++) {
            if (waiting[i].getTransactionId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void setSimulateCreditFailure(boolean value) {
        this.simulateCreditFailure = value;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public LinkedQueue getPendingQueue() {
        return pendingQueue;
    }

    public DoublyLinkedList getHistory() {
        return history;
    }

    public DoublyLinkedList getSuccessList() {
        return successList;
    }

    public DoublyLinkedList getFailedList() {
        return failedList;
    }

    public DoublyLinkedList getCancelledList() {
        return cancelledList;
    }

    public DoublyLinkedList getReversedList() {
        return reversedList;
    }

    public LinkedStack getReversibleStack() {
        return reversibleStack;
    }
}