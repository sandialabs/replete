package replete.scripting.rscript.ui;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class TreeImageModel extends ImageModel {
    public static final ImageModelConcept STATEMENT = conceptShared(SharedImage.STAR_YELLOW);
    public static final ImageModelConcept CONSTANT  = conceptShared(SharedImage.C_WHITE_CIRCLE_GREEN);
    public static final ImageModelConcept FUNCTION  = conceptShared(SharedImage.SCROLL);
    public static final ImageModelConcept LIST      = conceptShared(SharedImage.LIST_CURTAILED);
    public static final ImageModelConcept KEY_VALUE = conceptLocal("kv.gif");
    public static final ImageModelConcept OPERATOR  = conceptShared(SharedImage.DOT_RED);
    public static final ImageModelConcept UNIT      = conceptShared(SharedImage.DIAL_METER);
    public static final ImageModelConcept VARIABLE  = conceptShared(SharedImage.DIAMOND_3D_PURPLE);
}
