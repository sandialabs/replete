package finio.platform.exts.manager.trivial;

import javax.swing.ImageIcon;

import finio.core.managed.ManagedNonTerminal;
import finio.platform.exts.manager.trivial.ui.TrivialManagedParametersPanel;
import finio.plugins.extpoints.NonTerminalManager;
import finio.ui.images.FinioImageModel;
import finio.ui.manager.ManagedParametersPanel;
import replete.ui.images.concepts.ImageLib;

public class TrivialNonTerminalManager implements NonTerminalManager {

    @Override
    public String getName() {
        return "Trivial";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(FinioImageModel.TRIVIAL_MANAGEMENT);
    }

    @Override
    public ImageIcon getIconForManagedNonTerminal(ManagedNonTerminal G) {
        return null;
    }

    @Override
    public ManagedNonTerminal createManagedNonTerminal() {
        return new TrivialManagedNonTerminal(this);
    }

    public ManagedParametersPanel createParametersPanel() {
        return new TrivialManagedParametersPanel();
    }
}
