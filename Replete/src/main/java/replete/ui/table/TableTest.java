package replete.ui.table;

import java.awt.Color;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.table.rich.RichTableRowList;
import replete.ui.table.rich.RichTableRowTableModel;
import replete.ui.windows.Dialogs;

public class TableTest extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    private RTable tblData;
    private RichTableRowTableModel mdlData;
    private RTable tblData2;
    private RichTableRowTableModel mdlData2;
    private RTable tblData3;
    private RichTableRowTableModel mdlData3;



    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TableTest() {
        mdlData = new RichTableRowTableModel() {
            @Override
            protected int getPropertyPreferredWidth() {
                return 130;
            }
            @Override
            protected int getValuePreferredWidth() {
                return 450;
            }
        };
        mdlData2 = new RichTableRowTableModel() {
            @Override
            protected int getPropertyPreferredWidth() {
                return 130;
            }
            @Override
            protected int getValuePreferredWidth() {
                return 450;
            }
        };
        mdlData3 = new RichTableRowTableModel() {
            @Override
            protected int getPropertyPreferredWidth() {
                return 130;
            }
            @Override
            protected int getValuePreferredWidth() {
                return 450;
            }
        };

        Lay.BLtg(this,
            "C", Lay.GL(3, 1,
                tblData = Lay.tbl(mdlData, "rowheight=50"),
                Lay.tblp(tblData2 = Lay.tbl(mdlData2, "rowheight=50"), "pos=N"),
                tblData3 = Lay.tbl(mdlData3, "rowheight=50")
            ),
            "bg=100"
        );

        tblData3.setAutoCreateRowSorter(true);

        RichTableRowList rows = createTableContent();
        mdlData.setRows(rows);
        RichTableRowList rows2 = createTableContent();
        mdlData2.setRows(rows2);
        RichTableRowList rows3 = createTableContent();
        mdlData3.setRows(rows3);
    }

    private RichTableRowList createTableContent() {
        RichTableRowList rows = new RichTableRowList();
        rows.add("ID", "a");

        rows.addButton("Error?", ImageLib.get(CommonConcepts.ERROR), "Show Error", e -> {
            Dialogs.showDetails(getWindow(),
                "An error has occurred reading the state from the database.",
                "State Error", new RuntimeException("test error"));
        }, Color.red);

        return rows;
    }


    public static void main(String[] args) {
        Lay.BLtg(Lay.fr(), "C", new TableTest(), "size=600,center,visible");
    }
}
