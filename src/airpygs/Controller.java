package airpygs;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    serialHandler cli;
    RxDecoder rxdec = null;
    RxBuffer buffer;
    boolean cliConnected = false;
    File airPyDestinationFolder = null;

    @FXML
    private TextArea cliConsole;

    @FXML
    private Button bConnect;

    @FXML
    private ChoiceBox serialCombo;

    @FXML
    private ChoiceBox baudRateCombo;

    @FXML
    private void handleSetSourceAction(final ActionEvent event)
    {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(airPyDestinationFolder);
        airPyDestinationFolder = folderChooser.showDialog(null);
    }

    @FXML
    private void handleConnectButtonAction(final ActionEvent event)
    {
        if (cliConnected) {
            connect();
        } else {
            disconnect();
        }
    }

    @FXML
    private void handleUpdateButtonAction(final ActionEvent event)
    {
        //TODO: Copy from src to dst
        cliConsole.setText("Code updated\n\n");
    }

    private void connect() {
        try {
            rxdec.stopRxDecoder();
            cli.getSerial().closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        bConnect.setText("Connect");
        cliConnected = false;
    }

    private void disconnect() {
        //cliConsole.setText("Cli Started\n\n");
        bConnect.setText("Disconnect");
        buffer = new RxBuffer();
        cli = new serialHandler(cliConsole,(String)serialCombo.getValue(),(String)baudRateCombo.getValue(),buffer);
        cliConsole.textProperty().bind(cli.readString);
        cliConnected = true;

        if (rxdec == null){
            rxdec = new RxDecoder(cli,buffer);
            rxdec.start();
            rxdec.startRxDecoder();
        } else {
            rxdec.startRxDecoder();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";

        baudRateCombo.setItems(FXCollections.observableArrayList("9600","14400","38400","57600","115200"));
        serialCombo.setItems(FXCollections.observableArrayList(SerialPortList.getPortNames()));
    }
}
