package replete.ui.progress;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

import replete.ui.lay.Lay;

public class ProgressBarPanel extends JPanel {
    private JProgressBar pgb;
    public ProgressBarPanel(JProgressBar pgb) {
        this.pgb = pgb;
        setBorder(pgb.getBorder());
        pgb.setBorderPainted(false);
        pgb.setBorder(null);
        Lay.BLtg(this,
            "C", pgb,
            "opaque=false"
        );
    }
    public JProgressBar getProgressBar() {
        return pgb;
    }
}
