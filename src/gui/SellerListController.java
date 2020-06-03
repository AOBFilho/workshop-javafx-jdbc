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
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    private SellerService departmentService;

    @FXML
    private Button btnNew;

    @FXML
    private TableView<Seller> tableViewSeller;

    @FXML
    private TableColumn<Seller,Integer> tableColumnId;

    @FXML
    private TableColumn<Seller,String> tableColumnName;

    @FXML
    private TableColumn<Seller,String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, LocalDate> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller,Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller,Seller> tableColumnEdit;

    @FXML
    private TableColumn<Seller,Seller> tableColumnRemove;

    private ObservableList<Seller> observableListSeller;

    @FXML
    public void onBtnNewAction(ActionEvent event) {
        openDialogForm(new Seller(), "/gui/SellerForm.fxml", Utils.currentStage(event));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNode();
    }
    
    public void setSellerService(SellerService departmentService) {
        this.departmentService = departmentService;
    }

    public void updateTableView() {
        if (departmentService == null) {
            throw new IllegalStateException("Seller Service was null");
        }
        observableListSeller = FXCollections.observableArrayList(departmentService.findAll());
        tableViewSeller.setItems(observableListSeller);
        initEditButton();
        initDeleteButton();
    }

    private void initializeNode() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDate(tableColumnBirthDate,"dd/MM/yyyy");
        Utils.formatTableColumnDouble(tableColumnBaseSalary,2);

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
        tableViewSeller.prefWidthProperty().bind(stage.widthProperty());
    }

    private void openDialogForm(Seller department, String absoluteName, Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();
            SellerFormController departmentFormController = loader.getController();
            departmentFormController.setEntity(department);
            departmentFormController.setServices(new SellerService(), new DepartmentService());
            departmentFormController.loadAssociateObject();
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
            protected void updateItem(Seller department, boolean empty) {
                super.updateItem(department, empty);
                if (department == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event ->
                        openDialogForm(department, "/gui/SellerForm.fxml", Utils.currentStage(event)));
            }
        });
    }

    private void initDeleteButton() {
        tableColumnRemove.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnRemove.setCellFactory(param -> new TableCell<>(){
            private final Button button = new Button("Delete");

            @Override
            protected void updateItem(Seller department, boolean b) {
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

    private void removeEntity(Seller department) {
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
