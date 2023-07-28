package finio.ui.multidlg;

import replete.ui.windows.escape.EscapeDialog;

public class TextInputSourcePanelCreator implements InputSourcePanelCreator {
    @Override
    public InputSourcePanel createPanel(EscapeDialog parent) {
        return new TextInputSourcePanel(parent);
    }
}
