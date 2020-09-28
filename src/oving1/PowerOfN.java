package oving1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PowerOfN {

    /**
     * recursive method that takes in a base x (double) and the whole number exponent n
     * first checks if exponent is 0, returns 1 if true
     * then checks if exponent is 1, returns x if true
     * method used to multiply with itself until n is 1
     * @param x
     * @param n
     * @return x^n
     */
    public static double solve(double x, int n){
        if (n == 0){
            return 1;
        }
        if (n == 1){
            return x;
        }
        else {
            return x * solve(x, n - 1);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("data211.txt", true));
        double x = 0.01;
        int n = 10000;
        Date start = new Date();
        int runder = 0;
        double tid;
        Date slutt;
        do {
            solve(x, n);
            slutt = new Date();
            ++runder;
        } while (slutt.getTime()-start.getTime() < 1000);
        tid = (double)
                (slutt.getTime()-start.getTime()) / runder;
        System.out.println("Millisekund pr. runde:" + tid);
        String data = "x = " +x+ ", n = " +n+ ", millisekund per runde = " +tid;
        writer.append(data + "\n");
        writer.close();

        System.out.println("Testdata, x = 3, n = 14, svar skal bli: 4782969\nFaktisk svar: " +solve(3, 14));
    }
}
