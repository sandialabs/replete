package finio.platform.exts.manager.trivial;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.example.ExampleDataGenerator;
import finio.manager.ManagedParameters;
import finio.manager.SimpleWrapperManagedNonTerminal;
import finio.plugins.extpoints.NonTerminalManager;

public class TrivialManagedNonTerminal extends SimpleWrapperManagedNonTerminal {


    ////////////
    // FIELDS //
    ////////////

    protected NonTerminal Mreal;
    protected NonTerminal Mblank;    // Basic way to implement an "unloaded" managed non-terminal
    private TrivialManagedParameters params;
    protected boolean loaded;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TrivialManagedNonTerminal(NonTerminalManager manager) {
        super(manager);
    }

    @Override
    protected void initSimple() {
        Mreal = ExampleDataGenerator.createExampleData();
        Mblank = FMap.A();
        loaded = true;
        params = new TrivialManagedParameters("Rocky", "Bullwinkle");
    }


    //////////////
    // ACCESSOR //
    //////////////

    @Override
    protected NonTerminal getM() {
        if(loaded) {
            return Mreal;
        }
        return Mblank;
    }


    ////////////////
    // MANAGEMENT //
    ////////////////

    @Override
    public ManagedParameters getParams() {
        return params;
    }
    @Override
    public void setParams(ManagedParameters params) {
        this.params = (TrivialManagedParameters) params;
    }
    @Override
    public void load() {
        loaded = true;
        notifyBatchUpdate();
    }
    @Override
    public void unload() {
        loaded = false;
        notifyBatchUpdate();
    }
    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
