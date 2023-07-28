package replete.ui.form;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;

import replete.ui.GuiUtil;
import replete.ui.button.IconButton;


/**
 * @author Derek Trumbo
 */

public class OptionButtonPanel extends JPanel {
    protected JComponent comp;
    protected JButton btn;

    public OptionButtonPanel(JComponent c, String ttt, Icon icon, ActionListener listener) {
        comp = c;

        btn = new IconButton(icon, ttt, 5, listener);

        Border b = BorderFactory.createEmptyBorder(0, 0, 0, 5);
        setLayout(new BorderLayout());
        GuiUtil.addBorderedComponent(this, comp, b, BorderLayout.CENTER);
        add(btn, BorderLayout.EAST);
    }

    public void focus() {
        comp.requestFocusInWindow();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        comp.setEnabled(enabled);
        btn.setEnabled(enabled);
    }
}
