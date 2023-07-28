package finio.ui.actions.change;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class AddKeyValuePairWorker extends FWorker<String, Void> {


    ////////////
    // FIELDS //
    ////////////

    private Object Vinit;
    private boolean sibling;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AddKeyValuePairWorker(AppContext ac, WorldContext wc,
                                 String name, Object Vinit, boolean sibling) {
        super(ac, wc, name);
        this.Vinit = Vinit;
        this.sibling = sibling;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(String gathered) throws Exception {

        if(sibling) {
            List<NonTerminal> Vparents = new ArrayList<>();
            Map<Integer, SelectionContext> contextMap = new LinkedHashMap<>();

            int i = 0;
            for(SelectionContext C : getValidSelected()) {
                if(C.getParentV() != null) {
                    if(!Vparents.contains(C.getParentV())) {
                        Vparents.add((NonTerminal) C.getParentV());
                        contextMap.put(i++, C);
                    }
                }
            }

            i = 0;
            for(NonTerminal Vparent : Vparents) {
                Object K = Vparent.getNextAvailableKey();
                Object V = Vinit;
                Vparent.put(K, V);
                SelectionContext C = contextMap.get(i++);
                select(
                    new SelectRequest()
                        .setContext(C)
                        .setAction(SelectAction.SIBLING)
                        .setArgs(K)
                );
                expand(
                    new ExpandRequest()
                        .setContext(C)
                        .setAction(SelectAction.SIBLING)
                        .setArgs(K)
                );
            }

        } else {
            boolean first = true;

            for(SelectionContext C : getValidSelected()) {
                NonTerminal Vparent = (NonTerminal) C.getV();
                Object K = Vparent.getNextAvailableKey();
                Object V = Vinit;
                Vparent.put(K, V);

                // TODO: child selected is arbitrarily of the FIRST NT selected...
                // This can be very confusing, and really should "select" them all
                // for the EDITING!!  this would be so cool.  Implement that when
                // we figure out how to make EDIT multi select!!
                if(first) {
                    select(
                        new SelectRequest()
                            .setContext(C)
                            .setAction(SelectAction.CHILD)
                            .setArgs(K)
                    );
                } else {
                    expand(
                        new ExpandRequest()
                            .setContext(C)
                            .setAction(SelectAction.SELF)
                    );
                }
                expand(
                    new ExpandRequest()
                        .setContext(C)
                        .setAction(SelectAction.CHILD)
                        .setArgs(K)
                );
            }
        }

        setShouldEdit(false);
        return null;
    }
}
