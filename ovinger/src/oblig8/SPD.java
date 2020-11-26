package oblig8;

import java.io.*;
import java.util.*;
public class SPD {
    int N, K, I; //nr of nodes, nr of edges, interestpoints
    Node[] nodes;
    List<Coordinates> coordinates;
    private int inf = 1000000000; //value set to "infinity" initially
    int counted;

    /**
     * Method from book p.192
     * Reads the given file and creates a graph based on this
     * @param er
     * @param nr
     * @throws IOException
     */

    public void new_wgraph(BufferedReader er, BufferedReader nr, BufferedReader ir) throws IOException {
        StringTokenizer erst = new StringTokenizer(er.readLine());
        StringTokenizer nrst  = new StringTokenizer(nr.readLine());
        StringTokenizer irst = new StringTokenizer(ir.readLine());
        N = Integer.parseInt(nrst.nextToken());
        nodes = new Node[N];
        for (int i = 0; i < N; i++) {
            nodes[i] = new Node(); //new nodes based on the number of nodes
            nrst = new StringTokenizer(nr.readLine());
            Double.parseDouble(nrst.nextToken()); //skips next token
            nodes[i].cords = new Coordinates(Double.parseDouble(nrst.nextToken()), Double.parseDouble(nrst.nextToken()));
        }
        I = Integer.parseInt(irst.nextToken());
        for (int i = 0; i < I; i++){
            irst = new StringTokenizer(ir.readLine());
            int index = Integer.parseInt(irst.nextToken());
            int type = Integer.parseInt(irst.nextToken()); //petrol station?
            String name = "";
            while (irst.hasMoreTokens()) {
                name += irst.nextToken() + " ";
            }
            nodes[index].name = name;
            nodes[index].type = type;
        }
        K = Integer.parseInt(erst.nextToken());
        for (int i = 0; i < K; i++) {
            erst = new StringTokenizer(er.readLine());
            int from = Integer.parseInt(erst.nextToken());
            int to = Integer.parseInt(erst.nextToken());
            int weight = Integer.parseInt(erst.nextToken());
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

    public void dijkstra(int s, int f) {
        int count = 0;
        Node source = nodes[s]; //finds the node in pos of source
        init(source); //initialize based on our source node
        PriorityQueue<Node> pq = create_pq(source); //creates a prioriy queue with java.util priority queue
        Node n = null;
        while (n != nodes[f]){
            count++;
            n = pq.poll();
            for (Wedge w = (Wedge) n.edge; w != null; w = (Wedge) w.next) { //as long as there is an edge
                shorten(n, w, pq); //shorten the queue
            }
        }
        counted = count;
    }

    public void init(Node start) {
        for (int i = N; i-- > 0;) {
            nodes[i].data = new Forgj();
        }
        (start.data).dist = 0;
    }

    public ArrayList<Node> findClosest(int s, int type) {
        Node source = nodes[s];
        ArrayList<Node> closest = new ArrayList<>();
        init(source);
        PriorityQueue pq = create_pq(source);
        while (!pq.isEmpty()) {
            Node n = (Node) pq.poll();
            if (n.type == type || n.type == 6) {
                if (!n.finished) {
                    closest.add(n);
                    n.finished = true;
                }
            }
            if (closest.size() == 10) {
                break;
            }
            for (Wedge w = (Wedge) n.edge; w != null; w = (Wedge) w.next) {
                shorten(n, w, pq);
            }
        }
        return closest;
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
     * Creates the priority queue from java.util
     * @param source
     * @return
     */

    public PriorityQueue create_pq(Node source){
        PriorityQueue priorityQueue = new PriorityQueue();
        //priorityQueue.addAll(new ArrayList(Arrays.asList(pri))); //we add all of the conctents of our list to queue
        priorityQueue.add(source);
        return priorityQueue;
    }

    /**
     * Method to print our results
     * Node, our forgjenger, and the distance
     * if its impossible, we print "not reached"
     * @param s
     */

    public void print(int s, int i) {
        Node source = nodes[s];
        System.out.println("Node " + "     Forgj  " + "      Tid" + "      Landmark?  " + "    Latitude " + "Longitude");
        Node destination = nodes[i];
        if (destination == source) {
            System.out.println(i + "     source       " + (destination.data).dist);
        } else {
            if ((destination.data).dist == inf) {
                System.out.println(i + "               not reached");
            } else {
                while (destination != source) {
                    System.out.println(destination.nr + "     " + (destination.data).pre.nr + "        " + (destination.data).dist + "   " + destination.name + "          " + destination.cords.lat + " " + destination.cords.lon);
                    destination = destination.data.pre;
                }
                System.out.println(destination.nr + "    N/A          " + 0 + "      " + destination.name);
                System.out.print(destination.name + " to " + nodes[i].name + " takes approx " + hundredsToTime(nodes[i].data.dist) + "\n");
            }
        }
        fillCordinates(source, i);
    }

    /**
     * Method for estimating distance from current node to finish node
     * Provided in assignment description
     * To get hundreds of a second we use 35285538.46153846153846153846
     * @param n1
     * @param n2
     * @return
     */

    private int estimateDist(Node n1, Node n2){
        double sin_latitude = Math.sin((n1.cords.latRad - n2.cords.latRad) / 2.0);
        double sin_longitude = Math.sin((n1.cords.lonRad - n2.cords.lonRad) / 2.0);
        return (int) (35285538.46153846153846153846 * Math.asin(Math.sqrt(
                sin_latitude * sin_latitude + n1.cords.cos_lat * n2.cords.cos_lat * sin_longitude * sin_longitude
        )));
    }

    public void aStar(int s, int f) {

        Node start = nodes[s];
        Node target = nodes[f];
        init(start);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Map<Integer, Node> checked = new HashMap<>(); //A map where we add checked nodes
        start.estimate = estimateDist(start, target);
        start.f = start.estimate; //assigns the values f and estimate before adding to queue
        queue.add(start);
        start.inQueue = true; //inQueue is true for later check
        int count = 0;

        while (!queue.isEmpty()) {
            Node n = queue.poll();
            checked.put(n.nr, n); //puts the polled node into checked
            count++;

            if (n.nr == target.nr) {
                break;
            }

            for (Wedge e = (Wedge) n.edge; e != null; e = (Wedge) e.next) {
                Node child = e.to;
                Forgj md = child.data;
                //degining our dist, estimate and f values for temporary use
                double tempGScore = n.data.dist + e.weight;
                double heuristic = estimateDist(child, target);
                double tempFScore = tempGScore + heuristic;

                if (child == checked.get(e.to.nr) && tempFScore >= child.f) {
                    continue;
                }

                if (!child.inQueue || tempFScore < child.f) {
                    md.pre = n;
                    child.data.dist = (int) tempGScore;
                    child.f = tempFScore;
                    if (child.inQueue) { //removes if in queue
                        queue.remove(child);
                    }
                    queue.add(child);
                    child.inQueue = true;
                }
            }
        }
        counted = count;
    }

    private void fillCordinates(Node s, int i){
        coordinates = new ArrayList<>();
        Node destination = nodes[i];
        while(destination != s) {
            coordinates.add(destination.cords);
            destination = destination.data.pre;
        }
    }

    public List<Coordinates> getCoordinates() {
        return coordinates;
    }

    private String hundredsToTime(int hundreds){
        int totalSecs = hundreds/100;
        int seconds = totalSecs%60;
        int totalMinutes = totalSecs / 60;
        int minutes = totalMinutes % 60;
        int hours = totalMinutes / 60;

        return (hours + " hours, " + minutes + " minutes, " + seconds + " seconds");
    }

    public int getCount() {
        return counted;
    }

    public static void main(String[] args) {
        SPD spd = new SPD();
        String pathname = "src\\oblig8\\";
        String nodes = pathname+"noder.txt";
        String edges = pathname+"kanter.txt";
        String interest = pathname+"interessepkt.txt";
        String cords = pathname+"cords.txt";
        String gascords = pathname+"gas.txt";
        int v = 6198111; //VÆRNES
        int k = 6013683; //Kårvåg
        int g = 6225195; //Gjemnes
        int t = 3563448; //Trondheim torg
        int h = 1221382; //Helsinki
        int r = 1119181; //Røros
        int s = k;
        int f = g;
        try {
            BufferedReader edgereader = new BufferedReader(new FileReader(new File(edges)));
            BufferedReader nodereader = new BufferedReader(new FileReader(new File(nodes)));
            BufferedReader interestreader = new BufferedReader(new FileReader(new File(interest)));

            spd.new_wgraph(edgereader, nodereader, interestreader);
            System.out.println(edges + " with start node: " + s);

            //Choose to run either Dijkstra or A*, then change the compareTo method in Node depending on algorithm
            Date start = new Date();
            double tid;
            Date stop;
            //spd.dijkstra(s, f);
            spd.aStar(s,f);
            stop = new Date();
            tid = stop.getTime() - start.getTime();
            spd.print(s, f);

            //CoordinateFileWriter cfw = new CoordinateFileWriter(cords);
            //cfw.writeCoordinatesToFile(spd.getCoordinates());

            Date start2 = new Date();
            double tid2;
            Date stop2;
            ArrayList<Node> gas = spd.findClosest(r, 2);
            stop2 = new Date();
            tid2 = stop2.getTime() - start2.getTime();
            for (int i = 0; i < gas.size(); i++){
            //   System.out.println(gas.get(i).name + " at " + gas.get(i).cords);
            }

            //CoordinateFileWriter cfw2 = new CoordinateFileWriter(gascords);
            //cfw2.writeGasStations(gas);

            System.out.println("TIME USED SHORTEST PATH :" + tid + " with " + spd.getCount() + " nodes visited");
            System.out.println("TIME USED 10 GAS STATONS: " + tid2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * Node class that implements comparable, for correct priority in priority queue
 */

class Node implements Comparable<Node>{
    Edge edge;
    int nr;
    Forgj data;
    String name;
    Coordinates cords;
    int type;
    boolean finished;
    boolean inQueue;
    double estimate; //estimated distance node
    double f; //the sum of estimated cost so far and remaining cost
    boolean star = false;
    public Node() {
        this.estimate = 0;
        this.f = 0;
        this.inQueue = false;
    }

    /**
     * Method to calculate value f when estimate and dist is known
     */
    public void calc_f(){
        f = estimate + this.data.dist;
    }

    /*
    @Override
    //For use with Dijkstra
    public int compareTo(Node o) {
        if (data.dist == o.data.dist) return 0;
        else if (data.dist < o.data.dist) return -1;
        else return 1;
    }*/

    @Override
    //For use with A star
    public int compareTo(Node o) {
        if (f == o.f) return 0;
        else if (f < o.f) return -1;
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

/**
 * Coordinate class for simple use in Node class
 */
class Coordinates {
    double lat; //latitude (breddegrad)
    double lon; //longitude (lengdegrad)
    double latRad; //latitude in radians
    double lonRad; //longitude in radians
    double cos_lat;

    public Coordinates(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
        this.latRad = lat * Math.PI / 180;
        this.lonRad = lon * Math.PI / 180;
        this.cos_lat = Math.cos(latRad);
    }

    @Override
    public String toString() {
        return "["+ lat + ", " + lon + "]";
    }
}

class CoordinateFileWriter {
    PrintWriter writer;
    public CoordinateFileWriter(String path){
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void writeCoordinatesToFile(List<Coordinates> cords){
        for (int i = 0; i < cords.size(); i++){
            writer.write(cords.get(i).lat + ", " + cords.get(i).lon + "\n");
        }
        writer.flush();
        writer.close();
    }

    public void writeGasStations(List<Node> nodes){
        for (Node n:nodes) {
            writer.write(n.cords.lat + ", " + n.cords.lon + "\n");
        }
        writer.flush();
        writer.close();
    }
}