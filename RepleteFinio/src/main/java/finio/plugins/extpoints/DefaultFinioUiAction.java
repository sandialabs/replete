package finio.plugins.extpoints;

import finio.ui.actions.FActionMap;
import finio.ui.app.AppContext;

public abstract class DefaultFinioUiAction implements FinioUIAction {
    public abstract void register(AppContext ac);
    public void init(FActionMap map) {

    }
}
