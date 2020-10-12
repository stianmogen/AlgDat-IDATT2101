package oblig5;
import java.io.*;
import java.util.Iterator;
import java.util.Stack;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Undirected Graph, made with Kosaraju's Algorithm
 * Original code examples explaining algorithm:
 * https://www.programiz.com/dsa/strongly-connected-components?fbclid=IwAR1qrufiL5Ex-5HQnKRWkqUdgUs3SRJXgrh5wYhXlgCfqGDNs9j5eEsJ2yM
 */

class UndirectedGraph {
    int N, K;
    private LinkedList<Integer> nodes[];

    // Create a graph with empty constructor
    UndirectedGraph() {

    }
    // Another constructor used when transposing graph
    //initializes nodes and ads an ampty linked list to each pos
    UndirectedGraph(int size){
        N = size;
        nodes = new LinkedList[N];
        for (int i = 0; i < N; i++){
            nodes[i] = new LinkedList<>();
        }
    }

    /**
     * Undirected graph method from book p. 175
     * reads the given file, and creates a graph based on content
     * @param br
     * @throws IOException
     */
    public void new_ugraph(BufferedReader br) throws IOException{
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new LinkedList[N]; //new linked list with size equal to nr of nodes
        for (int i = 0; i < N; i++){
            nodes[i] = new LinkedList<>();
        }
        K = Integer.parseInt(st.nextToken()); //finds how many edges we habe
        for (int i = 0; i < K; i++){
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            addEdge(from, to); //we add an edge by defining the starting pos, and where the edge leads to
        }
    }

    /**
     * Adds edge to d, from nodes with index s
     * @param s
     * @param d
     */
    void addEdge(int s, int d) {
        nodes[s].add(d);
    }

    /**
     * A DFS method that searches the graph recursively
     * finds all the nodes that can be reached from the start
     * @param s
     * @param visited
     */
    void dfs_search(int s, boolean visited[]) {
        visited[s] = true; //keeps track of visited nodes
        System.out.print(s + " ");
        int n;
        Iterator<Integer> i = nodes[s].iterator();
        while (i.hasNext()) {
            n = i.next();
            if (!visited[n]) //if the next in line is not yet visited
                dfs_search(n, visited); //recursive function call
        }
    }

    /**
     * Transpose method, that creates a new graph
     * that is the transposed graph related to our original graph
     * @return
     */
    UndirectedGraph Transpose() {
        UndirectedGraph g = new UndirectedGraph(N); //new graph with second constructor
        for (int s = 0; s < N; s++) { //counts down, instead of up to give reverse order
            Iterator<Integer> i = nodes[s].listIterator();
            while (i.hasNext())
                g.nodes[i.next()].add(s);
        }
        return g;
    }

    /**
     * Pushes a node to the stack
     * in the order the DFS does not find any more nodes
     * that are not yet visited
     * @param s
     * @param visitedVertices
     * @param stack
     */
    void fillOrder(int s, boolean visitedVertices[], Stack stack) {
        visitedVertices[s] = true;

        Iterator<Integer> i = nodes[s].iterator();
        while (i.hasNext()) {
            int n = i.next();
            if (!visitedVertices[n])
                fillOrder(n, visitedVertices, stack);
        }
        stack.push(s);
    }

    /**
     * prints strongly connected components
     */
    void print() {
        Stack stack = new Stack();
        int row = 0;

        boolean visitedVertices[] = new boolean[N];
        for (int i = 0; i < N; i++)
            visitedVertices[i] = false; //sets all values to false

        for (int i = 0; i < N; i++)
            if (visitedVertices[i] == false) {
                fillOrder(i, visitedVertices, stack); //if not visited, checks
            }

        UndirectedGraph gr = Transpose(); //transposes our grapg

        for (int i = 0; i < N; i++) {
            visitedVertices[i] = false;
        }
        while (stack.empty() == false) { //as long as the stack is not empty
            int s = (int) stack.pop();
            if (visitedVertices[s] == false) {
                System.out.print(row + ": ");
                gr.dfs_search(s, visitedVertices); //does a dfs search
                System.out.println();
                row++;
            }
        }
    }

    public static void main(String args[]) {
        UndirectedGraph graph = new UndirectedGraph();
        String path = "src\\oblig5\\L7g1";
        String path2 = "src\\oblig5\\L7g6";
        String path3 = "src\\oblig5\\L7g2";
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path3)));
            graph.new_ugraph(br);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Strongly Connected Components:");
        graph.print();
    }
}