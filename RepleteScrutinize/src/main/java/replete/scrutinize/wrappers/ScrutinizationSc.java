package replete.scrutinize.wrappers;

import java.awt.AWTException;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import replete.scrutinize.core.BaseSc;


public class ScrutinizationSc extends BaseSc {
    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        Map<String, Object> cFields = new TreeMap<>();
        cFields.put("Graphics Environment", GraphicsEnvironment.class);   // DONE
        cFields.put("Locale",               Locale.class);                // DONE
        cFields.put("System",               System.class);                // DONE
        cFields.put("Mouse",                MouseInfo.class);             // DONE
        cFields.put("Toolkit",              Toolkit.class);               // DONE
        cFields.put("Runtime",              Runtime.class);               // DONE
        cFields.put("UI Manager",           UIManager.class);             // DONE
        cFields.put("Windows",              Window.class);                // DONE (Covers Frame.getFrames())
        cFields.put("File System",          FileSystemView.class);        // DONE
        cFields.put("Network",              NetworkInterface.class);      // DONE
        cFields.put("Internet Addresses",   InetAddress.class);           // DONE
        cFields.put("Management Beans",     ManagementFactory.class);     // DONE
        cFields.put("Class Loader",         ClassLoader.class);           // DONE
        // processes
        Robot robot = null;
        try {
            robot = new Robot();
        } catch(AWTException e) {
            e.printStackTrace();
        }
        cFields.put("Robot", robot);                                      // DONE
        return cFields;
    }
}
