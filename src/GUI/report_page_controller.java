package GUI;
import Class.*;
import com.sun.javafx.charts.Legend;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class report_page_controller {


    @FXML
    private Label algorithem_label;

    @FXML
    private Label total_avg_label;

    @FXML
    private Label number_of_lanes_label;

    @FXML
    private TableView tableview;

    @FXML
    private Label traffic_label;

    @FXML
    private Label insert_method_label;

    @FXML
    private Label total_cars_label;

    @FXML
    private PieChart pie;

    @FXML
    private BarChart<String, Number> bar_chart;
//    @FXML
//    private PieChart pie1;

    public MainSim ms;
    private int totalNumberOfCars = 0;
    private int totalNumberOfLanes = 0;
    private double totalAvg = 0;
    ObservableList<PieChart.Data> pieChartData;
    @FXML
    public void initialize(){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        TableColumn<Lane,String> laneCol = new TableColumn<>("Lane #");
        laneCol.setCellValueFactory(c-> new SimpleStringProperty("Lane "+c.getValue().getLaneNum()));
        TableColumn<Lane,String> carsCol = new TableColumn<>("Number of Cars");
        carsCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().reportTotalCars));
        TableColumn<Lane,String> avgCol = new TableColumn<>("AVG Waiting Time");
        avgCol.setCellValueFactory(c-> new SimpleStringProperty(df.format(c.getValue().reportTotalAvgTime/1000)));
        TableColumn<Lane,String> min_greenCol = new TableColumn<>("Min Green Time");
        min_greenCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getMin_green()));
        TableColumn<Lane,String> max_idleCol = new TableColumn<>("Max Idle Time");
        max_idleCol.setCellValueFactory(c-> new SimpleStringProperty(""+c.getValue().getMax_idle()));
        tableview.getColumns().addAll(laneCol,carsCol,avgCol,min_greenCol,max_idleCol);

        algorithem_label.setText(main_window_controller.algorithem);
        traffic_label.setText(main_window_controller.trafficLevel + "");
        insert_method_label.setText(main_window_controller.insertMethod + "");
        ms = new MainSim(main_window_controller.trafficLevel, false, 1000);
        ms.run();

        tableview.setItems(FXCollections.observableArrayList(ms.getLaneList()));

        XYChart.Series series2 = new XYChart.Series();
        series2.setName("Green Idle Time %");

        bar_chart.setTitle("Idle Time %");

        for (Lane l : ms.getLaneList()){
            totalNumberOfCars+=l.reportTotalCars;
            totalNumberOfLanes++;
            totalAvg += (l.reportTotalAvgTime/1000)*totalNumberOfCars;
            String laneName = "Lane " + l.getLaneNum();
            series2.getData().add(new XYChart.Data<>(laneName,((double)l.reportIdleGreen/(double)l.reportGreenCount)*100));
            pie.getData().add(new PieChart.Data("Lane "+l.getLaneNum() + " - " + df.format(((double)l.reportGreenCount)/(double)10) + "%",l.reportGreenCount));
        }
        bar_chart.getData().addAll(series2);
        number_of_lanes_label.setText(totalNumberOfLanes+"");
        total_cars_label.setText(totalNumberOfCars+"");
        total_avg_label.setText(df.format(totalAvg/totalNumberOfCars));

        //Used to change series color.
        for (XYChart.Series<String, Number> series : bar_chart.getData()) {
            if(series.getName().equals("Green Idle Time %"))
            {
                for(XYChart.Data data : series.getData())
                {
                    data.getNode().setStyle("-fx-bar-fill: #3FCA56;");
                }
            }
        }

        //Used to change legend color
        for(Node n : bar_chart.getChildrenUnmodifiable())
        {
            if(n instanceof Legend)
            {
                for(Legend.LegendItem items : ((Legend)n).getItems())
                {
                    System.out.println("Legend item text: " + items.getText());
                    if(items.getText().equals("Green Idle Time %"))
                    {
                        items.getSymbol().setStyle("-fx-bar-fill: #3FCA56;");
                    }
                }
            }
        }
    }
}
