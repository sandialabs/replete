package replete.ui.tabbed;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;

public class TabCreationDescriptor {


    ////////////
    // FIELDS //
    ////////////

    private Integer index;
    private Object key;
    private String title;              // Required
    private Component component;       // Required
    private Icon icon;
    private String tip;
    private boolean select;
    private Boolean closeable;         // Boolean so this object can also be used as a defaults object
    private String closeToolTip;
    private Boolean dirty;             // Boolean so this object can also be used as a defaults object
    private List<Component> extraComponents = new ArrayList<>();
    private Object metadata;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public Integer getIndex() {
        return index;
    }
    public Object getKey() {
        return key;
    }
    public String getTitle() {
        return title;
    }
    public Component getComponent() {
        return component;
    }
    public Icon getIcon() {
        return icon;
    }
    public String getTip() {
        return tip;
    }
    public boolean isSelect() {
        return select;
    }
    public Boolean isCloseable() {
        return closeable;
    }
    public String getCloseToolTip() {
        return closeToolTip;
    }
    public Boolean isDirty() {
        return dirty;
    }
    public List<Component> getExtraComponents() {
        return extraComponents;
    }
    public Object getMetadata() {
        return metadata;
    }

    // Mutators

    public TabCreationDescriptor setIndex(Integer index) {
        this.index = index;
        return this;
    }
    public TabCreationDescriptor setKey(Object key) {
        this.key = key;
        return this;
    }
    public TabCreationDescriptor setTitle(String title) {
        this.title = title;
        return this;
    }
    public TabCreationDescriptor setComponent(Component component) {
        this.component = component;
        return this;
    }
    public TabCreationDescriptor setIcon(Icon icon) {
        this.icon = icon;
        return this;
    }
    public TabCreationDescriptor setIcon(ImageModelConcept concept) {      // Bonus
        icon = ImageLib.get(concept);
        return this;
    }
    public TabCreationDescriptor setTip(String tip) {
        this.tip = tip;
        return this;
    }
    public TabCreationDescriptor setSelect(boolean select) {
        this.select = select;
        return this;
    }
    public TabCreationDescriptor setCloseable(Boolean closeable) {
        this.closeable = closeable;
        return this;
    }
    public TabCreationDescriptor setCloseToolTip(String closeToolTip) {
        this.closeToolTip = closeToolTip;
        return this;
    }
    public TabCreationDescriptor setDirty(Boolean dirty) {
        this.dirty = dirty;
        return this;
    }
    public TabCreationDescriptor addExtraComponent(Component c) {
        extraComponents.add(c);
        return this;
    }
    public TabCreationDescriptor setMetadata(Object metadata) {
        this.metadata = metadata;
        return this;
    }
}
