# Banking Transaction System

A Java program that processes deposits, withdrawals, and transfers between bank accounts, using arrays, linked lists, queues, and stacks to keep balances accurate and errors recoverable.

## Course Information

- **University:** COMSATS University Islamabad, Abbottabad Campus
- **Course:** CSC211 Data Structures
- **Exam:** Lab Mid Term, Fall 26 (CLO-4)
- **Class:** BCS-3A
- **Student:** Muhammad Huzaifa Bhatti (FA25-BCS-012)

## Features

- Create and search customer accounts by account number
- Queue-based processing of deposits, withdrawals, and transfers
- Validation checks before any balance changes
- Cancellation of pending transactions
- Safe recovery when a transfer fails halfway
- Separate records of successful, failed, pending, and reversed transactions
- Stack of recent reversible operations, with rules for when reversal is allowed
- Account statements and audit reports

## Data Structures Used

To be decided and justified in Phase 1 (design).

## Project Structure

```
src/main/java/com/bankingsystem/
├── model/            Account, Transaction, and their status and type labels
├── datastructure/    Custom queue, linked list, and stack
├── service/          Banking rules: deposit, withdraw, transfer, validate, cancel, reverse
├── exception/        Custom error types
├── report/           Account statements and audit reports
└── app/              Program entry point and simulation
src/test/java/        Automated test cases
config/               Settings kept out of the code
docs/                 Requirements, design, analysis, and evaluation report
```

## How to Run

Coming soon.

## Project Status

- [x] Phase 0: Project foundation
- [ ] Phase 1: Requirements and design
- [ ] Phase 2: Domain model
- [ ] Phase 3: Account storage and search
- [ ] Phase 4: Transaction queue
- [ ] Phase 5: Validation and processing engine
- [ ] Phase 6: History and categorized records
- [ ] Phase 7: Cancellation, recovery, and reversal
- [ ] Phase 8: Reports and simulation
- [ ] Phase 9: Tests and evaluation report