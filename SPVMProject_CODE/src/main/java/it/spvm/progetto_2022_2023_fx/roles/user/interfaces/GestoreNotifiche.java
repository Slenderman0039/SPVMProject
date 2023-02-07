package it.spvm.progetto_2022_2023_fx.roles.user.interfaces;

import it.spvm.progetto_2022_2023_fx.entity.Notifica;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.List;

public class GestoreNotifiche {

    public static List<Notifica> lista_notifiche;

    public static void shutdown() {
        for(Notifica notifica: lista_notifiche){
            DBMSManager.queryAggiornaNotifica(notifica.getData_Notifica());
        }
    }

}
