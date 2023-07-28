package replete.ui.images.shared;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import replete.hash.Md5Util;
import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.util.OsUtil;

// This enum represents all of the images in the
// replete.ui.images.shared package.

// EVERY time an image is added to this package,
// a corresponding value must be added to this enum.

// The main method below, when run, will detect if
// any value has an inappropriate file name (a file
// name with no corresponding image file).

// Extreme alignment used here for readability / typo
// detection.  This kind of alignment should not be
// considered standard for other enums.

// It is not a requirement for every image listed below
// to be used in some piece of software somewhere.  This
// list can also be used to store icons that *could*
// potentially be useful down the line.  This is acceptable
// because at least all the extra icons are centralized
// to this single source due to the new image model concept
// framework.

public enum SharedImage {


    ////////////
    // IMAGES //
    ////////////

    // Mini icons used good for decoration
    ARROW_RIGHT_GREEN_NO_TAIL_MINI      ("arrow-right-green-no-tail-mini.gif"),
    CHECK_MARK_GREEN_MINI               ("check-mark-green-mini.gif"),
    CLOCK_LIGHT_BLUE_MINI               ("clock-light-blue-mini.gif"),
    PAUSE_YELLOW_MINI                   ("pause-yellow-mini.gif"),
    SQUARE_RED_MINI                     ("square-red-mini.gif"),
    STAR_YELLOW_MINI                    ("star-yellow-mini.gif"),
    X_WHITE_SQUARE_RED_MINI             ("x-white-square-red-mini.gif"),

