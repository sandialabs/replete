package replete.ui.form;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RLabel;


public class FieldPanel extends RPanel {
    private FieldDescriptor desc;
    public JLabel lblCaption;

    public FieldPanel(FieldDescriptor d) {
        this(d, RFormPanel.DEFAULT_CAPTION_WIDTH);
    }
    public FieldPanel(FieldDescriptor d, int captionWidth) {
        super(new BorderLayout());
        desc = d;

        // Add a caption label if there is one.
        if(d.caption != null) {
            String postfix = d.caption.endsWith("?") ? "" : ":";
            JLabel lbl = new RLabel(d.caption + postfix, d.icon, JLabel.LEADING);
            Lay.hn(lbl, d.captionLabelHints);
            lblCaption = lbl;
            lbl.setPreferredSize(new Dimension(captionWidth, d.height));
            lbl.setVerticalAlignment(SwingConstants.TOP);
            lbl.setBorder(BorderFactory.createEmptyBorder(RFormPanel.DEFAULT_TOP_MARGIN, 0, 0, 0));
            add(lbl, BorderLayout.WEST);
        }

        // If the component is a label, make sure it is bumped downward
        // just like the caption label.
        int cmpBorder;
        if(d.cmp instanceof JLabel) {
            ((JLabel) d.cmp).setVerticalAlignment(SwingConstants.TOP);
            cmpBorder = RFormPanel.DEFAULT_TOP_MARGIN;
        } else {
            cmpBorder = 0;
        }

        // Put the border onto the component (only has an effect for labels).
        if(d.cmp instanceof JComponent) {
            JComponent jcmp = (JComponent) d.cmp;
            Border newBorder = BorderFactory.createEmptyBorder(cmpBorder, 0, 0, 0);
            jcmp.setBorder(BorderFactory.createCompoundBorder(newBorder, jcmp.getBorder()));
        }

        // Add the component to the field panel, and add the field panel
        // to the pane panel.
        add(d.cmp, BorderLayout.CENTER);

        // If the field is expandable, we don't set the maximum size.
        Dimension fieldPanelSize = new Dimension(RFormPanel.MAX_WIDTH, d.height);
        setPreferredSize(fieldPanelSize);
        setMinimumSize(fieldPanelSize);
        if(!d.expandable) {
            setMaximumSize(fieldPanelSize);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        desc.cmp.setEnabled(enabled);
    }
}
