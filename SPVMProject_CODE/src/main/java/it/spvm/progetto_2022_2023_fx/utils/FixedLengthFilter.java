package it.spvm.progetto_2022_2023_fx.utils;


import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;


public class FixedLengthFilter implements UnaryOperator<TextFormatter.Change> {
    private final int maxLength;

    //costruttore
    public FixedLengthFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    //metodo che imposta il numero massimo di cifre in un campo
    public TextFormatter.Change apply(TextFormatter.Change change) {
        if (change.getControlNewText().length() > maxLength) {
            return null;
        }
        return change;
    }
}
