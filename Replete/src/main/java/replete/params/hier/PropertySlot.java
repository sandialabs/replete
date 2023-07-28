package replete.params.hier;

import java.io.Serializable;

import javax.swing.Icon;

import replete.plugins.HumanDescribable;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class PropertySlot implements Serializable, HumanDescribable {


    ////////////
    // FIELDS //
    ////////////

    private String key;
    private String name;
    private String description;
    private Icon icon;
    private Class<? extends PropertyParams> paramsClass;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getKey() {
        return key;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getDescription() {
        return description;
    }
    @Override
    public Icon getIcon() {
        return icon;
    }
    public Class<? extends PropertyParams> getParamsClass() {
        return paramsClass;
    }

    // Mutators

    public PropertySlot setKey(String key) {
        this.key = key;
        return this;
    }
    public PropertySlot setName(String name) {
        this.name = name;
        return this;
    }
    public PropertySlot setDescription(String description) {
        this.description = description;
        return this;
    }
    public PropertySlot setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
    public PropertySlot setParamsClass(Class<? extends PropertyParams> paramsClass) {
        this.paramsClass = paramsClass;
        return this;
    }

    // Mutators (Extra)

    public PropertySlot setIcon(ImageModelConcept concept) {
        icon = ImageLib.get(concept);
        return this;
    }
}
