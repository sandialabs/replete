package replete.ui.help;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import replete.collections.Pair;
import replete.text.StringUtil;
import replete.text.patterns.PatternInterpretation;
import replete.text.patterns.PatternInterpretationType;
import replete.text.patterns.PatternUtil;
import replete.ui.help.model.HelpAlbum;
import replete.ui.help.model.HelpDataModel;
import replete.ui.help.model.HelpPage;
import replete.ui.help.model.HelpTerm;
import replete.ui.help.model.HelpTermLink;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.tree.NodeBase;
import replete.ui.tree.NodeEmptyRoot;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;

public class HelpIndexPanel extends HelpComponentPanel {


    ////////////
    // FIELDS //
    ////////////

    private HelpUiController uiController;
    private HelpDataModel dataModel;
    private RTree treIndex;
    private Map<HelpTerm, List<HelpTermGlob>> terms = new TreeMap<>();
    private RTextField txtFilter;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpIndexPanel(HelpUiController uiController, HelpDataModel dataModel) {
        this.uiController = uiController;
        this.dataModel = dataModel;

        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("Filter:", CommonConcepts.FILTER),
                "C", txtFilter = Lay.tx("", (DocumentChangeListener) e -> rebuildForFilterChange()),
                "gap=5,eb=5"
            ),
            "C", Lay.sp(treIndex = Lay.tr("seltype=single"))
        );
        treIndex.setMouseDragSelection(false);
        treIndex.setShowsRootHandles(true);

        treIndex.addSelectionListener(e -> {
            NodeBase uSel = treIndex.getSelObject();
            if(uSel instanceof NodeTerm) {
                NodeTerm uTerm = (NodeTerm) uSel;
                if(uTerm.glob != null) {
                    HelpPage page = uTerm.glob.termLink.getPage();
                    uiController.select(page);
                }
            } else if(uSel instanceof NodeTermContext) {
                NodeTermContext uTermContext = (NodeTermContext) uSel;
                HelpPage page = uTermContext.glob.termLink.getPage();
                uiController.select(page);
            } // else null
        });

        txtFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_KP_DOWN) {
                    if(treIndex.getRoot().hasChildren()) {
                        treIndex.setSelectionRow(0);
                        treIndex.focus();
                    }
                }
            }
        });

        treIndex.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_KP_UP) {
                    int[] rows = treIndex.getSelectionRows();
                    if(rows.length != 0 && rows[0] == 0) {
                        txtFilter.focus();
                    }
                }
            }
        });

        rebuildForAlbumChange();
        rebuildForFilterChange();
    }

    private void rebuildForAlbumChange() {     // Updates panel's local term cache
        terms.clear();
        for(HelpAlbum album : dataModel.getAlbums()) {
            for(HelpTermLink termLink : album.getTermLinks()) {
                List<HelpTermGlob> globs = terms.get(termLink.getTerm());
                if(globs == null) {
                    globs = new ArrayList<>();
                    terms.put(termLink.getTerm(), globs);
                }
                globs.add(new HelpTermGlob(album, termLink));
            }
        }
    }

    private void rebuildForFilterChange() {    // Updates tree according to term cache and filter
        PatternInterpretation defaultInterp = new PatternInterpretation(
            PatternInterpretationType.LITERAL, false, false, "."
        );
        Pair<String, PatternInterpretation> parseResults =
            PatternUtil.parsePatternInterpretationTag(txtFilter.getText(), defaultInterp);
        String filterPatternAny = parseResults.getValue1();
        PatternInterpretation filterPatternInterp = parseResults.getValue2();
        String filterPatternRegex = PatternUtil.convertToRegex(filterPatternAny, filterPatternInterp);
        boolean filterBlank = StringUtil.isBlank(filterPatternAny);
        RTreeNode nRoot = new RTreeNode(new NodeEmptyRoot());
        for(HelpTerm term : terms.keySet()) {
            List<HelpTermGlob> globs = terms.get(term);
            HelpTermGlob glob0 = globs.get(0);
            boolean includeTerm =
                filterBlank ||
                StringUtil.matches(term.getName(), filterPatternRegex, filterPatternInterp);
            if(globs.size() == 1 && StringUtil.isBlank(glob0.termLink.getContext())) {
                if(includeTerm) {
                    nRoot.add(new NodeTerm(term, glob0));
                }
            } else {
                RTreeNode nTerm = new RTreeNode(new NodeTerm(term, null));
                boolean includeAtLeastOneContext = false;
                for(HelpTermGlob glob : globs) {
                    boolean includeContext =
                        includeTerm ||
                        filterBlank ||
                        !StringUtil.isBlank(glob.termLink.getContext()) &&
                        StringUtil.matches(glob.termLink.getContext(), filterPatternRegex, filterPatternInterp);
                    if(includeContext) {
                        nTerm.add(new NodeTermContext(term, glob));
                        includeAtLeastOneContext = true;
                    }
                }
                if(includeTerm || includeAtLeastOneContext) {
                    nRoot.add(nTerm);
                }
            }
        }
        treIndex.setModel(nRoot);
        treIndex.expandAll();
    }
}
