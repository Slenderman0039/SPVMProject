package it.spvm.progetto_2022_2023_fx.roles;


import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import it.spvm.progetto_2022_2023_fx.utils.Validator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Index  {

    //metodo che smista le interfacce (User - Employee - Admin)
    public static void validator_form(TextField matricola_auth, PasswordField password_auth){
        if (!Validator.validator_form(Index.class, password_auth, matricola_auth)) {
            Impiegato account = DBMSManager.getImpiegatoWhere(Integer.valueOf(matricola_auth.getText()).intValue(),password_auth.getText());
            if(account != null) {
                Stage stage = new Stage();
                Scene scene = null;
                if(account.getMansione().equalsIgnoreCase("employee")){
                    FXMLLoader fxmlLoader = new FXMLLoader(Index.class.getResource("/it/spvm/progetto_2022_2023_fx/pages/dashboard_impiegato.fxml"));
                    try {
                        scene = new Scene(fxmlLoader.load(), 600, 400);
                        stage.setResizable(false);
                        stage.setTitle("Dashboard - Employee");
                        stage.setScene(scene);
                        stage.show();
                        Login.setDashboardData(scene);
                        Login.dashboard_impiegato = stage;
                        Login.mainstage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if(account.getMansione().equalsIgnoreCase("user")){
                    FXMLLoader fxmlLoader = new FXMLLoader(Index.class.getResource("/it/spvm/progetto_2022_2023_fx/pages/dashboard_utente.fxml"));
                    try {
                        scene = new Scene(fxmlLoader.load(), 600, 400);
                        stage.setResizable(false);
                        stage.setTitle("Dashboard - Utente");
                        stage.setScene(scene);
                        stage.show();
                        Login.setDashboardData(scene);
                        Login.dashboard_impiegato = stage;
                        Login.mainstage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else if(account.getMansione().equalsIgnoreCase("admin")){
                    FXMLLoader fxmlLoader = new FXMLLoader(Index.class.getResource("/it/spvm/progetto_2022_2023_fx/pages/dashboard_amministratore.fxml"));
                    try {
                        scene = new Scene(fxmlLoader.load(),  600, 620);
                        stage.setResizable(false);
                        stage.setTitle("Dashboard - Admin");
                        stage.setScene(scene);
                        stage.show();
                        Login.setDashboardDataAdmin(scene);
                        Login.dashboard_impiegato = stage;
                        Login.mainstage.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    System.out.println("ATTENZIONE: MANSIONE NON RICONOSCIUTA!");
                    System.exit(0);
                }
                stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
            }
        }
    }

}