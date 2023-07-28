package replete.plugins.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;
import replete.plugins.PluginManager;
import replete.plugins.Validatable;
import replete.plugins.ValidationResult;

public class PluginState implements Serializable {


    ////////////
    // FIELDS //
    ////////////

    private String id;
    private String name;
    private String desc;
    private String version;
    private String provider;
    private ImageIcon icon;
    private List<ExtensionPointState> extensionPoints;
    private List<ExtensionState> extensions;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // This constructor only to be used if you don't need
    // caching of extension point state and extension state
    // objects.
    public PluginState(Plugin plugin) {
        id = PluginManager.getPluginId(plugin);
        name = plugin.getName();
        desc = plugin.getDescription();
        version = plugin.getVersion();
        provider = plugin.getProvider();
        icon = getIcon();
        extensionPoints = new ArrayList<>();
        extensions = new ArrayList<>();
        if(plugin.getExtensionPoints() != null) {
            for(Class<? extends ExtensionPoint> extPoint : plugin.getExtensionPoints()) {
                extensionPoints.add(new ExtensionPointState(
                    PluginManager.getExtensionPointId(extPoint)));
            }
        }
        if(plugin.getExtensions() != null) {
            for(ExtensionPoint ext : plugin.getExtensions()) {
                ValidationResult vResult = null;
                if(ext instanceof Validatable) {
                    vResult = ((Validatable) ext).validate();
                }
                Class<? extends ExtensionPoint> extPoint = PluginManager.getPointForExtension(ext);
                String extPointId = PluginManager.getExtensionPointId(extPoint);
                ExtensionPointState extPointState = new ExtensionPointState(extPointId);
                extensions.add(new ExtensionState(
                    PluginManager.getExtensionId(ext),
                    vResult,
                    extPointState
                ));
            }
        }
    }

    // This constructor can be used to control caching of
    // extension point state and extension state external
    // to this class.
    public PluginState(String id, String name, String desc, String version, String provider,
                       ImageIcon icon, List<ExtensionPointState> extensionPoints,
                       List<ExtensionState> extensions) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.version = version;
        this.provider = provider;
        this.icon = icon;
        this.extensionPoints = extensionPoints;
        this.extensions = extensions;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDesc() {
        return desc;
    }
    public String getVersion() {
        return version;
    }
    public String getProvider() {
        return provider;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public List<ExtensionPointState> getExtensionPoints() {
        return extensionPoints;
    }
    public List<ExtensionState> getExtensions() {
        return extensions;
    }
}
