package finio.platform.exts.views.tabs;

import java.util.List;
import java.util.UUID;

import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.plugins.extpoints.Views;
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
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import replete.event.ExtChangeNotifier;
import replete.ui.lay.Lay;
import replete.ui.tabbed.HeaderContextMenuEvent;
import replete.ui.tabbed.HeaderContextMenuListener;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.tabbed.TabAboutToCloseEvent;
import replete.ui.tabbed.TabAboutToCloseListener;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionMap;
import replete.ui.uiaction.UIActionPopupMenu;


public class TabbedViewsPanel extends ViewsPanel {


    ////////////
    // FIELDS //
    ////////////

    // Global UI
    private AppContext ac;    // Application context (config, worlds)
    private WorldContext wc;  // World context (data model, world panel) in which this views panel exists.
    private Views views;            // A "Views" panel right now knows the extension from which it came...
    private UIActionMap actionMap;

    // Local UI
    private RTabbedPane tabs;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TabbedViewsPanel(AppContext ac, final WorldContext wc, Views views) {
        this.ac = ac;
        this.wc = wc;
        this.views = views;         // Not actually used yet.

        Lay.BLtg(this,
            "C", tabs = Lay.TBL("dc")
        );

        actionMap = new TabbedViewsActionMap(ac, tabs);

        WorldPanel pnlWorld = wc.getWorldPanel();
        pnlWorld.addViewAddedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                List<ViewContainerPanel> views = wc.getWorldPanel().getViewPanels();
                ViewContainerPanel pnlViewCont = views.get(views.size() - 1);
                ViewPanel pnlView = pnlViewCont.getViewPanel();
                tabs.addTab(
                    pnlViewCont.createTabTitle(),
                    pnlViewCont.getViewPanel().getView().getIcon(),
                    pnlViewCont, null, UUID.randomUUID().toString());
            }
        });
        pnlWorld.addRenameViewListener(renameViewListener);
        pnlWorld.addSelectedViewListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = wc.getWorldPanel().getSelectedViewIndex();
                tabs.setSelectedIndex(index);
            }
        });
        pnlWorld.addCloseViewListener(new CloseViewListener() {
            public void stateChanged(CloseViewEvent e) {
                tabs.remove(e.getIndex());
            }
        });

        tabs.addChangeListener(tabChangeListener);
        tabs.addTabAboutToCloseListener(tabAboutToCloseListener);
        tabs.addHeaderContextMenuListener(headerContextMenuListener);
    }

    private ChangeListener tabChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            int index = tabs.getSelectedIndex();
            Lay.hn(TabbedViewsPanel.this, "bg=" + (index == -1 ? "238" : "100"));
            ViewContainerPanel pnlViewCont = (index == -1) ? null :
                (ViewContainerPanel) tabs.getHeaderPanelAt(index).getMetadata();
            fireViewSelectionNotifier(pnlViewCont, index);
        }
    };

    private RenameViewListener renameViewListener = new RenameViewListener() {
        public void stateChanged(RenameViewEvent e) {
            tabs.setTitleAt(e.getViewIndex(),
                e.getViewContainerPanel().createTabTitle());
        }
    };

    private HeaderContextMenuListener headerContextMenuListener = new HeaderContextMenuListener() {
        public void stateChanged(HeaderContextMenuEvent e) {
            JPopupMenu mnuPopup = new UIActionPopupMenu(actionMap);
            actionMap.cleanUp();
            mnuPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    };

    private TabAboutToCloseListener tabAboutToCloseListener = new TabAboutToCloseListener() {
        public void stateChanged(TabAboutToCloseEvent e) {
            e.cancel();
            UIAction action = ac.getActionMap().getAction("close-view");
            action.execute();
        }
    };


    //////////////
    // NOTIFIER //
    //////////////

    private ExtChangeNotifier<ViewSelectionListener> viewSelectionNotifier = new ExtChangeNotifier<>();
    @Override
    public void addViewSelectionListener(ViewSelectionListener listener) {
        viewSelectionNotifier.addListener(listener);
    }
    @Override
    public void removeViewSelectionListener(ViewSelectionListener listener) {
        viewSelectionNotifier.removeListener(listener);
    }
    private void fireViewSelectionNotifier(ViewContainerPanel pnlViewCont, int i) {
        viewSelectionNotifier.fireStateChanged(new ViewSelectionEvent(pnlViewCont, i));
    }
}
