package replete.jgraph.ui.test;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import replete.jgraph.ui.images.RepleteGraphImageModel;
import replete.progress.FractionProgressMessage;
import replete.progress.IndeterminateProgressMessage;
import replete.progress.PercentProgressMessage;
import replete.progress.ProgressMessage;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.panels.GradientPanel;
import replete.ui.text.RLabel;

// TODO: Clean up the visuals for the progress bar... the background
// color can't just be like the standard color, like the panels near it.
// Also, the colors are not as good as they could be.  Search for better
// colors.

public class StageWrapperStatusPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    private static final Color errorLighter = Lay.clr("FFBABA");
    private static final Color errorDarker = Lay.clr("FF7F7C");
    private static final Color errorBorder = Lay.clr("C65755");

    private static final Color completeLighter = Lay.clr("BEFFBC");
    private static final Color completeDarker = Lay.clr("678965");
    private static final Color completeBorder = Lay.clr("517A4E");

    private static final Color dirtyLighter = Lay.clr("FFF5C6");
    private static final Color dirtyDarker = Lay.clr("FFEC9B");
    private static final Color dirtyBorder = Lay.clr("D17A4F");

    private static final Color progStart = Lay.clr("E57200");
    private static final Color progMid = Lay.clr("F1E004");
    private static final Color progEnd = Lay.clr("8DD600");

    private static final Font font = new Font("Tahoma", Font.BOLD, 12);

    // UI

    private CardLayout layout;
    private JProgressBar pgb;
    private GradientPanel pnlStatus;
    private RLabel lblStatus;
    private RLabel lblProgress;
    private JPanel pnlProgressLabel;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public StageWrapperStatusPanel() {
        final JLayeredPane pnlProgress;

        Lay.CLtg(this,
            pnlStatus = (GradientPanel) Lay.FL("L", lblStatus = Lay.lb(), "gradient"),
            pnlProgress = Lay.lay(
                pgb = new JProgressBar(),
                pnlProgressLabel = Lay.FL("L",
                    lblProgress = Lay.lb(CommonConcepts.PROGRESS),
                    "opaque=false"
                )
            )
        );

        pnlProgress.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pgb.setBounds(0, 0, pnlProgress.getWidth(), pnlProgress.getHeight());
                pnlProgressLabel.setBounds(0, 0, pnlProgress.getWidth(), pnlProgress.getHeight());
            }
        });

        layout = (CardLayout) getLayout();
        pgb.setBorderPainted(false);
        pgb.setBorder(null);
        pgb.setFont(font);
        pgb.setFocusable(false);
        lblStatus.setFont(font);
        lblProgress.setFont(font);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public void updateProgress(ProgressMessage message) {

        if(message instanceof FractionProgressMessage) {
            pgb.setMinimum(0);
            pgb.setMaximum(((FractionProgressMessage) message).getValue2());
            pgb.setValue(((FractionProgressMessage) message).getValue1());
            pgb.setIndeterminate(false);
        } else if(message instanceof PercentProgressMessage) {
            pgb.setMinimum(0);
            pgb.setMaximum(100);
            pgb.setValue(message.calculatePercentDone());
            pgb.setIndeterminate(false);
        } else if(message instanceof IndeterminateProgressMessage) {
            pgb.setIndeterminate(true);
        }

        String num = message.isIndeterminate() ? "" : "(" + message.renderNumericMessage() + ") ";
        lblProgress.setText(num + message.renderTextualMessage());
        Color bg;
        if(!pgb.isIndeterminate()) {
            int rf, gf, bf;
            double pct = (double) pgb.getValue() / pgb.getMaximum();
            int rm = progMid.getRed();
            int gm = progMid.getGreen();
            int bm = progMid.getBlue();

            if(pct < 0.5) {
                int rs = progStart.getRed();
                int gs = progStart.getGreen();
                int bs = progStart.getBlue();
                pct = pct * 2;
                rf = rs + (int) ((rm - rs) * pct);
                gf = gs + (int) ((gm - gs) * pct);
                bf = bs + (int) ((bm - bs) * pct);

            } else {
                int re = progEnd.getRed();
                int ge = progEnd.getGreen();
                int be = progEnd.getBlue();
                pct = (pct - 0.5) * 2;
                rf = rm + (int) ((re - rm) * pct);
                gf = gm + (int) ((ge - gm) * pct);
                bf = bm + (int) ((be - bm) * pct);
            }
            bg = new Color(rf, gf, bf);
        } else {
            bg = progMid;
        }

        pgb.setForeground(bg);
    }
    public void setComplete() {
        setBorderColor(completeBorder);
        pnlStatus.setColors(completeLighter, completeDarker);
        lblStatus.setIcon(RepleteGraphImageModel.CHECK);
        lblStatus.setText("Complete");
        layout.first(this);
    }
    public void setDirty() {
        setBorderColor(dirtyBorder);
        pnlStatus.setColors(dirtyLighter, dirtyDarker);
        lblStatus.setIcon(RepleteGraphImageModel.DIRTY);
        lblStatus.setText("Dirty");
        layout.first(this);
    }
    public void setError() {
        setBorderColor(errorBorder);
        pnlStatus.setColors(errorLighter, errorDarker);
        lblStatus.setIcon(RepleteGraphImageModel.ERROR);
        lblStatus.setText("Error");
        layout.first(this);
    }
    public void setProgress() {
        layout.last(this);
    }
    private void setBorderColor(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        String hex = String.format("#%02x%02x%02x", r, g, b);
        Lay.hn(this, "mb=[1t," + hex + "]");
    }
}
