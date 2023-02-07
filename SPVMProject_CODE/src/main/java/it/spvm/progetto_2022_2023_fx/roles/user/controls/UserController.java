package it.spvm.progetto_2022_2023_fx.roles.user.controls;

import it.spvm.progetto_2022_2023_fx.utils.Alert;
import it.spvm.progetto_2022_2023_fx.utils.DBMSManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;

public class UserController {

    public static void onClickAggiornaPassword(TextField old_password,TextField new_password,TextField confirm_new_password){
        if(!old_password.getText().isEmpty() && !new_password.getText().isEmpty() && !confirm_new_password.getText().isEmpty())
        {
            if (DigestUtils.sha1Hex(old_password.getText()).equals(DBMSManager.getCurrentImpiegato().getPassword())) {
                if (!DigestUtils.sha1Hex(old_password.getText()).equals(DigestUtils.sha1Hex(new_password.getText()))) {
                    if (new_password.getText().equalsIgnoreCase(confirm_new_password.getText())) {
                        if (DBMSManager.queryAggiornaPassword(new_password.getText())) {
                            Alert.showDialog(true, "Successo", "Hai aggiornato correttamente la password!");
                            DBMSManager.getCurrentImpiegato().setPassword(DigestUtils.sha1Hex(new_password.getText()));
                            old_password.clear();
                            new_password.clear();
                            confirm_new_password.clear();
                        } else {
                            Alert.showDialog(false, "Errore", "Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                        }
                    } else {
                        Alert.showDialog(false, "Errore", "Le nuove password non coincidono!");
                    }
                } else {
                    Alert.showDialog(false, "Errore", "Vecchia e nuova password coincidono!");
                }
            } else {
                Alert.showDialog(false, "Errore", "Vecchia password non valida!");

            }
        }else{
            Alert.showDialog(false, "Errore", "Campi vuoti!");

        }
    }
    public static void showCurrentPhoneString(Text current_phone){
        current_phone.setText(DBMSManager.getCurrentImpiegato().getN_telefono());
    }
    public static void showCurrentIBANString(Text current_iban){
        current_iban.setText(DBMSManager.getCurrentImpiegato().getIBAN());
    }


    public static ArrayList<Node> getAllNodes(Parent root) {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(root, nodes);
        return nodes;
    }

    public static void onClickAggiornaTelefono(TextField new_telefono_field, Text current_ntelefono){
        if(!new_telefono_field.getText().isEmpty()){
            if(new_telefono_field.getText().length()==10) {
                if(!new_telefono_field.getText().equals(current_ntelefono.getText())) {
                    if (DBMSManager.queryAggiornaNumeroTelefono(new_telefono_field.getText())) {
                        Alert.showDialog(true, "Successo", "Numero di Telefono modificato con successo!");
                        DBMSManager.getCurrentImpiegato().setN_telefono(new_telefono_field.getText());
                        current_ntelefono.setText(new_telefono_field.getText());
                        new_telefono_field.clear();
                    }else{
                        Alert.showDialog(false,"Errore","Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    }
                }else{
                    Alert.showDialog(false,"Errore","Il vecchio ed il nuovo numero di telefono coincidono!");
                }
            }else{
                Alert.showDialog(false,"Errore","Numero di telefono non valido!");

            }
        }else{
            Alert.showDialog(false,"Errore","Campo vuoto!");
        }
    }
    public static void onClickAggiornaIBAN(TextField new_iban_field, Text current_iban, Text new_iban){
        if(!new_iban_field.getText().isEmpty()){
            if(new_iban_field.getText().length() == 27){
                if(!current_iban.getText().equalsIgnoreCase(new_iban_field.getText())) {
                    if (DBMSManager.queryAggiornaIBAN(new_iban_field.getText())) {
                        DBMSManager.getCurrentImpiegato().setIBAN(new_iban_field.getText());
                        current_iban.setText(DBMSManager.getCurrentImpiegato().getIBAN());
                        new_iban.setText("");
                        new_iban_field.clear();
                        Alert.showDialog(true, "Successo", "IBAN modificato con successo!");
                    }else{
                        Alert.showDialog(false,"Errore","Errore durante la comunicazione con il DBMS, contatta un amministratore!");
                    }
                }else{
                    Alert.showDialog(false,"Errore","Il vecchio ed il nuovo iban coincidono!");
                }
            }else{
                Alert.showDialog(false,"Errore","IBAN non valido!");

            }
        }else{
            Alert.showDialog(false,"Errore","Campo vuoto!");
        }
    }

    private static void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }


}
