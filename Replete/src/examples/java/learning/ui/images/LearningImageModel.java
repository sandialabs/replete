package learning.ui.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;

public class LearningImageModel extends ImageModel {

    public static final ImageModelConcept ECLIPSE_LOGO = conceptLocal("eclipse-icon.gif");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
