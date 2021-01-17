package algorithms;

public class Node {
    public Node parent = null;
    public Node left = null;
    public Node right = null;
    protected boolean isNYT = false;
    protected boolean isLeaf = false;
    private int weight;
    private int index;
    private int value;

    public Node(Node parent, Node left, Node right, int weight, int index) {
        this.parent = parent;
        this.weight = weight;
        this.index = index;
    }

    public Node(Node parent) {
        this.parent = parent;
        this.weight = 0;
        this.index = 0;
        this.isNYT = true;
    }

    public Node(Node parent, int value) {
        this.parent = parent;
        this.weight = 1;
        this.index = 1;
        this.value = value;
        this.isLeaf = true;
        this.isNYT = false;
    }

    public boolean isLeaf() { return this.isLeaf; }

    public boolean isNYT() { return this.isNYT; }

    public String toString() {
        if(this.isLeaf) { return " index: "+this.index+" weight: "+this.weight+" value: "+this.value+" AM LEAF"; }
        else if(this.isNYT) { return " index: "+this.index+" weight: "+this.weight+" AM NYT"; }
        else { return " index: "+this.index+" weight: "+this.weight+" AM INTERNAL"; }
    }

    public void setWeight(int weight) { this.weight = weight; }
    public int getWeight() { return this.weight; }
    public void increment() { this.weight++; }
    public int getIndex() { return this.index; }
    public void setIndex(int index) { this.index = index; }
    public int getValue() { return this.value; }
}
