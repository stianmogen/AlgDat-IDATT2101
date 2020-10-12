package oblig6;

import java.io.*;
import java.util.*;
public class WeightedGraph {
    int N, K; //nr of nodes, nr of edges
    Node[] nodes;
    private int inf = 1000000000; //value set to "infinity" initially

    /**
     * Method from book p.192
     * Reads the given file and creates a graph based on this
     * @param br
     * @throws IOException
     */

    public void new_wgraph(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new Node[N];
        for (int i = 0; i < N; i++) {
            nodes[i] = new Node(); //new nodes based on the number of nodes
        }
        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            int weight = Integer.parseInt(st.nextToken());
            //new weighted edge with node reference, next edge, and weight
            Wedge w = new Wedge(nodes[to], (Wedge) nodes[from].edge, weight);
            //sets the edge og the node to the new edge, and the nr to from-value
            nodes[from].edge = w;
            nodes[from].nr = from;
        }
    }

    /**
     * Dijkstra algorithm that finds the shortest path from a source node (based on start pos s), to each node in graph
     * based on method book p.195
     * @param s
     */

    public void dijkstra(int s) {
        Node source = nodes[s]; //finds the node in pos of source
        init(source); //initialize based on our source node
        Node[] pri = nodes;
        PriorityQueue pq = lag_priko(pri); //creates a prioriy queue with java.util priority queue
        for (int i = N; i > 1; i--) { //iterates downwards through all nodes except source
            Node n = (Node) pq.poll();
            for (Wedge w = (Wedge) n.edge; w != null; w = (Wedge) w.next) { //as long as there is an edge
                shorten(n, w, pq); //shorten the queue
            }
        }
        print(source);
    }

    public void init(Node start) {
        for (int i = N; i-- > 0;) {
            nodes[i].data = new Forgj();
        }
        ((Forgj) start.data).dist = 0;
    }

    /**
     * Method to shorten until the shortest path is found
     * Book p.194
     * @param n
     * @param w
     * @param pq
     */

    public void shorten(Node n, Wedge w, PriorityQueue pq) {
        Forgj nd = n.data;
        Forgj md = w.to.data;
        //if the distance so far and the edge is shorter than the estimated distance
        if (md.dist > nd.dist + w.weight) {
            md.dist = nd.dist + w.weight;
            md.pre = n;
            pq.remove(w.to); //updates priority queue
            pq.add(w.to);
        }
    }

    /**
     * Method to print our results
     * Node, our forgjenger, and the distance
     * if its impossible, we print "not reached"
     * @param s
     */

    public void print(Node s) {
        System.out.println("Node " + " Forgj  " + "   Dis");
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == s) {
                System.out.println(i + "     source       " + (nodes[i].data).dist);
            } else {
                if (((Forgj) nodes[i].data).dist == inf) {
                    System.out.println(i + "               not reached");
                } else {
                    System.out.println(i + "     " + (nodes[i].data).pre.nr + "        " + (nodes[i].data).dist);
                }
            }

        }
    }

    public static void main(String[] args) {
        WeightedGraph weightedGraph = new WeightedGraph();
        String file_name = "vg1";
        String path = "src\\oblig6done\\"+file_name;
        int s = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            weightedGraph.new_wgraph(br);
            System.out.println(file_name + " with start node: " + s);
            weightedGraph.dijkstra(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the priority queue from java.util
     * @param pri
     * @return
     */

    public PriorityQueue lag_priko(Node[] pri){
        PriorityQueue priorityQueue = new PriorityQueue(N);
        priorityQueue.addAll(new ArrayList(Arrays.asList(pri))); //we add all of the conctents of our list to queue
        return priorityQueue;
    }
}

/**
 * Node class that implements comparable, for correct priority in priority queue
 */

class Node implements Comparable<Node>{
    Edge edge;
    int nr;
    Forgj data;
    boolean finished;
    public Node() {
    }

    @Override
    public int compareTo(Node o) {
        if (data.dist == o.data.dist) return 0;
        else if (data.dist < o.data.dist) return -1;
        else return 1;
    }
}

class Edge {
    Edge next;
    Node to;

    public Edge(Node to, Edge next) {
        this.to = to;
        this.next = next;
    }
}

/**
 * Wedge extends from edge and adds parameter weight, allowing us to have a weighted graph
 */

class Wedge extends Edge {
    int weight;

    public Wedge(Node n, Wedge next, int weight) {
        super(n, next);
        this.weight = weight;
    }
}

class Forgj {
    int dist;
    Node pre;
    static int infinite = 1000000000;

    public Forgj() {
        dist = infinite;
    }
}
