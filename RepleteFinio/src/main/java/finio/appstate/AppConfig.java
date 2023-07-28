package finio.appstate;

import javax.swing.JTabbedPane;

import gov.sandia.webcomms.http.Http;
import gov.sandia.webcomms.http.options.HttpRequestOptions;
import replete.event.ExtChangeNotifier;


public class AppConfig {


    ////////////
    // FIELDS //
    ////////////

    private HttpRequestOptions requestOptions = new HttpRequestOptions();
    private String proxyHost;
    private int proxyPort;
    private boolean nodeInfoEnabled = true;
    private int splitPaneState = 0;
    private boolean worldsUseDesktopPane;
    private boolean worldsExpandSingleWorld;
    private int worldsTabPlacement = JTabbedPane.TOP;
    private boolean showNodeMeta = true;
    private boolean suppressEmails = false;
    private String defaultEmailFromAddress = "dtrumbo@sandia.gov";


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public HttpRequestOptions getRequestOptions() {
        return requestOptions;
    }
    public String getProxyHost() {
        return proxyHost;
    }
    public int getProxyPort() {
        return proxyPort;
    }
    public boolean isNodeInfoEnabled() {
        return nodeInfoEnabled;
    }
    public int getSplitPaneState() {
        return splitPaneState;
    }
    public boolean isWorldsUseDesktopPane() {
        return worldsUseDesktopPane;
    }
    public boolean isWorldsExpandSingleWorld() {
        return worldsExpandSingleWorld;
    }
    public int getWorldsTabPlacement() {
        return worldsTabPlacement;
    }
    public boolean isShowNodeMeta() {
        return showNodeMeta;
    }
    public boolean isSuppressEmails() {
        return suppressEmails;
    }
    public String getDefaultEmailFromAddress() {
        return defaultEmailFromAddress;
    }

    // Mutators

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
    public void setNodeInfoEnabled(boolean nodeInfoEnabled) {
        boolean prev = this.nodeInfoEnabled;
        this.nodeInfoEnabled = nodeInfoEnabled;
        firePropertyChangeNotifier("nodeInfoEnabled", prev, this.nodeInfoEnabled);
    }
    public void setSplitPaneState(int splitPaneState) {
        if(splitPaneState != this.splitPaneState) {
            int prev = this.splitPaneState;
            this.splitPaneState = splitPaneState;
            firePropertyChangeNotifier("splitPaneState", prev, this.splitPaneState);
        }
    }
    public void setWorldsUseDesktopPane(boolean worldsUseDesktopPane) {
        boolean prev = this.worldsUseDesktopPane;
        this.worldsUseDesktopPane = worldsUseDesktopPane;
        firePropertyChangeNotifier("worldsUseDesktopPane", prev, this.worldsUseDesktopPane);
    }
    public void setWorldsExpandSingleWorld(boolean worldsExpandSingleWorld) {
        boolean prev = this.worldsExpandSingleWorld;
        this.worldsExpandSingleWorld = worldsExpandSingleWorld;
        firePropertyChangeNotifier("worldsExpandSingleWorld", prev, this.worldsExpandSingleWorld);
    }
    public void setWorldsTabPlacement(int worldsTabPlacement) {
        int prev = this.worldsTabPlacement;
        this.worldsTabPlacement = worldsTabPlacement;
        firePropertyChangeNotifier("worldsTabPlacement", prev, this.worldsTabPlacement);
    }
    public void setShowNodeMeta(boolean showNodeMeta) {
        boolean prev = this.showNodeMeta;
        this.showNodeMeta = showNodeMeta;
        firePropertyChangeNotifier("showNodeMeta", prev, this.showNodeMeta);
    }
    public void setSuppressEmails(boolean suppressEmails) {
        boolean prev = this.suppressEmails;
        this.suppressEmails = suppressEmails;
        firePropertyChangeNotifier("suppressEmails", prev, this.suppressEmails);
    }
    public void setDefaultEmailFromAddress(String defaultEmailFromAddress) {
        String prev = this.defaultEmailFromAddress;
        this.defaultEmailFromAddress = defaultEmailFromAddress;
        firePropertyChangeNotifier("defaultEmailFromAddress", prev, this.defaultEmailFromAddress);
    }


    //////////
    // MISC //
    //////////

    public void initProxy() {
        if(proxyHost == null) {
            Http.getInstance().clearProxy();
        } else {
            Http.getInstance().setProxy(proxyHost, proxyPort);
        }
    }
    protected Object readResolve() {
        propertyChangeNotifier = new ExtChangeNotifier<>();
        return this;
    }


    //////////////
    // NOTIFIER //
    //////////////

    private transient ExtChangeNotifier<AppStateChangeListener> propertyChangeNotifier =
        new ExtChangeNotifier<>();
    public void addPropertyChangeListener(AppStateChangeListener listener) {
        propertyChangeNotifier.addListener(listener);
    }
    public void removePropertyChangeListener(AppStateChangeListener listener) {
        propertyChangeNotifier.removeListener(listener);
    }
    private void firePropertyChangeNotifier(String name, Object prev, Object curr) {
        propertyChangeNotifier.fireStateChanged(new AppStateChangeEvent(name, prev, curr));
    }
}
