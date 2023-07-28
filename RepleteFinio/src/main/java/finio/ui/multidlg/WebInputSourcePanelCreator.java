package finio.ui.multidlg;

import replete.ui.windows.escape.EscapeDialog;

public class WebInputSourcePanelCreator implements InputSourcePanelCreator {
    @Override
    public InputSourcePanel createPanel(EscapeDialog parent) {
        return new WebInputSourcePanel(parent);
    }
}
