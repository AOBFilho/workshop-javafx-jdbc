package gui;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

import java.net.URL;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService service;

    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private DatePicker datePickerBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    private ObservableList<Department> observableListDepartment;

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

    public void setServices(SellerService service, DepartmentService departmentService) {
        this.service = service;
        this.departmentService = departmentService;
    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Seller was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
        textFieldEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        textFieldBaseSalary.setText(String.format("%.2f",entity.getBaseSalary()));
        datePickerBirthDate.setValue(entity.getBirthDate());
        if (entity.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    public void loadAssociateObject() {
        if (departmentService == null) {
            throw new IllegalStateException("Department Service was null");
        }
        observableListDepartment = FXCollections.observableArrayList(departmentService.findAll());
        comboBoxDepartment.setItems(observableListDepartment);
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>,ListCell<Department>> factory = lv -> new ListCell<>(){
            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);
                setText(empty ? "" : department.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

    private Seller getFormData() {
        ValidationException validationException = new ValidationException("Error validation");
        Seller seller = new Seller();
        seller.setId(Utils.tryParseToInt(textFieldId.getText()));

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            validationException.addErrorMessage("name","Field can't be empty");
        }
        seller.setName(textFieldName.getText());

        if (Utils.isEmailValid(textFieldEmail.getText())) {
            validationException.addErrorMessage("email","Invalid value");
        }
        seller.setEmail(textFieldEmail.getText());

        if (Double.parseDouble(textFieldBaseSalary.getText()) <= 0) {
            validationException.addErrorMessage("baseSalary","Value must be greater than zero");
        }
        seller.setBaseSalary(Double.parseDouble(textFieldBaseSalary.getText()));

        seller.setBirthDate(datePickerBirthDate.getValue());

        seller.setDepartment(comboBoxDepartment.getSelectionModel().getSelectedItem());
        if (validationException.getErrorsMessage().size() > 0) {
            throw validationException;
        }
        return seller;
    }

    private void initializeNodes() {
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxValue(textFieldName,70);
        Constraints.setTextFieldMaxValue(textFieldEmail,60);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Utils.formatDatePicker(datePickerBirthDate,"dd/MM/yyyy");
        initializeComboBoxDepartment();
    }

    private void notifyDataChangeListener(){
        for (DataChangeListener listener : dataChangeListeners) {
            listener.onDataChange();
        }
    }
}
