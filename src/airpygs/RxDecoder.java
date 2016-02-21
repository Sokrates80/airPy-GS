package airpygs;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxDecoder extends Thread {

    private double rxFrequency = 100;
    private boolean active;
    private serialHandler serial;
    private RxBuffer buffer;

    final int FRAME_MARKER = 255;

    public RxDecoder(serialHandler s, RxBuffer b) {
        serial = s;
        buffer = b;
    }

    public void startRxDecoder(){
        active = true;
    }

    public void stopRxDecoder(){
        active = false;
    }

    public void run(){

            while (true) {

                try {
                    //System.out.println(buffer.readRxBuffer());
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

    }
}
