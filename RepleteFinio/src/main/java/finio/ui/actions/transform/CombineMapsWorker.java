package finio.ui.actions.transform;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class CombineMapsWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CombineMapsWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {
        List<SelectionContext> Cs = getValidSelected();
        Set<Object> Ks = new LinkedHashSet<>();
        NonTerminal Mtarget = null;
        for(SelectionContext C : Cs) {
            NonTerminal M = (NonTerminal) C.getV();
            Ks.addAll(M.K());
            Mtarget = M;
        }
        for(Object K : Ks) {
            for(SelectionContext C : Cs) {
                NonTerminal M = (NonTerminal) C.getV();
                if(M == Mtarget) {
                    continue;
                }
                if(M.has(K)) {
                    Object V = M.get(K);
                    if(Mtarget.has(K)) {
                        Object Vtarget = Mtarget.get(K);
                        if(!FUtil.equals(V, Vtarget)) {
                            if(FUtil.isStringOrChar(Mtarget.get(K)) && FUtil.isStringOrChar(M.get(K))) {
                                Mtarget.put(K, Mtarget.get(K) + " " + M.get(K));
                            }
                        }
                    } else {
                        Mtarget.put(K, M.get(K));
                    }
                }
            }
        }
        for(SelectionContext C : Cs) {
            select(
                new SelectRequest()
                    .setContext(C)
                    .setAction(SelectAction.SELF)
            );
            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SELF)
            );
        }
        return null;
    }

    @Override
    public String getActionVerb() {
        return "combining maps";
    }
}
