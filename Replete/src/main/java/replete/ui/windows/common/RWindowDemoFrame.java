package replete.ui.windows.common;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.GuiUtil;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenu;
import replete.ui.menu.RMenuItem;
import replete.ui.windows.common.RWindowDemo.MyChildDialog;
import replete.ui.windows.common.RWindowDemo.MyChildFrame;
import replete.ui.windows.common.RWindowDemo.WindowListable;
import replete.ui.windows.escape.EscapeFrame;


public class RWindowDemoFrame extends EscapeFrame implements WindowListable {
    private JMenu mnuWindow;
    private CenterPanel cp;

    public RWindowDemoFrame() {
        super("CommonWindowDemo-Frame");

        addChildWindowCreationHandler("single-frame", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new MyChildFrame((String) args[0]);
            }
        });
        addChildWindowCreationHandler("multi-frame", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new MyChildFrame((String) args[0]);
            }
        });
        addChildWindowCreationHandler("single-dialog", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new MyChildDialog(RWindowDemoFrame.this, (String) args[0], false);
            }
        });
        addChildWindowCreationHandler("multi-dialog", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new MyChildDialog(RWindowDemoFrame.this, (String) args[0], false);
            }
        });
        addChildWindowCreationHandler("single-dialog-modal", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new MyChildDialog(RWindowDemoFrame.this, (String) args[0], true);
            }
        });
        addChildWindowCreationHandler("cwdf", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new RWindowDemoFrame();
            }
        });
        addChildWindowCreationHandler("cwdd", new ChildWindowCreationHandler() {
            public RWindow create(Object... args) {
                return new RWindowDemoDialog(RWindowDemoFrame.this, (Boolean) args[0]);
            }
        });

        addChildWindowListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                cp.setOutput(listWindows());
                updateWindowMenu();
            }
        });

        JMenu mnuFile = new RMenu("&File");
        mnuWindow = new RMenu("&Window");

        JMenuBar bar = new JMenuBar();
        bar.add(mnuFile);
        bar.add(mnuWindow);
        updateWindowMenu();

        cp = new CenterPanel(this, this);
        Lay.BLtg(this, "C", cp);

        setJMenuBar(bar);

        setSize(700, 200);
        setLocationRelativeTo(null);
    }

    private void updateWindowMenu() {
        mnuWindow.removeAll();
        JLabel lbl = new JLabel("  Open Windows:");
        GuiUtil.setSize(lbl, new Dimension(150, lbl.getPreferredSize().height));
        mnuWindow.add(lbl);
        List<RWindow> visWindows = getVisibleChildWindows();
        int i = 1;
        for(RWindow win : visWindows) {
            String text = ": " + win.getTitle();
            if(i < 10) {
                text = "&" + i + text;
            } else if(i == 10) {
                text = "1&0" + text;
            } else {
                text = i + text;
            }
            JMenuItem mnuWindowItem = new RMenuItem(text);
            final RWindow finalWin = win;
            mnuWindowItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showChildWindow(finalWin);
                }
            });
            mnuWindow.add(mnuWindowItem);
            i++;
        }
        if(i == 1) {
            mnuWindow.add(new JLabel("      <none>"));
        }
    }

    public String listWindows() {
        String ret = "";
        List<RWindow> allWindows = getAllChildWindows();
        ret += "All:\n";
        for(RWindow win : allWindows) {
            ret += "   " + win.getTitle() + "\n";
        }
        List<RWindow> visWindows = getVisibleChildWindows();
        ret += "Visible:\n";
        for(RWindow win : visWindows) {
            ret += "   " + win.getTitle() + "\n";
        }
        ret += "\n";
        return ret;
    }
}