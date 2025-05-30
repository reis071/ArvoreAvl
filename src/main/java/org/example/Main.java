package org.example;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
// Removi o import do Scanner, pois não estava sendo usado.
// Se você precisar dele para outra coisa, pode readicioná-lo.

import static spark.Spark.*; // Importante para staticFiles, options, before, get, post

public class Main {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        Gson gson = new Gson();

        // ----- ADICIONE A LINHA ABAIXO AQUI -----
        // Serve arquivos estáticos da pasta /public no classpath
        staticFiles.location("/public");
        // -----------------------------------------

        // Configurar o CORS para permitir requisições do front-end local
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*")); // Permitir todas as origens

        // Endpoint para obter a árvore atual
        get("/tree", (req, res) -> {
            res.type("application/json");
            return gson.toJson(tree.getTreeData());
        });

        // Endpoint para inserir um valor
        post("/insert", (req, res) -> {
            res.type("application/json");
            try {
                String payload = req.body();
                InputValue input = gson.fromJson(payload, InputValue.class);
                if (tree.search(input.value)) { // Evitar duplicados explicitamente
                    Map<String, Object> result = tree.getTreeData();
                    result.put("message", "Valor " + input.value + " já existe na árvore.");
                    return gson.toJson(result);
                }
                tree.insert(input.value);
                return gson.toJson(tree.getTreeData());
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Valor inválido. Insira um número."));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", "Erro interno: " + e.getMessage()));
            }
        });

        // Endpoint para remover um valor
        post("/delete", (req, res) -> {
            res.type("application/json");
            try {
                String payload = req.body();
                InputValue input = gson.fromJson(payload, InputValue.class);
                if (!tree.search(input.value)) {
                    Map<String, Object> result = tree.getTreeData();
                    result.put("message", "Valor " + input.value + " não encontrado para remoção.");
                    return gson.toJson(result);
                }
                tree.delete(input.value);
                return gson.toJson(tree.getTreeData());
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Valor inválido. Insira um número."));
            } catch (Exception e) {
                res.status(500);
                return gson.toJson(Map.of("error", "Erro interno: " + e.getMessage()));
            }
        });

        // Endpoint para buscar um valor (apenas informa se existe)
        get("/search/:value", (req, res) -> {
            res.type("application/json");
            try {
                int value = Integer.parseInt(req.params(":value"));
                boolean found = tree.search(value);
                Map<String, Object> result = new HashMap<>();
                result.put("found", found);
                result.put("value", value);
                return gson.toJson(result);
            } catch (NumberFormatException e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Valor inválido. Insira um número."));
            }
        });

        // Endpoint para resetar a árvore e as contagens
        post("/reset", (req, res) -> {
            tree.root = null; // Limpa a árvore
            Node.idCounter = 0; // Reseta o contador de ID dos nós
            tree.resetRotations();
            res.type("application/json");
            return gson.toJson(tree.getTreeData());
        });

        System.out.println("Servidor AVL Tree rodando em http://localhost:4567");
    }

    // Classe auxiliar para desserializar o JSON de entrada
    static class InputValue {
        int value;
    }
}