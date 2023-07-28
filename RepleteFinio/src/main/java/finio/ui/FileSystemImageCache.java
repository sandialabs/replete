package finio.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class FileSystemImageCache {

    private static Map<File, ImageIcon> images = new HashMap<>();

    public static ImageIcon get(File file) {
        if(!images.containsKey(file)) {
            loadImage(file);
        } else {
            checkRefresh(file);
        }
        return images.get(file);
    }

    private static void loadImage(File file) {
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        images.put(file, icon);
    }

    private static void checkRefresh(File file) {
        // TODO: Check if file has changed and reload into image cache.
    }
}
