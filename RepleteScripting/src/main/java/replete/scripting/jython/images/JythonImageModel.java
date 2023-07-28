package replete.scripting.jython.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;


public class JythonImageModel extends ImageModel {
    public static final ImageModelConcept JYTHON_LOGO = conceptLocal("jython-icon.png");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize();
    }
}
