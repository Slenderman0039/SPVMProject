package it.spvm.progetto_2022_2023_fx.utils;

import it.spvm.progetto_2022_2023_fx.Main;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Alert {

    //metodo principale per PopUpErrore o PopUpConferma
    public static void showDialog(boolean result, String title, String message){
        if(result){
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.NONE, message, ButtonType.OK);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Main.class.getResource("/assets/icons/success.png").toString()));
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert.close();
            }
        }else{
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.NONE, message, ButtonType.OK);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Main.class.getResource("/assets/icons/error.png").toString()));
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert.close();
            }
        }
    }
    //metodo per mostrare le info degli eventi 'Turno' sul calendario
    public static void showDialogEntryCalendar(String title, String message){
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.NONE, message, ButtonType.OK);
            alert.setTitle(title);
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Main.class.getResource("/assets/icons/calendar_icon.png").toString()));
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                alert.close();
            }
    }
}
