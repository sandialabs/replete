package finio.platform.exts.editor;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class RectangleJavaObjectEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "Rectangle Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(new Class<?>[] {
            Rectangle.class
        });
    }

    @Override
    public boolean canHandleNull() {
        return false;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new RectangleJavaObjectEditorPanel();
    }

}
