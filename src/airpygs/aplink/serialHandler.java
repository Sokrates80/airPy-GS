/*
 * airPyGS is a ground station software part of the airPy project (www.air-py.com).
 *
 * The MIT License (MIT)
 * Copyright (c) 2016 Fabrizio Scimia, fabrizio.scimia@gmail.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package airpygs.aplink;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class serialHandler implements SerialPortEventListener{

    private SerialPort serial;
    private TextArea console;
    private String serialPortName;
    private int baudRate;
    private byte[] tmpBytes;
    private ApBuffer buff;

    public StringProperty readString = new SimpleStringProperty("");

    public serialHandler(TextArea text, String sp, String br, ApBuffer b) {

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

    public void writeBytes(byte[] message) {
        try {
            serial.writeBytes(message);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public void serialEvent(SerialPortEvent event) {

        if(event.isRXCHAR()){//If data is available
            //if(event.getEventValue() == 10){//Check bytes count in the input buffer
                //Read data, if 10 bytes available
                try {
                    tmpBytes = serial.readBytes();
                    buff.addToRxBuffer(tmpBytes);
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
