package org.example;

public class Node {
    int value;
    Node left;
    Node right;
    int height;
    int id; // Para identificação única no front-end
    static int idCounter = 0;

    public Node(int value) {
        this.value = value;
        this.height = 1; // Altura inicial de um novo nó é 1
        this.id = idCounter++;
    }

    // Getters para serialização JSON (opcional, Gson pode usar campos diretamente)
    public int getValue() { return value; }
    public Node getLeft() { return left; }
    public Node getRight() { return right; }
    public int getHeight() { return height; }
    public int getId() { return id; }
}
