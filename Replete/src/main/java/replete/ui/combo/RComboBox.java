package replete.ui.combo;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;
import java.util.function.BiFunction;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import replete.equality.EqualsUtil;
import replete.errors.UnexpectedEnumValueUnicornException;
import replete.event.ChangeNotifier;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;
import replete.ui.windows.ExampleFrame;


public class RComboBox<T> extends JComboBox<T> implements SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum ComboBoxSelectionStateIdentityMethod implements SelectionStateCreationMethod {
        INDEX,                 // (Default) Just record the selected index
        OBJECT_CLASS_NAME,     // Record selected item's class name
        OBJECT_HASH_CODE,      // Record selected item's hash code
        OBJECT_REFERENCE,      // Record selected item's reference
        CUSTOM                 // BiFunction<Integer, Object, Object> as 2nd argument = customIdentityFunction
    }


    ////////////
    // FIELDS //
    ////////////

    private boolean selectAllEnabled = false;
    private boolean selectionChangeable = true;
    private boolean popupOpenedByMouse = false;
    private boolean popupUp = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RComboBox() {
        init();
    }
    public RComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
        init();
    }
    public RComboBox(T[] items) {
        super(items);
        init();
    }
    public RComboBox(Vector<T> items) {
        super(items);
        init();
    }

    private void init() {
        setBackground(Color.white);
        getEditor().addActionListener(editorActionListener);
        addActionListener(e -> {
            if(popupOpenedByMouse) {
                fireAcceptanceNotifier();
            }
        });
        addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                popupUp = true;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                popupUp = false;
                popupOpenedByMouse = false;
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                popupUp = false;
                popupOpenedByMouse = false;
            }
        });

        for(Component component : getComponents()) {
            if(component instanceof JButton) {
                ((JButton) component).addActionListener(e -> {
                    if(popupUp) {
                        popupOpenedByMouse = true;
                    }
                });
            }
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isSelectAll() {
        return selectAllEnabled;
    }

    // Accessors (Computed)

    public String getText() {
        return getTextField().getText();
    }
    public String getTrimmed() {
        return getText().trim();
    }
    public int getTextLength() {
        return getText().length();
    }
    public boolean isBlank() {
        return getTrimmed().isEmpty();
    }
    public boolean isEmpty() {
        return getText().isEmpty();
    }

    public T getSelected() {
        return (T) getSelectedItem();
    }
    public JTextField getTextField() {
        return (JTextField) getEditor().getEditorComponent();
    }

    // Mutators

    public void setText(String str) {
        getTextField().setText(str);
    }

    public void setSelectedLast() {
        int sz = getModel().getSize();
        if(sz != 0) {
            setSelectedIndex(sz - 1);
        }
    }
    public RComboBox<T> setSelectAll(boolean selectAllEnabled) {
        this.selectAllEnabled = selectAllEnabled;

        ComboBoxEditor cur = getEditor();
        if(cur != null) {
            cur.getEditorComponent().removeFocusListener(selectAllFocusListener);
            if(selectAllEnabled) {
                cur.getEditorComponent().addFocusListener(selectAllFocusListener);
            }
        }

        return this;
    }

    public RComboBox<T> setSelectionChangeable(boolean selectionChangeable) {
        this.selectionChangeable = selectionChangeable;
        return this;
    }


    //////////
    // MISC //
    //////////

    private ActionListener editorActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            fireAcceptanceNotifier();
        }
    };
    private FocusListener selectAllFocusListener = new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            getTextField().selectAll();
        }
    };

    public void focus() {
        requestFocusInWindow();
    }


    ///////////////
    // LISTENERS //
    ///////////////

    public void addChangeListener(DocumentListener listener) {
        getTextField().getDocument().addDocumentListener(listener);
    }
    public void removeChangeListener(DocumentListener listener) {
        getTextField().getDocument().removeDocumentListener(listener);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setEditor(ComboBoxEditor anEditor) {
        ComboBoxEditor prev = getEditor();
        if(prev != null) {
            prev.getEditorComponent().removeFocusListener(selectAllFocusListener);
            getEditor().removeActionListener(editorActionListener);
        }
        super.setEditor(anEditor);
        if(anEditor != null && selectAllEnabled) {
            anEditor.getEditorComponent().addFocusListener(selectAllFocusListener);
            getEditor().addActionListener(editorActionListener);
        }
    }

    public void setSelectedItemForce(Object object) {
        boolean prev = selectionChangeable;
        selectionChangeable = true;
        setSelectedItem(object);
        selectionChangeable = prev;
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if(selectionChangeable) {
            super.setSelectedItem(anObject);
        }
    }
    public void setSelected(Object anObject) {
        setSelectedItem(anObject);
    }

    @Override
    public SelectionState getSelectionState(Object... args) {
        ComboBoxSelectionStateIdentityMethod method =
            getDefaultArg(args, ComboBoxSelectionStateIdentityMethod.INDEX);

        // Method-specific arguments
        BiFunction<Integer, Object, Object> customIdentityFunction =
            getDefaultArg(args, BiFunction.class, 1);

        int selIndex = getSelectedIndex();
        Object selItem = getSelectedItem();

        Object key;
        if(selIndex != -1) {
            key = makeKey(selIndex, selItem, method, customIdentityFunction);
        } else {
            key = null;   // selIndex == -1, model size == 0
        }

        return new SelectionState(
            "selected",  key,
            "method",    method,
            "ifunction", customIdentityFunction
        );
    }

    @Override
    public void setSelectionState(SelectionState state) {
        Object selected = state.getGx("selected");
        ComboBoxSelectionStateIdentityMethod method = state.getGx("method");
        BiFunction<Integer, Object, Object> customIdentityFunction = state.getGx("ifunction");


        ComboBoxModel mdl = getModel();
        setSelectedIndex(mdl.getSize() == 0 ? -1 : 0);

        if(selected != null) {
            for(int row = 1; row < mdl.getSize(); row++) {
                Object elem = mdl.getElementAt(row);
                Object key = makeKey(row, elem, method, customIdentityFunction);
                if(EqualsUtil.equals(key, selected)) {
                    setSelectedIndex(row);
                    // ^To be consistent with RTable and RTree we don't break,
                    //  allowing any further elements also cause this to switch.
                    //  Whether this is desirable or appropriate comes down to
                    //  the quality of the strategy applied to the data in the
                    //  combo box.
                }
            }
        }
    }

    private Object makeKey(int selIndex, Object selItem,
                           ComboBoxSelectionStateIdentityMethod method,
                           // These are the method-specific params -->
                           BiFunction<Integer, Object, Object> customIdentityFunction) {

        if(method == ComboBoxSelectionStateIdentityMethod.INDEX) {
            return selIndex;

        } else if(method == ComboBoxSelectionStateIdentityMethod.OBJECT_CLASS_NAME) {
            if(selItem != null) {
                return selItem.getClass().getName();
            }
            return null;

        } else if(method == ComboBoxSelectionStateIdentityMethod.OBJECT_HASH_CODE) {
            if(selItem != null) {
                return selItem.hashCode();
            }
            return null;

        } else if(method == ComboBoxSelectionStateIdentityMethod.OBJECT_REFERENCE) {
            return selItem;

        } else if(method == ComboBoxSelectionStateIdentityMethod.CUSTOM) {
            return customIdentityFunction.apply(selIndex, selItem);

        } else {
            throw new UnexpectedEnumValueUnicornException(method);
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private transient ChangeNotifier acceptanceNotifier = new ChangeNotifier(this);
    public void addAcceptanceListener(ChangeListener listener) {
        acceptanceNotifier.addListener(listener);
    }
    private void fireAcceptanceNotifier() {
        acceptanceNotifier.fireStateChanged();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String laf = UIManager.getCrossPlatformLookAndFeelClassName();
        try { UIManager.setLookAndFeel(laf); } catch(Exception e) {}
        JFrame frame = new ExampleFrame();
        RComboBox cbo = new RComboBox(new Object[] {"Saturn", "Mars", "Neptune"});
        Lay.FLtg(frame, cbo, new JTextField("asdlfksj"));
        frame.setVisible(true);
    }
}
