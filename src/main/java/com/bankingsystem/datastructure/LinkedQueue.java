package com.bankingsystem.datastructure;

import com.bankingsystem.model.Transaction;

public class LinkedQueue {
    private class Node {
        Transaction data;
        Node next;

        private Node(Transaction data) {
            this.data = data;
            this.next = null;
        }

    }

    private Node front;
    private Node rear;
    private int size;

    public LinkedQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    public void enqueue(Transaction T) {
        Node newNode = new Node(T);
        if (rear == null && front == null) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }

    public Transaction dequeue() 
    {
        if (front == null) 
        {
            return null;
        }
        Node temp = front;
        front = front.next;
        size--;
        temp.next = null;
        if (front == null) 
        {
            rear = null;
        }
        return temp.data;
    }

    public Transaction peek()
    {
        if(front == null)
        {
            return null;
        }   
        return front.data;
    }


    public boolean isEmpty()
    {
    return front == null;
    }

    public int size() {
        return size;
    }
}