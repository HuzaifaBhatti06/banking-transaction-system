package com.bankingsystem.datastructure;

import com.bankingsystem.model.Transaction;

public class LinkedStack {

    private class Node {
        Transaction data;
        Node next;

        Node(Transaction data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node top;
    private int size;

    public LinkedStack() {
        top = null;
        size = 0;
    }

    public void push(Transaction t) {
        Node newNode = new Node(t);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public Transaction pop() {
        if (top == null) {
            return null;
        }
        Node temp = top;
        top = top.next;
        temp.next = null;
        size--;
        return temp.data;
    }

    public Transaction peek() {
        if (top == null) {
            return null;
        }
        return top.data;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}