package replete.scrutinize.wrappers.disk;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.filechooser.FileSystemView;

import replete.scrutinize.core.BaseSc;

public class FileSystemViewSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return FileSystemView.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getFileSystemView",
            "defaultDirectory",
            "homeDirectory",
            "roots"
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        Map<String, Object> fields = new TreeMap<>();
        fields.put("App Working Dir", new File(""));
        return fields;
    }
}
