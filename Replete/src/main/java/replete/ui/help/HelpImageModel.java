package replete.ui.help;

import replete.ui.images.concepts.ImageModel;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class HelpImageModel extends ImageModel {

    public static final ImageModelConcept TABLE_OF_CONTENTS  = conceptShared(SharedImage.PAGES_SMALL);
    public static final ImageModelConcept SEARCH             = conceptShared(SharedImage.FLASHLIGHT_QUESTION_MARK);
    public static final ImageModelConcept INDEX              = conceptShared(SharedImage.ROLODEX_BLUE);
    public static final ImageModelConcept BOOKMARKS          = conceptShared(SharedImage.BOOK_OPEN_BOOKMARK_BLUE);
    public static final ImageModelConcept TERM               = conceptShared(SharedImage.DOT_BLUE);
    public static final ImageModelConcept TERM_WITH_CONTEXTS = conceptShared(SharedImage.DOT_BLUE_EMPTY);
    public static final ImageModelConcept TERM_CONTEXT       = conceptShared(SharedImage.DOT_GREEN_SMALL);

    public static final ImageModelConcept TOC_PAGE_ROOT      = conceptShared(SharedImage.BOOK_CLOSED);
    public static final ImageModelConcept TOC_PAGE_ROOT_EXP  = conceptShared(SharedImage.BOOK_OPENING);
    public static final ImageModelConcept TOC_PAGE_MID_EMPTY = conceptShared(SharedImage.BOOK_OPEN);
    public static final ImageModelConcept TOC_PAGE_MID_CONT  = conceptShared(SharedImage.BOOK_OPEN_PENCIL);
    public static final ImageModelConcept TOC_PAGE_LEAF      = conceptShared(SharedImage.PAGE_SMALL);

    public static final ImageModelConcept SET_INLINE         = conceptShared(SharedImage.BARS_PARALLEL_BLUE);
    public static final ImageModelConcept SET_CONTENT_DIR    = conceptShared(SharedImage.DIR_HIER);


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        visualize(3);
    }
}
