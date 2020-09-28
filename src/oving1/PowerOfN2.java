package oving1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PowerOfN2 {

    public static double solve(double x, int n){
        if (n == 0){
            return 1;
        }
        if (n == 1){
            return x;
        }
        if ((n % 2) == 0){
            double value = solve(x, (n/2));
            return value * value;
        }
        else{
            double value = solve(x, ((n-1)/2));
            return x * value * value;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("data223.txt", true));
        double x = 1.001;
        int n = 5000;
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
