package org.jetbrains.kotlinx.spark.examples

import org.apache.spark.sql.Dataset
import org.jetbrains.kotlinx.spark.api.*
import org.jetbrains.kotlinx.spark.api.tuples.X
import org.jetbrains.kotlinx.spark.examples.GroupCalculation.getAllPossibleGroups
import scala.Tuple2
import kotlin.math.pow

/**
 * Gets all the possible, unique, non repeating groups of indices for a list.
 *
 * Example by Jolanrensen.
 */

fun main() = withSpark {
    val groupIndices = getAllPossibleGroups(listSize = 10, groupSize = 4)
        .sort("value")

    groupIndices.showDS(numRows = groupIndices.count().toInt())
}

object GroupCalculation {

    /**
     * Get all the possible, unique, non repeating groups (of size [groupSize]) of indices for a list of
     * size [listSize].
     *
     *
     * The workload is evenly distributed by [listSize] and [groupSize]
     *
     * @param listSize  the size of the list for which to calculate the indices
     * @param groupSize the size of a group of indices
     * @return all the possible, unique non repeating groups of indices
     */
    fun KSparkSession.getAllPossibleGroups(
        listSize: Int,
        groupSize: Int,
    ): Dataset<IntArray> {
        val indices = (0 until listSize).toList().toRDD() // Easy RDD creation!

        // for a groupSize of 1, no pairing up is needed, so just return the indices converted to IntArrays
        if (groupSize == 1) {
            return indices
                .mapPartitions {
                    it.map { intArrayOf(it) }
                }
                .toDS()
        }

        // this converts all indices to (number in table, index)
        val keys = indices.mapPartitions {

            // _1 is key (item in table), _2 is index in list
            it.transformAsSequence {
                flatMap { listIndex ->

                    // for each dimension loop over the other dimensions using addTuples
                    (0 until groupSize).asSequence().flatMap { dimension ->
                        addTuples(
                            groupSize = groupSize,
                            value = listIndex,
                            listSize = listSize,
                            skipDimension = dimension,
                        )
                    }
                }
            }
        }

        // Since we have a JavaRDD<Tuple2> we can aggregateByKey!
        // Each number in table occurs for each dimension as key.
        // The values of those two will be a tuple of (key, indices as list)
        val allPossibleGroups = keys.aggregateByKey(
            zeroValue = IntArray(groupSize) { -1 },
            seqFunc = { base: IntArray, listIndex: Int ->
                // put listIndex in the first empty spot in base
                base[base.indexOfFirst { it < 0 }] = listIndex

                base
            },

            // how to merge partially filled up int arrays
            combFunc = { a: IntArray, b: IntArray ->
                // merge a and b
                var j = 0
                for (i in a.indices) {
                    if (a[i] < 0) {
                        while (b[j] < 0) {
                            j++
                            if (j == b.size) return@aggregateByKey a
                        }
                        a[i] = b[j]
                        j++
                    }
                }
                a
            },
        )
            .values() // finally just take the values

        return allPossibleGroups.toDS()
    }

    /**
     * Simple method to give each index of x dimensions a unique number.
     *
     * @param indexTuple IntArray (can be seen as Tuple) of size x with all values < listSize. The index for which to return the number
     * @param listSize   The size of the list, aka the max width, height etc. of the table
     * @return the unique number for this [indexTuple]
     */
    private fun getTupleValue(indexTuple: List<Int>, listSize: Int): Int =
        indexTuple.indices.sumOf {
            indexTuple[it] * listSize.toDouble().pow(it).toInt()
        }


