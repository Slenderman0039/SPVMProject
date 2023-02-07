package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class inserisci_ritardo_admin {
    @FXML
     TextField matricola;

    @FXML
    TextField motivazione;

    @FXML
    protected void onInsertMatricola(){
        addTextLimiter(matricola,10);
        matricola.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    matricola.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public static void addTextLimiter(final TextField tf, final int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > maxLength) {
                    String s = tf.getText().substring(0, maxLength);
                    tf.setText(s);
                }
            }
        });
    }

    @FXML
    protected void onInserisciFirma(){
        AdminController.onInserisciFirma(matricola,motivazione);
    }
}
