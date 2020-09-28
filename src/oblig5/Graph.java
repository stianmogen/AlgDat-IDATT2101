package oblig5;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.lang.management.BufferPoolMXBean;
import java.util.StringTokenizer;

public class Graph {
    int N, K;
    Node[] nodes;

    public void new_ugraph(BufferedReader br) throws IOException{
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new Node[N];
        for (int i = 0; i < N; i++){
            nodes[i] = new Node(i+1);
        }
        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < nodes.length; i++) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            Edge e = new Edge(nodes[to], nodes[from].edge1);
            nodes[from].edge1 = e;
        }
    }

    public void print(){
        System.out.println(N + " , " + K + "\n Komponent - Noder");
        for (int i = 0; i < nodes.length; i++){
            System.out.println("         " + nodes[i]);
        }
    }




    public static void main(String[] args){
        String path = "src\\oblig5\\graf";
        Graph graph = new Graph();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            graph.new_ugraph(br);
        } catch (IOException e) {
            e.printStackTrace();
        }

        graph.print();
    }
}

class Edge {
    Node to;
    Edge next;

    public Edge(Node to, Edge next){
        this.to = to;
        this.next = next;
    }

    @Override
    public String toString() {
        return ""+to+"";
    }
}

class Node {
    int value;
    Edge edge1;

    public Node(int value){
        this.value = value;
    }

    @Override
    public String toString() {
        return "" +value+" - " + edge1;
    }
}

