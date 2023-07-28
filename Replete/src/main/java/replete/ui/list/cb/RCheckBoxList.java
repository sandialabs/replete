package replete.ui.list.cb;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import replete.collections.ArrayUtil;
import replete.ui.list.RList;
import replete.ui.windows.escape.EscapeFrame;



/**
 * A JList which shows a check box to the left of each
 * list item.  This check box list can contain any
 * object and use any list model, just like the normal
 * JList.  Items' checked state are queried or changed
 * based on their index in the list.  Items' checked
 * state can be changed with a double-click or by
 * pressing the space key.  Any item's check box can
 * be disabled.  Listeners can be registered on
 * check-state change just like selection change.
 * Renderer is as optimized as possible and works on
 * various L&F.  Renderer supports insets.
 *
 * @author Derek Trumbo
 */

public class RCheckBoxList<T> extends RList<T> {


    ////////////
    // FIELDS //
    ////////////

    protected ItemDetails[] items;

    protected CheckBoxCellRenderer renderer = new CheckBoxCellRenderer();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RCheckBoxList() {
        init();
    }
    public RCheckBoxList(ListModel dataModel) {
        super(dataModel);
        init();
    }
    public RCheckBoxList(T[] listData) {
        super(listData);
        init();
    }
    public RCheckBoxList(Vector<T> listData) {
        super(listData);
        init();
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    protected void init() {
        setCellRenderer(renderer);
        updateRenderer();
        addModelChangedListener();
        initCheckedFromModel();
        addCheckBoxListeners();
    }

    // Called by setListData(*) as well.
    @Override
    public void setModel(ListModel model) {
        super.setModel(model);
        addModelChangedListener();
        initCheckedFromModel();
    }

    protected void initCheckedFromModel() {
        ListModel model = getModel();

        // JList does not allow a null model, so no need to check.
        // All checked = false, enabled = true.
        items = ArrayUtil.newArray(ItemDetails.class, model.getSize());
    }


    ////////////////////////
    // INTERNAL LISTENERS //
    ////////////////////////

    protected void addModelChangedListener() {
        ListModel model = getModel();
        model.removeListDataListener(modelListener);   // Don't want duplicate listeners
        model.addListDataListener(modelListener);
    }

    protected ListDataListener modelListener = new ListDataListener() {
        public void intervalRemoved(ListDataEvent e) {
            items = ArrayUtil.removeElements(ItemDetails.class, items, e.getIndex0(), e.getIndex1());
        }
        public void intervalAdded(ListDataEvent e) {
            items = ArrayUtil.addElements(ItemDetails.class, items, e.getIndex0(), e.getIndex1());
        }
        public void contentsChanged(ListDataEvent e) {
            // No action necessary.
        }
    };

    protected void addCheckBoxListeners() {

        // Change check box value when clicked.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(!RCheckBoxList.this.isEnabled()) {
                    return;
                }
                int index = locationToIndex(e.getPoint());
                Rectangle r = getCellBounds(index, index);
                if(r != null && r.contains(e.getPoint())) {
                    if(index != -1 && items[index].enabled &&
                       (e.getPoint().x < 20 + renderer.getInsets().left || e.getClickCount() > 1)) {
                        items[index].checked = !items[index].checked;
                        repaint();
                        fireListCheckedEvent(index, items[index].checked);
                    }
                }
            }
        });

        // Allow the check boxes to be changed via the space key.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(!RCheckBoxList.this.isEnabled()) {
                    return;
                }
                if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                    int index = getSelectedIndex();
                    if(index != -1 && items[index].enabled) {
                        items[index].checked = !items[index].checked;
                        repaint();
                        fireListCheckedEvent(index, items[index].checked);
                    }
                }
            }
        });

        addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {

                // getForeground and getBackground could theoretically
                // return null if the list has not yet been added to
                // any container and isForegroundSet or isBackgroundSet
                // is false (which supposedly could vary from UI to UI),
                // which indicates that the component should pull those
                // colors from its parent.  So to cover all our bases
                // we should update the renderer each time our parent
                // changes.
                if(evt.getPropertyName().equals("ancestor")) {
                    updateRenderer();
                }
            }
        });
    }


    //////////////////////
    // PROPERTY CHANGES //
    //////////////////////

    // Property changes are used as a small optimization so that
    // these values are not needed to be called each time a
    // cell is rendered.

    @Override
    public void setSelectionForeground(Color bg) {
        super.setSelectionForeground(bg);
        updateRenderer();
    }
    @Override
    public void setSelectionBackground(Color bg) {
        super.setSelectionBackground(bg);
        updateRenderer();
    }

    @Override
    public void setForeground(Color bg) {
        super.setForeground(bg);
        updateRenderer();
    }
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        updateRenderer();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        updateRenderer();
    }
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        updateRenderer();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateRenderer();
    }

    protected void updateRenderer() {
        if(renderer != null) {
            renderer.updateRendererUI(this);
        }
    }


    ////////////////////////
    // EXTERNAL LISTENERS //
    ////////////////////////

    public ListCheckedListener[] getListCheckedListeners() {
        return listeners.toArray(new ListCheckedListener[0]);
    }
    protected List<ListCheckedListener> listeners = new ArrayList<ListCheckedListener>();
    public void addListCheckedListener(ListCheckedListener listener) {
        listeners.add(listener);
    }
    public void removeListCheckedListener(ListCheckedListener listener) {
        listeners.remove(listener);
    }
    protected void fireListCheckedEvent(int index, boolean checked) {
        ListCheckedEvent e = new ListCheckedEvent(this, index, checked);
        for(ListCheckedListener listener : listeners) {
            listener.valueChanged(e);
        }
    }


    /////////////////////
    // GET/SET METHODS //
    /////////////////////

    // These methods are analogous to the "selection" methods.

    public int getCheckedIndex() {
        for(int c = 0; c < items.length; c++) {
            if(items[c].checked) {
                return c;
            }
        }
        return -1;
    }
    public int[] getCheckedIndices() {
        int totalChecked = getCheckedCount();
        int[] checkedIdx = new int[totalChecked];
        if(totalChecked != 0) {
            int i = 0;
            for(int c = 0; c < items.length; c++) {
                if(items[c].checked) {
                    checkedIdx[i++] = c;
                }
            }
        }
        return checkedIdx;
    }
    public Object getCheckedValue() {
        for(int c = 0; c < items.length; c++) {
            if(items[c].checked) {
                return getModel().getElementAt(c);
            }
        }
        return null;
    }
    public Object[] getCheckedValues() {
        int totalChecked = getCheckedCount();
        Object[] checkedObj = new Object[totalChecked];
        if(totalChecked != 0) {
            int i = 0;
            for(int c = 0; c < items.length; c++) {
                if(items[c].checked) {
                    checkedObj[i++] = getModel().getElementAt(c);
                }
            }
        }
        return checkedObj;
    }
    public List<T> getCheckedValuesList() {         // Analogous to Java 7's getSelectedValuesList
        List<T> checkedItems = new ArrayList<>();
        for(Object o : getCheckedValues()) {
            checkedItems.add((T) o);
        }
        return checkedItems;
    }
    public int getCheckedCount() {
        int totalChecked = 0;
        for(int c = 0; c < items.length; c++) {
            if(items[c].checked) {
                totalChecked++;
            }
        }
        return totalChecked;
    }
    public int getMinCheckedIndex() {
        return getCheckedIndex();
    }
    public int getMaxCheckedIndex() {
        for(int c = items.length - 1; c >= 0; c--) {
            if(items[c].checked) {
                return c;
            }
        }
        return -1;
    }
    public boolean isCheckedIndex(int idx) {
        if(idx < 0 || idx >= items.length) {
            throw new IllegalArgumentException("checked index invalid: " + idx);
        }
        return items[idx].checked;
    }
    public boolean isCheckedEmpty() {
        return (getCheckedIndex() == -1);
    }

    public void clearChecked() {
        for(int c = 0; c < items.length; c++) {
            items[c].checked = false;
        }
        repaint();
    }

    public void checkAll() {
        for(int c = 0; c < items.length; c++) {
            items[c].checked = true;
        }
        repaint();
    }


    // Clears the checked state for all indices except
    // the index or indices specified, which are left
    // checked.
    public void setCheckedIndex(int idx) {
        setCheckedIndices(new int[] {idx});
    }
    public void setCheckedIndices(int[] indices) {
        for(int idx : indices) {
            if(idx < 0 || idx >= items.length) {
                throw new IllegalArgumentException("checked index invalid: " + idx);
            }
        }
        for(int c = 0; c < items.length; c++) {
            items[c].checked = false;
        }
        for(int idx : indices) {
            items[idx].checked = true;
        }
        repaint();
    }

    // Sets the checked state for the specified index or
    // indices to the specified value.  Does not modify
    // any other indices.
    public void setCheckedIndex(int idx, boolean checked) {
        setCheckedIndices(new int[] {idx}, checked);
    }
    public void setCheckedIndices(int[] indices, boolean checked) {
        for(int idx : indices) {
            if(idx < 0 || idx >= items.length) {
                throw new IllegalArgumentException("checked index invalid: " + idx);
            }
        }
        for(int idx : indices) {
            items[idx].checked = checked;
        }
        repaint();
    }

    // Clears the checked state for all list elements
    // except those that equal the specified object,
    // which are left checked.
    public void setCheckedValue(Object anObject) {
        ListModel model = getModel();
        for(int c = 0; c < model.getSize(); c++) {
            if(model.getElementAt(c).equals(anObject)) {
                items[c].checked = true;
            } else {
                items[c].checked = false;
            }
        }
        repaint();
    }

    // Sets the checked state for all list elements
    // that equal the specified object to the specified
    // value.  Does not modify any other elements.
    public void setCheckedValue(Object anObject, boolean checked) {
        ListModel model = getModel();
        for(int c = 0; c < model.getSize(); c++) {
            if(model.getElementAt(c).equals(anObject)) {
                items[c].checked = checked;
            }
        }
        repaint();
    }

    public void setCheckCellInsets(Insets insets) {
        renderer.setInsets(insets);
        updateUI();     // Must be called instead of repaint.
    }
    public Insets getCheckCellInsets() {
        return renderer.getInsets();
    }

    // Enabled state, foreground color, background color, and font.
    public boolean isItemEnabled(int index) {
        return items[index].enabled;
    }
    public void setItemEnabled(int index, boolean enabled) {
        items[index].enabled = enabled;
        repaint();
    }
    public void setItemsEnabled(boolean enabled) {
        for(int c = 0; c < items.length; c++) {
            items[c].enabled = enabled;
        }
        repaint();
    }
    public Font getItemFont(int index) {
        return items[index].font;
    }
    public void setItemFont(int index, Font font) {
        items[index].font = font;
        repaint();
    }
    public Color getItemForeground(int index) {
        return items[index].fg;
    }
    public void setItemForeground(int index, Color color) {
        items[index].fg = color;
        repaint();
    }
    public Color getItemBackground(int index) {
        return items[index].bg;
    }
    public void setItemBackground(int index, Color color) {
        items[index].bg = color;
        repaint();
    }
    public void clearItemForegrounds() {
        for(int c = 0; c < items.length; c++) {
            items[c].fg = null;
        }
        repaint();
    }
    public void clearItemBackgrounds() {
        for(int c = 0; c < items.length; c++) {
            items[c].bg = null;
        }
        repaint();
    }

    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void swap(int i0, int i1) {
        super.swap(i0, i1);
        boolean checked0 = isCheckedIndex(i0);
        boolean checked1 = isCheckedIndex(i1);
        setCheckedIndex(i0, checked1);
        setCheckedIndex(i1, checked0);
    }


    //////////////
    // RENDERER //
    //////////////

    protected class CheckBoxCellRenderer implements ListCellRenderer {
        protected Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        protected Border defaultFocusBorder = UIManager.getBorder("List.focusCellHighlightBorder");

        protected JCheckBox chk;
        protected JPanel pnl;

        protected Insets insets;
        public Insets getInsets() {
            return insets;
        }
        public void setInsets(Insets newInsets) {
            insets = newInsets;
            chk.setBorder(BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right));
        }

        // Keeping these things locally and updating them
        // only when the list properties change is a small
        // optimization so that they don't need to be called
        // every time a cell needs to be repainted.
        protected Color listSelFg;
        protected Color listSelBg;
        protected Color listFg;
        protected Color listBg;
        protected boolean listEnabled;
        protected Font listFont;

        public CheckBoxCellRenderer() {

            chk = new JCheckBox();
            chk.setOpaque(false);              // Need to see through checkbox.
            chk.setBorderPainted(false);       // Panel will handle border.

            // Panel gets border layout so the check box expands to fill.
            pnl = new JPanel(new BorderLayout());
            pnl.add(chk, BorderLayout.CENTER);

            // Place the checkbox into the panel with the given insets.
            setInsets(new Insets(0, 0, 0, 0));
        }

        public void updateRendererUI(JList list) {
            listSelFg   = list.getSelectionForeground();
            listSelBg   = list.getSelectionBackground();
            listFg      = list.getForeground();
            listBg      = list.getBackground();
            listEnabled = list.isEnabled();
            listFont    = list.getFont();

            // getForeground and getBackground could theoretically
            // return null if the list has not yet been added to
            // any container and isForegroundSet or isBackgroundSet
            // is false (which supposedly could vary from UI to UI),
            // which indicates that the component should pull those
            // colors from its parent.
            if(listFg == null) {
                listFg = UIManager.getColor("List.foreground");
            }
            if(listBg == null) {
                listBg = UIManager.getColor("List.background");
            }

            chk.setFont(listFont);
        }

        public Component getListCellRendererComponent(JList lst, Object value, int index,
            boolean isSelected, boolean cellHasFocus)
        {
            items = ArrayUtil.ensureSize(ItemDetails.class, items, index + 1);
            ItemDetails item = items[index];

            chk.setText(value.toString());
            chk.setSelected(item.checked);
            chk.setEnabled(item.enabled && listEnabled);

            chk.setFont(item.font == null ? listFont : item.font);
            chk.setForeground(item.fg == null ? null : item.fg);
            chk.setOpaque(item.bg != null);
            chk.setBackground(item.bg == null ? null : item.bg);

            pnl.setForeground(isSelected ? listSelFg : listFg);
            pnl.setBackground(isSelected ? listSelBg : listBg);
            pnl.setBorder(cellHasFocus ? defaultFocusBorder : noFocusBorder);

            return pnl;
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final DefaultListModel mdl = new DefaultListModel();
        Object[] objects = new Object[]{"apples", "oranges", "tomatoes", "bananas", "cucumbers", "grapes"};
        for(Object o : objects) {
            mdl.addElement(o);
        }
        final RCheckBoxList lstChecks = new RCheckBoxList(mdl);
        JList lstNormal = new JList(new Object[]{"apples", "oranges", "tomatoes", "bananas", "cucumbers", "grapes"});
        lstChecks.setItemEnabled(0, false);
        lstChecks.setItemEnabled(1, false);
        lstChecks.setCheckedIndex(1, true);
        lstChecks.setItemForeground(2, Color.red);
        lstChecks.setItemFont(2, new Font("Courier New", Font.BOLD, 16));
        lstChecks.setItemBackground(3, Color.green);

        JButton btnDo = new JButton("Do Something");
        btnDo.setMnemonic('D');
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lstChecks.setEnabled(!lstChecks.isEnabled());
                lstChecks.setFont(new Font("Courier New", 0, 16));
                lstChecks.setBackground(new Color(70, 220, 255));
                for(Object o : lstChecks.getCheckedValues()) {
                    System.out.println("checked=" + o);
                }
                Insets i = lstChecks.getCheckCellInsets();
                lstChecks.setCheckCellInsets(new Insets(5 + i.top, 5 + i.left, 5 + i.bottom, 5 + i.right));
            }
        });
        JButton btnAdd = new JButton("Add Element");
        btnAdd.setMnemonic('A');
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                mdl.insertElementAt("Dolly", 3);
//                mdl.addElement("New Element");
                lstChecks.clearItemBackgrounds();
                lstChecks.clearItemForegrounds();
            }
        });
        JButton btnRemove = new JButton("Remove Elements");
        btnRemove.setMnemonic('R');
        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                mdl.removeRange(2, 4);
                mdl.removeAllElements();
            }
        });

        JPanel pnlLists = new JPanel(new GridLayout(1, 2));
        pnlLists.add(new JScrollPane(lstChecks));
        pnlLists.add(new JScrollPane(lstNormal));

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnDo);
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnRemove);

        lstChecks.addListCheckedListener(new ListCheckedListener() {
            public void valueChanged(ListCheckedEvent e) {
                System.out.print("changed(" + e.getIndex() + "): ");
                for(Object o : lstChecks.getCheckedValues()) {
                    System.out.print(o + ",");
                }
                System.out.println();
            }
        });

        JFrame frmDemo = new EscapeFrame();
        frmDemo.setTitle("CheckBoxList Demo");
        frmDemo.setLayout(new BorderLayout());
        frmDemo.add(pnlLists, BorderLayout.CENTER);
        frmDemo.add(pnlButtons, BorderLayout.SOUTH);
        frmDemo.setSize(400, 400);
        frmDemo.setLocationRelativeTo(null);
        frmDemo.setVisible(true);
    }
}
