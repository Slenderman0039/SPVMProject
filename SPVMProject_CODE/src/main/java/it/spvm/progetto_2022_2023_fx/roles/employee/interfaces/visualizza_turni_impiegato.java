package it.spvm.progetto_2022_2023_fx.roles.employee.interfaces;


import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.entity.Turno;
import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;

import java.util.ArrayList;

public class visualizza_turni_impiegato {

public static void VisualizzaTurni(){
    ArrayList<Turno> turni = DBMSManager.queryTurniImpiegato();
    if(turni == null){
        Alert.showDialog(false,"Errore","Non ci sono turni disponibili.");
    }else {
        EmployeeController.onVisualizzaTurni(turni,"I tuoi turni");
    }
}



}
