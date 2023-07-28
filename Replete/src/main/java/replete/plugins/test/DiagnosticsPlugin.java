package replete.plugins.test;

import javax.swing.ImageIcon;

import replete.plugins.AutomaticPluginLoadingDisallowed;
import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;

public class DiagnosticsPlugin implements Plugin, AutomaticPluginLoadingDisallowed {


    ////////////
    // FIELDS //
    ////////////

    private Class[] extensionPoints = new Class[0];
    private ExtensionPoint[] extensions = new ExtensionPoint[0];


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DiagnosticsPlugin() {}
    public DiagnosticsPlugin(Class extensionPoint, ExtensionPoint... extensions) {
        extensionPoints = new Class[] {extensionPoint};
        this.extensions = extensions;
    }
    public DiagnosticsPlugin(ExtensionPoint... extensions) {
        this.extensions = extensions;
    }


    //////////////
    // MUTATORS //
    //////////////

    public DiagnosticsPlugin setExtensionPoints(Class... extensionPoints) {
        this.extensionPoints = extensionPoints;
        return this;
    }
    public DiagnosticsPlugin setExtensions(ExtensionPoint... extensions) {
        this.extensions = extensions;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Diagnostics Plug-in";
    }

    @Override
    public String getVersion() {
        return "0.0.1";
    }

    @Override
    public String getProvider() {
        return "Sandia National Laboratories";
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

    @Override
    public String getDescription() {
        return "This programmatically configurable plug-in can be used for testing purposes.";
    }

    @Override
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return extensionPoints;
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return extensions;
    }

    @Override
    public void start() {
    }
}
