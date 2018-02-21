/*
 PQ Kernel 1.0

 This code exhibits the algorithm the computes the PQ kernel in O(n log n) time.

 Original Authors
 Copyright (C) 2014  Radu Tudor Ionescu, Marius Popescu

 Java implementation
 Copyright (C) 2017  Butnaru Andrei-Madalin

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or any
 later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package shotgunwsd.relatedness.kernel.method;

public class PQKernel {
    public static double compute(double[] A, double[] B) {
        return pqk(A, B);
    }

    public static double pqk(double[] A, double[] B) {
        return pqk(A, B, A.length);
    }

    /* The two input parameters A and B are two samples (or histograms).
       Function will return the PQ kernel between the two samples.
     */
    public static double pqk(double[] A, double[] B, int n) {
        long s, n0, n1, n2, n3, t;
        int i, j, k, l;
        double rez;
        double[] a = A.clone(),
                b = B.clone(),
                tmp_array_1 = new double[n],
                tmp_array_2 = new double[n];

        merge_sort2(a, b, tmp_array_1, tmp_array_2, 0, n - 1);

        // Compute the total number of pairs
        n0 = (n * (n - 1)) >> 1;

        // Compute the number of equal pairs in a
        i = 0;
        n1 = 0;
        while (i < n - 1)
        {
            t = 1;
            j = i + 1;
            while (j < n - 1 && a[j] == a[i]) {j++; t++;}
            n1 += (t * (t - 1)) >> 1;
            i = j;
        }

        // Compute the number of equal pairs in a and b
        i = 0;
        n3 = 0;
        while (i < n - 1)
        {
            t = 1;
            j = i + 1;
            while (j < n - 1 && (a[j] == a[i]) && (b[j] == b[i])) {j++; t++;}
            n3 += (t * (t - 1)) >> 1;
            i = j;
        }

        // Compute the number of discordant pairs
        s = merge_sort1(b, tmp_array_2, 0, n - 1);

        // Compute the number of equal pairs in b
        i = 0;
        n2 = 0;
        while (i < n - 1)
        {
            t = 1;
            j = i + 1;
            while (j < n - 1 && b[j] == b[i]) {j++; t++;}
            n2 += (t * (t - 1)) >> 1;
            i = j;
        }

        rez = 2 * (double)(n0 + n3 - n1 - n2 - (2 * s));

        return rez;
    }

    private static void merge_sort2(double[] a, double[] b, double[] tmp_array_1, double[] tmp_array_2, int left, int right) {
        int i, j, k, center;

        if(left < right) {
            center = (left + right) >> 1;

            merge_sort2(a, b, tmp_array_1, tmp_array_2, left, center);
            merge_sort2(a, b, tmp_array_1, tmp_array_2, center + 1, right);

            for (i = center + 1; i > left; i--)
            {
                tmp_array_1[i - 1] = a[i - 1];
                tmp_array_2[i - 1] = b[i - 1];
            }
            for (j = center; j < right; j++)
            {
                tmp_array_1[right + center - j] = a[j + 1];
                tmp_array_2[right + center - j] = b[j + 1];
            }
            for (k = left; k <= right; k++) {
                if ((tmp_array_1[i] < tmp_array_1[j]) || ((tmp_array_1[i] == tmp_array_1[j]) && (tmp_array_2[i] < tmp_array_2[j]))) {
                    a[k] = tmp_array_1[i];
                    b[k] = tmp_array_2[i];
                    i++;
                } else {
                    a[k] = tmp_array_1[j];
                    b[k] = tmp_array_2[j];
                    j--;
                }
            }
        }
    }

    private static long merge_sort1(double[] a, double[] tmp_array, int left, int right) {
        int i, j, k, center;
        long sl, sr, m;

        if (left < right)
        {
            center = (left + right) >> 1;
            sl = merge_sort1(a, tmp_array, left, center);
            sr = merge_sort1(a, tmp_array, center + 1, right);

            m = 0;
            i = left;
            j = center + 1;
            while ((i <= center) && (j <= right))
            {
                if (a[j] < a[i])
                {
                    m += center + 1 - i;
                    j++;
                }
                else
                {
                    i++;
                }
            }
            for (i = center + 1; i > left; i--) tmp_array[i - 1] = a[i - 1];
            for (j = center; j < right; j++) tmp_array[right + center - j] = a[j + 1];
            for (k = left; k <= right; k++)
                a[k] = (tmp_array[i] < tmp_array[j]) ? tmp_array[i++] : tmp_array[j--];
            return sl + sr + m;
        }
        else
        {
            return 0;
        }
    }
}
