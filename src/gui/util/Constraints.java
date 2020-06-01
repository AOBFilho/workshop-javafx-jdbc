package gui.util;

import javafx.scene.control.TextField;

public class Constraints {

    public static void setTextFieldInteger(TextField textField) {
        textField.textProperty().addListener( (obs,oldValue,newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")){
                textField.setText(oldValue);
            }
        });
    }

    public static void setTextFieldMaxValue(TextField textField, int maxValue) {
        textField.textProperty().addListener((obs,oldValue,newValue) -> {
            if (newValue != null && newValue.length() > maxValue) {
                textField.setText(oldValue);
            }
        });
    }

    public static  void setTextFieldDouble(TextField textField) {
        textField.textProperty().addListener((obs,oldValue,newValue) -> {
            if (newValue != null && newValue.matches("\\d*([\\.]\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }
}
