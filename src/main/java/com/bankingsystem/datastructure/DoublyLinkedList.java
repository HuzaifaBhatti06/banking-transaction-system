package com.bankingsystem.datastructure;

import com.bankingsystem.model.Transaction;

public class DoublyLinkedList {

    
    private class Node {
        Transaction data;
        Node next;
        Node prev;

        Node(Transaction data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    private Node head;   
    private Node tail;  
    private int size;

    public DoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void addLast(Transaction t) {
        Node newNode = new Node(t);

        if (head == null) {
          
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;   
            newNode.prev = tail;    
            tail = newNode;        
        }

        size++;
    }

    public Transaction findById(String id) {
        Node current = head;

        while (current != null) {
            if (current.data.getTransactionId().equals(id)) {
                return current.data;
            }
            current = current.next;
        }

        return null;
    }

    public Transaction removeById(String id) {
        Node current = head;

        while (current != null) {
            if (current.data.getTransactionId().equals(id)) {

                if (current.prev == null) {
                  
                    head = current.next;
                } else {
                    current.prev.next = current.next;
                }

                if (current.next == null) {
                 
                    tail = current.prev;
                } else {
                    current.next.prev = current.prev;
                }

                size--;
                current.next = null;
                current.prev = null;
                return current.data;
            }
            current = current.next;
        }

        return null;
    }

    public Transaction[] getAll() {
        Transaction[] result = new Transaction[size];
        Node current = head;
        int index = 0;

        while (current != null) {
            result[index] = current.data;
            index++;
            current = current.next;
        }

        return result;
    }

    
    public Transaction[] getAllReverse() {
        Transaction[] result = new Transaction[size];
        Node current = tail;
        int index = 0;

        while (current != null) {
            result[index] = current.data;
            index++;
            current = current.prev;
        }

        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return head == null;
    }
}