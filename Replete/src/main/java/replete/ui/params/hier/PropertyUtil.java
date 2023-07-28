package replete.ui.params.hier;

import javax.swing.Icon;

import replete.params.hier.PropertySlot;
import replete.plugins.UiGenerator;
import replete.text.StringUtil;
import replete.ui.images.concepts.ImageLib;
import replete.ui.params.hier.images.HierParamsImageModel;

public class PropertyUtil {

    // Choose a single icon to represent the property, given the
    // slot and property generator.
    public static Icon getIcon(PropertySlot slot, UiGenerator generator) {
        Icon icon;
        if(slot.getIcon() != null) {
            icon = slot.getIcon();
        } else if(generator.getIcon() != null) {
            icon = generator.getIcon();
        } else {
            icon = ImageLib.get(HierParamsImageModel.PROPERTY);
        }
        return icon;
    }

    // Choose composite description to represent the property, given the
    // slot and property generator.
    public static String getDescription(PropertySlot slot, UiGenerator generator) {
        String desc = "";
        if(!StringUtil.isBlank(slot.getDescription())) {
            desc += StringUtil.markupMissingText(slot.getDescription());
        }
        if(!StringUtil.isBlank(generator.getDescription())) {
            if(!desc.isEmpty()) {
                desc += "  ";
            }
            desc += StringUtil.markupMissingText(generator.getDescription());
        }
        return StringUtil.isBlank(desc) ? "" : " <i>" + desc + "</i>";
    }

}
