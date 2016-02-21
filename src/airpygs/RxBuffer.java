package airpygs;

import java.util.ArrayList;

/**
 * Created by fabrizioscimia on 16/02/16.
 */
public class RxBuffer {

    private ArrayList buffer;

    public RxBuffer(){
        buffer = new ArrayList();
    }

    public synchronized char readRxBuffer() throws InterruptedException {
        if (buffer.size() == 0) {
            wait();
        }
        return (char) buffer.remove(0);
    }

    public synchronized void addToRxBuffer(String s){
        buffer.add(s);
        notifyAll();
    }
}
