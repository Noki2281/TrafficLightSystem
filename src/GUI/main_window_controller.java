package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class main_window_controller {
    @FXML
    public Button button_l;
    @FXML
    public ComboBox traffic_combo_box;
    @FXML
    public ComboBox algorithem_combobox;
    @FXML
    public ComboBox random_combobox;

    @FXML
    public void initialize(){
        for (int i = 0; i<10; i++)
            traffic_combo_box.getItems().add(""+(i+1));
        algorithem_combobox.getItems().addAll("Standard", "Smart");
        random_combobox.getItems().addAll("Random", "Equal");
    }
}
