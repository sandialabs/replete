package replete.text.patterns;

import java.io.InputStream;

import replete.io.FileUtil;
import replete.ui.ColorLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;

public class PatternInterpretationInfoPanel extends RPanel {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PatternInterpretationInfoPanel() {
        InputStream is = PatternInterpretationInfoPanel.class.getResourceAsStream("pattern-interp.txt");
        String content = FileUtil.getTextContent(is);
        Lay.BLtg(this,
            "C", Lay.txa(content, "bold,wrap,editable=false,bg=" + Lay.clr(ColorLib.DEFAULT))
        );
    }
}
