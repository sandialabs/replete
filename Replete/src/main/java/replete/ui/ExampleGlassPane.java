
package replete.ui;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

//http://weblogs.java.net/blog/alexfromsun/archive/2006/09/a_wellbehaved_g.html

public class ExampleGlassPane extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private Map<String, PanelInfo> panels = new HashMap<String, PanelInfo>();
    private Rectangle lastBounds = null;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExampleGlassPane() {
        setLayout(null);
        setOpaque(false);
    }


    ///////////////
    // SHOW/HIDE //
    ///////////////

    public void hideGlassPane() {
        setVisible(false);
    }
    public void showGlassPane() {
        setVisible(true);
    }
    @Override
    public boolean isShowing() {
        return isVisible();
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Map<String, PanelInfo> getPanels() {
        return panels;
    }


    ////////////////////////
    // MOUSE EVENT FIXING //
    ////////////////////////

    // This allows both the cursors from below and the cursors
    // for the help notes panel to be shown properly.

    @Override
    public boolean contains(int x, int y) {
        if(isVisible()) {
            for(PanelInfo info : panels.values()) {
                JPanel pnl = info.panel;
                if(x >= pnl.getX() && x <= pnl.getX() + pnl.getWidth() &&
                   y >= pnl.getY() && y <= pnl.getY() + pnl.getHeight() ) {
                    return true;
                }
            }
            return false;
        }
        return super.contains(x, y);
    }

    public void updateBounds(Rectangle bounds) {
        if(bounds == null) {
            return;
        }
        for(PanelInfo info : panels.values()) {
            if(info.repos != null) {
                info.repos.reposition(info.panel, bounds);
            }
        }
        lastBounds = bounds;
    }

    public void addPanel(String key, JPanel pnl) {
        addPanel(key, pnl, null);
    }
    public void addPanel(String key, JPanel pnl, Repositioner repos) {
        PanelInfo info = new PanelInfo(key, pnl, repos);
        panels.put(key, info);
        add(pnl);
        updateBounds(lastBounds);
    }

    public void removePanel(String key) {
        remove(panels.get(key).panel);
    }
    public void removePanel(JPanel pnl) {
        remove(pnl);
    }


    // ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class PanelInfo {
        public JPanel panel;
        public Repositioner repos;
        public String key;
        public PanelInfo(String key2, JPanel pnl, Repositioner repos2) {
            key = key2;
            panel = pnl;
            repos = repos2;
        }
    }

    public interface Repositioner {
        public void reposition(JPanel panel, Rectangle bounds);
    }
}
