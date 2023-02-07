package it.spvm.progetto_2022_2023_fx.roles.user.password_recovery.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.user.password_recovery.controls.RecuperaController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RecuperaPassword_s2 {
    private String prefix_sistema = "Sistema: ";
    @FXML
    private PasswordField confirm_password_recupero;
    @FXML
    private PasswordField password_recupero;
    @FXML
    private Label sistema;
    //@FXML
    //private TextField email_recupero;
    @FXML
    protected void onTypePassword() {
        if(password_recupero.getText().equals(confirm_password_recupero.getText())){
            sistema.setVisible(false);
        }else {
            sistema.setVisible(true);
            sistema.setText(prefix_sistema + "Le password non coincidono");
            sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            sistema.setTextFill(Color.RED);
        }
        }
    @FXML
    protected void onConfirmPassword() {
        if(confirm_password_recupero.getText().equals(password_recupero.getText())){
            sistema.setVisible(false);
        }else{
            sistema.setVisible(true);
            sistema.setText(prefix_sistema + "Le password non coincidono");
            sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            sistema.setTextFill(Color.RED);
        }
    }

    @FXML
    protected void OnConfirmButtonPassword(){
        RecuperaController.updatePassword(password_recupero,confirm_password_recupero,sistema);
    }

}
