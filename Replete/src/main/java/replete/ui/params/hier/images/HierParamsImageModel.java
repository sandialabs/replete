package replete.ui.params.hier.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class HierParamsImageModel extends ImageModel {

    public static final ImageModelConcept GLOBAL       = conceptShared(SharedImage.DIAMOND_3D_PURPLE);
    public static final ImageModelConcept HIERARCHICAL = conceptLocal("hierarchical.gif");
    public static final ImageModelConcept GROUP        = conceptShared(SharedImage.CIRCLES_3_GROUPED);
    public static final ImageModelConcept GROUP_ADD    = conceptShared(SharedImage.CIRCLES_3_GROUPED_NEW);
    public static final ImageModelConcept PROPERTY     = conceptShared(SharedImage.DOT_BLUE);
    public static final ImageModelConcept PROPERTY_ADD = conceptLocal("property-add.gif");
    public static final ImageModelConcept EDIT_ALL     = conceptLocal("pencil-multi.gif");
    public static final ImageModelConcept TEST         = conceptShared(SharedImage.MAGNIFYING_GLASS);


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
