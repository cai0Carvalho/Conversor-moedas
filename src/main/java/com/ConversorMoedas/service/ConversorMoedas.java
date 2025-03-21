package com.ConversorMoedas.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConversorMoedas {
    private static final String API_KEY = "84ddfa3700d83350b99d1ec3";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";

    public static void exibirMenu(){
        System.out.println("------ Conversor de Moedas ------\n" +
                            "1. Dólar para Peso Argentino \n" +
                            "2. Dólar para Real \n" +
                            "3. Dólar para Euro \n" +
                            "4. Peso argentino para Real\n" +
                            "5. Real para Peso Argentino\n" +
                            "6. sair\n" +
                            "escolha uma opção: ");
    }

    //Faz uma requisição HTTP à API para obter taxas de câmbio, lê a resposta, extrai as taxas para as moedas escolhidas e calcula a taxa.
    private static double obterTaxaCambio(String de, String para) throws IOException{
        HttpURLConnection conn = (HttpURLConnection) URI.create(API_URL).toURL().openConnection();
        conn.setRequestMethod("GET");

        if(conn.getResponseCode() != 200){
            throw new RuntimeException("Erro HTTP: " + conn.getResponseCode());
        }

        // Lê a resposta da API
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
            JsonObject taxas = jsonObject.getAsJsonObject("conversion_rates");

            // Obtém a taxa de câmbio das moedas
            double taxaDe = taxas.get(de).getAsDouble();
            double taxaPara = taxas.get(para).getAsDouble();

            return taxaPara / taxaDe;
        }
    }

    // Converte o valor com base na taxa de câmbio
    public static double converter(double valor, String de, String para) throws IOException{
        double taxa = obterTaxaCambio(de, para);
        return valor * taxa;
    }

        // Verifica se a opção escolhida é válida
    public static int lerOpcaoValida(Scanner sc) {
        while(true) {
            if (sc.hasNextInt()) {
                return sc.nextInt();
            } else {
                System.out.println("Digite um número válido: ");
                sc.next(); // Limpa o buffer
            }
        }
    }

    // Verifica se o valor escolhido é válido
    public static Double lerValorValido(Scanner sc) {
        while (!sc.hasNextDouble()) {
            System.out.println("Digite um número válido: ");
            sc.next(); // Limpa o buffer
        }
        return sc.nextDouble();
    }

    private static void exibirResultadoConversao(int opcao, double valor) throws IOException {
        double resultado = 0;
        String simbolo = "";
    
        switch (opcao) {
            case 1:
                resultado = converter(valor, "USD", "ARS");
                simbolo = "$"; 
                break;
            case 2:
                resultado = converter(valor, "USD", "BRL");
                simbolo = "R$"; 
                break;
            case 3:
                resultado = converter(valor, "USD", "EUR");
                simbolo = ""; 
                break;
            case 4:
                resultado = converter(valor, "ARS", "BRL");
                simbolo = "R$"; 
                break;
            case 5:
                resultado = converter(valor, "BRL", "ARS");
                simbolo = "R$"; 
                break;
            default:
                
                System.out.println("Opção inválida. Tente novamente.");
                return;
        }
        System.out.println("Resultado: " + simbolo + String.format("%.2f", resultado));
    }
    
    // Método principal para realizar a conversão de moedas
    public static void converterMoeda() {
        Scanner sc = new Scanner(System.in);
        int opcao = lerOpcaoValida(sc);

        if (opcao == 6) {
            System.out.println("Saindo...");
            System.exit(0);
        }

        // Realiza a conversão e exibe o resultado
        try {
            System.out.println("Digite o valor para conversão: ");
            double valor = lerValorValido(sc);

            // Exibe o resultado com base na opção escolhida
            exibirResultadoConversao(opcao, valor);

        } catch (IllegalArgumentException e) {
            // Trata exceções relacionadas a argumentos inválidos
            System.out.println("Erro: " + e.getMessage());
        } catch (IOException e) {
            // Trata exceções de IO, como erro ao acessar a API
            System.out.println("Erro ao acessar a API de câmbio: " + e.getMessage());
        } catch (Exception e) {
            // Trata exceções inesperadas
            System.out.println("Erro inesperado: " + e.getMessage());
        }
    }
}
