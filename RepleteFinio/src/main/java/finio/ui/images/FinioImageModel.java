package finio.ui.images;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class FinioImageModel extends ImageModel {

    // Re-order sometime if desired
    public static final ImageModelConcept CONFIG_HIDE           = conceptShared(SharedImage.COG_YELLOW_TRANSP);
    public static final ImageModelConcept LAYOUT                = conceptShared(SharedImage.WINDOW_TITLE_BAR_SEGMENTED);
    public static final ImageModelConcept ACTIONABLE            = conceptLocal("actionable.gif");
    public static final ImageModelConcept ALT_MAP               = conceptLocal("alt-map.gif");           // Not yet used, but possibly useful icon
    public static final ImageModelConcept ANCHOR                = conceptLocal("anchor.png");            // Icon might be generally useful, Replete-quality
    public static final ImageModelConcept LIST_SEPARATOR        = conceptLocal("arrow-right-blue.gif");  // Is there chance for Replete-level consistency here?
    public static final ImageModelConcept BYTE_STR_LOGO         = conceptLocal("binary-mag.gif");        // Byte Stream Inspector Logo
    public static final ImageModelConcept LARGE_BINOCS_EMPH     = conceptLocal("large-binocs-emph.png");
    public static final ImageModelConcept LARGE_BINOCS          = conceptLocal("large-binocs.png");      // Also: large-binocs.pdn
    public static final ImageModelConcept SYNTAX                = conceptShared(SharedImage.BRACES_PAIR);
    public static final ImageModelConcept NT_CLEAR              = conceptLocal("nt-clear.gif");
    public static final ImageModelConcept CHANGED_CHECK         = conceptLocal("checkmark.png");
    public static final ImageModelConcept NT_COMBINE            = conceptLocal("nt-combine.gif");
    public static final ImageModelConcept NT_DIFF               = conceptLocal("nt-combine.gif");        // Needs new icon
    public static final ImageModelConcept CVT_MAP_STR           = conceptLocal("cvt-map-str.gif");
    public static final ImageModelConcept IMPORT_COMP_HIER      = conceptShared(SharedImage.WINDOW_TITLE_BAR_SEGMENTED);
    public static final ImageModelConcept IMPORT_SCRUTZ         = CommonConcepts.SYSTEM;
    public static final ImageModelConcept DESCRIBE_OBJ          = conceptLocal("desc-obj.gif");          // Not sure if best icon for this...
    public static final ImageModelConcept DESKTOP_MODE          = conceptLocal("desktop-mode.gif");
    public static final ImageModelConcept MORE_OPTIONS          = conceptLocal("options-earmark.gif");   // Something similar being used in other projects (a thin "details" icon)
    public static final ImageModelConcept EDIT_ACTIVE           = conceptLocal("edit-active.gif");
    public static final ImageModelConcept SET_IMAGE             = conceptShared(SharedImage.SHAPES_4_MULTI_COLORED);
    public static final ImageModelConcept EXAMPLE_DATA          = conceptLocal("example-data.gif");
    public static final ImageModelConcept EXPAND_WORLD          = conceptLocal("expand-world.gif");
    public static final ImageModelConcept SET_FILE              = conceptLocal("set-file.gif");
    public static final ImageModelConcept SET_DIR               = conceptLocal("set-dir.gif");
    public static final ImageModelConcept FINIO_LOGO            = conceptLocal("finio-logo.gif");        // Also: finio-logo.pdn
    public static final ImageModelConcept FINIO_LOGO_BIG        = conceptLocal("finio-logo-big.gif");
    public static final ImageModelConcept FINIO_TEXT            = conceptLocal("finio-text.png");        // Also: finio-text.pdn
    public static final ImageModelConcept FLATTEN               = conceptLocal("flatten.gif");
    public static final ImageModelConcept FONT_DEC              = conceptLocal("font-dec.png");          // Non-standard (non-Replete) font inc/dec icons
    public static final ImageModelConcept FONT_INC              = conceptLocal("font-inc.png");
    public static final ImageModelConcept FONT_COLOR            = conceptLocal("font-color.png");
    public static final ImageModelConcept HINGE                 = conceptLocal("hinge.gif");
    public static final ImageModelConcept HINGE_SML             = conceptLocal("hinge-sml.gif");
    public static final ImageModelConcept JSON                  = CommonConcepts.JSON;                   // Could use conceptShared(SharedImage.SCROLL); as well
    public static final ImageModelConcept LOAD                  = conceptShared(SharedImage.CYLINDER_YELLOW);
    public static final ImageModelConcept UNLOAD                = conceptShared(SharedImage.CYLINDER_GRAY);
    public static final ImageModelConcept METAMAP               = conceptLocal("metamap.gif");
    public static final ImageModelConcept METAMAP_DISABLED      = conceptLocal("metamap-disabled.gif");  // Why is this a separate icon?
    public static final ImageModelConcept METAMAP_EMPTY         = conceptLocal("metamap-empty.gif");     // Also: metamap-empty.pdn
    public static final ImageModelConcept METAMAP_PAUSED        = conceptLocal("metamap-paused.gif");
    public static final ImageModelConcept METAMAP_TOGGLE        = conceptLocal("metamap-toggle.gif");
    public static final ImageModelConcept MANAGEMENT            = conceptLocal("management.gif");
    public static final ImageModelConcept WINDOW_NEW            = conceptLocal("window-new.gif");
    public static final ImageModelConcept NT_MAP                = conceptShared(SharedImage.DOT_BLUE);
    public static final ImageModelConcept NT_MAP_EMPTY          = conceptShared(SharedImage.DOT_BLUE_EMPTY); // Also: nt-map-empty.pdn
    public static final ImageModelConcept NODE_INFO             = CommonConcepts.INFO;
    public static final ImageModelConcept NT_MAP_DESCRIBE       = conceptLocal("nt-map-describe.gif");
    public static final ImageModelConcept NT_MAP_DESCRIBE_P     = conceptLocal("nt-map-describe-p.gif");
    public static final ImageModelConcept NT_MAP_SIMPLIFIED     = conceptLocal("nt-map-simplified.gif");
    public static final ImageModelConcept NT_MAP_PAUSED         = conceptLocal("nt-map-paused.gif");
    public static final ImageModelConcept NT_LIST               = conceptLocal("nt-list.gif");
    public static final ImageModelConcept NT_LISTMAP            = conceptLocal("nt-listmap.gif");
    public static final ImageModelConcept NT_MANAGED_MAP        = conceptLocal("nt-managed-map.gif");
    public static final ImageModelConcept NT_MANAGED_MAP_P      = conceptLocal("nt-managed-map-p.gif");
    public static final ImageModelConcept NT_MANAGED_MAP_U      = conceptLocal("nt-managed-map-u.gif");
    public static final ImageModelConcept OPAQUE_TOGGLE         = conceptLocal("opaque-toggle.gif");     // Not used yet! But nice icon
    public static final ImageModelConcept OPAQUE_ON             = conceptLocal("opaque-on.gif");
    public static final ImageModelConcept OPAQUE_OFF            = conceptLocal("opaque-off.gif");
    public static final ImageModelConcept VIEW                  = conceptLocal("view.gif");
    public static final ImageModelConcept VIEW_CONFIG           = conceptLocal("view-config.gif");
    public static final ImageModelConcept REALM                 = conceptShared(SharedImage.CIRCLES_3_MULTI_COLORED);
    public static final ImageModelConcept REALM_MANAGED         = conceptLocal("realm-managed.gif");
    public static final ImageModelConcept REALM_MANAGED_U       = conceptLocal("realm-managed-u.gif");
    public static final ImageModelConcept SIMPLIFY              = conceptLocal("simplify.gif");
    public static final ImageModelConcept PAINT                 = conceptLocal("paint.gif");             // Slightly modified from standard paint can
    public static final ImageModelConcept PICTURE_SQUARE        = conceptShared(SharedImage.PICTURE_SQUARE);
    public static final ImageModelConcept PROMOTE               = conceptLocal("promote.gif");
    public static final ImageModelConcept STATUS_BAR            = conceptLocal("status-bar.gif");
    public static final ImageModelConcept TABS                  = conceptLocal("tabs.gif");
    public static final ImageModelConcept TABS_DOWN             = conceptLocal("tabs-down.gif");
    public static final ImageModelConcept TABS_LEFT             = conceptLocal("tabs-left.gif");
    public static final ImageModelConcept TABS_RIGHT            = conceptLocal("tabs-right.gif");
    public static final ImageModelConcept TABS_UP               = conceptLocal("tabs-up.gif");
    public static final ImageModelConcept SPLIT                 = conceptLocal("split.gif");
    public static final ImageModelConcept SELCHILD              = conceptLocal("selchild.gif");
    public static final ImageModelConcept SELPARENT             = conceptLocal("selparent.gif");
    public static final ImageModelConcept SELROOT               = conceptLocal("selroot.gif");
    public static final ImageModelConcept SELSIB                = conceptLocal("selsib.gif");
    public static final ImageModelConcept TRIVIAL_MANAGEMENT    = conceptLocal("trivial-management.gif");
    public static final ImageModelConcept UNK_OBJ               = conceptLocal("unk-obj.gif");           // Not used yet but interesting icon
    public static final ImageModelConcept WORKING_SCOPE         = conceptLocal("working-scope.gif");
    public static final ImageModelConcept TC_HORIZ              = conceptLocal("tc-horiz.gif");
    public static final ImageModelConcept TC_VERT               = conceptLocal("tc-vert.gif");
    public static final ImageModelConcept SELECTED              = conceptLocal("selected.gif");
    public static final ImageModelConcept SEND                  = conceptLocal("send.png");
    public static final ImageModelConcept TERMINAL              = conceptLocal("terminal.gif");
    public static final ImageModelConcept TERMINAL_META         = conceptLocal("terminal-meta.gif");
    public static final ImageModelConcept TERMINAL_EXPAND       = conceptLocal("terminal-expand.gif");
    public static final ImageModelConcept TERMINAL_EXPANDABLE   = conceptLocal("terminal-expandable.gif");
    public static final ImageModelConcept TERMINAL_BINARY       = conceptLocal("terminal-binary.gif");
    public static final ImageModelConcept TERMINAL_BINARY_META  = conceptLocal("terminal-binary-meta.gif");
    public static final ImageModelConcept TERMINAL_MANAGED      = conceptLocal("terminal-managed.gif");
    public static final ImageModelConcept TERMINAL_MANAGED_NT   = conceptLocal("terminal-managed-nt.gif");
    public static final ImageModelConcept TERMINAL_UNEXPANDABLE = conceptLocal("terminal-unexpandable.gif");
    public static final ImageModelConcept TERMINAL_CONVERT      = conceptLocal("terminal-convert.gif");
    public static final ImageModelConcept TRANSFORM             = conceptLocal("transform.gif");
    public static final ImageModelConcept TEXTONLY_VIEW         = conceptLocal("textonly-view.gif");
    public static final ImageModelConcept TREE_VIEW             = conceptLocal("tree-view.gif");
    public static final ImageModelConcept SELECTION_TRANSFER    = conceptLocal("selection-transfer.gif");
    public static final ImageModelConcept SELECTION_ACTION      = conceptLocal("selection-action.gif");
    public static final ImageModelConcept OPEN_FOLDER           = conceptLocal("open-big-2.png");
    public static final ImageModelConcept OPEN_FOLDER_EMPH      = conceptLocal("open-big-emph.png");
    public static final ImageModelConcept NEW_STAR              = conceptLocal("star-big-2.png");
    public static final ImageModelConcept NEW_STAR_EMPH         = conceptLocal("star-big-emph.png");     // Also: star-big-emph.pdn
    public static final ImageModelConcept RESUME_PLAY           = conceptLocal("play-big-2.png");
    public static final ImageModelConcept RESUME_PLAY_EMPH      = conceptLocal("play-big-emph.png");     // Also: play-big.pdn
    public static final ImageModelConcept POJM                  = conceptLocal("pojm.gif");              // Need better names
    public static final ImageModelConcept POJO                  = conceptLocal("pojo.gif");              // Need better names
    public static final ImageModelConcept UI_HIERARCHY          = conceptShared(SharedImage.WINDOW_TITLE_BAR_COMPONENTS);
    public static final ImageModelConcept REENTER               = conceptLocal("reenter.gif");


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
