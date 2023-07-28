package replete.ui.uiaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

/**
 * @author Derek Trumbo
 */

public class UIActionToolBar extends JToolBar {

    public UIActionToolBar(UIActionMap actionMap) {
        initGUI(actionMap);
        actionMap.validate();
    }

    protected void initGUI(UIActionMap actionMap) {
        String prevGroup = null;
        boolean firstAction = true;

        for(final UIAction action : actionMap.getActions()) {
            final ToolBarActionDescriptor tDesc =
                (ToolBarActionDescriptor) action.getDescriptor(
                    ToolBarActionDescriptor.class);

            // If this action is to be manifested in the tool bar...
            if(tDesc != null) {

                // If this isn't the first action, and the group isn't
                // the same as the previous action, add a separator.
                if(!firstAction) {

                    boolean groupChanged;

                    if(prevGroup == null && tDesc.group == null) {
                        groupChanged = false;
                    } else if(prevGroup != null) {
                        groupChanged = !prevGroup.equals(tDesc.group);
                    } else {
                        groupChanged = true;    // prevGroup == null, tDesc.group != null
                    }

                    if(groupChanged) {
                        addSeparator();
                    }
                }
                firstAction = false;
                prevGroup = tDesc.group;

                // Decide what kind of button to use.
                AbstractButton btn;
                if(tDesc.isToggle) {
                    btn = new JToggleButton();
                } else {
                    btn = new JButton();
                }

                // Set remaining parameters.
                if(tDesc.toolTipText != null) {
                    btn.setToolTipText(tDesc.toolTipText);
                }

                // Prefer action's listener
                if(action.getListener() != null) {
                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            action.getListener().actionPerformed(e, action);
                        }
                    });

                } else if(tDesc.listener != null) {
                    btn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            tDesc.getListener().actionPerformed(e, action);
                        }
                    });

                } else {
                    MenuBarActionDescriptor mDesc = (MenuBarActionDescriptor) action.getDescriptor(MenuBarActionDescriptor.class);
                    if(mDesc != null) {
                        if(mDesc.component instanceof JMenu) {
                            final JMenu mnu = (JMenu) mDesc.component;
                            btn.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mousePressed(MouseEvent arg0) {
                                    if(arg0.getComponent().isEnabled()) {
                                        JPopupMenu popupMenu = mnu.getPopupMenu();
                                        popupMenu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
                                        popupMenu.setInvoker(mnu);
                                    }
                                }
                            });
                        }
                    }
                }

                if(tDesc.icon != null) {
                    btn.setIcon(tDesc.icon);
                }

                btn.setEnabled(false);

                // Save this button to the descriptor.
                tDesc.component = btn;

                // Add to the tool bar.
                add(btn);
            }
        }
    }
}
