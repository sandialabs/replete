package finio.ui.actions.world;

import java.io.File;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.xstream.XStreamWrapper;

public class OpenWorldWorker extends FWorker<File[], NonTerminal[]> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public OpenWorldWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected File[] gather() {
        RFileChooser fc = RFileChooser.getChooser("Open World");
        RFilterBuilder builder = new RFilterBuilder(fc, false);
        builder.append("World Files (*.world)", "world");
        return fc.showOpen(ac.getWindow()) ? fc.getSelectedFiles() : null;
    }

    @Override
    protected boolean proceed(File[] gathered) {
        return gathered != null;
    }

    @Override
    protected NonTerminal[] background(File[] files) throws Exception {
        NonTerminal[] worlds = new NonTerminal[files.length];
        int w = 0;
        for(File file : files) {
            worlds[w++] = (NonTerminal) XStreamWrapper.loadTarget(file);
        }
        return worlds;
    }

    @Override
    public String getActionVerb() {
        return "loading this world";
    }

    @Override
    protected void completeInner(NonTerminal[] Ws) {
        int w = 0;
        for(NonTerminal W : Ws) {
            WorldContext wc = new WorldContext(ac)
                .setW(W)
                .setSource(gathered[w++])
                .setDirty(false);
            ac.addWorld(wc);
        }
    }
}
