package airpygs.aplink;

import airpygs.Controller;

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
    private RxBuffer buffer;
    private byte tmpByte;

    //Message Specific
    private int tmpEOF;
    private AplMessage message;
    
    //Counters
    private long validApLinkMessages;
    private long lostApLinkMessages;

    public RxDecoder(RxBuffer b, Controller gui) {

        aplinkConfig = aplinkConfigManager.getInstance().getConfig();
        message = new AplMessage();
        apGui = gui;
        READING_INTERVAL = Long.decode(aplinkConfig.getProperty("rxBufferReadingTimeInterval"));
        buffer = b;
        startByteFound = false;
        byteIndex = 0;
        validApLinkMessages = 0;
        lostApLinkMessages = 0;
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
    
    private void decodeApLinkMessage(){
        switch (message.getMessageTypeID()) {

            case ApLinkParams.AP_MESSAGE_HEARTBEAT:   decodeHeartBeat();
                                                         break;

            case ApLinkParams.AP_MESSAGE_RC_INFO:     decodeRcInfo();
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
                            decodeApLinkMessage();
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
        if (byteIndex == 12) {
            tmpEOF = tmpByte;
            byteIndex++;
        } else if (byteIndex == 13) {

            if (tmpByte == tmpEOF) {
                validApLinkMessages++;
                apGui.setConnectLed(ConnectLed.TOGGLE);
                System.out.println("Heartbeat Decoded -> MessageID:" + message.getMessageID() + " - QCI:" + message.getQCI() + " LastFragment:" + message.getLastFragment() + " PayloadLength: " + message.getPayloadLength());
            } else {
                lostApLinkMessages++;
            }
            byteIndex = 0;
            startByteFound = false;
        }
    }

    private void decodeRcInfo(){
        if (byteIndex == 12) {
            tmpEOF = tmpByte;
            byteIndex++;
        } else if ((byteIndex == 11+message.getPayloadLength()+1)) {

            if (tmpByte == tmpEOF) {
                validApLinkMessages++;
                System.out.println("RcInfo Decoded -> MessageID:" + message.getMessageID() + " - QCI:" + message.getQCI() + " LastFragment:" + message.getLastFragment() + " PayloadLength: " + message.getPayloadLength());
            } else {
                lostApLinkMessages++;
            }
            byteIndex = 0;
            startByteFound = false;
        } else {
            byteIndex++;
        }
    }
}
