package airpygs;

import airpygs.aplink.*;
import airpygs.aplink.messages.AplGyroCalibration;
import airpygs.graphics.Xform;
import airpygs.utils.ApConfigManager;
import airpygs.utils.FileTypesFilter;
import airpygs.utils.TxSettingsFloat;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
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

public class Controller implements Initializable {

    //Load config.json
    ApConfigManager config = ApConfigManager.getInstance();

    serialHandler cli;
    RxDecoder rxdec = null;
    TxEncoder txenc = null;
    ApBuffer buffer;
    boolean cliConnected = false;
    int isRxCalibrating = 0;
    boolean isImuCalibrating = false;
    boolean isGyroCalibrating = false;
    File airPyDestinationFolder = null;
    File airPySourceFolder = null;
    String[] serialPorts = null;
    Rotate rxBox = new Rotate(0, 0, 0, 0, Rotate.X_AXIS);
    Rotate ryBox = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
    Rotate rzBox = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
    private static final double AXIS_LENGTH = 250.0;
    final Xform axisGroup = new Xform();;
    final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
    final Box yAxis = new Box(1, AXIS_LENGTH, 1);
    final Box zAxis = new Box(1, 1, AXIS_LENGTH);
    PhongMaterial blueMaterial = new PhongMaterial(Color.BLUE);
    PhongMaterial redMaterial = new PhongMaterial(Color.RED);
    PhongMaterial greenMaterial = new PhongMaterial(Color.GREEN);
    PhongMaterial grayMaterial = new PhongMaterial(Color.GRAY);
    boolean toggleFlag = false;
    XYChart.Series pitchSeries;
    XYChart.Series rollSeries;
    XYChart.Series pitchVelSeries;
    XYChart.Series rollVelSeries;
    XYChart.Series motor1Series;
    XYChart.Series motor2Series;
    XYChart.Series motor3Series;
    XYChart.Series motor4Series;
    int xAxisDefault = 50;
    int xAxisSamplesCount = 0;

    //Rc Calibration Specific
    ProgressBar[] pbChGroup = new ProgressBar[5];
    TxSettingsFloat thresholds = new TxSettingsFloat(config.getTxChannelsNumber());
    SimpleStringProperty[] sMinVals = new SimpleStringProperty[config.getTxChannelsNumber()];
    SimpleStringProperty[] sMaxVals = new SimpleStringProperty[config.getTxChannelsNumber()];
    SimpleStringProperty[] sCenterVals = new SimpleStringProperty[config.getTxChannelsNumber()];

    @FXML
    private LineChart chartAttitude, chartMotors;

    @FXML
    private Sphere ledIndicator;

    @FXML
    private Button buttonEscCalibration, buttonImu, buttonGyro, buttonCalibration, bConnect, bUpdate, buttonSavePIDs, buttonLoadPIDs;

    @FXML
    private Label lbCh1Min, lbCh2Min, lbCh3Min, lbCh4Min, lbCh5Min;

    @FXML
    private Label lbCh1Max, lbCh2Max, lbCh3Max, lbCh4Max, lbCh5Max;

    @FXML
    private Label lbCh1Center, lbCh2Center, lbCh3Center, lbCh4Center, lbCh5Center;

    @FXML
    private Box imuBox;

    @FXML
    private Label labelPitch, labelRoll, labelYaw, labelGpitch, labelGroll, labelGyaw;

    @FXML
    private TabPane apTabPane;

    @FXML
    private Tab imuTab;

    @FXML
    private ProgressBar pbCh1, pbCh2, pbCh3, pbCh4, pbCh5;

    @FXML
    private TextArea cliConsole;

    @FXML
    private TextField txtKp, txtKd, txtKi, txtMaxIncrement, txtGyroKp, txtGyroKd, txtGyroKi, txtGyroMaxIncrement;

    @FXML
    private ChoiceBox serialCombo, baudRateCombo;

