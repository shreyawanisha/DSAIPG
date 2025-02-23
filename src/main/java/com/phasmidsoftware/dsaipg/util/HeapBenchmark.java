package com.phasmidsoftware.dsaipg.util;
import com.phasmidsoftware.dsaipg.adt.fibonacciHeap.FibonacciHeap;
import com.phasmidsoftware.dsaipg.adt.fourAryHeap.FourAryHeap;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class HeapBenchmark {
    private static final int M = 4095;
    private static final int INSERTIONS = 16000;
    private static final int REMOVALS = 4000;
    private static final Map<String, Double> benchmarkResults = new HashMap<>();

    public static void main(String[] args) {
        benchmark("Binary Heap", () -> new PriorityQueue<Integer>(INSERTIONS, false, Comparator.naturalOrder(), false));
        benchmark("Binary Heap with Floyd's Trick", () -> new PriorityQueue<Integer>(INSERTIONS, false, Comparator.naturalOrder(), true));
        benchmark("4-ary Heap", () -> new FourAryHeap<Integer>(INSERTIONS, false, Comparator.naturalOrder()));
        benchmark("4-ary Heap with Floyd's Trick", () -> new FourAryHeap<>(generateRandomArray(INSERTIONS), false, Comparator.naturalOrder()));
        benchmark("Fibonacci Heap", () -> new FibonacciHeap<Integer>(Comparator.naturalOrder()));
        generateLogLogPlot();
    }

    private static <T> void benchmark(String name, Supplier<T> heapSupplier) {
        Benchmark_Timer<T> timer = new Benchmark_Timer<>(name, heap -> {
            Random rand = new Random();
            Integer maxSpilled = null;
            for (int i = 0; i < INSERTIONS; i++) {
                int num = rand.nextInt(100000);
                if (heap instanceof PriorityQueue) ((PriorityQueue<Integer>) heap).give(num);
                else if (heap instanceof FourAryHeap) ((FourAryHeap<Integer>) heap).give(num);
                else if (heap instanceof FibonacciHeap) ((FibonacciHeap<Integer>) heap).insert(num);

                if (((heap instanceof PriorityQueue && ((PriorityQueue<?>) heap).size() > M) ||
                        (heap instanceof FourAryHeap && ((FourAryHeap<?>) heap).getSize() > M) ||
                        (heap instanceof FibonacciHeap && ((FibonacciHeap<?>) heap).size() > M))) {
                    int removed;
                    if (heap instanceof PriorityQueue) {
                        try {
                            removed = ((PriorityQueue<Integer>) heap).take();
                        } catch (PQException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else if (heap instanceof FourAryHeap) removed = ((FourAryHeap<Integer>) heap).take();
                    else removed = ((FibonacciHeap<Integer>) heap).extractMin();
                    if (maxSpilled == null || removed > maxSpilled) maxSpilled = removed;
                }
            }

            for (int i = 0; i < REMOVALS; i++) {
                if (heap instanceof PriorityQueue) {
                    try {
                        ((PriorityQueue<Integer>) heap).take();
                    } catch (PQException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (heap instanceof FourAryHeap) ((FourAryHeap<Integer>) heap).take();
                else if (heap instanceof FibonacciHeap) ((FibonacciHeap<Integer>) heap).extractMin();
            }
            System.out.println(name + " - Highest priority spilled element: " + maxSpilled);
        });

        double time = timer.runFromSupplier(heapSupplier, 10);
        benchmarkResults.put(name, time);
        System.out.println(name + " execution time: " + time + " ms");
    }

    private static Integer[] generateRandomArray(int size) {
        Random rand = new Random();
        Integer[] array = new Integer[size];
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(100000);
        }
        return array;
    }

    private static void generateLogLogPlot() {
        List<Double> xValues = new ArrayList<>(); // Log(N)
        List<Double> yValues = new ArrayList<>(); // Log(time)
        for (Map.Entry<String, Double> entry : benchmarkResults.entrySet()) {
            xValues.add(Math.log(INSERTIONS));
            yValues.add(Math.log(entry.getValue()));
        }
        System.out.println("Log-Log Plot Data:");
        for (int i = 0; i < xValues.size(); i++) {
            System.out.println("( " + xValues.get(i) + " , " + yValues.get(i) + " )");
        }
    }
}
