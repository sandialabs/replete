package finio.ui;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import finio.core.FUtil;
import finio.ui.actions.FActionMap;
import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.SelectionContext;
import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenu;
import replete.ui.uiaction.ActionValidator;
import replete.ui.uiaction.HoverEvent;
import replete.ui.uiaction.HoverListener;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionPopupMenu;

public class FContextMenuCreator {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private FActionMap actionMap;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FContextMenuCreator(AppContext ac, FActionMap actionMap) {
        this.ac = ac;
        this.actionMap = actionMap;
    }


    ///////////
    // POPUP //
    ///////////

    public JPopupMenu createPopupMenu(MouseEvent e) {

        SelectionContext[] Cs = ac.getSelectedValues();

        final JLabel lblInfo = Lay.lb(
            getSelectedText(Cs, "Selected         "),
            FinioImageModel.SELECTED,
            "eb=6l"
        );
        final JLabel lblInfo2 = Lay.lb("", FinioImageModel.ACTIONABLE, "eb=6l");

        lblInfo2.setForeground(Lay.clr("006806"));
        lblInfo2.setText("... Actionable");

        JPanel pnlInfo = Lay.GL(
            2, 1,
            lblInfo, lblInfo2,
            "eb=20r"
        );

        final UIActionPopupMenu mnuPopup = new UIActionPopupMenu(actionMap, pnlInfo);
        mnuPopup.addHoverListener(createHoverListener(lblInfo, lblInfo2));

//        if(isSelection()) {
//                JMenuItem mnuAnchor = actionMap.getPopupMenuComponent("toggle-anchor");
//                if(mnuAnchor != null) {
//                    mnuAnchor.setText(shouldUnanchor() ? "Un&anchor" : "&Anchor");
//                }
//                JMenuItem mnuPause = actionMap.getPopupMenuComponent("toggle-pause");
//                if(mnuPause != null) {
//                    mnuPause.setText(shouldUnpause() ? "Un&pause" : "&Pause");
//                }
//        }

        return mnuPopup;
    }

    private HoverListener createHoverListener(final JLabel lblInfo, final JLabel lblInfo2) {
        return new HoverListener() {
            public void stateChanged(HoverEvent e) {
                UIAction action = actionMap.getAction(e.getId());

                if(e.getType().equals("exit")) {
                    return;

                } else if(e.getComponent() instanceof RMenu) {
                    lblInfo2.setText("... Actionable");
                    return;
                }

                ActionValidator validator = action.getValidator();
                SelectionContext[] Cs;
                if(validator == null) {
                    Cs = ac.getSelectedValues();
                    lblInfo2.setText(getSelectedText(Cs, "Actionable"));

                } else {
                    validator.isValid(e.getId());
                    if(validator instanceof AActionValidator) {
                        List<SelectionContext> sel = ((AActionValidator) validator).getLastValidResults();
                        if(sel == null) {
                            lblInfo2.setText("Any Actionable");

                        } else {
                            Cs = sel.toArray(new SelectionContext[0]);
                            lblInfo2.setText(getSelectedText(Cs, "Actionable"));
                        }

                    } else {
                        Cs = new SelectionContext[0];
                        lblInfo2.setText("???2");
                    }
                }
            }
        };
    }

    private String getSelectedText(SelectionContext[] Cs, String modifier) {
        int nt = 0;
        int t = 0;

        if(Cs != null) {                              // Just in case for now.
            for(SelectionContext C : Cs) {
                if(FUtil.isTerminal(C.getV())) {
                    t++;
                } else {
                    nt++;
                }
            }
        }

        String msg = "";
        if(nt != 0) {
            msg += nt + " Non-Terminal" + StringUtil.s(nt);
        }
        if(t != 0) {
            if(!msg.isEmpty()) {
                msg += ", ";
            }
            msg += t + " Terminal" + StringUtil.s(t);
        }
        if(nt == 0 && t == 0) {
            msg = "0";
        }
        return msg + " " + modifier;
    }
}
