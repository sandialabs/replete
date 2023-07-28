package finio.ui;

import javax.swing.ImageIcon;

import replete.ui.lay.Lay;
import replete.ui.panels.GradientPanel;

public class TitlePanel extends GradientPanel {
    public TitlePanel(String title, ImageIcon icon) {
        Lay.BLtg(this,
            "W", Lay.GBL(Lay.lb(icon, "eb=1t3l"), "opaque=false"),
            "C", Lay.lb(title, "fg=white,size=14,eb=4l"),
            "gradient,gradclr1=3143A5,gradclr2=4159D3,eb=2,augb=mb(2b,black)"
        );
    }
}
