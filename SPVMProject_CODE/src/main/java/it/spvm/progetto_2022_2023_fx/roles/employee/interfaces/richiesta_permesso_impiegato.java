package it.spvm.progetto_2022_2023_fx.roles.employee.interfaces;

import com.calendarfx.view.TimeField;
import it.spvm.progetto_2022_2023_fx.roles.employee.controls.EmployeeController;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;



public class richiesta_permesso_impiegato {
    @FXML
    ChoiceBox choice_box;

    @FXML
    TextField text_field;

    @FXML
    DatePicker data_picker_from;

    @FXML
    TimeField time_from;

    @FXML
    TimeField time_to;
    @FXML
    DatePicker data_picker_to;

    @FXML
    protected void onClickButton(){
        EmployeeController.onClickInserisciPermesso(data_picker_from,data_picker_to,time_from,time_to,choice_box);
    }
}
