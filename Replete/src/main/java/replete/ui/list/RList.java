package replete.ui.list;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.BiFunction;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;

import replete.collections.ArrayUtil;
import replete.errors.UnexpectedEnumValueUnicornException;
import replete.event.ChangeNotifier;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.nofire.NoFireList;
import replete.ui.panels.SelectionState;

public class RList<T> extends NoFireList<T> implements SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum ListSelectionStateIdentityMethod implements SelectionStateCreationMethod {
        INDEX,                 // (Default) Just record the selected index
        OBJECT_REFERENCE,      // Record selected item's reference
        OBJECT_HASH_CODE,      // Record selected item's hash code
        OBJECT_CLASS_NAME,     // Record selected item's class name
        OBJECT_TO_STRING,      // Record selected item's toString value
        CUSTOM                 // BiFunction<Integer, Object, Object> as 2nd argument = customIdentityFunction
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RList() {
        init();
    }
    public RList(ListModel<T> dataModel) {
        super(dataModel);
        init();
    }
    public RList(T[] listData) {
        super(listData);
        init();
    }
    public RList(Iterable<T> listData) {
        super();
        setListData(listData);
        init();
    }
    public RList(Vector<T> listData) {
        super(listData);
        init();
    }

    private void init() {
        addDeleteKeyListener();
    }


    //////////////
    // MUTATORS //
    //////////////

    public void selectFirst() {
        if(getModel().getSize() != 0) {
            setSelectedIndex(0);
        }
    }

    public void removeSelected() {
        removeSelected(this);
    }

    public void setUseStandardDeleteBehavior(boolean use) {
        removeKeyListener(stdDelKey);
        if(use) {
            addKeyListener(stdDelKey);
        }
    }

