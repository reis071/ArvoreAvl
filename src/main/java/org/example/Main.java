package org.example;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        AVLTree tree = new AVLTree();
        Gson gson = new Gson();

        staticFiles.location("/public");

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

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

        get("/tree", (req, res) -> {
            res.type("application/json");
            return gson.toJson(tree.getTreeData());
        });

        post("/insert", (req, res) -> {
            res.type("application/json");
            try {
                String payload = req.body();
                InputValue input = gson.fromJson(payload, InputValue.class);
                if (tree.search(input.value)) {
                    Map<String, Object> result = tree.getTreeData();
                    result.put("message", "Valor " + input.value + " já existe na árvore.");
                    return gson.toJson(result);
                }
                tree.insert(input.value);
                return gson.toJson(tree.getTreeData());
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Valor inválido ou erro no servidor."));
            }
        });

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
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(Map.of("error", "Valor inválido ou erro no servidor."));
            }
        });

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

        post("/reset", (req, res) -> {
            tree.reset(); // CORREÇÃO: Usando o método encapsulado da árvore
            res.type("application/json");
            return gson.toJson(tree.getTreeData());
        });

        System.out.println("Servidor AVL Tree rodando em http://localhost:4567");
    }

    static class InputValue {
        int value;
    }
}