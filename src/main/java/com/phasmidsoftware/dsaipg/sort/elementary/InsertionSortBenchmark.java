package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.util.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.Config;

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;
import java.util.function.Consumer;

/**
 * This class benchmarks the InsertionSortComparator implementation
 * using four different array orderings:
 *  - Random
 *  - Ordered
 *  - Partially Ordered
 *  - Reverse Ordered
 *
 * The benchmark uses a doubling strategy for the input size and runs
 * each configuration a fixed number of repetitions.
 */
public class InsertionSortBenchmark {

    public static void main(String[] args) throws IOException {
        // Load configuration settings (including instrumentation if enabled)
        Config config = Config.load(InsertionSortBenchmark.class);

        // Benchmark parameters
        int initialN = 1000;   // Starting array size
        int numDoubles = 5;    // How many times to double the array size
        int repetitions = 10;  // Number of repetitions for each input size

        // The four ordering types to test
        String[] orderings = {"Random", "Ordered", "Partially Ordered", "Reverse Ordered"};

        // Define the sort operation using the static sort method of InsertionSortComparator.
        // This Consumer sorts the array in place.
        Consumer<Integer[]> sortOperation = InsertionSortComparator::sort;

        System.out.println("Benchmarking InsertionSortComparator using different array orderings:");
        // Loop over each ordering type.
        for (String ordering : orderings) {
            System.out.println("----------------------------------------------------");
            System.out.println("Benchmark for ordering: " + ordering);
            int n = initialN;
            for (int i = 0; i < numDoubles; i++) {
                final int size = n;
                // Create a Supplier that generates an Integer array with the given ordering and size.
                Supplier<Integer[]> supplier = () -> createArray(ordering, size);

                // Create a Benchmark_Timer that times the sortOperation.
                Benchmark_Timer<Integer[]> timer =
                        new Benchmark_Timer<>("InsertionSort " + ordering + " n=" + size, sortOperation);

                // Run the benchmark (including warmup) and get the average time (in ms).
                double avgTime = timer.runFromSupplier(supplier, repetitions);

                System.out.printf("n = %d, average time = %.5f ms%n", size, avgTime);
                //For csv data
//                System.out.printf("%d\t%.5f%n", size, avgTime);

                // Double the input size for the next iteration.
                n *= 2;
            }
            System.out.println();
        }
    }

    /**
     * Generates an Integer array of the specified size and ordering.
     *
     * @param ordering one of "Random", "Ordered", "Partially Ordered", or "Reverse Ordered"
     * @param n        the size of the array to generate
     * @return an Integer[] array with the desired ordering
     */
    private static Integer[] createArray(String ordering, int n) {
        Integer[] arr = new Integer[n];
        switch (ordering) {
            case "Random": {
                Random rand = new Random();
                for (int j = 0; j < n; j++) {
                    arr[j] = rand.nextInt(n);
                }
                break;
            }
            case "Ordered": {
                for (int j = 0; j < n; j++) {
                    arr[j] = j;
                }
                break;
            }
            case "Partially Ordered": {
                // Start with an ordered array.
                for (int j = 0; j < n; j++) {
                    arr[j] = j;
                }
                // Introduce some disorder by swapping every 10th element with its predecessor.
                for (int j = 1; j < n; j += 10) {
                    int tmp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = tmp;
                }
                break;
            }
            case "Reverse Ordered": {
                for (int j = 0; j < n; j++) {
                    arr[j] = n - j-1;
                }
                break;
            }
            default: {
                // Default to an ordered array if the ordering is unknown.
                for (int j = 0; j < n; j++) {
                    arr[j] = j;
                }
            }
        }
        return arr;
    }
}
