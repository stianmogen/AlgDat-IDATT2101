package oblig3;
import java.util.Scanner;

public class DoubleLinkedList {

    Node head;
    Node tail;

    public DoubleLinkedList(){
        this.head = null;
        this.tail = null;
    }

    public void addFront(int value){
        head = new Node(value, head, null);
        if (tail == null) tail = head; //if there is no tail, the list will be 1 long, therefore the head will also be the tail
        else head.next.prev = head; //if not, the next node will be assigned the current one, as its previous
    }

    public void addBack(int value){
        tail = new Node(value, null, tail);
        if (head == null) head = tail;
        else tail.prev.next = tail;
    }

    public void valueAsDLL(int value){
        do {
            //we add the last digit
            addBack(value % 10);
            //the we divide by then to be able to add the next digit of the integer
            value = value / 10;
            //we do this as long as the value is above zero
        }while (value > 0);
    }

    public DoubleLinkedList addition(DoubleLinkedList dll){
        DoubleLinkedList result = new DoubleLinkedList();
        if (dll.tail == null) return this; //checks if dll is empty, if so returns this
        if (this.tail == null) return dll; //checks this tail is empty, if so returns the dll
        int mem = 0, sum;
        Node n1 = this.tail;
        Node n2 = dll.tail;
        while (n1 != null || n2 != null){
            int add1 = 0; //declares to values for addition
            int add2 = 0;
            if (n1 != null) add1 = n1.value; //these are set to the value if it exists, else it stays at zero
            if (n2 != null) add2 = n2.value;
            sum = add1 + add2 + mem; //add the two values as well as the memory
            mem = (sum >= 10) ? 1 : 0; //if the sum i greater than 10, we assign mem as 1, else 0
            sum = sum % 10; //sum is set to the modulo of 10, in case of value greater than 10
            result.addFront(sum);//we add the results to the front of the results list, to return correct format
            if (n1 != null) n1 = n1.prev;
            if (n2 != null) n2 = n2.prev;
        }
        if (mem > 0) result.addFront(mem);
        return result;
    }

    /**
     * Method that substracts the doublelinkedlist argument, from the original linked list
     * @param dll
     * @return
     */
    public DoubleLinkedList subtraction(DoubleLinkedList dll){
        DoubleLinkedList result = new DoubleLinkedList();
        if (dll.tail == null) return this;
        if (this.tail == null) return dll;
        int res;
        int rent = 0;
        Node n1 = this.tail;
        Node n2 = dll.tail;
        while (n1 != null || n2 != null){
            int num1 = 0;
            int num2 = 0;
            if (n1 != null) num1 = n1.value;
            if (n2 != null) num2 = n2.value;
            res = num1 - num2 - rent; //similar to addition, but here we naturally subtract from number 1
            rent = (res < 0) ? 1 : 0;
            res = res % 10;
            if (res < 0) res = 10 + res; //modulo may not be sufficient, if result is negative, we add 10 to it to get correct result
            result.addFront(res);
            if (n1 != null) n1 = n1.prev;
            if (n2 != null) n2 = n2.prev;
        }
        return result;
    }

    public void formatAns(){
        while (this.head != null){
            System.out.print(this.head.value + " ");
            head = head.next;
        }
    }

    /**
     * Method to transfer integers from a string, into our double linked list
     * @param val
     */
    public void stringToInts(String val){
        //iterate through the string and add the characters one and one
        for (int i = 0; i < val.length(); i++){
            int insert = Integer.parseInt(val.subSequence(i, i+1).toString());
            valueAsDLL(insert);
        }
    }

    static class Node{
        int value;
        Node next;
        Node prev;
        Node(int value){
            this.value = value;
            this.next = null;
            this.prev = null;
        }
        Node(int value, Node next, Node prev){
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DoubleLinkedList dll = new DoubleLinkedList();
        DoubleLinkedList dll2 = new DoubleLinkedList();

        System.out.println("This program does not take negative numbers into account\nIf you want to substract two values from each other, please write the largest integer first");

        while (true) {
            System.out.println("\nWrite first number");
            String val1 = sc.next();
            System.out.println("What operation do you want, '+' or '-'");
            String op = sc.next();
            System.out.println("Write second number");
            String val2 = sc.next();
            if (op.equals("+")){
                DoubleLinkedList ans;
                dll.stringToInts(val1);
                dll.formatAns();
                System.out.println("\n+");
                dll2.stringToInts(val2);
                dll2.formatAns();
                System.out.println("\n=");
                ans = dll.addition(dll2);
                ans.formatAns();
            } else if (op.equals("-")) {
                dll.stringToInts(val1);
                dll.formatAns();
                System.out.println("\n-");
                dll2.stringToInts(val2);
                dll2.formatAns();
                System.out.println("\n=");
                dll.subtraction(dll2).formatAns();
            }
        }
    }
}