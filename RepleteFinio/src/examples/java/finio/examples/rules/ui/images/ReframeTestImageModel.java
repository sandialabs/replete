package finio.examples.rules.ui.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class ReframeTestImageModel extends ImageModel {
    public static final ImageModelConcept RULE        = conceptShared(SharedImage.DOT_BLUE_SMALL);
    public static final ImageModelConcept RULE_ADD    = conceptLocal("rule-add.gif");
    public static final ImageModelConcept RULE_DIS    = conceptLocal("rule-dis.gif");
    public static final ImageModelConcept RULE_EXCL   = conceptLocal("rule-excl.gif");
    public static final ImageModelConcept RULESET     = conceptShared(SharedImage.CIRCLES_3_MULTI_COLORED);
    public static final ImageModelConcept RULESET_ADD = conceptLocal("ruleset-add.gif");
    public static final ImageModelConcept RULESET_DIS = conceptLocal("ruleset-dis.gif");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
