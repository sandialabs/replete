package finio.platform.exts.manager.orientdb;

import javax.swing.ImageIcon;

import finio.core.managed.ManagedNonTerminal;
import finio.platform.exts.manager.orientdb.nt.OdbRootNt;
import finio.platform.exts.manager.orientdb.nt.OdbUserClassesNt;
import finio.platform.exts.manager.orientdb.ui.images.OrientDbImageModel;
import finio.platform.exts.manager.xstream.ui.XStreamFileManagedParametersPanel;
import finio.plugins.extpoints.NonTerminalManager;
import finio.ui.manager.ManagedParametersPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class OrientDbFileMapManager implements NonTerminalManager {

    @Override
    public String getName() {
        return "OrientDB Database";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.ORIENTDB);
    }

    @Override
    public ImageIcon getIconForManagedNonTerminal(ManagedNonTerminal G) {
        if(G instanceof OdbRootNt) {
            return getIcon();
        } else if(G instanceof OdbUserClassesNt) {
            return ImageLib.get(OrientDbImageModel.CLASS_GROUP);
        }
        return null;
    }

    @Override
    public ManagedNonTerminal createManagedNonTerminal() {
        return new OdbRootNt(this);
    }

    public ManagedParametersPanel createParametersPanel() {
        return new XStreamFileManagedParametersPanel();
    }
}
