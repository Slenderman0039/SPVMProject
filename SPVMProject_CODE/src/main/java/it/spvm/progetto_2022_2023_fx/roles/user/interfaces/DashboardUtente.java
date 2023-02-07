package it.spvm.progetto_2022_2023_fx.roles.user.interfaces;

import it.spvm.progetto_2022_2023_fx.Main;
import it.spvm.progetto_2022_2023_fx.entity.Notifica;
import it.spvm.progetto_2022_2023_fx.roles.Login;
import it.spvm.progetto_2022_2023_fx.roles.user.controls.UserController;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
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
import java.util.ArrayList;

public class DashboardUtente {

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
    @FXML
    protected void showAccount() {
        title.setText("Account");
        stackpane.getChildren().set(0,main_pane);
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
}
