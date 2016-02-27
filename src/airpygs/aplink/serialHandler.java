package airpygs.aplink;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * Created by fabrizioscimia on 11/01/16.
 */
public class serialHandler implements SerialPortEventListener{

    private SerialPort serial;
    private TextArea console;
    private String serialPortName;
    private int baudRate;
    private byte[] tmpBytes;
    private RxBuffer buff;

    public StringProperty readString = new SimpleStringProperty("");

    public serialHandler(TextArea text, String sp, String br, RxBuffer b) {

        console = text;
        serialPortName = sp;
        baudRate = Integer.parseInt(br);
        serial = new SerialPort(serialPortName);
        buff = b;

        try {
            serial.openPort();//Open serial port
            switch (baudRate) {

                case 9600:  serial.setParams(SerialPort.BAUDRATE_9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            break;
                case 14400: serial.setParams(SerialPort.BAUDRATE_14400,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            break;
                case 38400: serial.setParams(SerialPort.BAUDRATE_38400,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            break;
                case 57600: serial.setParams(SerialPort.BAUDRATE_57600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                            break;
                case 115200: serial.setParams(SerialPort.BAUDRATE_115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                             break;

            }

            //Set params. Also you can set params by this string: serialPort.setParams(9600, 8, 1, 0);
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serial.setEventsMask(mask);//Set mask
            serial.addEventListener(this);//Add SerialPortEventListener

        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }

    public SerialPort getSerial() {
        return serial;
    }

    public void serialEvent(SerialPortEvent event) {

        if(event.isRXCHAR()){//If data is available
            //if(event.getEventValue() == 10){//Check bytes count in the input buffer
                //Read data, if 10 bytes available
                try {
                    tmpBytes = serial.readBytes();

                    buff.addToRxBuffer(tmpBytes);
                    //readString.set(tmpString);
                    //System.out.println(HexBin.encode(tmpBytes));
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            //}
            /*else if(event.isCTS()){//If CTS line has changed state
                if(event.getEventValue() == 1){//If line is ON
                    System.out.println("CTS - ON");
                }
                else {
                    System.out.println("CTS - OFF");
                }
            }
            else if(event.isDSR()){///If DSR line has changed state
                if(event.getEventValue() == 1){//If line is ON
                    System.out.println("DSR - ON");
                }
                else {
                    System.out.println("DSR - OFF");
                }
            }*/
        }

    }

}
