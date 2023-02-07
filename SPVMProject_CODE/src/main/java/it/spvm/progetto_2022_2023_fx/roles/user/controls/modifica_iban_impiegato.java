package it.spvm.progetto_2022_2023_fx.roles.user.controls;

import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.roles.employee.interfaces.DashboardImpiegato;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;


public class modifica_iban_impiegato extends DashboardImpiegato {

    @FXML
    private Text new_iban;

    @FXML
    private TextField new_iban_field;

    @FXML
    private Text current_iban;


    @FXML
    protected void clickConferma() throws IOException {
        UserController.onClickAggiornaIBAN(new_iban_field, current_iban, new_iban);
    }

    @FXML
    protected void anteprimaIBAN(){
        addTextLimiter(new_iban_field,27);
        new_iban.setText(new_iban_field.getText());
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
}

