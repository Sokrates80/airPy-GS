package airpygs;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    serialHandler cli;
    boolean cliConnected = false;

    @FXML
    private TextArea cliConsole;

    @FXML
    private Button bConnect;

    @FXML
    private ChoiceBox serialCombo;

    @FXML
    private ChoiceBox baudRateCombo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";

        baudRateCombo.setItems(FXCollections.observableArrayList("9600","14400","38400","57600","115200"));
        serialCombo.setItems(FXCollections.observableArrayList(SerialPortList.getPortNames()));

        bConnect.setText("Connect");
        bConnect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (cliConnected) {

                    try {
                        cli.getSerial().closePort();
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }

                    bConnect.setText("Connect");
                    cliConnected = false;

                } else {

                    //cliConsole.setText("Cli Started\n\n");
                    bConnect.setText("Disconnect");
                    cli = new serialHandler(cliConsole,(String)serialCombo.getValue(),(String)baudRateCombo.getValue());
                    cliConsole.textProperty().bind(cli.readString);
                    cliConnected = true;
                }



            }
        });

    }
}
