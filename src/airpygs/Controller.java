package airpygs;

import airpygs.aplink.ConnectLed;
import airpygs.aplink.RxBuffer;
import airpygs.aplink.RxDecoder;
import airpygs.aplink.serialHandler;
import javafx.collections.ObservableList;
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

import org.apache.commons.io.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

class FileTypesFilter implements FileFilter {

    String[] types;

    FileTypesFilter(String[] types) {
        this.types = types;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            if (f.getName().startsWith("."))
                return false;
            else
                return true;
        }
        for (String type : types) {
            if (f.getName().endsWith(type)) return true;
        }
        return false;
    }
}

public class Controller implements Initializable {

    serialHandler cli;
    RxDecoder rxdec = null;
    RxBuffer buffer;
    boolean cliConnected = false;
    File airPyDestinationFolder = null;
    File airPySourceFolder = null;
    String[] serialPorts = null;
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
    private Button bUpdate;
    @FXML
    private ChoiceBox serialCombo;

    @FXML
    private ChoiceBox baudRateCombo;

    @FXML
    private void handleSetSourceAction(final ActionEvent event)
    {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(airPyDestinationFolder);
        airPySourceFolder = folderChooser.showDialog(null);
        updateButtons();
    }

    @FXML
    private void handleSetDestinationAction(final ActionEvent event)
    {
        DirectoryChooser folderChooser = new DirectoryChooser();
        folderChooser.setInitialDirectory(airPyDestinationFolder);
        airPyDestinationFolder = folderChooser.showDialog(null);
        updateButtons();
    }

    @FXML
    private void handleConnectButtonAction(final ActionEvent event)
    {
        if (!cliConnected) {
            connect();
        } else {
            disconnect();
        }
    }

    @FXML
    private void handleUpdateButtonAction(final ActionEvent event)
    {
        try {
            String[] types = {"py"};
            FileFilter filter = new FileTypesFilter(types);
            FileUtils.copyDirectory(airPySourceFolder, airPyDestinationFolder, filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cliConsole.setText("Code updated\n\n");
    }

    @FXML
    private void handleRefreshButtonAction(final ActionEvent event)
    {
        updateComPortList();
        updateButtons();
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


    private void disconnect() {
        try {
            rxdec.stopRxDecoder();
            cli.getSerial().closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        cliConnected = false;
        connectLed.setImage(connectLedImageOff);
        updateButtons();
    }

    private void connect() {
        //cliConsole.setText("Cli Started\n\n");
        buffer = new RxBuffer();
        cli = new serialHandler(cliConsole,(String)serialCombo.getValue(),(String)baudRateCombo.getValue(),buffer);
        cliConsole.textProperty().bind(cli.readString);
        cliConnected = true;

        rxdec = new RxDecoder(buffer,this);
        rxdec.start();
        rxdec.startRxDecoder();

        connectLed.setImage(connectLedImageOn);
        updateButtons();
    }

    private void updateButtons() {
        if (airPySourceFolder != null && airPyDestinationFolder != null)
            bUpdate.setDisable(false);
        else
            bUpdate.setDisable(true);
        if (serialPorts != null && serialPorts.length > 0) {
            bConnect.setDisable(false);
            if (cliConnected)
                bConnect.setText("Disconnect");
            else
                bConnect.setText("connect");
        }
        else
            bConnect.setDisable(true);
    }

    private void updateComPortList() {
        serialPorts = SerialPortList.getPortNames();
        serialCombo.setItems(FXCollections.observableArrayList(serialPorts));
        serialCombo.getSelectionModel().select(0);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert bUpdate != null : "fx:id=\"bUpdate\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";
        assert connectLed != null : "fx:id=\"connectLed\" was not injected: check your FMXL";

        ObservableList baudRates = FXCollections.observableArrayList("9600","14400","38400","57600","115200");
        baudRateCombo.setItems(baudRates);
        baudRateCombo.getSelectionModel().select(baudRates.size()-1);

        updateComPortList();
        updateButtons();
        connectLed.setImage(connectLedImageOff);
    }
}
