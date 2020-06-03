package gui;

import application.Main;
import db.DBIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jdk.jshell.execution.Util;
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    private DepartmentService departmentService;

    @FXML
    private Button btnNew;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department,Integer> tableColumnId;

    @FXML
    private TableColumn<Department,String> tableColumnName;

    @FXML
    private TableColumn<Department,Department> tableColumnEdit;

    @FXML
    private TableColumn<Department,Department> tableColumnRemove;

    private ObservableList<Department> observableListDepartment;

    @FXML
    public void onBtnNewAction(ActionEvent event) {
        openDialogForm(new Department(), "/gui/DepartmentForm.fxml", Utils.currentStage(event));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNode();
    }
    
    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void updateTableView() {
        if (departmentService == null) {
            throw new IllegalStateException("Department Service was null");
        }
        observableListDepartment = FXCollections.observableArrayList(departmentService.findAll());
        tableViewDepartment.setItems(observableListDepartment);
        initEditButton();
        initDeleteButton();
    }

    private void initializeNode() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
        tableViewDepartment.prefWidthProperty().bind(stage.widthProperty());
    }

    private void openDialogForm(Department department, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();
            DepartmentFormController departmentFormController = loader.getController();
            departmentFormController.setEntity(department);
            departmentFormController.setService(new DepartmentService());
            departmentFormController.subscribeDataChangeListener(this);
            departmentFormController.updateFormData();

            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(pane));
            modalStage.setTitle("Enter department data");
            modalStage.setResizable(false);
            modalStage.initOwner(parentStage);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            Alerts.showAlert("IO Exception","Error opening view",e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void onDataChange() {
        updateTableView();
    }

    private void initEditButton() {
        tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEdit.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Edit");

            @Override
            protected void updateItem(Department department, boolean empty) {
                super.updateItem(department, empty);
                if (department == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event ->
                        openDialogForm(department, "/gui/DepartmentForm.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initDeleteButton() {
        tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnRemove.setCellFactory(param -> new TableCell<>(){
            private final Button button = new Button("Delete");

            @Override
            protected void updateItem(Department department, boolean b) {
                super.updateItem(department, b);
                if (department == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(department));
            }
        });
    }

    private void removeEntity(Department department) {
        if (departmentService == null) {
            throw new IllegalStateException("Service was null");
        }
        Optional<ButtonType> result = Alerts.showConfirmation("Deleting department","Are you sure to delete");
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                departmentService.delete(department);
                updateTableView();
            } catch (DBIntegrityException e) {
                Alerts.showAlert("Error deleting department",null,e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
