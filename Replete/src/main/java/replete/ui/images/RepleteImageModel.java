package replete.ui.images;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;


public class RepleteImageModel extends ImageModel {
    public static final ImageModelConcept REPLETE_LOGO      = conceptShared(SharedImage.BOOKS_STACKED);
    public static final ImageModelConcept EVAL_LOGO         = conceptShared(SharedImage.J_EXCLAM);
    public static final ImageModelConcept REGEX             = conceptShared(SharedImage.SUN);
    public static final ImageModelConcept NOFIRE            = conceptShared(SharedImage.CIRCLE_STRIKE_RED);
    public static final ImageModelConcept ERROR_LINE        = conceptShared(SharedImage.EXCLAM_RED);
    public static final ImageModelConcept SOURCE            = conceptShared(SharedImage.PAGE_BRACE);
    public static final ImageModelConcept VALIDATION_ROOT   = conceptShared(SharedImage.DOT_BLUE);
    public static final ImageModelConcept VALIDATION_FRAME  = conceptShared(SharedImage.DOT_GREEN_SMALL);
    public static final ImageModelConcept VALIDATION_ERROR  = conceptShared(SharedImage.EXCLAM_WHITE_CIRCLE_RED);
    public static final ImageModelConcept MATCH             = conceptShared(SharedImage.MATCH);
    public static final ImageModelConcept MATCH_GROUP       = conceptShared(SharedImage.MATCH_GROUP);
    public static final ImageModelConcept NORTH             = conceptShared(SharedImage.TARGET);
    public static final ImageModelConcept EAST              = conceptShared(SharedImage.SUN);
    public static final ImageModelConcept SOUTH             = conceptShared(SharedImage.CHAIN);
    public static final ImageModelConcept WEST              = conceptShared(SharedImage.GLOBE);
    public static final ImageModelConcept CSV_TYPE_GROUP    = conceptShared(SharedImage.DOTS_GRAPH_GREEN);
    public static final ImageModelConcept CSV_TYPE_OTHER    = conceptShared(SharedImage.CIRCLES_3_MULTI_COLORED);
    public static final ImageModelConcept CSV_COL_OTHER     = conceptShared(SharedImage.DOT_GREEN_SMALL);
    public static final ImageModelConcept DATA_TABLE_ROWS   = conceptShared(SharedImage.DATA_TABLE_ROWS);
    public static final ImageModelConcept CLEAR_TEXT        = conceptShared(SharedImage.ERASER);
    public static final ImageModelConcept APPLE             = conceptShared(SharedImage.APPLE);
    public static final ImageModelConcept BANANA            = conceptShared(SharedImage.BANANA);
    public static final ImageModelConcept ORANGE            = conceptShared(SharedImage.ORANGE);
    public static final ImageModelConcept KEY               = conceptShared(SharedImage.KEY_YELLOW);
    public static final ImageModelConcept CLOSE_HOV_DOWN    = conceptShared(SharedImage.X_RED_OFF_CENTER);
    public static final ImageModelConcept PLUGIN_INSTALL    = conceptShared(SharedImage.SOCKET_PLUG_FOLDER);
    public static final ImageModelConcept EXTENSION         = conceptShared(SharedImage.PLUG_YELLOW);
    public static final ImageModelConcept EXTENSION_INVALID = conceptShared(SharedImage.PLUG_YELLOW_X_RED);
    public static final ImageModelConcept EXTENSION_POINT   = conceptShared(SharedImage.SOCKET_BLUE);
    public static final ImageModelConcept PROPERTIES        = conceptShared(SharedImage.DATA_TABLE);
    public static final ImageModelConcept DETAILS_EXPAND    = conceptShared(SharedImage.ARROW_DOWN_YELLOW);  // Non-traditional expand
    public static final ImageModelConcept DETAILS_COLLAPSE  = conceptShared(SharedImage.ARROW_UP_YELLOW);    // Non-traditional collapse
    public static final ImageModelConcept MANUAL_RESULTS_INP  = conceptShared(SharedImage.CIRCLE_ANIMATED);
    public static final ImageModelConcept MANUAL_RESULTS_CMPL = conceptShared(SharedImage.CHECK_MARK_WHITE_CIRCLE_GREEN);
    public static final ImageModelConcept LOG_CODES         = conceptLocal("log-code.gif");
    public static final ImageModelConcept TRACKED_ID        = conceptShared(SharedImage.POUND_CIRCLE_GREEN);
    public static final ImageModelConcept PATTERN_INTERP    = conceptShared(SharedImage.SYMBOLS_4);
    public static final ImageModelConcept JOB_LOADED        = CommonConcepts.COMPUTATION;
    public static final ImageModelConcept JOB_UNLOADED      = conceptShared(SharedImage.COG_GRAY);
    public static final ImageModelConcept JOB_CRASHED       = conceptShared(SharedImage.EXCLAM_YELLOW);
    public static final ImageModelConcept JOB_INVALID       = conceptShared(SharedImage.EXCLAM_RED);
    public static final ImageModelConcept CHECKBOX_ON       = conceptShared(SharedImage.CHECKBOX_ON);
    public static final ImageModelConcept CHECKBOX_OFF      = conceptShared(SharedImage.CHECKBOX_OFF);
    public static final ImageModelConcept CHECKBOX_HALF     = conceptShared(SharedImage.CHECKBOX_HALF);


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize();
    }
}
