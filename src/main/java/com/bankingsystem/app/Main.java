package com.bankingsystem.app;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.report.ReportGenerator;
import com.bankingsystem.service.AccountManager;
import com.bankingsystem.service.BankingService;

public class Main {
    public static void main(String[] args) {
        AccountManager manager = new AccountManager(50);
        BankingService service = new BankingService(manager);
        ReportGenerator report = new ReportGenerator(service);

        String[] names = {
            "Ali Khan", "Sara Ahmed", "Bilal Hussain", "Ayesha Noor", "Usman Tariq",
            "Hina Malik", "Zain Abbas", "Fatima Raza", "Hamza Iqbal", "Maryam Shah",
            "Omar Farooq", "Noor Fatima", "Talha Mehmood", "Iqra Saeed", "Danish Ali",
            "Laiba Khan", "Faizan Butt", "Rabia Aslam", "Saad Javed", "Anam Qureshi"
        };
        double[] openingBalances = {
            10000, 5000, 7500, 3000, 6000, 8000, 2000, 12000, 4500, 9000,
            1500, 6500, 3500, 7000, 2500, 5500, 4000, 8500, 1000, 3200
        };

        heading("PHASE 1: Create accounts");
        double openingTotal = 0;
        for (int i = 0; i < names.length; i++) {
            String number = "ACC" + (i < 9 ? "00" : "0") + (i + 1);
            manager.addAccount(new Account(number, names[i], openingBalances[i]));
            openingTotal += openingBalances[i];
        }
        System.out.println("Accounts created: " + manager.getAccountCount()
                + " | Total money in bank: " + openingTotal);

        Account found = manager.findAccount("ACC007");
        System.out.println("Search ACC007 -> " + found);
        System.out.println("Search ACC999 -> " + manager.findAccount("ACC999"));

        heading("PHASE 2: Deposits, withdrawals, transfers (submitted, then processed in FIFO order)");
        submit(service, "T001", Transaction.DEPOSIT, null, "ACC001", 2000);
        submit(service, "T002", Transaction.DEPOSIT, null, "ACC002", 1500);
        submit(service, "T003", Transaction.DEPOSIT, null, "ACC003", 500);
        submit(service, "T004", Transaction.DEPOSIT, null, "ACC004", 2500);
        submit(service, "T005", Transaction.DEPOSIT, null, "ACC005", 1000);
        submit(service, "T006", Transaction.DEPOSIT, null, "ACC006", 750);
        submit(service, "T007", Transaction.WITHDRAWAL, "ACC001", null, 1000);
        submit(service, "T008", Transaction.WITHDRAWAL, "ACC008", null, 2000);
        submit(service, "T009", Transaction.WITHDRAWAL, "ACC010", null, 3000);
        submit(service, "T010", Transaction.WITHDRAWAL, "ACC014", null, 500);
        submit(service, "T011", Transaction.TRANSFER, "ACC001", "ACC002", 1500);
        submit(service, "T012", Transaction.TRANSFER, "ACC008", "ACC009", 3000);
        submit(service, "T013", Transaction.TRANSFER, "ACC010", "ACC011", 2000);
        submit(service, "T014", Transaction.TRANSFER, "ACC006", "ACC012", 1000);
        submit(service, "T015", Transaction.TRANSFER, "ACC014", "ACC015", 1500);
        submit(service, "T016", Transaction.TRANSFER, "ACC016", "ACC017", 2500);
        System.out.println("Waiting in queue: " + service.getPendingQueue().size());
        report.printPendingTransactions();
        processAndShow(service);

        heading("PHASE 3: Failure cases (validation before any balance changes)");
        submit(service, "T017", Transaction.WITHDRAWAL, "ACC019", null, 5000);
        submit(service, "T018", Transaction.TRANSFER, "ACC007", "ACC001", 9000);
        submit(service, "T019", Transaction.DEPOSIT, null, "ACC003", -100);
        submit(service, "T020", Transaction.DEPOSIT, null, "ACC999", 500);
        submit(service, "T021", Transaction.TRANSFER, "ACC005", "ACC005", 100);
        submit(service, "T022", Transaction.WITHDRAWAL, "ACC888", null, 100);
        manager.findAccount("ACC020").closeAccount();
        submit(service, "T023", Transaction.DEPOSIT, null, "ACC020", 400);
        submit(service, "T024", Transaction.TRANSFER, "ACC001", "ACC020", 100);
        processAndShow(service);

        heading("PHASE 4: Duplicate transaction requests");
        submit(service, "T001", Transaction.DEPOSIT, null, "ACC001", 2000);
        submit(service, "T025", Transaction.DEPOSIT, null, "ACC013", 600);
        submit(service, "T025", Transaction.DEPOSIT, null, "ACC013", 600);
        processAndShow(service);

        heading("PHASE 5: Several requests hit ACC005 together (queue serialises them)");
        System.out.println("ACC005 balance before: " + manager.findAccount("ACC005").getBalance());
        submit(service, "T026", Transaction.WITHDRAWAL, "ACC005", null, 2500);
        submit(service, "T027", Transaction.WITHDRAWAL, "ACC005", null, 2500);
        submit(service, "T028", Transaction.WITHDRAWAL, "ACC005", null, 2500);
        submit(service, "T029", Transaction.TRANSFER, "ACC005", "ACC018", 1000);
        submit(service, "T030", Transaction.DEPOSIT, null, "ACC005", 300);
        processAndShow(service);
        System.out.println("ACC005 balance after: " + manager.findAccount("ACC005").getBalance());

        heading("PHASE 6: Cancellation of pending transactions");
        submit(service, "T031", Transaction.DEPOSIT, null, "ACC009", 800);
        submit(service, "T032", Transaction.WITHDRAWAL, "ACC009", null, 200);
        submit(service, "T033", Transaction.TRANSFER, "ACC009", "ACC010", 400);
        System.out.println("Cancel T032 (pending)          -> " + service.cancelTransaction("T032"));
        System.out.println("Cancel T033 (pending)          -> " + service.cancelTransaction("T033"));
        System.out.println("Cancel T001 (already processed) -> " + service.cancelTransaction("T001"));
        System.out.println("Cancel T999 (does not exist)    -> " + service.cancelTransaction("T999"));
        report.printPendingTransactions();
        processAndShow(service);

        heading("PHASE 7: Transfer fails after the debit, money is rolled back");
        double srcBefore = manager.findAccount("ACC011").getBalance();
        double dstBefore = manager.findAccount("ACC012").getBalance();
        System.out.println("Before: ACC011 = " + srcBefore + " | ACC012 = " + dstBefore);
        service.setSimulateCreditFailure(true);
        submit(service, "T034", Transaction.TRANSFER, "ACC011", "ACC012", 1000);
        processAndShow(service);
        double srcAfter = manager.findAccount("ACC011").getBalance();
        double dstAfter = manager.findAccount("ACC012").getBalance();
        System.out.println("After : ACC011 = " + srcAfter + " | ACC012 = " + dstAfter);
        System.out.println("Balances unchanged (no money lost): "
                + (srcBefore == srcAfter && dstBefore == dstAfter));

        heading("PHASE 8: More normal traffic");
        submit(service, "T035", Transaction.DEPOSIT, null, "ACC017", 1200);
        submit(service, "T036", Transaction.WITHDRAWAL, "ACC018", null, 1500);
        submit(service, "T037", Transaction.TRANSFER, "ACC019", "ACC001", 300);
        submit(service, "T038", Transaction.TRANSFER, "ACC013", "ACC004", 700);
        submit(service, "T039", Transaction.DEPOSIT, null, "ACC007", 900);
        submit(service, "T040", Transaction.WITHDRAWAL, "ACC002", null, 1200);
        submit(service, "T041", Transaction.TRANSFER, "ACC015", "ACC016", 1000);
        processAndShow(service);

        heading("PHASE 9: Reversal (stack: most recent successful transaction first)");
        System.out.println("Reversible operations on the stack: " + service.getReversibleStack().size());
        System.out.println("Top of stack: " + service.getReversibleStack().peek().getTransactionId());
        showReversal(service);
        showReversal(service);
        showReversal(service);

        submit(service, "T042", Transaction.DEPOSIT, null, "ACC016", 500);
        processAndShow(service);
        manager.findAccount("ACC016").closeAccount();
        System.out.println("ACC016 has been closed. Try to reverse T042:");
        showReversal(service);

        heading("PHASE 10: Reports");
        report.printAllAccounts();
        report.printAccountStatement("ACC001");
        report.printAccountStatement("ACC005");
        report.printAccountStatement("ACC011");
        report.printAccountStatement("ACC404");
        report.printCategoryRecords();
        report.printAuditReport();
        report.printPendingTransactions();

        heading("FINAL CONSISTENCY CHECK");
        verifyTotals(service, manager, openingTotal);
    }

