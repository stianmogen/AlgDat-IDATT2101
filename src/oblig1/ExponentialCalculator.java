package oblig1;
import java.io.IOException;
import java.util.Date;

public class ExponentialCalculator {

    /**
     * recursive method that takes in a base x (double) and the whole number exponent n
     * first checks if exponent is 0, returns 1 if true
     * then checks if exponent is 1, returns x if true
     * method used to multiply with itself until n is 1
     * @param x
     * @param n
     * @return x^n
     */
    public static double solve1(double x, int n){
        if (n == 0){
            return 1;
        }
        if (n == 1){
            return x;
        }
        else {
            return x * solve1(x, n - 1);
        }
    }

    /**
     * recursive method with x as base, n as whole number exponent
     * checks if n is 0, or n is 1, if so returns 1 and x respectively
     * checks if x is an even number, if so uses the formula with the power of n/2 to solve
     * else it uses the odd nymber formula with the power of (n-1)/2
     * @param x
     * @param n
     * @return x^n
     */
    public static double solve2(double x, int n){
        if (n == 0){
            return 1;
        }
        if (n == 1){
            return x;
        }
        if ((n % 2) == 0){
            double value = solve2(x, (n/2));
            return value * value;
        }
        else{
            double value = solve2(x, ((n-1)/2));
            return x * value * value;
        }
    }

    public static void main(String[] args) throws IOException {

        double x = 3;
        int n = 14;

        Date start1 = new Date();
        int runder1 = 0;
        double tid1;
        Date slutt1;
        do {
            solve1(x, n);
            slutt1 = new Date();
            ++runder1;
        } while (slutt1.getTime()-start1.getTime() < 1000);

        tid1 = (double)
                (slutt1.getTime()-start1.getTime()) / runder1;
        String data1 = "FOR METODE 1: x = " +x+ ", n = " +n+ ", millisekund per runde = " +tid1+ ", antall runder: " +runder1;
        System.out.println(data1);

        Date start2 = new Date();
        int runder2 = 0;
        double tid2;
        Date slutt2;
        do {
            solve2(x, n);
            slutt2 = new Date();
            ++runder2;
        } while (slutt2.getTime()-start2.getTime() < 1000);

        tid2 = (double)
                (slutt2.getTime()-start2.getTime()) / runder2;
        String data2 = "FOR METODE 2: x = " +x+ ", n = " +n+ ", millisekund per runde = " +tid2+ ", antall runder: " +runder2;
        System.out.println(data2);


        Date start3 = new Date();
        int runder3 = 0;
        double tid3;
        Date slutt3;
        do {
            Math.pow(x, n);
            slutt3 = new Date();
            ++runder3;
        } while (slutt3.getTime()-start3.getTime() < 1000);

        tid3 = (double)
                (slutt3.getTime()-start3.getTime()) / runder3;
        String data3 = "FOR METODE 3: x = " +x+ ", n = " +n+ ", millisekund per runde = " +tid3+ ", antall runder: " +runder3;
        System.out.println(data3);

        System.out.println("Testdata, x = 3, n = 14, svar skal bli: 4782969\nFaktisk svar Metode 1: " +solve1(3, 14) + "\nFaktisk svar Metode 2: " +solve2(3, 14)+ "\nFaktisk svar på Math.pow: " + Math.pow(3, 14));
        System.out.println("Testdata, x = 2, n = 10, svar skal bli: 1024\nFaktisk svar Metode 1: " +solve1(2, 10) + "\nFaktisk svar Metode 2: " +solve2(2, 10)+ "\nFaktisk svar på Math.pow: " + Math.pow(2, 10));

    }

}
