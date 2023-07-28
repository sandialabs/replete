package finio.platform.exts.view.treeview.ui.editors;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeSelectionModel;

import finio.core.FUtil;
import finio.platform.exts.editor.StringObjectEditor;
import finio.platform.exts.editor.UnknownNativeObjectEditor;
import finio.platform.exts.view.treeview.ui.FNode;
import finio.platform.exts.view.treeview.ui.FTree;
import finio.platform.exts.view.treeview.ui.nodes.NodeFTree;
import finio.plugins.FinioPluginManager;
import finio.plugins.extpoints.JavaObjectEditor;
import finio.plugins.extpoints.JavaObjectEditorPanel;
import finio.ui.app.AppContext;
import replete.collections.Pair;
import replete.ui.GuiUtil;
import replete.ui.windows.Dialogs;

public abstract class TreeValueEditorPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    protected Object K;
    protected Object V;

    protected AppContext ac;
    protected FTree tree;
    protected FontMetrics fontMetrics;

    protected ActionListener acceptListener;
    protected ActionListener cancelListener;
    protected KeyListener keyListener;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TreeValueEditorPanel(AppContext ac, FTree tree,
                                ActionListener acceptListener,
                                ActionListener cancelListener,
                                KeyListener keyListener,
                                FontMetrics fontMetrics) {
        this.ac = ac;
        this.tree = tree;
        this.acceptListener = acceptListener;
        this.cancelListener = cancelListener;
        this.keyListener = keyListener;
        this.fontMetrics = fontMetrics;
    }


    /////////////
    // MUTATOR //
    /////////////

    public void setFontMetrics(FontMetrics fontMetrics) {
        this.fontMetrics = fontMetrics;
    }
    public abstract void setTreeFont(Font font);


    //////////////
    // ABSTRACT //
    //////////////

    public abstract NodeFTree getNewUserObject(FNode nCurrent);
    public abstract boolean changesValue();
    public abstract void setKeyValue(Object K, Object V);

    protected void showTypeMessage(String name, Object O) {
        JFrame parent = GuiUtil.fra(this);
        String msg =
            "The tree cell editor type handling the editing of this object is:\n" +
            "\n      " + name +
            "\n\nThese were all the editors available for this object:\n";
        List<JavaObjectEditor> editors = FinioPluginManager.getEditorsForObject(O);
        if(editors == null) {
            msg += "\n      (none)";
        } else {
            for(JavaObjectEditor editor : editors) {
                msg += "\n      " + editor.getName();
            }
        }
        Dialogs.showMessage(parent, msg, "Object Editor Type");
    }

    protected Pair<JavaObjectEditor, JavaObjectEditorPanel> findAndAddObjectEditorPanel(final JPanel pnlEditorContainer, Object O) {

        JavaObjectEditor editor = findInitialMostSpecificApplicableObjectEditor(O);

        JavaObjectEditorPanel pnlEditor = editor.getEditorPanel(ac);
        pnlEditor.addPreferredSizeChangedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pnlEditorContainer.updateUI();
                updateUI();
                TreeSelectionModel model = tree.getSelectionModel();
                ((AbstractLayoutCache) model.getRowMapper()).invalidateSizes();
                tree.treeDidChange();
                setSize(getPreferredSize());
            }
        });

        pnlEditor.setObject(O);
        pnlEditor.setKeyListener(keyListener);   // Total hack for now.
        pnlEditor.setFont(pnlEditorContainer.getFont());

        pnlEditorContainer.removeAll();
        pnlEditorContainer.add(pnlEditor, BorderLayout.CENTER);

        return new Pair<>(editor, pnlEditor);
    }

    private JavaObjectEditor findInitialMostSpecificApplicableObjectEditor(Object O) {
        JavaObjectEditor chosenEditor = null;

        List<JavaObjectEditor> editors = FinioPluginManager.getEditorsForObject(O);
        if(editors == null) {
            chosenEditor = new UnknownNativeObjectEditor();
        } else {

            // For a null value, choose the string object editor
            // as its initial editor.
            if(O == null) {
                for(JavaObjectEditor editor : editors) {
                    if(editor.getClass().equals(StringObjectEditor.class)) {
                        chosenEditor = editor;
                        break;
                    }
                }

            } else {
                List<ClassEditorTuple> matchingTypes = new ArrayList<>();
                for(JavaObjectEditor editor : editors) {
                    List<Class<?>> classes = editor.getHandledClasses();
                    if(classes != null) {
                        for(Class<?> clazz : classes) {
                            if(clazz.isAssignableFrom(O.getClass())) {
                                ClassEditorTuple pair = new ClassEditorTuple(clazz, editor);
                                matchingTypes.add(pair);
                            }
                        }
                    }
                }

                for(int i = 0; i < matchingTypes.size(); i++) {
                    for(int j = 0; j < matchingTypes.size(); j++) {
                        if(i != j) {
                            ClassEditorTuple ix = matchingTypes.get(i);
                            ClassEditorTuple jx = matchingTypes.get(j);
                            if(!ix.removed && !jx.removed) {
                                if(ix.editor != jx.editor) {
                                    if(ix.clazz.isAssignableFrom(jx.clazz) &&
                                        !jx.clazz.isAssignableFrom(ix.clazz)) {
                                        ix.removed = true;
                                    }
                                }
                            }
                        }
                    }
                }

                for(ClassEditorTuple matchingType : matchingTypes) {
                    if(!matchingType.removed) {
                        chosenEditor = matchingType.editor;
                    }
                }
            }
        }
        return chosenEditor;
    }

    protected Object chooseNewObject(JavaObjectEditorPanel pnlEditorPanel, Object Ocur) {
        Object Onew;
        if(pnlEditorPanel.allowsEdit()) {
            Onew = pnlEditorPanel.getObject();
            if(FUtil.isString(Onew)) {
                Onew = Onew.toString().trim();  // Option?
            }
        } else {
            Onew = Ocur;
        }
        return Onew;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class ClassEditorTuple {
        private Class<?> clazz;
        private JavaObjectEditor editor;
        private boolean removed = false;

        public ClassEditorTuple(Class<?> clazz, JavaObjectEditor editor) {
            this.clazz = clazz;
            this.editor = editor;
        }

        @Override
        public String toString() {
            return "ClassEditorTuple [clazz=" + clazz + ", editor=" + editor + ", removed=" + removed + "]";
        }
    }
}
