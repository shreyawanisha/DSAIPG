package com.phasmidsoftware.dsaipg.adt.fibonacciHeap;

import java.util.*;

public class FibonacciHeap<K> {
    private static class Node<K> {
        K key;
        Node<K> parent, child, left, right;
        int degree;
        boolean marked;

        Node(K key) {
            this.key = key;
            this.left = this.right = this;
        }
    }

    private Node<K> min;
    private int size;
    private final Comparator<K> comparator;

    public FibonacciHeap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public boolean isEmpty() {
        return min == null;
    }

    public int size() {
        return size;
    }

    public void insert(K key) {
        Node<K> node = new Node<>(key);
        if (min == null) {
            min = node;
        } else {
            linkNodes(min, node);
            if (comparator.compare(node.key, min.key) < 0) {
                min = node;
            }
        }
        size++;
    }

    public K extractMin() {
        if (min == null) throw new NoSuchElementException("Heap is empty");
        Node<K> oldMin = min;
        if (min.child != null) {
            Node<K> child = min.child;
            do {
                Node<K> next = child.right;
                linkNodes(min, child);
                child.parent = null;
                child = next;
            } while (child != min.child);
        }
        removeNode(min);
        size--;
        if (size == 0) {
            min = null;
        } else {
            min = oldMin.right;
            consolidate();
        }
        return oldMin.key;
    }

    private void consolidate() {
        Map<Integer, Node<K>> degreeTable = new HashMap<>();
        List<Node<K>> nodes = new ArrayList<>();
        Node<K> current = min;
        if (current != null) {
            do {
                nodes.add(current);
                current = current.right;
            } while (current != min);
        }

        for (Node<K> node : nodes) {
            int degree = node.degree;
            while (degreeTable.containsKey(degree)) {
                Node<K> other = degreeTable.get(degree);
                if (comparator.compare(other.key, node.key) < 0) {
                    Node<K> temp = node;
                    node = other;
                    other = temp;
                }
                linkChild(node, other);
                degreeTable.remove(degree);
                degree++;
            }
            degreeTable.put(degree, node);
        }
        min = null;
        for (Node<K> node : degreeTable.values()) {
            if (min == null || comparator.compare(node.key, min.key) < 0) {
                min = node;
            }
        }
    }

    private void linkChild(Node<K> parent, Node<K> child) {
        removeNode(child);
        child.left = child.right = child;
        if (parent.child == null) {
            parent.child = child;
        } else {
            linkNodes(parent.child, child);
        }
        child.parent = parent;
        parent.degree++;
        child.marked = false;
    }

    private void linkNodes(Node<K> a, Node<K> b) {
        b.left = a;
        b.right = a.right;
        a.right.left = b;
        a.right = b;
    }

    private void removeNode(Node<K> node) {
        node.left.right = node.right;
        node.right.left = node.left;
    }
}
