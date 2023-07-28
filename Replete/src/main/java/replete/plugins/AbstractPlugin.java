package replete.plugins;

import javax.swing.ImageIcon;


public abstract class AbstractPlugin implements Plugin {
    public String getVersion() {
        return "0.0.1";
    }
    public String getProvider() {
        return null;
    }
    public ImageIcon getIcon() {
        return null;
    }
    public String getDescription() {
        return null;
    }
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return null;
    }
    public ExtensionPoint[] getExtensions() {
        return null;
    }
    public void start() {
    }
}
