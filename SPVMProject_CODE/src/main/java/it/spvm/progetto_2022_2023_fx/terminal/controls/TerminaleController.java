package it.spvm.progetto_2022_2023_fx.terminal.controls;

import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.terminal.Terminale;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import it.spvm.progetto_2022_2023_fx.utils.Generator;
import it.spvm.progetto_2022_2023_fx.utils.Smtp_mail;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.mail.MessagingException;
import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static it.spvm.progetto_2022_2023_fx.terminal.Terminale.calcola_stipendi;
import static it.spvm.progetto_2022_2023_fx.terminal.Terminale.e;

public class TerminaleController {

    public static void firmaIngresso(TextField matricola, TextField nome, TextField cognome) throws IOException {

        if(!matricola.getText().isEmpty()){
            if(!nome.getText().isEmpty()){
                if(!cognome.getText().isEmpty()){
                    int matricola_int = 0;
                    try{ matricola_int = Integer.valueOf(matricola.getText()).intValue();}catch(Exception e){matricola_int = 0;}
                    if(DBMSManager.queryRilevazioneIngresso(matricola_int,nome.getText().toString(),cognome.getText().toString())){
                        Alert.showDialog(true, "Successo", "Hai firmato l'ingresso correttamente!");


                        Stage stage = Terminale.mainstage;
                        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("terminale.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
                        stage.setResizable(false);
                        stage.setTitle("Terminale");
                        scene.getStylesheets().add(Main.class.getResource("/fontstyle.css").toExternalForm());
                        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
                        stage.setScene(scene);
                        stage.show();
                        //chiusura del Thread
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
                        Text timestamp = (Text) scene.lookup("#current");
                        Button b_terminale_i = (Button) scene.lookup("#b_terminale_i");
                        Button b_terminale_u = (Button) scene.lookup("#b_terminale_u");

                        e= Executors.newSingleThreadScheduledExecutor();
                        //avvio del Thread
                        e.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                // do stuff
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        //Controlla e sposta gli impiegati se ad ogni ora esistono servizi chiusi
                                        if(LocalTime.now().getMinute() == 00){
                                            List<Impiegato> lista_impiegati_disponibili = DBMSManager.queryImpiegatiDisponibili();
                                            Map<Impiegato, Turno> lista_richieste_malattie= DBMSManager.queryRichiesteMalattiaImpiegati();
                                            int i = 0;
                                            for(Turno t: lista_richieste_malattie.values()){
                                                if(DBMSManager.queryAggiornaTurni(lista_impiegati_disponibili.get(i),t)){
                                                    Smtp_mail smtp = new Smtp_mail();
                                                    smtp.sendEmail(lista_impiegati_disponibili.get(i).getMatricola(),lista_impiegati_disponibili.get(i).getNome(),lista_impiegati_disponibili.get(i).getCognome(), t);
                                                }
                                            }
                                            ArrayList<Integer> matricole_notifiche = DBMSManager.queryChiusuraServizio();
                                            if(matricole_notifiche !=null){
                                                DBMSManager.queryInserisciNotifica(1000,"E' stato chiuso un servizio.","\uD83D\uDD14 Chiusura servizio");
                                            }
                                        }
                                        //Controlla se il terminale è stato avviato la prima volta
                                        if(Terminale.first_avvio){
                                            Terminale.first_avvio = false;
                                            String ultima_data = DBMSManager.queryUltimoTurno();
                                            //Se non esiste alcun calendario
                                            if(ultima_data.equalsIgnoreCase("")){
                                                //Imposta come data per generare i turni quella di oggi
                                                Terminale.data_genera_turni = LocalDate.now();
                                            }else{
                                                //Altrimenti prnede l'ultimo turno - 3 giorni e imposta la data di quando generare i turni
                                                String dt = ultima_data;  // Start date
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                Calendar c = Calendar.getInstance();
                                                try {
                                                    c.setTime(sdf.parse(dt));
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                c.add(Calendar.DATE, -3);

                                                dt = sdf.format(c.getTime());
                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                LocalDate localDate = LocalDate.parse(dt, formatter);
                                                Terminale.data_genera_turni = localDate;
                                            }
                                        }
                                        //Se è il giorno 24 del mese allora calcola ed inserisce gli stipendi di ciascun impiegato.
                                        if(LocalDate.now().getDayOfMonth() == 24){
                                            if(calcola_stipendi){
                                                HashMap<Integer,Float> stipendi_a = DBMSManager.queryOreLavorative();
                                                if(stipendi_a != null){
                                                    ArrayList<Integer> matricole = new ArrayList<>(stipendi_a.keySet());
                                                    ArrayList<Float> stipendi= new ArrayList<>(stipendi_a.values());
                                                    for(int i=0;i<stipendi.size();++i){
                                                        DBMSManager.queryAggiornaStipendio(matricole.get(i),LocalDate.now().toString(),stipendi.get(i),"Stipendio");
                                                        DBMSManager.queryInserisciNotifica(matricole.get(i),"Stipendio mensile accreditato","\uD83D\uDD14 Stipendio");
                                                    }
                                                    System.out.println("Hai calcolato ed inserito correttamente tutti gli stipendi.");

                                                }else{
                                                    System.out.println("Nessun dipendente ha lavorato.");
                                                }
                                                calcola_stipendi = false;
                                            }
                                        }else{
                                            calcola_stipendi = true;
                                        }
                                        //con la compareto vado a vedere se la data corrente è esattamente uguale alla data della generazione dei turni
                                        if(LocalDate.now().compareTo(Terminale.data_genera_turni) == 0){
                                            if((!Terminale.generati)){
                                                Generator.generateShifts(Terminale.data_genera_turni.toString());
                                                ArrayList<Impiegato> lista_impiegati = DBMSManager.queryImpiegatiTurni();
                                                for(Impiegato i: lista_impiegati){
                                                    DBMSManager.queryInserisciNotifica(i.getMatricola(),"Sono stati generati i turni per il prossimo trimestre.","\uD83D\uDD14 Nuova Turnazione");
                                                }
                                                Terminale.generati = true;
                                            }
                                        }

                                        DateFormat dateandtime = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                                        Date date = new Date();
                                        timestamp.setText(dateandtime.format(date));

                                        if(LocalTime.now().compareTo(LocalTime.of(8,10))<1 && LocalTime.now().compareTo(LocalTime.of(8,0))>=0){
                                            b_terminale_i.setDisable(false);
                                            b_terminale_u.setDisable(true);
                                        }else if(LocalTime.now().compareTo(LocalTime.of(16,10))<1 && LocalTime.now().compareTo(LocalTime.of(16,0))>=0){
                                            b_terminale_i.setDisable(false);
                                            b_terminale_u.setDisable(true);
                                        }else if(LocalTime.now().compareTo(LocalTime.of(00,11))<1 && LocalTime.now().compareTo(LocalTime.of(00,01))>=0){
                                            b_terminale_i.setDisable(false);
                                            b_terminale_u.setDisable(true);
                                        }else{
                                            b_terminale_i.setDisable(true);
                                            b_terminale_u.setDisable(false);
                                        }
                                        if(LocalTime.now().compareTo(LocalTime.of(00,11)) == 0){
                                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("00:01:00","07:59:00");;
                                            if(lista_impiegati_mancata_firma != null) {
                                                for (int i = 0; i < 3; ++i) {
                                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                    Smtp_mail mail = new Smtp_mail();
                                                    try {
                                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "00:01:00 - 07:59:00");
                                                    } catch (MessagingException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                }
                                            }else{
                                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                            }
                                        }
                                        if(LocalTime.now().compareTo(LocalTime.of(8,11)) == 0){
                                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("08:00:00","16:00:00");
                                            if(lista_impiegati_mancata_firma != null) {
                                                for (int i = 0; i < 3; ++i) {
                                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                    Smtp_mail mail = new Smtp_mail();
                                                    try {
                                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "08:00:00 - 16:00:00");
                                                    } catch (MessagingException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                }
                                            }else{
                                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                            }
                                        }
                                        if(LocalTime.now().compareTo(LocalTime.of(16,11)) == 0) {
                                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("16:00:00", "23:59:00");
                                            if (lista_impiegati_mancata_firma != null) {
                                                for (int i = 0; i < 3; ++i) {
                                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                    Smtp_mail mail = new Smtp_mail();
                                                    try {
                                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "16:00:00 - 23:59:00");
                                                    } catch (MessagingException ex) {
                                                        throw new RuntimeException(ex);
                                                    }
                                                }
                                            }else{
                                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                            }
                                        }
                                    }
                                    // of course, you could improve this by moving dateformat variable elsewhere

                                });
                            }
                        }, 0, 1, TimeUnit.SECONDS);
                        Terminale.ingressostage.close();
                    }
                }else{
                    Alert.showDialog(false, "Errore", "Campo cognome vuoto!");
                }
            }else{
                Alert.showDialog(false, "Errore", "Campo nome vuoto!");
            }
        }else{
            Alert.showDialog(false, "Errore", "Campo matricola vuoto!");

        }
    }

    public static void Terminale(Stage stage, Scene scene){
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
        Text timestamp = (Text) scene.lookup("#current");
        Button b_terminale_i = (Button) scene.lookup("#b_terminale_i");
        Button b_terminale_u = (Button) scene.lookup("#b_terminale_u");



        e= Executors.newSingleThreadScheduledExecutor();
        e.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // do stuff
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        if(LocalTime.now().getMinute() == 00){
                            List<Impiegato> lista_impiegati_disponibili = DBMSManager.queryImpiegatiDisponibili();
                            Map<Impiegato, Turno> lista_richieste_malattie= DBMSManager.queryRichiesteMalattiaImpiegati();
                            int i = 0;
                            for(Turno t: lista_richieste_malattie.values()){
                                if(DBMSManager.queryAggiornaTurni(lista_impiegati_disponibili.get(i),t)){
                                    Smtp_mail smtp = new Smtp_mail();
                                    smtp.sendEmail(lista_impiegati_disponibili.get(i).getMatricola(),lista_impiegati_disponibili.get(i).getNome(),lista_impiegati_disponibili.get(i).getCognome(), t);
                                }
                            }
                            ArrayList<Integer> matricole_notifiche = DBMSManager.queryChiusuraServizio();
                            if(matricole_notifiche !=null){
                                    DBMSManager.queryInserisciNotifica(1000,"E' stato chiuso un servizio.","\uD83D\uDD14 Chiusura servizio");
                            }
                        }

                        if(Terminale.first_avvio){
                            Terminale.first_avvio = false;
                            String ultima_data = DBMSManager.queryUltimoTurno();
                            if(ultima_data.equalsIgnoreCase("")){
                                Terminale.data_genera_turni = LocalDate.now();
                            }else{
                                String dt = ultima_data;  // Start date
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                try {
                                    c.setTime(sdf.parse(dt));
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                c.add(Calendar.DATE, -3);

                                dt = sdf.format(c.getTime());
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                LocalDate localDate = LocalDate.parse(dt, formatter);
                                Terminale.data_genera_turni = localDate;
                            }
                        }

                        if(LocalDate.now().getDayOfMonth() == 24){
                            if(calcola_stipendi){
                                HashMap<Integer,Float> stipendi_a = DBMSManager.queryOreLavorative();
                                if(stipendi_a != null){
                                    ArrayList<Integer> matricole = new ArrayList<>(stipendi_a.keySet());
                                    ArrayList<Float> stipendi= new ArrayList<>(stipendi_a.values());
                                    for(int i=0;i<stipendi.size();++i){
                                        DBMSManager.queryAggiornaStipendio(matricole.get(i),LocalDate.now().toString(),stipendi.get(i),"Stipendio");
                                        DBMSManager.queryInserisciNotifica(matricole.get(i),"Stipendio mensile accreditato","\uD83D\uDD14 Stipendio");
                                    }
                                    System.out.println("Hai calcolato ed inserito correttamente tutti gli stipendi.");

                                }else{
                                    System.out.println("Nessun dipendente ha lavorato.");
                                }
                                calcola_stipendi = false;
                            }
                        }else{
                            calcola_stipendi = true;
                        }

                        if(LocalDate.now().compareTo(Terminale.data_genera_turni) == 0){
                            if((!Terminale.generati)){
                                Generator.generateShifts(Terminale.data_genera_turni.toString());
                                ArrayList<Impiegato> lista_impiegati = DBMSManager.queryImpiegatiTurni();
                                for(Impiegato i: lista_impiegati){
                                    DBMSManager.queryInserisciNotifica(i.getMatricola(),"Sono stati generati i turni per il prossimo trimestre.","\uD83D\uDD14 Nuova Turnazione");
                                }
                                Terminale.generati = true;
                            }
                        }

                        DateFormat dateandtime = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                        Date date = new Date();
                        timestamp.setText(dateandtime.format(date));

                        if(LocalTime.now().compareTo(LocalTime.of(8,10))<1 && LocalTime.now().compareTo(LocalTime.of(8,0))>=0){
                            b_terminale_i.setDisable(false);
                            b_terminale_u.setDisable(true);
                        }else if(LocalTime.now().compareTo(LocalTime.of(16,10))<1 && LocalTime.now().compareTo(LocalTime.of(16,0))>=0){
                            b_terminale_i.setDisable(false);
                            b_terminale_u.setDisable(true);
                        }else if(LocalTime.now().compareTo(LocalTime.of(00,11))<1 && LocalTime.now().compareTo(LocalTime.of(00,01))>=0){
                            b_terminale_i.setDisable(false);
                            b_terminale_u.setDisable(true);
                        }else{
                            b_terminale_i.setDisable(true);
                            b_terminale_u.setDisable(false);
                        }
                        if(LocalTime.now().compareTo(LocalTime.of(00,11)) == 0){
                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("00:01:00","07:59:00");;
                            if(lista_impiegati_mancata_firma != null) {
                                for (int i = 0; i < 3; ++i) {
                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                    Smtp_mail mail = new Smtp_mail();
                                    try {
                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "00:01:00 - 07:59:00");
                                    } catch (MessagingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }else{
                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                            }
                        }
                        if(LocalTime.now().compareTo(LocalTime.of(8,11)) == 0){
                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("08:00:00","16:00:00");
                            if(lista_impiegati_mancata_firma != null) {
                                for (int i = 0; i < 3; ++i) {
                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                    Smtp_mail mail = new Smtp_mail();
                                    try {
                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "08:00:00 - 16:00:00");
                                    } catch (MessagingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }else{
                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                            }
                        }
                        if(LocalTime.now().compareTo(LocalTime.of(16,11)) == 0) {
                            ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("16:00:00", "23:59:00");
                            if (lista_impiegati_mancata_firma != null) {
                                for (int i = 0; i < 3; ++i) {
                                    int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                    Impiegato im = lista_impiegati_mancata_firma.get(random);
                                    Smtp_mail mail = new Smtp_mail();
                                    try {
                                        DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                        mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "16:00:00 - 23:59:00");
                                    } catch (MessagingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                            }else{
                                System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                            }
                        }
                    }
                    // of course, you could improve this by moving dateformat variable elsewhere

                });
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
    public static void firmaUscita(TextField matricola, TextField nome, TextField cognome) throws IOException {
        if(!matricola.getText().isEmpty()) {
            if (!nome.getText().isEmpty()) {
                if (!cognome.getText().isEmpty()) {
                    int matricola_int = 0;
                    try{ matricola_int = Integer.valueOf(matricola.getText()).intValue();}catch(Exception e){matricola_int = 0;}
                    if (DBMSManager.queryRilevazioneUscita(matricola_int, nome.getText(), cognome.getText())) {
                        Alert.showDialog(true, "Successo", "Hai firmato correttamente l'uscita!");
                    }
                    Stage stage = Terminale.mainstage;
                    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("terminale.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 451, 282);
                    stage.setResizable(false);
                    stage.setTitle("Terminale");
                    scene.getStylesheets().add(Main.class.getResource("/fontstyle.css").toExternalForm());
                    stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
                    stage.setScene(scene);
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
                        }
                    });
                    stage.show();
                    Text timestamp = (Text) scene.lookup("#current");
                    Button b_terminale_i = (Button) scene.lookup("#b_terminale_i");
                    Button b_terminale_u = (Button) scene.lookup("#b_terminale_u");


                    e = Executors.newSingleThreadScheduledExecutor();
                    e.scheduleAtFixedRate(new Runnable() {
                        @Override
                        public void run() {
                            // do stuff
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {

                                    if(LocalTime.now().getMinute() == 00){
                                        List<Impiegato> lista_impiegati_disponibili = DBMSManager.queryImpiegatiDisponibili();
                                        Map<Impiegato, Turno> lista_richieste_malattie= DBMSManager.queryRichiesteMalattiaImpiegati();
                                        int i = 0;
                                        for(Turno t: lista_richieste_malattie.values()){
                                            if(DBMSManager.queryAggiornaTurni(lista_impiegati_disponibili.get(i),t)){
                                                Smtp_mail smtp = new Smtp_mail();
                                                smtp.sendEmail(lista_impiegati_disponibili.get(i).getMatricola(),lista_impiegati_disponibili.get(i).getNome(),lista_impiegati_disponibili.get(i).getCognome(), t);
                                            }
                                        }
                                        ArrayList<Integer> matricole_notifiche = DBMSManager.queryChiusuraServizio();
                                        if(matricole_notifiche !=null){
                                            DBMSManager.queryInserisciNotifica(1000,"E' stato chiuso un servizio.","\uD83D\uDD14 Chiusura servizio");
                                        }
                                    }
                                    if(Terminale.first_avvio){
                                        Terminale.first_avvio = false;
                                        String ultima_data = DBMSManager.queryUltimoTurno();
                                        if(ultima_data.equalsIgnoreCase("")){
                                            Terminale.data_genera_turni = LocalDate.now();
                                        }else{
                                            String dt = ultima_data;  // Start date
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                            Calendar c = Calendar.getInstance();
                                            try {
                                                c.setTime(sdf.parse(dt));
                                            } catch (ParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                            c.add(Calendar.DATE, -3);

                                            dt = sdf.format(c.getTime());
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                            LocalDate localDate = LocalDate.parse(dt, formatter);
                                            Terminale.data_genera_turni = localDate;
                                        }
                                    }

                                    if(LocalDate.now().compareTo(Terminale.data_genera_turni) == 0){
                                        if((!Terminale.generati)){
                                            Generator.generateShifts(Terminale.data_genera_turni.toString());
                                            ArrayList<Impiegato> lista_impiegati = DBMSManager.queryImpiegatiTurni();
                                            for(Impiegato i: lista_impiegati){
                                                DBMSManager.queryInserisciNotifica(i.getMatricola(),"Sono stati generati i turni per il prossimo trimestre.","\uD83D\uDD14 Nuova Turnazione");
                                            }
                                            Terminale.generati = true;
                                        }
                                    }

                                    if(LocalDate.now().getDayOfMonth() == 24){
                                        if(calcola_stipendi){
                                            HashMap<Integer,Float> stipendi_a = DBMSManager.queryOreLavorative();
                                            if(stipendi_a != null){
                                                ArrayList<Integer> matricole = new ArrayList<>(stipendi_a.keySet());
                                                ArrayList<Float> stipendi= new ArrayList<>(stipendi_a.values());
                                                for(int i=0;i<stipendi.size();++i){
                                                    DBMSManager.queryAggiornaStipendio(matricole.get(i),LocalDate.now().toString(),stipendi.get(i),"Stipendio");
                                                    DBMSManager.queryInserisciNotifica(matricole.get(i),"Stipendio mensile accreditato","\uD83D\uDD14 Stipendio");
                                                }
                                                System.out.println("Hai calcolato ed inserito correttamente tutti gli stipendi.");

                                            }else{
                                                System.out.println("Nessun dipendente ha lavorato.");
                                            }
                                            calcola_stipendi = false;
                                        }
                                    }else{
                                        calcola_stipendi = true;
                                    }

                                    DateFormat dateandtime = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                                    Date date = new Date();
                                    timestamp.setText(dateandtime.format(date));

                                    if(LocalTime.now().compareTo(LocalTime.of(8,10))<1 && LocalTime.now().compareTo(LocalTime.of(8,0))>=0){
                                        b_terminale_i.setDisable(false);
                                        b_terminale_u.setDisable(true);
                                    }else if(LocalTime.now().compareTo(LocalTime.of(16,10))<1 && LocalTime.now().compareTo(LocalTime.of(16,0))>=0){
                                        b_terminale_i.setDisable(false);
                                        b_terminale_u.setDisable(true);
                                    }else if(LocalTime.now().compareTo(LocalTime.of(00,11))<1 && LocalTime.now().compareTo(LocalTime.of(00,01))>=0){
                                        b_terminale_i.setDisable(false);
                                        b_terminale_u.setDisable(true);
                                    }else{
                                        b_terminale_i.setDisable(true);
                                        b_terminale_u.setDisable(false);
                                    }
                                    if(LocalTime.now().compareTo(LocalTime.of(00,11)) == 0){
                                        ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("00:01:00","07:59:00");;
                                        if(lista_impiegati_mancata_firma != null) {
                                            for (int i = 0; i < 3; ++i) {
                                                int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                Smtp_mail mail = new Smtp_mail();
                                                try {
                                                    DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                    DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                    mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "00:01:00 - 07:59:00");
                                                } catch (MessagingException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        }else{
                                            System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                        }
                                    }
                                    if(LocalTime.now().compareTo(LocalTime.of(8,11)) == 0){
                                        ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("08:00:00","16:00:00");
                                        if(lista_impiegati_mancata_firma != null) {
                                            for (int i = 0; i < 3; ++i) {
                                                int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                Smtp_mail mail = new Smtp_mail();
                                                try {
                                                    DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                    DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                    mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "08:00:00 - 16:00:00");
                                                } catch (MessagingException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        }else{
                                            System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                        }
                                    }
                                    if(LocalTime.now().compareTo(LocalTime.of(16,11)) == 0) {
                                        ArrayList<Impiegato> lista_impiegati_mancata_firma = DBMSManager.getImpiegatiMancataFirma("16:00:00", "23:59:00");
                                        if (lista_impiegati_mancata_firma != null) {
                                            for (int i = 0; i < 3; ++i) {
                                                int random = (int) (Math.random() * (lista_impiegati_mancata_firma.size() - 0 + 1) + 0);
                                                Impiegato im = lista_impiegati_mancata_firma.get(random);
                                                Smtp_mail mail = new Smtp_mail();
                                                try {
                                                    DBMSManager.queryInserisciNotifica(im.getMatricola(),"Mancata Firma","\uD83D\uDD14 Firma");
                                                    DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + im.getMatricola() + " non ha firmato!","\uD83D\uDD14 Mancata Firma");
                                                    mail.sendMessageMancataFirma(im.getEmail(), im.getMatricola(), im.getNome(), im.getCognome(), LocalDate.now().toString(), "16:00:00 - 23:59:00");
                                                } catch (MessagingException ex) {
                                                    throw new RuntimeException(ex);
                                                }
                                            }
                                        }else{
                                            System.out.println("TUTTI GLI IMPIEGATI HANNO FIRMATO");
                                        }
                                    }
                                }
                                // of course, you could improve this by moving dateformat variable elsewhere

                            });
                        }
                    }, 0, 1, TimeUnit.SECONDS);
                    Terminale.uscitastage.close();
                }else{
                    Alert.showDialog(false, "Errore", "Campo cognome vuoto!");
                }
            }else{
                Alert.showDialog(false, "Errore", "Campo nome vuoto!");
            }
        }else{
            Alert.showDialog(false, "Errore", "Campo matricola vuoto!");
        }
    }
}
