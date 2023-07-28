package finio.ui.view;



public class RenameViewEvent {


    ////////////
    // FIELDS //
    ////////////

    private ViewContainerPanel pnlViewCont;
    private int viewIndex;
    private String previousName;
    private String currentName;
    // WorldContext/WorldPanel someday?


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RenameViewEvent(ViewContainerPanel pnlViewCont, int viewIndex,
                           String previousName, String currentName) {
        this.pnlViewCont = pnlViewCont;
        this.viewIndex = viewIndex;
        this.previousName = previousName;
        this.currentName = currentName;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ViewContainerPanel getViewContainerPanel() {
        return pnlViewCont;
    }
    public int getViewIndex() {
        return viewIndex;
    }
    public String getPreviousName() {
        return previousName;
    }
    public String getCurrentName() {
        return currentName;
    }
}
