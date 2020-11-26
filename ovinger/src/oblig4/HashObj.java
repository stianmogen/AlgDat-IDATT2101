package oblig4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HashObj {

    /**
     * Hashtable with methods described in the book; Algrotimer and Datastrukturer
     */
    static int A = 1327217885;
    int x;
    Node[] table; //2^7 gives us fairly close to 1.3 times the size of the table
    int col = 0;
    public HashObj(int x, int size){
        this.x = x;
        this.table = new Node[size];
    }

    //Hashing methods that are described in the book
    public static int divhash(int k, int m){
        return k % m;
    }

    public static int multhash(int k, int x){
        return (k * A >>> (31 - x));
    }

    /**
     * We take in the string s as parameter, and get a numerical value from the given string
     * we divide it by the length of the table, to get our value
     * @param s
     * @return
     */
    public int getNrValue(String s) {
        int value = 0;
        for (int i = 0; i < s.length(); i++) {
            value += s.charAt(i) * (i + 1); //we do i+1 to prevent multiplying by 0 for the first letter
        }
        //we use the modulo division method to get our position.
        return value % table.length; //didnt bother with a method call, as its such a quick operation to write
    }

    /**
     * we compare the size of the table to the elements within it, to see if we are utilizing the space efficiently
     * @param nrOfElements
     * @return
     */
    public double lastFactor(int nrOfElements){
        return (double)nrOfElements / (double) table.length;
    }

    public int getCol() {
        return col;
    }

    public double colPerPers(int nrOfElements){
        return (double) col / (double) nrOfElements;
    }

    /**
     * Method to add a name to the hash table
     * @param name
     */
    public void add(String name){
        int k = getNrValue(name); //gets the int value based on name
        if (table[k] == null) { //checks if the position is free
            table[k] = new Node(k, name); //creates a new node with string and places it at the position
        }
        else  {
            col++; //adds one to collision counter, and prints details of collision
            System.out.println(col + " - Collision between " + name + " and " + table[k].name);
            Node copy = table[k]; //creates a copy of node at position k
            table[k] = new Node(k, name); //sets new node at position  k
            table[k].next = copy; //moves the copy one place back
            table[k].next.prev = table[k]; //links the two together
        }
    }

    /**
     * Method that returns the node with name matching our string parameter
     * @param name
     * @return
     */
    public Node findNode(String name){
        int k = getNrValue(name);
        Node[]tempTable = table; //creates a temporary table as a copy of our table
        if (tempTable[k] == null) return null; //if there are nothing at pos k, returns zero
        if (!tempTable[k].name.equals(name)) { //if the names do not match we loop through
            while (true) {
                if (tempTable[k].next == null) return null;
                if (tempTable[k].next.name.equals(name)) return tempTable[k].next; //returns if names match
                tempTable[k] = tempTable[k].next; //if we move ahead in the list and does the check again
            }
        }
        return tempTable[k]; //returns our node
    }

    public int findPosWithKey(String name){
        return findNode(name).pos;
    }

    public String findNameWithKey(String name){
        return findNode(name).name;
    }

    /**
     * Method to print readable information
     */
    public void print(){
        for (Node node:this.table) {
            Node copyNode = node;
            if (copyNode != null) System.out.println("");
            while (copyNode != null){
                System.out.print("Position: " + copyNode.pos + " Name: " + copyNode.name + ", ");
                copyNode = copyNode.next; //prints to the position as long as the current node is not null
            }
        }
    }

    @Override
    public String toString() {
        return "HashObj: " +
                "\n" + Arrays.toString(table) +
                "\nTotal collision: " + col;
    }

    static class Node{
        int pos;
        String name;
        Node next;
        Node prev;
        Node(int pos, String name){
            this.pos = pos;
            this.name = name;
            this.next = null;
            this.prev = null;
        }
        Node(int pos, String name, Node next, Node prev){
            this.pos = pos;
            this.name = name;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public String toString() {
            return "Node -" +
                    " Position: " + pos +
                    " Name: " + name +
                    " Next node -> " + next;
        }
    }

    static class Formatter{
        /**
         * Formatter to return a array of strings to help us add each individual name to hashtable
         * @param path
         * @return
         */
        static String[] format(String path) {
            ArrayList<String> result = new ArrayList<>();
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(path)));
                String line;
                while ((line = br.readLine()) != null) {
                    result.add(line);
                }
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String[] copy = new String[result.size()];
            return result.toArray(copy);
        }

    }

    public static void main(String[] args) {
        int x = 7;
        HashObj hashObj = new HashObj(x, (int) Math.pow(2, x));
        String namesearch = "Stian Fjæran,Mogen";
        String namesearchNextnode = "Nicolay,Schiøll-Johansen";

        String path = "src\\oblig4\\navn20.txt";
        String[] names = Formatter.format(path);
        System.out.println(names.length);

        for (String name:names) {
            hashObj.add(name);
        }
        hashObj.print();
        System.out.println("\nTotal collisions: " + hashObj.getCol());
        System.out.println("Collisions per person " + hashObj.colPerPers(names.length));
        System.out.println("Info for " + namesearch);
        System.out.println(hashObj.findNode(namesearch));
        System.out.println("Position found: " + hashObj.findPosWithKey(namesearch));
        Node exampleNode = hashObj.findNode(namesearchNextnode);
        System.out.println("This also works for finding a node in the 'next' position: " + exampleNode);
        System.out.println("Lastfaktor: " + hashObj.lastFactor(names.length));
    }
}
