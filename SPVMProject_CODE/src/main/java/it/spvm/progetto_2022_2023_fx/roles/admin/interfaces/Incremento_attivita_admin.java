package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;

public class Incremento_attivita_admin {

    @FXML
    DatePicker date_picker_from;

    @FXML
    DatePicker date_picker_to;

    @FXML
    protected void onClickIncrementoAttivita() {
        AdminController.IncrementoAttivita(date_picker_from,date_picker_to);
    }
}
