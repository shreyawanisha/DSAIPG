package com.phasmidsoftware.dsaipg.adt.fourAryHeap;

import java.util.*;

public class FourAryHeap<K> {
    private final boolean max;
    private int capacity;
    private final Comparator<K> comparator;
    private K[] heap;
    private int size;

    @SuppressWarnings("unchecked")
    public FourAryHeap(int capacity, boolean max, Comparator<K> comparator) {
        this.capacity = capacity;
        this.max = max;
        this.comparator = comparator;
        this.heap = (K[]) new Object[capacity + 1];
        this.size = 0;
    }

    public FourAryHeap(K[] elements, boolean max, Comparator<K> comparator) {
        this.capacity = elements.length;
        this.max = max;
        this.comparator = comparator;
        this.heap = Arrays.copyOf(elements, elements.length);
        this.size = elements.length;
        floydHeapify();
    }

    private int parent(int i) {
        return (i - 1) / 4;
    }

    private int child(int i, int k) {
        return 4 * i + k + 1;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getSize() {
        return size;
    }

    public void give(K key) {
        if (size >= capacity) {
            resizeHeap();  // Increase heap size if full
        }
        heap[size] = key;
        bubbleUp(size);
        size++;
    }

    private void resizeHeap() {
        int newCapacity = capacity * 2;
        heap = Arrays.copyOf(heap, newCapacity);
        capacity = newCapacity;
    }

    public K take() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        K root = heap[0];
        heap[0] = heap[--size];
        heap[size] = null;
        sinkDown(0);
        return root;
    }

    private void bubbleUp(int i) {
        while (i > 0 && compare(heap[i], heap[parent(i)])) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    private void sinkDown(int i) {
        while (true) {
            int bestChild = i;
            for (int k = 0; k < 4; k++) {
                int childIndex = child(i, k);
                if (childIndex < size && compare(heap[childIndex], heap[bestChild])) {
                    bestChild = childIndex;
                }
            }
            if (bestChild == i) break;
            swap(i, bestChild);
            i = bestChild;
        }
    }

    private void floydHeapify() {
        for (int i = (size - 2) / 4; i >= 0; i--) {
            sinkDown(i);
        }
    }

    private boolean compare(K a, K b) {
        return max ? comparator.compare(a, b) > 0 : comparator.compare(a, b) < 0;
    }

    private void swap(int i, int j) {
        K temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
