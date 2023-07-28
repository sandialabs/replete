package finio.ui.actions;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class ActionDestinationDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public ActionDestinationDialog(JFrame parent) {
        super(parent, "Action Destination", true);
        setIcon(CommonConcepts.TARGET);
        JButton btnOk, btnCancel;

        String content =
            "Choose the destination of the result of the chosen action 'action' with respect to each of the selected values.";

        Lay.BLtg(this,
            "N", Lay.lb("<html>" + content + "</html>", "bg=220,eb=5,augb=mb(1b,black)"),
            "C", Lay.BxL(
//                Lay.BL(
                    "N", Lay.opt("<html>Replace value in situ.  This will remove the value on which the action operates and will replace it with the result of the action.</htm>", "alignx=0"),
//                    "prefh=40"
//                ),
                Lay.BxL(
                    Lay.opt("hi", "alignx=0"),//, Lay.p(Lay.lb("adfssa"))
                    Lay.tx("sadf", "alignx=0", "dimh=30"),
                    "alignx=0"
                ),
//                Lay.BL(
                    "W", Lay.opt("hi", "alignx=0"),//, Lay.p(Lay.lb("adfssa"))
//                ),
//                Lay.BL(
                    "W", Lay.opt("hi", "alignx=0"),//, Lay.p(Lay.lb("adfssa"))
//                ),
                Box.createVerticalGlue()
            ),
            "S", Lay.FL("R",
                btnOk = Lay.btn("&OK", CommonConcepts.ACCEPT),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL),
                "bg=100,mb=[1t,black]"
            ),
            "size=600,center"
        );

        btnCancel.addActionListener(e -> close());
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        ActionDestinationDialog frame = new ActionDestinationDialog(null);
        frame.setVisible(true);
    }
}