    private KeyListener stdDelKey = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                JList lst = (JList) e.getSource();
                removeSelected(lst);
            }
        }
    };

    private void removeSelected(JList lst) {
        DefaultListModel mdl = (DefaultListModel) lst.getModel();
        int size = mdl.size();
        if(size != 0 && lst.getSelectedIndex() != -1) {
            int[] indices = lst.getSelectedIndices();
            int low = indices[0];
            for(int i = indices.length - 1; i >= 0; i--) {
                int rmIdx = indices[i];
                mdl.remove(rmIdx);
            }
            if(low >= mdl.size()) {
                low = mdl.size() - 1;
            }
            lst.setSelectedIndex(low);
        }
    }

    public void moveSelectedUp() {
        int[] indices = getSelectedIndices();
        List<Integer> indexList = ArrayUtil.asList(indices);
        for(int i = 0; i < indexList.size(); i++) {
            Integer curIndex = indexList.get(i);
            if(curIndex == 0) {
                continue;
            }
            Integer prevIndex = curIndex - 1;
            if(indexList.contains(prevIndex)) {
                continue;
            }
            swap(curIndex, prevIndex);
            indexList.set(i, prevIndex);
        }
        setSelectedIndices(indexList);
    }

    public void moveSelectedDown() {
        DefaultListModel mdl = (DefaultListModel) getModel();
        int[] indices = getSelectedIndices();
        List<Integer> indexList = ArrayUtil.asList(indices);
        for(int i = indexList.size() - 1; i >= 0; i--) {
            Integer curIndex = indexList.get(i);
            if(curIndex == mdl.getSize() - 1) {
                continue;
            }
            Integer nextIndex = curIndex + 1;
            if(indexList.contains(nextIndex)) {
                continue;
            }
            swap(curIndex, nextIndex);
            indexList.set(i, nextIndex);
        }
        setSelectedIndices(indexList);
    }

    protected void swap(int i0, int i1) {
        DefaultListModel mdl = (DefaultListModel) getModel();
        Object curElem = mdl.getElementAt(i0);
        Object prevElem = mdl.getElementAt(i1);
        mdl.setElementAt(prevElem, i0);
        mdl.setElementAt(curElem, i1);
    }

    public void setSelectedIndices(List<Integer> indices) {
        int[] newIndices = new int[indices.size()];
        for(int i = 0; i < indices.size(); i++) {
            Integer curIndex = indices.get(i);
            newIndices[i] = curIndex;
        }
        setSelectedIndices(newIndices);
    }

    public void setListData(Iterable<T> listData) {
        DefaultListModel<T> model = new DefaultListModel<>();
        for(T data : listData) {
            model.addElement(data);
        }
        setModel(model);
    }

    public void addSelectionListener(ListSelectionListener listener) {
        addListSelectionListener(listener);
    }


    //////////
    // MISC //
    //////////

    private void addDeleteKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DELETE) {
                    fireDeleteNotifier();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() > 1) {
                    int index = locationToIndex(e.getPoint());
                    if(index == -1 || !getCellBounds(index, index).contains(e.getPoint())) {
                        fireEmptyDoubleClickNotifier();
                    } else {
                        // sel could still be -1 i think
                        fireDoubleClickNotifier();
                    }
                }
            }
        });
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public SelectionState getSelectionState(Object... args) {
        ListSelectionStateIdentityMethod method =
            getDefaultArg(args, ListSelectionStateIdentityMethod.INDEX);

        // Method-specific arguments
        BiFunction<Integer, Object, Object> customIdentityFunction =
            getDefaultArg(args, BiFunction.class, 1);

        Set<Object> selKeys = new LinkedHashSet<>();

        for(int selIndex : getSelectedIndices()) {
            Object selItem = getModel().getElementAt(selIndex);
            Object selKey = makeKey(selIndex, selItem, method, customIdentityFunction);
            if(selKey != null) {
                selKeys.add(selKey);
            }
        }

        return new SelectionState(
            "selected",  selKeys,
            "method",    method,
            "ifunction", customIdentityFunction
        );
    }

    @Override
    public void setSelectionState(SelectionState state) {
        Set<Object> selKeys = state.getGx("selected");
        ListSelectionStateIdentityMethod method = state.getGx("method");
        BiFunction<Integer, Object, Object> customIdentityFunction = state.getGx("ifunction");

        clearSelection();
        ListModel mdl = getModel();
        for(int index = 0; index < mdl.getSize(); index++) {
            Object item = mdl.getElementAt(index);
            Object key = makeKey(index, item, method, customIdentityFunction);
            if(selKeys.contains(key)) {
                getSelectionModel().addSelectionInterval(index, index);
            }
        }
    }

    private Object makeKey(int selIndex, Object selItem,
                           ListSelectionStateIdentityMethod method,
                           // These are the method-specific params -->
                           BiFunction<Integer, Object, Object> customIdentityFunction) {

        if(method == ListSelectionStateIdentityMethod.INDEX) {
            return selIndex;

        } else if(method == ListSelectionStateIdentityMethod.OBJECT_REFERENCE) {
            return selItem;

        } else if(method == ListSelectionStateIdentityMethod.OBJECT_HASH_CODE) {
            if(selItem != null) {
                return selItem.hashCode();
            }
            return null;

        } else if(method == ListSelectionStateIdentityMethod.OBJECT_CLASS_NAME) {
            if(selItem != null) {
                return selItem.getClass().getName();
            }
            return null;

        } else if(method == ListSelectionStateIdentityMethod.OBJECT_TO_STRING) {
            if(selItem != null) {
                return selItem.toString();
            }
            return null;

        } else if(method == ListSelectionStateIdentityMethod.CUSTOM) {
            return customIdentityFunction.apply(selIndex, selItem);

        } else {
            throw new UnexpectedEnumValueUnicornException(method);
        }
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ChangeNotifier deleteKeyNotifier = new ChangeNotifier(this);
    public void addDeleteListener(ChangeListener listener) {
        deleteKeyNotifier.addListener(listener);
    }
    protected void fireDeleteNotifier() {
        deleteKeyNotifier.fireStateChanged();
    }

    protected ChangeNotifier emptyDoubleClickNotifier = new ChangeNotifier(this);
    public void addEmptyDoubleClickListener(ChangeListener listener) {
        emptyDoubleClickNotifier.addListener(listener);
    }
    protected void fireEmptyDoubleClickNotifier() {
        emptyDoubleClickNotifier.fireStateChanged();
    }

    // TODO: might not be working yet
    protected ChangeNotifier doubleClickNotifier = new ChangeNotifier(this);
    public void addDoubleClickListener(ChangeListener listener) {
        doubleClickNotifier.addListener(listener);
    }
    protected void fireDoubleClickNotifier() {
        doubleClickNotifier.fireStateChanged();
    }


    //////////
    // TEST //
    //////////

    private static SelectionState ss;
    public static void main(String[] args) {
        Object[] i = new Object[] {
            "aaa", "bbb", "ccc", "ccc", "ccc",
            "ddd", "ddd", "ddd", "eee", "eee", "fff"
        };
        DefaultListModel mdl = new DefaultListModel<>();
        for(Object j : i) {
            mdl.addElement(j);
        }
        RList lst;
        Lay.BLtg(Lay.fr(),
            "C", Lay.sp(lst = Lay.lst(mdl)),
            "S", Lay.WL(
                Lay.btn("GE&T SS",    (ActionListener) e -> ss = lst.getSelectionState()),
                Lay.btn("GE&T SS HC", (ActionListener) e -> ss = lst.getSelectionState(ListSelectionStateIdentityMethod.OBJECT_HASH_CODE)),
                Lay.btn("GE&T SS CN", (ActionListener) e -> ss = lst.getSelectionState(ListSelectionStateIdentityMethod.OBJECT_CLASS_NAME)),
                Lay.btn("&SET SS",    (ActionListener) e -> lst.setSelectionState(ss)),
                Lay.btn("CHG SEL MD", (ActionListener) e -> lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)),
                Lay.btn("CLEAR",      (ActionListener) e -> lst.setModel(new DefaultListModel())),
                Lay.btn("ADD",        (ActionListener) e -> {
                    ((DefaultListModel) lst.getModel()).addElement("qqq");
                    ((DefaultListModel) lst.getModel()).addElement("qqq");
                    ((DefaultListModel) lst.getModel()).addElement("qqq");
                    ((DefaultListModel) lst.getModel()).addElement("rrr");
                    ((DefaultListModel) lst.getModel()).addElement("rrr");
                    ((DefaultListModel) lst.getModel()).addElement("sss");
                    ((DefaultListModel) lst.getModel()).addElement("ttt");
                }),
                Lay.btn("REMOVE",     (ActionListener) e -> lst.removeSelected()),
                Lay.btn("RESET",      (ActionListener) e -> {
                    DefaultListModel mdl2 = new DefaultListModel();
                    for(Object j : i) {
                        mdl2.addElement(j);
                    }
                    lst.setModel(mdl2);
                })
            ),
            "size=600,center,visible"
        );
    }
}
