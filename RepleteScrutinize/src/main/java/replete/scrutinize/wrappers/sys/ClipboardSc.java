package replete.scrutinize.wrappers.sys;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;

public class ClipboardSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Clipboard.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "availableDataFlavors",
            "name"
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        Clipboard clipboard = (Clipboard) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        for(DataFlavor flavor : clipboard.getAvailableDataFlavors()) {
            String key = "getData(" + flavor + ")";
            try {
                fields.put(key, clipboard.getData(flavor));
            } catch(Exception e) {
                fields.put(key, "<Error: " + e.getMessage() + ">");
            }
        }
        return fields;
    }
}
