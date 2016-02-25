package airpygs;

import airpygs.aplink.ConnectLed;
import airpygs.aplink.RxBuffer;
import airpygs.aplink.RxDecoder;
import airpygs.aplink.serialHandler;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

public class Controller implements Initializable {

    serialHandler cli;
    RxDecoder rxdec = null;
    RxBuffer buffer;
    boolean cliConnected = false;
    File airPyDestinationFolder = null;
    File connectLedFileOn = new File("./resources/img/switch_on.png");
    Image connectLedImageOn = new Image(connectLedFileOn.toURI().toString());
    File connectLedFileOff = new File("./resources/img/switch_off.png");
    Image connectLedImageOff = new Image(connectLedFileOff.toURI().toString());
    File connectLedFileHeartBeat = new File("./resources/img/switch_heartbeat.png");
    Image connectLedImageHeartBeat = new Image(connectLedFileHeartBeat.toURI().toString());
    boolean toggleFlag = false;

    @FXML
    private ImageView connectLed;

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

    public void setConnectLed(ConnectLed led) {

        switch (led) {

            case ON:        connectLed.setImage(connectLedImageOn);
                            break;

            case OFF:       connectLed.setImage(connectLedImageOff);
                            break;

            case TOGGLE:    if (toggleFlag) {
                                connectLed.setImage(connectLedImageOn);
                                toggleFlag = false;
                            } else {
                                connectLed.setImage(connectLedImageHeartBeat);
                                toggleFlag = true;
                            }
                            break;
        }
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
        connectLed.setImage(connectLedImageOff);
    }

    private void disconnect() {
        //cliConsole.setText("Cli Started\n\n");
        bConnect.setText("Disconnect");
        buffer = new RxBuffer();
        cli = new serialHandler(cliConsole,(String)serialCombo.getValue(),(String)baudRateCombo.getValue(),buffer);
        cliConsole.textProperty().bind(cli.readString);
        cliConnected = true;

        rxdec = new RxDecoder(buffer,this);
        rxdec.start();
        rxdec.startRxDecoder();

        connectLed.setImage(connectLedImageOn);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";
        assert connectLed != null : "fx:id=\"connectLed\" was not injected: check your FMXL";

        baudRateCombo.setItems(FXCollections.observableArrayList("9600","14400","38400","57600","115200"));
        serialCombo.setItems(FXCollections.observableArrayList(SerialPortList.getPortNames()));
    }
}
