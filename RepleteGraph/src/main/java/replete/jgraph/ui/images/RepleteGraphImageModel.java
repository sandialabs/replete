package replete.jgraph.ui.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class RepleteGraphImageModel extends ImageModel {

    public static final ImageModelConcept ARROW_DIAG_DL = conceptLocal("arrow-diag-dl.gif");
    public static final ImageModelConcept ARROW_DIAG_DR = conceptLocal("arrow-diag-dr.gif");
    public static final ImageModelConcept ARROW_DIAG_UL = conceptLocal("arrow-diag-ul.gif");
    public static final ImageModelConcept ARROW_DIAG_UR = conceptLocal("arrow-diag-ur.gif");
    public static final ImageModelConcept HAND_GRAB     = conceptShared(SharedImage.HAND_GRAB);
    public static final ImageModelConcept HAND_OPEN     = conceptShared(SharedImage.HAND_OPEN);
    public static final ImageModelConcept CHECK         = conceptLocal("check.png");
    public static final ImageModelConcept DIRTY         = conceptLocal("dirty.png");    // Nice icons, move to Replete someday
    public static final ImageModelConcept ERROR         = conceptLocal("error.png");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
