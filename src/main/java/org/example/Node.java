package org.example;

public class Node {
    int value;
    Node left;
    Node right;
    int height;
    int id;
    static int idCounter = 0;

    public Node(int value) {
        this.value = value;
        this.height = 0; // CORREÇÃO: A altura inicial de uma folha é 0.
        this.id = idCounter++;
    }

    public int getValue() { return value; }
    public Node getLeft() { return left; }
    public Node getRight() { return right; }
    public int getHeight() { return height; }
    public int getId() { return id; }
}