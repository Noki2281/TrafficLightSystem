package GUI;

import Class.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class main_window_controller {
    @FXML
    public Button edit_lanesBtn;
    @FXML
    public Button start_btn, stop_btn;
    @FXML
    public ComboBox algorithem_combobox;
    @FXML
    public ComboBox random_combobox;
    @FXML
    public TableView tableView;
    @FXML
    public Label carsGenNum;
    @FXML
    public TextField iterationsTxt;

    public static final String EQUAL = "Equal";
    public static final String UNIFORM = "Uniform";
    public static final String POISSON = "Poisson";

    public static final String STANDARD = "Standard";
    public static final String SMART = "Smart";

    public static Thread simThread, updateThread;
    public static MainSim simInstance;
    public static TableUpdater tableUpdater;
    public static int trafficLevel = 1;
    public static String algorithm =STANDARD;
    public static String insertMethod = EQUAL;
    public static int algo = 1;
    public static int mode = 1;
    public static int iterations = 1000;
    private static Crossroad cr1;

    @FXML
    public void initialize(){
        cr1 = new Crossroad();
        Lane l1 = new Lane(1,3,20,10,4,20,200);
        Lane l2 = new Lane(2,1,30,10,2,10,300);
        Lane l3 = new Lane(3,2,30,10,2,10,300);
        Lane l4 = new Lane(4,3,20,10,4,20,200);
        l1.getRelatives().add(l4);
        l3.getRelatives().add(l4);
        l4.getRelatives().add(l1);
        cr1.getLaneList().add(l1);
        cr1.getLaneList().add(l2);
        cr1.getLaneList().add(l3);
        cr1.getLaneList().add(l4);

        iterationsTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    iterationsTxt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

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


        algorithem_combobox.getItems().addAll(STANDARD, SMART);
        random_combobox.getItems().addAll(EQUAL,UNIFORM, POISSON);
        tableUpdater = new TableUpdater(simInstance);
        updateThread = new Thread(tableUpdater);
        updateThread.start();
    }

    @FXML
    public void edit_lanesPressed(){
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("../GUI/edit_lanes.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Edit Lanes");
            stage.setScene(new Scene(root, 1000, 650));
            stage.show();
            // Hide this current window (if this is what you want)
//            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void iterationsTxtChanged(ActionEvent event){
        MainSim.setGenerateCars(true);
        MainSim.carsGeneratedSum=0;
        carsGenNum.setText(""+MainSim.carsGeneratedSum);
        iterations = Integer.parseInt(iterationsTxt.getText());
    }

    @FXML
    public void startSimulation(){
        if (simInstance==null)
            simInstance = new MainSim(true,0,algo,mode,cr1);
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
    public void algorithmSelect(){
        algorithm = algorithem_combobox.getValue().toString();
        //algo = 0;
        if (algorithm.equals(STANDARD)) algo = MainSim.STANDARD;
        if (algorithm.equals(SMART)) algo = MainSim.SMART;
        if (simInstance!=null){
            simInstance.setAlgorithm(algo);
        }
    }

    @FXML
    public void insertModeSelect(){
        MainSim.setGenerateCars(true);
        MainSim.carsGeneratedSum=0;
        carsGenNum.setText(""+MainSim.carsGeneratedSum);
        insertMethod = random_combobox.getValue().toString();
        if (insertMethod.equals(EQUAL)) mode = MainSim.EQUAL;
        if (insertMethod.equals(UNIFORM)) mode = MainSim.UNIFORM;
        if (insertMethod.equals(POISSON)) mode = MainSim.POISSON;
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
            stage.setScene(new Scene(root, 1000, 650));
            stage.show();
            // Hide this current window (if this is what you want)
//            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        carsGenNum.setText(""+MainSim.carsGeneratedSum);
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

    public static Crossroad getCr1() {
        return cr1;
    }

}
