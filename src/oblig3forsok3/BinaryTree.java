package oblig3forsok3;
/**
 * Klassen binærtre
 */
public class BinaryTree {

    TreNode rot;
    String[] utskriftListe = new String[]{"","","",""}; //Oppretter tomme strenger for å forenkle utskriftsmetoden og ikke få med elementer med "null"

    /**
     * Konstruktør uten parametre
     */
    BinaryTree(){
        rot = null;
    }

    /**
     * Konstruktør med én streng som parameter
     */
    BinaryTree(String rot){
        this.rot = new TreNode(rot);
    }

    /**
     * Konstuktør som tar en liste med ord
     */
    BinaryTree(String[] ord){
        if (ord.length > 0)
            this.rot = new TreNode(ord[0]);
        for (int i = 1; i < ord.length; i++)
            nyTreNode(ord[i]);
    }

    /**
     * Metode for å legge til ny node i treet
     */
    public void nyTreNode(String ord){
        if (rot == null)
            rot = new TreNode(ord);
        else {
            TreNode forelder = rot;
            while (true){
                int i = ord.compareToIgnoreCase(forelder.ord);
                if (i < 0) { //Hvis ordet kommer før alfabetisk
                    if (forelder.vBlad == null) { //Hvis ikke elementet har et venstre blad
                        forelder.vBlad = new TreNode(ord);
                        return;
                    }
                    forelder = forelder.vBlad;
                }
                else if (i > 0) { //Hvis ordet kommer etter alfabetisk
                    if (forelder.hBlad == null) { //Hvis ikke elementet har et høyre blad
                        forelder.hBlad = new TreNode(ord);
                        return;
                    }
                    forelder = forelder.hBlad;
                }
            }
        }
    }

    /**
     * Metode som printer ut binærtreet
     */
    public void print(){
        System.out.println("\n\nBinærtre:");
        add(rot,0);
        for (String linje : utskriftListe) {
            System.out.println(linje);
        }
    }

    /**
     * Metode som legget til bladene på utskriftformat i et array
     */
    private void add(TreNode treNode, int index){
        double lengde = 64/Math.pow(2, index); //Lengden på strengen halveres for hvert nivå ned vi går
        if (treNode != null)
            utskriftListe[index] += " ".repeat((int) Math.floor((lengde-treNode.ord.length())/2)) + treNode.ord + " ".repeat((int) Math.ceil((lengde-treNode.ord.length())/2)); //Dersom noden ikke er lik null fyller vi inn ordet norden holder i midten av et tomrom
        else utskriftListe[index] += " ".repeat((int) lengde); //Dersom noden er lik null fyller vi inn tomrom
        if (index < utskriftListe.length-1){ //Vi går nedover i Treet og fyller inn tomrom eller noder utifra hvor de er plassert
            if (treNode != null) {
                add(treNode.vBlad, index + 1);
                add(treNode.hBlad, index + 1);
            }
            else {
                add(null, index+1);
                add(null, index+1);
            }
        }
    }


    /**
     * Klassen som representerer nodene i binærtreet vårt
     */
    static class TreNode{
        String ord;
        TreNode vBlad = null;
        TreNode hBlad = null;

        TreNode(String ord){
            this.ord = ord;
        }
    }

    public static void main(String[] args) {
        BinaryTree binaryTree = new BinaryTree();
        binaryTree.nyTreNode("vamos");
        binaryTree.nyTreNode("a");
        binaryTree.nyTreNode("la");
        binaryTree.nyTreNode("playa");
        binaryTree.nyTreNode("me");
        binaryTree.nyTreNode("gusta");
        binaryTree.nyTreNode("bailar");
        binaryTree.print();
    }
}

