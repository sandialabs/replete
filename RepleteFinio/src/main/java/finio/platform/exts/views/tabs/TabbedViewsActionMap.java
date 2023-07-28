package finio.platform.exts.views.tabs;

import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;

import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class TabbedViewsActionMap extends UIActionMap {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private JTabbedPane tabs;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TabbedViewsActionMap(AppContext ac, JTabbedPane tabs) {
        this.ac = ac;
        this.tabs = tabs;

        init();
    }

    private void init() {

        UIActionListener listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getActionMap().getAction("rename-view").execute();
            }
        };
        createAction("rename-view", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Rename...")
                    .setIcon(CommonConcepts.RENAME));

        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getActionMap().getAction("rename-view").execute();
            }
        };
        createAction("close-view", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("&Close")
                    .setIcon(CommonConcepts.CLOSE));

        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                ac.getActionMap().getAction("close-all-views").execute();
            }
        };
        createAction("close-all-views", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setText("Close &All")
                    .setIcon(CommonConcepts.CLOSE_ALL));

        createAction()
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setSeparator(true));

        // View Tabs Top
        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                tabs.setTabPlacement(JTabbedPane.TOP);
            }
        };
        createAction("views-tabs-top", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setOptMenu(true, "views-tabs")
                    .setText("View Tabs Top")
                    .setIcon(FinioImageModel.TABS_UP));

        // View Tabs Left
        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                tabs.setTabPlacement(JTabbedPane.LEFT);
            }
        };
        createAction("views-tabs-left", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setOptMenu(true, "views-tabs")
                    .setText("View Tabs Left")
                    .setIcon(FinioImageModel.TABS_LEFT));

        // View Tabs Right
        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                tabs.setTabPlacement(JTabbedPane.RIGHT);
            }
        };
        createAction("views-tabs-right", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setOptMenu(true, "views-tabs")
                    .setText("View Tabs Right")
                    .setIcon(FinioImageModel.TABS_RIGHT));

        // View Tabs Bottom
        listener = new UIActionListener() {
            public void actionPerformed(ActionEvent e, UIAction action) {
                tabs.setTabPlacement(JTabbedPane.BOTTOM);
            }
        };
        createAction("views-tabs-bottom", listener)
            .addDescriptor(
                new PopupMenuActionDescriptor()
                    .setOptMenu(true, "views-tabs")
                    .setText("View Tabs Bottom")
                    .setIcon(FinioImageModel.TABS_DOWN));
    }

    @Override
    public void cleanUp() {
        int placement = tabs.getTabPlacement();
        getPopupMenuComponent("views-tabs-top").setSelected(placement == JTabbedPane.TOP);
        getPopupMenuComponent("views-tabs-left").setSelected(placement == JTabbedPane.LEFT);
        getPopupMenuComponent("views-tabs-right").setSelected(placement == JTabbedPane.RIGHT);
        getPopupMenuComponent("views-tabs-bottom").setSelected(placement == JTabbedPane.BOTTOM);
    }
}
