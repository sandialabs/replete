package replete.ui.help.test;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;

import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.plugins.test.DiagnosticsPlugin;
import replete.ui.GuiUtil;
import replete.ui.help.HelpFrame;
import replete.ui.help.HelpProvider;
import replete.ui.help.model.HelpAlbum;
import replete.ui.help.model.HelpDataModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionMap;
import replete.ui.uiaction.UIActionPopupMenu;
import replete.ui.windows.Dialogs;
import replete.ui.windows.notifications.NotificationFrame;
import replete.ui.windows.notifications.NotificationWindow;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorker;

public class TestHelp {
    public static void main(String[] args) {
        HelpDataModel dataModel = createHelpDataModel();
        // Bookmarks?

//        .addBookmark(new Bookmark(pageDogsTraining, "good page!"))
//        .addBookmark(new Bookmark(pageCats, "cats are cool too..."))
//        .addBookmark(new Bookmark(pageAnimals, null))

//        HelpFrame frame = new HelpFrame(null, dataModel);
//        frame.setVisible(true);
//        frame.addClosingListener(e -> Platform.exit());

        NotificationFrame fra = new NotificationFrame();
        fra.setShowStatusBar(true);
        Lay.BLtg(fra,
            "N", Lay.FL("L",
                Lay.lb("Here is some feature. Access & Change help here => "),
                Lay.btn(CommonConcepts.HELP, "icon", (ActionListener) e -> openHelp(fra, "FEATURE1")),
                Lay.lb("DEV:"),
                createHelpLabel("aaa-aaa", true, true, "DEV-EXISTS"),
                createHelpLabel("aaa-aaa", true, false, "DEV-NOEXIST"),
                Lay.lb("PROD:"),
                createHelpLabel("aaa-aaa", false, true, "PROD-EXISTS"),
                createHelpLabel("aaa-aaa", false, false, "PROD-NOEXIST")
            ),
            "size=600,center,visible"
        );
    }

    private static JLabel createHelpLabel(String id, boolean testDev, boolean testExists, String testTtt) {
        JLabel lbl;
        boolean dev = testDev; //isDevelopment();
        boolean exists = testExists;  //library.exists(id)
        MouseListener listener;
        if(dev) {
            lbl = Lay.lb(exists ? CommonConcepts.ACCEPT : CommonConcepts.DELETE, "cursor=hand,ttt=" + testTtt);
            listener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    Component btn = (Component) e.getSource();
                    UIActionMap actions = new HomeViewsLoadActionMap();

                    JPopupMenu mnuPopup = new UIActionPopupMenu(actions);
//                    actions.setState("default");        // Causes enabled/disabled states of popup menu items to be manifested

                    int x = 0;                          // Rare case where e.getX() & e.getY() are not used since we
                    int y = btn.getHeight();            // want the popup menu to appear in same, fixed spot regardless
                    mnuPopup.show(btn, x, y);           // of where mouse is on the icon.
                }
            };
        } else {
            lbl = Lay.lb(exists ? CommonConcepts.INFO : CommonConcepts.ERROR, "cursor=hand,ttt=" + testTtt);
            listener = new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    NotificationWindow win = (NotificationWindow) GuiUtil.win(lbl);
                    if(exists) {
                        openHelp(win, "FEATURE1");
                    } else {
                        Dialogs.showWarning((Component) win, "ERROR!");
                        // dialog showing that no help has yet been associated with this ID
                    }
                }
            };
        }
        lbl.addMouseListener(listener);
        return lbl;
    }

    private static class HomeViewsLoadActionMap extends UIActionMap {
            public HomeViewsLoadActionMap() {
                createAction("a")
                    .addDescriptor(
                        new PopupMenuActionDescriptor()
                            .setText("aaaa")
                    );
            }
    }

    private static boolean isDevelopment() {
        return true;
    }

    private static void openHelp(NotificationWindow fra, String feature) {
        RWorker<Void, HelpDataModel> worker = new RWorker<Void, HelpDataModel>() {
            @Override
            protected HelpDataModel background(Void gathered) throws Exception {
                return createHelpDataModel();
            }

            @Override
            protected void complete() {
                try {
                    HelpDataModel dataModel = getResult();
                    HelpFrame.getInstanceAndShow((Component) fra, dataModel);
                } catch(Exception e) {
                    Dialogs.showDetails(
                        (Component) fra, "An error occurred attempting to show the help.",
                        "Show Help Failure", e
                    );
                }
            }
        };

        NotificationTask task = new NotificationTask()
            .setAction(worker)
            .setTitle("Opening Help")
            .setUseWaitCursor(true)
            .setAutoRemove(true)
        ;
        fra.getNotificationModel().getTasks().add(task);
        worker.execute();
    }

    private static HelpDataModel createHelpDataModel() {
        DiagnosticsPlugin plugin = new DiagnosticsPlugin(
            HelpProvider.class,
            new ExampleHelpProvider(),
            new ExampleHelpProvider2()
        );
        PluginManager.initialize(plugin);
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(HelpProvider.class);
        HelpDataModel dataModel = new HelpDataModel();
        for(ExtensionPoint ext : exts) {
            HelpProvider provider = (HelpProvider) ext;
            provider.loadAlbum();
            HelpAlbum album = provider.getAlbum();
            album.setProvider(provider);      // Every album is told its context manager upon being loaded for convenient back-tracking
            dataModel.addAlbum(album);
        }
        return dataModel;
    }
}
