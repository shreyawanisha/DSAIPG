package com.phasmidsoftware.dsaipg.adt.fibonacciHeap;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import java.util.Comparator;
import java.util.NoSuchElementException;

public class FibonacciHeapTest {

    private FibonacciHeap<Integer> heap;

    @Before
    public void setUp() {
        heap = new FibonacciHeap<Integer>(Comparator.naturalOrder());
    }

    @Test
    public void testIsEmptyInitially() {
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
    }

    @Test
    public void testInsert() {
        heap.insert(10);
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
    }

    @Test
    public void testExtractMinSingleElement() {
        heap.insert(5);
        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testExtractMinMultipleElements() {
        heap.insert(10);
        heap.insert(5);
        heap.insert(20);
        heap.insert(3);

        assertEquals(Integer.valueOf(3), heap.extractMin());
        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testExtractMinFromEmptyHeap() {
        heap.extractMin();
    }

    @Test
    public void testSizeAfterInsertionsAndExtractions() {
        heap.insert(8);
        heap.insert(3);
        heap.insert(15);
        heap.insert(6);
        assertEquals(4, heap.size());

        heap.extractMin();
        assertEquals(3, heap.size());

        heap.extractMin();
        assertEquals(2, heap.size());

        heap.extractMin();
        assertEquals(1, heap.size());

        heap.extractMin();
        assertEquals(0, heap.size());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testExtractMinWithDuplicates() {
        heap.insert(5);
        heap.insert(3);
        heap.insert(3);
        heap.insert(7);

        assertEquals(Integer.valueOf(3), heap.extractMin());
        assertEquals(Integer.valueOf(3), heap.extractMin());
        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertEquals(Integer.valueOf(7), heap.extractMin());
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMin() {
        heap.insert(10);
        heap.insert(5);
        heap.insert(20);
        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
    }

    @Test
    public void testConsolidate() {
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);
        heap.insert(15);
        heap.insert(25);
        heap.insert(30);

        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(15), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
        assertEquals(Integer.valueOf(25), heap.extractMin());
        assertEquals(Integer.valueOf(30), heap.extractMin());
    }

    @Test
    public void testInsertMultipleElements() {
        for (int i = 1; i <= 100; i++) {
            heap.insert(i);
        }
        assertEquals(100, heap.size());
    }

    @Test
    public void testExtractMinWithManyElements() {
        for (int i = 1; i <= 100; i++) {
            heap.insert(i);
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals(Integer.valueOf(i), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMinWithRandomOrder() {
        int[] values = {10, 5, 20, 15, 30, 25};
        for (int value : values) {
            heap.insert(value);
        }
        assertEquals(Integer.valueOf(5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(15), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
        assertEquals(Integer.valueOf(25), heap.extractMin());
        assertEquals(Integer.valueOf(30), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithNegativeNumbers() {
        heap.insert(-10);
        heap.insert(-5);
        heap.insert(-20);
        assertEquals(Integer.valueOf(-20), heap.extractMin());
        assertEquals(Integer.valueOf(-10), heap.extractMin());
        assertEquals(Integer.valueOf(-5), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithMixedNumbers() {
        heap.insert(10);
        heap.insert(-5);
        heap.insert(20);
        heap.insert(-15);
        assertEquals(Integer.valueOf(-15), heap.extractMin());
        assertEquals(Integer.valueOf(-5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithLargeNumbers() {
        heap.insert(1000000);
        heap.insert(500000);
        heap.insert(2000000);
        assertEquals(Integer.valueOf(500000), heap.extractMin());
        assertEquals(Integer.valueOf(1000000), heap.extractMin());
        assertEquals(Integer.valueOf(2000000), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithSmallNumbers() {
        heap.insert(0);
        heap.insert(-1);
        heap.insert(1);
        assertEquals(Integer.valueOf(-1), heap.extractMin());
        assertEquals(Integer.valueOf(0), heap.extractMin());
        assertEquals(Integer.valueOf(1), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithZero() {
        heap.insert(0);
        heap.insert(10);
        heap.insert(-10);
        assertEquals(Integer.valueOf(-10), heap.extractMin());
        assertEquals(Integer.valueOf(0), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithSameValue() {
        heap.insert(10);
        heap.insert(10);
        heap.insert(10);
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithManyDuplicates() {
        for (int i = 0; i < 100; i++) {
            heap.insert(10);
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(Integer.valueOf(10), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMinWithManyNegativeNumbers() {
        for (int i = -1; i >= -100; i--) {
            heap.insert(i);
        }
        for (int i = -100; i <= -1; i++) {
            assertEquals(Integer.valueOf(i), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMinWithManyPositiveNumbers() {
        for (int i = 1; i <= 100; i++) {
            heap.insert(i);
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals(Integer.valueOf(i), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMinWithManyMixedNumbers() {
        int[] values = {10, -5, 20, -15, 30, -25};
        for (int value : values) {
            heap.insert(value);
        }
        assertEquals(Integer.valueOf(-25), heap.extractMin());
        assertEquals(Integer.valueOf(-15), heap.extractMin());
        assertEquals(Integer.valueOf(-5), heap.extractMin());
        assertEquals(Integer.valueOf(10), heap.extractMin());
        assertEquals(Integer.valueOf(20), heap.extractMin());
        assertEquals(Integer.valueOf(30), heap.extractMin());
    }

    @Test
    public void testInsertAndExtractMinWithManyLargeNumbers() {
        for (int i = 1000000; i <= 2000000; i += 100000) {
            heap.insert(i);
        }
        for (int i = 1000000; i <= 2000000; i += 100000) {
            assertEquals(Integer.valueOf(i), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }

    @Test
    public void testInsertAndExtractMinWithManySmallNumbers() {
        for (int i = 0; i >= -100; i--) {
            heap.insert(i);
        }
        for (int i = -100; i <= 0; i++) {
            assertEquals(Integer.valueOf(i), heap.extractMin());
        }
        assertTrue(heap.isEmpty());
    }
}