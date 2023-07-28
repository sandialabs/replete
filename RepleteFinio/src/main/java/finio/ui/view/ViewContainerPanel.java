package finio.ui.view;

import java.awt.BorderLayout;

import finio.ui.fpanel.FPanel;
import replete.ui.lay.Lay;

public class ViewContainerPanel extends FPanel {


    ///////////
    // FIELD //
    ///////////

    private ViewPanel pnlView;
    private String viewName;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ViewContainerPanel(ViewPanel pnlView, String viewName) {
        this.pnlView = pnlView;
        this.viewName = viewName;

        Lay.BLtg(this,
            "C", pnlView
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ViewPanel getViewPanel() {
        return pnlView;
    }
    public String getViewName() {
        return viewName;
    }

    // Accessors (Computed)

    public String createTabTitle() {
        return
            "<html><i>" + pnlView.getViewTypeName() +
            (viewName != null ? ":" : "") + "</i>" +
            (viewName != null ? " " + viewName : "") + "</html>";
    }

    // Mutators

    public void setViewPanel(ViewPanel pnlView) {
        removeAll();
        // remove listeners...

        this.pnlView = pnlView;
        // add listeners
        add(pnlView, BorderLayout.CENTER);

        updateUI();
    }
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