    @FXML
    private CheckBox checkBoxPitch, checkBoxPitchVel, checkBoxRoll, checkBoxRollVel;

    @FXML
    private CheckBox checkBoxMotor1, checkBoxMotor2, checkBoxMotor3, checkBoxMotor4;

    @FXML
    private void handleButtonSavePIDs(final  ActionEvent event) {
        float[] pids = {Float.parseFloat(txtKp.getText()),
                        Float.parseFloat(txtKd.getText()),
                        Float.parseFloat(txtKi.getText()),
                        Float.parseFloat(txtMaxIncrement.getText()),
                        Float.parseFloat(txtGyroKp.getText()),
                        Float.parseFloat(txtGyroKd.getText()),
                        Float.parseFloat(txtGyroKi.getText()),
                        Float.parseFloat(txtGyroMaxIncrement.getText())
        };

        //Send the infos through ApLink
        txenc.savePidSettings(pids);
    }

    @FXML
    private void handleButtonLoadPIDs(final  ActionEvent event) {
        if (cliConnected & isRxCalibrating == 0) {
            txenc.getCurrentPIDs();
        }
    }

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

    @FXML
    private void handleCalibrationButtonAction(final ActionEvent event) {
        if (cliConnected & isRxCalibrating == 0) {
            txenc.enableMessage(ApLinkParams.AP_MESSAGE_RC_INFO);
            buttonCalibration.setText("Done");
            isRxCalibrating = 1;

            Alert txCalibAlert = new Alert(Alert.AlertType.INFORMATION);
            txCalibAlert.setTitle("Tx Calibration - Step 1 of 2");
            txCalibAlert.setHeaderText(null);
            txCalibAlert.setContentText("Move all the stick in the max and min position. \n\n" +
                    "Press Done when finished");
            txCalibAlert.showAndWait();

        } else if(cliConnected & isRxCalibrating == 1) {
            buttonCalibration.setText("Done");
            isRxCalibrating = 2;

            //Show next step dialog box
            Alert txCalibAlert = new Alert(Alert.AlertType.INFORMATION);
            txCalibAlert.setTitle("Tx Calibration - Step 2 of 2");
            txCalibAlert.setHeaderText(null);
            txCalibAlert.setContentText("Leave all the stick in the center position and the throttle to 0. \n\n" +
                    "Press Done when finished");
            txCalibAlert.showAndWait();

        } else if(cliConnected & isRxCalibrating == 2) {
            txenc.disableMessage(ApLinkParams.AP_MESSAGE_RC_INFO);
            isRxCalibrating = 0;

            Alert txCalibAlert = new Alert(Alert.AlertType.INFORMATION);
            txCalibAlert.setTitle("Tx Calibration Completed");
            txCalibAlert.setHeaderText(null);
            txCalibAlert.setContentText("Tx Calibration Completed. \n\n" +
                    "MIN/MAX/CENTER values sent to airPy");
            txCalibAlert.showAndWait();

            buttonCalibration.setText("Start Tx Calibration");

            // Send TxSettings to airPy
            txenc.saveTxSettings(thresholds);
        }

    }

    @FXML
    private void handleImuButtonAction(final ActionEvent event) {

        if (cliConnected & !isImuCalibrating) {
            txenc.enableMessage(ApLinkParams.AP_MESSAGE_IMU_STATUS);
            buttonImu.setText("Stop IMU Streaming");
            isImuCalibrating = true;
        } else if(cliConnected & isImuCalibrating) {
            txenc.disableMessage(ApLinkParams.AP_MESSAGE_IMU_STATUS);
            buttonImu.setText("Start IMU Streaming");
            isImuCalibrating = false;
        }

    }