    /**
     * To make sure that every tuple is only picked once, this method returns true only if the indices are in the right
     * corner of the matrix. This works for any number of dimensions > 1. Here is an example for 2-D:
     *
     *
     * -  0  1  2  3  4  5  6  7  8  9
     * --------------------------------
     * 0| x  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
     * 1| x  x  ✓  ✓  ✓  ✓  ✓  ✓  ✓  ✓
     * 2| x  x  x  ✓  ✓  ✓  ✓  ✓  ✓  ✓
     * 3| x  x  x  x  ✓  ✓  ✓  ✓  ✓  ✓
     * 4| x  x  x  x  x  ✓  ✓  ✓  ✓  ✓
     * 5| x  x  x  x  x  x  ✓  ✓  ✓  ✓
     * 6| x  x  x  x  x  x  x  ✓  ✓  ✓
     * 7| x  x  x  x  x  x  x  x  ✓  ✓
     * 8| x  x  x  x  x  x  x  x  x  ✓
     * 9| x  x  x  x  x  x  x  x  x  x
     *
     * @param indexTuple a tuple of indices in the form of an IntArray
     * @return true if this tuple is in the right corner and should be included
     */
    private fun isValidIndexTuple(indexTuple: List<Int>): Boolean {
        // x - y > 0; 2d
        // (x - y) > 0 && (x - z) > 0 && (y - z) > 0; 3d
        // (x - y) > 0 && (x - z) > 0 && (x - a) > 0 && (y - z) > 0  && (y - a) > 0 && (z - a) > 0; 4d
        require(indexTuple.size >= 2) { "not a tuple" }
        for (i in 0 until indexTuple.size - 1) {
            for (j in i + 1 until indexTuple.size) {
                if (indexTuple[i] - indexTuple[j] <= 0) return false
            }
        }
        return true
    }

    /**
     * Recursive method that for [skipDimension] loops over all the other dimensions and returns all results from
     * [getTupleValue] as key and [value] as value.
     * In the end, the return value will have, for each key in the table below, a value for the key's column, row etc.
     *
     *
     * This is an example for 2D. The letters will be int indices as well (a = 0, b = 1, ..., [listSize]), but help for clarification.
     * The numbers we don't want are filtered out using [isValidIndexTuple].
     * The actual value of the number in the table comes from [getTupleValue].
     *
     *
     *
     *
     * -  a  b  c  d  e  f  g  h  i  j
     * --------------------------------
     * a| -  1  2  3  4  5  6  7  8  9
     * b| -  - 12 13 14 15 16 17 18 19
     * c| -  -  - 23 24 25 26 27 28 29
     * d| -  -  -  - 34 35 36 37 38 39
     * e| -  -  -  -  - 45 46 47 48 49
     * f| -  -  -  -  -  - 56 57 58 59
     * g| -  -  -  -  -  -  - 67 68 69
     * h| -  -  -  -  -  -  -  - 78 79
     * i| -  -  -  -  -  -  -  -  - 89
     * j| -  -  -  -  -  -  -  -  -  -
     *
     *
     * @param groupSize        the size of index tuples to form
     * @param value            the current index to work from (can be seen as a letter in the table above)
     * @param listSize         the size of the list to make
     * @param skipDimension    the current dimension that will have a set value [value] while looping over the other dimensions
     */
    private fun addTuples(
        groupSize: Int,
        value: Int,
        listSize: Int,
        skipDimension: Int,
    ): List<Tuple2<Int, Int>> {

        /**
         * @param currentDimension the indicator for which dimension we're currently calculating for (and how deep in the recursion we are)
         * @param indexTuple       the list (or tuple) in which to store the current indices
         */
        fun recursiveCall(
            currentDimension: Int = 0,
            indexTuple: List<Int> = emptyList(),
        ): List<Tuple2<Int, Int>> = when {
            // base case
            currentDimension >= groupSize ->
                if (isValidIndexTuple(indexTuple))
                    listOf(getTupleValue(indexTuple, listSize) X value)
                else
                    emptyList()

            currentDimension == skipDimension ->
                recursiveCall(
                    currentDimension = currentDimension + 1,
                    indexTuple = indexTuple + value,
                )

            else ->
                (0 until listSize).flatMap { i ->
                    recursiveCall(
                        currentDimension = currentDimension + 1,
                        indexTuple = indexTuple + i,
                    )
                }
        }

        return recursiveCall()
    }
}
