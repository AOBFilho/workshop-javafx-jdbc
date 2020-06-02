package gui;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        if (service == null) {
            throw new IllegalStateException("Service was null");
        }
        try {
            entity = getFormData();
            service.insertOrUpdate(entity);
            notifyDataChangeListener();
            Utils.currentStage(event).close();
        } catch (ValidationException e) {
            setErrorMessages(e.getErrorsMessage());
        } catch (DBException e) {
            Alerts.showAlert("Error saving department",null,e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setErrorMessages(Map<String, String> errorsMessage) {
        Set<String> fields = errorsMessage.keySet();
        if (fields.contains("name")) {
            labelErrorName.setText(errorsMessage.get("name"));
        }
    }

    @FXML
    public void onButtonCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    public void setEntity(Seller entity) {
        this.entity = entity;
    }

    public void setService(SellerService service) {
        this.service = service;
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Seller was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    private Seller getFormData() {
        ValidationException validationException = new ValidationException("Error validation");
        Seller department = new Seller();
        department.setId(Utils.tryParseToInt(textFieldId.getText()));
        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            validationException.addErrorMessage("name","Field can't be empty");
        }
        department.setName(textFieldName.getText());
        if (validationException.getErrorsMessage().size() > 0) {
            throw validationException;
        }
        return department;
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxValue(textFieldName,30);
    }

    private void notifyDataChangeListener(){
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChange();
        }
    }
}
