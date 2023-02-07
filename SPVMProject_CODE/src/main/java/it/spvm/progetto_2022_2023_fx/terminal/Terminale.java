package it.spvm.progetto_2022_2023_fx.terminal;

import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.terminal.controls.TerminaleController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ScheduledExecutorService;


public class Terminale extends Application {

    public static boolean first_avvio = true;
    public static LocalDate data_genera_turni = null;
    public static boolean calcola_stipendi = true;
    public static boolean generati = false;
    public static Stage mainstage;
    public static Stage ingressostage;
    public static Stage uscitastage;

    public static ScheduledExecutorService e;


    @Override
    public void start(Stage stage) throws IOException {
        mainstage = stage;
        //smtp_mail = new Smtp_mail();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("terminale.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
        stage.setResizable(false);
        stage.setTitle("Terminale");
        scene.getStylesheets().add(Login.class.getResource("/fontstyle.css").toExternalForm());
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
        stage.setScene(scene);

        TerminaleController.Terminale(stage,scene);

    }
    //evento del click nel bottone di ingresso
    @FXML
    protected void onClickIngresso() throws IOException {
       mainstage.close();
       e.close();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/pages/terminale/ingresso_terminale.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
        stage.setResizable(false);
        stage.setTitle("Terminale - Ingresso");
        scene.getStylesheets().add(Login.class.getResource("/fontstyle.css").toExternalForm());
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));

        stage.setScene(scene);

        //evento per chiudere il Thread
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        e.close();
                        System.exit(0);
                    }
                });
            }});
        stage.show();
        ingressostage = stage;
    }

    //evento del click nel bottone di uscita
    @FXML
    protected void onClickUscita() throws IOException {
        mainstage.close();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/pages/terminale/uscita_terminale.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
        stage.setResizable(false);
        stage.setTitle("Terminale - Uscita");
        scene.getStylesheets().add(Login.class.getResource("/fontstyle.css").toExternalForm());
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));

        stage.setScene(scene);
        //evento per chiudere il Thread
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        e.close();
                        System.exit(0);
                    }
                });
            }});
        stage.show();
        uscitastage = stage;
    }
    public static void main(String[] args) {
        launch();
    }
}