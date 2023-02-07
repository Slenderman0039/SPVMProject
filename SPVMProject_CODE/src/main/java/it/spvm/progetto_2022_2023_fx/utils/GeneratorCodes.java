package it.spvm.progetto_2022_2023_fx.utils;

import java.util.Random;

public class GeneratorCodes {


    public static int generate(){
        //genera il numero randomico da 0 a 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        //converte il numero randomico a 6 cifre
        return Integer.valueOf(String.format("%06d", number));
    }
}
