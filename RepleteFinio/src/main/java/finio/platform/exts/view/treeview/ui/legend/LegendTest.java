package finio.platform.exts.view.treeview.ui.legend;

import javax.swing.JPanel;

import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class LegendTest extends EscapeFrame {

    public LegendTest() {
        super("Finio Tree Legend Test");
        Lay.BxLtg(this,
            "size=600,center"
        );
        for(LegendEntry entry : LegendEntry.getEntries()) {
            add(Lay.hn(new LegendEntryPanel(entry), "eb=5tlr"));
        }
    }

    private class LegendEntryPanel extends JPanel {
        public LegendEntryPanel(LegendEntry le) {
            Lay.BxLtg(this,
                Lay.BL("C", Lay.lb("  " + le.getTitle(), "bold", le.getIcon()), "E", Lay.lb(le.getType().getLabel(), "fg=60ADFF"), "alignx=0"),
                Lay.lb("  " + le.getDescription(), "italic", GuiUtil.createBlankImage(), "alignx=0")
            );
        }
    }

    public static void main(String[] args) {
        LegendTest frame = new LegendTest();
//        frame.pack();
        frame.setVisible(true);
    }
}