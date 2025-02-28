package com.phasmidsoftware.dsaipg.util;
import com.phasmidsoftware.dsaipg.adt.fibonacciHeap.FibonacciHeap;
import com.phasmidsoftware.dsaipg.adt.fourAryHeap.FourAryHeap;
import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public class HeapBenchmark {
    private static final int[] M_VALUES = {4095, 8191, 16383, 32767}; // Different heap sizes
    private static final Map<String, List<Double>> benchmarkResults = new HashMap<>();

    public static void main(String[] args) {
        for (int M : M_VALUES) {
            int insertions = M * 4; // Maintain insert/remove ratio
            int removals = insertions / 4;

            System.out.println("\n=== Benchmarking for M = " + M + " ===");

            benchmark("Binary Heap", M, insertions, removals,
                    () -> new PriorityQueue<Integer>(insertions, false, Comparator.naturalOrder(), false));
            benchmark("Binary Heap with Floyd's Trick", M, insertions, removals,
                    () -> new PriorityQueue<Integer>(insertions, false, Comparator.naturalOrder(), true));
            benchmark("4-ary Heap", M, insertions, removals,
                    () -> new FourAryHeap<Integer>(insertions, false, Comparator.naturalOrder()));
            benchmark("4-ary Heap with Floyd's Trick", M, insertions, removals,
                    () -> new FourAryHeap<>(generateRandomArray(insertions), false, Comparator.naturalOrder()));
            benchmark("Fibonacci Heap", M, insertions, removals,
                    () -> new FibonacciHeap<Integer>(Comparator.naturalOrder()));
        }

        generateLogLogPlot();
    }

    private static <T> void benchmark(String name, int M, int insertions, int removals, Supplier<T> heapSupplier) {
        Benchmark_Timer<T> timer = new Benchmark_Timer<>(name, heap -> {
            Random rand = new Random();
            Integer maxSpilled = null;

            for (int i = 0; i < insertions; i++) {
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

            for (int i = 0; i < removals; i++) {
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

            System.out.println(name + " (M = " + M + ") - Highest priority spilled element: " + maxSpilled);
        });

        double time = timer.runFromSupplier(heapSupplier, 10);
        benchmarkResults.computeIfAbsent(name, k -> new ArrayList<>()).add(time);
        System.out.println(name + " (M = " + M + ") execution time: " + time + " ms");
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
        System.out.println("\nLog-Log Plot Data:");
        for (Map.Entry<String, List<Double>> entry : benchmarkResults.entrySet()) {
            String name = entry.getKey();
            List<Double> times = entry.getValue();
            for (int i = 0; i < M_VALUES.length; i++) {
                double logN = Math.log(M_VALUES[i] * 4); // Log of number of insertions
                double logTime = Math.log(times.get(i));
                System.out.println(name + ": ( " + logN + " , " + logTime + " )");
            }
        }
    }
}