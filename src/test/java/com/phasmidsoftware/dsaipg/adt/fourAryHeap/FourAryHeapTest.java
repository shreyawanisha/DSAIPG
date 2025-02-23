package com.phasmidsoftware.dsaipg.adt.fourAryHeap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class FourAryHeapTest {

    private FourAryHeap<Integer> maxHeap;
    private FourAryHeap<Integer> minHeap;

    @Before
    public void setUp() {
        maxHeap = new FourAryHeap<Integer>(10, true, Comparator.naturalOrder());
        minHeap = new FourAryHeap<Integer>(10, false, Comparator.naturalOrder());
    }

    @Test
    public void testIsEmpty() {
        assertTrue(maxHeap.isEmpty());
        maxHeap.give(10);
        assertFalse(maxHeap.isEmpty());
    }

    @Test
    public void testGetSize() {
        assertEquals(0, maxHeap.getSize());
        maxHeap.give(10);
        assertEquals(1, maxHeap.getSize());
    }

    @Test
    public void testGive() {
        maxHeap.give(10);
        maxHeap.give(20);
        maxHeap.give(5);
        assertEquals(3, maxHeap.getSize());
    }

    @Test
    public void testTakeMaxHeap() {
        maxHeap.give(10);
        maxHeap.give(20);
        maxHeap.give(5);
        assertEquals(Integer.valueOf(20), maxHeap.take());
        assertEquals(Integer.valueOf(10), maxHeap.take());
        assertEquals(Integer.valueOf(5), maxHeap.take());
    }

    @Test
    public void testTakeMinHeap() {
        minHeap.give(10);
        minHeap.give(20);
        minHeap.give(5);
        assertEquals(Integer.valueOf(5), minHeap.take());
        assertEquals(Integer.valueOf(10), minHeap.take());
        assertEquals(Integer.valueOf(20), minHeap.take());
    }

    @Test(expected = NoSuchElementException.class)
    public void testTakeHeapEmpty() {
        maxHeap.take();
    }
}