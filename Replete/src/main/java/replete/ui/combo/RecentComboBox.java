package replete.ui.combo;

import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import replete.ui.lay.Lay;
import replete.ui.nofire.NoFireComboBox;
import replete.ui.text.validating.Validator;
import replete.ui.windows.escape.EscapeFrame;

public class RecentComboBox<T> extends NoFireComboBox<T> {


    ////////////
    // FIELDS //
    ////////////

    private UnaryOperator<String> inputCleaner;
    private BiPredicate<String, T> inputEqualizer;
    private Predicate<Object> trivialInputDecider;
    private boolean similarInputReplaces = false;
    private boolean pushHistoryOnAction = false;
    private final ActionListener actionListener = e -> pushHistory();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RecentComboBox() {
        super();
    }
    public RecentComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
    }
    public RecentComboBox(T[] items) {
        super(items);
    }
    public RecentComboBox(Vector<T> items) {
        super(items);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public UnaryOperator<String> getInputCleaner() {
        return inputCleaner;
    }
    public BiPredicate<String, T> getInputEqualizer() {
        return inputEqualizer;
    }
    public Predicate<Object> getTrivialInputDecider() {
        return trivialInputDecider;
    }
    public boolean isSimilarInputReplaces() {
        return similarInputReplaces;
    }
    public boolean isPushHistoryOnAction() {
        return pushHistoryOnAction;
    }

    // Mutators

    public void setInputCleaner(UnaryOperator<String> inputCleaner) {
        this.inputCleaner = inputCleaner;
    }
    public void setInputEqualizer(BiPredicate<String, T> inputEqualizer) {
        this.inputEqualizer = inputEqualizer;
    }
    public void setTrivialInputDecider(Predicate<Object> trivialInputDecider) {
        this.trivialInputDecider = trivialInputDecider;
    }
    public void setSimilarInputReplaces(boolean similarInputReplaces) {
        this.similarInputReplaces = similarInputReplaces;
    }
    public void setPushHistoryOnAction(boolean pushHistoryOnAction) {
        this.pushHistoryOnAction = pushHistoryOnAction;

        ComboBoxEditor cur = getEditor();
        if(cur != null) {
            cur.removeActionListener(actionListener);
            if(pushHistoryOnAction) {
                cur.addActionListener(actionListener);
            }
        }
    }


    /////////////
    // HISTORY //
    /////////////

    protected String cleanInput(String input) {
        if(inputCleaner != null) {
            return inputCleaner.apply(input);
        }
        return input;
    }

    public Object pushHistory() {

        // Determine the user's input.
        Object sel;
        if(isEditable()) {
            sel = cleanInput(getText());
        } else {
            sel = getSelectedItem();
        }
        if(trivialInputDecider != null && trivialInputDecider.test(sel)) {
            return null;
        }

        // Decide if that input corresponds to any
        // element currently in the model.
        DefaultComboBoxModel<T> mdl = (DefaultComboBoxModel) getModel();
        int index;
        if(isEditable() && inputEqualizer != null) {
            index = -1;
            for(int i = 0; i < mdl.getSize(); i++) {
                T elem = mdl.getElementAt(i);
                if(inputEqualizer.test((String) sel, elem)) {
                    index = i;
                    break;
                }
            }
        } else {
            index = mdl.getIndexOf(sel);
        }

        // Remove the element at the index identified.
        Object remove = null;
        if(index != -1) {
            remove = mdl.getElementAt(index);
            mdl.removeElementAt(index);
        }

        // Re-insertion Condition Matrix:
        //   index == -1    ||  index != -1
        //   mdl.size == 0  ||  mdl.size != 0
        //   elems strings  ||  elems not strings
        //   sel String     ||  sel Bean          || sel null
        // 2 * 2 * 2 * 3 = 24 scenarios

        Object insert = null;
        int sz = mdl.getSize();     // Post-removal size

        // Choose which object should be re-inserted.
        if(index != -1) {
            // index != -1
            // mdl.size == 0 || mdl.size != 0
            // elems strings || elems not strings
            // sel String    || sel Bean          || sel null
            // 2 * 2 * 3 = 12 scenarios

            if(remove instanceof String && similarInputReplaces) {
                insert = sel;    // Allows modifying of existing, similar string
            } else {
                insert = remove;
            }
        } else {
            // index == -1
            // mdl.size == 0 || mdl.size != 0
            // elems strings || elems not strings
            // sel String    || sel Bean          || sel null
            // 2 * 2 * 3 = 12 scenarios

            if(sz == 0) {
                // index == -1
                // mdl.size == 0
                // elems strings || elems not strings
                // sel String    || sel Bean          || sel null
                // 2 * 3 = 6 scenarios

                if(sel != null) {
                    insert = sel;   // Could be string or bean (null if not editable)
                }
            } else {
                // index == -1
                // mdl.size != 0
                // elems strings || elems not strings
                // sel String    || sel Bean          || sel null
                // 2 * 3 = 6 scenarios

                boolean atLeastOneString = false;
                for(int i = 0; i < mdl.getSize(); i++) {
                    if(mdl.getElementAt(i) instanceof String) {
                        atLeastOneString = true;
                        break;
                    }
                }
                if(atLeastOneString) {
                    insert = sel;
                }
            }
        }

        // Replace new value, if found at top of the model
        // and select that new value.
        if(insert != null) {
            mdl.insertElementAt((T) insert, 0);
            setSelectedItem(insert);
        }

        return insert;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setEditor(ComboBoxEditor editor) {
        ComboBoxEditor prev = getEditor();
        if(prev != null) {
            prev.removeActionListener(actionListener);
        }
        super.setEditor(editor);
        if(editor != null && pushHistoryOnAction) {
            editor.addActionListener(actionListener);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {

        Validator validator = (txt, text) -> {
            return true;//!StringUtil.isBlank(text); //.contains("x");
        };

        RecentComboBox<String> cbo0 = (RecentComboBox<String>) Lay.cb("recent,selectall");
        cbo0.setTrivialInputDecider(o -> "".equals(o));

        RecentComboBox<String> cbo1 = (RecentComboBox<String>) Lay.cb("recent,selectall",
            new String[] {"Apple", "Orange", "Kiwi"});
        cbo1.setInputCleaner(e -> e.trim());
        cbo1.setInputEqualizer((u, e) -> u.equalsIgnoreCase(e));

        RecentComboBox<String> cbo2 = (RecentComboBox<String>) Lay.cb("recent",
            new String[] {"Jupiter", "Mars", "Earth"}
        );

        RecentComboBox<Employee> cbo3 = (RecentComboBox<Employee>) Lay.cb("recent,selectall",
            new Employee[] {
                new Employee("Joe",    12),
                new Employee("Sally",  30),
                new Employee("Marcus", 43)
            }
        );
        cbo3.setInputEqualizer((u, e) -> e.toString().toLowerCase().startsWith(u.toLowerCase()));

        RecentComboBox<Employee> cbo4 = (RecentComboBox<Employee>) Lay.cb("recent",
            new Employee[] {
                new Employee("Nightcore",  77),
                new Employee("Glitch Hop", 88),
                new Employee("Dubstep",    99)
            }
        );

        JButton btnPush0;
        JButton btnPush1;
        JButton btnPush2;
        JButton btnPush3;
        JButton btnPush4;

        EscapeFrame fra = Lay.fr("RecentComboBox");
        Lay.BLtg(fra,
            "N", Lay.FL("L",
                cbo0, btnPush0 = Lay.btn("Push"),
                cbo1, btnPush1 = Lay.btn("Push"),
                cbo2, btnPush2 = Lay.btn("Push"),
                cbo3, btnPush3 = Lay.btn("Push"),
                cbo4, btnPush4 = Lay.btn("Push")
            ),
            "size=[1000,300],center,visible,toplevel"
        );

        Lay.hn(cbo0, "editable=true,selectall,prefw=200");
        Lay.hn(cbo1, "editable=true,selectall");
        Lay.hn(cbo3, "editable=true,selectall");

//        cbo0.setEditor(new GlowingValidatingComboBoxEditor());
//        cbo3.setEditor(new GlowingValidatingComboBoxEditor());

        ActionListener pl0 = (ActionListener) e -> cbo0.pushHistory();
        ActionListener pl1 = (ActionListener) e -> cbo1.pushHistory();
        ActionListener pl2 = (ActionListener) e -> cbo2.pushHistory();
        ActionListener pl3 = (ActionListener) e -> cbo3.pushHistory();
        ActionListener pl4 = (ActionListener) e -> cbo4.pushHistory();

        cbo0.setPushHistoryOnAction(true);
        cbo1.setPushHistoryOnAction(true);
        cbo3.setPushHistoryOnAction(true);

        btnPush0.addActionListener(pl0);
        btnPush1.addActionListener(pl1);
        btnPush2.addActionListener(pl2);
        btnPush3.addActionListener(pl3);
        btnPush4.addActionListener(pl4);
    }

    private static class Employee {
        String name;int age;
        public Employee(String name, int age) {
            this.name = name;
            this.age = age;
        }
        @Override
        public String toString() {
            return name + "#" + age;
        }
    }
}
