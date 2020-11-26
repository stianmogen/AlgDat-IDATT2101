package oblig4;

import java.util.*;

public class BigHashTable {

    static int A = 1327217885; //A example given in textbook/foils
    int x;
    int[]table;
    int col = 0;

    /**
     * Our constructor takes in the closest power of two (2^x) and the size of the table we want to create
     * @param x
     * @param sizeOfTable
     */
    public BigHashTable(int x, int sizeOfTable){
        this.x = x;
        this.table = new int[sizeOfTable];
    }

    public double lastFactor(int nrOfElements){
        return (double)nrOfElements / (double) table.length; //table.length should be about 1.3 times bigger
    }

    /**
     * multiplication method from book page 159
     * fixed typo, unsigned -> int, A value changed
     * @param k
     * @return
     */

    private int hash1(int k){
        return (k * A >>> (31 - x)) % table.length; //uses unsigned right shift for hashing
    }

    /**
     * hash2 finction is similar to regular modulo div
     * since m (size of table) is prime, we can use do k % (m-1)
     * we add 1 at the end, since hash2 may never be equal to zero
     * @param k
     * @return
     */

    private int hash2(int k){
        return (k % (table.length - 1)) + 1;
    }

    public int getCol() {
        return col;
    }

    public double collisionsPerElement(int nrOfElements){
        return (double) col / (double) nrOfElements;
    }

    /**
     * Probe method that returns a possible free position in table
     * @param h1 first hashvalue
     * @param h2 second hashvalue (may not be 0)
     * @param i iterated value that updates when there is a collision
     * @return
     */

    private int probeDouble(int h1, int h2, int i){
        return (h1 + i*h2) % table.length;
    }

    /**
     * Takes in an integer and adds it to table
     * Uses hashing to find position, then uses the probe method to handle collisions
     * @param k
     * @return
     */

    public void addTo(int k){
        int h1 = hash1(k); //gives us the first hashvalue
        if (table[h1] == 0) {
            table[h1] = k; //if position is free, places the integer at this position
        } else {
            col++; //add one collision
            int h2 = hash2(k); //finds the second hashvalue used for probing
            int i = 0;
            while (true){
                int j = probeDouble(h1, h2, i); //finds a new position until a free pos is found
                if (table[j] == 0){
                    table[j] = k; //if this position is free, our integer value is places here
                    break;
                }
                col++;
                i++; //if the pos was occupied, we increment i and search again
            }
        }
    }

    public static void main(String[] args) {
        int x = 24; //closest power of two bigger than size of table
        int sizeOfTable = 13000027; //closest prime number to 13 million
        BigHashTable bht = new BigHashTable(x, sizeOfTable);
        Random random = new Random();
        int streamSize = 10000000;
        int[] arrRandom = random.ints(streamSize, 2,1000000).toArray();
        Date start1 = new Date();
        double tid1;
        Date slutt1;
        for (int value : arrRandom) {
            bht.addTo(value);
        }
        slutt1 = new Date();
        tid1 = slutt1.getTime()-start1.getTime();
        System.out.println("Tid brukt av i ms med " + arrRandom.length + " tall: " + tid1);
        System.out.println("Number of collisions: " + bht.getCol());
        System.out.println("Lastfaktor: " + bht.lastFactor(arrRandom.length));
        System.out.println("Collisions per element in table: " + bht.collisionsPerElement(arrRandom.length));

        HashMap<Integer, Integer> hashMap = new HashMap();
        Date start2 = new Date();
        double tid2;
        Date slutt2;
        for (int value : arrRandom) {
            hashMap.put(value, value);
        }
        slutt2 = new Date();
        tid2 = slutt2.getTime()-start2.getTime();
        System.out.println();
        System.out.println("Tid brukt java.util i ms med " + arrRandom.length + " tall: " + tid2);
    }
}
