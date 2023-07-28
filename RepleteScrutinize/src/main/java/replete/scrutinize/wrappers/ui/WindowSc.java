package replete.scrutinize.wrappers.ui;

import java.awt.Window;

import replete.scrutinize.core.BaseSc;

public class WindowSc extends BaseSc {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Window.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getWindows",
            "getOwnerlessWindows",
            "defaultModalityType",         // ????
            "autoRequestFocus",
            "disposing",
            "recursivelyVisible",
            "shape",
            "background",
            "active",
            "alwaysOnTop",
            "alwaysOnTopSupported",
            "focusableWindow",
            "focusCycleRoot",
            "focusCycleRootAncestor",
            "focusableWindowState",
            "focused",
            "locationByPlatform",
            "showing",
            "locale",
            "ownedWindows",
            "modalBlocked",
            "modalExclusionType",
            "focusOwner",
            "title",             // has menu bar, how many components?
            "opacity",
            "type",
            "warningString",
            "size",
            "location",
            "rootPaneCheckingEnabled",
            "defaultCloseOperation",
            "transferHandler"           // TODO ?
        };
    }
}

//Window win = (Window) obj;
//ret += "DefaultModalityType=" + Dialog.DEFAULT_MODALITY_TYPE + "\n";
//// much more .. x,y, contained components, widht/height,
//// container/component stuff
//
//if(obj instanceof Frame) {
//    Frame frame = (Frame) obj;
//    ret += "Frame: " + obj.getClass().getSimpleName() + "\n";
//    ret += "Title=" + frame.getTitle() + "\n";
//} else if(obj instanceof Dialog) {
//    Dialog frame = (Dialog) obj;
//    ret += "Dialog: " + obj.getClass().getSimpleName() + "\n";
//    ret += "Title=" + frame.getTitle() + "\n";
//}
