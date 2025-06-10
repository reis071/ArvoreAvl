package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AVLTree {

    Node root;
    private int rotationCountInsert = 0;
    private int rotationCountDelete = 0;

    private int height(Node N) {
        if (N == null) return -1; // -1 para null permite altura 0 para folhas
        return N.height;
    }

    private void updateHeight(Node N) {
        if (N != null) {
            N.height = 1 + Math.max(height(N.left), height(N.right));
        }
    }

    private int getBalanceFactor(Node N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

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

    public void insert(int value) {
        root = insertRec(root, value);
    }

    private Node insertRec(Node node, int value) {
        // Passo 1: Inserção BST padrão
        if (node == null) return new Node(value);

        if (value < node.value) {
            node.left = insertRec(node.left, value);
        } else if (value > node.value) {
            node.right = insertRec(node.right, value);
        } else {
            return node; // Duplicatas não são permitidas
        }

        // Passo 2: Atualizar altura
        updateHeight(node);
        
        // Passo 3: Obter balance factor
        int balance = getBalanceFactor(node);

        // Passo 4: Aplicar rotações se necessário
        // Caso Left Left
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            rotationCountInsert++;
            return rotateRight(node);
        }
        // Caso Left Right
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            rotationCountInsert += 2;
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Caso Right Right
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            rotationCountInsert++;
            return rotateLeft(node);
        }
        // Caso Right Left
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            rotationCountInsert += 2;
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }

    public void delete(int value) {
        root = deleteRec(root, value);
    }
    
    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    private Node deleteRec(Node node, int value) {
        // Passo 1: Remoção BST padrão
        if (node == null) return node;

        if (value < node.value) {
            node.left = deleteRec(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRec(node.right, value);
        } else {
            // Nó a ser removido encontrado
            if (node.left == null || node.right == null) {
                // Nó com um filho ou nenhum filho
                Node temp = (node.left != null) ? node.left : node.right;
                return temp; // Retorna o filho (ou null)
            } else {
                // Nó com dois filhos
                Node temp = minValueNode(node.right);
                node.value = temp.value; // Copia o valor do sucessor
                node.right = deleteRec(node.right, temp.value); // Remove o sucessor
            }
        }

        // Passo 2: Atualizar altura
        updateHeight(node);
        
        // Passo 3: Obter balance factor
        int balance = getBalanceFactor(node);

        // Passo 4: Aplicar rotações se necessário
        // Caso Left Left
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            rotationCountDelete++;
            return rotateRight(node);
        }
        // Caso Left Right
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            rotationCountDelete += 2;
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Caso Right Right
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            rotationCountDelete++;
            return rotateLeft(node);
        }
        // Caso Right Left
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            rotationCountDelete += 2;
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }

    public boolean search(int value) {
        return searchRec(root, value);
    }

    private boolean searchRec(Node root, int value) {
        if (root == null) return false;
        if (root.value == value) return true;
        return value < root.value ? searchRec(root.left, value) : searchRec(root.right, value);
    }
    
    public void reset() {
        root = null;
        Node.idCounter = 0;
        resetRotations();
    }

    public void resetRotations() {
        rotationCountInsert = 0;
        rotationCountDelete = 0;
    }

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

    private void populateTreeData(Node node, List<Map<String, Object>> nodesList, 
                                List<Map<String, Object>> edgesList, int parentId) {
        if (node == null) return;
        
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", node.id);
        nodeMap.put("label", String.valueOf(node.value) + 
                    "\n(h:" + node.height + ", bf:" + getBalanceFactor(node) + ")");
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