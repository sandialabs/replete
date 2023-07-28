package replete.ui.uiaction;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import replete.ui.GuiUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.windows.escape.EscapeFrame;



// This class illustrates the "UI Action" framework.  The purpose
// of the framework is to provide an effortless way to generate
// aesthetically appealing, functional, consistent, and easy-to-
// implement user interfaces.

// The framework is relevant when you want a window to have a menu
// bar and/or a tool bar.  When creating a menu bar, it can be time-
// consuming to make menus and menu items with the proper captions,
// mnemonics, accelerator keys, make the buttons that correspond to
// each of the menu items (or some subset/superset thereof).

// The framework works by defining abstract "UI actions" that are
// essentially some action you want to allow the user to take in
// the UI.  Then, you describe how you want to allow that action
// to be visualized/manifested in the UI. This is done by giving
// each UI Action various "descriptors" that describe how that
// action with materialize into a representation in the UI.
// Currently there are just two types of descriptors: a menu bar
// descriptor and a tool bar descriptor.  Custom menu bar and
// tool bar classes know how to examine these descriptors and
// produce the relevant components.

// There's also the concept of "UI states".  The idea is that when
// an action is not applicable in the current moment, we don't want
// the user to be able to even click on it, but rather we want to
// disable the component so it's obvious to the user that that
// action is not relevant.  The common alternative is to allow UI
// components to remain enabled, but when clicked they either do or
// the software has to display a warning dialog box describing that
// the action is not available.  Not doing anything could confuse
// a user into wondering if something is broken.  The dialog box
// option is just an extra step that can often be avoided.

// So, you can put the "UI Action map" into various states, and
// each UI Action will know whether or not it should or should not
// be enabled in that state.

// The framework also handles check box menu items and toggle buttons
// on the tool bar.  An action that manifests itself as both a check
// box menu item and a toggle button in the tool bar will have those
// components synchronized.
//
// Another thing: the UI Action tool bar has another feature.  If you
// add a tool bar descriptor with no action listener, then the tool
// bar will look at the action and see if it has a menu bar descriptor
// whose component has already been set to a JMenu.  If so, when you
// click the button on the tool bar for that action, it will show a
// popup menu that is identical to the action's JMenu in the menu bar.
// For this reason, you must construct your tool bar after you construct
// your menu bar if you require this kind of functionality.

/**
 * @author Derek Trumbo
 */

public class UIActionDemo extends EscapeFrame {

    // Run this application to view the demo.
    public static void main(String[] args) {
        new UIActionDemo();
    }

    // The UI has 3 states: 1) when no project nor images are loaded,
    // 2) when a project is loaded but no images are loaded, and 3)
    // when both a project and images are loaded.  The application
    // defines these strings and they decide which components are
    // enabled in each given state.
    protected static final String MINIMAL_STATE = "minimal gui state";
    protected static final String PROJ_STATE = "project loaded state";
    protected static final String IMAGES_STATE = "images loaded state";

    protected String state = UIActionMap.DISABLE_ALL_STATE;

    protected UIActionMap actionMap;
    protected UIActionPopupMenu uiPopup;
    protected JTextField txt;

    private ImageModelConcept concept = CommonConcepts._PLACEHOLDER;

