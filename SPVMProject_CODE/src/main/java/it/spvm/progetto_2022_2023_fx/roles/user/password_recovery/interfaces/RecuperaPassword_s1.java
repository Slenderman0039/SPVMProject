package it.spvm.progetto_2022_2023_fx.roles.user.password_recovery.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.user.password_recovery.controls.RecuperaController;
import javafx.fxml.FXML;
import javafx.scene.control.*;


public class RecuperaPassword_s1 {
    @FXML
    private Label label_sistema;
    @FXML
    private TextField email_recupero;
    @FXML
    private TextField codice_recupero;


    @FXML
    protected void onSendCodeEmailClick() {
        RecuperaController.onSendCodeByEmail(email_recupero,label_sistema);
    }
    @FXML
    protected void onConfirmCodeClick() {
        RecuperaController.confirmCode(codice_recupero,email_recupero,label_sistema);
    }
}
