package oblig7;

import java.util.Comparator;

public class HuffmanNode implements Comparator<HuffmanNode> {

    int frequency;
    char character;

    HuffmanNode left;
    HuffmanNode right;

    public HuffmanNode() {
    }

    public HuffmanNode(char character, int frequency) {
        this.character = character;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    @Override
    public int compare(HuffmanNode o1, HuffmanNode o2) {
        return o1.frequency - o2.frequency;
    }
} 