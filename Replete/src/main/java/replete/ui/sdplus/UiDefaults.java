package replete.ui.sdplus;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.sdplus.images.SdPlusImageModel;


/**
 * Holds default UI settings for both the individual scale panels
 * and the scale set panel.
 *
 * @author Derek Trumbo
 */

public class UiDefaults {

    // ScaleSetPanel only
    public static final Font SSP_SEARCH_FONT = new Font(null, Font.PLAIN, 10);
    public static final boolean SSP_COALESCE_EVENTS = false;
    public static final Color SSP_SCALE_AREA_COLOR = null;  // Can use null to mean use default BG color.

    // Most ScalePanel's
    public static final Font SP_TITLE_FONT = new Font(null, Font.BOLD, 12);
    public static final Font SP_TITLE_COUNTS_FONT = new Font(null, Font.PLAIN, 12);
    public static final Font SP_NOTES_FONT = new Font(null, Font.PLAIN, 10);
    public static final Font SP_FILTER_FONT = new Font(null, Font.PLAIN, 12);
    public static final Color SP_BACKGROUND_COLOR = null;  // Can use null to mean use default BG color.
    public static final Color SP_BORDER_COLOR = Color.black;
    public static final Color SP_SUBSELECTED_BORDER_COLOR = new Color(90, 230, 220);
    public static final Color SP_HIGHLIGHT_COLOR = new Color(255, 255, 160);
    public static final int SP_TITLE_MARGIN = 3;
    public static final ImageIcon SP_BASE_ICON = ImageLib.get(SdPlusImageModel.SP_BASE);
    public static final boolean SP_SHOW_TITLE_COUNTS = true;
    public static final boolean SP_POPUP_SECTION_LABELS = true;
    public static final int SP_OUTER_MARGIN = 3;
    public static final int SP_INNER_SPACING = 3;

    // EnumScale*Panel's
    public static final ImageIcon ENUM_ICON = ImageLib.get(SdPlusImageModel.SP_ENUM);
    public static final boolean ENUM_COALESCE_EVENTS = false;
    public static final boolean ENUM_SHOW_VALUE_COUNTS = true;

    // ContScalePanel's
    public static final ImageIcon CONT_ICON = ImageLib.get(SdPlusImageModel.SP_CONT);
    public static final Color CONT_EDITED_COLOR = new Color(153, 238, 232);
    public static final Color CONT_ERROR_COLOR = new Color(255, 127, 127);

    // LongScalePanel's
    public static final ImageIcon LONG_ICON = ImageLib.get(SdPlusImageModel.SP_LONG);

    // DateScalePanel's
    public static final ImageIcon DATE_ICON = ImageLib.get(CommonConcepts.DATE_TIME);

    // GroupPanel's
    public static final Font GRP_TITLE_FONT = new Font(null, Font.BOLD, 12);
    public static final Font GRP_NOTES_FONT = new Font(null, Font.PLAIN, 10);
    public static final int GRP_TITLE_MARGIN = 3;
    public static final int GRP_INDENT = 10;
    public static final ImageIcon GRP_OPEN_ICON = ImageLib.get(SdPlusImageModel.GROUP_OPEN);
    public static final ImageIcon GRP_CLOSED_ICON = ImageLib.get(CommonConcepts.PACKAGE);
    public static final Color GRP_BACKGROUND_COLOR = new Color(238, 243, 238);
}
