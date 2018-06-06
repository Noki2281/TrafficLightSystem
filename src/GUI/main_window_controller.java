package GUI;

import Class.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class main_window_controller {
    @FXML
    public Button button_l;
    @FXML
    public Button start_btn, stop_btn;
    @FXML
    public ComboBox traffic_combo_box;
    @FXML
    public ComboBox algorithem_combobox;
    @FXML
    public ComboBox random_combobox;
    @FXML
    public TableView tableView;

    public static Thread simThread, updateThread;
    public static MainSim simInstance;
    public static TableUpdater tableUpdater;
    public static int trafficLevel = 1;
    public static String algorithm ="Standard";
    public static String insertMethod = "Random";
    public static int algo = 1;
    public static int mode = 1;

    @FXML
    public void initialize(){
        TableColumn<Lane,String> laneCol = new TableColumn<>("Lane #");
        laneCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getLaneNum()));
        laneCol.setCellFactory(column -> new TableCell<Lane, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item);
                    TableRow currentRow = getTableRow();
                    Lane l = getTableView().getItems().get(getIndex());
                    if (l.is_green()) {
                        currentRow.setStyle("-fx-background-color: #3FCA56;");

                    } else currentRow.setStyle("-fx-background-color: red;");
                }
            }
        });
        TableColumn<Lane,String> carsCol = new TableColumn<>("Number of Cars");
        carsCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getCarList().size()));
        TableColumn<Lane,String> avgCol = new TableColumn<>("AVG Waiting Time");
        avgCol.setCellValueFactory(c-> new SimpleStringProperty(calcAverageForLane(c.getValue())));
        TableColumn<Lane,String> weightCol = new TableColumn<>("Lane Weight");
        weightCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getWeight()));
        tableView.getColumns().addAll(laneCol,carsCol,avgCol,weightCol);

        for (int i = 0; i<10; i++)
            traffic_combo_box.getItems().add(""+(i+1));
        algorithem_combobox.getItems().addAll("Standard", "Smart");
        random_combobox.getItems().addAll("Random", "Equal","Weight");
        tableUpdater = new TableUpdater(simInstance);
        updateThread = new Thread(tableUpdater);
        updateThread.start();
    }

    @FXML
    public void startSimulation(){
        if (simInstance==null)
            simInstance = new MainSim(trafficLevel, true,0,algo,mode);
        tableUpdater = new TableUpdater(simInstance);
        updateThread = new Thread(tableUpdater);
        updateThread.start();
        simThread = new Thread(simInstance);
        simThread.start();
    }

    @FXML
    public void stopSimulation(){
        if (simThread!=null){
            simThread.stop();
        }
        if (updateThread!=null){
            updateThread.stop();
        }
    }

    @FXML
    public void trafficLevelSet(){
        trafficLevel = Integer.parseInt(traffic_combo_box.getValue().toString());
        if (simInstance!=null){
            simInstance.setTrafficLevel(trafficLevel);
        }
    }

    @FXML
    public void algorithmSelect(){
        algorithm = algorithem_combobox.getValue().toString();

        //algo = 0;
        if (algorithm.equals("Standard")) algo = MainSim.STANDARD;
        if (algorithm.equals("Smart")) algo = MainSim.SMART;
        if (algorithm.equals("Standard Waze")) algo = MainSim.STANDARD_WAZE;
        if (algorithm.equals("Sensor")) algo = MainSim.SENSOR;
        if (simInstance!=null){
            simInstance.setAlgorithm(algo);
        }
    }

    @FXML
    public void insertModeSelect(){
        insertMethod = random_combobox.getValue().toString();
        if (insertMethod.equals("Random")) mode = MainSim.RANDOM;
        if (insertMethod.equals("Equal")) mode = MainSim.EQUAL;
        if (insertMethod.equals("Weight")) mode = MainSim.WEIGHT;
        if (simInstance!=null){
            simInstance.setInsertMode(mode);
        }
    }

    @FXML
    public void goToReportPage(ActionEvent event){
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../GUI/report_page.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Report");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
            // Hide this current window (if this is what you want)
//            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TableUpdater implements Runnable{

        private MainSim _ms;
        public TableUpdater(MainSim ms){
            _ms=ms;
        }

        @Override
        public void run() {
            while(true){
                if (_ms!=null) {

                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArrayList<Lane> lanes = _ms.getLaneList();
//                    for (Lane l : lanes){
//                        System.out.println(l.getCarList().size());
//                    }
                    tableView.getItems().clear();
                    tableView.setItems(FXCollections.observableArrayList(lanes));
                }
            }
        }
    }
    public String calcAverageForLane(Lane l){
        String res ="";
        ArrayList<Car> cars = l.getCarList();
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double avg = 0;
        int now = 0;
        if (simInstance!=null) now = simInstance.getTime();
        for (Car c : cars){
            avg += now - c.getTime();
        }
        avg = avg==0? 0: avg/(cars.size());
        res = df.format(avg);
        return res;
    }


}
