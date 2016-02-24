package airpygs.aplink;

import java.util.Properties;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxDecoder extends Thread {

    //ApLink config
    Properties aplinkConfig;

    private final long READING_INTERVAL;
    private boolean active;
    private boolean startByteFound;
    private int byteIndex;
    private RxBuffer buffer;
    private byte tmpByte;

    //Header fields
    private int tmpMsgID;
    private int tmpQCI;
    private int tmpLastFragment;
    private int tmpMsgTypeID;
    private int tmpFailSafe;
    private int tmpFlightMode;
    private int tmpPayloadLength;
    
    //Message Specific
    private int tmpEOF;
    
    //Counters
    private long validApLinkMessages;
    private long lostApLinkMessages;

    public RxDecoder(RxBuffer b) {

        aplinkConfig = AplinkConfigManager.getInstance().getConfig();
        READING_INTERVAL = Long.decode(aplinkConfig.getProperty("rxBufferReadingTimeInterval"));
        buffer = b;
        startByteFound = false;
        byteIndex = 0;
        tmpMsgID = 0;
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

            case ApLinkParams.MESSAGE_ID_BYTE_1:            tmpMsgID = (unsignedByteToInt(tmpByte) << 8);
                                                            break;

            case ApLinkParams.MESSAGE_ID_BYTE_2:            //tmpMsgID = (tmpMsgID + (int)tmpByte);
                                                            tmpMsgID = tmpMsgID + unsignedByteToInt(tmpByte);
                                                            break;

            case ApLinkParams.QCI_AND_LAST_FRAGMENT_FLAG:   tmpQCI = ((unsignedByteToInt(tmpByte) & 0xF8)  >> 3);
                                                            tmpLastFragment = ((int)(tmpByte) & 0x07);
                                                            break;

            case ApLinkParams.MESSAGE_TYPE_ID:              tmpMsgTypeID = (int) tmpByte;
                                                            break;

            case ApLinkParams.FAIL_SAFE_AND_FLIGHT_MODE:    tmpFailSafe = (((int) (tmpByte) & 0xF0) >> 4);
                                                            tmpFlightMode = ((int)(tmpByte) & 0x0F);
                                                            break;

            case ApLinkParams.PAYLOAD_LENGTH:               tmpPayloadLength = (int)tmpByte;
                                                            break;
        }

        byteIndex++;
    }
    
    private void decodeApLinkMessage(){
        switch (tmpMsgTypeID) {

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
                System.out.println("Heartbeat Decoded -> MessageID:" + tmpMsgID + " - QCI:" + tmpQCI + " LastFragment:" + tmpLastFragment + " PayloadLength: " + tmpPayloadLength);
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
        } else if ((byteIndex == 11+tmpPayloadLength+1)) {

            if (tmpByte == tmpEOF) {
                validApLinkMessages++;
                System.out.println("RCInfo Decoded -> MessageID:" + tmpMsgID + " - QCI:" + tmpQCI + " LastFragment:" + tmpLastFragment + " PayloadLength: " + tmpPayloadLength);
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
