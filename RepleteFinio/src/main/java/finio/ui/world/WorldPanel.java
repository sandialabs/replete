package finio.ui.world;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.core.KeyPath;
import finio.platform.exts.view.consoleview.ui.ConsolePanel;
import finio.platform.exts.view.treeview.ui.FTreePanel;
import finio.ui.app.AppContext;
import finio.ui.view.RenameViewEvent;
import finio.ui.view.RenameViewListener;
import finio.ui.view.ViewContainerPanel;
import finio.ui.view.ViewPanel;
import finio.ui.views.CloseViewEvent;
import finio.ui.views.CloseViewListener;
import finio.ui.views.ViewSelectionEvent;
import finio.ui.views.ViewSelectionListener;
import finio.ui.views.ViewsPanel;
import finio.ui.worlds.WorldContext;
import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.ui.lay.Lay;

public class WorldPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;           // Application context (global config, etc.)
    private WorldContext wc;         // World context (data model, world panel)
    private List<ViewContainerPanel> viewPanels = new ArrayList<>();
        // List of view panels, contained within this world panel, each with a data
        // model rooted somewhere within the world data model tree.  Each are actually
        // added to the views panel.  However, the views panel could be switched with
        // a new one at any time using the "ViewContainerPanel" class, though this
        // should probably just become a JPanel of its own.
    private int selectedViewIndex = -1;
    private ViewsPanel pnlViews;            // Contains the view panels.  Can be changed.
    private NoViewsViewPanel pnlNoViews;    // Tells user to create a view.
    private boolean expandSingleView;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldPanel(AppContext ac, WorldContext wc) {
        this.ac = ac;
        this.wc = wc;

        Lay.BLtg(this,
            "C", pnlNoViews = new NoViewsViewPanel(ac, wc)
        );

        // Can one day choose a default view (like Tree) given a default preference.
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<ViewContainerPanel> getViewPanels() {
        return viewPanels;
    }
    public int getSelectedViewIndex() {
        return selectedViewIndex;
    }
    public ViewContainerPanel getSelectedViewContainerPanel() {
        if(selectedViewIndex == -1 || selectedViewIndex >= viewPanels.size()) {
            return null;
        }
        return viewPanels.get(selectedViewIndex);
    }
    public ViewPanel getSelectedView() {
        if(selectedViewIndex == -1 || selectedViewIndex >= viewPanels.size()) {
            return null;
        }
        return viewPanels.get(selectedViewIndex).getViewPanel();
    }
    public ViewPanel getViewPanel(Class<? extends ViewPanel> clazz) {
        for(ViewContainerPanel pnlViewCont : viewPanels) {
            ViewPanel pnlView = pnlViewCont.getViewPanel();
            if(clazz.isAssignableFrom(pnlView.getClass())) {
                return pnlView;
            }
        }
        return null;
    }
    public ViewsPanel getViewsPanel() {
        return pnlViews;
    }
    public NoViewsViewPanel getNoViewsPanel() {
        return pnlNoViews;
    }

    // Mutators

    public void setSelectedViewIndex(int selectedViewIndex) {
        this.selectedViewIndex = selectedViewIndex;
        fireSelectedViewNotifier();
    }
    public void setViewsPanel(ViewsPanel pnlViews) {
        if(this.pnlViews != null) {
            this.pnlViews.removeViewSelectionListener(viewSelListener);
        }
        this.pnlViews = pnlViews;
        pnlViews.addViewSelectionListener(viewSelListener);
        // TODO: Add all views to the new views panel.
        // ALSO: The views panels themselves, though removed, might
        // still be listening to the WorldPanel or WorldContext,
        // potentially adding UI components to itself and thus
        // removing those UI components from the parents where
        // they should be located.  So we need a "disconnect" your
        // listeners for orphaned UI components.
    }

    public void addViewPanel(final ViewPanel pnlView) {
        viewPanels.add(new ViewContainerPanel(pnlView, null));
        updateCenterPanel();

        pnlView.addSelectedValuesListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireSelectedValuesNotifier();
            }
        });
        pnlView.addEditContextChangedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                fireEditContextChangedNotifier();
            }
        });

