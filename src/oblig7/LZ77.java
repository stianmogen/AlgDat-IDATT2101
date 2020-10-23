package oblig7;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class for compression with Lempel Ziv method
 */

public class LZ77 {
    //final values for buffer looking back through array, and for use by pointer class
    private static final int BUFFERSIZE = (1 << 11) - 1; //11 bits for looking back
    private static final int POINTERSIZE = (1 << 4) - 1; //4 bits for match size
    private static final int MIN_SIZE_POINTER = 3;
    private char[] data;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public LZ77() {
    }

    /**
     * Compression method taking in the path to the file we want to compress
     * @param path
     * @throws IOException
     */
    public void compress(String path) throws IOException {
        //DataStreams for reading and writing bytes
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path+"compress")));
        data = new char[inputStream.available()]; //sets the length of character array to the size of the input stream

        //Before compressing
        String text = new String(inputStream.readAllBytes(), StandardCharsets.ISO_8859_1); //we read all the bytes from the input stream into a string
        data = text.toCharArray(); //we convert it to a character array
        for (char c : data ){
            System.out.print((byte)c + " "); //we print all the bytes from array
        }

        //For keeping track of variables that can not be compressed
        StringBuilder incompressible = new StringBuilder();

        for (int i = 0; i < data.length;){

            Pointer pointer = findPointer(i); //Tries to find a pointer for the current look ahead buffer
            if (pointer != null){ //If a pointer was found

                if (incompressible.length() != 0) { //Write stored incompressible variables if any
                    outputStream.writeByte((byte) (incompressible.length()));
                    for(int c = 0; c < incompressible.length(); c++)
                        outputStream.writeByte((byte)incompressible.charAt(c));
                    incompressible = new StringBuilder();
                }

                //A pointer is stored as two bytes on the format 1DDD DDDD | DDDD LLLL where d's and l's is distance and length in bit form.
                //The first bit (1) makes the byte become a negative number and this indicates that it is a pointer

                outputStream.writeByte((byte) (pointer.getDistance() >> 4) | (1 << 7));
                outputStream.writeByte((((byte) pointer.getDistance() & 0x0F) << 4) | (pointer.getLength() - 1));

                i += pointer.getLength();
            }
            else {

                incompressible.append(data[i]);

                //A sequence of incompressible bytes is written on format 0LLL LLLL (as a byte), where l's is size of sequence in bit form, and then 'size' uncompressed characters follow.
                //The first bit (0) indicates that it is a positive number, and therefore not a pointer.

                if (incompressible.length() == 127) { //If the size becomes 111 1111 (127) or the last character is reached
                    outputStream.writeByte((byte) (incompressible.length())); //Writes length of a sequence of incompressible bytes
                    for (int c = 0; c < incompressible.length(); c++) //And the sequence
                        outputStream.writeByte((byte) incompressible.charAt(c));
                    incompressible = new StringBuilder();
                }
                i += 1;
            }
        }
        if (incompressible.length() != 0) {
            outputStream.writeByte((byte) (incompressible.length())); //Writes length of a sequence of incompressible bytes
            for (int c = 0; c < incompressible.length(); c++) //And the sequence
                outputStream.writeByte((byte) incompressible.charAt(c));
        }

        inputStream.close();
        outputStream.flush();
        outputStream.close();
        printAfter(path);
    }

    public void printAfter(String path) throws IOException {
        DataInputStream compressed = new DataInputStream(new BufferedInputStream(new FileInputStream(path+"compress")));
        byte[] bytes = new byte[compressed.available()];
        compressed.readFully(bytes);
        System.out.println("\nAfter compression");
        for (byte b:bytes) {
            System.out.print(b + " ");
        }
    }

    private Pointer findPointer(int currentIndex){
        Pointer pointer = new Pointer();

        int max = currentIndex + POINTERSIZE; //Maximum index in the search word
        if (max > data.length - 1)
            max = data.length - 1;

        int min = currentIndex - BUFFERSIZE; //Minimum index of the sliding window
        if (min < 0)
            min = 0;

        char[] buffer = Arrays.copyOfRange(data, min, currentIndex); //Seach buffer

        int i = currentIndex + MIN_SIZE_POINTER  - 1; //The match must be at least from currentIndex to i (both excluded)

        outer:
        while(i <= max){
            char[] searchWord = Arrays.copyOfRange(data, currentIndex, i + 1); //The word we are searching for. Starting at length i - currentIndex
            int j = 0;
            while(searchWord.length + j <= buffer.length){ //Never compare variables outside the search buffer array
                int k = searchWord.length - 1; //Find the index (if any) where letters dont match
                while (k >= 0 && searchWord[k] == buffer[j+k]) {
                    k--;
                }
                if(k < 0){ //All characters in the search word matched the search buffer
                    pointer.setDistance(buffer.length - j);
                    pointer.setLength(searchWord.length);
                    i++;
                    continue outer; //Continues loop with an additional character in search word until it fails
                }
                else {
                    int l = k-1; //Find last index of failed character from buffer in the search word if any
                    while(l >= 0 && searchWord[l] != buffer[j+k]) {
                        l--;
                    }
                    j += k - l; //Slide scope according to Boyer Moore
                }
            }
            break; //If it comes to this there was no match for the last search word
        }
        if (pointer.getLength() > 0 && pointer.getLength() > 0) //If it was a match
            return pointer;
        return null; //If it was not a match
    }

    public void deCompress(String path) throws IOException {
        inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(path+"compress")));
        outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path+"decompress")));
        byte[] bytes = new byte[inputStream.available()];
        inputStream.readFully(bytes);

        ArrayList<Byte> b = new ArrayList<>();
        int currentIndex = 0;

        System.out.println("\nAfter decompression");
        int i = 0; //Current index in input file
        while (i < bytes.length-1){
            byte condition = bytes[i];
            if (condition >= 0){
                //Condition is number of uncompressed bytes
                for (int j = 0; j < condition; j++){
                    b.add(bytes[i+j+1]);
                }
                currentIndex += condition;
                i += condition + 1; //We read 1 + condition number uncompressed bytes
            }
            else {
                int jump = ((condition & 127) << 4) | ((bytes[i+1] >> 4) & 15);
                int length = (bytes[i+1] & 0x0F) + 1; //Length of pointer

                for (int j = 0; j < length; j++){
                    b.add(b.get(currentIndex - jump + j));
                }
                currentIndex += length;
                i += 2; //We read a pointer (2 bytes)
            }
        }
        for (i = 0; i < currentIndex; i++) {
            System.out.print(b.get(i) + " ");
            outputStream.write(b.get(i));
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Pointer Class to point to a position in Array
     * Defining where and how much to compress
     */
    private class Pointer {

        /**
         * length: the length of the text to compress
         * distance: how far back from current position
         */
        private int length;
        private int distance;

        /**
         * Constructor defining initial length and distance values as -1
         */
        public Pointer() {
            this(-1,-1);
        }

        /**
         * Constructor defining lenght and distance
         * @param matchLength
         * @param matDistance
         */
        public Pointer(int matchLength, int matDistance) {
            super();
            this.length = matchLength;
            this.distance = matDistance;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int matchLength) {
            this.length = matchLength;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int matDistance) {
            this.distance = matDistance;
        }

    }

    public static void main(String[] args) throws IOException {
        String path = "src\\oblig7\\oppgavetekst";
        LZ77 lz77 = new LZ77();
        lz77.compress(path);
        lz77.deCompress(path);
    }
}
