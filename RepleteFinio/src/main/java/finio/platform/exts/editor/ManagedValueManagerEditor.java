package finio.platform.exts.editor;

import java.util.Arrays;
import java.util.List;

import finio.core.managed.ManagedValueManager;
import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class ManagedValueManagerEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "Managed Value Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(
            new Class<?>[] {
                ManagedValueManager.class
            }
        );
    }

    @Override
    public boolean canHandleNull() {
        return false;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new ManagedValueManagerEditorPanel();
    }

}
