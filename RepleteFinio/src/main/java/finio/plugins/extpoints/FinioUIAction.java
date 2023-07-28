package finio.plugins.extpoints;

import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;
import replete.plugins.ExtensionPoint;

public interface FinioUIAction extends ExtensionPoint {
    public void register(AppContext ac);
    public void init(FActionMap map);
}
