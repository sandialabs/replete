package finio.platform.exts.editor;

import java.util.Arrays;
import java.util.List;

import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class UnknownNativeObjectEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "Base View-Only Object Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(new Class<?>[] {
            Object.class
        });
    }

    @Override
    public boolean canHandleNull() {
        return true;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new UnknownNativeObjectEditorPanel();
    }

}
