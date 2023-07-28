package finio.platform.exts.editor;

import javax.swing.Icon;

import finio.core.FImages;
import finio.core.FStrings;
import finio.core.managed.ManagedValueManager;

public class ManagedValueManagerEditorPanel extends UnknownNativeObjectEditorPanel {

    @Override
    public void setObject(Object O) {
        this.O = O;
        ManagedValueManager G = (ManagedValueManager) O;
        Icon icon = FImages.createIconForTerminal(null, G, false);
        lbl.setIcon(icon);
        lbl.setText(FStrings.createBasicValueString(G));
    }
    
}
