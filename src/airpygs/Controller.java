package airpygs;

import airpygs.aplink.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
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
    File logoBigFile = new File("./resources/img/airPyLogo_big.png");
    Image logoBigImage = new Image(logoBigFile.toURI().toString());
    Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
    Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
    Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
    PhongMaterial blueMaterial = new PhongMaterial(Color.BLUE);


    boolean toggleFlag = false;

    @FXML
    private Box imuBox;

    @FXML
    private Label labelPitch;

    @FXML
    private Label labelRoll;

    @FXML
    private Label labelYaw;

    @FXML
    private TabPane apTabPane;

    @FXML
    private Tab imuTab;

    @FXML
    private ProgressBar pbCh1;

    @FXML
    private ProgressBar pbCh2;

    @FXML
    private ProgressBar pbCh3;

    @FXML
    private ProgressBar pbCh4;

    @FXML
    private ImageView connectLed;

    @FXML
    private ImageView imgLogoBig;

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

    public Label getLabelPitch(){

        return labelPitch;
    }
    public Label getLabelRoll(){

        return labelRoll;
    }
    public Label getLabelYaw(){
        return labelYaw;
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
                bConnect.setText("Connect");
        }
        else
            bConnect.setDisable(true);
    }

    private void updateComPortList() {
        serialPorts = SerialPortList.getPortNames();
        serialCombo.setItems(FXCollections.observableArrayList(serialPorts));
        serialCombo.getSelectionModel().select(0);
    }

    public void updateRcBars(int[] channels) {

       // if (apTabPane.getSelectionModel().getSelectedItem().getId() == "rcSetupTab") {

            for (int i = 0; i < channels.length; i++) {
                switch (i) {
                    case 0: pbCh1.progressProperty().set(channels[i] / ApLinkParams.MAX_RC_VALUE);
                            break;

                    case 1: pbCh2.progressProperty().set(channels[i] / ApLinkParams.MAX_RC_VALUE);
                            break;

                    case 2: pbCh3.progressProperty().set(channels[i] / ApLinkParams.MAX_RC_VALUE);
                            break;

                    case 3: pbCh4.progressProperty().set(channels[i] / ApLinkParams.MAX_RC_VALUE);
                            break;
                }
            }
        //}

    }


    public void updateModelRotations(float[] angles) {

        //Update the rotation of the 3D object
        rxBox.setAngle(angles[0]);
        ryBox.setAngle(angles[2]);
        rzBox.setAngle(angles[1]);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert bUpdate != null : "fx:id=\"bUpdate\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";
        assert connectLed != null : "fx:id=\"connectLed\" was not injected: check your FMXL";
        assert imgLogoBig != null : "fx:id=\"imgLogoBig\" was not injected: check your FMXL";
        assert pbCh1 != null : "fx:id=\"pbCh1\" was not injected: check your FMXL";


        //TODO: move the allowed baudrate in a property file
        ObservableList baudRates = FXCollections.observableArrayList("9600","14400","38400","57600","115200");
        baudRateCombo.setItems(baudRates);
        baudRateCombo.getSelectionModel().select(baudRates.size()-1);
        updateComPortList();
        updateButtons();

        connectLed.setImage(connectLedImageOff);
        imgLogoBig.setImage(logoBigImage);

        //Initialization of 3D cube TODO: loading of custom 3d model
        blueMaterial.setSpecularColor(Color.LIGHTBLUE);
        imuBox.setMaterial(blueMaterial);
        imuBox.getTransforms().addAll(rxBox, ryBox, rzBox);

    }
}
