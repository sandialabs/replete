package replete.scrutinize.wrappers.sys;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.TreeMap;

import replete.scrutinize.core.BaseSc;

public class ToolkitSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Toolkit.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "extraMouseButtonsEnabled",
            "getAWTEventListeners;count",
            "colorModel",            // TODO
            "defaultToolkit",
            "maximumCursorColors",
            "menuShortcutKeyMask",   // Could make this a pretty string
            "screenResolution",
            "screenSize",
            "systemClipboard",
            "systemEventQueue",
            "systemSelection",
            "alwaysOnTopSupported",
            "dynamicLayoutActive",
            "dynamicLayoutSet"
        };
    }

    @Override
    public Map<String, Object> getCustomFields(Object nativeObj) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Map<String, Object> fields = new TreeMap<>();

        fields.put("desktopProperty(awt.font.desktophints)",    // TODO
            tk.getDesktopProperty("awt.font.desktophints"));

        addKey(tk, fields, "KeyEvent.VK_CAPS_LOCK",   KeyEvent.VK_CAPS_LOCK);
        addKey(tk, fields, "KeyEvent.VK_NUM_LOCK",    KeyEvent.VK_NUM_LOCK);
        addKey(tk, fields, "KeyEvent.VK_SCROLL_LOCK", KeyEvent.VK_SCROLL_LOCK);
        addKey(tk, fields, "KeyEvent.VK_KANA_LOCK",   KeyEvent.VK_KANA_LOCK);

        fields.put("frameStateSupported(Frame.ICONIFIED)",
            tk.isFrameStateSupported(Frame.ICONIFIED));
        fields.put("frameStateSupported(Frame.MAXIMIZED_BOTH)",
            tk.isFrameStateSupported(Frame.MAXIMIZED_BOTH));
        fields.put("frameStateSupported(Frame.MAXIMIZED_HORIZ)",
            tk.isFrameStateSupported(Frame.MAXIMIZED_HORIZ));
        fields.put("frameStateSupported(Frame.MAXIMIZED_VERT)",
            tk.isFrameStateSupported(Frame.MAXIMIZED_VERT));
        fields.put("frameStateSupported(Frame.NORMAL)",
            tk.isFrameStateSupported(Frame.NORMAL));

        for(Dialog.ModalExclusionType type : Dialog.ModalExclusionType.values()) {
            fields.put("modalExclusionTypeSupported(Dialog.ModalExclusionType." + type + ")", tk.isModalExclusionTypeSupported(type));
        }
        for(Dialog.ModalityType type : Dialog.ModalityType.values()) {
            fields.put("modalityTypeSupported(Dialog.ModalityType." + type + ")", tk.isModalityTypeSupported(type));
        }

        fields.put("ScreenInsets", tk.getScreenInsets(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()));

        return fields;
    }

    private void addKey(Toolkit tk, Map<String, Object> fields, String lbl, int vkCapsLock) {
        Object v;
        try {
            v = tk.getLockingKeyState(vkCapsLock);
        } catch(Exception e) {
            v = "N/A";
        }
        fields.put("lockingKeyState(" + lbl + ")", v);
    }
}
