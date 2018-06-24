package GUI;

import Class.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class edit_lanes_controller {

    @FXML
    private TableView lane_table;

    @FXML
    private Button saveBtn;



    @FXML
    public void initialize(){

        TableColumn<Lane,String> laneCol = new TableColumn<>("Lane #");
        laneCol.setCellValueFactory(c-> new SimpleStringProperty("Lane "+c.getValue().getLaneNum()));
        TableColumn<Lane,String> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getWeight()));
        weightCol.setCellFactory(TextFieldTableCell.forTableColumn());
        weightCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setWeight(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        TableColumn<Lane,String> min_greenCol = new TableColumn<>("Min Green Time");
        min_greenCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getMin_green()));
        min_greenCol.setCellFactory(TextFieldTableCell.forTableColumn());
        min_greenCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setMin_green(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        TableColumn<Lane,String> max_idleCol = new TableColumn<>("Max Idle Time");
        max_idleCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getMax_idle_time()));
        max_idleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        max_idleCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setMax_idle_time(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        TableColumn<Lane,String> cycleCol = new TableColumn<>("Cycle Time");
        cycleCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getCycle_time()));
        cycleCol.setCellFactory(TextFieldTableCell.forTableColumn());
        cycleCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setCycle_time(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        TableColumn<Lane,String> ccpsCol = new TableColumn<>("Cars Crossing Per Second");
        ccpsCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getCars_crossing_per_second()));
        ccpsCol.setCellFactory(TextFieldTableCell.forTableColumn());
        ccpsCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setCars_crossing_per_second(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        TableColumn<Lane,String> lambdaCol = new TableColumn<>("Lambda (Cars Per Minute)");
        lambdaCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getLambda()));
        lambdaCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lambdaCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Lane, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Lane, String> t) {
                        ((Lane) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setLambda(Integer.parseInt(t.getNewValue()));
                    }
                }
        );
        lane_table.getColumns().addAll(laneCol,weightCol,min_greenCol,max_idleCol,cycleCol,ccpsCol,lambdaCol);
        lane_table.getSelectionModel().setCellSelectionEnabled(true);
        lane_table.setEditable(true);
        lane_table.setItems(FXCollections.observableArrayList(main_window_controller.getCr1().getLaneList()));
    }

    @FXML
    public void saveBtnPressed(ActionEvent event){
        Stage stage = (Stage) saveBtn.getScene().getWindow();
        stage.close();
    }

}