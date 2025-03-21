package com.ConversorMoedas;

import com.ConversorMoedas.service.ConversorMoedas;

public class Main {
    public static void main(String[] args){
        while(true){
            ConversorMoedas.exibirMenu();
            ConversorMoedas.converterMoeda();
        }
    }
}
