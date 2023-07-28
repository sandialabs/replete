package finio.platform.exts.manager.xstream;

import javax.swing.ImageIcon;

import finio.core.managed.ManagedNonTerminal;
import finio.platform.exts.manager.xstream.ui.XStreamFileManagedParametersPanel;
import finio.plugins.extpoints.NonTerminalManager;
import finio.ui.manager.ManagedParametersPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class XStreamFileMapManager implements NonTerminalManager {

    @Override
    public String getName() {
        return "XStream File";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.XSTREAM);
    }

    @Override
    public ImageIcon getIconForManagedNonTerminal(ManagedNonTerminal G) {
        if(G instanceof XStreamFileManagedNonTerminal) {
            return getIcon();
        }
        return null;
    }

    @Override
    public ManagedNonTerminal createManagedNonTerminal() {
        return new XStreamFileManagedNonTerminal(this);
    }

    public ManagedParametersPanel createParametersPanel() {
        return new XStreamFileManagedParametersPanel();
    }
}
