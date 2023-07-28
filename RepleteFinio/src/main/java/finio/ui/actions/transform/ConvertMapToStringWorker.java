package finio.ui.actions.transform;

import finio.core.NonTerminal;
import finio.core.syntax.FMapSyntax;
import finio.renderers.map.StandardAMapRenderer;
import finio.ui.SyntaxSelectionDialog;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ConvertMapToStringWorker extends FWorker<FMapSyntax, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConvertMapToStringWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected FMapSyntax gather() {
        SyntaxSelectionDialog dlg = new SyntaxSelectionDialog(
            ac.getWindow());
        dlg.setVisible(true);
        return dlg.getResult() == SyntaxSelectionDialog.SELECT ? dlg.getSyntax() : null;
    }

    @Override
    protected boolean proceed(FMapSyntax gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(FMapSyntax syntax) throws Exception {
        StandardAMapRenderer renderer = new StandardAMapRenderer(syntax);
        for(SelectionContext C : getValidSelected()) {
            String S = renderer.renderValue(C.getV());
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Mparent.put(C.getK(), S);
            ac.sendToConsole(S);
            // Selection/expansion unchanged.
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "converting the map to a string";
    }
}
