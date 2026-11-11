package com.bankingsystem.report;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.service.BankingService;

public class ReportGenerator {
    private BankingService service;

    public ReportGenerator(BankingService service) {
        this.service = service;
    }

    public void printAccountStatement(String accountNumber) {
        Account account = service.getAccountManager().findAccount(accountNumber);
        System.out.println();
        if (account == null) {
            System.out.println("=== STATEMENT: account " + accountNumber + " not found ===");
            return;
        }

        System.out.println("=== STATEMENT: " + account.getAccountNumber() + " | "
                + account.getAccountHolderName() + " ===");
        System.out.println("Current balance: " + account.getBalance()
                + " | Active: " + account.isActive());

        Transaction[] newestFirst = service.getHistory().getAllReverse();
        int shown = 0;
        for (int i = 0; i < newestFirst.length; i++) {
            Transaction t = newestFirst[i];
            boolean isSource = accountNumber.equals(t.getSourceAccount());
            boolean isDestination = accountNumber.equals(t.getDestinationAccount());
            if (isSource || isDestination) {
                String direction = isSource ? "OUT" : "IN ";
                System.out.println("  " + direction + " " + describe(t));
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("  (no processed transactions)");
        }
    }

    public void printAuditReport() {
        printSection("AUDIT REPORT: full processing history (oldest first)",
                service.getHistory().getAll());

        int success = service.getSuccessList().size();
        int failed = service.getFailedList().size();
        int cancelled = service.getCancelledList().size();
        int reversed = service.getReversedList().size();
        int pending = countPending();
        int processed = service.getHistory().size();

        System.out.println();
        System.out.println("--- Audit summary ---");
        System.out.println("Successful : " + success);
        System.out.println("Failed     : " + failed);
        System.out.println("Cancelled  : " + cancelled);
        System.out.println("Reversed   : " + reversed);
        System.out.println("Pending    : " + pending + " (not in history yet)");
        System.out.println("History records: " + processed);
        if (processed == success + failed + cancelled + reversed) {
            System.out.println("Check: history size equals the four category totals. OK");
        } else {
            System.out.println("Check: MISMATCH between history and category lists!");
        }
    }

    public void printCategoryRecords() {
        printSection("SUCCESSFUL transactions", service.getSuccessList().getAll());
        printSection("FAILED transactions", service.getFailedList().getAll());
        printSection("CANCELLED transactions", service.getCancelledList().getAll());
        printSection("REVERSED transactions", service.getReversedList().getAll());
    }

    public void printPendingTransactions() {
        Transaction[] waiting = service.getPendingQueue().getAll();
        System.out.println();
        System.out.println("=== PENDING queue (front first) ===");
        int shown = 0;
        for (int i = 0; i < waiting.length; i++) {
            if (waiting[i].getStatus().equals(Transaction.PENDING)) {
                System.out.println("  " + describe(waiting[i]));
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("  (none)");
        }
    }

    public void printAllAccounts() {
        Account[] all = service.getAccountManager().getAllAccounts();
        System.out.println();
        System.out.println("=== ALL ACCOUNTS (" + all.length + ") ===");
        for (int i = 0; i < all.length; i++) {
            System.out.println("  " + all[i]);
        }
    }

    private void printSection(String title, Transaction[] items) {
        System.out.println();
        System.out.println("=== " + title + " (" + items.length + ") ===");
        if (items.length == 0) {
            System.out.println("  (none)");
        }
        for (int i = 0; i < items.length; i++) {
            System.out.println("  " + describe(items[i]));
        }
    }

    private int countPending() {
        Transaction[] waiting = service.getPendingQueue().getAll();
        int count = 0;
        for (int i = 0; i < waiting.length; i++) {
            if (waiting[i].getStatus().equals(Transaction.PENDING)) {
                count++;
            }
        }
        return count;
    }

    private String describe(Transaction t) {
        String from = t.getSourceAccount() == null ? "-" : t.getSourceAccount();
        String to = t.getDestinationAccount() == null ? "-" : t.getDestinationAccount();
        String line = t.getTransactionId() + " | " + t.getType() + " | " + from + " -> " + to
                + " | " + t.getAmount() + " | " + t.getStatus();
        if (!t.getFailureReason().equals("")) {
            line = line + " (" + t.getFailureReason() + ")";
        }
        return line;
    }
}