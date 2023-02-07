package it.spvm.progetto_2022_2023_fx.roles;

import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


public class Login extends Application {

    @FXML
    private TextField matricola_auth;
    @FXML
    private PasswordField password_auth;

    public static Stage stage_recupero_step1;
    public static Stage dashboard_impiegato;
    public static Stage mainstage;


    @Override
    public void start(Stage stage) throws IOException {
        mainstage = stage;
        //smtp_mail = new Smtp_mail();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
        stage.getIcons().add(new Image(getClass().getResource("/assets/images/logo.png").toExternalForm()));
        stage.setResizable(false);
        stage.setTitle("Autenticazione");
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }

    //Una volta che ho compilato i campi di login verifico se sono validi
    @FXML
    protected void onAuthButtonClick() {
       Index.validator_form(matricola_auth,password_auth);
    }

    //Evento click recuperaPassword
    @FXML
    protected void onPasswordRecoveryClick() {
        mainstage.toBack();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/pages/recupera_step1.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 450);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setResizable(false);
        stage.getIcons().add(new Image(getClass().getResource("/assets/images/logo.png").toExternalForm()));
        stage.setTitle("Recupero password - Step 1");
        stage.setScene(scene);
        stage.show();
        stage_recupero_step1 = stage;
    }
    //Ogni volta che immetto un valore sul campo matricola verifico che non sia una lettera e che sia massimo di 10 caratteri
    @FXML
    protected void checkMatricola(){
        addTextLimiter(matricola_auth,10);
        matricola_auth.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    matricola_auth.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    //imposto i valori della dashboard di partenza (nome,cognome,iban,...)
    public static void setDashboardData(Scene scene){
        Text t = (Text) scene.lookup("#cognome_title");
        t.setText(DBMSManager.getCurrentImpiegato().getCognome());
        Label iban = (Label) scene.lookup("#iban");
        iban.setText("IBAN: " +DBMSManager.getCurrentImpiegato().getIBAN());
        TextField tf_nome = (TextField) scene.lookup("#nome");
        tf_nome.setText(DBMSManager.getCurrentImpiegato().getNome());
        TextField tf_cognome = (TextField) scene.lookup("#cognome");
        tf_cognome.setText(DBMSManager.getCurrentImpiegato().getCognome());
        TextField tf_telefono = (TextField) scene.lookup("#n_telefono");
        tf_telefono.setText(DBMSManager.getCurrentImpiegato().getN_telefono());
        TextField tf_nascita = (TextField) scene.lookup("#d_nascita");
        tf_nascita.setText(DBMSManager.getCurrentImpiegato().getD_nascita().toString());
        TextField tf_servizio = (TextField) scene.lookup("#servizio");
        tf_servizio.setText(DBMSManager.getCurrentImpiegato().getServizio());
        TextField tf_mansione = (TextField) scene.lookup("#mansione");
        tf_mansione.setText(DBMSManager.getCurrentImpiegato().getMansione());
    }

    public static void setDashboardDataAdmin(Scene scene){
        Text t = (Text) scene.lookup("#cognome_title");
        t.setText(DBMSManager.getCurrentImpiegato().getCognome());
        Label iban = (Label) scene.lookup("#iban");
        iban.setText("IBAN: " +DBMSManager.getCurrentImpiegato().getIBAN());
        TextField tf_nome = (TextField) scene.lookup("#nome");
        tf_nome.setText(DBMSManager.getCurrentImpiegato().getNome());
        TextField tf_cognome = (TextField) scene.lookup("#cognome");
        tf_cognome.setText(DBMSManager.getCurrentImpiegato().getCognome());
        TextField tf_telefono = (TextField) scene.lookup("#n_telefono");
        tf_telefono.setText(DBMSManager.getCurrentImpiegato().getN_telefono());
        TextField tf_nascita = (TextField) scene.lookup("#d_nascita");
        tf_nascita.setText(DBMSManager.getCurrentImpiegato().getD_nascita().toString());
        TextField tf_mansione = (TextField) scene.lookup("#mansione");
        tf_mansione.setText(DBMSManager.getCurrentImpiegato().getMansione());
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