package finio.ui.worlds;

import java.io.File;

import javax.swing.event.ChangeListener;

import finio.core.NonTerminal;
import finio.platform.exts.views.tabs.TabbedViews;
import finio.platform.exts.views.tabs.TabbedViewsPanel;
import finio.ui.app.AppContext;
import finio.ui.views.ViewsPanel;
import finio.ui.world.WorldPanel;
import replete.event.ChangeNotifier;
import replete.text.StringUtil;

public class WorldContext {           // Used to be "WorldUiController"


    ////////////
    // FIELDS //
    ////////////

    // -- IN-RAM INFO REPRESENTATION --
    private NonTerminal W;          // "THE" data model for this world.  A "world" IS a top-level information tree.
    private String name;            // A user-assigned label for the data model (allows for 1 more level above W).

    // -- SOURCE DETAILS --
    private File source;            // The source where the data model was read in from.  Right now,
                                    // this is just a file but could technically be any source.
                                    // For example:
                                    //     https://www.server.com/resource, ftp://www.server.com/resource,
                                    //     finio://182.34.54.176:11/resource/282731, C:\Users\dtrumbo\work\dir\file.txt
                                    //     [Bytes Located @ Local Mongo, Port 243, Table T, Row R, Col C]
    private boolean dirty = true;   // Whether or not the world data model ("W") has been modified in memory
                                    // since it was originally taken from the source.  Right now, this will
                                    // always be true

    // -- UI COMPONENTS --
    private AppContext ac;          // Application context (global config, etc.).
    //private WorldConfig config;   // Some day
    private WorldPanel pnlWorld;    // The panel that is visualizing this world. This could be a
                                    // list some day, for all the panels that are open to visualize
                                    // a given world's data model.


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldContext(AppContext ac) {
        this.ac = ac;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public NonTerminal getW() {
        return W;
    }
    public String getName() {
        return name;
    }
    public File getSource() {        // Could technically be read from any number of types of sources.
        return source;
    }
    public boolean isDirty() {
        return dirty;
    }
    public AppContext getAppContext() {
        return ac;
    }
    public WorldPanel getWorldPanel() {
        return pnlWorld;
    }

    // Accessors (Computed)

    public String getTitle() {
        return name == null ? (source == null ? "World" : source.getName()) : name;
    }
    public String createTabTitle() {         // Duplicate
        return
            "<html><i>World:</i>" +
            (name != null ? " " + StringUtil.cleanXmlCdata(name): "") + "</html>";
    }

    // Mutators

    // setW is always called right at construction but due to a
    // weird pseudo-builder pattern being used right now it's not
    // passed into the constructor...?  Could change this.
    public WorldContext setW(NonTerminal W) {
        this.W = W;

        // Construct an initial world panel.  This might be skipped
        // in future if we have a list of world panels.  But this is
        // the initial one.
        pnlWorld = new WorldPanel(ac, this);

        // Each world panel should know how to arrange its views,
        // much like a Java Swing layout manager applied to a panel.
        // Choose a default one right now for the initial world panel.
        // In the future, this code probably won't hard-code a specific
        // "Views" panel but rather dynamically load the one currently
        // designated as the default.  This code could also happen
        // in the WorldPanel constructor.
        ViewsPanel pnlViews = new TabbedViewsPanel(ac, this, new TabbedViews());
//        pnlViews = new SplitViewsPanel(ac, this, new SplitViews());
        pnlWorld.setViewsPanel(pnlViews);

//        FTreePanel pnlTree = new FTreePanel(ac, this, null, W, new TreeView());
//        pnlWorld.addViewPanel(pnlTree);

        return this;
    }
    public WorldContext setName(String name) {
        this.name = name;
        return this;
    }
    public WorldContext setWorldPanel(WorldPanel pnlWorld) {
        this.pnlWorld = pnlWorld;
        return this;
    }
    public WorldContext setSource(File file) {
        source = file;
        return this;
    }
    public WorldContext setDirty(boolean dirty) {
        if(!dirty) {
            return this;                 // Enable this when we can make the world dirty
        }
        this.dirty = dirty;
        fireDirtyChangedNotifier();
        return this;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    private ChangeNotifier dirtyChangedNotifier = new ChangeNotifier(this);
    public void addDirtyChangedListener(ChangeListener listener) {
        dirtyChangedNotifier.addListener(listener);
    }
    private void fireDirtyChangedNotifier() {
        dirtyChangedNotifier.fireStateChanged();
    }


    //////////
    // MISC //
    //////////

    public void refresh() {
        pnlWorld.refresh();
    }
}
