package it.spvm.progetto_2022_2023_fx.roles.employee.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;



public class firma_da_remoto_impiegato {

    @FXML
    TextArea motivazione;

    @FXML
    protected void onFirmaRitardo(){
        EmployeeController.onFirmaRitardo(motivazione);
    }

}