    A_ARROW_DOWN                        ("a-arrow-down.png"),
    A_ARROW_UP                          ("a-arrow-up.png"),
    A_COMMA_E_BARS                      ("a-comma-e-bars.gif"),
    A_TEXT_CURSOR_E                     ("a-text-cursor-e.gif"),
    A_Z_ARROW_DOWN                      ("a-z-arrow-down.gif"),
    APPLE                               ("apple.gif"),
    ARROW_BENT_GREEN                    ("arrow-bent-green.gif"),
    ARROW_COUNTER_CW_BLUE               ("arrow-counter-cw-blue.png"),
    ARROW_COUNTER_CW_BLUE_FADED         ("arrow-counter-cw-blue-faded.png"),
    ARROW_DOWN_RED                      ("arrow-down-red.gif"),
    ARROW_DOWN_YELLOW                   ("arrow-down-yellow.gif"),
    ARROW_DOWN_YELLOW_BAR_BLUE          ("arrow-down-yellow-bar-blue.gif"),
    ARROW_DOWN_YELLOW_BROKEN_TAIL       ("arrow-down-yellow-broken-tail.gif"),
    ARROW_LEFT_BLUE                     ("arrow-left-blue.png"),
    ARROW_LEFT_BLUE_LIGHT               ("arrow-left-blue-light.gif"),
    ARROW_LEFT_BLUE_CIRCLE              ("arrow-left-blue-circle.gif"),
    ARROW_LEFT_BLUE_SMALL               ("arrow-left-blue-small.gif"),
    ARROW_LEFT_CURVE_RED                ("arrow-left-curve-red.gif"),
    ARROW_LEFT_CURVE_YELLOW             ("arrow-left-curve-yellow.gif"),
    ARROW_LEFT_GREEN                    ("arrow-left-green.png"),
    ARROW_LEFT_RED                      ("arrow-left-red.gif"),
    ARROW_LEFT_YELLOW                   ("arrow-left-yellow.gif"),
    ARROW_RIGHT_BLUE                    ("arrow-right-blue.png"),
    ARROW_RIGHT_BLUE_CIRCLE             ("arrow-right-blue-circle.gif"),
    ARROW_RIGHT_BLUE_SMALL              ("arrow-right-blue-small.gif"),
    ARROW_RIGHT_CURVE_YELLOW            ("arrow-right-curve-yellow.gif"),
    ARROW_RIGHT_GREEN                   ("arrow-right-green.png"),
    ARROW_RIGHT_GREEN_LIGHT             ("arrow-right-green-light.gif"),
    ARROW_RIGHT_GREEN_SMALL             ("arrow-right-green-small.gif"),
    ARROW_RIGHT_GREEN_NO_TAIL           ("arrow-right-green-no-tail.gif"),
    ARROW_RIGHT_RED_DOOR_OPEN           ("arrow-right-red-door-open.gif"),
    ARROW_RIGHT_RED_ORANGE              ("arrow-right-red-orange.gif"),
    ARROW_RIGHT_YELLOW                  ("arrow-right-yellow.gif"),
    ARROW_UP_YELLOW                     ("arrow-up-yellow.gif"),
    ARROW_UP_YELLOW_BAR_BLUE            ("arrow-up-yellow-bar-blue.gif"),
    ARROW_UP_YELLOW_BROKEN_TAIL         ("arrow-up-yellow-broken-tail.gif"),
    ARROW_WHITE_CIRCLE_GREEN            ("arrow-white-circle-green.gif"),
    ARROW_WHITE_CIRCLE_GREEN_PLUS_YELLOW("arrow-white-circle-green-plus-yellow.gif"),
    ARROWS_LEFT_BLUE_LIGHT              ("arrows-left-blue-light.gif"),
    ARROWS_LEFT_RED                     ("arrows-left-red.gif"),
    ARROWS_OPPOSITE_YELLOW              ("arrows-opposite-yellow.gif"),
    ARROWS_REVOLVING                    ("arrows-revolving.gif"),
    ARROWS_REVOLVING_EXCLAM_RED         ("arrows-revolving-exclam-red.gif"),
    ARROWS_RIGHT_GREEN_LIGHT            ("arrows-right-green-light.gif"),
    ARROWS_RIGHT_GREEN_SMALL            ("arrows-right-green-small.gif"),
    ARROWS_UP_DOWN_RED                  ("arrows-up-down-red.gif"),
    AT_SIGN                             ("at-sign.gif"),
    AT_SIGN_SMALL                       ("at-sign-small.gif"),
    BANANA                              ("banana.gif"),
    BAR_YELLOW_ARROW_GREEN              ("bar-yellow-arrow-green.gif"),
    BARS_PARALLEL_BLUE                  ("bars-parallel-blue.gif"),
    BEAKER                              ("beaker.png"),
    BINOCULARS_BLACK                    ("binoculars-black.gif"),
    BINOCULARS_BLACK_FADED              ("binoculars-black-faded.gif"),
    BINOCULARS_GREEN                    ("binoculars-green.gif"),
    BINOCULARS_ORANGE                   ("binoculars-orange.gif"),
    BOOK_CLOSED                         ("book-closed.gif"),
    BOOK_OPEN                           ("book-open.gif"),
    BOOK_OPEN_PENCIL                    ("book-open-pencil.gif"),
    BOOK_OPEN_BOOKMARK_BLUE             ("book-open-bookmark-blue.gif"),
    BOOK_OPENING                        ("book-opening.gif"),
    BOOKMARK_BLUE                       ("bookmark-blue.gif"),
    BOOKS_STACKED                       ("books-stacked.gif"),
    BOX_ARROW_INWARD_BLUE               ("box-arrow-inward-blue.gif"),
    BOX_ARROW_OUTWARD_BLUE              ("box-arrow-outward-blue.gif"),
    BRACES_PAIR                         ("braces-pair.gif"),
    BRICKS_3_COLORED                    ("bricks-3-colored.png"),
    C_WHITE_CIRCLE_GREEN                ("c-white-circle-green.gif"),
    C_WHITE_CIRCLE_GRAY                 ("c-white-circle-gray.gif"),
    CHAIN                               ("chain.gif"),
    CHART_BAR                           ("chart-bar.png"),
    CHART_PIE                           ("chart-pie.png"),
    CHAIN_ELLIPSIS                      ("chain-ellipsis.gif"),
    CHECKBOX_HALF                       ("checkbox-half.gif"),
    CHECKBOX_OFF                        ("checkbox-off.gif"),
    CHECKBOX_ON                         ("checkbox-on.gif"),
    CHECK_MARK_GREEN                    ("check-mark-green.gif"),
    CHECK_MARK_GREEN_THICK              ("check-mark-green-thick.png"),
    CHECK_MARK_WHITE_CIRCLE_GREEN       ("check-mark-white-circle-green.gif"),
    CHEVRON_DOWN_BLUE                   ("chevron-down-blue.gif"),
    CHEVRON_LEFT_BLUE                   ("chevron-left-blue.gif"),
    CHEVRON_RIGHT_BLUE                  ("chevron-right-blue.gif"),
    CHEVRON_UP_BLUE                     ("chevron-up-blue.gif"),
    CIRCLE_ANIMATED                     ("circle-animated.gif"),
    CIRCLE_ANIMATED_BIG                 ("circle-animated-big.gif"),
    CIRCLE_BLUE_BLACK_BORDER            ("circle-blue-black-border.png"),
    CIRCLE_STRIKE_RED                   ("circle-strike-red.gif"),
    CIRCLES_3_GROUPED                   ("circles-3-grouped.gif"),
    CIRCLES_3_GROUPED_NEW               ("circles-3-grouped-new.gif"),            // Could be removed using image stacking
    CIRCLES_3_MULTI_COLORED             ("circles-3-multi-colored.gif"),
    CIRCLES_BLUE_EDGES                  ("circles-blue-edges.gif"),
    CIRCLES_GREEN_EDGES_RECT            ("circles-green-edges-rect.gif"),
    CIRCLES_RED_EDGE_BROKEN             ("circles-red-edge-broken.gif"),
    CLIPBOARD_PAGE                      ("clipboard-page.gif"),
    CLIPBOARD_PAGE_OFFSET               ("clipboard-page-offset.gif"),
    CLOCK_LIGHT_BLUE                    ("clock-light-blue.gif"),
    CLOCK_LIGHT_BLUE_ARROW_GREEN        ("clock-light-blue-arrow-green.gif"),     // Could be removed using image stacking
    CLOCK_LIGHT_BLUE_PAUSE_YELLOW       ("clock-light-blue-pause-yellow.gif"),    // Could be removed using image stacking
    CLOCK_DARK_BLUE                     ("clock-dark-blue.gif"),
    COG_BLUE_ARROW_RIGHT_GREEN_NO_TAIL  ("cog-blue-arrow-right-green-no-tail.gif"),
    COG_BLUE_PAUSE_YELLOW               ("cog-blue-pause-yellow.gif"),
    COG_BLUE_PLUS_YELLOW                ("cog-blue-plus-yellow.gif"),
    COG_BLUE_RECT_GRAY                  ("cog-blue-rect-gray.gif"),
    COG_BLUE_SQUARE_RED                 ("cog-blue-square-red.gif"),
    COG_BLUE_X_WHITE_SQUARE_RED         ("cog-blue-x-white-square-red.gif"),
    COG_GRAY                            ("cog-gray.gif"),
    COG_RED_PADLOCK_RED                 ("cog-red-padlock-red.gif"),
    COG_RED_ARROW_RIGHT_GREEN_NO_TAIL   ("cog-red-arrow-right-green-no-tail.gif"),
    COG_RED_RECT_GRAY                   ("cog-red-rect-gray.gif"),
    COG_YELLOW                          ("cog-yellow.gif"),
    COG_YELLOW_ARROW_RIGHT_GREEN_NO_TAIL("cog-yellow-arrow-right-green-no-tail.gif"),
    COG_YELLOW_PAUSE_YELLOW             ("cog-yellow-pause-yellow.gif"),
    COG_YELLOW_PLUS_YELLOW              ("cog-yellow-plus-yellow.gif"),
    COG_YELLOW_RECT_GRAY                ("cog-yellow-rect-gray.gif"),
    COG_YELLOW_SQUARE_RED               ("cog-yellow-square-red.gif"),
    COG_YELLOW_TRANSP                   ("cog-yellow-transp.gif"),
    COG_YELLOW_X_WHITE_SQUARE_RED       ("cog-yellow-x-white-square-red.gif"),
    COGS_YELLOW_ROTATING                ("cogs-yellow-rotating.gif"),
    COMPUTER                            ("computer.png"),
    COMPUTER_MONITOR                    ("computer-monitor.gif"),
    CONSOLE_WINDOW_BLACK                ("console-window-black.gif"),
    CUBE_GREEN                          ("cube-green.png"),
    CUBES_2_GREEN                       ("cubes-2-green.png"),
    CYLINDER_BLUE                       ("cylinder-blue.gif"),
    CYLINDER_BLUE_ARROW_YELLOW          ("cylinder-blue-arrow-yellow.gif"),
    CYLINDER_GRAY                       ("cylinder-gray.gif"),
    CYLINDER_YELLOW                     ("cylinder-yellow.gif"),
    CYLINDERS_3_YELLOW                  ("cylinders-3-yellow.gif"),
    DATA_TABLE                          ("data-table.gif"),
    DATA_TABLE_COLS                     ("data-table-cols.gif"),
    DATA_TABLE_ROWS                     ("data-table-rows.gif"),
    DATE_TIME                           ("date-time.gif"),
    DELTA                               ("delta.gif"),
    DEVICES_NETWORK                     ("devices-network.png"),
    DIAL_METER                          ("dial-meter.gif"),
    DIAMOND_3D_BLUE                     ("diamond-3d-blue.gif"),
    DIAMOND_3D_PURPLE                   ("diamond-3d-purple.gif"),
    DIAMOND_3D_RED                      ("diamond-3d-red.gif"),
    DIAMOND_3D_RED_WINDOW_TITLE_BAR     ("diamond-3d-red-window-title-bar.gif"),
    DIAMOND_GEM                         ("diamond-gem.png"),
    DIAMOND_YELLOW                      ("diamond-yellow.gif"),
    DICE                                ("dice.gif"),
    DIR_HIER                            ("dir-hier.gif"),
    DISK_DRIVE_GRAY                     ("disk-drive-gray.gif"),
    DOT_BLUE                            ("dot-blue.gif"),
    DOT_BLUE_EMPTY                      ("dot-blue-empty.gif"),
    DOT_RED_EMPTY                       ("dot-red-empty.gif"),
    DOT_BLUE_SMALL                      ("dot-blue-small.gif"),
    DOT_GREEN                           ("dot-green.gif"),
    DOT_GREEN_SMALL                     ("dot-green-small.gif"),
    DOT_PURPLE                          ("dot-purple.gif"),
    DOT_RED                             ("dot-red.gif"),
    DOTS_BLUE_WIN_WHITE                 ("dots-blue-win-white.gif"),
    DOTS_CONNECTED_BLUE                 ("dots-connected-blue.gif"),
    DOTS_GRAPH_GREEN                    ("dots-graph-green.gif"),
    ENVELOPE_BACK_ARROW                 ("envelope-back-arrow.png"),
    ENVELOPE_BACK                       ("envelope-back.png"),
    ENVELOPE_BACK_RED_EXCLAM            ("envelope-back-red-exclam.png"),
    ENVELOPE_BACK_ORANGE_EXCLAM         ("envelope-back-orange-exclam.png"),
    ENVELOPE_FRONT                      ("envelope-front.gif"),
    ERASER                              ("eraser.png"),
    ETHERNET_PORT                       ("ethernet-port.png"),
    EXCLAM_BLACK_TRIANGLE_YELLOW        ("exclam-black-triangle-yellow.gif"),
    EXCLAM_RED                          ("exclam-red.gif"),
    EXCLAM_YELLOW                       ("exclam-yellow.gif"),
    EXCLAM_WHITE_CIRCLE_RED             ("exclam-white-circle-red.png"),
    EYE_GLASSES                         ("eye-glasses.gif"),
    FILE_ONES_ZEROS                     ("file-ones-zeros.gif"),
    FLAME                               ("flame.png"),
    FLASHLIGHT_QUESTION_MARK            ("flashlight-question-mark.gif"),
    FLOPPY_DISK                         ("floppy-disk.gif"),
    FLOPPY_DISK_ELLIPSIS                ("floppy-disk-ellipsis.gif"),
    FLOPPY_DISKS_3                      ("floppy-disks-3.gif"),
    FOLDER_OPEN_BLUE                    ("folder-open-blue.gif"),
    FOLDER_OPEN_BLUE_PLUS_GREEN         ("folder-open-blue-plus-green.gif"),
    FOLDER_OPEN_YELLOW                  ("folder-open-yellow.gif"),
    FUNNEL_BLUE                         ("funnel-blue.png"),
    FUNNEL_BLUE_2                       ("funnel-blue-2.png"),
    GHOST                               ("ghost.png"),
    GLOBE                               ("globe.gif"),
    GLOBE_WINDOW_TITLE_BAR              ("globe-window-title-bar.gif"),
    HAND                                ("hand.png"),
    HAND_GRAB                           ("hand-grab.gif"),
    HAND_OPEN                           ("hand-open.gif"),
    HOUSE_1                             ("house-1.gif"),
    HOUSE_2                             ("house-2.png"),
    J_EXCLAM                            ("j-exclam.gif"),
    JSON_LOGO                           ("json-logo.png"),
    KEY_YELLOW                          ("key-yellow.gif"),
    LEAF_GREEN_MONGO                    ("leaf-green-mongo.png"),
    LEFT_RIGHT_ANGLE                    ("left-right-angle.gif"),
    LIGHTBULB_GREEN                     ("lightbulb-green.gif"),
    LIGHTBULB_YELLOW                    ("lightbulb-yellow.png"),
    LIGHTBULB_YELLOW_2                  ("lightbulb-yellow-2.gif"),
    LIGHTNING                           ("lightning.png"),
    LIGHTNING_NEW                       ("lightning-new.png"),
    LINES_HORIZ_ARROW_LEFT_BLUE         ("lines-horiz-arrow-left-blue.png"),
    LINES_HORIZ_ARROW_RIGHT_BLUE        ("lines-horiz-arrow-right-blue.png"),
    LIST                                ("list.gif"),
    LIST_COG                            ("list-cog.gif"),
    LIST_CURTAILED                      ("list-curtailed.gif"),
    LIST_FADED                          ("list-faded.gif"),
    LIST_PERSON                         ("list-person.gif"),
    MAGNIFYING_GLASS                    ("magnifying-glass.gif"),
    MAGNIFYING_GLASS_FADED              ("magnifying-glass-faded.gif"),
    MAGNIFYING_GLASS_MINUS              ("magnifying-glass-minus.gif"),
    MAGNIFYING_GLASS_PLUS               ("magnifying-glass-plus.gif"),
    MAN_HAT_COAT_BLACK                  ("man-hat-coat-black.png"),
    MATCH                               ("match.gif"),
    MATCH_GROUP                         ("match-group.gif"),
    MINUS_GREEN                         ("minus-green.gif"),
    MINUS_WHITE_CIRCLE_RED              ("minus-white-circle-red.gif"),
    MS_EXCEL_LOGO                       ("ms-excel-logo.gif"),
    MS_WORD_LOGO                        ("ms-word-logo.gif"),
    MUSHROOM                            ("mushroom.gif"),
    N_WHITE_BG_BLACK                    ("n-white-bg-black.gif"),
    NUMBER_SIGN_WHITE_SQUARE_RED        ("number-sign-white-square-red.png"),
    OCTAGON_RED                         ("octagon-red.png"),
    ONES_ZEROS                          ("ones-zeros.png"),
    ORANGE                              ("orange.gif"),
    ORIENTDB_LOGO                       ("orientdb-logo.png"),
    PADLOCK_RED                         ("padlock-red.gif"),
    PADLOCK_YELLOW                      ("padlock-yellow.gif"),
    PAGE                                ("page.gif"),
    PAGE_BLANK                          ("page-blank.gif"),
    PAGE_BRACE                          ("page-brace.gif"),
    PAGE_CIRCLE_STRIKE_RED              ("page-circle-strike-red.png"),
    PAGE_CYLINDER_YELLOW                ("page-cylinder-yellow.gif"),
    PAGE_HEADER_RED                     ("page-header-red.gif"),
    PAGE_PLUS_LEFT_GREEN                ("page-plus-left-green.gif"),
    PAGE_PLUS_RIGHT_GREEN               ("page-plus-right-green.gif"),
    PAGE_RED_WORDS                      ("page-red-words.gif"),
    PAGE_SMALL                          ("page-small.gif"),
    PAGE_X_GRAY_TINY                    ("page-x-gray-tiny.gif"),
    PAGE_YELLOW_WORDS                   ("page-yellow-words.gif"),
    PAGES                               ("pages.gif"),
    PAGES_SMALL                         ("pages-small.gif"),
    PAUSE_BLUE                          ("pause-blue.gif"),
    PACKAGE_BROWN                       ("package-brown.gif"),
    PACKAGES_BROWN                      ("packages-brown.gif"),
    PAGE_PICTURE                        ("page-picture.gif"),
    PAGE_PICTURE_BW                     ("page-picture-bw.gif"),
    PAUSE_YELLOW                        ("pause-yellow.gif"),
    PAUSE_YELLOW_FADED                  ("pause-yellow-faded.gif"),
    PDF_LOGO                            ("pdf-logo.gif"),
    PENCIL                              ("pencil.gif"),
    PENCIL_2                            ("pencil-2.png"),
    PENCIL_TINY                         ("pencil-tiny.gif"),
    PEOPLE_GROUP                        ("people-group.gif"),
    PERSON                              ("person.png"),
    PICTURE_MOUNTAIN_SKY                ("picture-mountain-sky.gif"),
    PICTURE_SQUARE                      ("picture-square.gif"),
    PLUG_YELLOW                         ("plug-yellow.gif"),
    PLUG_YELLOW_X_RED                   ("plug-yellow-x-red.gif"),
    PLUS_GREEN                          ("plus-green.gif"),
    POUND_CIRCLE_GREEN                  ("pound-circle-green.gif"),
    PRINTER_PAGE                        ("printer-page.gif"),
    PROGRESS_BAR                        ("progress-bar.gif"),
    PUSH_PIN                            ("push-pin.gif"),
    RAINBOW                             ("rainbow.png"),
    ROBOT_HEAD                          ("robot-head.png"),
    ROLODEX_BLUE                        ("rolodex-blue.gif"),
    SCISSORS                            ("scissors.gif"),
    SCROLL                              ("scroll.gif"),
    SERVER_BLACK                        ("server-black.png"),
    SERVER_RED                          ("server-red.png"),
    SERVER_YELLOW                       ("server-yellow.png"),
    SHAPES_4_MULTI_COLORED              ("shapes-4-multi-colored.gif"),
    SHAPES_COLUMNS_2                    ("shapes-columns-2.gif"),
    SOCKET_BLUE                         ("socket-blue.gif"),
    SOCKET_BLUE_PLUG_YELLOW             ("socket-blue-plug-yellow.gif"),
    SOCKET_PLUG_FOLDER                  ("socket-plug-folder.gif"),
    SQUARE_RED                          ("square-red.gif"),
    SQUARES_RED                         ("squares-red.gif"),
    SQUARES_YELLOW                      ("squares-yellow.gif"),
    SQUARES_YELLOW_CYLINDER_BLUE        ("squares-yellow-cylinder-blue.gif"),
    STAR_YELLOW                         ("star-yellow.gif"),
    STAR_YELLOW_2                       ("star-yellow-2.gif"),
    SUN                                 ("sun.png"),
    SYMBOLS_4                           ("symbols-4.png"),
    TARGET                              ("target.png"),
    TARGETS_3                           ("targets-3.png"),
    TEXT_FIELD_CURSOR                   ("text-field-cursor.gif"),
    TITLED_TEXT_PANE                    ("titled-text-pane.gif"),
    TRIANGLE_UPWARDS_BLUE               ("triangle-upwards-blue.gif"),
    TRASH_CAN                           ("trash-can.gif"),
    WHITE_SQUARE_BLUE_MINUS             ("white-square-blue-minus.gif"),
    WHITE_SQUARE_BLUE_PLUS              ("white-square-blue-plus.gif"),
    WHITE_SQUARES_BLUE_MINUS            ("white-squares-blue-minus.gif"),
    WHITE_SQUARES_BLUE_PLUS             ("white-squares-blue-plus.gif"),
    WHITE_CIRCLE                        ("white-circle.gif"),
    WHITE_CIRCLE_BLUE_I                 ("white-circle-blue-i.gif"),
    WHITE_CIRCLE_BLUE_QM                ("white-circle-blue-qm.gif"),
    WHITE_I_PURPLE_CIRCLE               ("white-i-purple-circle.gif"),
    WHITE_I_PURPLE_CIRCLES              ("white-i-purple-circles.gif"),
    WHITE_M_BLUE_CIRCLE                 ("white-m-blue-circle.gif"),
    WHITE_M_BLUE_CIRCLES                ("white-m-blue-circles.gif"),
    WINDOW_TITLE_BAR                    ("window-title-bar.gif"),
    WINDOW_TITLE_BAR_SEGMENTED          ("window-title-bar-segmented.gif"),
    WINDOW_TITLE_BAR_COMPONENTS         ("window-title-bar-components.gif"),
    WRENCH                              ("wrench.gif"),
    X_GRAY                              ("x-gray.gif"),
    X_2_GRAY                            ("x-2-gray.gif"),
    X_GRAY_TINY                         ("x-gray-tiny.gif"),
    X_RED                               ("x-red.gif"),
    X_RED_OFF_CENTER                    ("x-red-off-center.gif"),
    X_WHITE_CIRCLE_RED                  ("x-white-circle-red.gif"),
    X_WHITE_CIRCLE_RED_FADED            ("x-white-circle-red-faded.gif"),
    XSTREAM_LOGO                        ("xstream-logo.png");


