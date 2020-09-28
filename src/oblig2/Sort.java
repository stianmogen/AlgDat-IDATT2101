package oblig2;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

/**
 * Klassen Sort bruker metoder fra lærerbok;
 * Algoritmer og datastrukturer (Hafting og Ljosland), side 59 - 63.
 */


public class Sort {

    public static void bytt(int[] tab, int i, int j){
        int k = tab[j];
        tab[j] = tab[i];
        tab[i] = k;
    }

    public static int median3sort(int[] tab, int v, int h){
        int m = (v+h)/2;
        if (tab[v] > tab[m]) bytt(tab,v,m);
        if (tab[m] > tab[h]){
            bytt(tab,m,h);
            if (tab[v] > tab[m]) bytt(tab,v,m);
        }
        return m;
    }

    public static int splitt(int[] tab, int v, int h){
        int iv, ih;
        int m = median3sort(tab, v, h);
        int dv = tab[m];
        bytt(tab, m, h-1);
        for (iv = v, ih = h - 1;;){
            while(tab[++iv] < dv);
            while(tab[--ih] > dv);
            if (iv >= ih) break;
            bytt(tab, iv, ih);
        }
        bytt(tab, iv, h-1);
        return iv;
    }

    public static void quickSort(int[] tab, int v, int h){
        if (h - v > 2){
            int partpos = splitt(tab,v,h);
            quickSort(tab, v, partpos - 1);
            quickSort(tab, partpos + 1, h);
        } else median3sort(tab, v, h);
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

    public static void main(String[] args) {
        System.out.println("---- Resultater Sort-metode lærerbok ----");
        Random random = new Random();
        int streamSize = 1000000;

        int[] arrRandom = random.ints(streamSize, 1,10000000).toArray();
        int[] arrLimited = random.ints(streamSize, 1, 3).toArray();
        int[] arrSame = random.ints(streamSize, 1, 2).toArray();
        Long[] sumsBefore = {sum(arrRandom), sum(arrLimited), sum(arrSame)};

        Date start1 = new Date();
        double tid1;
        Date slutt1;
        quickSort(arrRandom, 0, arrRandom.length - 1);
        slutt1 = new Date();
        tid1 = slutt1.getTime()-start1.getTime();
        System.out.println("Tid brukt i ms med " + arrRandom.length + " tall: " + tid1);

        Date start2 = new Date();
        double tid2;
        Date slutt2;
        quickSort(arrLimited, 0, arrLimited.length - 1);
        slutt2 = new Date();
        tid2 = slutt2.getTime()-start2.getTime();
        System.out.println("Tid brukt i ms med " + arrLimited.length + " tall: " + tid2);

        Date start3 = new Date();
        double tid3;
        Date slutt3;
        quickSort(arrSame, 0, arrSame.length - 1);
        slutt3 = new Date();
        tid3 = slutt3.getTime()-start3.getTime();
        System.out.println("Tid brukt i ms med " + arrSame.length + " tall: " + tid3);

        /*
        //Sortering av en allerede sortert tabell
        Date start4 = new Date();
        double tid4;
        Date slutt4;
        quickSort(arrRandom, 0, arrRandom.length - 1);
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
