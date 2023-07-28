package finio.platform.exts.manager.xstream;

import java.io.File;
import java.io.IOException;

import finio.core.NonTerminal;
import finio.manager.ManagedParameters;
import finio.platform.exts.manager.trivial.TrivialManagedNonTerminal;
import replete.util.User;
import replete.xstream.XStreamWrapper;

public class XStreamFileManagedNonTerminal extends TrivialManagedNonTerminal {


    ///////////
    // FIELD //
    ///////////

    private XStreamFileManagedParameters params;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public XStreamFileManagedNonTerminal(XStreamFileMapManager manager) {
        super(manager);
    }

    @Override
    protected void initSimple() {
        super.initSimple();

        File file = new File(User.getDesktop(), "test.xml");
        params = new XStreamFileManagedParameters(file);
    }


    //////////
    // MISC //
    //////////

    @Override
    public boolean isRefreshable() {
        return true;
    }
    @Override
    public void refresh() {
        load();
    }
    // 2/16 methods


    ////////////////
    // MANAGEMENT //
    ////////////////

    @Override
    public ManagedParameters getParams() {
        return params;
    }
    @Override
    public void setParams(ManagedParameters params) {
        this.params = (XStreamFileManagedParameters) params;
    }
    @Override
    public void load() {
        try {
            Mreal = (NonTerminal) XStreamWrapper.loadTarget(params.getFile());
            loaded = true;
            subscribe();
            notifyBatchUpdate();
        } catch(IOException e) {
            throw new RuntimeException("Could not load");
        }
    }
    @Override
    public void unload() {
        try {
            unsubscribe();
            XStreamWrapper.writeToFile(Mreal, params.getFile());
            loaded = false;
            Mreal = null;
            notifyBatchUpdate();
        } catch(IOException e) {
            throw new RuntimeException("Could not unload");
        }
    }
}
