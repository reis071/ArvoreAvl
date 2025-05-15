package org.example;

public class AVLTree {
    private Node raiz;
    private int rotacoes = 0;

    public void inserir(int valor) {
        raiz = inserir(raiz, valor);
    }

    private Node inserir(Node node, int valor) {
        if (node == null) {
            return new Node(valor);
        }

        if (valor < node.valor) {
            node.esquerda = inserir(node.esquerda, valor);
        } else if (valor > node.valor) {
            node.direita = inserir(node.direita, valor);
        } else {
            return node; // não permite valores duplicados
        }

        atualizarAltura(node);
        return balancear(node);
    }

    public void remover(int valor) {
        raiz = remover(raiz, valor);
    }

    private Node remover(Node node, int valor) {
        if (node == null) {
            return null;
        }

        if (valor < node.valor) {
            node.esquerda = remover(node.esquerda, valor);
        } else if (valor > node.valor) {
            node.direita = remover(node.direita, valor);
        } else {
            // Nó encontrado
            if (node.esquerda == null || node.direita == null) {
                node = (node.esquerda != null) ? node.esquerda : node.direita;
            } else {
                Node menor = menorValor(node.direita);
                node.valor = menor.valor;
                node.direita = remover(node.direita, menor.valor);
            }
        }

        if (node == null) {
            return null;
        }

        atualizarAltura(node);
        return balancear(node);
    }

    private Node menorValor(Node node) {
        while (node.esquerda != null) {
            node = node.esquerda;
        }
        return node;
    }

    public boolean buscar(int valor) {
        return buscar(raiz, valor);
    }

    private boolean buscar(Node node, int valor) {
        if (node == null) {
            return false;
        }
        if (valor == node.valor) {
            return true;
        } else if (valor < node.valor) {
            return buscar(node.esquerda, valor);
        } else {
            return buscar(node.direita, valor);
        }
    }

    private void atualizarAltura(Node node) {
        node.altura = 1 + Math.max(altura(node.esquerda), altura(node.direita));
    }

    private int altura(Node node) {
        return (node == null) ? 0 : node.altura;
    }

    private int fatorBalanceamento(Node node) {
        return (node == null) ? 0 : altura(node.esquerda) - altura(node.direita);
    }

    private Node balancear(Node node) {
        int balance = fatorBalanceamento(node);

        if (balance > 1) {
            if (fatorBalanceamento(node.esquerda) < 0) {
                node.esquerda = rotacaoEsquerda(node.esquerda);
            }
            return rotacaoDireita(node);
        }

        if (balance < -1) {
            if (fatorBalanceamento(node.direita) > 0) {
                node.direita = rotacaoDireita(node.direita);
            }
            return rotacaoEsquerda(node);
        }

        return node;
    }

    private Node rotacaoDireita(Node y) {
        Node x = y.esquerda;
        Node T2 = x.direita;

        x.direita = y;
        y.esquerda = T2;

        atualizarAltura(y);
        atualizarAltura(x);

        rotacoes++;
        return x;
    }

    private Node rotacaoEsquerda(Node x) {
        Node y = x.direita;
        Node T2 = y.esquerda;

        y.esquerda = x;
        x.direita = T2;

        atualizarAltura(x);
        atualizarAltura(y);

        rotacoes++;
        return y;
    }

    public void exibirArvore() {
        exibirArvore(raiz, "", true);
    }

    private void exibirArvore(Node node, String prefixo, boolean isTail) {
        if (node != null) {
            System.out.println(prefixo + (isTail ? "└── " : "├── ") + node.valor);
            exibirArvore(node.direita, prefixo + (isTail ? "    " : "│   "), false);
            exibirArvore(node.esquerda, prefixo + (isTail ? "    " : "│   "), true);
        }
    }

    public int getNumeroDeRotacoes() {
        return rotacoes;
    }
}
