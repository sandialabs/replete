package replete.scrutinize.archive.app2;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import replete.text.ToStringFormatter;


public class AppState {


    ////////////
    // FIELDS //
    ////////////

    private Dimension mainFrameSize = null;
    private Point mainFrameLoc = null;
    private int mainFrameExtState = JFrame.NORMAL;
    private String lafClassName;
    private String lafThemeName;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AppState() {
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Dimension getMainFrameSize() {
        return mainFrameSize;
    }
    public void setMainFrameSize(Dimension mainFrameSize) {
        this.mainFrameSize = mainFrameSize;
    }
    public Point getMainFrameLoc() {
        return mainFrameLoc;
    }
    public void setMainFrameLoc(Point mainFrameLoc) {
        this.mainFrameLoc = mainFrameLoc;
    }
    public int getMainFrameExtState() {
        return mainFrameExtState;
    }
    public void setMainFrameExtState(int mainFrameExtState) {
        this.mainFrameExtState = mainFrameExtState;
    }
    public String getLafClassName() {
        return lafClassName;
    }
    public void setLafClassName(String lafName) {
        lafClassName = lafName;
    }
    public String getLafThemeName() {
        return lafThemeName;
    }
    public void setLafThemeName(String lafThemeName) {
        this.lafThemeName = lafThemeName;
    }


    ////////////
    // STATIC //
    ////////////

    protected static AppState state;
    public static void setState(AppState appState) {
        state = appState;
    }
    public static AppState getState() {
        return state;
    }


    //////////////
    // toString //
    //////////////

    @Override
    public String toString() {
        ToStringFormatter ts = new ToStringFormatter();
        return ts.render(this);
    }
}

