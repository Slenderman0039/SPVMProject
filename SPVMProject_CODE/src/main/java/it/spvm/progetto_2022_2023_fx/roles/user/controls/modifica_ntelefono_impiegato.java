package it.spvm.progetto_2022_2023_fx.roles.user.controls;

import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.roles.employee.interfaces.DashboardImpiegato;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;

public class modifica_ntelefono_impiegato extends DashboardImpiegato {


    @FXML
    private Text current_ntelefono;

    @FXML
    private TextField new_telefono_field;


    //CONTROLLI DB
    @FXML
    protected void clickConferma() throws IOException {
        UserController.onClickAggiornaTelefono(new_telefono_field,current_ntelefono);
    }

    @FXML
    protected void onInsertTelefono(){
        new_telefono_field.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (!newValue.matches("\\d*")) {
                            new_telefono_field.setText(newValue.replaceAll("[^\\d]", ""));
                        }else{
                            if (new_telefono_field.getText().length() > 10) {
                                String s = new_telefono_field.getText().substring(0, 10);
                                new_telefono_field.setText(s);
                            }
                        }
            }
        });
    }
}

