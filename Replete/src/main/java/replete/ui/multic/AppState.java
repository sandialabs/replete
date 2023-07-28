package replete.ui.multic;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import replete.text.ToStringFormatter;


public class AppState {


    ////////////
    // FIELDS //
    ////////////

    private List<String> commands = new ArrayList<>();
    private String rows = "1";
    private String cols = "1";
    private String label = "Console %T";
    private String tOff = "0";
    private String wd = "";
    private boolean topLabels = false;
    private boolean fixedWidth = false;
    private Dimension mainFrameSize = null;
    private Point mainFrameLoc = null;
    private int mainFrameExtState = JFrame.NORMAL;
//    private String lafClassName;
//    private String lafThemeName;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AppState() {
        commands.add("echo Row=%R, Col=%C, ID=%T");
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public List<String> getCommands() {
        return commands;
    }
    public void setCommands(List<String> newCommands) {
        commands = newCommands;
    }
    public String getRows() {
        return rows;
    }
    public void setRows(String rows) {
        this.rows = rows;
    }
    public String getCols() {
        return cols;
    }
    public void setCols(String cols) {
        this.cols = cols;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getTOffset() {
        return tOff;
    }
    public void setTOffset(String tOff) {
        this.tOff = tOff;
    }
    public String getWorkingDir() {
        return wd;
    }
    public void setWorkingDir(String wd) {
        this.wd = wd;
    }
    public boolean isTopLabels() {
        return topLabels;
    }
    public void setTopLabels(boolean topLabels) {
        this.topLabels = topLabels;
    }
    public boolean isFixedWidth() {
        return fixedWidth;
    }
    public void setFixedWidth(boolean fixedWidth) {
        this.fixedWidth = fixedWidth;
    }
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
//    public String getLafClassName() {
//        return lafClassName;
//    }
//    public void setLafClassName(String lafName) {
//        lafClassName = lafName;
//    }
//    public String getLafThemeName() {
//        return lafThemeName;
//    }
//    public void setLafThemeName(String lafThemeName) {
//        this.lafThemeName = lafThemeName;
//    }


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

