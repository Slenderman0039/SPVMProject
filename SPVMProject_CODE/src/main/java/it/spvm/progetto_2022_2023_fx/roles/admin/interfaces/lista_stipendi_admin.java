package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.Index;
import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;

public class lista_stipendi_admin {

    public static void VisualizzaStipendi(Label title, StackPane stackpane) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Index.class.getResource("/it/spvm/progetto_2022_2023_fx/admin/pane_lista_stipendi.fxml"));
        Parent parent = fxmlLoader.load();
        title.setText("Lista stipendi");
        stackpane.getChildren().set(0,parent);
        AdminController.onClickVisualizzaStipendi(parent);
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
