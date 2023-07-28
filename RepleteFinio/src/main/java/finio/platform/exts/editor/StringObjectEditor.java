package finio.platform.exts.editor;

import java.util.Arrays;
import java.util.List;

import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class StringObjectEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "String (and More) Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(
            new Class<?>[] {
                String.class,
                Character.class,
                Boolean.class,
                Byte.class,
                Short.class,
                Integer.class,
                Long.class,
                Float.class,
                Double.class
            }
        );
    }

    @Override
    public boolean canHandleNull() {
        return true;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new StringObjectEditorPanel();
    }

}
