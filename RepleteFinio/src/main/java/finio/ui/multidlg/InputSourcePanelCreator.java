package finio.ui.multidlg;

import replete.plugins.ExtensionPoint;
import replete.ui.windows.escape.EscapeDialog;

public interface InputSourcePanelCreator extends ExtensionPoint {
    public InputSourcePanel createPanel(EscapeDialog parent);
}
