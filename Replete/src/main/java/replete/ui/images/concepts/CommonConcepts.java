package replete.ui.images.concepts;

import replete.ui.images.shared.SharedImage;

public class CommonConcepts extends ImageModel {

    // Can be used to indicate either 1) an "example" image or 2) an image that hasn't been shown yet.
    // A rainbow is chosen because it should be an image that should look out of place in any context.
    public static final ImageModelConcept _PLACEHOLDER   = conceptShared(SharedImage.RAINBOW);

    // Smaller icons that can be easily added to other 16x16 icons.
    public static final ImageModelConcept COMPLETE_DECORATOR = conceptShared(SharedImage.CHECK_MARK_GREEN_MINI);
    public static final ImageModelConcept ERROR_DECORATOR    = conceptShared(SharedImage.X_WHITE_SQUARE_RED_MINI);
    public static final ImageModelConcept INPROG_DECORATOR   = conceptShared(SharedImage.CLOCK_LIGHT_BLUE_MINI);
    public static final ImageModelConcept NEW_DECORATOR      = conceptShared(SharedImage.STAR_YELLOW_MINI);
    public static final ImageModelConcept PAUSED_DECORATOR   = conceptShared(SharedImage.PAUSE_YELLOW_MINI);
    public static final ImageModelConcept RUNNING_DECORATOR  = conceptShared(SharedImage.ARROW_RIGHT_GREEN_NO_TAIL_MINI);
    public static final ImageModelConcept STOPPED_DECORATOR  = conceptShared(SharedImage.SQUARE_RED_MINI);

