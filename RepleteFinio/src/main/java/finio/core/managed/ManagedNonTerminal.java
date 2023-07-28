package finio.core.managed;

import finio.core.NonTerminal;
import finio.manager.ManagedParameters;
import finio.plugins.extpoints.NonTerminalManager;

// Are ManagedNonTerminals ManagedValues or is there a naming issue?

public interface ManagedNonTerminal extends NonTerminal {


    /////////////
    // MANAGER //
    /////////////

    public NonTerminalManager getManager();
    // 1 method


    ////////////
    // PARAMS //
    ////////////

    public ManagedParameters getParams();
    public void setParams(ManagedParameters params);
    // 2 methods


    /////////////
    // LOADING //
    /////////////

    public void load();
    public void unload();
    public boolean isLoaded();
    // 3 methods


    /////////////////////////////
    // CHANGE ALLOWED/PROBABLE //
    /////////////////////////////

//    public boolean canLoad();
//    public boolean canUnload();
//    public boolean wouldLoad();
//    public boolean wouldUnload();
    // 4 methods

}

