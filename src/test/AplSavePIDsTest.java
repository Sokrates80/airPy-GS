package test;

import airpygs.aplink.messages.AplSavePIDs;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fabrizioscimia on 29/06/16.
 */
public class AplSavePIDsTest {


    @org.junit.Test
    public void testGetPayloadLength() throws Exception {

        float[] testArray = {0.1f, 0.2f, 0.3f, 0.4f};

        AplSavePIDs msg = new AplSavePIDs(testArray);

        assertEquals("Size of 4 float element is 16", 16, msg.getPayloadLength());

    }
}