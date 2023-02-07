package it.spvm.progetto_2022_2023_fx.roles.employee.controls;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.TimeField;
import com.calendarfx.view.page.MonthPage;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.roles.employee.interfaces.DashboardImpiegato;
import it.spvm.progetto_2022_2023_fx.entity.Stipendio;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.CustomCalendar;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class EmployeeController {

    private static ArrayList<Stipendio> lista_stipendi;

    public static void onVisualizzaTurni(ArrayList<Turno> turni,String title){
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardImpiegato.class.getResource("/it/spvm/progetto_2022_2023_fx/impiegati/pane_visualizza_turni.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 528, 585);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));

        try {
            MonthPage mv = (MonthPage) scene.lookup("#month_view");
            CalendarSource calendarSource = new CalendarSource();

            CustomCalendar calendar1 = new CustomCalendar();
            for(Turno t: turni){
                calendar1.createEntries(t.getGiorno(),t.getOrario_Inizio(),t.getOrario_Fine(),t.getServizio());
            }
            calendar1.setName("Calendar 1");
            calendar1.setReadOnly(true);
            calendar1.setStyle(Calendar.Style.getStyle(40));
            calendarSource.getCalendars().setAll(calendar1);
            mv.getCalendarSources().setAll(calendarSource);

            mv.setEntryDetailsCallback(new Callback<DateControl.EntryDetailsParameter, Boolean>() {
                @Override
                public Boolean call(DateControl.EntryDetailsParameter entryDetailsParameter) {

                    String data_inizio= entryDetailsParameter.getEntry().getStartDate().getDayOfMonth()+"/"+entryDetailsParameter.getEntry().getStartDate().getMonthValue()+"/"+entryDetailsParameter.getEntry().getStartDate().getYear();
                    Alert.showDialogEntryCalendar( entryDetailsParameter.getEntry().getTitle(),"=== Turno ===\n[Giorno]: "+ data_inizio+"\n[Dalle]: "+ entryDetailsParameter.getEntry().getStartTime() +"\n[Alle]: " + entryDetailsParameter.getEntry().getEndTime()+"\n[Totale ore]: 8h\n\n[Servizio]: " + entryDetailsParameter.getEntry().getLocation());
                    return null;
                }
            });

            stage.setResizable(false);
            stage.setTitle(title);

            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public static void onClickVisualizzaStipendi(Label title, StackPane stackpane,int size_table) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DashboardImpiegato.class.getResource("/it/spvm/progetto_2022_2023_fx/impiegati/pane_visualizza_stipendio.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Visualizza Stipendio");
        stackpane.getChildren().set(0,parent);
        for(Node n: getAllNodes(parent)){
            if(n instanceof Pane){
                Pane panel = ((Pane) n);
                if(panel.getId()!= null){
                    panel.setPrefHeight(400);
                }
            }
        }
        TableView tv_node = null;
        for (Node currentNode :  parent.getChildrenUnmodifiable()){
            if (currentNode instanceof TableView<?>){
                TableView tab = (TableView<String>) currentNode;
                tab.setPrefHeight(size_table);
                tv_node = tab;
                tab.setEditable(false);

                TableColumn dataColumn = new TableColumn("Data Accredito");
                dataColumn.setCellValueFactory(new PropertyValueFactory<>("Data"));
                dataColumn.setPrefWidth(106.3999605178833);
                dataColumn.setStyle("-fx-alignment: CENTER;");
                dataColumn.setEditable(false);
                dataColumn.setReorderable(false);


                TableColumn causaleColumn = new TableColumn("Causale");
                causaleColumn.setCellValueFactory(new PropertyValueFactory<>("Causale"));
                causaleColumn.setPrefWidth(175.59999084472656);
                causaleColumn.setStyle("-fx-alignment: CENTER;");
                causaleColumn.setEditable(false);
                causaleColumn.setReorderable(false);


                TableColumn totaleColumn = new TableColumn("Stipendio");
                totaleColumn.setCellValueFactory(new PropertyValueFactory<>("Stipendio"));
                totaleColumn.setPrefWidth(104.800048828125);
                totaleColumn.setStyle("-fx-alignment: CENTER;");
                totaleColumn.setEditable(false);
                totaleColumn.setReorderable(false);

                tab.getColumns().addAll(dataColumn, causaleColumn, totaleColumn);

                //RICHIAMARE DAL DB TUTTI GLI STIPENDI E FARE UN CICLO FOR CHE CREA n OGGETTI di tipo STIPENDIO E LI AGGIUNGE ALLA TAB
                //SELECT DEGLI STIPENDI IN ORDINE DI DATA (La 1° è l'ultimo stipendio)
                lista_stipendi =  DBMSManager.queryStipendioWhere(DBMSManager.getCurrentImpiegato().getMatricola());
                if(lista_stipendi != null){
                    for (Stipendio s: lista_stipendi) {
                        tab.getItems().add(s);
                    }
                }
                dataColumn.setSortable(false);
            }
        }

        for (Node currentNode : getAllNodes(parent)) {
            if (currentNode instanceof Label) {
                Label label_stipendio = ((Label) currentNode);
                if(label_stipendio.getText().equalsIgnoreCase("{last_stipendio}")){
                    if(lista_stipendi != null){
                        label_stipendio.setText(lista_stipendi.get(lista_stipendi.size()-1).getStipendio()+"");
                    }else{
                        label_stipendio.setText("0.0 €");
                    }
                }else if(label_stipendio.getText().equalsIgnoreCase("{d}")){
                    if(lista_stipendi != null){
                        String month_day = lista_stipendi.get(lista_stipendi.size()-1).getData().substring(5, lista_stipendi.get(lista_stipendi.size()-1).getData().length());
                        String[] result = month_day.split("-", 0);
                        label_stipendio.setText(result[result.length-1] +"/"+ result[0]);
                    }else{
                        label_stipendio.setText("0/0");
                    }
                }
            }
        }

    }

    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }









    public static void onClickInserisciPermesso(DatePicker data_picker_from, DatePicker data_picker_to, TimeField time_from, TimeField time_to, ChoiceBox choice_box){
        if(data_picker_from.getValue()!=null){
            if(data_picker_to.getValue() != null){
                if(!choice_box.getValue().equals("Tipo Permesso")){
                    LocalDate localDate_from = data_picker_from.getValue();
                    if(localDate_from.compareTo(LocalDate.now()) >= 0){
                        LocalDate localDate_to = data_picker_to.getValue();
                        if(localDate_to.compareTo(LocalDate.now()) >= 0){
                            if(time_from.getValue().compareTo(LocalTime.now()) >= 0 || localDate_from.compareTo(LocalDate.now()) >= 0 ){
                                if(time_to.getValue().compareTo(LocalTime.now()) >= 0 || localDate_from.compareTo(LocalDate.now()) >= 0){
                                    String newString_1 = localDate_from.toString() + " " + time_from.getValue().toString();
                                    String newString_2 = localDate_to.toString() + " " + time_to.getValue().toString();
                                        if((localDate_from.compareTo(localDate_to) <= 0)) {
                                            if (time_to.getValue().compareTo(time_from.getValue()) < 0 && localDate_from.compareTo(localDate_to) == 0) {
                                                Alert.showDialog(false, "Errore", "Ora di fine permesso inferiore a quella di inizio!");
                                                return;
                                            }

                                            if (choice_box.getValue().equals("Ferie")) {

                                                java.util.Calendar c1 = java.util.Calendar.getInstance();
                                                String dt1 = newString_1;
                                                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

                                                try {
                                                    c1.setTime(sdf1.parse(dt1));
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                c1.add(java.util.Calendar.MONTH, -1);
                                                dt1 = sdf1.format(c1.getTime());
                                                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                LocalDate localDate1 = LocalDate.parse(dt1, formatter1);

                                                if (localDate1.compareTo(LocalDate.now()) >= 0) {
                                                    if(!DBMSManager.queryVerificaEsistenzaPeriodoIncrementoAttivita(localDate_from.toString(),localDate_to.toString())) {
                                                        boolean cancellaturni = DBMSManager.queryVerificaEsistenzaTurno(DBMSManager.getCurrentImpiegato().getMatricola(), newString_1, newString_2);
                                                        if (DBMSManager.queryInserisciPermesso(newString_1, newString_2, choice_box.getValue().toString())) {
                                                            if (cancellaturni) {
                                                                Alert.showDialog(true, "Successo", "Ferie programmate con successo, hai cancellato i turni per quelle date!");
                                                            } else {
                                                                Alert.showDialog(true, "Successo", "Ferie programmate con successo!");
                                                            }
                                                        }
                                                    }else {
                                                        Alert.showDialog(false, "Errore", "Impossibile richiedere le ferie!");
                                                    }
                                                } else {
                                                    Alert.showDialog(false, "Errore", "Impossibile richiedere le ferie!");
                                                }
                                            }else {
                                                java.util.Calendar c = java.util.Calendar.getInstance();
                                                String dt = newString_1;
                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                                try {
                                                    c.setTime(sdf.parse(dt));
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                c.add(java.util.Calendar.DATE, -3);
                                                dt = sdf.format(c.getTime());
                                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                LocalDate localDate = LocalDate.parse(dt, formatter);

                                                if (localDate.compareTo(LocalDate.now()) >= 0) {
                                                    boolean cancellaturni = DBMSManager.queryVerificaEsistenzaTurno(DBMSManager.getCurrentImpiegato().getMatricola(), newString_1, newString_2);
                                                    if (DBMSManager.queryInserisciPermesso(newString_1, newString_2, choice_box.getValue().toString())) {
                                                        if (cancellaturni) {
                                                            Alert.showDialog(true, "Successo", "Permesso aggiunto con successo, hai cancellato i turni per quelle date!");
                                                        } else {
                                                            Alert.showDialog(true, "Successo", "Permesso aggiunto con successo!");
                                                        }
                                                    }
                                                } else {
                                                    Alert.showDialog(false, "Errore", "Puoi richiedere un permesso almeno 3 giorni prima!");
                                                }
                                            }
                                            }else{
                                                Alert.showDialog(false, "Errore", "Le date del permesso non sono valide!");
                                            }
                                }else{
                                    Alert.showDialog(false,"Errore","Campo [A - Orario] errato. Non puoi selezionare un permesso per un orario precedente!");
                                }
                            }else{
                                Alert.showDialog(false,"Errore","Campo [DA - Orario] errato. Non puoi selezionare un permesso per un orario precedente!");
                            }
                        }else{
                            Alert.showDialog(false,"Errore","Campo [A] errato. Non puoi selezionare un permesso per una data precedente!");
                        }
                    }else{
                        Alert.showDialog(false,"Errore","Campo [DA] errato. Non puoi selezionare un permesso per una data precedente!");
                    }
                }else{
                    Alert.showDialog(false,"Errore","Tipo permesso non selezionato!");
                }
            }else{
                Alert.showDialog(false,"Errore","Campo data [A] vuoto!");
            }
        }else{
            Alert.showDialog(false,"Errore","Campo data [DA] vuoto!");
        }
    }
    public static void onFirmaRitardo(TextArea motivazione){
        if(LocalTime.now().compareTo(LocalTime.of(8,11))>=0 && LocalTime.now().compareTo(LocalTime.of(15,59))<=0) {
                if (!DBMSManager.checkFirmaImpiegato()) {
                    if (!motivazione.getText().isEmpty()) {
                        if (DBMSManager.queryInserisciPresenzaRitardo(motivazione.getText())) {
                            Alert.showDialog(true, "Successo", "Hai firmato correttamente!");
                            DBMSManager.queryInserisciNotifica(1000, "L'impiegato " + DBMSManager.getCurrentImpiegato().getMatricola() + " ha firmato giorno " + LocalDate.now() + " alle " + LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond(), "\uD83D\uDD14 Firma in ritardo dell'impiegato " + DBMSManager.getCurrentImpiegato().getMatricola());
                        } else {
                            Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                        }
                    } else {
                        Alert.showDialog(false, "Errore", "Campo motivazione vuoto!");
                    }
                } else {
                    Alert.showDialog(false, "Errore", "L'impiegato ha già firmato oggi!");
                }

        }else if(LocalTime.now().compareTo(LocalTime.of(16,11))>=0 && LocalTime.now().compareTo(LocalTime.of(23,59))<=0){
            if (!DBMSManager.checkFirmaImpiegato()) {
                if (!motivazione.getText().isEmpty()) {
                    if (DBMSManager.queryInserisciPresenzaRitardo(motivazione.getText())) {
                        Alert.showDialog(true, "Successo", "Hai firmato correttamente!");
                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato "+DBMSManager.getCurrentImpiegato().getMatricola()+ " ha firmato giorno " + LocalDate.now() + " alle " + LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond(),"\uD83D\uDD14 Firma in ritardo dell'impiegato " + DBMSManager.getCurrentImpiegato().getMatricola());

                    } else {
                        Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    }
                } else {
                    Alert.showDialog(false, "Errore", "Campo motivazione vuoto!");
                }
            } else {
                Alert.showDialog(false, "Errore", "Hai già firmato oggi!");
            }
        }else if(LocalTime.now().compareTo(LocalTime.of(00,11))>=0 && LocalTime.now().compareTo(LocalTime.of(7,59))<=0){
            if (!DBMSManager.checkFirmaImpiegato()) {
                if (!motivazione.getText().isEmpty()) {
                    if (DBMSManager.queryInserisciPresenzaRitardo(motivazione.getText())) {
                        Alert.showDialog(true, "Successo", "Hai firmato correttamente!");
                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato "+DBMSManager.getCurrentImpiegato().getMatricola()+ " ha firmato giorno " + LocalDate.now() + " alle " + LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond(),"\uD83D\uDD14 Firma in ritardo dell'impiegato " + DBMSManager.getCurrentImpiegato().getMatricola());
                    } else {
                        Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    }
                } else {
                    Alert.showDialog(false, "Errore", "Campo motivazione vuoto!");
                }
            } else {
                Alert.showDialog(false, "Errore", "Hai già firmato oggi!");
            }
        }else{
            Alert.showDialog(false, "Errore", "Questa opzione non è al momento disponibile.");
        }
    }
}