    @FXML
    private void handleGyroButtonAction(final ActionEvent event) {
        if (cliConnected & !isGyroCalibrating) {
            txenc.gyroCalibration(AplGyroCalibration.START_CALIBRATION);
            buttonGyro.setText("Stop Gyro Calibration");
            isGyroCalibrating = true;
        } else if(cliConnected & isGyroCalibrating) {
            txenc.gyroCalibration(AplGyroCalibration.STOP_CALIBRATION);
            buttonGyro.setText("Start Gyro Calibration");
            isGyroCalibrating = false;
        }
    }

    @FXML
    private void handleEnableEscCalibrationAction(final ActionEvent event) {
        if (cliConnected & isRxCalibrating == 0) {
            txenc.enableEscCalibration();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Esc Calibration Mode");
        alert.setHeaderText(null);
        alert.setContentText("ESC Calibration mode Enabled! \n\n" +
                "after power cycling the airPy controller the throttle channel will be set in pass-through mode in " +
                "order to perform the calibration.\n" +
                "After the calibration is completed, a second power cycle will disable the Esc calibration mode");

        alert.showAndWait();

    }

    @FXML
    private void handleCheckBoxPitch(final ActionEvent event) {

    }

    @FXML
    private void handleCheckBoxRoll(final ActionEvent event) {

    }

    @FXML
    private void handleCheckBoxMotor1(final ActionEvent event) {

    }

    @FXML
    private void handleCheckBoxMotor2(final ActionEvent event) {

    }

    @FXML
    private void handleCheckBoxMotor3(final ActionEvent event) {

    }

    @FXML
    private void handleCheckBoxMotor4(final ActionEvent event) {

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

    public Label getLabelGPitch(){

        return labelGpitch;
    }

    public Label getLabelGRoll(){

        return labelGroll;
    }

    public Label getLabelGYaw(){

        return labelGyaw;
    }

    public void updatePIDTextBox(float[] pids){

        txtKp.setText(Float.toString(pids[0]));
        txtKd.setText(Float.toString(pids[1]));
        txtKi.setText(Float.toString(pids[2]));
        txtMaxIncrement.setText(Float.toString(pids[3]));
        txtGyroKp.setText(Float.toString(pids[4]));
        txtGyroKd.setText(Float.toString(pids[5]));
        txtGyroKi.setText(Float.toString(pids[6]));
        txtGyroMaxIncrement.setText(Float.toString(pids[7]));
    }

    public void setConnectLed(ConnectLed led) {

        switch (led) {

            case ON:        ledIndicator.setMaterial(greenMaterial);
                            break;

            case OFF:       ledIndicator.setMaterial(grayMaterial);
                            break;

            case TOGGLE:    if (toggleFlag) {
                ledIndicator.setMaterial(greenMaterial);
                toggleFlag = false;
            } else {
                ledIndicator.setMaterial(grayMaterial);
                toggleFlag = true;
            }
                break;
        }
    }

    private void disconnect() {
        try {
            rxdec.stopRxDecoder();
            txenc = null;
            cli.getSerial().closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
        cliConnected = false;
        ledIndicator.setMaterial(grayMaterial);
        updateButtons();

        //Disable buttons:
        buttonCalibration.setDisable(true);
        buttonImu.setDisable(true);
        buttonEscCalibration.setDisable(true);
        buttonLoadPIDs.setDisable(true);
        buttonSavePIDs.setDisable(true);
    }

    private void connect() {
        //cliConsole.setText("Cli Started\n\n");
        buffer = new ApBuffer();
        cli = new serialHandler(cliConsole,(String)serialCombo.getValue(),(String)baudRateCombo.getValue(),buffer);
        cliConsole.textProperty().bind(cli.readString);
        cliConnected = true;

        //Initialize Rx Chain
        rxdec = new RxDecoder(buffer,this);
        rxdec.start();
        rxdec.startRxDecoder();

        //Initialize Tx Chain
        txenc = new TxEncoder(cli);

        ledIndicator.setMaterial(greenMaterial);
        updateButtons();

        //Enable buttons:
        buttonCalibration.setDisable(false);
        buttonImu.setDisable(false);
        buttonEscCalibration.setDisable(false);
        buttonLoadPIDs.setDisable(false);
        buttonSavePIDs.setDisable(false);
    }

    public  void updateConsole(String message) {

        cliConsole.appendText(message);

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

                    for (int i = 0; i < thresholds.NUM_CHANNELS; i++) {

                        pbChGroup[i].progressProperty().set(channels[i] / ApLinkParams.MAX_RC_VALUE);

                        if (channels[i] < thresholds.getMinThreshold(i)) {
                            thresholds.setMinThreshold(i, channels[i]);
                        }
                        if (channels[i] > thresholds.getMaxThreshold(i)) {
                            thresholds.setMaxThreshold(i, channels[i]);
                        }

                        thresholds.setCenterThreshold(i, channels[i]);
                        sMaxVals[i].set(String.valueOf(thresholds.getMaxThreshold(i)));
                        sMinVals[i].set(String.valueOf(thresholds.getMinThreshold(i)));
                        sCenterVals[i].set(String.valueOf(thresholds.getCenterThreshold(i)));
                    }
    }


    public void updateModelRotations(float[] angles) {
        //Update the rotation of the 3D object
        rxBox.setAngle(angles[0]);
        ryBox.setAngle(angles[2]);
        rzBox.setAngle(angles[1]);
    }

    public void updateAttitudeChart(float[] angles) {

        if (xAxisSamplesCount > 100) {

            NumberAxis xAxis = (NumberAxis) chartAttitude.getXAxis();
            xAxis.setForceZeroInRange(false);
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xAxisSamplesCount - 100);
            xAxis.setUpperBound(xAxisSamplesCount);

        }

        if (checkBoxPitch.isSelected()) {
            pitchSeries.getData().add(new XYChart.Data(xAxisSamplesCount, angles[0]));
        }

        if (checkBoxRoll.isSelected()) {
            rollSeries.getData().add(new XYChart.Data(xAxisSamplesCount, angles[1]));
        }

        if (checkBoxPitchVel.isSelected()) {
            pitchVelSeries.getData().add(new XYChart.Data(xAxisSamplesCount, angles[3]));
        }

        if (checkBoxRollVel.isSelected()) {
            rollVelSeries.getData().add(new XYChart.Data(xAxisSamplesCount, angles[4]));
        }

    }

    public void updateMotorChart(short[] motors) {
        if (xAxisSamplesCount >= 100) {


            NumberAxis xAxis = (NumberAxis) chartMotors.getXAxis();
            xAxis.setForceZeroInRange(false);
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(xAxisSamplesCount - 100);
            xAxis.setUpperBound(xAxisSamplesCount);

        }

        if (checkBoxMotor1.isSelected()) {
            motor1Series.getData().add(new XYChart.Data(xAxisSamplesCount, motors[0]));

        }
        if (checkBoxMotor2.isSelected()) {
            motor2Series.getData().add(new XYChart.Data(xAxisSamplesCount, motors[1]));
        }
        if (checkBoxMotor3.isSelected()) {
            motor3Series.getData().add(new XYChart.Data(xAxisSamplesCount, motors[2]));
        }
        if (checkBoxMotor4.isSelected()) {
            motor4Series.getData().add(new XYChart.Data(xAxisSamplesCount, motors[3]));
        }
        //TODO: Move in a static class variable related to the chart
        xAxisSamplesCount++;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assert cliConsole != null : "fx:id=\"cliConsole\" was not injected: check your FMXL";
        assert bConnect != null : "fx:id=\"bConnect\" was not injected: check your FMXL";
        assert bUpdate != null : "fx:id=\"bUpdate\" was not injected: check your FMXL";
        assert buttonCalibration != null: "fx:id=\"buttonCalibration\" was not injected: check your FMXL";
        assert serialCombo != null : "fx:id=\"serialCombo\" was not injected: check your FMXL";
        assert pbCh1 != null : "fx:id=\"pbCh1\" was not injected: check your FMXL";

        //TODO: move the allowed baudrate in a property file
        ObservableList baudRates = FXCollections.observableArrayList("9600","14400","38400","57600","115200");
        baudRateCombo.setItems(baudRates);
        baudRateCombo.getSelectionModel().select(baudRates.size()-1);
        updateComPortList();
        updateButtons();

        for (int i = 0; i < config.getTxChannelsNumber(); i++) {
            sMinVals[i] = new SimpleStringProperty("");
            sCenterVals[i] = new SimpleStringProperty("");
            sMaxVals[i] = new SimpleStringProperty("");
        }

        //Init Progress Bars
        pbChGroup[0] = pbCh1;
        pbChGroup[1] = pbCh2;
        pbChGroup[2] = pbCh3;
        pbChGroup[3] = pbCh4;
        pbChGroup[4] = pbCh5;

        //Init Axes
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);


        //Initialization of 3D cube TODO: loading of custom 3d model
        blueMaterial.setSpecularColor(Color.LIGHTBLUE);
        imuBox.setMaterial(blueMaterial);
        imuBox.getTransforms().addAll(rxBox, ryBox, rzBox);


        //Initialization of 3D sphere
        redMaterial.setSpecularColor(Color.LIGHTCORAL);
        ledIndicator.setMaterial(grayMaterial);
        greenMaterial.setSpecularColor(Color.LIGHTGREEN);
        grayMaterial.setSpecularColor(Color.LIGHTGRAY);

        //Initialize RC labels
        lbCh1Min.textProperty().bind(sMinVals[0]);
        lbCh2Min.textProperty().bind(sMinVals[1]);
        lbCh3Min.textProperty().bind(sMinVals[2]);
        lbCh4Min.textProperty().bind(sMinVals[3]);
        lbCh5Min.textProperty().bind(sMinVals[4]);
        lbCh1Center.textProperty().bind(sCenterVals[0]);
        lbCh2Center.textProperty().bind(sCenterVals[1]);
        lbCh3Center.textProperty().bind(sCenterVals[2]);
        lbCh4Center.textProperty().bind(sCenterVals[3]);
        lbCh5Center.textProperty().bind(sCenterVals[4]);
        lbCh1Max.textProperty().bind(sMaxVals[0]);
        lbCh2Max.textProperty().bind(sMaxVals[1]);
        lbCh3Max.textProperty().bind(sMaxVals[2]);
        lbCh4Max.textProperty().bind(sMaxVals[3]);
        lbCh5Max.textProperty().bind(sMaxVals[4]);

        //Initialize Charts
        pitchSeries = new XYChart.Series();
        pitchSeries.setName("Pitch angle");
        rollSeries = new XYChart.Series();
        rollSeries.setName("Roll angle");
        pitchVelSeries = new XYChart.Series();
        pitchVelSeries.setName("Pitch Vel");
        rollVelSeries = new XYChart.Series();
        rollVelSeries.setName("Roll Vel");
        motor1Series = new XYChart.Series();
        motor1Series.setName("Motor 1");
        motor2Series = new XYChart.Series();
        motor2Series.setName("Motor 2");
        motor3Series = new XYChart.Series();
        motor3Series.setName("Motor 3");
        motor4Series = new XYChart.Series();
        motor4Series.setName("Motor 4");

        chartAttitude.getData().add(pitchSeries);
        chartAttitude.getData().add(rollSeries);
        chartAttitude.getData().add(pitchVelSeries);
        chartAttitude.getData().add(rollVelSeries);
        chartMotors.getData().add(motor1Series);
        chartMotors.getData().add(motor2Series);
        chartMotors.getData().add(motor3Series);
        chartMotors.getData().add(motor4Series);
        chartAttitude.setAnimated(true);
        chartMotors.setAnimated(true);

        checkBoxPitch.setSelected(true);
    }
}
