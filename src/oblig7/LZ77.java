package oblig7;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for compression with Lempel Ziv method
 */

public class LZ77 {
    /**
     * final values for buffer looking back through array, and for use by pointer class
     */
    private static final int BUFFERSIZE = (1 << 7) - 1; //7 bits for looking back
    private static final int POINTERSIZE = (1 << 4) - 1; //4 bits for match size
    private static final int MIN_SIZE_POINTER = 3; //compressed text may only be larger than 3 characters
    private int buffersize;
    private char[] data;

    DataInputStream inputStream;
    DataOutputStream outputStream;

    /**
     * LZ77 constructor sets our non final buffersize equal to BUFFERSIZE
     */
    public LZ77() {
        this.buffersize = BUFFERSIZE;
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

        String text = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8); //we read all the bytes from the input stream into a string
        data = text.toCharArray(); //we convert it to a character array
        System.out.println("\n--< Before compression >--");
        for (char c : data ){
            System.out.print((byte)c + " "); //we print all the bytes from array
        }

        StringBuilder incompressible = new StringBuilder(); //creating a string builder as to more easily handle all characters
        for (int i = 0; i < data.length;){ //for the entire length of all characters
            Pointer pointer = findPointer(i); //we find the given pointer (if any) to the current index
            if (pointer != null){ //if a pointer is returned
                if (incompressible.length() != 0) { //if the stringbuilder is not empyt
                    outputStream.writeByte((byte) incompressible.length()); //we write the length of the stringbuilder as a byte to our output stream
                    for(int c = 0; c < incompressible.length(); c++) //we iterate through the stringbuilder
                        outputStream.writeByte((byte)incompressible.charAt(c)); //and write each character as a byte to out outputstream
                    incompressible = new StringBuilder(); //we then reset the stringbuilder
                }
                outputStream.writeByte((byte)(-pointer.getDistance())); //we then write the negative distance as byte (to help the decompression know when to look backwards)
                outputStream.writeByte((byte)pointer.getLength());
                i += pointer.getLength(); //increment the index by the length of pointer
            } else {
                incompressible.append(data[i]); //if we have an empty pointer, we append the bytes at index i
                i += 1;
            }
        }
        //if our string builder is not empty
        if (incompressible.length() != 0) {
            outputStream.writeByte((byte)incompressible.length()); //we write the length of the string builder
            for(int c = 0; c < incompressible.length(); c++)
                outputStream.writeByte((byte) incompressible.charAt(c)); //we write each character as a byte in the outputstream
        }
        //close and flush streams
        inputStream.close();
        outputStream.flush();
        outputStream.close();
        printAfter(path);
        deCompress(path); //then decompress
    }

    public void printAfter(String path) throws IOException {
        //create a new input stream from the compressed path
        DataInputStream compressed = new DataInputStream(new BufferedInputStream(new FileInputStream(path+"compress")));
        byte[] bytes = new byte[compressed.available()];
        compressed.readFully(bytes);
        System.out.println("\n\n--< After compression >--");
        for (byte b:bytes) {
            System.out.print(b + " ");
        }
    }

    private Pointer findPointer(int currentIndex){
        Pointer pointer = new Pointer();

        int max = currentIndex + POINTERSIZE; //Maximum index in the search word
        if (max > data.length - 1)
            max = data.length - 1;

        int min = currentIndex - buffersize; //Minimum index of the sliding window
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
                    //System.out.println(searchWord);
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
        int nrOfBytes = inputStream.available();
        byte[] bytes = new byte[nrOfBytes];
        inputStream.readFully(bytes);
        int last = 0;
        //byte[] b = new byte[nrOfBytes + 4000];
        List<Byte> b = new ArrayList<>();

        int i = 0;
        while (i < bytes.length){
            int n = bytes[i];
            if (n >= 0){
                for (int j = 0; j < n; j++) {
                    b.add(last + j, bytes[i + j + 1]);
                }
                i += n + 1;
                last += n;
            }
            else {
                int length = bytes[i+1];
                for (int j = 0; j < length; j++){
                    b.add(last + j, b.get(last + n + j));
                }
                i += 2;
                last += length;
            }
        }
        System.out.println("\n\n--< After decompression as bytes >--");
        for (int x = 0; x < last; x++)
            System.out.print(b.get(x) + " ");
        System.out.println("\n\n--< After decompression as text >--");
        for (int x = 0; x < last; x++)
            System.out.print(b.get(x));
        byte[] copy = new byte[b.size()];
        for (int k = 0; k < b.size(); k++){
            copy[k] = b.get(k);
        }
        outputStream.write(copy);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * Pointer Class to point to a position in Array
     * Defining where and how much to compress
     */

    class Pointer {

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
        String inpath = "src\\oblig7\\oppgavetekst";
        LZ77 lz77 = new LZ77();
        lz77.compress(inpath);
    }
}
