package it.spvm.progetto_2022_2023_fx.roles.employee.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;


import java.io.IOException;

public class visualizza_stipendio_impiegato {

public static void VisualizzaStipendio(Label title, StackPane stackpane, int size_table) throws IOException {
    EmployeeController.onClickVisualizzaStipendi(title,stackpane,size_table);
}




}