    public static final ImageModelConcept ABOUT          = conceptShared(SharedImage.AT_SIGN);
    public static final ImageModelConcept ACCEPT         = conceptShared(SharedImage.CHECK_MARK_GREEN);
    public static final ImageModelConcept ACTION         = conceptShared(SharedImage.LIGHTNING);
    public static final ImageModelConcept ADD            = conceptShared(SharedImage.PLUS_GREEN);
    public static final ImageModelConcept ADD_FILE       = conceptShared(SharedImage.PAGE_PLUS_RIGHT_GREEN);
    public static final ImageModelConcept ADD_DIRECTORY  = conceptShared(SharedImage.FOLDER_OPEN_BLUE_PLUS_GREEN);
    public static final ImageModelConcept BINARY         = conceptShared(SharedImage.ONES_ZEROS);
    public static final ImageModelConcept BOOKMARK       = conceptShared(SharedImage.BOOKMARK_BLUE);
    public static final ImageModelConcept CANCEL         = conceptShared(SharedImage.ARROW_LEFT_CURVE_RED);  // Used for both "Cancel" & "Close" window buttons
    public static final ImageModelConcept CHANGE         = conceptShared(SharedImage.DELTA);                 // Noun, not the verb
    public static final ImageModelConcept CLEAR          = conceptShared(SharedImage.PAGE_X_GRAY_TINY);
    public static final ImageModelConcept CLONE          = conceptShared(SharedImage.CUBES_2_GREEN);
    public static final ImageModelConcept CLOSE          = conceptShared(SharedImage.X_GRAY);                // Used for closing/removing UI elements like tabs, panels (NOT A SYNONYM FOR CANCEL)
    public static final ImageModelConcept CLOSE_ALL      = conceptShared(SharedImage.X_2_GRAY);              // Used for closing/removing UI elements like tabs, panels (NOT A SYNONYM FOR CANCEL)
    public static final ImageModelConcept COLLAPSE       = conceptShared(SharedImage.WHITE_SQUARE_BLUE_MINUS);
    public static final ImageModelConcept COLLAPSE_ALL   = conceptShared(SharedImage.WHITE_SQUARES_BLUE_MINUS);
    public static final ImageModelConcept COLLAPSE_DOWN  = conceptShared(SharedImage.CHEVRON_DOWN_BLUE);
    public static final ImageModelConcept COLLAPSE_LEFT  = conceptShared(SharedImage.CHEVRON_LEFT_BLUE);
    public static final ImageModelConcept COLLAPSE_RIGHT = conceptShared(SharedImage.CHEVRON_RIGHT_BLUE);
    public static final ImageModelConcept COLLAPSE_UP    = conceptShared(SharedImage.CHEVRON_UP_BLUE);
    public static final ImageModelConcept COMPLETE       = conceptShared(SharedImage.CHECK_MARK_GREEN);
    public static final ImageModelConcept COMPUTATION    = conceptShared(SharedImage.COG_YELLOW);            // Two very different concepts
    public static final ImageModelConcept CONFIGURATION  = conceptShared(SharedImage.COG_YELLOW);            // use same icon
    public static final ImageModelConcept CONNECT        = conceptShared(SharedImage.CIRCLES_GREEN_EDGES_RECT);  // Have used plug-in icon in past...
    public static final ImageModelConcept CONSOLE        = conceptShared(SharedImage.CONSOLE_WINDOW_BLACK);
    public static final ImageModelConcept CONTACT        = conceptShared(SharedImage.AT_SIGN);
    public static final ImageModelConcept COPY           = conceptShared(SharedImage.PAGES);
    public static final ImageModelConcept CSV            = conceptShared(SharedImage.A_COMMA_E_BARS);
    public static final ImageModelConcept CUT            = conceptShared(SharedImage.SCISSORS);
    public static final ImageModelConcept DATE_TIME      = conceptShared(SharedImage.DATE_TIME);
    public static final ImageModelConcept DATABASE       = conceptShared(SharedImage.CYLINDER_YELLOW);
    public static final ImageModelConcept DEADLOCK       = conceptShared(SharedImage.PADLOCK_RED);              // Just the general idea of "deadlock". THREAD_DEADLOCKED also exists when you want to include the thread icon
    public static final ImageModelConcept DEINDENT       = conceptShared(SharedImage.LINES_HORIZ_ARROW_LEFT_BLUE);
    public static final ImageModelConcept DELETE         = conceptShared(SharedImage.X_RED);
    public static final ImageModelConcept DIRTY          = conceptShared(SharedImage.TRIANGLE_UPWARDS_BLUE);
    public static final ImageModelConcept DISCONNECT     = conceptShared(SharedImage.CIRCLES_RED_EDGE_BROKEN);
    public static final ImageModelConcept DOWN           = conceptShared(SharedImage.ARROW_DOWN_YELLOW);        // A weak concept here, because it is still very useful
    public static final ImageModelConcept EDIT           = conceptShared(SharedImage.PENCIL);
    public static final ImageModelConcept EDIT_SMALL     = conceptShared(SharedImage.PENCIL_TINY);
    public static final ImageModelConcept E_MAIL         = conceptShared(SharedImage.ENVELOPE_FRONT);
    public static final ImageModelConcept E_MAIL_SEND    = conceptShared(SharedImage.ENVELOPE_BACK_ARROW);
    public static final ImageModelConcept ERROR          = conceptShared(SharedImage.X_WHITE_CIRCLE_RED);
    public static final ImageModelConcept EXCEPTION      = conceptShared(SharedImage.EXCLAM_RED);
    public static final ImageModelConcept EXIT           = conceptShared(SharedImage.X_RED);
    public static final ImageModelConcept EXPAND         = conceptShared(SharedImage.WHITE_SQUARE_BLUE_PLUS);
    public static final ImageModelConcept EXPAND_ALL     = conceptShared(SharedImage.WHITE_SQUARES_BLUE_PLUS);
    public static final ImageModelConcept EXPERIMENT     = conceptShared(SharedImage.BEAKER);
    public static final ImageModelConcept EXPORT         = conceptShared(SharedImage.BOX_ARROW_OUTWARD_BLUE);
    public static final ImageModelConcept FAVORITE       = conceptShared(SharedImage.STAR_YELLOW);
    public static final ImageModelConcept FILE           = conceptShared(SharedImage.PAGE);
    public static final ImageModelConcept FILE_NAVIGATOR = conceptShared(SharedImage.DIR_HIER);          // i.e. Windows Explorer, Finder, etc.
    public static final ImageModelConcept FILTER         = conceptShared(SharedImage.FUNNEL_BLUE);
    public static final ImageModelConcept FIND           = conceptShared(SharedImage.BINOCULARS_BLACK);
    public static final ImageModelConcept FOLDER         = conceptShared(SharedImage.FOLDER_OPEN_BLUE);      // Maybe use yellow one so differentiated from "open" action?
    public static final ImageModelConcept FONT_INCREASE  = conceptShared(SharedImage.A_ARROW_UP);
    public static final ImageModelConcept FONT_DECREASE  = conceptShared(SharedImage.A_ARROW_DOWN);
    public static final ImageModelConcept FORWARD        = conceptShared(SharedImage.ARROW_RIGHT_GREEN_LIGHT);
    public static final ImageModelConcept FORWARD_MORE   = conceptShared(SharedImage.ARROWS_RIGHT_GREEN_LIGHT);
    public static final ImageModelConcept HARD_DISK      = conceptShared(SharedImage.DISK_DRIVE_GRAY);
    public static final ImageModelConcept HASH           = conceptShared(SharedImage.NUMBER_SIGN_WHITE_SQUARE_RED);
    public static final ImageModelConcept HELP           = conceptShared(SharedImage.WHITE_CIRCLE_BLUE_QM);
    public static final ImageModelConcept HOME           = conceptShared(SharedImage.HOUSE_1);
    public static final ImageModelConcept IMPORT         = conceptShared(SharedImage.BOX_ARROW_INWARD_BLUE);
    public static final ImageModelConcept INFO           = conceptShared(SharedImage.WHITE_CIRCLE_BLUE_I);
    public static final ImageModelConcept INBOX          = IMPORT;
    public static final ImageModelConcept INDENT         = conceptShared(SharedImage.LINES_HORIZ_ARROW_RIGHT_BLUE);
    public static final ImageModelConcept INTERNET       = conceptShared(SharedImage.GLOBE);
    public static final ImageModelConcept JSON           = conceptShared(SharedImage.JSON_LOGO);
    public static final ImageModelConcept LAUNCH         = conceptShared(SharedImage.ARROW_WHITE_CIRCLE_GREEN);  // Very similar to PLAY, different icon
    public static final ImageModelConcept LIBRARY        = conceptShared(SharedImage.BOOKS_STACKED);
    public static final ImageModelConcept LINK           = conceptShared(SharedImage.CHAIN);
    public static final ImageModelConcept LOG            = conceptShared(SharedImage.CLIPBOARD_PAGE);
    public static final ImageModelConcept LOOK_AND_FEEL  = conceptShared(SharedImage.STAR_YELLOW);
    public static final ImageModelConcept MANUAL         = conceptShared(SharedImage.HAND);
    public static final ImageModelConcept MESSAGE        = conceptShared(SharedImage.ENVELOPE_BACK);
    public static final ImageModelConcept MODEL          = conceptShared(SharedImage.CIRCLES_3_GROUPED);         // Just an example icon for the multi-meaning "model"
    public static final ImageModelConcept MONGO          = conceptShared(SharedImage.LEAF_GREEN_MONGO);
    public static final ImageModelConcept NULL           = conceptShared(SharedImage.N_WHITE_BG_BLACK);
    public static final ImageModelConcept PDF            = conceptShared(SharedImage.PDF_LOGO);
    public static final ImageModelConcept MONITOR        = conceptShared(SharedImage.COMPUTER_MONITOR);
    public static final ImageModelConcept MOVE_DOWN      = conceptShared(SharedImage.ARROW_DOWN_YELLOW_BROKEN_TAIL);
    public static final ImageModelConcept MOVE_UP        = conceptShared(SharedImage.ARROW_UP_YELLOW_BROKEN_TAIL);
    public static final ImageModelConcept MS_EXCEL       = conceptShared(SharedImage.MS_EXCEL_LOGO);
    public static final ImageModelConcept MS_WORD        = conceptShared(SharedImage.MS_WORD_LOGO);
    public static final ImageModelConcept NEW            = conceptShared(SharedImage.STAR_YELLOW);
    public static final ImageModelConcept NEXT           = conceptShared(SharedImage.ARROW_RIGHT_YELLOW);
    public static final ImageModelConcept OCR            = conceptShared(SharedImage.PAGE_PICTURE);
    public static final ImageModelConcept OPEN           = conceptShared(SharedImage.FOLDER_OPEN_BLUE);
    public static final ImageModelConcept OPTIONS        = conceptShared(SharedImage.LIST);
    public static final ImageModelConcept ORIENTDB        = conceptShared(SharedImage.ORIENTDB_LOGO);
    public static final ImageModelConcept OUTBOX         = EXPORT;
    public static final ImageModelConcept PACKAGE        = conceptShared(SharedImage.PACKAGE_BROWN);
    public static final ImageModelConcept PAUSE          = conceptShared(SharedImage.PAUSE_YELLOW);
    public static final ImageModelConcept PASTE          = conceptShared(SharedImage.CLIPBOARD_PAGE_OFFSET);
    public static final ImageModelConcept PLAY           = conceptShared(SharedImage.ARROW_RIGHT_GREEN_NO_TAIL);
    public static final ImageModelConcept PLUGIN         = conceptShared(SharedImage.SOCKET_BLUE_PLUG_YELLOW);
    public static final ImageModelConcept PREV           = conceptShared(SharedImage.ARROW_LEFT_YELLOW);
    public static final ImageModelConcept PRINT          = conceptShared(SharedImage.PRINTER_PAGE);
    public static final ImageModelConcept PROFILING      = conceptShared(SharedImage.FLAME);
    public static final ImageModelConcept IN_PROGRESS    = conceptShared(SharedImage.CIRCLE_ANIMATED);
    public static final ImageModelConcept PROGRESS       = conceptShared(SharedImage.PROGRESS_BAR);
    public static final ImageModelConcept RANDOM         = conceptShared(SharedImage.DICE);
    public static final ImageModelConcept REDO           = conceptShared(SharedImage.ARROW_RIGHT_CURVE_YELLOW);
    public static final ImageModelConcept REFRESH        = conceptShared(SharedImage.ARROWS_REVOLVING);
    public static final ImageModelConcept REMOVE         = DELETE;                                                    // Synonym concept
    public static final ImageModelConcept RENAME         = conceptShared(SharedImage.A_TEXT_CURSOR_E);
    public static final ImageModelConcept REPLACE        = conceptShared(SharedImage.ARROWS_UP_DOWN_RED);
    public static final ImageModelConcept RESET          = conceptShared(SharedImage.ARROW_COUNTER_CW_BLUE);
    public static final ImageModelConcept RESTART        = conceptShared(SharedImage.BAR_YELLOW_ARROW_GREEN);
    public static final ImageModelConcept REWIND         = conceptShared(SharedImage.ARROW_LEFT_BLUE_LIGHT);
    public static final ImageModelConcept REWIND_MORE    = conceptShared(SharedImage.ARROWS_LEFT_BLUE_LIGHT);
    public static final ImageModelConcept RUN            = PLAY;                                                      // Synonym concept
    public static final ImageModelConcept SAVE           = conceptShared(SharedImage.FLOPPY_DISK);
    public static final ImageModelConcept SAVE_ALL       = conceptShared(SharedImage.FLOPPY_DISKS_3);
    public static final ImageModelConcept SAVE_AS        = conceptShared(SharedImage.FLOPPY_DISK_ELLIPSIS);
    public static final ImageModelConcept SCRIPT         = conceptShared(SharedImage.SCROLL);
    public static final ImageModelConcept SEARCH         = conceptShared(SharedImage.MAGNIFYING_GLASS);
    public static final ImageModelConcept SECURITY       = conceptShared(SharedImage.PADLOCK_YELLOW);
    public static final ImageModelConcept SERVER         = conceptShared(SharedImage.SERVER_YELLOW);
    public static final ImageModelConcept STACK_TRACE    = conceptShared(SharedImage.BARS_PARALLEL_BLUE);
    public static final ImageModelConcept STOP           = conceptShared(SharedImage.SQUARE_RED);
    public static final ImageModelConcept STOP_ALL       = conceptShared(SharedImage.SQUARES_RED);
    public static final ImageModelConcept STOP_SIGN      = conceptShared(SharedImage.OCTAGON_RED);
    public static final ImageModelConcept SORT_ASC       = conceptShared(SharedImage.A_Z_ARROW_DOWN);
    public static final ImageModelConcept SUBTRACT       = conceptShared(SharedImage.MINUS_GREEN);            // Kinda a weak general concept
    public static final ImageModelConcept SYSTEM         = conceptShared(SharedImage.COMPUTER_MONITOR);
    public static final ImageModelConcept SYNCHRONIZE    = conceptShared(SharedImage.ARROWS_OPPOSITE_YELLOW);
    public static final ImageModelConcept TABLE          = conceptShared(SharedImage.DATA_TABLE);
    public static final ImageModelConcept TARGET         = conceptShared(SharedImage.TARGET);
    public static final ImageModelConcept TEXT_INPUT     = conceptShared(SharedImage.TEXT_FIELD_CURSOR);      // Kinda a weak general concept, but icon is really nice...
    public static final ImageModelConcept TIME           = conceptShared(SharedImage.CLOCK_LIGHT_BLUE);
    public static final ImageModelConcept TOOLS          = conceptShared(SharedImage.WRENCH);
    public static final ImageModelConcept UNDO           = conceptShared(SharedImage.ARROW_LEFT_CURVE_YELLOW);
    public static final ImageModelConcept UP             = conceptShared(SharedImage.ARROW_UP_YELLOW);        // A weak concept here, because it is still very useful
    public static final ImageModelConcept WARNING        = conceptShared(SharedImage.EXCLAM_BLACK_TRIANGLE_YELLOW);
    public static final ImageModelConcept WORLD          = conceptShared(SharedImage.GLOBE);
    public static final ImageModelConcept XML            = conceptShared(SharedImage.LEFT_RIGHT_ANGLE);
    public static final ImageModelConcept XSTREAM        = conceptShared(SharedImage.XSTREAM_LOGO);
    public static final ImageModelConcept ZOOM_IN        = conceptShared(SharedImage.MAGNIFYING_GLASS_PLUS);
    public static final ImageModelConcept ZOOM_OUT       = conceptShared(SharedImage.MAGNIFYING_GLASS_MINUS);

