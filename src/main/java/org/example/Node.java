
package org.example;

public class Node {
    int value;
    Node left;
    Node right;
    int height;
    int id;
    static int idCounter = 0;
    

    int x, y;

    public Node(int value) {
        this.value = value;
        this.height = 0;
        this.id = idCounter++;
    }

    // Getters
    public int getValue() { return value; }
    public Node getLeft() { return left; }
    public Node getRight() { return right; }
    public int getHeight() { return height; }
    public int getId() { return id; }
}