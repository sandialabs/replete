package replete.progress;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import junit.framework.TestCase;

public class ProgressManagerTest extends TestCase {

    public String allOutput = "";

    public ProgressManagerTest(String name) {
        super(name);
    }

    // **** Test Methods *******************************************************

    public void testManager() {
        Object key1 = new String("Key##1");
        Object key2 = new String("Key##2");
        Object key3 = new String("Key##3");
        Object key4 = new String("Key##4");
        Object key5 = new String("Key##5");

        ProgressManager.addProgressListener(progressListener);
        ProgressManager.addClearListener(clearListener);

        // Single source.
        end(key1, "h");
        send(key1, "h", 1, 1);
        send(key1, "h", 1, 2);
        send(key1, "h", 1, 3);
        end(key1, "h");
        end(key1, "h");
        send(key1, "h", 1, 3);
        end(key1, "h");

        System.out.println();

        // MED1, HIGH1, MED1
        send(key2, "m", 2, 1);
        send(key2, "m", 2, 2);
        send(key1, "h", 1, 4);
        //end(key1, "h");  // uncomment to see difference
        send(key2, "m", 2, 3);
        send(key2, "m", 2, 4);
        send(key1, "h", 1, 5);
        end(key1, "h");
        send(key2, "m", 2, 5);
        end(key2, "m");

        System.out.println();

        // HIGH1, HIGH2, MED1, LOW1
        send(key1, "h", 1, 1);
        send(key2, "h", 2, 1);
        send(key3, "m", 3, 1);
        send(key4, "m", 4, 1);
        send(key2, "h", 2, 2);
        send(key3, "m", 3, 2);
        end(key1, "h");
        end(key2, "h");
        send(key5, "l", 5, 1);
        end(key3, "m");
        send(key1, "h", 1, 2);
        end(key4, "m");
        end(key1, "h");
        end(key5, "l");

        String expected =
            "[88% : 37 / 42] h/1/1: h/1/1" +
            "[88% : 37 / 42] h/1/2: h/1/2" +
            "[88% : 37 / 42] h/1/3: h/1/3" +
            "CLEAR" +
            "[88% : 37 / 42] h/1/3: h/1/3" +
            "CLEAR" +
            "[88% : 37 / 42] m/2/1: m/2/1" +
            "[88% : 37 / 42] m/2/2: m/2/2" +
            "[88% : 37 / 42] h/1/4: h/1/4" +
            "[88% : 37 / 42] h/1/5: h/1/5" +
            "[88% : 37 / 42] m/2/4: m/2/4" +
            "[88% : 37 / 42] m/2/5: m/2/5" +
            "CLEAR" +
            "[88% : 37 / 42] h/1/1: h/1/1" +
            "[88% : 37 / 42] h/2/2: h/2/2" +
            "[88% : 37 / 42] m/3/2: m/3/2" +
            "[88% : 37 / 42] m/4/1: m/4/1" +
            "[88% : 37 / 42] h/1/2: h/1/2" +
            "[88% : 37 / 42] l/5/1: l/5/1" +
            "CLEAR";

        assertEquals(expected, allOutput);
    }

    protected void end(Object key, String hml) {
        int x;
        if(hml.equals("h")) {
            x = ProgressManager.HIGH_PR;
        } else if(hml.equals("m")) {
            x = ProgressManager.MEDIUM_PR;
        } else {
            x = ProgressManager.LOW_PR;
        }
        ProgressManager.endProgress(key, x);
    }

    protected void send(Object key, String hml, int source, int msgNo) {
        ProgressMessage m = new FractionProgressMessage(
            hml + "/" + source + "/" + msgNo,
            hml + "/" + source + "/" + msgNo,
            37, 42);
        int x;
        if(hml.equals("h")) {
            x = ProgressManager.HIGH_PR;
        } else if(hml.equals("m")) {
            x = ProgressManager.MEDIUM_PR;
        } else {
            x = ProgressManager.LOW_PR;
        }
        ProgressManager.sendProgress(key, x, m);
    }

    public ChangeListener progressListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            ProgressMessage pm = (ProgressMessage) e.getSource();
            allOutput += pm.toString();
        }
    };

    public ChangeListener clearListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            allOutput += "CLEAR";
        }
    };
}
