# Requirements and Design

## 1. Stakeholders

| Stakeholder | Needs from the system |
|---|---|
| Customer | Fast, correct deposits, withdrawals, and transfers; accurate balance; ability to cancel a pending request |
| Bank teller | Submit transactions at the counter; look up accounts quickly by account number |
| Account manager | Create and manage accounts; view account statements |
| Auditor | Complete, ordered record of completed, failed, and reversed transactions; nothing deleted silently |
| Banking administrator | Reverse eligible transactions; handle failures; review recent reversible actions |

**Conflict (CCPS-4):** Customers want speed, while auditors want careful records. The system accepts a little extra work per transaction to keep a full audit trail.

## 2. Account Information

| Field | Purpose |
|---|---|
| Account number | Unique identifier; used for searching |
| Holder name | Identifies the customer |
| Balance | Current money; changes only through validated transactions |
| Status (Active / Closed) | Closed accounts cannot send or receive transactions |

## 3. Transaction Types and Fields

- **Deposit:** adds money to one account (destination only).
- **Withdrawal:** removes money from one account (source only).
- **Transfer:** moves money from a source account to a destination account.

| Field | Notes |
|---|---|
| Transaction ID | Unique; used to detect duplicates |
| Type | Deposit, Withdrawal, or Transfer |
| Source account | Not used for deposits |
| Destination account | Used for deposits and transfers only |
| Amount | Must be greater than zero |
| Timestamp | When the request was submitted |
| Status | See section 4 |

## 4. Transaction Statuses

Pending, Successful, Failed, Cancelled, Reversed.

- A new transaction starts as **Pending**.
- Pending becomes **Successful**, **Failed**, or **Cancelled**.
- Only a Successful transaction can become **Reversed**.

## 5. Validation Rules (checked before any balance changes)

1. The transaction ID must not already exist (duplicate check).
2. The amount must be greater than zero.
3. The source account (if used) must exist and be active.
4. The destination account (if used) must exist and be active.
5. For withdrawals and transfers, balance must be at least the amount (insufficient funds check).
6. A transfer's source and destination must be different accounts.

## 6. Banking Constraints

- Balances are never negative.
- Transactions are processed in arrival order (first in, first out).
- Only Pending transactions can be cancelled.
- A Successful transaction can be reversed only if its status is Successful, it has not already been reversed, and the account that must give money back has enough balance.
- Every success, failure, cancellation, and reversal is recorded for audit.

## 7. Transfer Failure Recovery

A transfer is done in two steps: debit the source, then credit the destination. If the credit fails, the system immediately returns the money to the source account (**rollback**), marks the transfer Failed, and records the reason. To make this safe, both accounts are validated before any money moves.

## 8. Data-Structure Decisions

| Need | Structure | Justification |
|---|---|---|
| Customer accounts | Array | Simple, fast access; searched by account number |
| Pending transactions | Linked queue | Peak hours can make the queue grow, and a linked queue has no fixed size; a circular queue overflows when full. Cancellation also needs removal of a specific item. |
| Transaction history | Doubly linked list | Deleting or moving from a known node is faster than in a singly linked list, which needs the previous node. |
| Categorised records | Separate linked lists | Successful, Failed, Cancelled, and Reversed each have their own list. |
| Recent reversible operations | Stack | The most recent action is reversed first (last in, first out). |

**Reviewing vs reversing:** Reviewing only looks at a transaction and changes nothing. Reversing changes balances, so it needs validation and an audit record.

**Not used:** A tree could speed up account search for very large banks. It is recommended in the evaluation report for future scaling, but is not needed for this system's size.