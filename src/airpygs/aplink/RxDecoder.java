package airpygs.aplink;

import airpygs.Controller;
import airpygs.aplink.messages.AplMessage;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Properties;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxDecoder extends Thread {

    //ApLink config
    Properties aplinkConfig;
    Controller apGui;

    private final long READING_INTERVAL;
    private boolean active;
    private boolean startByteFound;
    private int byteIndex;
    private int rcChannelIndex;
    private ApBuffer buffer;
    private byte tmpByte;

    //Message Specific
    private int tmpEOF;
    private AplMessage message;
    private int[] rcInfoMessage;
    private float[] rotations;
    public StringProperty pitchString;
    public StringProperty rollString;
    public StringProperty yawString;

    //Counters
    private long validApLinkMessages;
    private long lostApLinkMessages;

    public RxDecoder(ApBuffer b, Controller gui) {

        aplinkConfig = aplinkConfigManager.getInstance().getConfig();
        message = new AplMessage();
        apGui = gui;
        READING_INTERVAL = Long.decode(aplinkConfig.getProperty("rxBufferReadingTimeInterval"));
        buffer = b;
        startByteFound = false;
        byteIndex = 0;
        rcChannelIndex = 0;
        validApLinkMessages = 0;
        lostApLinkMessages = 0;
        rcInfoMessage = new int[ApLinkParams.AP_MESSAGE_RC_INFO_NUM_CHANNELS];
        rotations = new float[3];
        pitchString = new SimpleStringProperty("");
        rollString = new SimpleStringProperty("");
        yawString = new SimpleStringProperty("");
        apGui.getLabelPitch().textProperty().bind(pitchString);
        apGui.getLabelRoll().textProperty().bind(rollString);
        apGui.getLabelYaw().textProperty().bind(yawString);
    }

    public void startRxDecoder(){
        active = true;
    }

    public void stopRxDecoder(){
        active = false;
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private void parseHeader() {
        switch (byteIndex) {

            case ApLinkParams.MESSAGE_ID_BYTE_1:            message.setMessageID((unsignedByteToInt(tmpByte) << 8));
                                                            break;

            case ApLinkParams.MESSAGE_ID_BYTE_2:            message.setMessageID(message.getMessageID() + unsignedByteToInt(tmpByte));
                                                            break;

            case ApLinkParams.QCI_AND_LAST_FRAGMENT_FLAG:   message.setQCI((unsignedByteToInt(tmpByte) & 0xF8)  >> 3);
                                                            message.setLastFragment((int)(tmpByte) & 0x07);
                                                            break;

            case ApLinkParams.MESSAGE_TYPE_ID:              message.setMessageTypeID((int) tmpByte);
                                                            break;

            case ApLinkParams.FAIL_SAFE_AND_FLIGHT_MODE:    message.setFailSafe(((int) (tmpByte) & 0xF0) >> 4);
                                                            message.setFlightMode((int)(tmpByte) & 0x0F);
                                                            break;

            case ApLinkParams.PAYLOAD_LENGTH:               message.setPayloadLength((int)tmpByte);
                                                            break;
        }

        byteIndex++;
    }

    private void loadPayload() {
        if (byteIndex == ApLinkParams.PAYLOAD_1ST_BYTE) {

            //The 1st byte of the payload is used to mark the end of the frame
            tmpEOF = tmpByte;
            message.setPayloadByte(byteIndex-ApLinkParams.HEADER_LENGTH,tmpByte);
            byteIndex++;

        } else if ((byteIndex == ApLinkParams.HEADER_LENGTH + message.getPayloadLength())) {

            if (tmpByte == tmpEOF) {
                //The whole message is valid. Update GUI
                validApLinkMessages++;
                System.out.println("Message Decoded -> MessageTypeID:" + message.getMessageTypeID() + " - QCI:" + message.getQCI() + " LastFragment:" + message.getLastFragment() + " PayloadLength: " + message.getPayloadLength());

                // Decode the message
                decodeApLinkMessage();

            } else {
                lostApLinkMessages++;
            }
            byteIndex = 0;
            startByteFound = false;
        } else {
            // keep loading payload bytes
            message.setPayloadByte(byteIndex-ApLinkParams.HEADER_LENGTH,tmpByte);
            byteIndex++;
        }

    }

    private void decodeApLinkMessage(){
        switch (message.getMessageTypeID()) {

            case ApLinkParams.AP_MESSAGE_HEARTBEAT:   decodeHeartBeat();
                                                         break;

            case ApLinkParams.AP_MESSAGE_RC_INFO:     decodeRcInfo();
                                                        break;

            case ApLinkParams.AP_MESSAGE_IMU_STATUS:  decodeImuStatus();
                                                        break;
        }
    }

    public void run(){

            while (active) {
                try {
                    //Read a new byte from the Rx buffer
                    tmpByte = buffer.readRxBuffer();

                    if (startByteFound) {
                        if (byteIndex < ApLinkParams.HEADER_LENGTH) {
                            parseHeader();
                        } else {
                            loadPayload();
                        }

                    } else {
                        if ((short) tmpByte == ApLinkParams.START_BYTE) {
                            startByteFound = true;
                            byteIndex++;
                        }
                    }

                    sleep(READING_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
    
    private void decodeHeartBeat(){
        apGui.setConnectLed(ConnectLed.TOGGLE);
    }

    //Extract Pitch, Roll, Yaw angle from the payload
    private void decodeImuStatus(){

        for (int i = 0; i < 3; i++) {
            byte[] bytes = {(message.getPayload())[i * 4],
                    (message.getPayload())[i * 4 + 1],
                    (message.getPayload())[i * 4 + 2],
                    (message.getPayload())[i * 4 + 3]
            };

            rotations[i] = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }

        //Update rotation gui value
        Platform.runLater(() -> {
            //update labels
            pitchString.set(String.valueOf(rotations[0]));
            rollString.set(String.valueOf(rotations[1]));
            yawString.set(String.valueOf(rotations[2]));

            //update 3D model rotation
            apGui.updateModelRotations(rotations);
        });

        //System.out.println("Pitch: " + rotations[0] + " - Roll: " + rotations[1] + " - Yaw: " + rotations[2]);
    }

    private void decodeRcInfo(){

        for (int i = ApLinkParams.PAYLOAD_1ST_BYTE; i < ApLinkParams.HEADER_LENGTH + message.getPayloadLength(); i++) {

            // Each channel is encoded with 2 bytes.
            if ((i - ApLinkParams.HEADER_LENGTH-1) % 2 == 0) {
                rcInfoMessage[rcChannelIndex] = rcInfoMessage[rcChannelIndex] + (unsignedByteToInt(tmpByte)<<8);
                rcChannelIndex++;
            } else {
                rcInfoMessage[rcChannelIndex] = (unsignedByteToInt(tmpByte));
            }
        }
        //System.out.println( "CH1: " + rcInfoMessage[0] + " - CH2: " + rcInfoMessage[1] + " - CH3: " + rcInfoMessage[2] + " - CH4: " + rcInfoMessage[3]);
        apGui.updateRcBars(rcInfoMessage);
        rcChannelIndex = 0;

        /*
        if (byteIndex == ApLinkParams.PAYLOAD_1ST_BYTE) {
            tmpEOF = tmpByte;
            rcInfoMessage[rcChannelIndex] = (unsignedByteToInt(tmpByte));
            byteIndex++;
        } else if ((byteIndex == ApLinkParams.HEADER_LENGTH + message.getPayloadLength())) {

            if (tmpByte == tmpEOF) {
                //The whole message is valid. Update GUI progress bar value
                validApLinkMessages++;
                apGui.updateRcBars(rcInfoMessage);
                rcChannelIndex = 0;
                System.out.println("RcInfo Decoded -> MessageID:" + message.getMessageID() + " - QCI:" + message.getQCI() + " LastFragment:" + message.getLastFragment() + " CH0: " + rcInfoMessage[0]);
            } else {
                lostApLinkMessages++;
            }
            byteIndex = 0;
            startByteFound = false;
        } else {
            // Each channel is encoded with 2 bytes.
            if ((byteIndex - ApLinkParams.HEADER_LENGTH-1) % 2 == 0) {
                rcInfoMessage[rcChannelIndex] = rcInfoMessage[rcChannelIndex] + (unsignedByteToInt(tmpByte)<<8);
                rcChannelIndex++;
            } else {
                rcInfoMessage[rcChannelIndex] = (unsignedByteToInt(tmpByte));
            }
            byteIndex++;
        }*/
    }
}
