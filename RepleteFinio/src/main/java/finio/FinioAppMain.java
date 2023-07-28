package finio;

import java.io.File;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import finio.appstate.AppConfig;
import finio.appstate.AppState;
import finio.appstate.WorldBundle;
import finio.core.NonTerminal;
import finio.core.impl.FList;
import finio.core.impl.FMap;
import finio.core.warnings.RecursionWarning;
import finio.example.Car;
import finio.plugins.FinioPluginManager;
import finio.plugins.platform.FinioPlugin;
import finio.ui.FFrame;
import finio.ui.FFrameManager;
import finio.ui.actions.FActionMapBuilder;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import gov.sandia.webcomms.http.Http;
import replete.plugins.PluginManager;
import replete.threads.SwingTimerManager;
import replete.ui.GuiUtil;
import replete.ui.fc.RFileChooser;
import replete.util.AppMain;
import replete.util.User;
import replete.xstream.XStreamWrapper;

// TODO: Investigate http://stackoverflow.com/questions/2873449/occasional-interruptedexception-when-quitting-a-swing-application
// https://api.guildwars2.com/v1/map_floor.json?continent_id=1&floor=1

// FMap code variable abbreviations:
//   O  - Any piece of information, without assigning or knowing whether it is a key or a value yet
//   M  - Map (grouping of information)
//   K  - Key (name of one piece of information)
//   V  - Value (value of one piece of information)
//   KV - A key-value pair
//   L  - List (a BMap)
//   I  - Index of an element in a list (basically a K in the BMap)
//   E  - Element of a list (basically a V in the BMap)
//   P  - Key path (location of information)
//   S  - Segment (for key paths)
//   Q  - Key specification (i.e. query)
//   Z  - number of keys in a map (i.e. integral size of map or list).
//   T  - Type of an object (represents java.lang.Class usually)
//   F  - Java Field
//   N  - Level within a hierarchy or recursion stack
//   C  - Comparator
//   m  - An object that implements Java's Map interface

public class FinioAppMain extends AppMain {

    public static final String TITLE = "Finio";
    private static File appDir = new File(User.getHome(), ".finio");
    private static File appState = new File(appDir, "state.xml");

    // TODO: Start to think about how Finio can also be a
    // developer library for data transformations, and
    // debugging.
//    public static void inspect(Object... args) {
//        configureXStream();
//        GuiUtil.enableTabsHighlighted();
//        PluginManager.initialize(new PlatformPlugin(), null, null);
//        setDefaultChooserPaths();
//        readAppState();
//        AppContext ac = createAppContext();
//        try {
//            NonTerminal W = AMap.A();
//            for(int a = 0; a < args.length; a++) {
//                Object V = args[a];
//                JavaObjectReflectionExtractor X =
//                    new JavaObjectReflectionExtractor(V);
//                W.put(a, X.extract());
//            }
//            WorldContext wc = new WorldContext(ac)
//                .setW(W)
//                .setName("Test World");
//            ac.addWorld(wc);
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        FFrame frame = createAndShowFrame(ac);
//        createUncaughtExceptionHandler(ac, frame);
//    }

