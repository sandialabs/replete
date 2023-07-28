package replete.scrutinize.archive.app2.images;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class ScrutinizeImageModel extends ImageModel {
    public static final ImageModelConcept SCRUTINIZE_ICON = conceptShared(SharedImage.MAGNIFYING_GLASS);
    public static final ImageModelConcept LOCAL           = conceptShared(SharedImage.COMPUTER_MONITOR);
    public static final ImageModelConcept LOOK_LEFT_ARROW = conceptLocal("arrow-left-blue-large.png");
}
