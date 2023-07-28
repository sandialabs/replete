package replete.ui.form2;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import replete.text.StringUtil;
import replete.ui.lay.GBC;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;


public class NewFieldPanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    private FieldDescriptor descriptor;
    public JLabel lblCaption;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NewFieldPanel(FieldDescriptor descriptor, int captionWidth) {
        super(new BorderLayout());
        this.descriptor = descriptor;

        // Add a caption label if there is one.
        if(descriptor.caption != null) {
            // It is not recommended for developers to put ":" on the end of
            // their captions - let this class do it for them.
            String postfix =
                StringUtil.isBlank(descriptor.caption) ? "" : (
                    descriptor.caption.endsWith("?") || descriptor.caption.endsWith(":") ? "" : ":");
            lblCaption = Lay.lb(
                descriptor.caption + postfix,
                descriptor.icon,
                descriptor.captionLabelHints
            );
            GridBagConstraints constraints = GBC.c().a(GBC.W).wx(0.1);
            JPanel pnlCaption = Lay.GBL(
                Lay.FL(lblCaption, "nogap"), constraints,
                "prefw=" + captionWidth + ",prefh=" + descriptor.height
            );
            add(pnlCaption, BorderLayout.WEST);
        }

        // If the component is a label, make sure it is bumped downward
        // just like the caption label.
//        int cmpBorder;
//        if(d.cmp instanceof JLabel) {
//            ((JLabel) d.cmp).setVerticalAlignment(SwingConstants.TOP);
//            cmpBorder = RFormPanel.DEFAULT_TOP_MARGIN;
//        } else {
//            cmpBorder = 0;
//        }

        // Put the border onto the component (only has an effect for labels).
//        if(d.cmp instanceof JComponent) {
//            JComponent jcmp = (JComponent) d.cmp;
//            Border newBorder = BorderFactory.createEmptyBorder(cmpBorder, 0, 0, 0);
//            jcmp.setBorder(BorderFactory.createCompoundBorder(newBorder, jcmp.getBorder()));
//        }

        // Add the component to the field panel.
        if(descriptor.fill) {
            // Can't use HelpStyle.INLINE_RIGHT here, but can do a button on the right perhaps.
            add(descriptor.component, BorderLayout.CENTER);

            Lay.hn(this, "minh=" + descriptor.height);
            Lay.hn(this, "prefh=" + descriptor.height);
            if(!descriptor.expandable) {
                Lay.hn(this, "maxh=" + descriptor.height);   // If the field is expandable, we don't set the maximum size.
            }

        } else {
            JLabel lblHelp;
            if(descriptor.helpText != null && descriptor.helpStyle == HelpStyle.INLINE_RIGHT) {
                lblHelp = Lay.lb("<html>" + descriptor.helpText + "</html>", "eb=" + descriptor.helpPadding + "l");
            } else {
                lblHelp = null;
            }
            GridBagConstraints constraints = GBC.c().a(GBC.W).wx(0.1);
            JPanel pnlValue = Lay.GBL(
                Lay.FL(descriptor.component, lblHelp, "nogap"), constraints
            );
            add(pnlValue, BorderLayout.CENTER);

            Lay.hn(this, "minh=" + descriptor.height);
            Lay.hn(this, "prefh=" + descriptor.height);
            Lay.hn(this, "maxh=" + descriptor.height);
//System.out.println("Field Panel " + d.caption + ":");
//System.out.printf("  %-30s = %s%n", "d.component " + d.component.getClass().getSimpleName(), GuiUtil.getSize(d.component));
//System.out.printf("  %-30s = %s%n", "fieldPanel", GuiUtil.getSize(this));
        }
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public FieldDescriptor getDescriptor() {
        return descriptor;
    }
    public JLabel getCaptionLabel() {
        return lblCaption;
    }

    // Mutators

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        descriptor.component.setEnabled(enabled);
    }
}
