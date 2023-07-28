package finio.ui.actions.window;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import finio.ui.FFrame;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;

public class FullScreenWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FullScreenWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getActionVerb() {
        return "switching to full screen";
    }

    @Override
    protected void completeInner(Void result) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        int prefMon = 0;
        if(prefMon < ge.getScreenDevices().length) {
            GraphicsDevice gdPref = ge.getScreenDevices()[prefMon];
            // Create the first frame, set undecorated, turn on and set full screen
            FFrame win = ac.getWindow();
            win.setVisible(false);
            win.dispose();
            win.setUndecorated(true);
            win.setVisible(true);
            gdPref.setFullScreenWindow(win);
        }
    }
}
