package finio.ui.actions.change;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class RemoveWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RemoveWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // NEEDS HIERARCHY CHECKING!
    // TODO: Research how to convert a
    // sequence of paths into a sequence of paths that are
    // mutually exclusive. and only delete those paths.
    @Override
    protected Void background(Void gathered) throws Exception {

        // Do we need to worry whether or not the values
        // chosen are still in the map at this point?
        // They should be right...
        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Mparent.removeByKey(C.getK());
//            select(
//                new SelectRequest()
//                    .setContext(C)
//                    .setAction(SelectAction.REMOVE)
//                    .setArgs(?)
//            );
        }

        // Standard list-delete procedure....
//        int position = nParent.getIndex(nSel);
//        if(position == nParent.getCount()) {
//            position--;
//        }
//        if(position >= 0) {
//            newSelPaths.add(nParent.getTChildAt(position).getTPath());
//        } else {
//            newSelPaths.add(nParent.getTPath());
//        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "removing this key-value pair";
    }

    // TODO: (BUG) Selection doesn't work if you select all of a Map's children and delete.
//    @Override
//    protected void completex() {
//        try {
//            getResult();
//            TPath minPath = tree.highestPath(newSelPaths);
//            if(minPath != null) {   // Needed?
//                if(tree.getRoot().exists(minPath)) {
//                    tree.addSelectionPath(minPath);
//                }
//            }
//
//        } catch(ExecutionException e) {
//            Dialogs.showDetails(ac.getWindow(),
//                "An error has occurred.", name, e.getCause());
//
//        } catch(Exception e) {
//            Dialogs.showDetails(ac.getWindow(),
//                "An error has occurred.", name, e);
//        }
//    }
}
