package airpygs.aplink;

/**
 * Created by fabrizioscimia on 24/02/16.
 */
public class AplMessage {

    //Header fields
    private int messageID;
    private int QCI;
    private int lastFragment;
    private int messageTypeID;
    private int failSafe;
    private int flightMode;
    private int payloadLength;
    private byte[] payload;

    public AplMessage() {
        messageID = -1;
        QCI = -1;
        lastFragment = -1;
        messageTypeID = -1;
        failSafe = -1;
        flightMode = -1;
        payloadLength = -1;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getQCI() {
        return QCI;
    }

    public void setQCI(int QCI) {
        this.QCI = QCI;
    }

    public int getLastFragment() {
        return lastFragment;
    }

    public void setLastFragment(int lastFragment) {
        this.lastFragment = lastFragment;
    }

    public int getMessageTypeID() {
        return messageTypeID;
    }

    public void setMessageTypeID(int messageTypeID) {
        this.messageTypeID = messageTypeID;
    }

    public int getFailSafe() {
        return failSafe;
    }

    public void setFailSafe(int failSafe) {
        this.failSafe = failSafe;
    }

    public int getFlightMode() {
        return flightMode;
    }

    public void setFlightMode(int flightMode) {
        this.flightMode = flightMode;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}