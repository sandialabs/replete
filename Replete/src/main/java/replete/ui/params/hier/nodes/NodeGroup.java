package replete.ui.params.hier.nodes;

import javax.swing.Icon;

import replete.params.hier.PropertyGroup;
import replete.text.StringUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.params.hier.images.HierParamsImageModel;
import replete.ui.tree.NodeBase;

public class NodeGroup extends NodeBase {


    ////////////
    // FIELDS //
    ////////////

    private PropertyGroup group;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NodeGroup(PropertyGroup group) {
        this.group = group;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public PropertyGroup getGroup() {
        return group;
    }

    // Mutators

    public NodeGroup setGroup(PropertyGroup group) {
        this.group = group;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Icon getIcon(boolean expanded) {
        return ImageLib.get(HierParamsImageModel.GROUP);
    }

    @Override
    public String toString() {
        String s = "<html>";
        if(group.getLabel() == null) {
            s += "<i>(Group):</i>";
        } else {
            s += "<u><b>" + StringUtil.cleanXmlCdata(group.getLabel()) + ":</b></u>";
        }
        s += " " + StringUtil.cleanXmlCdata(group.getCriteria().toString());
        s += "</html>";
        return s;
    }
}