    public static void main(String[] args) {
//        StringBuilder b = new StringBuilder("afsdasdfaasdf");
//        inspect(b, new Car()/*, new TrivialManagedNonTerminal(new TrivialMapManager())*/); // StackOverflowError
//        if(true) {
//            return;
//        }
        Logger.getRootLogger().setLevel(Level.OFF);
        configureXStream();
        GuiUtil.enableTabsHighlighted();
        PluginManager.initialize(FinioPlugin.class);
        FinioPluginManager.initialize();
        setDefaultChooserPaths();
        readAppState();
        final AppContext ac = createAppContext();
        attachSavedBundles(ac);

        FFrameManager.getInstance().setAppContext(ac);
        FFrameManager.getInstance().addAllFramesClosedListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {

                // Stop all registered swing timers properly (globally).
                SwingTimerManager.shutdown();

                try {
                    appDir.mkdirs();
                    for(WorldContext wc : ac.getWorlds()) {
                        if(wc.getSource() != null) {
                            AppState.getState().getWorlds().clear();
                            AppState.getState().getWorlds()
                                .add(new WorldBundle(wc.getName(), wc.getSource()));
                        }
                    }
                    AppState.getState().setRecentFiles(ac.getRecentFiles());
                    XStreamWrapper.writeToFile(AppState.getState(), appState);

                    // Don't know why, but after allowing multiple JFrames,
                    // the system won't quit after launching multiple frames.
                    // I even did a thread and ownerless window analysis.
                    // Calling exit here for assured, speedy shutdown..
                    System.exit(0);

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // Not invoking "New Window" action for quicker startup.
        FFrame Finit = FFrameManager.getInstance().create();
        Finit.setVisible(true);
    }

    private static void configureXStream() {
        XStreamWrapper.addAlias("AMap", FMap.class);
        XStreamWrapper.addAlias("AList", FList.class);
        XStreamWrapper.addAlias("Car", Car.class);
        XStreamWrapper.addAlias("RecursionWarning", RecursionWarning.class);
    }

    private static void readAppState() {
        try {
            AppState.setState((AppState) XStreamWrapper.loadTarget(appState));
            AppConfig config = AppState.getState().getConfig();
            config.initProxy();
        } catch(Exception e) {
            AppState.setState(new AppState());
            AppConfig config = AppState.getState().getConfig();
            config.setProxyHost(Http.getInstance().getProxyHost());
            config.setProxyPort(Http.getInstance().getProxyPort());
        }
    }

    private static void setDefaultChooserPaths() {

        // Some hard coded paths for ease of use right now.
        File ws = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
        RFileChooser chooser = RFileChooser.getChooser();
        chooser.addRecentLink(new File("C:\\Users\\dtrumbo\\work\\Data\\CCDE_UCNI\\DOD\\5210-41M\\UCNI 5210-41M Vol 2.PDF"));
        chooser.addRecentLink(new File(ws, "JobTaskLinker\\supplemental\\Competencies.xlsx"));
        chooser.addRecentLink(new File(ws, "Orbweaver\\resources-UCNI\\DOE\\word\\document.xml"));
        chooser.addRecentLink(new File(ws, "Orbweaver\\resources-UCNI\\DOE.docx"));
        chooser.addRecentLink(new File(ws, "Orbweaver\\resources-UCNI\\Hello.docx"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\Test.docx"));
        chooser.addRecentLink(new File(ws, "Orbweaver\\resources-UCNI\\DOE-O-473.3-PPO-snippit.json"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\test.xml"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\duke.xml"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\broken.xml"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\broken.json"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\test.csv"));
        chooser.addRecentLink(new File(ws, "RepleteFinio\\supplemental\\test.json"));
        File reso = new File(ws, "RepleteFinio\\supplemental");
        chooser.setCurrentDirectory(reso);
    }

    private static AppContext createAppContext() {
        final AppContext ac = new AppContext()
            .setRecent(AppState.getState().getRecentFiles())
            .setConfig(AppState.getState().getConfig());
        FActionMapBuilder builder = new FActionMapBuilder();
        builder.build(ac);
        return ac;
    }

    private static void attachSavedBundles(final AppContext ac) {
        List<WorldBundle> bundles = AppState.getState().getWorlds();
        if(bundles != null) {
            for(WorldBundle bundle : bundles) {
                try {
                    NonTerminal W = (NonTerminal) XStreamWrapper.loadTarget(bundle.getFile());
                    WorldContext wc = new WorldContext(ac)
                        .setW(W)
                        .setName(bundle.getLabel())
                        .setSource(bundle.getFile());
                    ac.addWorld(wc);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

//    private static FFrame createAndShowFrame(final AppContext ac) {
//        final FFrame frame = new FFrame(ac);
//
//        frame.addAttemptToCloseListener(new CommonWindowClosingListener() {
//            public void stateChanged(CommonWindowClosingEvent e) {
//                ac.notImpl("Save");
//                if(!ac.getActionMap().checkSave(
//                        "Your world is unsaved.  Do you wish to save?",
//                        "Save Before Closing?")) {
//                    e.cancelClose();
//                }
//            }
//        });

//        frame.addClosingListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                SwingTimerManager.shutdown();
//                try {
//                    appDir.mkdirs();
//                    for(WorldContext wc : ac.getWorlds()) {
//                        if(wc.getFile() != null) {
//                            AppState.getState().getWorlds().clear();
//                            AppState.getState().getWorlds()
//                                .add(new WorldBundle(wc.getName(), wc.getFile()));
//                        }
//                    }
//                    AppState.getState().setRecentFiles(ac.getRecentFiles());
//                    XStreamWrapper.writeToFile(AppState.getState(), appState);
//                } catch(Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

//        frame.setVisible(true);
//        return frame;
//    }
}
