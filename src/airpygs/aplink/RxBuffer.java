package airpygs.aplink;

import java.util.ArrayList;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxBuffer {

    private ArrayList<Byte> buffer;

    public RxBuffer(){
        buffer = new ArrayList<Byte>();
    }

    public synchronized byte readRxBuffer() throws InterruptedException {
        if (buffer.size() == 0) {
            wait();
        }
        return buffer.remove(0);
    }

    public synchronized void addToRxBuffer(byte[] newBytes){
        for (int i = 0; i <  newBytes.length; i++){
            buffer.add(newBytes[i]);
        }
        notifyAll();
    }
}
