package oblig7;

import java.util.PriorityQueue;

public class MinHeap {
    int length;
    PriorityQueue<HuffmanNode> heap;

    public MinHeap(int length) {
        this.length = length;
        this.heap = new PriorityQueue<>(new HuffmanNode());
    }

    /**
     * Add HuffmanNodes with the character (int of index)
     * with the frequency of the value at index to the characters value
     */
    public MinHeap createAndBuild(int[] values) {
        for (int i = 0; i < values.length; i++) {
            if (charHasNoFrequency(values[i]))
                continue;
            
            char currentCharacter = (char) i;
            int frequency = values[i];
            HuffmanNode huffmanNode = new HuffmanNode(currentCharacter, frequency);
            heap.add(huffmanNode);
        }
        return this;
    }

    private boolean charHasNoFrequency(int value) {
        return value == 0;
    }

    public boolean isOfSizeOne() {
        return heap.size() == 1;
    }

    public HuffmanNode getMin() {
        return heap.poll();
    }

    public void insert(HuffmanNode top) {
        heap.add(top);
    }
}
