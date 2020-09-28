package oving1;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class PowerOfN3 {

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("dataMathPow.txt", true));
        double x = 0.01;
        int n = 10000;
        Date start = new Date();
        int runder = 0;
        double tid;
        Date slutt;
        do {
            Math.pow(x, n);
            slutt = new Date();
            ++runder;
        } while (slutt.getTime()-start.getTime() < 1000);
        tid = (double)
                (slutt.getTime()-start.getTime()) / runder;
        System.out.println("Millisekund pr. runde:" + tid);
        String data = "x = " +x+ ", n = " +n+ ", millisekund per runde = " +tid;
        writer.append(data + "\n");
        writer.close();

        System.out.println("Testdata, x = 3, n = 14, svar skal bli: 4782969\nFaktisk svar: " + Math.pow(3, 14));
    }
}
