package finio.ui.actions.tools;

import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import gov.sandia.webcomms.http.ui.HttpRequestOptionsDialogSmall;

public class HttpRequestOptionsDialogWorker extends FWorker<Object[], Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public HttpRequestOptionsDialogWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Object[] gather() {
        HttpRequestOptionsDialogSmall dlg = new HttpRequestOptionsDialogSmall(
            ac.getWindow(), ac.getConfig().getRequestOptions(),
            ac.getConfig().getProxyHost(),
            ac.getConfig().getProxyPort());
        dlg.setVisible(true);
        return dlg.getResult() == HttpRequestOptionsDialogSmall.SET ?
            new Object[] {dlg.getProxyHost(), dlg.getProxyPort()} :
            null;
    }

    @Override
    protected boolean proceed(Object[] gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(Object[] proxy) throws Exception {
        ac.getConfig().setProxyHost((String) proxy[0]);
        ac.getConfig().setProxyPort((Integer) proxy[1]);
        ac.getConfig().initProxy();
        return null;
    }
}
