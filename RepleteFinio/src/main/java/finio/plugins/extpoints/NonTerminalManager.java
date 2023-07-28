package finio.plugins.extpoints;

import java.io.Serializable;

import javax.swing.ImageIcon;

import finio.core.managed.ManagedNonTerminal;
import finio.ui.manager.ManagedParametersPanel;
import replete.plugins.ExtensionPoint;

public interface NonTerminalManager extends ExtensionPoint, Serializable {
    public String getName();
    public ImageIcon getIcon();
    public ImageIcon getIconForManagedNonTerminal(ManagedNonTerminal G);
    public ManagedNonTerminal createManagedNonTerminal();
    public ManagedParametersPanel createParametersPanel();
}
