package it.spvm.progetto_2022_2023_fx.roles.user.password_recovery.controls;

import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.utils.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import java.io.IOException;

public class RecuperaController {
    private static int code;
    private static String prefix_sistema = "Sistema: ";

    public static Stage recupera_1;

    private static String email;


    public static void onSendCodeByEmail(TextField email_recupero, Label label_sistema) {
        if (!Validator.validator_form(Main.class, email_recupero)) {
            if (email_recupero.getText().contains("@")) {
                if (DBMSManager.queryVerificaIndirizzoEmail(email_recupero.getText())) {
                    code = GeneratorCodes.generate();
                    try {
                        Smtp_mail smtp = new Smtp_mail();
                        smtp.sendMessage(email_recupero.getText(), code);
                        email = email_recupero.getText();
                        label_sistema.setText(prefix_sistema + "Codice inviato con successo!");
                        label_sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
                        label_sistema.setTextFill(Color.GREEN);
                    } catch (MessagingException mex) {
                        if (mex.getCause().getMessage().contains("invalid address")) {
                            label_sistema.setText(prefix_sistema + "Email non valida!");
                            label_sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
                            label_sistema.setTextFill(Color.RED);
                        }
                        code = 0;
                    }
                    System.out.println("Valore del codice: " + code);
                } else {
                    Alert.showDialog(false, "Errore", "Email errata!");
                }
            }else{
                Alert.showDialog(false, "Errore", "Email non valida!");
            }
        }
    }


public static void confirmCode(TextField codice_recupero,TextField email_recupero,Label label_sistema){
    if (code != 0) {
        if (!Validator.validator_form(Main.class, codice_recupero)) {
            if (codice_recupero.getText().equalsIgnoreCase(Integer.valueOf(code).toString())) {
                label_sistema.setText(prefix_sistema + "Codice corretto!");
                label_sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
                label_sistema.setTextFill(Color.GREEN);

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/it/spvm/progetto_2022_2023_fx/pages/recupera_step2.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 514, 394);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                stage.setResizable(false);
                stage.setTitle("Recupero password - Step 2");
                stage.setScene(scene);
                stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
                stage.show();
                recupera_1 = stage;
                Login.stage_recupero_step1.close();

            } else {
                label_sistema.setText(prefix_sistema + "Codice errato!");
                label_sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
                label_sistema.setTextFill(Color.RED);
                code = 0;
                email_recupero.clear();
                codice_recupero.clear();
            }

        }
    }else{
        label_sistema.setText(prefix_sistema + "Devi prima richiedere il codice!");
        label_sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        label_sistema.setTextFill(Color.RED);
    }
}

public static void updatePassword(PasswordField password_recupero, PasswordField confirm_password_recupero, Label sistema){
    if(password_recupero.getText().isBlank() || password_recupero.getText().isEmpty() || confirm_password_recupero.getText().isEmpty() || confirm_password_recupero.getText().isBlank()){
        sistema.setVisible(true);
        sistema.setText(prefix_sistema + "Campi vuoti");
        sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        sistema.setTextFill(Color.RED);
    }else{
        if(password_recupero.getText().equals(confirm_password_recupero.getText())){
            if(DBMSManager.queryRecuperaPasswordImpiegato(email,password_recupero.getText())){
                Alert.showDialog(true,"Successo","Password modificata con successo!");
                RecuperaController.recupera_1.close();
                Login.mainstage.toFront();
            }

        }
    }
}
}
