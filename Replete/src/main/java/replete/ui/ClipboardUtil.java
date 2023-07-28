package replete.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtil {

    public static void set(String val){
        StringSelection sel = new StringSelection(val);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(sel, null);
    }

    public static String get() {
        String val = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasText =
          (contents != null) &&
          contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if(hasText) {
            try {
                val = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch(Exception ex) {}
        }
        return val;
    }
}
