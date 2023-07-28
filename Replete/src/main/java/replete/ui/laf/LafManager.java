package replete.ui.laf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.menu.RMenuItem;

// sun.font.FontDesignMetrics$MetricsKey

public class LafManager {


    ////////////
    // FIELDS //
    ////////////

    private static RebootFramesListener rebootFramesListener;
    private static LafCatalog catalog;
    private static Laf currentLaf;
    private static JRadioButtonMenuItem mnuPrevLafTheme;
    private static List<RMenuItem> currentLafLabels = new ArrayList<>();


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {
        catalog = new LafCatalog();
        for(Laf laf : catalog.getLafs().values()) {
            if(UIManager.getLookAndFeel().getClass().getName().equals(laf.getCls())) {
                currentLaf = laf;
            }
        }
    }

    public static void setNeedToRebootListener(RebootFramesListener listener) {
        rebootFramesListener = listener;
    }
    public static Laf getCurrentLaf() {
        return currentLaf;
    }
    public static Laf getLaf(String className) {
        return catalog.getLafs().get(className);
    }

    public static JMenu createLafMenu() {
        return createLafMenu(false);
    }
    public static JMenu createLafMenu(boolean includeOptionMenu) {
        RMenuItem mnuCurrentLaf = Lay.mi(CommonConcepts.ACCEPT);
        currentLafLabels.add(mnuCurrentLaf);
        updateCurrentMenuItems();

        JMenu mnuLafAll = Lay.mn("&Look && Feel", CommonConcepts._PLACEHOLDER);
        mnuLafAll.add(mnuCurrentLaf);
        mnuLafAll.add(new JSeparator());
        for(JMenu mnuLaf : createMenuItems().values()) {
            mnuLafAll.add(mnuLaf);
        }
        if(includeOptionMenu) {
            mnuLafAll.add(new JSeparator());
            mnuLafAll.add(buildOptionMenu());
        }
        return mnuLafAll;
    }
    public static JPopupMenu createLafPopupMenu() {
        return createLafPopupMenu(false);
    }
    public static JPopupMenu createLafPopupMenu(boolean includeOptionMenu) {
        RMenuItem mnuCurrentLaf = Lay.mi(CommonConcepts.ACCEPT);
        currentLafLabels.add(mnuCurrentLaf);
        updateCurrentMenuItems();

        JPopupMenu mnuLafAll = new JPopupMenu();
        mnuLafAll.add(mnuCurrentLaf);
        mnuLafAll.add(new JSeparator());
        for(JMenu mnuLaf : createMenuItems().values()) {
            mnuLafAll.add(mnuLaf);
        }
        if(includeOptionMenu) {
            mnuLafAll.add(new JSeparator());
            mnuLafAll.add(buildOptionMenu());
        }
        return mnuLafAll;
    }

    private static Map<String, JMenu> createMenuItems() {
        ButtonGroup grp = new ButtonGroup();
        Map<String, JMenu> lafMenus = new TreeMap<>();
        for(Laf laf : catalog.getLafs().values()) {
            String[] themes = laf.getThemeNames();
            JMenu mnuLaf = new JMenu(laf.getName());
            lafMenus.put(laf.getName(), mnuLaf);
            for(String theme : themes) {
                JRadioButtonMenuItem mnuLafTheme = new JRadioButtonMenuItem(theme);
                mnuLafTheme.addActionListener(e -> {
                    if(!changeToLaf(laf.getCls(), theme)) {
                        mnuPrevLafTheme.setSelected(true);   // change selected menu item back to previous
                    } else {
                        mnuPrevLafTheme = mnuLafTheme;
                    }
                });
                mnuLaf.add(mnuLafTheme);
                grp.add(mnuLafTheme);
                if(laf.getCls().equals(currentLaf.getCls()) && theme.equals(currentLaf.getCurTheme())) {
                    mnuLafTheme.setSelected(true);
                    mnuPrevLafTheme = mnuLafTheme;
                }
            }
        }
        return lafMenus;
    }

