package airpygs.aplink;

/**
 * Created by fabrizioscimia on 23/02/16.
 */
public class ApLinkParams {

    //AirPy Link Protocol Header
    public static final short HEADER_LENGTH = 12;
    public static final short START_BYTE = 15;
    public static final short MESSAGE_ID_BYTE_1 = 1;
    public static final short MESSAGE_ID_BYTE_2 = 2;
    public static final short QCI_AND_LAST_FRAGMENT_FLAG = 3;
    public static final short MESSAGE_TYPE_ID = 4;
    public static final short FAIL_SAFE_AND_FLIGHT_MODE = 10;
    public static final short PAYLOAD_LENGTH = 11;
    public static final short PAYLOAD_1ST_BYTE = 12;

    // AirPy Link Protocol messages
    public static final short AP_MESSAGE_HEARTBEAT = 10;
    public static final short AP_MESSAGE_RC_INFO = 20;

    // AirPy Link Protocol general parameters
    public static double MAX_RC_VALUE = 2048.0;  //Maximum value of ab RC channel (11 Bit)
    public static int AP_MESSAGE_RC_INFO_NUM_CHANNELS = 18;
}
