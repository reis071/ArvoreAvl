package org.example;


// Classe Node que estava faltando
class Node {
    int value;
    int height;
    int id;
    Node left, right;
    static int idCounter = 0;
    
    Node(int value) {
        this.value = value;
        this.height = 0; // Altura de folha é 0
        this.id = ++idCounter;
        this.left = this.right = null;
    }


    
    public int getValue() { return value; }
    public Node getLeft() { return left; }
    public Node getRight() { return right; }
    public int getHeight() { return height; }
    public int getId() { return id; }
}