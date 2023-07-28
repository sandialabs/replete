package gotstyle.ui.images;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class GotStyleImageModel extends ImageModel {

    public static final ImageModelConcept GOTSTYLE_APP = conceptLocal("style.png");
    public static final ImageModelConcept EXAMPLES     = conceptLocal("ex.gif");
    public static final ImageModelConcept PAGES        = conceptLocal("pg.gif");

    public static final ImageModelConcept TOGGLE_NAV   = conceptLocal("toggle-nav.gif");
    public static final ImageModelConcept TOGGLE_CODE  = conceptLocal("toggle-code.gif");
    public static final ImageModelConcept CHANGE_TITLE = CommonConcepts.RENAME;
    public static final ImageModelConcept CHANGE_MSG   = CommonConcepts.RENAME;
    public static final ImageModelConcept CHANGE_EX_ST = conceptShared(SharedImage.DOT_BLUE);
    public static final ImageModelConcept CHANGE_PG_ST = conceptShared(SharedImage.DOT_BLUE_SMALL);
    public static final ImageModelConcept DEINDENT     = CommonConcepts.DEINDENT;
    public static final ImageModelConcept INDENT       = CommonConcepts.INDENT;

    public static final ImageModelConcept SEL_EX_FS    = conceptShared(SharedImage.ARROW_UP_YELLOW_BAR_BLUE);
    public static final ImageModelConcept SEL_EX_PR    = conceptShared(SharedImage.ARROW_UP_YELLOW);
    public static final ImageModelConcept SEL_EX_NX    = conceptShared(SharedImage.ARROW_DOWN_YELLOW);
    public static final ImageModelConcept SEL_EX_LS    = conceptShared(SharedImage.ARROW_DOWN_YELLOW_BAR_BLUE);

    public static final ImageModelConcept IMAGE_SET    = conceptShared(SharedImage.PAGE_PICTURE);
    public static final ImageModelConcept IMAGE_CLEAR  = conceptShared(SharedImage.PAGE_PICTURE_BW);

    public static final ImageModelConcept SEL_PG_FS    = conceptLocal("select-pg-home.gif");
    public static final ImageModelConcept SEL_PG_PR    = conceptLocal("select-pg-prev.gif");
    public static final ImageModelConcept SEL_PG_NX    = conceptLocal("select-pg-next.gif");
    public static final ImageModelConcept SEL_PG_LS    = conceptLocal("select-pg-end.gif");

    public static final ImageModelConcept INS_EX_BF    = conceptLocal("insert-ex-before.gif");
    public static final ImageModelConcept INS_EX_AF    = conceptLocal("insert-ex-after.gif");
    public static final ImageModelConcept INS_PG_BF    = conceptLocal("insert-pg-before.gif");
    public static final ImageModelConcept INS_PG_AF    = conceptLocal("insert-pg-after.gif");

    public static final ImageModelConcept MOVE_EX_UP   = conceptShared(SharedImage.ARROW_UP_YELLOW_BAR_BLUE);
    public static final ImageModelConcept MOVE_EX_DN   = conceptShared(SharedImage.ARROW_DOWN_YELLOW_BAR_BLUE);
    public static final ImageModelConcept MOVE_PG_LF   = conceptLocal("select-pg-home.gif");
    public static final ImageModelConcept MOVE_PG_RT   = conceptLocal("select-pg-end.gif");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
