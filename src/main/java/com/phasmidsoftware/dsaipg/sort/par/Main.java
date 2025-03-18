/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * CONSIDER tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
        int size = 5000000;
        processArgs(args);
        int[] threadCounts = {1, 2, 4, 6, 8, 10}; // Varying thread counts
        Random random = new Random();

        int[] array = new int[size];
        ArrayList<Long> timeList = new ArrayList<>();

        for (int threadCount : threadCounts) {
            System.out.println("Testing with " + threadCount + " threads.");

            ForkJoinPool pool = new ForkJoinPool(threadCount);

            for (int j = 50; j < 150; j++) {
                ParSort.cutoff = 10000 * (j + 1);
                long time;
                long startTime = System.currentTimeMillis();

                for (int t = 0; t < 10; t++) {
                    for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);

                    // Run sorting in the custom ForkJoinPool
                    pool.submit(() -> ParSort.sort(array, 0, array.length)).join();
                }

                long endTime = System.currentTimeMillis();
                time = (endTime - startTime);
                timeList.add(time);
                System.out.println("Threads: " + threadCount + " | Cutoff: " + ParSort.cutoff + " | Time: " + time + "ms");
            }

            pool.shutdown(); // Clean up the thread pool

        }
        // Save results to CSV
        saveResultsToCSV(timeList, size);
    }

    private static void saveResultsToCSV(ArrayList<Long> timeList, int size) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./src/result.csv", true)))) {
            int j = 0;
            for (long i : timeList) {
                String content = ((double) 10000 * (j + 1) / size) + "," + ((double) i / 10) + "\n";
                j++;
                bw.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("-N")) {
            setConfig(x, Integer.parseInt(y));
        } else if (x.equalsIgnoreCase("-P")) {
            System.out.println("Custom parallelism flag provided. Current parallelism: "
                    + ForkJoinPool.getCommonPoolParallelism());
        } else {
            System.err.println("Unknown x: " + x);
        }
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();


}