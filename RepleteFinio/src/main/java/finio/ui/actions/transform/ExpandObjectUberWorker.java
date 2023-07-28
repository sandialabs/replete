package finio.ui.actions.transform;

import finio.core.NonTerminal;
import finio.extractors.jo.JavaObjectUberExtractor;
import finio.extractors.jo.PopulateParamsProvider;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.extractors.PopulateParamsProviderDialog;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ExpandObjectUberWorker extends FWorker<PopulateParamsProvider, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExpandObjectUberWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected PopulateParamsProvider gather() {
        PopulateParamsProviderDialog dialog =
            new PopulateParamsProviderDialog(ac.getWindow());
        dialog.setVisible(true);
        if(dialog.getResult() == PopulateParamsProviderDialog.OK) {
            return dialog.getProvider();
        }
        return null;
    }

    @Override
    protected boolean proceed(PopulateParamsProvider provider) {
        return provider != null;
    }

    @Override
    protected Void background(PopulateParamsProvider provider) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object V = C.getV();

            JavaObjectUberExtractor X =
                new JavaObjectUberExtractor(V, provider);
            Object VselNew = X.extract();

            Object K = C.getK();
            Mparent.put(K, VselNew);

            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "expanding the object";
    }
}
