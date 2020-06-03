package gui.util;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utils {

    public static Stage currentStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static Integer tryParseToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double tryParseToDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static <T> void formatTableColumnDate(TableColumn<T, LocalDate> tableColumn, String format) {
        tableColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern(format)));
                }
            }
        });
    }

    public static <T> void formatTableColumnDouble(TableColumn<T, Double> tableColumn, int decimalPlaces) {
        tableColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    Locale.setDefault(Locale.US);
                    setText(String.format("%." + decimalPlaces + "f", item));
                }
            }
        });
    }

    public static void formatDatePicker(DatePicker datePicker, String format) {
        datePicker.setConverter(new StringConverter<>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
            {
                datePicker.setPromptText(format.toLowerCase());
            }
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(regex);
        return (email != null) && (!email.trim().equals("")) && (pattern.matcher(email).matches());
    }
}
