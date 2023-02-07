package it.spvm.progetto_2022_2023_fx.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Validator {
    //Classe che verifica se i campi inseriti sono corretti oppure no
    //Object... field prende in input un insieme di field e le controlla
    public static boolean validator_form(Object controller, Object... field){
        boolean flag = false;
        String campo="";
        for(int i=0;i<field.length;++i){
            if(field[i] instanceof TextField){
                TextField tf = (TextField) field[i];
                if(tf.getText().isBlank()){
                    flag = true;
                    campo = tf.getPromptText();
                }
            }else if(field[i] instanceof PasswordField){
                PasswordField pf = (PasswordField) field[i];
                if(pf.getText().isBlank()) {
                    flag = true;
                    campo = pf.getPromptText();
                }
            }
        }
        if(flag){
            Alert alert = new Alert(Alert.AlertType.NONE, "Attenzione: Il campo '"+campo+"' risulta vuoto", ButtonType.OK);
            alert.setTitle("Errore - Campo " + campo);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(controller.getClass().getResource("/assets/icons/error.png").toString()));
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert.close();
            }
        }
        return flag;
    }
}
