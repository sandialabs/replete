package replete.plugins.test;

import javax.swing.ImageIcon;

import replete.SoftwareVersion;
import replete.plugins.AutomaticPluginLoadingDisallowed;
import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class TestPlugin implements Plugin, AutomaticPluginLoadingDisallowed {

    @Override
    public String getName() {
        return "Test Plugin";
    }

    @Override
    public String getVersion() {
        return SoftwareVersion.get().getFullVersionString();
    }

    @Override
    public String getProvider() {
        return "Provider";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(CommonConcepts.PLUGIN);
    }

    @Override
    public String getDescription() {
        return "Some test plug-in description.";
    }

    @Override
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {MyExtensionPoint.class};
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {new MyExtension()};
    }

    @Override
    public void start() {
    }
}
