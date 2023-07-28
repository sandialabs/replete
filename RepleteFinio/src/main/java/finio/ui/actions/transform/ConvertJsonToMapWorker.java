package finio.ui.actions.transform;

import org.json.modified.JSONArray;
import org.json.modified.JSONObject;

import finio.core.FConst;
import finio.core.NonTerminal;
import finio.extractors.JsonExtractor;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ConvertJsonToMapWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConvertJsonToMapWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            Object V = C.getV();
            String S = V.toString();

            Object jsonObj;
            if(S.trim().startsWith("[")) {
                jsonObj = new JSONArray(S, true);
            } else {
                jsonObj = new JSONObject(S, true);
            }
            JsonExtractor X = new JsonExtractor(jsonObj);
            NonTerminal M = X.extract();
            M.putSysMeta(FConst.SYS_ALT_KEY, S);  // TODO need consistent way to add ALT_KEY

            Object K = C.getK();
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Mparent.put(K, M);                    // UI will start auto-updating at this point due to attached listeners, no way/reason to stop this

            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
            // Selection unchanged.
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "converting JSON to a map";
    }
}
