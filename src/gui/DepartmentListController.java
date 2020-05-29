package gui;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Department;
import model.services.DepartmentService;

import java.net.URL;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable {

    private DepartmentService departmentService;

    @FXML
    private Button btnNew;

    @FXML
    private TableView<Department> tableViewDepartment;

    @FXML
    private TableColumn<Department,Integer> tableColumnId;

    @FXML
    private TableColumn<Department,String> tableColumnName;

    private ObservableList<Department> observableListDepartment;
    @FXML
    public void onBtnNewAction() {
        System.out.println("onBtnNewAction");
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
    }

    private void initializeNode() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
        tableViewDepartment.prefWidthProperty().bind(stage.widthProperty());
    }

}
