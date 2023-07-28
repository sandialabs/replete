package replete.scrutinize.wrappers.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;


public class GraphicsDeviceSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return GraphicsDevice.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "availableAcceleratedMemory",
            "configurations",
            "defaultConfiguration",
            "displayMode",
            "displayModes",
            "IDstring",
            "type",
            "displayChangeSupported",
            "fullScreenSupported",
            "fullScreenWindow",
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        GraphicsDevice device = (GraphicsDevice) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        for(WindowTranslucency wt : WindowTranslucency.values()) {
            String key = "windowTranslucencySupported(" + wt.name() + ")";
            fields.put(key, device.isWindowTranslucencySupported(wt));
        }
        return fields;
    }
}
