package oblig7;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Huffman {

    private static final int MAX_BYTE_VALUE = 256;

    // frequencies[i] = frequency of char i
    private List<Byte> bytes;
    private int[] frequencies;
    private String[] bitstrings;
    private DataOutputStream out;

    public Huffman() {
        this.frequencies = new int[MAX_BYTE_VALUE];
        this.bitstrings = new String[MAX_BYTE_VALUE];
        bytes = new ArrayList<>();
    }

    public void compress(byte[] compressedBytes,  String outpath) throws IOException {
        getFrequenciesFrom(compressedBytes);
        HuffmanNode root = buildHuffmanTree();
        parseCodes(root, "");
        writeToOutputFile(outpath, compressedBytes);
    }

    private void getFrequenciesFrom(byte[] compressedBytes) {
        for (int b : compressedBytes) {
            if (b < 0)
                frequencies[MAX_BYTE_VALUE + b]++;
            else
                frequencies[b]++;
        }
    }

    private HuffmanNode buildHuffmanTree() {
        MinHeap heap = new MinHeap(frequencies.length);
        heap.createAndBuild(frequencies);

        HuffmanNode root = new HuffmanNode();

        while (!heap.isOfSizeOne()) {
            HuffmanNode left = heap.getMin();
            HuffmanNode right = heap.getMin();

            int topFrequency = left.frequency + right.frequency;
            HuffmanNode top = new HuffmanNode('\0', topFrequency);

            top.left = left;
            top.right = right;

            heap.insert(top);
            root = top;
        }

        return root;
    }

    private void parseCodes(HuffmanNode root, String s) {
        if (root.left == null && root.right == null) {
            bitstrings[root.character] = s;
            return;
        }

        parseCodes(root.left, s + "0");
        parseCodes(root.right, s + "1");
    }
    
    private void writeToOutputFile(String outpath, byte[] compressedBytes) throws IOException {
        out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outpath)));

        for (int i : frequencies)
            out.writeInt(i);

        int lastByte = parseBitStringsAndGetLastByte(compressedBytes);
        out.writeInt(lastByte);

        writeBytes();
        
        out.close();
    }

    private void writeBytes() throws IOException {
        for (Byte s : bytes)
            out.write(s);
    }

    private int parseBitStringsAndGetLastByte(byte[] compressedBytes) {
        int input;
        int i = 0;
        int j, k;
        long currentByte = 0L;

        for (k = 0; k < compressedBytes.length; k++) {
            input = compressedBytes[k];
            if (input < 0)
                input += MAX_BYTE_VALUE;

            String bitString = bitstrings[input];

            j = 0;
            while (j < bitString.length()) {
                if (bitString.charAt(j) == '0')
                    currentByte = (currentByte << 1);
                else
                    currentByte = ((currentByte << 1) | 1);

                ++j;
                ++i;

                if (i == 8) {
                    bytes.add((byte) currentByte);
                    i = 0;
                    currentByte = 0L;
                }
            }
        }

        int lastByte = i;
        while (i < 8 && i != 0) {
            currentByte = (currentByte << 1);
            ++i;
        }

        bytes.add((byte) currentByte);

        return lastByte;
    }

    public byte[] decompress(String fileName) throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(fileName));
        getFrequenciesFrom(input);

        int lastByte = input.readInt();
        HuffmanNode root = buildHuffmanTree();

        byte characterAsBytes;
        byte[] bytes = input.readAllBytes();
        input.close();

        int length = bytes.length;

        if (lastByte > 0)
            length--;

        ArrayList<Byte> outputBytes = new ArrayList<>();
        BitString h = new BitString(0, 0);

        for (int i = 0; i < length; i++) {
            characterAsBytes = bytes[i];
            BitString bitstring = new BitString(8, characterAsBytes);
            h = BitString.concat(h, bitstring);
            h = writeCharactersTo(root, h, outputBytes);
        }

        if (lastByte > 0) {
            BitString b = new BitString(lastByte, bytes[length] >> (8 - lastByte));
            h = BitString.concat(h, b);
            writeCharactersTo(root, h, outputBytes);
        }

        return toByteArray(outputBytes);
    }

    private void getFrequenciesFrom(DataInputStream input) throws IOException {
        frequencies = new int[MAX_BYTE_VALUE];

        for (int i = 0; i < frequencies.length; i++) {
            int freq = input.readInt();
            frequencies[i] = freq;
        }
    }

    private static BitString writeCharactersTo(HuffmanNode tree, BitString bitstring, ArrayList<Byte> decompressedBytes) {
        HuffmanNode tempTree = tree;
        int c = 0;

        for (long j = 1 << bitstring.length - 1; j != 0; j >>= 1) {
            c++;
            if ((bitstring.bits & j) == 0)
                tempTree = tempTree.left;
            else
                tempTree = tempTree.right;

            if (tempTree.left == null) {
                long character = tempTree.character;
                decompressedBytes.add((byte) character);
                long temp = ~(0);
                bitstring.bits = (bitstring.bits & temp);
                bitstring.length = bitstring.length - c;
                c = 0;
                tempTree = tree;
            }
        }

        return bitstring;
    }

    public byte[] toByteArray(ArrayList<Byte> source) {
        byte[] byteArray = new byte[source.size()];

        for (int i = 0; i < source.size(); i++)
            byteArray[i] = source.get(i);

        return byteArray;
    }

    static class BitString {
        int length;
        long bits;

        BitString(int length, long bitsAsLong) {
            this.length = length;
            this.bits = bitsAsLong;
        }

        BitString(int length, byte bits) {
            this.length = length;
            this.bits = convertByte(bits, length);
        }

        static BitString concat(BitString bitstring, BitString other) {
            int length = bitstring.length + other.length;
            long bits = other.bits | (bitstring.bits << other.length);

            if (length > 64)
                throw new IllegalArgumentException(
                        "Bitstring too long: " + bits + ", length=" + length);

            return new BitString(length, bits);
        }

        public long convertByte(byte bite, int length) {
            long temp = 0;
            for (long i = 1 << length - 1; i != 0; i >>= 1)
                if ((bite & i) == 0)
                    temp = (temp << 1);
                 else
                     temp = ((temp << 1) | 1);

            return temp;
        }
    }
}
