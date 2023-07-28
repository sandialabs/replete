package replete.ui.sdplus.images;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class SdPlusImageModel extends ImageModel {
    public static final ImageModelConcept OPTIONS_EXPAND      = CommonConcepts.PLAY;
    public static final ImageModelConcept OPTIONS_COLLAPSE    = conceptShared(SharedImage.ARROW_DOWN_RED);
    public static final ImageModelConcept COLORS              = conceptShared(SharedImage.CIRCLES_3_MULTI_COLORED);
    public static final ImageModelConcept SP_CONT             = conceptLocal("sp-cont.gif");
    public static final ImageModelConcept SP_CONT_PLACEHOLDER = conceptLocal("sp-cont-alt.png");
    public static final ImageModelConcept SP_ENUM             = conceptShared(SharedImage.SHAPES_4_MULTI_COLORED);
    public static final ImageModelConcept SP_ENUM_PLACEHOLDER = conceptLocal("sp-enum-alt.gif");
    public static final ImageModelConcept SP_DATE_PLACEHOLDER = conceptLocal("sp-date-alt.gif");
    public static final ImageModelConcept SP_LONG             = conceptLocal("sp-long.gif");
    public static final ImageModelConcept SP_BASE             = conceptShared(SharedImage.DOT_GREEN_SMALL);
    public static final ImageModelConcept SEL_ON              = conceptLocal("sel-on.gif");
    public static final ImageModelConcept SEL_OFF             = conceptLocal("sel-off.gif");
    public static final ImageModelConcept TBL_GOTO            = conceptLocal("tbl-goto.gif");
    public static final ImageModelConcept TBL_HIDE            = conceptLocal("tbl-hide.gif");
    public static final ImageModelConcept VIZ_MARK_X          = conceptLocal("viz-mark-x.gif");
    public static final ImageModelConcept VIZ_MARK_Y          = conceptLocal("viz-mark-y.gif");
    public static final ImageModelConcept VIZ_X               = conceptLocal("viz-x.gif");
    public static final ImageModelConcept VIZ_Y               = conceptLocal("viz-y.gif");
    public static final ImageModelConcept VIZ_COLOR           = conceptLocal("viz-color.gif");
    public static final ImageModelConcept VIZ_SHAPE           = conceptShared(SharedImage.DIAMOND_3D_BLUE);
    public static final ImageModelConcept GROUP_OPEN          = conceptShared(SharedImage.PACKAGES_BROWN);
}
