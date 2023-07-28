package replete.ui.help;

import replete.ui.help.model.HelpDataModel;
import replete.ui.help.model.HelpPage;
import replete.ui.lay.Lay;
import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;

public class HelpBookmarksPanel extends HelpComponentPanel {


    ////////////
    // FIELDS //
    ////////////

    private HelpUiController uiController;
    private HelpDataModel dataModel;
    private RTree treBookmarks;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpBookmarksPanel(HelpUiController uiController, HelpDataModel dataModel) {
        this.uiController = uiController;
        this.dataModel = dataModel;

        Lay.BLtg(this,
            "C", Lay.sp(treBookmarks = Lay.tr("seltype=single"))
        );
        treBookmarks.setMouseDragSelection(false);
        treBookmarks.setShowsRootHandles(true);

        treBookmarks.addSelectionListener(e -> {
            NodeBase uSel = treBookmarks.getSelObject();
            if(uSel instanceof NodeBookmark) {
                NodeBookmark uBookmark = (NodeBookmark) uSel;
                HelpPage page = uBookmark.getBookmark().getPage();
                uiController.select(page);
            } // else null
        });

//        rebuildForAlbumChange();
//        rebuildForFilterChange();
    }

//    private void rebuildForAlbumChange() {     // Updates panel's local term cache
//        terms.clear();
//        for(HelpAlbum album : dataModel.getAlbums()) {
//            for(HelpTermLink termLink : album.getTermLinks()) {
//                List<HelpTermGlob> globs = terms.get(termLink.getTerm());
//                if(globs == null) {
//                    globs = new ArrayList<>();
//                    terms.put(termLink.getTerm(), globs);
//                }
//                globs.add(new HelpTermGlob(album, termLink));
//            }
//        }
//    }
//
//    private void rebuildForFilterChange() {    // Updates tree according to term cache and filter
//        PatternInterpretation defaultInterp = new PatternInterpretation(
//            PatternInterpretationType.LITERAL, false, false, "."
//        );
//        Pair<String, PatternInterpretation> parseResults =
//            PatternUtil.parsePatternInterpretationTag(txtFilter.getText(), defaultInterp);
//        String filterPatternAny = parseResults.getValue1();
//        PatternInterpretation filterPatternInterp = parseResults.getValue2();
//        String filterPatternRegex = PatternUtil.convertToRegex(filterPatternAny, filterPatternInterp);
//        boolean filterBlank = StringUtil.isBlank(filterPatternAny);
//        RTreeNode nRoot = new RTreeNode(new NodeEmptyRoot());
//        for(HelpTerm term : terms.keySet()) {
//            List<HelpTermGlob> globs = terms.get(term);
//            HelpTermGlob glob0 = globs.get(0);
//            boolean includeTerm =
//                filterBlank ||
//                StringUtil.matches(term.getName(), filterPatternRegex, filterPatternInterp);
//            if(globs.size() == 1 && StringUtil.isBlank(glob0.termLink.getContext())) {
//                if(includeTerm) {
//                    nRoot.add(new NodeTerm(term, glob0));
//                }
//            } else {
//                RTreeNode nTerm = new RTreeNode(new NodeTerm(term, null));
//                boolean includeAtLeastOneContext = false;
//                for(HelpTermGlob glob : globs) {
//                    boolean includeContext =
//                        includeTerm ||
//                        filterBlank ||
//                        !StringUtil.isBlank(glob.termLink.getContext()) &&
//                        StringUtil.matches(glob.termLink.getContext(), filterPatternRegex, filterPatternInterp);
//                    if(includeContext) {
//                        nTerm.add(new NodeTermContext(term, glob));
//                        includeAtLeastOneContext = true;
//                    }
//                }
//                if(includeTerm || includeAtLeastOneContext) {
//                    nRoot.add(nTerm);
//                }
//            }
//        }
//        treIndex.setModel(nRoot);
//        treIndex.expandAll();
//    }
}
