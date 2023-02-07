package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class licenzia_impiegato_admin {

    @FXML
    TextField matricola;

    @FXML
    TextField nome;

    @FXML
    TextField cognome;

    @FXML
    protected void onClicklicenziaImpiegato(){
        AdminController.onClickLicenziaImpiegato(matricola,nome,cognome);
    }
}
