package finio.plugins;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import finio.plugins.extpoints.JavaObjectEditor;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.scrutinize.ScrutinizePlugin;

public class FinioPluginManager {

    private static Map<Class<?>, List<JavaObjectEditor>> javaObjectEditorHandlers =
        new LinkedHashMap<>();

    public static void initialize() {
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(JavaObjectEditor.class);
        for(ExtensionPoint point : exts) {
            JavaObjectEditor editor = (JavaObjectEditor) point;
            List<Class<?>> handles = editor.getHandledClasses();
            if(handles != null) {
                for(Class<?> handle : handles) {
                    List<JavaObjectEditor> handlers = javaObjectEditorHandlers.get(handle);
                    if(handlers == null) {
                        handlers = new ArrayList<>();
                        javaObjectEditorHandlers.put(handle, handlers);
                    }
                    handlers.add(editor);
                }
            }
            if(editor.canHandleNull()) {
                List<JavaObjectEditor> handlers = javaObjectEditorHandlers.get(null);
                if(handlers == null) {
                    handlers = new ArrayList<>();
                    javaObjectEditorHandlers.put(null, handlers);
                }
                handlers.add(editor);
            }
        }

        PluginManager.initialize(ScrutinizePlugin.class);
    }

    public static List<JavaObjectEditor> getEditorsForObject(Object O) {
        List<JavaObjectEditor> applicableEditors = new ArrayList<>();

        if(O == null) {
            List<JavaObjectEditor> editorsE = javaObjectEditorHandlers.get(null);
            applicableEditors.addAll(editorsE);

        } else {
            for(Class<?> clazz : javaObjectEditorHandlers.keySet()) {
                if(clazz != null) {
                    if(clazz.isAssignableFrom(O.getClass())) {
                        List<JavaObjectEditor> editorsE = javaObjectEditorHandlers.get(clazz);
                        applicableEditors.addAll(editorsE);
                    }
                }
            }
        }

        if(applicableEditors.isEmpty()) {
            return null;
        }

        return applicableEditors;
    }
}