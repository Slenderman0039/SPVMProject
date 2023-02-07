package it.spvm.progetto_2022_2023_fx.roles.employee.interfaces;

import com.calendarfx.view.TimeField;
import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.entity.Notifica;
import it.spvm.progetto_2022_2023_fx.entity.Stipendio;
import it.spvm.progetto_2022_2023_fx.roles.user.controls.UserController;
import it.spvm.progetto_2022_2023_fx.roles.user.interfaces.GestoreNotifiche;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class DashboardImpiegato {

    DatePicker dp_to;
    DatePicker dp_from;
    ChoiceBox cb;
    TimeField tf_from;
    TimeField tf_to;
    private ArrayList<Stipendio> lista_stipendi;

    public static final ObservableList<Notifica> dataList = FXCollections.observableArrayList();
    @FXML
    protected MenuButton menu_button;
    @FXML
    private ImageView logout_button;
    @FXML
    protected Label title;

    @FXML
    protected StackPane stackpane;
    @FXML
    private Label sistema;
    @FXML
    public Pane main_pane;
    boolean flag;
    @FXML
    private TableView tabella_stipendio;



    //LOGOUT
    @FXML
    protected void onLogout() throws IOException {
        Stage stage = Login.mainstage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 451, 282);
        stage.setResizable(false);
        stage.setTitle("Autenticazione");
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        Login.dashboard_impiegato.close();
    }

    //SHOW BUTTON ACCOUNT
    @FXML
    protected void showAccount() {
        title.setText("Account");
        stackpane.getChildren().set(0,main_pane);
    }
    //MODIFICA
    @FXML
    protected void onModificaIban() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/utenti/pane_modifica_iban.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Modifica IBAN");
        stackpane.getChildren().set(0,parent);
        for(Node n: getAllNodes(parent)){
            if(n instanceof Text){
                Text testo = ((Text) n);
                if(testo.getText().equalsIgnoreCase("{current_iban}")){
                    UserController.showCurrentIBANString(testo);
                }
            }
        }
    }
    @FXML
    protected void onModificaPassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/utenti/pane_modifica_password.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Modifica Password");
        stackpane.getChildren().set(0,parent);
    }
    @FXML
    protected void onModificaTelefono() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/utenti/pane_modifica_ntelefono.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Modifica N. Telefono");
        stackpane.getChildren().set(0,parent);
        for(Node n: getAllNodes(parent)){
            if(n instanceof Text){
                Text testo = ((Text) n);
                if(testo.getText().equalsIgnoreCase("{current_ntelefono}")){
                    UserController.showCurrentPhoneString(testo);
                }
            }
        }
    }

    @FXML
    protected void onVisualizzaStipendio() throws IOException {
        visualizza_stipendio_impiegato.VisualizzaStipendio(title,stackpane,120);
    }

    @FXML
    protected void onVisualizzaTurni() throws IOException {
        visualizza_turni_impiegato.VisualizzaTurni();
    }
    @FXML
    protected void onFirmaRemoto() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/impiegati/pane_firma_remoto.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Firma da Remoto");
        stackpane.getChildren().set(0,parent);
    }
    @FXML
    protected void onRichiestaPermesso() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/it/spvm/progetto_2022_2023_fx/impiegati/pane_richiesta_permesso.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Richiesta Permesso");

        stackpane.getChildren().set(0,parent);
        for(Node n: getAllNodes(parent)){
            if(n instanceof DatePicker){
                if(n.getId().equalsIgnoreCase("data_picker_to")){
                    dp_to = (DatePicker) n;
                }
                if(n.getId().equalsIgnoreCase("data_picker_from")){
                    dp_from = (DatePicker) n;
                    dp_from.valueProperty().addListener(new ChangeListener<LocalDate>() {
                        @Override
                        public void changed(ObservableValue<? extends LocalDate> observableValue, LocalDate localDate, LocalDate t1) {
                            if(cb != null && cb.getValue().equals("Permesso")){
                                dp_to.setDisable(true);
                                dp_to.setValue(dp_from.getValue());
                            }
                        }
                    });
                }
            }
            if(n instanceof TimeField){
                if(n.getId().equalsIgnoreCase("time_from")){
                    tf_from = (TimeField) n;
                }
                if(n.getId().equalsIgnoreCase("time_to")){
                    tf_to = (TimeField) n;
                }
            }
            if(n instanceof ChoiceBox){

                cb = ((ChoiceBox) n);
                cb.setValue("Tipo Permesso");
                cb.getItems().add("Permesso");
                cb.getItems().add("Ferie");
                cb.getItems().add("Malattie");
                cb.getItems().add("Congedo");
                cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                            if (cb.getItems().get((Integer) number2).equals("Ferie")) {
                                    dp_to.setDisable(false);
                                    tf_from.setDisable(true);
                                    tf_from.setValue(LocalTime.of(0,0));
                                    tf_to.setDisable(true);
                                    tf_to.setValue(LocalTime.of(0,0));
                            } else if (cb.getItems().get((Integer) number2).equals("Permesso")) {
                                dp_to.setDisable(true);
                                tf_from.setDisable(false);
                                tf_to.setDisable(false);
                                dp_to.setValue(dp_from.getValue());
                            } else if (cb.getItems().get((Integer) number2).equals("Congedo")) {
                                dp_to.setDisable(false);
                                tf_from.setDisable(true);
                                tf_from.setValue(LocalTime.of(0,0));
                                tf_to.setDisable(true);
                                tf_to.setValue(LocalTime.of(0,0));
                            }else{
                                dp_to.setDisable(false);
                                tf_from.setDisable(false);
                                tf_to.setDisable(false);
                            }
                    }
                });
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

    @FXML
    protected void controllaNotifiche() {

        Stage stage = Login.mainstage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("./pages/notification.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 451, 282);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.getIcons().add(new Image(Login.class.getResource("/assets/images/logo.png").toExternalForm()));
        stage.setResizable(false);
        stage.setTitle("Le tue notifiche");
        scene.getStylesheets().add(getClass().getResource("/fontstyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setOnHidden(e -> GestoreNotifiche.shutdown());
        stage.show();

        TableView tab = (TableView) scene.lookup("#table");
        TextField search = (TextField) scene.lookup("#search");
                tab.setEditable(false);

                TableColumn titoloColumn = new TableColumn("Titolo");
                titoloColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
                titoloColumn.setPrefWidth(120.3999605178833);
                titoloColumn.setStyle("-fx-alignment: CENTER;");
                titoloColumn.setEditable(false);
                titoloColumn.setReorderable(false);


                TableColumn messaggioColumn = new TableColumn("Messaggio");
                messaggioColumn.setCellValueFactory(new PropertyValueFactory<>("messaggio"));
                messaggioColumn.setPrefWidth(195.59999084472656);
                messaggioColumn.setStyle("-fx-alignment: CENTER;");
                messaggioColumn.setEditable(false);
                messaggioColumn.setReorderable(false);


                TableColumn DataColumn = new TableColumn("Data");
                DataColumn.setCellValueFactory(new PropertyValueFactory<>("Data_Notifica"));
                DataColumn.setPrefWidth(114.800048828125);
                DataColumn.setStyle("-fx-alignment: CENTER;");
                DataColumn.setEditable(false);
                DataColumn.setReorderable(false);

                TableColumn LettaColumn = new TableColumn("Letta");
                LettaColumn.setCellValueFactory(new PropertyValueFactory<>("LettaString"));
                LettaColumn.setPrefWidth(114.800048828125);
                LettaColumn.setStyle("-fx-alignment: CENTER;");
                LettaColumn.setEditable(false);
                LettaColumn.setReorderable(false);

                tab.getColumns().addAll(titoloColumn, messaggioColumn, DataColumn,LettaColumn);

                //RICHIAMARE DAL DB TUTTI GLI STIPENDI E FARE UN CICLO FOR CHE CREA n OGGETTI di tipo STIPENDIO E LI AGGIUNGE ALLA TAB
                //SELECT DEGLI STIPENDI IN ORDINE DI DATA (La 1° è l'ultimo stipendio)
        GestoreNotifiche.lista_notifiche =  DBMSManager.queryVisualizzaNotifiche();
                dataList.clear();
                if(GestoreNotifiche.lista_notifiche != null){
                    for (Notifica n: GestoreNotifiche.lista_notifiche) {
                        tab.getItems().add(n);
                        dataList.add(n);
                    }
                }

        FilteredList<Notifica> filteredData = new FilteredList<>(dataList, b -> true);

        search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(notifica -> {
                // If filter text is empty, display all persons.


                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                 if(notifica.getData_Notifica().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }else if(notifica.getMessaggio().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                }else if(notifica.getTitolo().toLowerCase().contains(lowerCaseFilter)) {
                     return true; // Filter matches last name.
                 }else
                    return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<Notifica> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        // 	  Otherwise, sorting the TableView would have no effect.
        sortedData.comparatorProperty().bind(tab.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        tab.setItems(sortedData);
                messaggioColumn.setSortable(false);
                titoloColumn.setSortable(false);

                for(Notifica notifica: GestoreNotifiche.lista_notifiche){
                    DBMSManager.queryAggiornaNotifica(notifica.getData_Notifica());
                }
            }
        }