    private static void heading(String text) {
        System.out.println();
        System.out.println("##################################################");
        System.out.println("# " + text);
        System.out.println("##################################################");
    }

    private static void submit(BankingService service, String id, String type,
                               String from, String to, double amount) {
        boolean accepted = service.submitTransaction(new Transaction(id, type, from, to, amount));
        String line = "Submit " + id + " " + type + " " + (from == null ? "-" : from)
                + " -> " + (to == null ? "-" : to) + " " + amount;
        if (accepted) {
            System.out.println(line + " : queued");
        } else {
            System.out.println(line + " : REJECTED (duplicate ID, recorded as Failed)");
        }
    }

    private static void processAndShow(BankingService service) {
        System.out.println("--- processing queue ---");
        while (!service.getPendingQueue().isEmpty()) {
            Transaction t = service.processNext();
            String line = "Processed " + t.getTransactionId() + " " + t.getType() + " "
                    + t.getAmount() + " -> " + t.getStatus();
            if (!t.getFailureReason().equals("")) {
                line = line + " (" + t.getFailureReason() + ")";
            }
            System.out.println(line);
        }
    }

    private static void showReversal(BankingService service) {
        Transaction top = service.getReversibleStack().peek();
        String id = top == null ? "none" : top.getTransactionId();
        String result = service.reverseLastTransaction();
        if (result.equals("")) {
            System.out.println("Reverse " + id + " -> done, status is now "
                    + top.getStatus());
        } else {
            System.out.println("Reverse " + id + " -> REFUSED: " + result);
        }
    }

    private static void verifyTotals(BankingService service, AccountManager manager, double openingTotal) {
        double expected = openingTotal;
        Transaction[] ok = service.getSuccessList().getAll();
        for (int i = 0; i < ok.length; i++) {
            if (ok[i].getType().equals(Transaction.DEPOSIT)) {
                expected += ok[i].getAmount();
            } else if (ok[i].getType().equals(Transaction.WITHDRAWAL)) {
                expected -= ok[i].getAmount();
            }
        }
        double actual = 0;
        Account[] all = manager.getAllAccounts();
        for (int i = 0; i < all.length; i++) {
            actual += all[i].getBalance();
        }
        System.out.println("Expected total: " + expected);
        System.out.println("Actual total  : " + actual);
        System.out.println(expected == actual ? "Result: CONSISTENT, no money created or lost"
                : "Result: DISCREPANCY FOUND");
    }
}