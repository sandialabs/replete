package replete.ui.debug;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * @author Derek Trumbo
 */

public class DebugTransferHandler extends TransferHandler {
    private DebugListener listener;
    public DebugTransferHandler() {
        this(null);
    }
    public DebugTransferHandler(String evNames) {
        listener = new DebugListener(evNames);
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        if(listener.acceptEvent("exportAsDrag")) {
            String debugStr = "exportAsDrag{comp=" + comp.getClass().getSimpleName() +
                    ", action=" + action +"}";
            System.out.println(debugStr);
        }
        super.exportAsDrag(comp, e, action);
    }

    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action)
            throws IllegalStateException {
        if(listener.acceptEvent("exportToClipboard")) {
            String debugStr = "exportToClipboard{comp=" + comp.getClass().getSimpleName() +
                    ", clip=" + clip + ", action=" + action +"}";
            System.out.println(debugStr);
        }
        super.exportToClipboard(comp, clip, action);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if(listener.acceptEvent("importData")) {
            String debugStr = "importData{support=" + support + "}";
            System.out.println(debugStr);
        }
        return super.importData(support);
    }

    @Override
    public boolean importData(JComponent comp, Transferable data) {
        if(listener.acceptEvent("importData")) {
            String debugStr = "importData{comp=" + comp.getClass().getSimpleName() +
                    ", data=" + data + "}";
            System.out.println(debugStr);
        }
        return super.importData(comp, data);
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if(listener.acceptEvent("canImport")) {
            String debugStr = "canImport{support=" + support + "}";
            System.out.println(debugStr);
        }
        return super.canImport(support);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if(listener.acceptEvent("canImport")) {
            String debugStr = "canImport{comp=" + comp.getClass().getSimpleName() +
                    ", dataflavors=" + Arrays.toString(transferFlavors) +"}";
            System.out.println(debugStr);
        }
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public int getSourceActions(JComponent comp) {
        if(listener.acceptEvent("getSourceActions")) {
            String debugStr = "getSourceActions{comp=" + comp.getClass().getSimpleName() + "}";
            System.out.println(debugStr);
        }
        return super.getSourceActions(comp);
    }

    @Override
    public Icon getVisualRepresentation(Transferable data) {
        if(listener.acceptEvent("getVisualRepresentation")) {
            String debugStr = "getVisualRepresentation{data=" + data + "}";
            System.out.println(debugStr);
        }
        return super.getVisualRepresentation(data);
    }

    @Override
    protected Transferable createTransferable(JComponent comp) {
        if(listener.acceptEvent("createTransferable")) {
            String debugStr = "createTransferable{comp=" + comp.getClass().getSimpleName() + "}";
            System.out.println(debugStr);
        }
        return super.createTransferable(comp);
    }

    @Override
    protected void exportDone(JComponent comp, Transferable data, int action) {
        if(listener.acceptEvent("exportDone")) {
            String debugStr =
                "exportDone{comp=" + comp.getClass().getSimpleName() +
                ", data=" + data +
                ", action=" + action + "}";
            System.out.println(debugStr);
        }
        super.exportDone(comp, data, action);
    }
}
