package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AVLTree avl = new AVLTree();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEscolha uma opção:");
            System.out.println("1 - Inserir");
            System.out.println("2 - Remover");
            System.out.println("3 - Buscar");
            System.out.println("4 - Exibir Árvore");
            System.out.println("5 - Número de Rotações");
            System.out.println("6 - Sair");

            int opcao = scanner.nextInt();
            switch (opcao) {
                case 1:
                    System.out.print("Valor para inserir: ");
                    int valorInserir = scanner.nextInt();
                    avl.inserir(valorInserir);
                    break;
                case 2:
                    System.out.print("Valor para remover: ");
                    int valorRemover = scanner.nextInt();
                    avl.remover(valorRemover);
                    break;
                case 3:
                    System.out.print("Valor para buscar: ");
                    int valorBuscar = scanner.nextInt();
                    System.out.println(avl.buscar(valorBuscar) ? "Encontrado!" : "Não encontrado.");
                    break;
                case 4:
                    avl.exibirArvore();
                    break;
                case 5:
                    System.out.println("Número de rotações realizadas: " + avl.getNumeroDeRotacoes());
                    break;
                case 6:
                    System.out.println("Saindo...");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}
