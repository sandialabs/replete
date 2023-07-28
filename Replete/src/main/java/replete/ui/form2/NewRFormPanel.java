package replete.ui.form2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.ui.lay.GBC;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.tabbed.RNotifPanel;


/**
 * @author Derek Trumbo
 */


// Default Heights: 16 - lbl, 20 - txt, 24 - chk, 25 - cbo, 22 - iconbtn


public abstract class NewRFormPanel extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final int MAX_WIDTH = 100000;
    public static final int DEFAULT_CAPTION_WIDTH = 100;
    public static final int DEFAULT_TOP_MARGIN = 7;
    public static final int DEFAULT_HEIGHT = 30;

    // Core

    private List<FieldDescriptor> fields = new ArrayList<>();
    private int captionWidth;
    private int interFieldSpacing = 10;
    private int marginTop = 10;
    private int marginLeft = 10;
    private int marginRight = 10;
    private int marginBottom = 10;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NewRFormPanel() {
        this(DEFAULT_CAPTION_WIDTH);
    }
    public NewRFormPanel(int cWidth) {
        captionWidth = cWidth;
    }

    protected void init() {
        String bCode = marginLeft + "l" + marginRight + "r" + marginTop + "t" + marginBottom + "b";
        Lay.BxLtg(this, "eb=" + bCode);

        addFields();

        for(int f = 0; f < fields.size(); f++) {
            FieldDescriptor field = fields.get(f);
            NewFieldPanel pnlField = new NewFieldPanel(field, captionWidth);
            field.pnlField = pnlField;

            add(Lay.vs(field.marginTop));
            add(pnlField);

            // If there is help text to be shown below the component...
            if(field.helpText != null && field.helpStyle == HelpStyle.INLINE_UNDER) {
                JPanel pnlFieldHelp = Lay.BL("prefh=20,minh=20,maxh=20");
                if(field.caption != null) {
                    pnlFieldHelp.add(Lay.lb(" ", "pref=[" + captionWidth + ",5]"), BorderLayout.WEST);
                }
                JLabel lblHelp = Lay.lb("<html>" + field.helpText + "</html>");
//                JLabel lblHelp = Lay.lb(field.helpText);
//                add(lblHelp, BorderLayout.CENTER);
                GridBagConstraints c = GBC.c().a(GBC.W).wx(0.1);
                pnlFieldHelp.add(Lay.GBL(Lay.FL(lblHelp, null, "nogap"), c), BorderLayout.CENTER);
                add(pnlFieldHelp);
//System.out.println("Help Panel " + field.caption + ":");
//System.out.println("  lblHelp Size=" + GuiUtil.getSize(lblHelp));
//System.out.println("  pnlFieldHelp Size=" + GuiUtil.getSize(pnlFieldHelp));
            }

            add(Lay.vs(field.marginBottom));
            if(f < fields.size() - 1) {
                add(Lay.vs(interFieldSpacing));
            }
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    // Mutators

    public NewRFormPanel setMarginTop(int marginTop) {
        this.marginTop = marginTop;
        return this;
    }
    public NewRFormPanel setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }
    public NewRFormPanel setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }
    public NewRFormPanel setMarginRight(int marginRight) {
        this.marginRight = marginRight;
        return this;
    }
    public NewRFormPanel setMargin(int margin) {
        marginTop = margin;
        marginBottom = margin;
        marginLeft = margin;
        marginRight = margin;
        return this;
    }
    public NewRFormPanel setInterFieldSpacing(int interFieldSpacing) {
        this.interFieldSpacing = interFieldSpacing;
        return this;
    }

    protected void addFields() {}

    protected void addField(FieldDescriptor desc) {
        fields.add(desc);
    }


    //////////
    // MISC //
    //////////

    protected RPanel wrapAlignMidLeft(Component c) {
        return Lay.GBL(c, GBC.c().wx(0.5).a(GBC.LS).f(GBC.H));
    }
    protected RPanel wrapAlignMidLeft(Component c, String str) {
        return Lay.BL(
            "W", Lay.GBL(c, GBC.c().wx(0.5).a(GBC.LS).f(GBC.H)),
            "C", Lay.lb(str)
        );
    }
}