    private static JMenu buildOptionMenu() {
        ButtonGroup grp2 = new ButtonGroup();
        JRadioButtonMenuItem mnuTopPlc = new JRadioButtonMenuItem("Top Placement");
        JRadioButtonMenuItem mnuLeftPlc = new JRadioButtonMenuItem("Left Placement");
        JRadioButtonMenuItem mnuBotPlc = new JRadioButtonMenuItem("Bottom Placement");
        JRadioButtonMenuItem mnuRightPlc = new JRadioButtonMenuItem("Right Placement");
        JCheckBoxMenuItem mnuScroll = new JCheckBoxMenuItem("Scrollable Tabs");
        mnuScroll.addActionListener(e -> {
            if(mnuScroll.isSelected()) {
                changeTabPropeties(-1, JTabbedPane.SCROLL_TAB_LAYOUT);
            } else {
                changeTabPropeties(-1, JTabbedPane.WRAP_TAB_LAYOUT);
            }
        });

        mnuTopPlc.addActionListener(e -> changeTabPropeties(JTabbedPane.TOP, -1));
        mnuLeftPlc.addActionListener(e -> changeTabPropeties(JTabbedPane.LEFT, -1));
        mnuBotPlc.addActionListener(e -> changeTabPropeties(JTabbedPane.BOTTOM, -1));
        mnuRightPlc.addActionListener(e -> changeTabPropeties(JTabbedPane.RIGHT, -1));

        grp2.add(mnuTopPlc);
        grp2.add(mnuLeftPlc);
        grp2.add(mnuBotPlc);
        grp2.add(mnuRightPlc);
        mnuTopPlc.setSelected(true);
        JMenu mnuTabOpts = new JMenu("Tabs");
        mnuTabOpts.add(mnuTopPlc);
        mnuTabOpts.add(mnuLeftPlc);
        mnuTabOpts.add(mnuBotPlc);
        mnuTabOpts.add(mnuRightPlc);
        mnuTabOpts.add(new JSeparator());
        mnuTabOpts.add(mnuScroll);
        JMenu mnuOpts = new JMenu("Options");
        mnuOpts.add(mnuTabOpts);
        // Could have anti-aliasing maybe?
        return mnuOpts;
    }

    private static void changeTabPropeties(int plcType, int scrollType) {
        for(int i = 0; i < Window.getWindows().length; i++) {
            Window w = Window.getWindows()[i];
            changeTabProperties(w, plcType, scrollType);
        }
    }
    public static void changeTabProperties(Container c, int plcType, int scrollType) {
        for(Component cc : c.getComponents()) {
            if(cc instanceof JTabbedPane) {
                if(plcType >= 0) {
                    ((JTabbedPane) cc).setTabPlacement(plcType);
                }
                if(scrollType >= 0) {
                    ((JTabbedPane) cc).setTabLayoutPolicy(scrollType);
                }
            }
            if(cc instanceof Container) {
                changeTabProperties((Container) cc, plcType, scrollType);
            }
        }
    }

    // We are assuming that createLafMenu only gets called once per application.
    // Otherwise, we'd need some kind of infrastructure to update all menus
    // created when the LAF is changed.
    private static void updateCurrentMenuItems() {
        if(currentLafLabels.size() != 0) {
            for(RMenuItem mnuCurrentLaf : currentLafLabels) {
                mnuCurrentLaf.setText(currentLaf.getName() + " / " + currentLaf.getCurTheme());
            }
        }
    }

    public static void initialize(String initClassName, String initTheme) {
        changeToLaf(initClassName, initTheme);
    }

    private static boolean changeToLaf(String newClassName, String newTheme) {
        try {
            Laf oldLaf = currentLaf;
            Laf newLaf = catalog.getLafs().get(newClassName);
            if(newLaf == null) {
                return false;
            }
            if(oldLaf.getCls().equals(newLaf.getCls()) && oldLaf.getCurTheme().equals(newTheme)) {
                return false;
            }
            boolean needReboot = oldLaf.isWindowDecorationOn() != newLaf.isWindowDecorationOn();
            if(needReboot && rebootFramesListener != null && !rebootFramesListener.allowReboot()) {
                return false;
            }
            newLaf.setCurTheme(newTheme);
            UIManager.setLookAndFeel(newLaf.getInst());
            currentLaf = newLaf;
            updateCurrentMenuItems();

            if(needReboot && rebootFramesListener != null) {
                rebootFramesListener.reboot();
            } else {
                for(int i = 0; i < Window.getWindows().length; i++) {
                    SwingUtilities.updateComponentTreeUI(Window.getWindows()[i]);
                }
            }
            return true;
        } catch(Exception e1) {
            e1.printStackTrace();
            return false;
        }
    }
}