    ////////////
    // FIELDS //
    ////////////

    private String fileName;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private SharedImage(String fileName) {
        this.fileName = fileName;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getFileName() {
        return fileName;
    }


    //////////
    // MISC //
    //////////

    public static ImageIcon get(String fileName) {
        return GuiUtil.getImageLocal(fileName);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        try {
            performChecks();
        } catch(Exception e) {
            System.err.println("Checks Failed");
            e.printStackTrace();
            return;
        }

        System.out.println("Checks Pass: " + values().length + " Total Images");

        openExplorer();
    }

    private static void performChecks() {
        String msg = checkSrcBinSymmetry();
        if(msg != null) {
            throw new IllegalStateException(msg);
        }
        msg = checkFileNames();
        if(msg != null) {
            throw new IllegalStateException(msg);
        }
        msg = checkFileMd5();
        if(msg != null) {
            throw new IllegalStateException(msg);
        }
        msg = checkMissingEnumValues();
        if(msg != null) {
            throw new IllegalStateException(msg);
        }
        msg = checkNamePatterns();
        if(msg != null) {
            throw new IllegalStateException(msg);
        }
    }

    private static String checkSrcBinSymmetry() {
        File sharedBinDir = getSharedImageTargetClassesDir();
        File sharedSrcDdir = getSharedImageResourcesDir();

        if(!sharedSrcDdir.exists() || !sharedBinDir.exists()) {
            return "critical problem";
        }

        File[] srcFiles = sharedSrcDdir.listFiles();
        File[] binFiles = sharedBinDir.listFiles();

        Map<File, String> srcMd5s = new HashMap<>();
        for(File srcFile : srcFiles) {
            srcMd5s.put(srcFile, Md5Util.getMd5(srcFile));
        }

        Map<File, String> binMd5s = new HashMap<>();
        for(File binFile : binFiles) {
            binMd5s.put(binFile, Md5Util.getMd5(binFile));
        }

        String errors = "";
        String s = File.separator;
        String binPathSeg = s + "target" + s + "classes" + s;
        String resoPathSeg = s + "src" + s + "main" + s + "resources" + s;

        // Check to make sure there's a bin file for every source file.
        int srcCount = 0;
        for(File srcFile : srcMd5s.keySet()) {
            String srcMd5 = srcMd5s.get(srcFile);

            srcCount++;

            String pattern2 = Pattern.quote(resoPathSeg);
            String repl2 = Matcher.quoteReplacement(binPathSeg);
            File binFile = new File(srcFile.getAbsolutePath().replaceAll(pattern2, repl2));

            if(!binMd5s.containsKey(binFile)) {
                errors += "BIN FILE DOESN'T EXIST: " + binFile + "\n";
            } else if(!srcMd5.equals(binMd5s.get(binFile))) {
                errors += "BIN FILE MD5 MISMATCH: " + binFile + "\n";
            }
        }

        int binCount = 0;
        for(File binFile : binMd5s.keySet()) {
            String binMd5 = binMd5s.get(binFile);

            if(!binFile.getName().equals("SharedImage.class")) {
                binCount++;
            }

            String pattern2 = Pattern.quote(binPathSeg);
            String repl2 = Matcher.quoteReplacement(resoPathSeg);
            File srcFile = new File(binFile.getAbsolutePath().replaceAll(pattern2, repl2));

            if(!srcMd5s.containsKey(srcFile)) {
                if(!srcFile.getName().equals("SharedImage.class")) {
                    errors += "SRC FILE DOESN'T EXIST: " + srcFile + "\n";
                }
            } else if(!binMd5.equals(srcMd5s.get(srcFile))) {
                errors += "SRC FILE MD5 MISMATCH: " + srcFile + "\n";
            }
        }

        if(srcCount != binCount) {
            errors += "Source/Bin Dir asymmetry: " + srcCount + " != " + binCount + "\n";
        }

        return StringUtil.forceBlankNull(errors.trim());
    }

    private static String checkFileNames() {
        for(SharedImage image : values()) {
            String fileName = image.getFileName();
            if(SharedImage.class.getResource(fileName) == null) {
                return
                    "Image file '" + fileName +
                    "' does not exist in shared image package.";
            }
        }
        return null;
    }

    private static String checkFileMd5() {
        Map<String, String> md5s = new HashMap<>();
        for(SharedImage image : values()) {
            String fileName = image.getFileName();
            URL anyImage = SharedImage.class.getResource(fileName);
            File imageFile = new File(anyImage.getFile());
            String md5 = Md5Util.getMd5(imageFile);
            if(md5s.containsKey(md5)) {
                return "Image file '" + fileName +
                    "' appears to be a duplicate of '" + md5s.get(md5) + "'";
            }
            md5s.put(md5, fileName);
        }
        return null;
    }

    private static String checkMissingEnumValues() {
        Set<String> fileNames = new HashSet<>();
        for(SharedImage image : values()) {
            fileNames.add(image.getFileName());
        }

        File binDir = getSharedImageTargetClassesDir();
        File srcDir = getSharedImageResourcesDir();

        String msg = checkDir(fileNames, binDir, "class");
        if(msg != null) {
            return msg;
        }
        msg = checkDir(fileNames, srcDir, "java");
        if(msg != null) {
            return msg;
        }
        return null;
    }

    private static String checkDir(Set<String> fileNames, File dir, String ext) {
        for(File file : dir.listFiles()) {
            String fileName = file.getName();
            if(!fileNames.contains(fileName)) {
                if(!fileName.equals(SharedImage.class.getSimpleName() + "." + ext)) {
                    return "Unrecognized image file '" + fileName +
                        "' appears in shared image package without a corresponding enum value.";
                }
            }
        }
        return null;
    }

    private static String checkNamePatterns() {
        for(SharedImage image : values()) {
            if(!StringUtil.matches(image.name(), "[A-Z][A-Z0-9]*(_[A-Z0-9]+)*", true)) {
                return "Enum value's name '" + image.name() + "' does not match acceptable pattern";
            }
            String fileName = image.getFileName();
            if(!StringUtil.matches(fileName, "[a-z][a-z0-9]*(-[a-z0-9]+)*\\.(gif|png)", true)) {
                return "Image file name '" + fileName + "' does not match acceptable pattern";
            }

            String temp = FileUtil.getNameWithoutExtension(fileName);
            temp = temp.replaceAll("-", "_");
            temp = temp.toUpperCase();

            if(!temp.equals(image.name())) {
                return "Enum value's name '" + image.name() + "' does not correspond to its image file's name '" + fileName + "'";
            }

        }
        return null;
    }

    private static void openExplorer() {
        File parentFile = getSharedImageResourcesDir();
        OsUtil.openExplorer(parentFile);
    }

    private static File getSharedImageTargetClassesDir() {
        URL anyImage = SharedImage.class.getResource(values()[0].getFileName());
        File imageFile = new File(anyImage.getFile());
        File parentFile = imageFile.getParentFile();
        return parentFile;
    }

    private static File getSharedImageResourcesDir() {
        File parentFile = getSharedImageTargetClassesDir();
        String s = File.separator;
        String binPathSeg = s + "target" + s + "classes" + s;
        String resoPathSeg = s + "src" + s + "main" + s + "resources" + s;
        String binPathSegPattern = Pattern.quote(binPathSeg);
        String resoPathSegRepl = Matcher.quoteReplacement(resoPathSeg);
        File srcFile = new File(parentFile.getAbsolutePath().replaceAll(binPathSegPattern, resoPathSegRepl));
        return srcFile;
    }
}
