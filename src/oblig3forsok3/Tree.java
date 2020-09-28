package oblig3forsok3;

public class Tree {

    Node root; //root of the tree, we will be branching out from this point
    String[] temp = new String[]{"", "", "", ""}; //a list to hold the future string values when printing the list

    Tree(){
        root = null;
    }

    /**
     * Method that takes in a word as an argument
     * Check if it has a greater or lower alphabetical value than its parent
     * places it either left or right to said parent
     * then updated the parent to be ready for the next iteration
     * @param word
     */
    public void newNode(String word){
        //creates a root if there isnt one already
        if (root == null) root = new Node(word);
        else{
            //sets the parent as the current root
           Node parent = root;
           while (true) {
               //compares our string to the root, to see if its going to the left or to the right
               int i = word.compareToIgnoreCase(parent.word);
               if (i < 0) {
                   //if the left spot is empty, it will go there
                   if (parent.left == null) {
                       //defines the left node as out word
                       parent.left = new Node(word);
                       //if it is free, we can now return as the job is done
                       return;
                   }
                   //updates the parent to the left spot, so we can do the loop ones more
                   parent = parent.left;
               }
               else if (i > 0) {
                   if (parent.right == null) {
                       parent.right = new Node(word);
                       return;
                   }
                   parent = parent.right;
               }
           }
        }
    }

    /**
     * Formatter to let us print each row correctly
     * we cut the distance between words in half for each row
     * @param node
     * @param row
     */
    private void format(Node node, int row){
        int pixels = 64;
        int length = pixels / (int) (Math.pow(2, row)); //this is for getting half the distance for each row
        //repeat method lets us get a space as many times as we like, we times one space with our length while taking the lenght of the word into account
        if (node != null) temp[row] += " ".repeat((length-node.word.length())/2) + node.word + " ".repeat((length-node.word.length())/2) ;
        else temp[row] += " ".repeat(length);
        //we keep moving down for each row
        if (row < temp.length-1){
            if (node != null) {
                //we use the method recursively to fill in the rest of the strings
                format(node.left, row + 1);
                format(node.right, row + 1);
            }
            else {
                format(null, row + 1);
                format(null, row + 1);
            }
        }
    }

    public void printTree(){
        format(root, 0);
        for (String row: temp) {
            System.out.println(row);
        }
    }

    /**
     * A node consists of a word, a node to its left, and a node to its right
     */
    static class Node{
        String word;
        Node left = null;
        Node right = null;
        Node(String word){
            this.word = word;
        }
    }

    public static void main(String[] args) {
        Tree tree = new Tree();
        tree.newNode("BAMOS");
        tree.newNode("A");
        tree.newNode("LA");
        tree.newNode("PLAYA");
        tree.newNode("ME");
        tree.newNode("GUSTA");
        tree.newNode("BAILAR");
        tree.printTree();
    }
}