    // Threads made into a common concept to make sure that no other project
    // goes through the pain of duplicating this list within their own image
    // models.  One day we should look into converting this to use a
    // decorated image builder.
    public static final ImageModelConcept THREAD               = COMPUTATION;
    public static final ImageModelConcept THREADS              = conceptShared(SharedImage.COGS_YELLOW_ROTATING);
    public static final ImageModelConcept THREAD_ALIVE         = conceptShared(SharedImage.COG_YELLOW_ARROW_RIGHT_GREEN_NO_TAIL);
    public static final ImageModelConcept THREAD_WAITING       = conceptShared(SharedImage.COG_YELLOW_PAUSE_YELLOW);
    public static final ImageModelConcept THREAD_BLOCKED       = conceptShared(SharedImage.COG_YELLOW_SQUARE_RED);
    public static final ImageModelConcept THREAD_TERMINATED    = conceptShared(SharedImage.COG_YELLOW_X_WHITE_SQUARE_RED);
    public static final ImageModelConcept THREAD_NEW           = conceptShared(SharedImage.COG_YELLOW_PLUS_YELLOW);
    public static final ImageModelConcept THREAD_REMOVED       = conceptShared(SharedImage.COG_YELLOW_RECT_GRAY);
    public static final ImageModelConcept THREAD_ALIVE_AV      = conceptShared(SharedImage.COG_BLUE_ARROW_RIGHT_GREEN_NO_TAIL);
    public static final ImageModelConcept THREAD_WAITING_AV    = conceptShared(SharedImage.COG_BLUE_PAUSE_YELLOW);
    public static final ImageModelConcept THREAD_BLOCKED_AV    = conceptShared(SharedImage.COG_BLUE_SQUARE_RED);
    public static final ImageModelConcept THREAD_TERMINATED_AV = conceptShared(SharedImage.COG_BLUE_X_WHITE_SQUARE_RED);
    public static final ImageModelConcept THREAD_NEW_AV        = conceptShared(SharedImage.COG_BLUE_PLUS_YELLOW);
    public static final ImageModelConcept THREAD_REMOVED_AV    = conceptShared(SharedImage.COG_BLUE_RECT_GRAY);
    public static final ImageModelConcept THREAD_DEADLOCKED    = conceptShared(SharedImage.COG_RED_PADLOCK_RED);
    public static final ImageModelConcept THREAD_REMOVED_ERROR = conceptShared(SharedImage.COG_RED_RECT_GRAY);
    public static final ImageModelConcept THREAD_ALIVE_ERROR   = conceptShared(SharedImage.COG_RED_ARROW_RIGHT_GREEN_NO_TAIL);


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize();
    }
}
