package replete.ui.text.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import replete.io.FileUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.sp.IconDescriptor;
import replete.ui.sp.LineNumberRange;
import replete.ui.sp.RScrollPane;
import replete.ui.sp.RangeHighlightDescriptor;
import replete.ui.sp.RulerClickListener;
import replete.ui.sp.RulerDescriptor;
import replete.ui.sp.RulerModel;
import replete.ui.windows.escape.EscapeFrame;


public class EditorTester extends EscapeFrame {


    ///////////
    // FIELD //
    ///////////

    private REditor ed;
    private ImageIcon imageWarning  = ImageLib.get(CommonConcepts.WARNING);
    private ImageIcon imageBookmark = ImageLib.get(CommonConcepts.BOOKMARK);
    private ImageIcon imageCancel   = ImageLib.get(CommonConcepts.CANCEL);


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public EditorTester(){
        super("Line Numbering");
        setIcon(CommonConcepts.OPTIONS);

        JButton btn, btn1, btn2, btn3, btn4, btn5,
                btn6, btn7, btn8, btn9, btn10,
                btn11, btn12, btn13, btn14, btn15,
                btn16, btn17;

        Lay.BLtg(this,
            "C", ed = Lay.ed(),
            "S", Lay.WL("R",
                btn = new JButton("add bkmk & warning icons"),
                btn4 = new JButton("add bkmk lower"),
                btn2 = new JButton("clear warnings"),
                btn3 = new JButton("clearall"),
                btn1 = new JButton("add range of cancel icons"),
                btn5 = new JButton("bg"),
                btn6 = new JButton("marginfg"),
                btn7 = new JButton("marginbg"),
                btn8 = new JButton("Range1"),
                btn9 = new JButton("RangeMult"),
                btn10 = new JButton("Clear Ranges"),
                btn11 = new JButton("Flip Line Number Side"),
                btn12 = new JButton("Toggle ranges & icons"),
                btn13 = new JButton("Toggle font"),
                btn14 = new JButton("Toggle Text Editable"),
                btn15 = new JButton("Set wider row header"),
                btn16 = new JButton("Toggle status line"),
                btn17 = new JButton("Toggle ruler")
            ),
            "size=600,center,visible,dco=exit"
        );

        btn.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            RulerModel model = sp.getRulerModel();
            model.addIcon("bookmarks", 2, new IconDescriptor(imageBookmark, "Hi There!"));
            model.addIcon("warnings",  2, new IconDescriptor(imageWarning, "Hi There Warning!"));
            model.addIcon("warnings",  3, imageWarning);
            focus(ed.getTextPane());
        });
        btn1.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            RulerModel model = sp.getRulerModel();
            model.addIcon("errors", new LineNumberRange(10, 15), imageCancel);
            focus(ed.getTextPane());
        });
        btn2.addActionListener(e -> {
            ed.getScrollPane().getRulerModel().clearIcons("warnings");
            focus(ed.getTextPane());
        });
        btn3.addActionListener(e -> {
            ed.getScrollPane().getRulerModel().clearIcons();
            focus(ed.getTextPane());
        });
        btn4.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            RulerModel model = sp.getRulerModel();
            model.addIcon("bookmarks", 9, new IconDescriptor(imageBookmark, "Hi There Lower!"));
            focus(ed.getTextPane());
        });
        btn5.addActionListener(e -> {
            ed.getScrollPane().getRulerPanel().setBackground(Color.red);
            focus(ed.getTextPane());
        });
        btn6.addActionListener(e -> {
            ed.getScrollPane().getRulerPanel().setMarginForeground(Color.blue);
            focus(ed.getTextPane());
        });
        btn7.addActionListener(e -> {
            ed.getScrollPane().getRulerPanel().setMarginBackground(Color.cyan);
            focus(ed.getTextPane());
        });
        btn8.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            RulerModel model = sp.getRulerModel();
            model.addRangeHighlight("relevance analysis", 2, Lay.clr("9EBEFF"));
            focus(ed.getTextPane());
        });
        btn9.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            RulerModel model = sp.getRulerModel();
            model.addRangeHighlight("relevance analysis",
                new LineNumberRange(1, 12), new RangeHighlightDescriptor(Lay.clr("C9FFDD"), "range!"));
            model.addRangeHighlight("relevance analysis",
                new LineNumberRange(7, 20), Lay.clr("C9FFDD"));
            focus(ed.getTextPane());
        });
        btn10.addActionListener(e -> {
            ed.getScrollPane().getRulerModel().clearRangeHighlights();
            focus(ed.getTextPane());
        });
        btn11.addActionListener(e -> {
            boolean flip = ed.getScrollPane().isFlipRowHeaderSide();
            ed.getScrollPane().setFlipRowHeaderSide(!flip);
            focus(ed.getTextPane());
        });
        btn12.addActionListener(e -> {
            boolean show = ed.getScrollPane().isShowRangesAndIcons();
            ed.getScrollPane().setShowRangesAndIcons(!show);
            focus(ed.getTextPane());
        });
        btn13.addActionListener(e -> {
            if(ed.getTextPane().getFont().getFontName().contains("Courier")) {
                ed.getTextPane().setFont(new Font("Arial", Font.PLAIN, 12));
            } else {
                ed.getTextPane().setFont(new Font("Courier New", Font.PLAIN, 12));
            }
            focus(ed.getTextPane());
        });
        btn14.addActionListener(e -> {
            boolean edit = ed.getTextPane().isEditable();
            ed.getTextPane().setEditable(!edit);
            focus(ed.getTextPane());
        });
        btn15.addActionListener(e -> {
            RScrollPane sp = ed.getScrollPane();
            if(sp.getMinRulerDigitColumns() == 3) {
                sp.setMinRulerDigitColumns(1);
            } else {
                sp.setMinRulerDigitColumns(3);
            }
        });
        btn16.addActionListener(e -> ed.setShowStatusLine(!ed.isShowStatusLine()));
        btn17.addActionListener(e -> ed.getScrollPane().setShowRuler(!ed.getScrollPane().isShowRuler()));
        String content = FileUtil.getTextContent(EditorTester.class.getResourceAsStream("content.txt"));
        ed.getTextPane().setText(content);
        addStuff();
    }

    private void addStuff() {
        RScrollPane sp = ed.getScrollPane();
        RulerModel model = sp.getRulerModel();

        // Ranges
        model.addRangeHighlight(
            "relevance analysis",
            new LineNumberRange(1, 12),
            new RangeHighlightDescriptor(Lay.clr("C9FFDD"), "range!", clickListener)
        );

//        model.addRangeHighlight("relevance analysis", new LineNumberRange(12, 20), Lay.clr("C9FFDD"));
        model.addRangeHighlight("relevance analysis", new LineNumberRange(12, 14), Lay.clr("9EBEFF"));

        // Icons
        model.addIcon("bookmarks", 9, new IconDescriptor(imageBookmark, "Hi There Lower!"));
        model.addIcon("bookmarks", 2, new IconDescriptor(imageBookmark, "Hi There!"));
        model.addIcon("warnings",  2, new IconDescriptor(imageWarning, "Hi There Warning!", clickListener));
        model.addIcon("warnings",  3, imageWarning);
        model.addIcon("errors",    new LineNumberRange(10, 15), imageCancel);

        focus(ed.getTextPane());
    }

    RulerClickListener clickListener = e -> {
        System.out.println("LINE=" + e.getLine() + ", X=" + e.getX() + ", Y=" + e.getY());
        for(RulerDescriptor cd : e.getDescriptors()) {
            System.out.println("   " + cd.getCategory() + " " + cd.getLines() + " " + cd.getToolTip());
            if(cd instanceof IconDescriptor) {
                System.out.println("      " + ((IconDescriptor) cd).getIcon());
            } else if(cd instanceof RangeHighlightDescriptor) {
                System.out.println("      " + ((RangeHighlightDescriptor) cd).getColor());
            }
        }
    };


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        new EditorTester();
    }
}