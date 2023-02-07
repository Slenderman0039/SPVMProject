package it.spvm.progetto_2022_2023_fx.roles.user.controls;

import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import it.spvm.progetto_2022_2023_fx.roles.employee.interfaces.DashboardImpiegato;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;


public class modifica_password_impiegato extends DashboardImpiegato {

    boolean click = false;
    @FXML
    private ImageView check;

    @FXML
    private ImageView double_check;

    @FXML
    private PasswordField old_password;
    @FXML
    private PasswordField new_password;

    @FXML
    private Label label_show_password;
    @FXML
    private PasswordField confirm_new_password;

    @FXML
    private Label sistema;

    @FXML
    protected void type_password(){
        if(confirm_new_password.getText().equals(new_password.getText())){
            sistema.setVisible(false);
            check.setVisible(true);
            double_check.setVisible(true);
        }else {
            check.setVisible(false);
            double_check.setVisible(false);
            sistema.setVisible(true);
            sistema.setText("Le password non coincidono");
            sistema.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
            sistema.setTextFill(Color.RED);
        }
    }
    @FXML
    protected void show_button(){
        if(click==false) {
            click = true;
            label_show_password.setText(old_password.getText());
            label_show_password.setVisible(true);
        }
        else{
            click= false;
            label_show_password.setVisible(false);
        }

    }
    @FXML
    protected  void onShowPassword(){
        if(click) {
            label_show_password.setText(old_password.getText());
        }else{
            label_show_password.setVisible(false);
        }
    }
    //CONTROLLI DB
    @FXML
    protected void clickConferma() throws IOException {
        UserController.onClickAggiornaPassword(old_password,new_password,confirm_new_password);
    }



}
