package finio.platform.exts.editor;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class FileObjectEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "File/Directory Path Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(new Class<?>[] {
            File.class
        });
    }

    @Override
    public boolean canHandleNull() {
        return true;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new FileObjectEditorPanel(ac);
    }

}
