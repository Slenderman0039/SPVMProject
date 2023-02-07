package it.spvm.progetto_2022_2023_fx.roles.admin.controls;

import com.calendarfx.view.TimeField;
import it.spvm.progetto_2022_2023_fx.roles.admin.interfaces.lista_impiegati_admin;
import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.entity.Stipendio;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import it.spvm.progetto_2022_2023_fx.utils.Smtp_mail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.apache.commons.codec.digest.DigestUtils;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class AdminController {
    public static TableView table;
    public static final ObservableList<Impiegato> dataList = FXCollections.observableArrayList();
    public static final ObservableList<Stipendio> dataList2 = FXCollections.observableArrayList();
    public static void onClickVisualizzaImpiegati(Parent parent){
        for(Node n: getAllNodes(parent)){
            if(n instanceof Pane){
                Pane panel = ((Pane) n);
                if(panel.getId()!= null){
                    panel.setPrefHeight(500);
                }
            }
        }

        TextField search = null;
        for(Node n: getAllNodes(parent)){
            if(n instanceof TextField){
                TextField search_i = ((TextField) n);
                if(search_i.getPromptText().equalsIgnoreCase("Cerca...")){
                    search = search_i;
                }
            }
        }

        TableView tv_node = null;
        for (Node currentNode :  parent.getChildrenUnmodifiable()){
            if (currentNode instanceof TableView<?>){
                TableView tab = (TableView<String>) currentNode;
                tab.setPrefHeight(350);
                tv_node = tab;
                tab.setEditable(false);

                table = tab;
                TableColumn matricola_column= new TableColumn("Matricola");
                matricola_column.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
                matricola_column.setPrefWidth(70.3999605178833);
                matricola_column.setStyle("-fx-alignment: CENTER;");
                matricola_column.setEditable(false);
                matricola_column.setReorderable(false);

                TableColumn cognome_column= new TableColumn("Cognome");
                cognome_column.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
                cognome_column.setPrefWidth(90.3999605178833);
                cognome_column.setStyle("-fx-alignment: CENTER;");
                cognome_column.setEditable(false);
                cognome_column.setReorderable(false);


                TableColumn nome_column = new TableColumn("Nome");
                nome_column.setCellValueFactory(new PropertyValueFactory<>("Nome"));
                nome_column.setPrefWidth(90.59999084472656);
                nome_column.setStyle("-fx-alignment: CENTER;");
                nome_column.setEditable(false);
                nome_column.setReorderable(false);

                TableColumn data_column = new TableColumn("Data di Nascita");
                data_column.setCellValueFactory(new PropertyValueFactory<>("d_nascita"));
                data_column.setPrefWidth(90.800048828125);
                data_column.setStyle("-fx-alignment: CENTER;");
                data_column.setEditable(false);
                data_column.setReorderable(false);

                TableColumn carriera_column = new TableColumn("Carriera attiva");
                carriera_column.setCellValueFactory(new PropertyValueFactory<>("CarrireraAttivaString"));
                carriera_column.setPrefWidth(90.800048828125);
                carriera_column.setStyle("-fx-alignment: CENTER;");
                carriera_column.setEditable(false);
                carriera_column.setReorderable(false);


                TableColumn mansione_column = new TableColumn("Mansione");
                mansione_column.setCellValueFactory(new PropertyValueFactory<>("Mansione"));
                mansione_column.setPrefWidth(120.800048828125);
                mansione_column.setStyle("-fx-alignment: CENTER;");
                mansione_column.setEditable(false);
                mansione_column.setReorderable(false);

                TableColumn servizio_column = new TableColumn("Servizio");
                servizio_column.setCellValueFactory(new PropertyValueFactory<>("Servizio"));
                servizio_column.setPrefWidth(120.800048828125);
                servizio_column.setStyle("-fx-alignment: CENTER;");
                servizio_column.setEditable(false);
                servizio_column.setReorderable(false);

                TableColumn telefono_column = new TableColumn("Telefono");
                telefono_column.setCellValueFactory(new PropertyValueFactory<>("N_telefono"));
                telefono_column.setPrefWidth(150.800048828125);
                telefono_column.setStyle("-fx-alignment: CENTER;");
                telefono_column.setEditable(false);
                telefono_column.setReorderable(false);


                TableColumn email_column = new TableColumn("Email");
                email_column.setCellValueFactory(new PropertyValueFactory<>("Email"));
                email_column.setPrefWidth(150.800048828125);
                email_column.setStyle("-fx-alignment: CENTER;");
                email_column.setEditable(false);
                email_column.setReorderable(false);

                TableColumn fiscale_column = new TableColumn("Codice Fiscale");
                fiscale_column.setCellValueFactory(new PropertyValueFactory<>("C_f"));
                fiscale_column.setPrefWidth(190.800048828125);
                fiscale_column.setStyle("-fx-alignment: CENTER;");
                fiscale_column.setEditable(false);
                fiscale_column.setReorderable(false);

                TableColumn IBAN_column = new TableColumn("IBAN");
                IBAN_column.setCellValueFactory(new PropertyValueFactory<>("IBAN"));
                IBAN_column.setPrefWidth(190.800048828125);
                IBAN_column.setStyle("-fx-alignment: CENTER;");
                IBAN_column.setEditable(false);
                IBAN_column.setReorderable(false);

                tab.getColumns().addAll(matricola_column, cognome_column, nome_column, data_column, carriera_column, mansione_column, servizio_column, telefono_column, email_column,fiscale_column, IBAN_column);

                ArrayList<Impiegato> impiegati = DBMSManager.queryImpiegatiOrderBySurname();
                dataList.clear();
                for(Impiegato i: impiegati){
                    dataList.add(i);
                    tab.getItems().add(i);
                }
                clickImpiegato();



                FilteredList<Impiegato> filteredData = new FilteredList<>(dataList, b -> true);

                search.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(impiegato -> {
                        // If filter text is empty, display all persons.


                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();
                        String str2 = Integer.toString(impiegato.getMatricola());

                        if (impiegato.getNome().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                            return true; // Filter matches first name.
                        }else if (impiegato.getCognome().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true; // Filter matches last name.
                        }else if (impiegato.getEmail().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true; // Filter matches last name.
                        }else if (impiegato.getEmail().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true; // Filter matches last name.
                        }else if(str2.startsWith(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        }else if(impiegato.getN_telefono().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        }else
                            return false; // Does not match.
                    });
                });

                // 3. Wrap the FilteredList in a SortedList.
                SortedList<Impiegato> sortedData = new SortedList<>(filteredData);

                // 4. Bind the SortedList comparator to the TableView comparator.
                // 	  Otherwise, sorting the TableView would have no effect.
                sortedData.comparatorProperty().bind(tab.comparatorProperty());

                // 5. Add sorted (and filtered) data to the table.
                tab.setItems(sortedData);


            }
        }
    }


    public static void onInserisciFirma(TextField matricola,TextField motivazione){
        int matricola_int = Integer.valueOf(matricola.getText().toString());
        if(DBMSManager.queryVerificaEsistenzaImpiegatoAttivo(matricola_int)) {
            if (!DBMSManager.queryVerificaFirma(matricola_int)) {
                if (!motivazione.getText().isEmpty()) {
                    if (DBMSManager.queryInserisciRitardoImpiegato(matricola_int, motivazione.getText().toString())) {
                        Alert.showDialog(true, "Successo", "Presenza registrata con successo!");
                        DBMSManager.queryInserisciNotifica(1000,"L'impiegato " + matricola_int + " ha firmato giorno " + LocalDate.now() + " alle " + LocalTime.now().getHour()+":"+LocalTime.now().getMinute()+":"+LocalTime.now().getSecond(),"\uD83D\uDD14 Firma in ritardo dell'impiegato " + matricola_int);
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
            Alert.showDialog(false, "Errore", "Impiegato inesistente!");
        }
    }

    public static void onClickVisualizzaStipendi(Parent parent){
        for(Node n: getAllNodes(parent)){
            if(n instanceof Pane){
                Pane panel = ((Pane) n);
                if(panel.getId()!= null){
                    panel.setPrefHeight(500);
                }
            }
        }

        TextField search = null;
        for(Node n: getAllNodes(parent)){
            if(n instanceof TextField){
                TextField search_i = ((TextField) n);
                if(search_i.getPromptText().equalsIgnoreCase("Cerca...")){
                    search = search_i;
                }
            }
        }

        TableView tv_node = null;
        for (Node currentNode :  parent.getChildrenUnmodifiable()){
            if (currentNode instanceof TableView<?>){
                TableView tab = (TableView<String>) currentNode;
                tab.setPrefHeight(350);
                tv_node = tab;
                tab.setEditable(false);

                TableColumn matricola_column= new TableColumn("Matricola");
                matricola_column.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
                matricola_column.setPrefWidth(70.3999605178833);
                matricola_column.setStyle("-fx-alignment: CENTER;");
                matricola_column.setEditable(false);
                matricola_column.setReorderable(false);

                TableColumn cognome_column= new TableColumn("Cognome");
                cognome_column.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
                cognome_column.setPrefWidth(90.3999605178833);
                cognome_column.setStyle("-fx-alignment: CENTER;");
                cognome_column.setEditable(false);
                cognome_column.setReorderable(false);


                TableColumn nome_column = new TableColumn("Nome");
                nome_column.setCellValueFactory(new PropertyValueFactory<>("Nome"));
                nome_column.setPrefWidth(90.59999084472656);
                nome_column.setStyle("-fx-alignment: CENTER;");
                nome_column.setEditable(false);
                nome_column.setReorderable(false);


                TableColumn data_column = new TableColumn("Data Accredito");
                data_column.setCellValueFactory(new PropertyValueFactory<>("Data"));
                data_column.setPrefWidth(90.800048828125);
                data_column.setStyle("-fx-alignment: CENTER;");
                data_column.setEditable(false);
                data_column.setReorderable(false);

                TableColumn stipendio_column = new TableColumn("Stipendio");
                stipendio_column.setCellValueFactory(new PropertyValueFactory<>("Stipendio"));
                stipendio_column.setPrefWidth(120.800048828125);
                stipendio_column.setStyle("-fx-alignment: CENTER;");
                stipendio_column.setEditable(false);
                stipendio_column.setReorderable(false);

                TableColumn causale_column = new TableColumn("Causale");
                causale_column.setCellValueFactory(new PropertyValueFactory<>("Causale"));
                causale_column.setPrefWidth(150.800048828125);
                causale_column.setStyle("-fx-alignment: CENTER;");
                causale_column.setEditable(false);
                causale_column.setReorderable(false);

                tab.getColumns().addAll(matricola_column, cognome_column, nome_column, data_column, stipendio_column, causale_column);
                ArrayList<Stipendio> stipendi = DBMSManager.queryStipendiDipendenti();
                AdminController.dataList2.clear();
                for(Stipendio s: stipendi){
                    AdminController.dataList2.add(s);
                    tab.getItems().add(s);
                }

                FilteredList<Stipendio> filteredData = new FilteredList<>(AdminController.dataList2, b -> true);

                search.textProperty().addListener((observable, oldValue, newValue) -> {
                    filteredData.setPredicate(stipendio -> {
                        // If filter text is empty, display all persons.


                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();
                        String str2 = Integer.toString(stipendio.getMatricola());

                        if (stipendio.getNome().toLowerCase().indexOf(lowerCaseFilter) != -1 ) {
                            return true; // Filter matches first name.
                        }else if (stipendio.getCognome().toLowerCase().indexOf(lowerCaseFilter) != -1) {
                            return true; // Filter matches last name.
                        }else if(str2.startsWith(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        }else if(stipendio.getData().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        }else if(stipendio.getCausale().startsWith(lowerCaseFilter)) {
                            return true; // Filter matches last name.
                        }else
                            return false; // Does not match.
                    });
                });

                // 3. Wrap the FilteredList in a SortedList.
                SortedList<Impiegato> sortedData = new SortedList<>(filteredData);

                // 4. Bind the SortedList comparator to the TableView comparator.
                // 	  Otherwise, sorting the TableView would have no effect.
                sortedData.comparatorProperty().bind(tab.comparatorProperty());

                // 5. Add sorted (and filtered) data to the table.
                tab.setItems(sortedData);
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

    public static void onClickInserisciPermessoAdmin (int matricola, DatePicker data_picker_from, DatePicker data_picker_to, TimeField time_from, TimeField time_to, ChoiceBox choice_box){
        if(data_picker_from.getValue()!=null){
            if(data_picker_to.getValue() != null){
                if(!choice_box.getValue().equals("Tipo Permesso")){
                    LocalDate localDate_from = data_picker_from.getValue();
                    if(localDate_from.compareTo(LocalDate.now()) >= 0){
                        LocalDate localDate_to = data_picker_to.getValue();
                        if(localDate_to.compareTo(LocalDate.now()) >= 0){
                            if(time_from.getValue().compareTo(LocalTime.now()) >= 0 || localDate_from.compareTo(LocalDate.now()) >= 0 ){
                                if(time_to.getValue().compareTo(LocalTime.now()) >= 0 || localDate_from.compareTo(LocalDate.now()) >= 0){
                                    String newString_1 = localDate_from.toString() + " "+time_from.getValue().toString();
                                    String newString_2 = localDate_to.toString() + " "+time_to.getValue().toString();
                                        if(localDate_from.compareTo(localDate_to) <= 0){

                                            if(time_to.getValue().compareTo(time_from.getValue()) < 0 && localDate_from.compareTo(localDate_to) == 0){
                                                Alert.showDialog(false,"Errore","Ora di fine permesso inferiore a quella di inizio!");
                                                return;
                                            }
                                            if (choice_box.getValue().equals("Ferie")) {
                                                if(DBMSManager.queryVerificaEsistenzaImpiegatoAttivo(matricola)) {
                                                    if (!DBMSManager.queryVerificaEsistenzaPeriodoIncrementoAttivita(localDate_from.toString(), localDate_to.toString())) {
                                                        boolean cancellaturni = DBMSManager.queryVerificaEsistenzaTurno(matricola, newString_1, newString_2);
                                                        if (DBMSManager.queryInserisciPermessoAdmin(matricola, newString_1, newString_2, choice_box.getValue().toString())) {
                                                            if (cancellaturni) {
                                                                Alert.showDialog(true, "Successo", "Ferie programmate con successo, hai cancellato i turni per quelle date!");
                                                            } else {
                                                                Alert.showDialog(true, "Successo", "Ferie programmate con successo");
                                                            }
                                                        }
                                                    } else {
                                                        Alert.showDialog(false, "Errore", "Impossibile richiedere le ferie!");
                                                    }
                                                }else{
                                                    Alert.showDialog(false,"Errore","Impiegato inesistente!");
                                                }
                                                }else{
                                            if(DBMSManager.queryVerificaEsistenzaImpiegato(matricola)){
                                                boolean cancellaturni = DBMSManager.queryVerificaEsistenzaTurno(matricola,newString_1,newString_2);
                                                if(DBMSManager.queryInserisciPermessoAdmin(matricola,newString_1,newString_2,choice_box.getValue().toString())){
                                                    if(cancellaturni){
                                                        Alert.showDialog(true, "Successo", "Permesso aggiunto con successo, hai cancellato i turni per quelle date!");
                                                    }else{
                                                        Alert.showDialog(true, "Successo", "Permesso aggiunto con successo!");
                                                    }
                                                }
                                            }else{
                                                Alert.showDialog(false,"Errore","Impiegato inesistente!");
                                            }
                                            }
                                        }else{
                                            Alert.showDialog(false,"Errore","Le date del permesso non sono valide!");
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

    public static void clickImpiegato(){
        lista_impiegati_admin.clickImpiegato();
    }

    public static void VisualizzaImpiegatiLicenziamento(Parent parent){
        for(Node n: getAllNodes(parent)){
            if(n instanceof Pane){
                Pane panel = ((Pane) n);
                if(panel.getId()!= null){
                    panel.setPrefHeight(500);
                }
            }
        }

        TableView tv_node = null;
        for (Node currentNode :  parent.getChildrenUnmodifiable()){
            if (currentNode instanceof TableView<?>){
                TableView tab = (TableView<String>) currentNode;
                tab.setPrefHeight(203);
                tv_node = tab;
                tab.setEditable(false);

                TableColumn matricola_column= new TableColumn("Matricola");
                matricola_column.setCellValueFactory(new PropertyValueFactory<>("Matricola"));
                matricola_column.setPrefWidth(70.3999605178833);
                matricola_column.setStyle("-fx-alignment: CENTER;");
                matricola_column.setEditable(false);
                matricola_column.setReorderable(false);

                TableColumn cognome_column= new TableColumn("Cognome");
                cognome_column.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
                cognome_column.setPrefWidth(90.3999605178833);
                cognome_column.setStyle("-fx-alignment: CENTER;");
                cognome_column.setEditable(false);
                cognome_column.setReorderable(false);


                TableColumn nome_column = new TableColumn("Nome");
                nome_column.setCellValueFactory(new PropertyValueFactory<>("Nome"));
                nome_column.setPrefWidth(90.59999084472656);
                nome_column.setStyle("-fx-alignment: CENTER;");
                nome_column.setEditable(false);
                nome_column.setReorderable(false);

                TableColumn data_column = new TableColumn("Data di Nascita");
                data_column.setCellValueFactory(new PropertyValueFactory<>("d_nascita"));
                data_column.setPrefWidth(120.800048828125);
                data_column.setStyle("-fx-alignment: CENTER;");
                data_column.setEditable(false);
                data_column.setReorderable(false);

                TableColumn carriera_column = new TableColumn("Carriera attiva");
                carriera_column.setCellValueFactory(new PropertyValueFactory<>("CarrireraAttivaString"));
                carriera_column.setPrefWidth(90.800048828125);
                carriera_column.setStyle("-fx-alignment: CENTER;");
                carriera_column.setEditable(false);
                carriera_column.setReorderable(false);


                TableColumn mansione_column = new TableColumn("Mansione");
                mansione_column.setCellValueFactory(new PropertyValueFactory<>("Mansione"));
                mansione_column.setPrefWidth(120.800048828125);
                mansione_column.setStyle("-fx-alignment: CENTER;");
                mansione_column.setEditable(false);
                mansione_column.setReorderable(false);

                TableColumn servizio_column = new TableColumn("Servizio");
                servizio_column.setCellValueFactory(new PropertyValueFactory<>("Servizio"));
                servizio_column.setPrefWidth(120.800048828125);
                servizio_column.setStyle("-fx-alignment: CENTER;");
                servizio_column.setEditable(false);
                servizio_column.setReorderable(false);

                TableColumn telefono_column = new TableColumn("Telefono");
                telefono_column.setCellValueFactory(new PropertyValueFactory<>("N_telefono"));
                telefono_column.setPrefWidth(150.800048828125);
                telefono_column.setStyle("-fx-alignment: CENTER;");
                telefono_column.setEditable(false);
                telefono_column.setReorderable(false);


                TableColumn email_column = new TableColumn("Email");
                email_column.setCellValueFactory(new PropertyValueFactory<>("Email"));
                email_column.setPrefWidth(150.800048828125);
                email_column.setStyle("-fx-alignment: CENTER;");
                email_column.setEditable(false);
                email_column.setReorderable(false);

                TableColumn fiscale_column = new TableColumn("Codice Fiscale");
                fiscale_column.setCellValueFactory(new PropertyValueFactory<>("C_f"));
                fiscale_column.setPrefWidth(190.800048828125);
                fiscale_column.setStyle("-fx-alignment: CENTER;");
                fiscale_column.setEditable(false);
                fiscale_column.setReorderable(false);

                TableColumn IBAN_column = new TableColumn("IBAN");
                IBAN_column.setCellValueFactory(new PropertyValueFactory<>("IBAN"));
                IBAN_column.setPrefWidth(190.800048828125);
                IBAN_column.setStyle("-fx-alignment: CENTER;");
                IBAN_column.setEditable(false);
                IBAN_column.setReorderable(false);

                tab.getColumns().addAll(matricola_column, cognome_column, nome_column, data_column, carriera_column, mansione_column, servizio_column, telefono_column, email_column,fiscale_column, IBAN_column);
                ArrayList<Impiegato> impiegati = DBMSManager.queryImpiegatiOrderBySurname();
                if(impiegati != null) {
                    for (Impiegato i : impiegati) {
                        tab.getItems().add(i);
                    }
                }
                tab.setRowFactory( tv -> {
                    TableRow<Impiegato> row = new TableRow<>();
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            Impiegato rowData = row.getItem();
                            ArrayList<Turno> turni = DBMSManager.queryTurniImpiegato(rowData.getMatricola());
                            if(turni == null){
                                Alert.showDialog(false,"Errore","Non ci sono turni per questo impiegato!");
                            }else{
                                EmployeeController.onVisualizzaTurni(turni,"Turni - "+rowData.getCognome()+" " + rowData.getNome());
                            }
                        }
                    });
                    return row ;
                });
            }
        }
    }

    public static void IncrementoAttivita(DatePicker date_picker_from, DatePicker date_picker_to){
        if(date_picker_from.getValue() != null){
            if(date_picker_to.getValue() != null){
                if(!DBMSManager.queryVerificaEsistenzaPeriodoIncrementoAttivita(date_picker_from.getValue().toString(),date_picker_to.getValue().toString())){
                    if(DBMSManager.queryInserisciIncrementoAttivita(date_picker_from.getValue().toString(),date_picker_to.getValue().toString())){
                        Alert.showDialog(true, "Successo", "Hai inserito correttamente il periodo di incremento attività dal " + date_picker_from.getValue().getDayOfMonth()+"/"+date_picker_from.getValue().getMonthValue()+"/"+date_picker_from.getValue().getYear()+" al " +date_picker_to.getValue().getDayOfMonth()+"/"+date_picker_to.getValue().getMonthValue()+"/"+date_picker_to.getValue().getYear());
                    }else{
                        Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    }
                }else{
                    Alert.showDialog(false,"Errore","Esistono già delle attività per quel periodo.");
                }
            }else{
                Alert.showDialog(false,"Errore","Bisogna completare il campo [A] incremento attività.");
            }
        }else{
            Alert.showDialog(false,"Errore","Bisogna completare il campo [DA] incremento attività.");
        }
    }

    public static void onClickLicenziaImpiegato(TextField matricola, TextField nome, TextField cognome){
        if(!matricola.getText().isEmpty()){
            if(!nome.getText().isEmpty()){
                if(!cognome.getText().isEmpty()){
                    int matricola_int = Integer.valueOf(matricola.getText().toString()).intValue();
                    if(DBMSManager.queryVerificaEsistenzaImpiegatoAttivo(matricola_int)){
                        Impiegato i = DBMSManager.getImpiegatoByMatricola(matricola_int);
                        if(i  != null){
                            if(i.getNome().equalsIgnoreCase(nome.getText().toString())){
                                if(i.getCognome().equalsIgnoreCase(cognome.getText().toString())){
                                    if(DBMSManager.queryLicenziamentoImpiegato(matricola_int)){
                                        Alert.showDialog(true,"Successo","Hai licenziato correttamente l'impiegato ["+i.getMatricola()+"] "+ i.getCognome()+ " " + i.getNome());
                                    }else{
                                        Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                                    }
                                }else{
                                    Alert.showDialog(false,"Errore","Il cognome non corrisponde alla matricola selezionata.");
                                }
                            }else{
                                Alert.showDialog(false,"Errore","Il nome non corrisponde alla matricola selezionata.");
                            }
                        }else{
                            Alert.showDialog(false,"Errore","Impiegato non trovato.");
                        }
                    }else{
                        Alert.showDialog(false,"Errore","Impiegato inesistente!");
                    }
                }else{
                    Alert.showDialog(false,"Errore","Devi prima compilare il campo cognome!");
                }
            }else{
                Alert.showDialog(false,"Errore","Devi prima compilare il campo nome!");
            }
        }else{
            Alert.showDialog(false,"Errore","Devi prima compilare il campo matricola!");
        }
    }

    public static void onClickAssumiImpiegato(TextField nome, TextField cognome, TextField n_telefono,  TextField servizio, TextField IBAN, TextField mail, TextField cod_fisc, DatePicker d_nascita){
        if(!nome.getText().isEmpty()){
            if(!cognome.getText().isEmpty()){
                if(!n_telefono.getText().isEmpty() && n_telefono.getText().length()==10 && !(n_telefono.getText().matches("[a-zA-Z]+"))){
                        if(!servizio.getText().isEmpty() && (servizio.getText().equalsIgnoreCase("A") || servizio.getText().equalsIgnoreCase("B") || servizio.getText().equalsIgnoreCase("C") || servizio.getText().equalsIgnoreCase("D"))){
                            if(!IBAN.getText().isEmpty() && IBAN.getText().length()==27){
                                if(!mail.getText().isEmpty() && mail.getText().contains("@")){
                                    if(!cod_fisc.getText().isEmpty() && cod_fisc.getText().length()==16){
                                        if(d_nascita.getValue() != null &&  (LocalDate.from(d_nascita.getValue()).until(LocalDate.now(), ChronoUnit.YEARS))>=18){
                                            String nome_cognome = nome.getText().toString() + "."+ cognome.getText().toString();
                                            if(DBMSManager.queryAssunzioneImpiegato(nome.getText(), cognome.getText(), d_nascita.getValue().toString(), n_telefono.getText().toString(), servizio.getText().charAt(0)+"", IBAN.getText().toString(),mail.getText().toString(), cod_fisc.getText().toString(), DigestUtils.sha1Hex(nome_cognome))){
                                                Impiegato i = DBMSManager.queryGetMatricolaImpiegato(mail.getText().toString(),nome.getText().toString(),cognome.getText().toString());
                                                Smtp_mail smtp = new Smtp_mail();
                                                if(i != null) {
                                                    try {
                                                        smtp.sendMessageAssumiImpiegato(mail.getText().toString(), i.getMatricola(), nome_cognome);
                                                    } catch (MessagingException e) {
                                                        throw new RuntimeException(e);
                                                    }
                                                }else{
                                                    Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                                                }
                                                Alert.showDialog(true,"Successo","Hai assunto correttamente l'impiegato " + cognome.getText().toString()+" " + nome.getText().toString()+".");
                                            }else{
                                                Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                                            }
                                        }else{
                                            Alert.showDialog(false,"Errore","Campo data di nascita mancante!");
                                        }
                                    }else{
                                        Alert.showDialog(false,"Errore","Campo codice fiscale mancante o errato!");
                                    }
                                }else{
                                    Alert.showDialog(false,"Errore","Campo mail mancante o errato!");
                                }
                            }else{
                                Alert.showDialog(false,"Errore","Campo iban mancante o non valido!");
                            }
                        }else{
                            Alert.showDialog(false,"Errore","Campo servizio mancante o errato!");
                        }
                }else{
                    Alert.showDialog(false,"Errore","Campo numero di telefono mancante o non valido!");
                }
            }else{
                Alert.showDialog(false,"Errore","Campo cognome mancante!");
            }
        }else{
            Alert.showDialog(false,"Errore","Campo nome mancante!");
        }
    }

}
