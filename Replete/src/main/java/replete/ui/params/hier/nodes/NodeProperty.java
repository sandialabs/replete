package replete.ui.params.hier.nodes;

import javax.swing.Icon;

import replete.params.hier.PropertyParams;
import replete.params.hier.PropertySetSpecification;
import replete.params.hier.PropertySlot;
import replete.text.StringUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.params.hier.images.HierParamsImageModel;
import replete.ui.tree.NodeBase;

public class NodeProperty extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private PropertySetSpecification spec;
    private String key;
    private PropertyParams params;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeProperty(PropertySetSpecification spec, String key, PropertyParams params) {
        this.spec = spec;
        this.key = key;
        this.params = params;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getKey() {
        return key;
    }
    public PropertyParams getParams() {
        return params;
    }

    // Mutators

    public NodeProperty setKey(String key) {
        this.key = key;
        return this;
    }
    public NodeProperty setParams(PropertyParams params) {
        this.params = params;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        PropertySlot slot = spec.getSlot(key);
        if(slot.getIcon() != null) {
            return slot.getIcon();
        }
        // Could maybe check the generator's icon too... Can use PropertyUtil.getIcon(...)?
        return ImageLib.get(HierParamsImageModel.PROPERTY);
    }

    @Override
    public String toString() {
        return
            "<html><u><b>" +
            StringUtil.cleanXmlCdata(spec.getSlot(key).getName()) +
            ":</b></u> " + StringUtil.cleanXmlCdata(params.toString()) +
            "</html>";
    }
}
