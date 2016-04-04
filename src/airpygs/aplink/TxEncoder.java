package airpygs.aplink;

import airpygs.aplink.messages.AplDisableMessage;
import airpygs.aplink.messages.AplEnableMessage;
import airpygs.aplink.messages.AplEnableEscCalibration;

/**
 * Created by fabrizioscimia on 13/03/16.
 */
public class TxEncoder {

    private serialHandler serial;

    public TxEncoder(serialHandler s) {
        serial = s;
    }

    public void enableMessage(int messageTypeId) {
        serial.writeBytes((new AplEnableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Enable Request for Message Type " + messageTypeId);
    }

    public void disableMessage(int messageTypeId) {
        serial.writeBytes((new AplDisableMessage(messageTypeId).getBytes()));
        System.out.println("Sent Disable Request for Message Type " + messageTypeId);
    }

    public void enableEscCalibration() {
        serial.writeBytes((new AplEnableEscCalibration().getBytes()));
        System.out.println("Sent Enable Esc Calbration Request");
    }
}
