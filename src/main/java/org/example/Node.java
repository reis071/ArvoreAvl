package org.example;

public class Node {
    int valor;
    int altura;
    Node esquerda;
    Node direita;

    Node(int valor) {
        this.valor = valor;
        this.altura = 1;
    }
}
