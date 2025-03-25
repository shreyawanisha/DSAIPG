/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.sort.linearithmic;

import com.phasmidsoftware.dsaipg.sort.Helper;
import com.phasmidsoftware.dsaipg.sort.SortException;
import com.phasmidsoftware.dsaipg.sort.SortWithComparableHelper;
import com.phasmidsoftware.dsaipg.sort.elementary.InsertionSort;
import com.phasmidsoftware.dsaipg.util.Config;

import java.util.Arrays;

import static com.phasmidsoftware.dsaipg.util.Config_Benchmark.*;

/**
 * Class MergeSort.
 *
 * @param <X> the underlying comparable type.
 */
public class MergeSort<X extends Comparable<X>> extends SortWithComparableHelper<X> {

    public static final String DESCRIPTION = "MergeSort";

    /**
     * Constructor for MergeSort
     * <p>
     * NOTE this is used only by unit tests, using its own instrumented helper.
     *
     * @param helper an explicit instance of Helper to be used.
     */
    public MergeSort(Helper<X> helper) {
        super(helper);
        insertionSort = setupInsertionSort(helper);
    }

    /**
     * Constructor for MergeSort
     *
     * @param N      the number elements we expect to sort.
     * @param nRuns  the expected number of runs.
     * @param config the configuration.
     */
    public MergeSort(int N, int nRuns, Config config) {
        super(DESCRIPTION + getConfigString(config), N, nRuns, config);
        insertionSort = setupInsertionSort(getHelper());
    }

    private InsertionSort<X> setupInsertionSort(final Helper<X> helper) {
        return new InsertionSort<>(helper.clone("MergeSort: insertion sort"));
    }

    public X[] sort(X[] xs, boolean makeCopy) {
        getHelper().init(xs.length);
        additionalMemory(xs.length);
        X[] result = makeCopy ? Arrays.copyOf(xs, xs.length) : xs;
        sort(result, 0, result.length);
        additionalMemory(-xs.length);
        return result;
    }

    public void sort(X[] a, int from, int to) {
        Config config = helper.getConfig();
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);
        // CONSIDER don't copy but just allocate according to the xs/aux interchange optimization
        @SuppressWarnings("unchecked") X[] aux = noCopy ? helper.copyArray(a) : (X[]) new Comparable[a.length];
        sort(a, aux, from, to);
    }

    private void sort(X[] a, X[] aux, int from, int to) {
        Config config = helper.getConfig();
        boolean insurance = config.getBoolean(MERGESORT, INSURANCE);
        boolean noCopy = config.getBoolean(MERGESORT, NOCOPY);

        // Base case: use insertion sort on small subarrays.
        if (to - from <= helper.cutoff()) {
            insertionSort.sort(a, from, to);
            return;
        }

        int mid = from + (to - from) / 2;

        if (noCopy) {
            // ----- FLIPPING VARIANT (no extra copy at every merge) -----
            // Note: In the flipping variant the roles of the two arrays are reversed at each level.
            // Start by recursing with swapped arrays.
            sort(aux, a, from, mid);
            sort(aux, a, mid, to);

            // Insurance optimization: if the two halves are already in order, simply copy
            if (insurance && !helper.less(a[mid], a[mid - 1])) {
                for (int i = from; i < to; i++) {
                    helper.copy(a[i], aux, i);
                }
                // No merge is needed.
                return;
            }
            // Merge sorted halves from 'a' into 'aux'.
            merge(a, aux, from, mid, to);

            // At the top level we require the final sorted result to reside in array 'a'.
            // (For an odd number of levels the result is left in 'aux'.)
            if (from == 0 && to == a.length) {
                for (int i = from; i < to; i++) {
                    helper.copy(aux[i], a, i);
                }
            }

        } else {
            // ----- NON-FLIPPING VARIANT (extra copy after each merge) -----
            // Recurse in the same direction: sort 'a' (using aux as temporary space).
            sort(a, aux, from, mid);
            sort(a, aux, mid, to);

            // Insurance optimization: if already in order, skip the merge.
            if (insurance && !helper.less(a[mid], a[mid - 1])) {
                return;
            }

            // Merge sorted halves from 'a' into 'aux'.
            merge(a, aux, from, mid, to);

            // Copy the merged result back from aux into a.
            for (int i = from; i < to; i++) {
                helper.copy(aux[i], a, i);
            }
        }
    }

    // CONSIDER combine with MergeSortBasic, perhaps.
    private void merge(X[] sorted, X[] result, int from, int mid, int to) {
        int i = from;
        int j = mid;
        X v = helper.get(sorted, i);
        X w = helper.get(sorted, j);
        for (int k = from; k < to; k++) {
            if (i >= mid) {
                helper.copy(w, result, k);
                if (++j < to) w = helper.get(sorted, j);
            } else if (j >= to) {
                helper.copy(v, result, k);
                if (++i < mid) v = helper.get(sorted, i);
            } else if (helper.less(w, v)) {
                helper.incrementFixes(mid - i);
                helper.copy(w, result, k);
                if (++j < to) w = helper.get(sorted, j);
            } else {
                helper.copy(v, result, k);
                if (++i < mid) v = helper.get(sorted, i);
            }
        }
    }

    public static final String MERGESORT = "mergesort";
    public static final String NOCOPY = "nocopy";
    public static final String INSURANCE = "insurance";

    private static String getConfigString(Config config) {
        StringBuilder stringBuilder = new StringBuilder();
        if (config.getBoolean(MERGESORT, INSURANCE)) stringBuilder.append(" with insurance comparison");
        if (config.getBoolean(MERGESORT, NOCOPY)) stringBuilder.append(" with no copy");
        int cutoff = config.getInt(HELPER, CUTOFF, CUTOFF_DEFAULT);
        if (cutoff != CUTOFF_DEFAULT) {
            if (cutoff == 1) stringBuilder.append(" with no cutoff");
            else stringBuilder.append(" with cutoff ").append(cutoff);
        }
        return stringBuilder.toString();
    }

    private final InsertionSort<X> insertionSort;


    private int arrayMemory = -1;
    private int additionalMemory;
    private int maxMemory;

    public void setArrayMemory(int n) {
        if (arrayMemory == -1) {
            arrayMemory = n;
            additionalMemory(n);
        }
    }

    public void additionalMemory(int n) {
        additionalMemory += n;
        if (maxMemory < additionalMemory) maxMemory = additionalMemory;
    }

    public Double getMemoryFactor() {
        if (arrayMemory == -1)
            throw new SortException("Array memory has not been set");
        return 1.0 * maxMemory / arrayMemory;
    }

}
