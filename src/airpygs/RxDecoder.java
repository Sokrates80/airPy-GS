package airpygs;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxDecoder extends Thread {

    private final short START_BYTE = 15; // 0x0F
    private final short HEADER_LENGTH = 12; // bytes

    private long reading_interval = 2;  //ms
    private boolean active;
    private boolean startByteFound;
    private int byteIndex;
    private RxBuffer buffer;
    private byte tmpByte;

    //Header fields
    private short tmpMsgID;
    private short tmpQCI;
    private short tmpLastFragment;
    private short tmpMsgTypeID;
    private short tmpFailSafe;
    private short tmpFlightMode;
    private short tmpPayloadLength;
    
    //Message Specific
    private short tmpEOF;
    
    //Counters
    private long validApLinkMessages;
    private long lostApLinkMessages;

    public RxDecoder(RxBuffer b) {
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

    private void parseHeader() {
        switch (byteIndex) {

            case 1:     tmpMsgID = (short)((int)(tmpByte) << 8);
                        break;

            case 2:     tmpMsgID = (short) (tmpMsgID + (short)tmpByte);
                        break;

            case 3:     tmpQCI = (short)(((short)(tmpByte) & 0xF8)  >> 3);
                        tmpLastFragment = (short) ((short)(tmpByte) & 0x07);
                        break;

            case 4:     tmpMsgTypeID = (short)tmpByte;
                        break;

            case 10:    tmpFailSafe = (short)(((short)(tmpByte) & 0xF0) >> 4);
                        tmpFlightMode = (short) ((short)(tmpByte) & 0x0F);
                        break;
            case 11:    tmpPayloadLength = (short)tmpByte;
                        break;

        }

        byteIndex++;
    }
    
    private void decodeApLinkMessage(){
        switch (tmpMsgTypeID) {

            case 10:    decodeHeartBeat();
                        break;

            case 20:    decodeRcInfo();
                        break;
        }
    }

    public void run(){

            while (active) {
                try {
                    //Read a new byte from the Rx buffer
                    tmpByte = buffer.readRxBuffer();

                    if (startByteFound) {
                        if (byteIndex < HEADER_LENGTH) {
                            parseHeader();
                        } else {
                            decodeApLinkMessage();
                        }

                    } else {
                        if ((short) tmpByte == START_BYTE) {
                            startByteFound = true;
                            byteIndex++;
                        }
                    }

                    sleep(reading_interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
    
    private void decodeHeartBeat(){
        if (byteIndex == 12) {
            tmpEOF = tmpByte;
            byteIndex++;
        } else if (byteIndex == 13 && tmpByte == tmpEOF) {
            byteIndex = 0;
            startByteFound = false;
            validApLinkMessages++;
            System.out.println("Heartbeat Decoded");
        } else if (byteIndex == 13 && tmpByte != tmpEOF) {
            lostApLinkMessages++;
            byteIndex = 0;
            startByteFound = false;
        }
    }

    private void decodeRcInfo(){
        if (byteIndex == 12) {
            tmpEOF = tmpByte;
            byteIndex++;
        } else if ((byteIndex == 11+tmpPayloadLength+1) && (tmpByte == tmpEOF)) {
            byteIndex = 0;
            startByteFound = false;
            validApLinkMessages++;
            System.out.println("RCInfo Decoded");
        } else if ((byteIndex == 11+tmpPayloadLength+1) && (tmpByte != tmpEOF)) {
            byteIndex = 0;
            startByteFound = false;
            lostApLinkMessages++;
        } else {
            byteIndex++;
        }
    }
}
