package finio.ui.actions.transform;

import java.util.ArrayList;
import java.util.List;

import finio.core.KeyPath;
import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.text.StringUtil;
import replete.ui.windows.Dialogs;

public class FlattenWorker extends FWorker<String, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FlattenWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String gather() {
        String msg =
            "Please enter the string delimiter to be used to join key\n" +
            "segments during the flattening process.  Please remember\n" +
            "this delimiter should not appear in any of the key segments\n" +
            "or hierarchy reconstruction may be compromised. Examples of\n" +
            "delimiters include the comma (,), period (.), semi-colon (;)\n" +
            "or any character you would like to separate your keys.";
        return Dialogs.showInput(ac.getWindow(), msg, "Flatten Delimiter");
    }

    @Override
    protected boolean proceed(String gathered) {
        if(gathered == null) {
            return false;
        }

        List<Object[]> questionable = new ArrayList<>();

        for(SelectionContext C : getValidSelected()) {
            Object V = C.getV();

            if(V instanceof NonTerminal) {
                NonTerminal M = (NonTerminal) V;
                NonTerminal Mf = M.flatten();

                Object K = C.getK();
                if(K.toString().contains(gathered)) {
                    questionable.add(new Object[] {C, K});
                }

                for(Object Kn : Mf.keySet()) {
                    KeyPath P = (KeyPath) Kn;
                    for(Object Kp : P) {
                        String s = Kp.toString();
                        if(s.contains(gathered)) {
                            questionable.add(new Object[]{C, Kp});
                        }
                    }
                }
            }
        }

        if(!questionable.isEmpty()) {
            String s =
                "The following keys contain the chosen delimiter.\n" +
                "This may compromise hierarchy reconstruction.\n";
            int added = 0;
            for(Object[] q : questionable) {
                SelectionContext C = (SelectionContext) q[0];
                Object K = q[1];
                s += "\n      " + C.getK() + " (questionable key = " + K + ")";
                added++;
                if(added == 10) {
                    break;
                }
            }
            if(questionable.size() > added) {
                s += "\n      ...(" + (questionable.size() - added) + " more)...";
            }
            s += "\n\nDo you wish to continue?";
            boolean ok = Dialogs.showConfirm(ac.getWindow(), s, "Continue?", true);
            return ok;
        }

        return true;
    }

    @Override
    protected Void background(String gathered) throws Exception {
        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object V = C.getV();

            if(V instanceof NonTerminal) {
                NonTerminal M = (NonTerminal) V;
                NonTerminal Mf = M.flatten();

                Object K = C.getK();
                Mparent.removeByKey(K);

                for(Object Kn : Mf.keySet()) {
                    KeyPath P = (KeyPath) Kn;
                    String s = P.toString();
                    if(s.contains(gathered)) {

                    }
                }

                for(Object Kn : Mf.keySet()) {
                    KeyPath P = (KeyPath) Kn;
                    Object Vn = Mf.get(Kn);
                    P.prepend(K);
                    String s = StringUtil.join(P, gathered);
                    Mparent.put(s, Vn);

                    select(
                        new SelectRequest()
                            .setContext(C)
                            .setAction(SelectAction.SIBLING)
                            .setArgs(s)
                    );
                }
            }
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "flattening the map";
    }
}
