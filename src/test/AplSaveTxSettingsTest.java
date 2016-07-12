package test;

import airpygs.aplink.messages.AplSaveTxSettings;
import airpygs.utils.TxSettings;
import airpygs.utils.TxSettingsFloat;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fabrizioscimia on 29/06/16.
 */

/**
 * Created by fabrizioscimia on 07/07/16.
 */
public class AplSaveTxSettingsTest {

    TxSettingsFloat txs = new TxSettingsFloat(3); // 3 Channels
    AplSaveTxSettings msg;

    private void initTest() {

        txs.setMinThreshold(0, 1f);
        txs.setMinThreshold(1, 3f);
        txs.setMinThreshold(2, 1f);
        txs.setMaxThreshold(0, 4f);
        txs.setMaxThreshold(1, 8f);
        txs.setMaxThreshold(2, 12f);
        txs.setCenterThreshold(0, 2f);
        txs.setCenterThreshold(1, 4f);
        txs.setCenterThreshold(2, 6f);

        msg = new AplSaveTxSettings(txs);
    }

    @org.junit.Test
    public void testGetPayloadLength() throws Exception {

        this.initTest();
        assertEquals("Size of 3 int * 3 arrays element is 36 bytes", 36, msg.getPayloadLength());


    }

    @org.junit.Test
    public void testGetPayloadValue() throws Exception {

        this.initTest();
        assertEquals("Value of Element (1,8) is 8", 8f, msg.getPayload()[19]);
    }


}
