package finio.platform.exts.editor;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;

public class UrlObjectEditor implements JavaObjectEditor {

    @Override
    public String getName() {
        return "URL/URI Editor";
    }

    public List<Class<?>> getHandledClasses() {
        return Arrays.asList(new Class<?>[] {
            URL.class,
            URI.class
        });
    }

    @Override
    public boolean canHandleNull() {
        return false;
    }

    @Override
    public JavaObjectEditorPanel getEditorPanel(AppContext ac) {
        return new UrlObjectEditorPanel(ac);
    }

}
