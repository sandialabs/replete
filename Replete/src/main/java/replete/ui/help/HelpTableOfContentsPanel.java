package replete.ui.help;

import java.util.List;

import javax.swing.JButton;

import replete.text.StringUtil;
import replete.ui.ColorLib;
import replete.ui.help.model.HelpAlbum;
import replete.ui.help.model.HelpDataModel;
import replete.ui.help.model.HelpPage;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.tree.NodeBase;
import replete.ui.tree.NodeSimpleLabel;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.Dialogs;

public class HelpTableOfContentsPanel extends HelpComponentPanel {


    ////////////
    // FIELDS //
    ////////////

    private HelpDataModel dataModel;

    private HelpUiController uiController;
    private RTree treContents;
    private JButton btnAdd, btnRemove, btnMoveUp, btnMoveDown, btnRename, btnInfo, btnConfig;

    private boolean suppressSelection;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpTableOfContentsPanel(HelpUiController uiController, HelpDataModel dataModel) {
        this.uiController = uiController;
        this.dataModel = dataModel;

        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.FL("L",
                    btnAdd      = Lay.btn(CommonConcepts.ADD,           "ttt=Add..."),
                    btnRemove   = Lay.btn(CommonConcepts.REMOVE,        "ttt=Remove"),
                    btnMoveUp   = Lay.btn(CommonConcepts.MOVE_UP,       "ttt=Move-Up"),
                    btnMoveDown = Lay.btn(CommonConcepts.MOVE_DOWN,     "ttt=Move-Down"),
                    btnRename   = Lay.btn(CommonConcepts.RENAME,        "ttt=Rename..."),
                    btnInfo     = Lay.btn(CommonConcepts.INFO,          "ttt=Show-Provider")
                ),
                "E", Lay.FL(
                    btnConfig   = Lay.btn(CommonConcepts.CONFIGURATION, "ttt=Edit-Configuration...")
                ),
                "chtransp,bg=" + Lay.clr(ColorLib.YELLOW_LIGHT)
            ),
            "C", Lay.sp(treContents = Lay.tr("seltype=single"))
        );
        treContents.setRootVisible(true);
        treContents.setMouseDragSelection(false);
        treContents.setShowsRootHandles(true);

        treContents.addSelectionListener(e -> {
            if(!suppressSelection) {
                NodeBase uSel = treContents.getSelObject();
                if(uSel instanceof NodePage) {
                    NodePage uPage = (NodePage) uSel;
                    HelpPage page = uPage.page;
                    uiController.select(page);
                } else {
                    uiController.select(null);
                }
            }
        });
        uiController.addPageSelectionListener(e -> {
            boolean otherEnabled = e.getPage() != null;
            btnAdd.setEnabled(true);
            btnRemove.setEnabled(otherEnabled);
            btnMoveUp.setEnabled(otherEnabled);
            btnMoveDown.setEnabled(otherEnabled);
            btnRename.setEnabled(otherEnabled);
            btnInfo.setEnabled(otherEnabled);

            suppressSelection = true;
            if(e.getPage() == null) {
                treContents.clearSelection();
            } else {
                HelpPage[] path = e.getPage().getPagePath();
                RTreeNode nCurrent = treContents.getRoot();
                int p = 0;
                for(HelpPage page : path) {
                    for(RTreeNode nChild : nCurrent) {
                        if(((NodePage) nChild.get()).page == page) {
                            if(p == path.length - 1) {
                                treContents.setSelNode(nChild);
                            } else {
                                nCurrent = nChild;
                            }
                            break;
                        }
                    }
                    p++;
                }
            }
            suppressSelection = false;
        });

        btnAdd.addActionListener(e -> {
            RTreeNode nSel = treContents.getSelNode();
            NodeBase uSel = treContents.getSelObject();

            if(uSel instanceof NodePage) {
                promptNameAndAddNew(null, nSel, uSel);

            // No selection or root selection
            } else {
                nSel = treContents.getRoot();         // Reset incase was null
                uSel = treContents.getRootObject();

                AddRootHelpSectionDialog dlg = new AddRootHelpSectionDialog(getWindow());
                dlg.setVisible(true);
                if(dlg.getResult() == AddRootHelpSectionDialog.ACCEPT) {
                    promptNameAndAddNew(dlg.getHelpProvider(), nSel, uSel);
                }
            }
        });

        btnRemove.addActionListener(e -> {
            RTreeNode nSel = treContents.getSelNode();
            NodePage uSel = treContents.getSelObject();
            HelpPage page = uSel.page;
            if(page.getParent() != null) {
                page.getParent().getChildren().remove(page);
            } else {
                page.getAlbum().removePageRoot(page);
            }
            treContents.remove(nSel);
            page.save();       // For now, save whole album after every operation -- can change this one day
        });

        btnMoveUp.addActionListener(e -> {
            RTreeNode nSel = treContents.getSelNode();
            treContents.moveUp(nSel);

            NodePage uSel = treContents.getSelObject();
            HelpPage page = uSel.page;
            HelpPage parent = page.getParent();

            List<HelpPage> children;
            if(parent == null) {
                children = page.getAlbum().getPageRoots();
            } else {
                children = parent.getChildren();
            }

            int i = children.indexOf(page);
            if(i != 0) {
                HelpPage pageBefore = children.get(i - 1);
                children.set(i, pageBefore);
                children.set(i - 1, page);
                page.save();
            }
        });

        btnMoveDown.addActionListener(e -> {
            RTreeNode nSel = treContents.getSelNode();
            treContents.moveDown(nSel);

            NodePage uSel = treContents.getSelObject();
            HelpPage page = uSel.page;
            HelpPage parent = page.getParent();

            List<HelpPage> children;
            if(parent == null) {
                children = page.getAlbum().getPageRoots();
            } else {
                children = parent.getChildren();
            }

            int i = children.indexOf(page);
            if(i != children.size() - 1) {
                HelpPage pageAfter = children.get(i + 1);
                children.set(i, pageAfter);
                children.set(i + 1, page);
                page.save();
            }
        });

        btnRename.addActionListener(e -> {
            NodePage uSel = treContents.getSelObject();
            HelpPage page = uSel.page;
            String input = Dialogs.showInput(getWindow(), "Enter the new name for this help section:", "Rename Help Section", page.getName());
            if(input != null) {
                if(StringUtil.isBlank(input)) {
                    Dialogs.showMessage(getWindow(), "Section name cannot be blank.", "Rename Error");
                } else {
                    dataModel.rename(page, input.trim());
                    treContents.updateUI();
                    page.save();       // For now, save whole album after every operation -- can change this one day
                }
            }
        });

        btnInfo.addActionListener(e -> {
            NodePage uSel = treContents.getSelObject();
            HelpPage page = uSel.page;
            HelpAlbum album = page.getAlbum();
            HelpProvider provider = album.getProvider();
            Dialogs.showMessage(getWindow(), "This help section is from " + provider.getName() + "\n\n" + provider.getDescription());
        });

        btnConfig.addActionListener(e -> {
            ConfigureHelpContextsDialog dlg = new ConfigureHelpContextsDialog(getWindow());
            dlg.setVisible(true);
            // TODO: Currently cheats a little and puts save action in the dialog itself...
        });

        dataModel.addPageContentChangedListener(e -> treContents.updateUI());

        rebuildForAlbumChange();
        rebuildForFilterChange();
    }

    private void promptNameAndAddNew(HelpProvider provider, RTreeNode nSel, NodeBase uSel) {
        String input = Dialogs.showInput(
            getWindow(), "Enter the name for the new help section:", "Add New Help Section", "New Section"
        );
        if(input != null) {
            if(StringUtil.isBlank(input)) {
                Dialogs.showMessage(getWindow(), "Section name cannot be blank.", "Add Error");
            } else {
                HelpPage childPage = new HelpPage(input);

                if(provider == null) {
                    HelpPage page = ((NodePage) uSel).page;
                    page.addChildPage(childPage);
                    childPage.setParent(page);
                    childPage.setAlbum(page.getAlbum());
                } else {
                    HelpAlbum album = provider.getAlbum();
                    album.addPageRoot(childPage);
                    childPage.setAlbum(album);
                    // childPage will have no parent
                }

                RTreeNode nChild = new RTreeNode(new NodePage(childPage));

                treContents.getTModel().append(nSel, nChild);
                treContents.expand(nSel);
                treContents.select(nChild);

                childPage.save();       // For now, save whole album after every operation -- can change this one day
            }
        }
    }

    private void rebuildForAlbumChange() {     // Updates panel's local term cache
//        terms.clear();
//        for(HelpAlbum album : dataModel.getAlbums()) {
//            for(HelpTermLink termLink : album.getTermLinks()) {
//                List<HelpTermGlob> globs = terms.get(termLink.term);
//                if(globs == null) {
//                    globs = new ArrayList<>();
//                    terms.put(termLink.term, globs);
//                }
//                globs.add(new HelpTermGlob(album, termLink));
//            }
//        }
    }

    private void rebuildForFilterChange() {    // Updates tree according to term cache and filter
        RTreeNode nRoot = new RTreeNode(new NodeSimpleLabel("All Help", CommonConcepts.LIBRARY));
        for(HelpAlbum album : dataModel.getAlbums()) {
            for(HelpPage pageRoot : album.getPageRoots()) {
                RTreeNode nPageRoot = nRoot.add(new NodePage(pageRoot));
                populate(nPageRoot, pageRoot);
            }
        }
        treContents.setModel(nRoot);
        treContents.expandAll();
    }

    private void populate(RTreeNode nPage, HelpPage page) {
        for(HelpPage child : page.getChildren()) {
            RTreeNode nPageChild = nPage.add(new NodePage(child));
            populate(nPageChild, child);
        }
    }
}
