package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AVLTree {

    Node root;
    private int rotationCountInsert = 0;
    private int rotationCountDelete = 0;

    // Retorna a altura de um nó, considerando -1 para nós nulos.
    private int height(Node N) {
        if (N == null) return -1;
        return N.height;
    }

    // Recalcula e atualiza a altura de um nó com base na altura de seus filhos.
    private void updateHeight(Node N) {
        if (N != null) {
            N.height = 1 + Math.max(height(N.left), height(N.right));
        }
    }

    // Calcula o fator de balanceamento de um nó (diferença de altura das sub-árvores).
    private int getBalanceFactor(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    // Realiza uma rotação simples à direita em torno do nó 'y'.
    private Node rotateRight(Node y) {
        if (y == null || y.left == null) return y;
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    // Realiza uma rotação simples à esquerda em torno do nó 'x'.
    private Node rotateLeft(Node x) {
        if (x == null || x.right == null) return x;
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // Verifica o balanceamento de um nó e aplica as rotações necessárias.
    private Node rebalance(Node node, boolean isInsert) {
        int balance = getBalanceFactor(node);
        int singleRotation = 1;
        int doubleRotation = 2;

        // Desbalanceado para a Esquerda
        if (balance > 1) {
            if (getBalanceFactor(node.left) >= 0) { // Caso Esquerda-Esquerda
                if (isInsert) rotationCountInsert += singleRotation; else rotationCountDelete += singleRotation;
                return rotateRight(node);
            } else { // Caso Esquerda-Direita
                if (isInsert) rotationCountInsert += doubleRotation; else rotationCountDelete += doubleRotation;
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
        }

        // Desbalanceado para a Direita
        if (balance < -1) {
            if (getBalanceFactor(node.right) <= 0) { // Caso Direita-Direita
                if (isInsert) rotationCountInsert += singleRotation; else rotationCountDelete += singleRotation;
                return rotateLeft(node);
            } else { // Caso Direita-Esquerda
                if (isInsert) rotationCountInsert += doubleRotation; else rotationCountDelete += doubleRotation;
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
        }
        
        return node;
    }
    
    // Ponto de entrada público para inserir um valor na árvore.
    public void insert(int value) {
        root = insertRec(root, value);
    }

    // Lógica recursiva para encontrar a posição correta e inserir um novo nó.
    private Node insertRec(Node node, int value) {
        if (node == null) return new Node(value);

        if (value < node.value) {
            node.left = insertRec(node.left, value);
        } else if (value > node.value) {
            node.right = insertRec(node.right, value);
        } else {
            return node; // Ignora valores duplicados
        }

        updateHeight(node);
        
        return rebalance(node, true);
    }

    // Ponto de entrada público para deletar um valor da árvore.
    public void delete(int value) {
        root = deleteRec(root, value);
    }
    
    // Encontra o nó com o menor valor em uma sub-árvore (o mais à esquerda).
    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    // Lógica recursiva para encontrar e deletar um nó da árvore.
    private Node deleteRec(Node node, int value) {
        if (node == null) return node;

        if (value < node.value) {
            node.left = deleteRec(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRec(node.right, value);
        } else {
            // Nó com um ou nenhum filho
            if (node.left == null || node.right == null) {
                return (node.left != null) ? node.left : node.right;
            } else {
                // Nó com dois filhos: substitui pelo sucessor em ordem
                Node temp = minValueNode(node.right);
                node.value = temp.value;
                node.right = deleteRec(node.right, temp.value);
            }
        }
        
        if (node == null) return null;

        updateHeight(node);

        return rebalance(node, false);
    }

    // Ponto de entrada público para buscar um valor na árvore.
    public boolean search(int value) {
        return searchRec(root, value);
    }

    // Lógica recursiva para buscar um valor.
    private boolean searchRec(Node root, int value) {
        if (root == null) return false;
        if (root.value == value) return true;
        return value < root.value ? searchRec(root.left, value) : searchRec(root.right, value);
    }
    
    // Limpa a árvore e reseta os contadores.
    public void reset() {
        root = null;
        Node.idCounter = 0;
        resetRotations();
    }

    // Reseta apenas os contadores de rotação.
    public void resetRotations() {
        rotationCountInsert = 0;
        rotationCountDelete = 0;
    }

    // Coleta os dados da árvore para visualização.
    public Map<String, Object> getTreeData() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        populateTreeData(root, nodes, edges, -1);
        data.put("nodes", nodes);
        data.put("edges", edges);
        data.put("rotationCountInsert", rotationCountInsert);
        data.put("rotationCountDelete", rotationCountDelete);
        return data;
    }

    // Percorre a árvore recursivamente para extrair dados dos nós e arestas.
    private void populateTreeData(Node node, List<Map<String, Object>> nodesList, List<Map<String, Object>> edgesList, int parentId) {
        if (node == null) return;
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", node.id);
        nodeMap.put("label", String.valueOf(node.value) + "\n(h:" + node.height + ", bf:" + getBalanceFactor(node) + ")");
        nodesList.add(nodeMap);
        if (parentId != -1) {
            Map<String, Object> edgeMap = new HashMap<>();
            edgeMap.put("from", parentId);
            edgeMap.put("to", node.id);
            edgesList.add(edgeMap);
        }
        populateTreeData(node.left, nodesList, edgesList, node.id);
        populateTreeData(node.right, nodesList, edgesList, node.id);
    }
}