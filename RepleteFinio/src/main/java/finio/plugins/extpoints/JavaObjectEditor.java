package finio.plugins.extpoints;

import java.util.List;

import finio.ui.app.AppContext;
import replete.plugins.ExtensionPoint;

public interface JavaObjectEditor extends ExtensionPoint {
    public String getName();
    public List<Class<?>> getHandledClasses();
    public JavaObjectEditorPanel getEditorPanel(AppContext ac);
    public boolean canHandleNull();
}
