/*
Copyright 2013 Sandia Corporation.
Under the terms of Contract DE-AC04-94AL85000 with Sandia Corporation,
the U.S. Government retains certain rights in this software.
Distributed under the BSD-3 license. See the file LICENSE for details.
*/

package replete.jgraph.ui;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import com.mxgraph.swing.mxGraphOutline;

import replete.event.ChangeNotifier;
import replete.jgraph.ui.images.RepleteGraphImageModel;
import replete.ui.button.IconButton;
import replete.ui.lay.Lay;


public class CollapsibleOutlinePanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private mxGraphOutline outline;
    private JButton btnCollapse;
    private JButton btnExpand;
    private boolean expanded;


    //////////////
    // NOTIFIER //
    //////////////

    private ChangeNotifier expandCollapseNotifier = new ChangeNotifier(this);
    public void addExpandCollapseListener(ChangeListener listener) {
        expandCollapseNotifier.addListener(listener);
    }
    private void fireExpandCollapseNotifier() {
        expandCollapseNotifier.fireStateChanged();
    }


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CollapsibleOutlinePanel(mxGraphOutline out) {
        outline = out;

        btnCollapse = new IconButton(RepleteGraphImageModel.ARROW_DIAG_DL, "Collapse Overview");
        btnCollapse.addActionListener(e -> collapse());

        btnExpand = new IconButton(RepleteGraphImageModel.ARROW_DIAG_UR, "Expand Overview");
        btnExpand.addActionListener(e -> expand());

        expand();  // Starts out expanded.
    }


    //////////////
    // ACCESSOR //
    //////////////

    public boolean isExpanded() {
        return expanded;
    }


    ///////////////////////
    // EXPAND / COLLAPSE //
    ///////////////////////

    public void expand() {
        removeAll();
        Lay.BLtg(this,
            "N", Lay.BL(
                "C", Lay.lb("Overview"),
                "E", btnCollapse,
                "bg=BAE1FF,eb=2,augb=mb(1b,black)"
            ),
            "C", outline,
            "mb=[2,#000066]"
        );
        updateUI();
        expanded = true;
        fireExpandCollapseNotifier();
    }

    public void collapse() {
        removeAll();
        Lay.BLtg(this,
            "C", btnExpand,
            "mb=[2,#000066]"
        );
        updateUI();
        expanded = false;
        fireExpandCollapseNotifier();
    }
}
