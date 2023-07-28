package finio.ui.multidlg;

import replete.ui.windows.escape.EscapeDialog;

public class ByteInputSourcePanelCreator implements InputSourcePanelCreator {
    @Override
    public InputSourcePanel createPanel(EscapeDialog parent) {
        return new ByteInputSourcePanel(parent);
    }
}
