package it.spvm.progetto_2022_2023_fx.terminal.interfaces;


import it.spvm.progetto_2022_2023_fx.terminal.controls.TerminaleController;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;


public class IngressoTerminale {
    @FXML
    TextField matricola;

    @FXML
    TextField nome;

    @FXML
    TextField cognome;

    //Evento nel terminale d'ingresso e clicchiamo su 'Firma' dopo avere compilato i campi
    @FXML
    protected void onClickFirma() throws IOException {
        TerminaleController.firmaIngresso(matricola,nome,cognome);
    }



}
