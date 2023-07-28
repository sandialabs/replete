package finio.ui.actions.world;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class NewWorldWorker extends FWorker<Void, NonTerminal> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public NewWorldWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected NonTerminal background(Void gathered) throws Exception {
        NonTerminal W = FMap.A();
//        W.put("Example Import", ExampleDataGenerator.createExampleData());
        return W;
    }

    @Override
    public String getActionVerb() {
        return "loading a new world";
    }

    @Override
    protected void completeInner(NonTerminal W) {
        WorldContext wc = new WorldContext(ac)
            .setW(W)
            .setName("New World");
        ac.addWorld(wc);
    }
}
