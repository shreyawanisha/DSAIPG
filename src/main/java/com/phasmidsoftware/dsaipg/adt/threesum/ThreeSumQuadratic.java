/*
 * Copyright (c) 2024. Robin Hillyard
 */

package com.phasmidsoftware.dsaipg.adt.threesum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ThreeSum which follows the approach of dividing the solution-space into
 * N sub-spaces where each sub-space corresponds to a fixed value for the middle index of the three values.
 * Each sub-space is then solved by expanding the scope of the other two indices outwards from the starting point.
 * Since each sub-space can be solved in O(N) time, the overall complexity is O(N^2).
 * <p>
 * NOTE: The array provided in the constructor MUST be ordered.
 */
public class ThreeSumQuadratic implements ThreeSum {
    /**
     * Construct a ThreeSumQuadratic on a.
     *
     * @param a a sorted array.
     */
    public ThreeSumQuadratic(int[] a) {
        this.a = a;
        length = a.length;
    }

    /**
     * Retrieves an array of unique Triples. Each Triple represents a unique combination of three integers from
     * the source array that sum to zero.
     *
     * @return an array of distinct Triples, sorted in natural order, where each Triple satisfies the condition that
     * the sum of its three integers is zero.
     */
    public Triple[] getTriples() {
        List<Triple> triples = new ArrayList<>();
        for (int i = 0; i < length; i++) triples.addAll(getTriples(i));
        Collections.sort(triples);
        return triples.stream().distinct().toArray(Triple[]::new);
    }

    /**
     * Get a list of Triples such that the middle index is the given value j.
     *
     * @param j the index of the middle value.
     * @return a Triple such that
     */
     List<Triple> getTriples(int j) {
         List<Triple> triples = new ArrayList<>();
         int target = -1 * a[j];

         int left = j+1;
         int right = length-1;
         while(left < right) {
             int sum = a[left] + a[right];
             if(sum == target) {
                 final Triple triple = sort(a[left], a[right], a[j]);
                 triples.add(triple);
                 left++;
                 right--;
             }
             else if(sum > target) {
                 right--;
             }else {
                 left++;
             }
         }
         return triples;
        // TO BE IMPLEMENTED  : for each candidate, test if a[i] + a[j] + a[k] = 0.
//        throw new RuntimeException("implementation missing");
    }

    private Triple sort(int smallest, int middle, int largest) {
         int[] arr = new int[3];
         arr[0] = smallest;
         arr[1] = middle;
         arr[2] = largest;

         Arrays.sort(arr);
         return new Triple(arr[0], arr[1], arr[2]);
    }

    private final int[] a;
    private final int length;
}