package finio.ui.multidlg;

import replete.ui.windows.escape.EscapeDialog;

public class FileInputSourcePanelCreator implements InputSourcePanelCreator {
    @Override
    public InputSourcePanel createPanel(EscapeDialog parent) {
        return new FileInputSourcePanel(parent);
    }
}
