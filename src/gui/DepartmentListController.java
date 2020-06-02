package gui;

import application.Main;
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
import model.entities.Department;
import model.services.DepartmentService;

import java.io.IOException;
import java.net.URL;
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
}
