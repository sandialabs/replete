package replete.scrutinize.wrappers.disk;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.filechooser.FileSystemView;

import replete.io.FileUtil;
import replete.scrutinize.core.BaseSc;

public class FileSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return File.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getAbsolutePath",
            "canRead",
            "canWrite",
            "name",
            "path",
            "canonicalPath",
            "exists",
            "length",                // File.listRoots() ?
            "isDirectory",
            "isFile",
            "hidden",
            "lastModified",
            "canExecute",
            "list;count",
            "isAbsolute"
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        File file = (File) nativeObj;
        Map<String, Object> fields = new TreeMap<>();
        FileSystemView fsv = FileSystemView.getFileSystemView();
        if(fsv.isRoot(file)) {
            fields.put("(Root) FreeSpace", FileUtil.getReadableSizeString(file.getFreeSpace()));
            fields.put("(Root) TotalSpace", FileUtil.getReadableSizeString(file.getTotalSpace()));
            fields.put("(Root) UsableSpace", FileUtil.getReadableSizeString(file.getUsableSpace()));
            fields.put("(Root) DisplayName", fsv.getSystemDisplayName(file));
        }
        return fields;
    }
}
