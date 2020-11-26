package oblig2;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

// Java program to implement
// dual pivot QuickSort
public class SortDual {
    static void swap(int[] arr, int i, int j)
    {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static void dualPivotQuickSort(int[] arr,
                                   int low, int high)
    {
        if (low < high)
        {

            // piv[] stores left pivot and right pivot.
            // piv[0] means left pivot and
            // piv[1] means right pivot
            int[] piv;
            piv = partition(arr, low, high);

            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }

    static int[] partition(int[] arr, int low, int high)
    {
        if (arr[low] > arr[high])
            swap(arr, low, high);

        // p is the left pivot, and q
        // is the right pivot.
        int j = low + 1;
        int g = high - 1, k = low + 1,
                p = arr[low], q = arr[high];

        while (k <= g)
        {

            // If elements are less than the left pivot
            if (arr[k] < p)
            {
                swap(arr, k, j);
                j++;
            }

            // If elements are greater than or equal
            // to the right pivot
            else if (arr[k] >= q)
            {
                while (arr[g] > q && k < g)
                    g--;

                swap(arr, k, g);
                g--;

                if (arr[k] < p)
                {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;

        // Bring pivots to their appropriate positions.
        swap(arr, low, j);
        swap(arr, high, g);

        // Returning the indices of the pivots
        // because we cannot return two elements
        // from a function, we do that using an array.
        return new int[] { j, g };
    }

    public static boolean testSortedCorrectly(int[] tab){
        for (int i = 0; i < tab.length - 1; i ++){
            if (tab[i + 1] < tab[i]){
                return false;
            }
        }
        return true;
    }

    public static Long sum(int tab[]){
        Long sum = 0L;
        for (int i = 0; i < tab.length; i++){
            sum += tab[i];
        }
        return sum;
    }

    // Driver code
    public static void main(String[] args)
    {
        System.out.println("---- Resultater Sort GeeksforGeeks ----");
        Random random = new Random();
        int streamSize = 1000000;

        int[] arrRandom = random.ints(streamSize, 1,10000000).toArray();
        int[] arrLimited = random.ints(streamSize, 1, 3).toArray();
        int[] arrSame = random.ints(streamSize, 1, 2).toArray();
        Long[] sumsBefore = {sum(arrRandom), sum(arrLimited), sum(arrSame)};

        Date start1 = new Date();
        double tid1;
        Date slutt1;
        dualPivotQuickSort(arrRandom, 0, arrRandom.length - 1);
        slutt1 = new Date();
        tid1 = slutt1.getTime()-start1.getTime();
        System.out.println("Tid brukt i ms med " + arrRandom.length + " tall: " + tid1);

        Date start2 = new Date();
        double tid2;
        Date slutt2;
        dualPivotQuickSort(arrLimited, 0, arrLimited.length - 1);
        slutt2 = new Date();
        tid2 = slutt2.getTime()-start2.getTime();
        System.out.println("Tid brukt i ms med " + arrLimited.length + " tall: " + tid2);

        Date start3 = new Date();
        double tid3;
        Date slutt3;
        dualPivotQuickSort(arrSame, 0, arrSame.length - 1);
        slutt3 = new Date();
        tid3 = slutt3.getTime()-start3.getTime();
        System.out.println("Tid brukt i ms med " + arrSame.length + " tall: " + tid3);

        /*
        //Denne sorteringen vil gi feil!
        Date start4 = new Date();
        double tid4;
        Date slutt4;
        dualPivotQuickSort(arrRandom, 0, arrRandom.length - 1);
        slutt4 = new Date();
        tid4 = slutt4.getTime()-start4.getTime();
        System.out.println("(Allerede sortert) Tid brukt i ms med " + arrRandom.length + " tall: " + tid4);
        */

        Long[] sumsAfter = {sum(arrRandom), sum(arrLimited), sum(arrSame)};
        System.out.println("First sorted correctly: " + testSortedCorrectly(arrRandom) + "\n" +
                "Second sorted correctly: " + testSortedCorrectly(arrLimited) + "\n" +
                "Third sorted correctly: " + testSortedCorrectly(arrSame) + "\n" +
                "Sum of arrays before sort " + Arrays.toString(sumsBefore) + "\n" +
                "Sum of arrays after sort " + Arrays.toString(sumsAfter));
    }
}
//Contributed bt Gourish Sadhu
