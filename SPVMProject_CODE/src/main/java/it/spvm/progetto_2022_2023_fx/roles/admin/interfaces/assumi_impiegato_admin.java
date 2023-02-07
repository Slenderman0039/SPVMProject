package it.spvm.progetto_2022_2023_fx.roles.admin.interfaces;

import it.spvm.progetto_2022_2023_fx.roles.admin.controls.AdminController;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


public class assumi_impiegato_admin {

    @FXML
    private TextField nome;

    @FXML
    private TextField cognome;

    @FXML
    private TextField n_telefono;


    @FXML
    private TextField servizio;

    @FXML
    private TextField IBAN;

    @FXML
    private TextField mail;

    @FXML
    private TextField cod_fisc;

    @FXML
    private DatePicker d_nascita;

  @FXML
    protected void onClickAssumiImpiegato(){
      AdminController.onClickAssumiImpiegato( nome,  cognome,  n_telefono,  servizio,  IBAN,  mail,  cod_fisc,  d_nascita);
  }



}
