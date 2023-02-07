package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.Index;
import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.entity.Impiegato;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;

public class lista_impiegati_admin {



    public static void VisualizzaImpiegati(Label title, StackPane stackpane) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Index.class.getResource("/it/spvm/progetto_2022_2023_fx/admin/pane_lista_impiegati.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Lista impiegati");
        stackpane.getChildren().set(0,parent);
        AdminController.onClickVisualizzaImpiegati(parent);

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
    public static void clickImpiegato(){
        AdminController.table.setRowFactory( tv -> {
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
