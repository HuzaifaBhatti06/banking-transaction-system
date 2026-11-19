# Banking Transaction System

A Java program that processes deposits, withdrawals, and transfers between bank accounts. It uses an array, a linked queue, doubly linked lists, and a stack to keep balances accurate, keep a full audit trail, and recover safely from errors.

## Course Information

- **University:** COMSATS University Islamabad, Abbottabad Campus
- **Course:** CSC211 Data Structures
- **Exam:** Lab Mid Term, Fall 26 (CLO-4)
- **Class:** BCS-3A
- **Student:** Muhammad Huzaifa Bhatti (FA25-BCS-012)

## Features

- Create accounts and search them by account number
- Queue-based processing of deposits, withdrawals, and transfers in arrival order (FIFO)
- Validation before any balance changes (amount, account exists and is active, sufficient funds, no transfer to the same account)
- Duplicate transaction detection
- Cancellation of pending transactions
- Safe transfer recovery: if the credit fails after the debit, the amount is returned to the source (rollback)
- Reversal of the most recent successful transaction, only when the balances allow it
- Separate records of successful, failed, cancelled, reversed, and pending transactions
- Account statements (newest first) and an audit report with a consistency check

## Data Structures Used

| Need | Structure | Why |
|---|---|---|
| Customer accounts | Array | Simple and fast to read, searched by account number |
| Pending transactions | Linked queue | Grows as needed at peak hours, so it cannot overflow like a circular queue |
| Transaction history and category records | Doubly linked list | Unlinking a found node is O(1) because of the previous pointer, and statements can be read newest first |
| Recent reversible transactions | Stack | The most recent successful operation is reversed first (LIFO) |

All data structures are written from scratch, with no built-in Java collections.

## Architecture

The code is separated into layers. Upper layers use lower ones, never the reverse.

```
src/main/java/com/bankingsystem/
├── model/            Account, Transaction (data and their own rules)
├── datastructure/    LinkedQueue, DoublyLinkedList, LinkedStack
├── service/          AccountManager, TransactionValidator, BankingService
├── report/           ReportGenerator (read-only statements and audit report)
└── app/              Main (the simulation)
docs/                 Requirements and design decisions
.vscode/              Tells VS Code where the source folders are
```

- **BankingService** is the only class that sets transaction statuses and moves money.
- **TransactionValidator** only checks rules and never changes anything.
- **ReportGenerator** only reads data and never changes anything.

## How to Run

1. Install a recent JDK (17 or newer) and VS Code with the Extension Pack for Java.
2. Clone the repository and open the folder in VS Code.
3. Open `src/main/java/com/bankingsystem/app/Main.java` and click **Run**.

The simulation prints every step to the console.

## Simulation Coverage

`Main` runs 20 accounts and more than 40 transactions in ten phases, covering:

- Deposits, withdrawals, and transfers
- Insufficient funds and other validation failures
- Duplicate transaction requests
- Several requests on one account (handled one at a time by the queue)
- Cancellation of pending transactions
- A failed transfer with rollback
- Reversal, including a refused reversal
- Reports and a final check that no money was created or lost

## Limitations and Future Work

- The account array has a fixed capacity. A tree or hash table would suit a much larger bank.
- Requests are processed one at a time, so there is no real multi-threaded concurrency.
- Rollback handles a failed credit, not a program crash between the debit and the credit. A transaction log would cover that.
- Reversal works on the most recent successful transaction only, because it uses a stack.
- Cancelled requests stay in the queue until they reach the front (lazy cancellation).
- There are no automated unit tests yet. The simulation in `Main` is the test run.

## Project Status

- [x] Phase 0: Project foundation
- [x] Phase 1: Requirements and design
- [x] Phase 2: Domain model
- [x] Phase 3: Account storage and search
- [x] Phase 4: Transaction queue
- [x] Phase 5: Validation and processing engine
- [x] Phase 6: History and categorized records
- [x] Phase 7: Cancellation, recovery, and reversal
- [x] Phase 8: Reports and simulation
- [ ] Phase 9: Automated tests and written evaluation report (not in this repository)