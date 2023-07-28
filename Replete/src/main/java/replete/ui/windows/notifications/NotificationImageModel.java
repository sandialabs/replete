package replete.ui.windows.notifications;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class NotificationImageModel extends ImageModel {
    public static final ImageModelConcept PAUSE_REQUESTED = conceptShared(SharedImage.PAUSE_BLUE);
    public static final ImageModelConcept GARBAGE_COLLECT = conceptShared(SharedImage.TRASH_CAN);
    public static final ImageModelConcept DURATION        = conceptShared(SharedImage.CLOCK_LIGHT_BLUE);
    public static final ImageModelConcept TRACE           = conceptShared(SharedImage.TITLED_TEXT_PANE);
    public static final ImageModelConcept EXPAND          = conceptShared(SharedImage.ARROW_UP_YELLOW);    // Non-traditional expand
    public static final ImageModelConcept COLLAPSE        = conceptShared(SharedImage.ARROW_DOWN_YELLOW);  // Non-traditional collapse
    public static final ImageModelConcept GOTO            = CommonConcepts.NEXT;
}
