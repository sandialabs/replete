package replete.ui.tabbed;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;

import replete.event.ExtChangeNotifier;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenuItem;
import replete.ui.panels.SelectionState;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;


public class RTabbedPane extends JTabbedPane implements Iterable<Component>, SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum TabbedPaneSelectionStateCreationMethod implements SelectionStateCreationMethod {
        RECORD_INNER,       // (Default) Record tabbed pane's selected tab and contained component
        RECORD_SELF_ONLY    // Only record tabbed pane's selected tab
    }



    ////////////
    // FIELDS //
    ////////////

    // Maintaining this state allows us to assign a unique key
    // to a tab instead of relying on tab title uniqueness.
    private Map<Object, RTabHeaderPanel> keyToHeader = new HashMap<>();
    private Map<RTabHeaderPanel, Object> headerToKey = new HashMap<>();

    private Object prevActiveTabKey = null;
    private int prevTabIndex = -1;
    private Map<Integer, Component> focusedComponentsOnTabs = new HashMap<>();
    protected boolean focusPreviousComponents = true;
    private boolean useBorders = true;
    private int headerHeight = -1;
    private RTabHeaderPanel currentlyInsertingTabHeaderPanel;
    private TabCreationDescriptor defaultTabCreationDescriptor = new TabCreationDescriptor()
        .setCloseToolTip("Close Tab")
    ;

    public TabCreationDescriptor getDefaultTabCreationDescriptor() {
        return defaultTabCreationDescriptor;
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Could have constructors / mutators to change the default icons.
    // (dirty, close, close-hover, close-hover-pressed).

    public RTabbedPane() {
        this(false);
    }
    public RTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        this(tabPlacement, tabLayoutPolicy, false);
    }
    public RTabbedPane(int tabPlacement) {
        this(tabPlacement, false);
    }
    public RTabbedPane(boolean dc) {
        super();
        init(dc);
    }
    public RTabbedPane(int tabPlacement, int tabLayoutPolicy, boolean dc) {
        super(tabPlacement, tabLayoutPolicy);
        init(dc);
    }
    public RTabbedPane(int tabPlacement, boolean dc) {
        super(tabPlacement);
        init(dc);
    }

    private Component getFocusedComponent(Component targetCmp) {
        if(targetCmp.hasFocus()) {
            return targetCmp;
        }
        if(targetCmp instanceof Container) {
            Container cnt = (Container) targetCmp;
            for(int c = 0; c < cnt.getComponentCount(); c++) {
                Component childCmp = cnt.getComponent(c);
                Component fCmp = getFocusedComponent(childCmp);
                if(fCmp != null) {
                    return fCmp;
                }
            }
        }
        return null;
    }

    private void init(boolean dc) {
        defaultTabCreationDescriptor.setCloseable(dc);
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if(focusPreviousComponents) {
                    // TODO: This really should be a map of String -> Component, not Integer -> Component.
                    // However, if the code after this can't be used because of internal state bugs,
                    // then this code would probably suffer from the same thing if switched over.
                    // Therefore, this feature is "implemented" -- but with the caveat that it will not
                    // work on tabbed panes that has tabs that are being added and removed after creation.
                    if(prevTabIndex != -1 && prevTabIndex < getTabCount()) {
                        Component tabCmp = getComponentAt(prevTabIndex);
                        if(tabCmp != null) {
                            Component cmp = getFocusedComponent(tabCmp);  // Could be null.
                            focusedComponentsOnTabs.put(prevTabIndex, cmp);
                        }
                    }
                    int nextTabIndex = getSelectedIndex();
                    if(nextTabIndex != -1) {
                        Component cmp = focusedComponentsOnTabs.get(nextTabIndex);
                        if(cmp != null) {
                            cmp.requestFocusInWindow();
                        }
                    }
                    prevTabIndex = nextTabIndex;
                }

                if(true) {
                    return; // TODO: Not done yet... internal state is inconsistent on insert/remove.
                }

                // Send deactivated to previous active tab.
                if(prevActiveTabKey != null) {
                    int index = indexOfTabByKey(prevActiveTabKey);
                    Component c = getComponentAt(index);
                    if(c instanceof RTabPanel) {
                        ((RTabPanel) c).tabDeactivated();
                    }
                }

                // Send activated to new active tab.
                int index = getSelectedIndex();
                if(index != -1) {
                    Component c = getComponentAt(index);
                    if(c instanceof RTabPanel) {
                        ((RTabPanel) c).tabActivated();
                    }
                    prevActiveTabKey = getKeyAt(index);
                } else {
                    prevActiveTabKey = null;
                }
            }
        });
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    protected ExtChangeNotifier<TabCloseListener> tabCloseNotifier =
        new ExtChangeNotifier<>();
    public void addTabCloseListener(TabCloseListener listener) {
        tabCloseNotifier.addListener(listener);
    }
    protected void fireTabCloseNotifier(int index, Object key, Component cmp) {
        TabCloseEvent e = new TabCloseEvent(index, key, cmp);
        tabCloseNotifier.fireStateChanged(e);
    }

    protected ExtChangeNotifier<TabAboutToCloseListener> tabAboutToCloseNotifier =
        new ExtChangeNotifier<>();
    public void addTabAboutToCloseListener(TabAboutToCloseListener listener) {
        tabAboutToCloseNotifier.addListener(listener);
    }
    public void removeTabAboutToCloseListener(TabAboutToCloseListener listener) {
        tabAboutToCloseNotifier.removeListener(listener);
    }
    protected boolean fireTabAboutToCloseNotifier(int index, Object key) {
        TabAboutToCloseEvent e = new TabAboutToCloseEvent(index, key);
        tabAboutToCloseNotifier.fireStateChanged(e);
        return !e.isCanceled();
    }

    protected ExtChangeNotifier<HeaderContextMenuListener> headerContextMenuNotifier =
        new ExtChangeNotifier<>();
    public void addHeaderContextMenuListener(HeaderContextMenuListener listener) {
        headerContextMenuNotifier.addListener(listener);
    }
    public void removeHeaderContextMenuListener(HeaderContextMenuListener listener) {
        headerContextMenuNotifier.removeListener(listener);
    }
    protected void fireHeaderContextMenuNotifier(Object key, Component component, int x, int y) {
        HeaderContextMenuEvent e = new HeaderContextMenuEvent(key, component, x, y);
        headerContextMenuNotifier.fireStateChanged(e);
    }


    ////////////
    // INSERT //
    ////////////

    public Object insertTab(String title, Icon icon, Component component, int index) {
        return insertTab(title, icon, component, null, index, title);  // TODO: Fix title being key (needs to be null)
    }
    @Override
    public void insertTab(String title, Icon icon, Component component, String tip, int index) {
        insertTab(title, icon, component, tip, index, title);  // TODO: Fix title being key (needs to be null)
    }
    public Object insertTab(String title, Icon icon, Component component, String tip, int index, Object key) {
        return insertTab(title, icon, component, tip, index, key, false);
    }
    public Object insertTab(String title, Icon icon, Component component, String tip, int index, Object key, boolean select) {
        return insertTab(
            new TabCreationDescriptor()
                .setIndex(index)
                .setKey(key)
                .setTitle(title)
                .setComponent(component)
                .setIcon(icon)
                .setTip(tip)
                .setSelect(select)
                .setCloseable(null)       // No preference
                .setDirty(null)           // No preference
                //.addExtraComponent()
        );
    }
    public Object insertTab(TabCreationDescriptor desc) {
        Object key    = desc.getKey();
        String title  = desc.getTitle();
        Component cmp = desc.getComponent();

        applyDefaults(desc);

        if(key == null) {
            key = UUID.randomUUID();
        }
        if(title == null) {
            throw new RuntimeException("Cannot have null title.");
        }

        int removeIndex = indexOfComponent(cmp);     // Code from super.insertTab
        if(cmp != null && removeIndex != -1) {      // Code from super.insertTab
            // Going to be removed to be placed at different index -
            // OK if same key is used.
            if(keyToHeader.containsKey(key)) {             // Clean up.
                headerToKey.remove(keyToHeader.get(key));
            }
        } else {
            // The component is new or empty super.insertTab will
            // not call removeTabAt, we can't have a duplicate key.
            if(keyToHeader.containsKey(key)) {
                throw new RuntimeException("A tab using this key already exists.");
            }
        }

        // Construct header panel given current info and update
        // internal data structures.
        RTabHeaderPanel pnlHeader = createCloseableTabHeader(key, desc);
        keyToHeader.put(key, pnlHeader);
        headerToKey.put(pnlHeader, key);

        // Save a special reference to the current header panel
        // in case the insert fires any events that then ask
        // for the new header panel.
        currentlyInsertingTabHeaderPanel = pnlHeader;

        // Ask JTabbedPane to insert the new tab, which will fire
        // change events.
        Integer index = desc.getIndex();
        if(index == null) {
            index = getTabCount();
        }
        super.insertTab(title, desc.getIcon(), cmp, desc.getTip(), index);
        if(desc.isSelect()) {
            setSelectedIndex(index);
        }

        // Unset special panel variable.
        currentlyInsertingTabHeaderPanel = null;

        // Now that you know the tab exists, officially set the
        // header panel for this index.
        super.setTabComponentAt(index, pnlHeader);

        updateTitleWidths();

        return key;
    }

    // Lazy way to have a defaults pattern.  Better way is 1) a generic map
    // that holds the properties combined with an overlay operation or 2)
    // to create a "copy" method within the TCD class.
    private void applyDefaults(TabCreationDescriptor desc) {
        if(desc.getTitle() == null && defaultTabCreationDescriptor.getTitle() != null) {
            desc.setTitle(defaultTabCreationDescriptor.getTitle());
        }
        if(desc.getIcon() == null && defaultTabCreationDescriptor.getIcon() != null) {
            desc.setIcon(defaultTabCreationDescriptor.getIcon());
        }
        if(desc.getTip() == null && defaultTabCreationDescriptor.getTip() != null) {
            desc.setTip(defaultTabCreationDescriptor.getTip());
        }
        if(desc.isCloseable() == null && defaultTabCreationDescriptor.isCloseable() != null) {
            desc.setCloseable(defaultTabCreationDescriptor.isCloseable());
        }
        if(desc.getCloseToolTip() == null && defaultTabCreationDescriptor.getCloseToolTip() != null) {
            desc.setCloseToolTip(defaultTabCreationDescriptor.getCloseToolTip());
        }
        if(desc.isDirty() == null && defaultTabCreationDescriptor.isDirty() != null) {
            desc.setDirty(defaultTabCreationDescriptor.isDirty());
        }
    }

    @Override
    public Component getTabComponentAt(int index) {
        Component pnlHeader = super.getTabComponentAt(index);
        if(pnlHeader == null && currentlyInsertingTabHeaderPanel != null) {
            return currentlyInsertingTabHeaderPanel;
        }
        return pnlHeader;
    }

    private void updateTitleWidths() {
        if(getTabPlacement() == JTabbedPane.LEFT ||
                getTabPlacement() == JTabbedPane.RIGHT) {
            int maxWidth = -1;
            for(int i = 0; i < getTabCount(); i++) {
                Component c = getTabComponentAt(i);
                if(c.getPreferredSize().width > maxWidth) {
                    maxWidth = c.getPreferredSize().width;
                }
            }
            for(int i = 0; i < getTabCount(); i++) {
                Component c = getTabComponentAt(i);
                int height = c.getPreferredSize().height;
                c.setPreferredSize(new Dimension(maxWidth, height));
            }
        }
    }

    protected RTabHeaderPanel createCloseableTabHeader(final Object key, TabCreationDescriptor desc) {
        RTabHeaderPanel pnlHeader = new RTabHeaderPanel(desc);
        pnlHeader.addHeaderContextMenuListener(e -> {
            switchToTab(key);
            fireHeaderContextMenuNotifier(key, e.getComponent(), e.getX(), e.getY());
        });

        pnlHeader.setUseBorder(useBorders);
        if(headerHeight != -1) {
            pnlHeader.setHeight(headerHeight);
        }

        pnlHeader.addSwitchTabListener(e -> switchToTab(key));
        pnlHeader.addCloseListener(e -> {
            int index = indexOfTabByKey(key);
            if(fireTabAboutToCloseNotifier(index, key)) {
                Component cmp = getComponentAt(index);
                removeTabAt(index);
                fireTabCloseNotifier(index, key, cmp);
            }
        });

        return pnlHeader;
    }

    protected void switchToTab(Object key) {
        int index = indexOfTabByKey(key);
        setSelectedIndex(index);
    }


    //////////
    // KEYS //
    //////////

    // Like indexOfTab(Icon), indexOfTab(String).
    public int indexOfTabByKey(Object key) {
        internalKeyCheck();
        for(int i = 0; i < getTabCount(); i++) {
            RTabHeaderPanel pnlHeader = (RTabHeaderPanel) getTabComponentAt(i);
            if(headerToKey.get(pnlHeader).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    // Like getTitleAt(), getIconAt().
    public Object getKeyAt(int index) {
        internalKeyCheck();
        RTabHeaderPanel pnlHeader = (RTabHeaderPanel) getTabComponentAt(index);
        return headerToKey.get(pnlHeader);
    }

    // More of a debugging thing to make sure that the code is working correctly.
    private Object[] internalKeyCheck() {
        Set<Object> keys1 = keyToHeader.keySet();
        Set<Object> keys2 = new HashSet<Object>(headerToKey.values());
        Set<Object> keys3 = new HashSet<Object>();
        for(int i = 0; i < getTabCount(); i++) {
            RTabHeaderPanel pnlHeader = (RTabHeaderPanel) getTabComponentAt(i);
            // Just means that currently the tab is being inserted, but
            // setTabComponent has not been called yet.
            if(pnlHeader == null) {
                continue;
            }
            keys3.add(headerToKey.get(pnlHeader));
        }
        if(keys1.equals(keys2) && keys1.equals(keys3) && keys2.equals(keys3)) {
            return keys1.toArray(new Object[0]);
        }
        String msg = "KEYS1=" + keys1 + "\nKEYS2=" + keys2 + "\nKEYS3=" + keys3;
        new RuntimeException("Internal key check failed.\n" + msg).printStackTrace();
        return keys1.toArray(new Object[0]);
    }

    public Object[] getKeys() {
        return internalKeyCheck();
    }

    public void changeKey(Object from, Object to) {
        internalKeyCheck();
        if(from == null || to == null) {
            throw new RuntimeException("Neither from- nor to-key can be null for key change.");
        }
        if(!keyToHeader.containsKey(from)) {
            throw new RuntimeException("Key '" + from + "' does not exist.");
        }
        RTabHeaderPanel pnl = keyToHeader.get(from);
        if(pnl == null || !headerToKey.get(pnl).equals(from)) {
            throw new RuntimeException("Invalid state for key '" + from + "'.");
        }
        if(keyToHeader.containsKey(to)) {
            throw new RuntimeException("A tab using key '" + to + "' already exists.");
        }
        keyToHeader.remove(from);
        keyToHeader.put(to, pnl);
        headerToKey.put(pnl, to);
    }

    public String getTitleByKey(Object key) {
        if(keyToHeader.containsKey(key)) {
            return keyToHeader.get(key).getTitle();
        }
        return null;
    }
    public boolean hasKey(Object key) {
        return keyToHeader.containsKey(key);
    }


    ////////////
    // REMOVE //
    ////////////

    @Override
    public void removeTabAt(int index) {
        // TODO: For consistency with other controls, bring these
        // events back when there exists a NoFireCloseableTabbedPane
        // which does not fire these events.  For now removing a
        // tab programmatically will not result in the closing events
        // to fire.
//        if(fireTabAboutToCloseNotifier(index)) {
//            fireTabCloseNotifier(index);
            Object key = getKeyAt(index);
            headerToKey.remove(keyToHeader.get(key));
            keyToHeader.remove(key);
            super.removeTabAt(index);
//        }
    }

    public void remove(String title) {
        int index = indexOfTab(title);
        removeTabAt(index);
    }
    public void removeTabsWithCompType(Class<?> clazz) {
        for(int i = getTabCount() - 1; i >= 0; i--) {
            Component comp = getComponentAt(i);
            if(clazz.isAssignableFrom(comp.getClass())) {
                removeTabAt(i);
            }
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isDirtyAt(int index) {
        return getHeaderPanelAt(index).isDirty();
    }
    public boolean isCloseableAt(int index) {
        return getHeaderPanelAt(index).isCloseable();
    }
    public boolean isFocusPreviousComponents() {
        return focusPreviousComponents;
    }
    public List<Component> getAllTabComponents() {
        List<Component> cmps = new ArrayList<>();
        for(int i = 0; i < getTabCount(); i++) {
            cmps.add(getComponentAt(i));
        }
        return cmps;
    }
    public boolean isUseBorders() {
        return useBorders;
    }

    public <T extends Component> T getCompAt(int index) {
        return (T) super.getComponentAt(index);
    }
    public <T extends Component> T getSelectedCompInScrollPane() {
        int index = getSelectedIndex();
        if (index == -1) {
            return null;
        }
        Component cmp = getComponentAt(index);
        if(cmp instanceof JScrollPane) {
            cmp = ((JScrollPane) cmp).getViewport().getView();
        }
        return (T) cmp;
    }
    public <T extends Component> T getSelectedComp() {
        int index = getSelectedIndex();
        if (index == -1) {
            return null;
        }
        return (T) getComponentAt(index);
    }
    public <T extends Component> T getFirstCompOfType(Class<?> clazz) {
        for(int i = 0; i < getTabCount(); i++) {
            Component comp = getComponentAt(i);
            if(clazz.isAssignableFrom(comp.getClass())) {
                return (T) comp;
            }
        }
        return null;
    }
    public int getFirstIndexOfCompType(Class<?> clazz) {
        for(int i = 0; i < getTabCount(); i++) {
            Component comp = getComponentAt(i);
            if(clazz.isAssignableFrom(comp.getClass())) {
                return i;
            }
        }
        return -1;
    }
    public <T extends Component> T getComponentAt(String title) {
        int index = indexOfTab(title);
        return (T) super.getComponentAt(index);
    }

    // Mutators

    public void setDirtyAt(int index, boolean dirty) {
        getHeaderPanelAt(index).setDirty(dirty);
    }
    public void setCloseableAt(int index, boolean cl) {
        getHeaderPanelAt(index).setCloseable(cl);
    }
    public void setCloseable(boolean cl) {
        for(RTabHeaderPanel pnl : headerToKey.keySet()) {
            pnl.setCloseable(cl);
        }
    }
    // No accessor for this one (lazy).
    public void setUseBorderAt(int index, boolean use) {
        getHeaderPanelAt(index).setUseBorder(use);
    }
    public void setUseBorders(boolean use) {
        for(RTabHeaderPanel pnl : headerToKey.keySet()) {
            pnl.setUseBorder(use);
        }
        useBorders = use;
    }
    public void setHeaderHeight(int height) {
        for(RTabHeaderPanel pnl : headerToKey.keySet()) {
            pnl.setHeight(height);
        }
        headerHeight = height;
    }
    public void setIconAt(int index, ImageModelConcept concept) {
        setIconAt(index, ImageLib.get(concept));
    }
    @Override
    public void setIconAt(int index, Icon icon) {
        super.setIconAt(index, icon);
        RTabHeaderPanel pnl = getHeaderPanelAt(index);
        pnl.setIcon(icon);
        pnl.getLabel().setBorder(icon == null ? Lay.eb() : Lay.eb("4l"));
    }
    @Override
    public void setTitleAt(int index, String title) {
        if(title == null) {
            throw new RuntimeException("Cannot have null title.");
        }
        super.setTitleAt(index, title);
        getHeaderPanelAt(index).setTitle(title);
    }
    public void setTitleAt(Object key, String title) {
        setTitleAt(indexOfTabByKey(key), title);
    }
    public void setFocusPreviousComponents(boolean focus) {
        focusPreviousComponents = focus;
    }
    public void setSelectedTab(String title) {
        int index = indexOfTab(title);
        if(index != -1) {
            setSelectedIndex(index);
        }
    }
    public void setSelectedTab(int index) {
        setSelectedIndex(index);
    }


    ///////////////////
    // ADD OVERLOADS //
    ///////////////////

    // Add methods overloaded to provide key support.

    @Override
    public void addTab(String title, Icon icon, Component component) {
        insertTab(title, icon, component, null, getTabCount(), title, false);
    }
    public void addTab(String title, Icon icon, Component component, boolean select) {
        insertTab(title, icon, component, null, getTabCount(), title, select);
    }
    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        insertTab(title, icon, component, tip, getTabCount(), title, false);
    }
    public void addTab(String title, Component component, Object key) {
        insertTab(title, null, component, null, getTabCount(), key, false);
    }
    public void addTab(String title, Icon icon, Component component, String tip, Object key) {
        insertTab(title, icon, component, tip, getTabCount(), key, false);
    }
    public void addTab(String title, Component component, String tip, Object key, boolean select) {
        insertTab(title, null, component, tip, getTabCount(), key, select);
    }
    public void addTab(String title, Icon icon, Component component, String tip, Object key, boolean select) {
        insertTab(title, icon, component, tip, getTabCount(), key, select);
    }

    public void addTab(String title, ImageModelConcept concept, Component component) {
        ImageIcon icon = ImageLib.get(concept);
        insertTab(title, icon, component, null, getTabCount(), title, false);
    }
    public void addTab(String title, ImageModelConcept concept, Component component, boolean select) {
        ImageIcon icon = ImageLib.get(concept);
        insertTab(title, icon, component, null, getTabCount(), title, select);
    }
    public void addTab(String title, ImageModelConcept concept, Component component, String tip) {
        ImageIcon icon = ImageLib.get(concept);
        insertTab(title, icon, component, tip, getTabCount(), title, false);
    }
    public void addTab(String title, ImageModelConcept concept, Component component, String tip, Object key) {
        ImageIcon icon = ImageLib.get(concept);
        insertTab(title, icon, component, tip, getTabCount(), key, false);
    }
    public void addTab(String title, ImageModelConcept concept, Component component, String tip, Object key, boolean select) {
        ImageIcon icon = ImageLib.get(concept);
        insertTab(title, icon, component, tip, getTabCount(), key, select);
    }


    public void addTab(TabCreationDescriptor desc) {
        insertTab(desc.setIndex(getTabCount()));         // needs to be sink for others eventually...
    }

    public Component add(Component component, String keyStr) {
        if (!(component instanceof UIResource)) {
            addTab(component.getName(), component, keyStr);
        } else {
            super.add(component);
        }
        return component;
    }
    public Component add(String title, Component component, Object key) {
        if (!(component instanceof UIResource)) {
            addTab(title, component, key);
        } else {
            super.add(title, component);
        }
        return component;
    }
    public Component add(Component component, int index, Object key) {
        if (!(component instanceof UIResource)) {
            // Container.add() interprets -1 as "append", so convert
            // the index appropriately to be handled by the vector
            insertTab(component.getName(), null, component, null,
                      index == -1? getTabCount() : index, key);
        } else {
            super.add(component, index);
        }
        return component;
    }
    public void add(Component component, Object constraints, Object key) {
        if (!(component instanceof UIResource)) {
            if (constraints instanceof String) {
                addTab((String)constraints, component, key);
            } else if (constraints instanceof Icon) {
                addTab(null, (Icon)constraints, component, null, key);
            } else {
                add(component);
            }
        } else {
            super.add(component, constraints);
        }
    }
    public void add(Component component, Object constraints, int index, Object key) {
        if (!(component instanceof UIResource)) {

            Icon icon = constraints instanceof Icon? (Icon)constraints : null;
            String title = constraints instanceof String? (String)constraints : null;
            // Container.add() interprets -1 as "append", so convert
            // the index appropriately to be handled by the vector
            insertTab(title, icon, component, null, index == -1? getTabCount() : index, key);
        } else {
            super.add(component, constraints, index);
        }
    }

    public RTabHeaderPanel getHeaderPanelAt(int index) {
        Object key = getKeyAt(index);
        return keyToHeader.get(key);
    }
    public RTabHeaderPanel getHeaderPanelAt(String title) {
        int index = indexOfTab(title);
        if(index != -1) {
            return getHeaderPanelAt(index);
        }
        return null;
    }
    public Object getMetadata(int index) {
        return getHeaderPanelAt(index).getMetadata();
    }
    public void setMetadata(int index, Object ai) {
        getHeaderPanelAt(index).setMetadata(ai);
    }

    @Override
    public void setToolTipTextAt(int index, String toolTipText) {
        super.setToolTipTextAt(index, toolTipText);      // A few pixels in the tab header show this
        RTabHeaderPanel pnlHeader = getHeaderPanelAt(index);
        pnlHeader.setToolTipText(toolTipText);
    }

    // Although -1 is technically a "valid" value for JTabbedPane - actually
    // manifesting that value when there are tabs present should never result
    // in an "empty" tab selection.
    @Override
    public void setSelectedIndex(int index) {
        if(index == -1 && getTabCount() != 0) {
            index = 0;
        }
        super.setSelectedIndex(index);
    }


    //////////
    // MISC //
    //////////

    public void focus() {
        requestFocusInWindow();
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        TestFrame frame = new TestFrame();
        frame.setVisible(true);
    }

    public static class TestFrame extends EscapeFrame {
        public TestFrame() {
            super("Title Here");
            setLayout(new BorderLayout());
            final RTabbedPane ttp = new RTabbedPane(/*SwingConstants.LEFT*/);
            for(int x = 0; x < 4; x++) {
                final int y = x;
                RTabPanel atp1 = new RTabPanel() {
                    @Override
                    public void tabDeactivated() {
                        System.out.println(y + " Deactivated");
                    }
                    @Override
                    public void tabActivated() {
                        System.out.println(y + " Activated");
                    }
                };
                Lay.FLtg(atp1, Lay.lb("TestBody" + (x + 1)), new JButton("TestButton" + (x + 1)), new JTextField("TestText" + (x + 1)), new JComboBox());
                ttp.addTab("TestTitle" + (x + 1), ImageLib.get(CommonConcepts.FILE), atp1, "TOOL TIPIP");
            }

            ttp.addHeaderContextMenuListener(new HeaderContextMenuListener() {
                public void stateChanged(HeaderContextMenuEvent e) {
                    System.out.println(e.getKey() + " // [" + e.getX() + "," + e.getY() + "]" + e.getComponent());
                    JPopupMenu mnuPopup = new JPopupMenu();
                    JMenuItem mnuMessage = new RMenuItem("Message", ImageLib.get(CommonConcepts.FILE));
                    mnuMessage.addActionListener(ev -> {
                        Dialogs.showMessage(TestFrame.this, "Some message...", "Title");
                    });
                    mnuPopup.add(mnuMessage);
                    mnuPopup.show(e.getComponent(), e.getX(), e.getY());
                }
            });

            ttp.addTabAboutToCloseListener(new TabAboutToCloseListener() {
                public void stateChanged(TabAboutToCloseEvent e) {
                    System.out.println("About To Close: " + e.getIndex());
                    if(!Dialogs.showConfirm(TestFrame.this, "close?")) {
                        e.cancel();
                    }
                }
            });
            ttp.addTabCloseListener(new TabCloseListener() {
                public void stateChanged(TabCloseEvent e) {
                    System.out.println("Closed: " + e.getIndex() + " Key=" + e.getKey());
                    System.out.println("Component: " + e.getComponent());
                }
            });

            add(ttp, BorderLayout.CENTER);

            JButton btnTogDirty = Lay.btn("Toggle Dirty", CommonConcepts.DIRTY, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                ttp.setDirtyAt(index, !ttp.isDirtyAt(index));
            });

            JButton btnTogCloseable = Lay.btn("Toggle Closeable", CommonConcepts.CLOSE, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                ttp.setCloseableAt(index, !ttp.isCloseableAt(index));
            });

            JButton btnTogIcon = Lay.btn("Toggle Icon", CommonConcepts.FILE, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                if(ttp.getIconAt(index) == null) {
                    ttp.setIconAt(index, CommonConcepts.FILE);
                } else {
                    ttp.setIconAt(index, (Icon) null);
                }
            });

            JButton btnClose = Lay.btn("Close Tab", CommonConcepts.REMOVE, (ActionListener) e -> {
                ttp.removeTabAt(ttp.getSelectedIndex());
            });

            JButton btnNewTitle = Lay.btn("New Title", CommonConcepts.RENAME, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                String title = Dialogs.showInput(this, "New Tab Title?");
                if(title != null) {
                    ttp.setTitleAt(index, title);
                }
            });

            JButton btnChangeKey = Lay.btn("Change Key", RepleteImageModel.KEY, (ActionListener) e -> {
                String chg = Dialogs.showInput(this, "Enter from,to:");
                if(chg != null) {
                    String[] parts = chg.trim().split("\\s*,\\s*");
                    if(parts.length == 2) {
                        ttp.changeKey(parts[0], parts[1]);
                    }
                }
            });

            JButton btnAddExtraBtn = Lay.btn("Add Extra Button", RepleteImageModel.KEY, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                JButton btn = Lay.btn(CommonConcepts._PLACEHOLDER,
                    (ActionListener) e2 -> Dialogs.notImpl(), "icon");
                ttp.getHeaderPanelAt(index)
                    .addExtraComponent(btn)
                ;
            });

            JButton btnAddExtraLbl = Lay.btn("Add Extra Label", RepleteImageModel.KEY, (ActionListener) e -> {
                int index = ttp.getSelectedIndex();
                JLabel lbl = Lay.lb(CommonConcepts.BINARY);
                ttp.getHeaderPanelAt(index)
                    .addExtraComponent(lbl)
                ;
            });

//            JTabbedPane jtp = new JTabbedPane(/*SwingConstants.LEFT*/);
//            jtp.addTab("TestTitle0", ImageUtil.getImage("file_obj.gif"), Lay.lb("test"), "x");
//            jtp.addTab("TestTitle1", ImageUtil.getImage("file_obj.gif"), Lay.lb("test"), "x");

            Lay.BLtg(this,
                "C", ttp,
                "S", Lay.GL(3, 3,
                    btnTogIcon, btnTogCloseable, btnTogDirty,
                    btnNewTitle, btnClose, btnChangeKey,
                    btnAddExtraBtn, btnAddExtraLbl, Lay.p()
                ),
                "size=600,center"
            );
        }
    }

    @Override
    public Iterator iterator() {
        return new AdvancedTabbedPaneIterator();
    }

    private class AdvancedTabbedPaneIterator implements Iterator<Component> {
        int i = 0;

        @Override
        public boolean hasNext() {
            return i < getTabCount();
        }

        @Override
        public Component next() {
            return getComponentAt(i++);
        }

        @Override
        public void remove() {
        }
    }

    @Override
    public SelectionState getSelectionState(Object... args) {
        TabbedPaneSelectionStateCreationMethod method =
            getDefaultArg(args, TabbedPaneSelectionStateCreationMethod.RECORD_INNER);

        SelectionState state = new SelectionState(
            "tabIndex", getSelectedIndex()
        );

        if(method == TabbedPaneSelectionStateCreationMethod.RECORD_INNER) {
            int t = 0;
            for(Component pnl : getAllTabComponents()) {
                state.putSsIf("tabComp/" + t + "/" + pnl.getClass().getName(), pnl);
                t++;
            }
        }

        return state;
    }

    @Override
    public void setSelectionState(SelectionState state) {
        setSelectedIndex(state.getInt("tabIndex", getTabCount() - 1));

        int t = 0;
        for(Component pnl : getAllTabComponents()) {
            state.setSsIf(pnl, "tabComp/" + t + "/" + pnl.getClass().getName());
            t++;
        }
    }
}
