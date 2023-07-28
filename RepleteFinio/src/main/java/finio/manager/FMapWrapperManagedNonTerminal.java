package finio.manager;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.plugins.extpoints.NonTerminalManager;

public class FMapWrapperManagedNonTerminal extends SimpleWrapperManagedNonTerminal {

    // Not unloadable/reloadable, always loaded.  Not refreshable.  No parameters.


    ///////////
    // FIELD //
    ///////////

    private NonTerminal M;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FMapWrapperManagedNonTerminal(NonTerminalManager manager) {
        super(manager);
    }

    @Override
    protected void initSimple() {
        M = FMap.A();
    }


    //////////////
    // ACCESSOR //
    //////////////

    @Override
    protected NonTerminal getM() {
        return M;
    }

    public static void main(String[] args) {
//        AMapWrapperManagedNonTerminal G = new AMapWrapperManagedNonTerminal(null);
//System.out.println(Integer.toHexString(System.identityHashCode(G)) + "/" + G.getClass().getSimpleName());
//        G.addKeyAddedListener(new KeyAddedListener() {
//            public void keyAdded(KeyAddedEvent e) {
//                System.out.println(e);
//            }
//        });
//        G.put("a", 2);
    }
}