    public UIActionDemo() {
        super("UI Action Demo");

        // This is where you set up all the actions you want the
        // user to have access to.
        actionMap = new DemoActionMap();

        // The UI Action menu bar and the UI Action tool bar know
        // how to "render" the action map that they are provided.
        // All components are disabled initially and the UI Action
        // Map is in the "disable all" state.  The action
        JMenuBar menuBar = new UIActionMenuBar(actionMap);
        JToolBar toolBar = new UIActionToolBar(actionMap);
        uiPopup = new UIActionPopupMenu(actionMap);

        // Make sure any corresponding check box menu items and
        // toggle buttons remain synchronized.  Must do this
        // step after setting up the UI components.
        actionMap.syncToggleableDescriptors();

        // Besides the UI states that the map can be in, we can
        // also have the UI Actions call an arbitrary piece of
        // code to decide when it is/is not enabled.  Basically
        // a UI Action's associated components will be enabled
        // only if the UI state says they can be enabled AND
        // all registered deciders say the components can be
        // enabled.
        UIActionEnabledDecider checker = new UIActionEnabledDecider() {
            public boolean canEnable() {
                try {
                    return Integer.parseInt(txt.getText()) < 50;
                } catch(Exception e) {
                    return false;
                }
            }
        };

        // Add the above decider to the Paste action - both
        // descriptors.
        MenuBarActionDescriptor mDesc =
            (MenuBarActionDescriptor) actionMap.getAction("paste")
            .getDescriptor(MenuBarActionDescriptor.class);
        mDesc.addStateChecker(checker);

        ToolBarActionDescriptor tDesc =
            (ToolBarActionDescriptor) actionMap.getAction("paste")
            .getDescriptor(ToolBarActionDescriptor.class);
        tDesc.addStateChecker(checker);

        // Create some buttons to illustrate the framework.

        JButton btn1 = new JButton("DISABLE ALL");
        JButton btn2 = new JButton("MIN STATE");
        JButton btn3 = new JButton("PROJ STATE");
        JButton btn4 = new JButton("IMAGES STATE");
        final JButton btn5 = new JButton("POPUP MENU");

        btn1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionMap.setState(UIActionMap.DISABLE_ALL_STATE);
            }
        });
        btn2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                actionMap.setState(MINIMAL_STATE);
            }
        });
        btn3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionMap.setState(PROJ_STATE);
            }
        });
        btn4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionMap.setState(IMAGES_STATE);
            }
        });
        btn5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uiPopup.show(btn5, 10, 10);
            }
        });

        // The value of this text field will decide whether
        // the Paste action's components are enabled.
        txt = new JTextField(10);
        txt.setText("10");

        JPanel pnl = new JPanel();
        pnl.setLayout(new FlowLayout());
        pnl.add(btn1);
        pnl.add(btn2);
        pnl.add(btn3);
        pnl.add(btn4);
        pnl.add(txt);
        pnl.add(btn5);

        setJMenuBar(menuBar);
        add(toolBar, BorderLayout.PAGE_START);
        add(pnl, BorderLayout.CENTER);

        setSize(500, 200);
        setLocationRelativeTo(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private class DemoActionMap extends UIActionMap {

        public DemoActionMap() {

            // These maps tell each action whether its
            // components should be enabled or not in
            // each UI state.
            Map<String, Boolean> minimalEnabledStateMap = new HashMap<String, Boolean>();
            Map<String, Boolean> projectEnabledStateMap = new HashMap<String, Boolean>();
            Map<String, Boolean> imagesEnabledStateMap = new HashMap<String, Boolean>();

            // The UI actions that are always enabled need to
            // have true set for all states.
            minimalEnabledStateMap.put(MINIMAL_STATE, true);
            minimalEnabledStateMap.put(PROJ_STATE, true);
            minimalEnabledStateMap.put(IMAGES_STATE, true);

            // The UI actions that are enabled when there is
            // a project enabled needs to have true set for
            // merely the second and third states.
            projectEnabledStateMap.put(PROJ_STATE, true);
            projectEnabledStateMap.put(IMAGES_STATE, true);

            // The UI actions that are only enabled when
            // there are images loaded are the most restrictive.
            imagesEnabledStateMap.put(IMAGES_STATE, true);

            // Each UI Action: construct the action with a unique
            // string "ID".  Add a descriptor for each place you
            // want the action to show up.

            // Just menu bar.
            createAction("fileMenu")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setText("&File")
                        .setEnabledStateMap(minimalEnabledStateMap))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setText("&File")
                        .setEnabledStateMap(minimalEnabledStateMap));

            // Menu bar and tool bar.
            createAction("newProject")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("fileMenu")
                        .setText("&New Project...")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_N)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("fileMenu")
                        .setText("&New Project...")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_N)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("fileMenu")
                        .setToolTipText("New Project...")
                        .setIcon(concept)
                        .setListener(tbListener)
                        .setEnabledStateMap(minimalEnabledStateMap));

            createAction("openProject")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("fileMenu")
                        .setText("&Open Project...")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_O)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("fileMenu")
                        .setText("&Open Project...")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_O)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("fileMenu")
                        .setToolTipText("Open Project")
                        .setIcon(concept)
                        .setListener(tbListener)
                        .setEnabledStateMap(minimalEnabledStateMap));

            createAction()
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setSeparator(true))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setSeparator(true));

            createAction("exitProgram")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("fileMenu")
                        .setText("E&xit")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_Q)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("fileMenu")
                        .setText("E&xit")
                        .setIcon(concept)
                        .setEnabledStateMap(minimalEnabledStateMap)
                        .setAccKey(KeyEvent.VK_Q)
                        .setAccCtrl(true)
                        .setListener(menuListener));

            createAction("editMenu")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setText("&Edit")
                        .setEnabledStateMap(projectEnabledStateMap))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setText("&Edit")
                        .setEnabledStateMap(projectEnabledStateMap));

            createAction("paste")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Paste")
                        .setIcon(concept)
                        .setEnabledStateMap(projectEnabledStateMap)
                        .setAccKey(KeyEvent.VK_V)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Paste")
                        .setIcon(concept)
                        .setEnabledStateMap(projectEnabledStateMap)
                        .setAccKey(KeyEvent.VK_V)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Paste")
                        .setIcon(concept)
                        .setListener(tbListener)
                        .setEnabledStateMap(projectEnabledStateMap));

            createAction("copy")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Copy")
                        .setIcon(concept)
                        .setEnabledStateMap(projectEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_C)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Copy")
                        .setIcon(concept)
                        .setEnabledStateMap(projectEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_C)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Copy")
                        .setIcon(concept)
                        .setToggle(true)
                        .setListener(tbListener)
                        .setEnabledStateMap(projectEnabledStateMap));

            createAction()
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setSeparator(true))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setSeparator(true));

            createAction("cut")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu")
                        .setText("Cut")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_F1)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu")
                        .setText("Cut")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_F1)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Cut")
                        .setIcon(concept)
                        .setToggle(true)
                        .setListener(tbListener)
                        .setEnabledStateMap(imagesEnabledStateMap));

            createAction("otherop")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Other Options")
                        .setEnabledStateMap(imagesEnabledStateMap))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu")
                        .setText("&Other Options")
                        .setEnabledStateMap(imagesEnabledStateMap))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Other Options")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap));

            createAction("print")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu/otherop")
                        .setText("&Print")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_P)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu/otherop")
                        .setText("&Print")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_P)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Print")
                        .setIcon(concept)
                        .setToggle(true)
                        .setListener(tbListener)
                        .setEnabledStateMap(imagesEnabledStateMap));

            createAction("find")
                .addDescriptor(
                    new MenuBarActionDescriptor()
                        .setPath("editMenu/otherop")
                        .setText("&Find")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_F)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("editMenu/otherop")
                        .setText("&Find")
                        .setIcon(concept)
                        .setEnabledStateMap(imagesEnabledStateMap)
                        .setCheckMenu(true)
                        .setAccKey(KeyEvent.VK_F)
                        .setAccCtrl(true)
                        .setListener(menuListener))
                .addDescriptor(
                    new ToolBarActionDescriptor()
                        .setGroup("editMenu")
                        .setToolTipText("Find")
                        .setIcon(concept)
                        .setToggle(true)
                        .setListener(tbListener)
                        .setEnabledStateMap(imagesEnabledStateMap));
        }
    }

    protected ImageIcon i(String iconFileName) {
        return GuiUtil.getImageLocal(iconFileName);
    }

    // Generic listener for all menu items.
    protected UIActionListener menuListener = new UIActionListener() {
        public void actionPerformed(ActionEvent e, UIAction action) {
            JMenuItem mnu = (JMenuItem) e.getSource();
            System.out.println(mnu.getClass() + " " + mnu.getText());
        }
    };

    // Generic listener for all tool bar buttons.
    protected UIActionListener tbListener = new UIActionListener() {
        public void actionPerformed(ActionEvent e, UIAction action) {
            AbstractButton btn = (AbstractButton) e.getSource();
            System.out.println(btn.getClass() + " " + btn.getToolTipText());
        }
    };
}
