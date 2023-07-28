package finio.platform.exts.view.treeview.ui.actions;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.tree.RTreePath;
import replete.ui.windows.Dialogs;

public class ToggleSysMetaWorker extends FWorker<String, Void> {


    ////////////
    // FIELDS //
    ////////////

    private boolean show;
    private boolean recursive;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ToggleSysMetaWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String EN = "Enable";
        String ER = "Enable (Recursive)";
        String DN = "Disable";
        String DR = "Disable (Recursive)";
        String CN = "Cancel";
        Map<String, Icon> buttonIcons = new HashMap<>();
        buttonIcons.put(EN, ImageLib.get(FinioImageModel.METAMAP));
        buttonIcons.put(ER, ImageLib.get(FinioImageModel.METAMAP));
        buttonIcons.put(DN, ImageLib.get(FinioImageModel.METAMAP_DISABLED));
        buttonIcons.put(DR, ImageLib.get(FinioImageModel.METAMAP_DISABLED));
        buttonIcons.put(CN, ImageLib.get(CommonConcepts.CANCEL));

        int result = Dialogs.showMulti(ac.getWindow(),
            "How you would like to enable/diable the SysMeta maps?", getName(),
            new String[] {EN, ER, DN, DR, CN}, -1, buttonIcons
        );

        if(result != -1 && result != 4) {
            show = result == 0 || result == 1;
            recursive = result == 1 || result == 3;
            return "go";
        }

        return null;
    }

    @Override
    protected boolean proceed(String gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(String gathered) throws Exception {
        FTree tree = wc.getWorldPanel().getTreePanel().getTree();
        RTreePath[] pSels = tree.getSelPaths();                // Order Selected
        for(RTreePath pSel : pSels) {
            FNode nSel = (FNode) pSel.getLast();
            nSel.setShowAlphaMeta(show, recursive);
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "toggling system meta nodes";
    }
}
