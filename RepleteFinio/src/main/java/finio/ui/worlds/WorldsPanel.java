package finio.ui.worlds;

import javax.swing.JPanel;

import finio.ui.app.AppContext;
import replete.event.ExtChangeNotifier;
import replete.event.rnotif.RChangeEvent;
import replete.event.rnotif.RChangeListener;



public abstract class WorldsPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    public static final String STYLE = "bg=EEF3F9,mb=[1,004391]";
    protected AppContext ac;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldsPanel(AppContext ac) {
        this.ac = ac;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract void setSelectedIndex(int i);
    public abstract void addWorld(WorldContext wc, int i);
    public abstract void removeWorld(WorldContext wc);
    public abstract void unsubscribe();


    //////////////
    // NOTIFIER //
    //////////////

    private ExtChangeNotifier<WorldSelectedListener> worldSelectedNotifier = new ExtChangeNotifier<>();
    public void addWorldSelectedListener(WorldSelectedListener listener) {
        worldSelectedNotifier.addListener(listener);
    }
    public void removeWorldSelectedListener(WorldSelectedListener listener) {
        worldSelectedNotifier.removeListener(listener);
    }
    protected void fireWorldSelectedNotifier(int i) {
        worldSelectedNotifier.fireStateChanged(new WorldSelectedEvent(-2, i));
    }


    ////////////
    // CHANGE //
    ////////////

    protected RChangeListener worldAddedListener = new RChangeListener() {
        @Override
        public void handle(RChangeEvent e) {
            int lastIndex = ac.getWorldCount() - 1;
            WorldContext w = ac.getWorld(lastIndex);
            addWorld(w, lastIndex);
        }
    };
}