//        pnlView.addAnyActionListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                updateWorkingScopeErrorIcon();
//            }
//        });
        pnlView.addSelectedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int index = -1;
                for(int i = 0; i < viewPanels.size(); i++) {
                    if(viewPanels.get(i).getViewPanel() == pnlView) {
                        index = i;
                        break;
                    }
                }
                setSelectedViewIndex(index);
                ac.getActionMap().validate();
            }
        });

        pnlView.init();
        pnlView.focus();

        fireViewAddedNotifier();
        setSelectedViewIndex(viewPanels.size() - 1);
    }


    //////////
    // MISC //
    //////////

    public void refresh() {
        for(ViewContainerPanel pnlViewCont : viewPanels) {
            pnlViewCont.getViewPanel().refresh();
        }
    }

    public void focusTree() {
        getTreePanel().focus();
    }
    public void focusConsole() {
        getConsolePanel().focusInput();
    }
    public void increaseFont() {
        for(ViewContainerPanel pnlViewCont : viewPanels) {
            pnlViewCont.getViewPanel().increaseFont();
        }
    }
    public void decreaseFont() {
        for(ViewContainerPanel pnlViewCont : viewPanels) {
            pnlViewCont.getViewPanel().decreaseFont();
        }
    }

    public void closeView(int index) {
        ViewContainerPanel pnlViewCont = viewPanels.remove(index);
        updateCenterPanel();
        fireCloseViewNotifier(pnlViewCont, index);
    }

    public void setExpandSingleView(boolean expandSingleView) {
        this.expandSingleView = expandSingleView;
        updateCenterPanel();
    }

    private void updateCenterPanel() {
        removeAll();

        if(viewPanels.size() == 0) {
            add(pnlNoViews, BorderLayout.CENTER);

        } else if(viewPanels.size() == 1 && expandSingleView) {
            add(viewPanels.get(0).getViewPanel(), BorderLayout.CENTER);

        } else {
            add(pnlViews, BorderLayout.CENTER);
        }

        updateUI();
    }

    public void init() {
        pnlViews.init();
//        updateWorkingScopeErrorIcon();
    }

    public FTreePanel getTreePanel() {
        return (FTreePanel) getViewPanel(FTreePanel.class);
    }
    public ConsolePanel getConsolePanel() {
        return (ConsolePanel) getViewPanel(ConsolePanel.class);
    }
    public void transferSelectedPaths(boolean action) {
        KeyPath[] selectedPaths = getTreePanel().getSelectedKeyPaths();
        getConsolePanel().addSelectedKeyPaths(selectedPaths, action);
        if(action) {
            getConsolePanel().focusInputAndHighlightCommand();
        }
    }
    private ViewSelectionListener viewSelListener = new ViewSelectionListener() {
        public void stateChanged(ViewSelectionEvent e) {
            setSelectedViewIndex(e.getIndex());
        }
    };


    ///////////////
    // NOTIFIERS //
    ///////////////

    // this is probably bad design, and needs to change as we let various windows have various different working scopes.

    private ChangeNotifier anyActionNotifier = new ChangeNotifier(this);
    public void addAnyActionListener(ChangeListener listener) {
        anyActionNotifier.addListener(listener);
    }
    private void fireAnyActionNotifier() {
        anyActionNotifier.fireStateChanged();
    }
    public void renameSelectedView(String input) {
        ViewContainerPanel pnlViewCont = getSelectedViewContainerPanel();
        if(pnlViewCont != null) {
            String previous = pnlViewCont.getViewName();
            pnlViewCont.setViewName(input.isEmpty() ? null : input);
            fireRenameViewNotifier(pnlViewCont, selectedViewIndex, previous, pnlViewCont.getViewName());
        }
    }

    private ChangeNotifier viewAddedNotifier = new ChangeNotifier(this);
    public void addViewAddedListener(ChangeListener listener) {
        viewAddedNotifier.addListener(listener);
    }
    private void fireViewAddedNotifier() {
        viewAddedNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedViewNotifier = new ChangeNotifier(this);
    public void addSelectedViewListener(ChangeListener listener) {
        selectedViewNotifier.addListener(listener);
    }
    public void removeSelectedViewListener(ChangeListener listener) {
        selectedViewNotifier.removeListener(listener);
    }
    private void fireSelectedViewNotifier() {
        selectedViewNotifier.fireStateChanged();
    }

    private ChangeNotifier selectedValuesNotifier = new ChangeNotifier(this);
    public void addSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.addListener(listener);
    }
    public void removeSelectedValuesListener(ChangeListener listener) {
        selectedValuesNotifier.removeListener(listener);
    }
    private void fireSelectedValuesNotifier() {
        selectedValuesNotifier.fireStateChanged();
    }

    private ChangeNotifier editContextChangedNotifier = new ChangeNotifier(this);
    public void addEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.addListener(listener);
    }
    public void removeEditContextChangedListener(ChangeListener listener) {
        editContextChangedNotifier.removeListener(listener);
    }
    private void fireEditContextChangedNotifier() {
        editContextChangedNotifier.fireStateChanged();
    }

    private ExtChangeNotifier<RenameViewListener> renameViewNotifier = new ExtChangeNotifier<>();
    public void addRenameViewListener(RenameViewListener listener) {
        renameViewNotifier.addListener(listener);
    }
    public void removeRenameViewListener(RenameViewListener listener) {
        renameViewNotifier.removeListener(listener);
    }
    private void fireRenameViewNotifier(ViewContainerPanel pnlViewCont, int index, String previous, String current) {
        RenameViewEvent event = new RenameViewEvent(pnlViewCont, index, previous, current);
        renameViewNotifier.fireStateChanged(event);
    }

    private ExtChangeNotifier<CloseViewListener> closeViewNotifier = new ExtChangeNotifier<>();
    public void addCloseViewListener(CloseViewListener listener) {
        closeViewNotifier.addListener(listener);
    }
    public void removeCloseViewListener(CloseViewListener listener) {
        closeViewNotifier.removeListener(listener);
    }
    private void fireCloseViewNotifier(ViewContainerPanel pnlViewCont, int index) {
        CloseViewEvent event = new CloseViewEvent(pnlViewCont, index);
        closeViewNotifier.fireStateChanged(event);
    }
}
