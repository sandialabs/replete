
package replete.ui.lay;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.Document;
import javax.swing.tree.TreeModel;

import org.javadev.AnimatingCardLayout;
import org.javadev.effects.Animation;
import org.javadev.effects.CubeAnimation;
import org.javadev.effects.DashboardAnimation;
import org.javadev.effects.FadeAnimation;
import org.javadev.effects.IrisAnimation;
import org.javadev.effects.RadialAnimation;
import org.javadev.effects.SlideAnimation;

import darrylbu.renderer.CheckBoxIcon;
import darrylbu.renderer.RadioButtonIcon;
import replete.extensions.ui.WrapLayout;
import replete.text.StringUtil;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.RSplitPane;
import replete.ui.button.IconButton;
import replete.ui.button.IconToggleButton;
import replete.ui.button.RButton;
import replete.ui.button.RCheckBox;
import replete.ui.button.RRadioButton;
import replete.ui.button.RToggleButton;
import replete.ui.button.TristateCheckBox;
import replete.ui.button.TristateCheckBox2;
import replete.ui.button.TristateValue;
import replete.ui.combo.RecentComboBox;
import replete.ui.debug.DebugWindowListener;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.label.BorderedLabel;
import replete.ui.label.GradientLabel;
import replete.ui.list.EmptyMessageList;
import replete.ui.list.RList;
import replete.ui.list.cb.RCheckBoxList;
import replete.ui.menu.RMenu;
import replete.ui.menu.RMenuItem;
import replete.ui.nofire.NoFireComboBox;
import replete.ui.panels.BorderedPanel;
import replete.ui.panels.GradientPanel;
import replete.ui.panels.RPanel;
import replete.ui.progress.RainbowProgressBar;
import replete.ui.sp.RScrollPane;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.table.RTable;
import replete.ui.table.RTablePanel;
import replete.ui.text.CopiableLabel;
import replete.ui.text.GlowingValidatingTextField;
import replete.ui.text.RLabel;
import replete.ui.text.RTextArea;
import replete.ui.text.RTextField;
import replete.ui.text.RTextPane;
import replete.ui.text.editor.REditor;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.tree.NodeBase;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.tree.state.VisualStateSavingNoFireTree;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.common.RDialog;
import replete.ui.windows.common.RFrame;
import replete.ui.windows.escape.EscapeDialog;
import replete.ui.windows.escape.EscapeFrame;
import replete.ui.windows.notifications.NotificationFrame;
import replete.util.ReflectionUtil;


/**
 * The Lay class has been designed to simplify and speed the development
 * of Java Swing UI's created by hand (in the absence of a UI builder).
 *
 * The class attempts to ameliorate one of the most tedious aspects of
 * manual Swing UI construction: layout.  The class is not complex by
 * any means and serves simply to provide a shorthand method for specifying
 * the layout of a frame, dialog, or other container.  The resulting
 * code has the goal of being much easier to read and modify than
 * standard layout code.
 *
 * Furthermore, a major theme of this class is brevity, or trying enable
 * the developer to type as few characters as possible to enact the
 * desired layout.
 *
 * Traditionally laying out some Swing container involves a set of
 * code not unlike the following:
 *
 *    JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
 *    JButton btn = ...
 *    ...
 *    pnlTop.add(btn);
 *    pnlTop.add(btn2);
 *    ...
 *    JPanel pnlCenter = new JPanel(new BorderLayout());
 *    JTextArea txt = ...
 *    JList lst = ...
 *    JPanel pnlMisc = createMiscPanel();
 *    pnlCenter.add(txt, BorderLayout.SOUTH);
 *    pnlCenter.add(lst, BorderLayout.EAST);
 *    pnlCenter.add(pnlMisc, BorderLayout.CENTER);
 *    pnlMisc.setBorder(
 * BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
 *    txt.setBorder(BorderFactory.createCompoundBorder(
 *        BorderFactory.createEmptyBorder(0, 5, 0, 5),
 *            txt.getBorder()); // Padding
 *    ...
 *    this.setLayout(new BorderLayout());
 *    add(pnlTop, BorderLayout.NORTH);
 *    add(pnlCenter, BorderLayout.CENTER);
 *    add(new StatusBar(), BorderLayout.SOUTH);
 *
 * Above we see the creation of various JPanels merely for the purpose
 * of creating a "group" of controls that all behave according to a
 * given layout.  These JPanels are organized into a compositional
 * hierarchy to construct the final composite layout.  Although the
 * final UI at runtime will be this compositional hierarchy of
 * containers, the code we had to write to create it was very linear -
 * vertical if you will.  This makes understanding the code and changing
 * the layout more onerous than it needs to be, as the developer has to
 * transform this vertical representation of the UI code into a hierarchy
 * before they can make confidently make changes to it (they literally
 * have to look at which components are being added to which other
 * components to comprehend the hierarchy).
 *
 * Moreover, you tend to see a lot of repeated code.  Constructors, add
 * methods, borders for padding, constants (e.g. BorderLayout.CENTER,
 * FlowLayout.LEFT) all distract the UI developer from his/her main goal.
 *
 * The Lay class provides two kinds of methods:
 *
 *  - Layout Methods (start with upper case letter).  These either:
 *     1) return a new JPanel with the desired layout, or
 *        >> Methods: BxL, GL, FL, BL <<
 *     2) set the desired layout onto an existing ("target") container.
 *        >> Methods: BxLtg, GLtg, FLtg, BLtg <<
 *
 *  - Border Methods (start with lower case letter).  These either:
 *     1) construct and return a Border object, or
 *        >> Methods: cb, eb, mb <<
 *     2) take a JComponent, set a border to that JComponent and
 *         return the same JComponent.
 *        >> Methods: augb, eb, mb <<
 *
 * Let's take a look at how the above code would look if using the Lay
 * class:
 *
 *    JButton btn = ...
 *    JTextArea txt = ...
 *    JList lst = ...
 *    JPanel pnlMisc = createMiscPanel();
 *    ...
 *
 *    Lay.BLtg(this,
 *        "N", Lay.FL("L", btn, btn2),
 *        "C", Lay.BL(
 *            "E", lst,
 *            "C", Lay.eb(pnlMisc, "10"),
 *            "S", Lay.augb(txt, Lay.eb("5lr"))
 *        ),
 *        "S", new StatusBar()
 *    );
 *
 * The code that performs the grouping and layout of the controls is
 * now isolated to a single multi-line statement in this case.  There
 * is no longer any mention of JPanel, LayoutManager, or Border objects.
 * This allows the developer to focus on what's most important - the
 * controls!  Buttons, lists, trees, split panes, check boxes - those
 * are what the developer cares about.  The controls are what need to
 * have listeners attached and properties set.
 *
 * Notice that using proper indentation, the compositional hierarchy
 * of your UI is extremely evident!  You can actually see at a glance
 * what contains what and immediately construct the image of what the
 * finished container will look like in your mind.
 *
 * The short method names, even if somewhat confusing the first time
 * you see them, are essential to the simplicity of the Lay class and
 * are a reaction to the traditional method of laying out UI's requiring
 * so many characters to do relatively simple things.  Example:
 *    BorderFactory.createEmptyBorder(10, 10, 10, 10);
 * is represented in the Lay class by the statement:
 *    Lay.eb("10");
 *
 * As you can see the brevity comes not only from the short method names
 * but also from encoding as much of the layout information into string
 * literals as possible.  For example, "S" stands for BorderLayout.SOUTH
 * in Lay.BL and "10" signifies a 10-pixel-width border for along all four
 * sides, top, left, bottom, and right in Lay.eb and Lay.mb.
 *
 * Border Side Thickness Codes
 * ===========================
 * To speed the creation of borders, string codes to encode the specification
 * of the thicknesses of the four sides are used in Lay.eb and Lay.mb.
 * The code is in the following format:
 *     (Ns*)+   or regex:   ([0-9]+[tlbr]*)+
 * Where N is an integer, s is a side indicator: t, l, b, or r. Valid
 * examples of a thickness code are:
 *    "10"               t=l=b=r=10
 *    "10t"              t=10, l=b=r=0
 *    "7bt6rl"           t=b=7, l=r=6
 *    "5t10r"            t=5, r=10, l=b=0
 *    "5t4l3b2r"         t=5, l=4, b=3, r=2
 *
 * Layout UI Hints
 * ===============
 * In some situations you may still need to get a reference to one of the
 * generated JPanel objects to set some property:
 *
 *    JPanel pnlTop = Lay.FL("L", btn, btn2);
 *    pnlTop.setBackground(new Color(77, 88, 99));
 *    pnlTop.setOpaque(true);
 *    Lay.BLtg(this,
 *        "N", pnlTop,
 *        "C", Lay.BL(
 *            "E", lst,
 *            "C", Lay.eb(pnlMisc, "10"),
 *            "S", Lay.augb(txt, Lay.eb("5lr"))
 *        ),
 *        "S", new StatusBar()
 *    );
 *
 * The above code is still a great improvement over the traditional code.
 * However, to help minimize the amount of this code, common properties
 * can be set on containers using UI "hints" provided in the argument
 * list of the layout methods.  Here is the same code above using a hint:
 *
 *    Lay.BLtg(this,
 *        "N", Lay.FL("L", btn, btn2, "bg=[77,88,99],opaque=true"),
 *        "C", Lay.BL(
 *            "E", lst,
 *            "C", Lay.eb(pnlMisc, "10"),
 *            "S", Lay.augb(txt, Lay.eb("5lr"))
 *        ),
 *        "S", new StatusBar()
 *    );
 *
 * Hints are available on any of the layout methods.  Supported keys and
 * values and what methods are invoked on the container are:
 *
 *       visible=true|false     # Container.setVisible
 *       opaque=true|false      # JComponent.setOpaque
 *       bg=[R,B,G]|name        # setBackground
 *       pref=[W,H]             # setPreferredSize
 *       min=[W,H]              # setMinimumSize
 *       max=[W,H]              # setMaximumSize
 *       alignx=floatval        # setAlignmentX (often used in BoxLayouts)
 *       aligny=floatval        # setAlignmentY (often used in BoxLayouts)
 *       divpixel=intval        # JSplitPane.setDividerLocation
 *       divratio=floatval      # JSplitPane.setDividerLocation
 *       resizew=floatval       # JSplitPane.setResizeWeight
 *       enabled=true|false     # setEnabled
 *       size=[W,H]             # Window.setSize
 *       center                 # Window.setLocationRelativeTo(getParent())
 *       resizable=true|false   # Window.setResizable
 *
 * The hints can be in a single, comma-delimited string argument or multiple
 * string arguments:
 *         "bg=[155,229,100]", "size=[300,400]"
 *    -or- "bg=[155,229,100],size=[300,400]"
 *
 * Method Summary
 * ==============
 *
 * BxL(Object... cmps)
 *  - Create a JPanel with a BoxLayout (X_AXIS), add any Components
 *    in the argument list to the JPanel, and return the JPanel.
 *
 * BxLtg(Container target, Object... cmps)
 *  - Set a new BoxLayout (X_AXIS) to the container provided, add any
 *    Components in the argument list to the container, and return null.
 *
 * --> All BoxLayout Methods (BxL*):
 *    If a String is found in the argument list, and found to be
 *    any of these values (case insensitive):
 *      "X", "X_AXIS", "H", "HORIZ", "Y", "Y_AXIS", "V", "VERT"
 *    it overrides the orientation of the BoxLayout.
 *    If an Integer is found in the argument list, it overrides
 *    the orientation of the BoxLayout (e.g. BoxLayout.X_AXIS).
 *
 * GL(int rows, int cols, Object... cmps)
 *  - Create a JPanel with a GridLayout (using the dimensions provided),
 *    add any Components in the argument list to the JPanel, and
 *    return the JPanel (default gaps equal to 0).
 *
 * GLtg(Container target, int rows, int cols, Object... cmps)
 *  - Set a new GridLayout (using the axis provided) to the container
 *    provided, add any Components in the argument list to the container,
 *    and return null (default gaps equal to 0).
 *
 * FL(Object... cmps)
 *  - Create a JPanel with a FlowLayout (CENTER), add any Components
 *    in the argument list to the JPanel, and return the JPanel
 *    (default gaps equal to 5).
 *
 * FLtg(Container target, Object... cmps)
 *  -
 *
 * --> All FlowLayout Methods (FL*):
 *    If a String is found in the argument list, and found to be
 *    any of these values (case insensitive):
 *      "L", "LEFT", "R", "RIGHT", "C", "CENTER"
 *    it overrides the orientation of the FlowLayout.
 *    If an Integer is found in the argument list, it overrides
 *    the orientation of the FlowLayout (e.g. FlowLayout.LEFT).
 *
 * @author Derek Trumbo
 */

public class Lay {



    // =================================================== //
    //                       LAYOUTS                       //
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv //



    /////////////////////
    // ABSOLUTE LAYOUT //
    /////////////////////

    public static <T extends JPanel> T AL(Object... args) {
        return ALtg((Container) null, args);
    }

    public static <T extends JPanel> T ALtg(Container target, Object... args) {
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                String str = (String) arg;
                hints.addHints(LayHints.parseHints(str));

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        // Set layout on container, add components, and set hints on container.
        Container cont = chooseContainer(target, hints);
        cont.setLayout(null);
        for(Component c : cmpsChosen) {
            cont.add(c);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    ///////////////
    // BOXLAYOUT //
    ///////////////

    // Shorthand Literals
    private static Map<String, Integer> blAxis = new HashMap<>();
    static {
        blAxis.put("X",          BoxLayout.X_AXIS);
        blAxis.put("X_AXIS",     BoxLayout.X_AXIS);
        blAxis.put("H",          BoxLayout.X_AXIS);
        blAxis.put("HORIZ",      BoxLayout.X_AXIS);
        blAxis.put("HORIZONTAL", BoxLayout.X_AXIS);
        blAxis.put("Y",          BoxLayout.Y_AXIS);
        blAxis.put("Y_AXIS",     BoxLayout.Y_AXIS);
        blAxis.put("V",          BoxLayout.Y_AXIS);
        blAxis.put("VERT",       BoxLayout.Y_AXIS);
        blAxis.put("VERTICAL",   BoxLayout.Y_AXIS);
    }

    // Layout Methods
    public static <T extends JPanel> T BxL(Object... args) {
        /*
         * Create a JPanel with a BoxLayout (Y_AXIS), add any Components
         * in the argument list to the JPanel, and return the JPanel.
         */
        return BxLtg((Container) null, args);
    }

    public static <T extends JPanel> T BxLtg(Container target, Object... args) {
        /*
         * BxLtg(Container target, Object... cmps)
         *  - Set a new BoxLayout (Y_AXIS) to the container provided, add any
         *    Components in the argument list to the container, and return null.
         */
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();
        int axisChosen = BoxLayout.Y_AXIS;  // More common of a default

        for(Object arg : args) {

            // Save hints or axis
            if(arg instanceof String) {
                String str = (String) arg;

                // String axis override
                if(blAxis.get(str.toUpperCase()) != null) {
                    axisChosen = blAxis.get(str.toUpperCase());

                // Save hints
                } else {
                    hints.addHints(LayHints.parseHints(str));
                }

            // Integer axis override
            } else if(arg instanceof Integer) {
                axisChosen = (Integer) arg;

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        // Set layout on container, add components, and set hints on container.
        Container cont = chooseContainer(target, hints);
        BoxLayout bl = new BoxLayout(cont, axisChosen);
        cont.setLayout(bl);
        for(Component c : cmpsChosen) {
            cont.add(c);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    ////////////////
    // GRIDLAYOUT //
    ////////////////

    public static <T extends JPanel> T GL(int rows, int cols, Object... args) {
        /*
         *GL(int rows, int cols, Object... cmps)
         *  - Create a JPanel with a GridLayout (using the dimensions provided),
         *    add any Components in the argument list to the JPanel, and
         *    return the JPanel (default gaps equal to 0).
         */
        return GLtg((Container) null, rows, cols, args);
    }

    public static <T extends JPanel> T GLtg(Container target, int rows, int cols, Object... args) {
        /* GLtg(Container target, int rows, int cols, Object... cmps)
         *  - Set a new GridLayout (using the axis provided) to the container
         *    provided, add any Components in the argument list to the container,
         *    and return null (default gaps equal to 0).
        */
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

                // Component lists
                } else if(arg instanceof List) {

                    for(Object elem : (List) arg) {

                        // Save components
                        if(elem instanceof Component) {
                            cmpsChosen.add((Component) elem);

                        // Save event listeners
                        } else if(elem instanceof EventListener) {
                            listeners.add((EventListener) elem);
                        }

                    }

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        Container cont = chooseContainer(target, hints);
        GridLayout gl = new GridLayout(rows, cols, 0, 0);
        cont.setLayout(gl);
        for(Component c : cmpsChosen) {
            cont.add(c);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    ////////////////
    // FLOWLAYOUT //
    ////////////////

    // Shorthand Literals
    private static Map<String, Integer> flAlign = new HashMap<>();
    static {
        flAlign.put("L",      FlowLayout.LEFT);
        flAlign.put("LEFT",   FlowLayout.LEFT);
        flAlign.put("R",      FlowLayout.RIGHT);
        flAlign.put("RIGHT",  FlowLayout.RIGHT);
        flAlign.put("C",      FlowLayout.CENTER);
        flAlign.put("CENTER", FlowLayout.CENTER);
    }

    // Layout Methods
    public static <T extends JPanel> T FL(Object... args) {
        /*
        *  FL(Object... cmps)
        *  - Create a JPanel with a FlowLayout (CENTER), add any Components
        *   in the argument list to the JPanel, and return the JPanel
        *    (default gaps equal to 5).
        */
        return FLtg((Container) null, args);
    }

    public static <T extends JPanel> T FLtg(Container target, Object... args) {
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();
        FlowLayout fl = new FlowLayout(FlowLayout.CENTER, 5, 5);

        for(Object arg : args) {

            // Save hints or alignment
            if(arg instanceof String) {
                String str = (String) arg;

                // String axis override
                if(flAlign.get(str.toUpperCase()) != null) {
                    fl.setAlignment(flAlign.get(str.toUpperCase()));

                // Save hints
                } else {
                    hints.addHints(LayHints.parseHints(str));
                }

            // Integer axis override
            } else if(arg instanceof Integer) {
                fl.setAlignment((Integer) arg);

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        // Set layout on container, add components, and set hints on container.
        Container cont = chooseContainer(target, hints);
        cont.setLayout(fl);
        for(Component c : cmpsChosen) {
            cont.add(c);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    ////////////////
    // WRAPLAYOUT //
    ////////////////

    // Layout Methods
    public static <T extends JPanel> T WL(Object... args) {
        return WLtg((Container) null, args);
    }

    public static <T extends JPanel> T WLtg(Container target, Object... args) {
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();
        WrapLayout fl = new WrapLayout(FlowLayout.CENTER, 5, 5);

        for(Object arg : args) {

            // Save hints or alignment
            if(arg instanceof String) {
                String str = (String) arg;

                // String axis override
                if(flAlign.get(str.toUpperCase()) != null) {
                    fl.setAlignment(flAlign.get(str.toUpperCase()));

                // Save hints
                } else {
                    hints.addHints(LayHints.parseHints(str));
                }

            // Integer axis override
            } else if(arg instanceof Integer) {
                fl.setAlignment((Integer) arg);

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        // Set layout on container, add components, and set hints on container.
        Container cont = chooseContainer(target, hints);
        cont.setLayout(fl);
        for(Component c : cmpsChosen) {
            cont.add(c);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    //////////////////
    // BORDERLAYOUT //
    //////////////////

    // Shorthand Literals
    private static Map<String, String> blDirs = new HashMap<>();
    static {
        blDirs.put("N",      BorderLayout.NORTH);
        blDirs.put("NORTH",  BorderLayout.NORTH);
        blDirs.put("E",      BorderLayout.EAST);
        blDirs.put("EAST",   BorderLayout.EAST);
        blDirs.put("S",      BorderLayout.SOUTH);
        blDirs.put("SOUTH",  BorderLayout.SOUTH);
        blDirs.put("W",      BorderLayout.WEST);
        blDirs.put("WEST",   BorderLayout.WEST);
        blDirs.put("C",      BorderLayout.CENTER);
        blDirs.put("CENTER", BorderLayout.CENTER);
    }

    // Layout Methods
    public static <T extends JPanel> T BL(Object... args) {
        return BLtg((Container) null, args);
    }

    public static <T extends JPanel> T BLtg(Container target, Object... args) {
        HintList hints = new HintList();
        Map<Component, String> cmpsChosen = new LinkedHashMap<>();
        List<EventListener> listeners = new ArrayList<>();

        for(int a = 0; a < args.length; a++) {
            Object arg = args[a];

            // Save components with their associated positions
            if(arg instanceof Component) {
                Component cmp = (Component) arg;
                String area = BorderLayout.CENTER;

                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof String) {
                        String str = (String) obj2;
                        if(blDirs.get(str.toUpperCase()) != null) {
                            area = blDirs.get(str.toUpperCase());
                            a++;
                        }
                    }
                }

                cmpsChosen.put(cmp, area);

            // Save components with their associated positions, or hints
            } else if(arg instanceof String) {
                String str = (String) arg;
                if(blDirs.get(str.toUpperCase()) != null) {
                    String area = blDirs.get(str.toUpperCase());
                    if(a < args.length - 1) {
                        Object obj2 = args[a + 1];
                        if(obj2 instanceof Component) {
                            Component cmp = (Component) obj2;
                            cmpsChosen.put(cmp, area);
                            a++;
                        }
                    }

                // Save hints
                } else {
                    hints.addHints(LayHints.parseHints(str));
                }

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            } else if(arg instanceof ImageModelConcept) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        Container cont = chooseContainer(target, hints);
        BorderLayout bl = new BorderLayout();
        if(cont.getLayout() == null || !cont.getLayout().getClass().equals(bl.getClass())) {
            cont.setLayout(bl);
        }
        for(Component cmp : cmpsChosen.keySet()) {
            cont.add(cmp, cmpsChosen.get(cmp));
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    ///////////////////
    // GRIDBAGLAYOUT //
    ///////////////////

    // Layout Methods
    public static <T extends JPanel> T GBL(Object... args) {
        return GBLtg((Container) null, args);
    }

    public static <T extends JPanel> T GBLtg(Container target, Object... args) {
        HintList hints = new HintList();
        Map<Component, GridBagConstraints> cmpsChosen = new LinkedHashMap<>();
        List<EventListener> listeners = new ArrayList<>();

        for(int a = 0; a < args.length; a++) {
            Object arg = args[a];

            // Save components with their associated constraints
            if(arg instanceof Component) {
                Component cmp = (Component) arg;
                GridBagConstraints cnstr = GBC.c();

                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof GridBagConstraints) {
                        cnstr = (GridBagConstraints) obj2;
                        a++;
                    }
                }

                cmpsChosen.put(cmp, cnstr);

            // Save components with their associated constraints
            } else if(arg instanceof GridBagConstraints) {
                GridBagConstraints cnstr = (GridBagConstraints) arg;
                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof Component) {
                        Component cmp = (Component) obj2;
                        cmpsChosen.put(cmp, cnstr);
                        a++;
                    }
                }
            }

            // Save hints
            else if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        Container cont = chooseContainer(target, hints);
        GridBagLayout gl = new GridBagLayout();
        cont.setLayout(gl);
        for(Component c : cmpsChosen.keySet()) {
            cont.add(c, cmpsChosen.get(c));
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }

    //////////////////
    // GROUP LAYOUT //
    //////////////////
    /*
     *  Example Usage of Group Layout with Lay:
     *
     *  Lay.BLtg(this,
     *      "C", Lay.GPL(
     *          "H", Lay.SG(
     *              btn1,
     *              btn2,
     *              Lay.PG(
     *                  btn3,
     *                  btn4,
     *                  "leading"
     *              )
     *          ),
     *          "V", Lay.SG(
     *              Lay.PG(
     *                  btn1,
     *                  btn2,
     *                  btn3,
     *                  "baseline"
     *              ),
     *              btn4
     *          ),
     *          "agaps=true, acgaps=true"
     *      ),
     *      "size=600,center"
     *  );
     */

    private static List<String> glDims = new ArrayList<>();
    static {
        glDims.add("H");
        glDims.add("V");
    }

    // Layout Methods
    public static <T extends JPanel> T GPL(Object... args) {
        return GPLtg((Container) null, args);
    }

    private static <T extends JPanel> T GPLtg(Container target, Object... args) {
        HintList hints = new HintList();
        List<GPLGp> groups = new ArrayList<>();
        for(int a = 0; a < args.length; a++) {
            Object arg = args[a];
            GPLGp group = new GPLGp();

            if(arg instanceof String) {
                String str = (String)arg;
                if(glDims.contains(str)){
                    group.dim = str;
                    Object object2 = args[a + 1];
                    if (object2 instanceof Object[]) {
                        group.components = (Object[])object2;
                    } else {
                        continue;
                    }
                } else {
                   hints.addHints(LayHints.parseHints(str));
                }
            } else if(arg instanceof Object[]) {
                group.components = (Object[])arg;
                String str = (String)args[a + 1];
                if(glDims.contains(str)) {
                    group.dim = str;
                } else {
                   continue;
                }
            }

            if(group.dim != null && group.components != null) {
                groups.add(group);
                a += 1;
            }
        }

        Container cont = chooseContainer(target, hints);
        GroupLayout gl = new GroupLayout(cont);

        for(GPLGp group : groups) {
            Group actualGroup = null;
            if(group.components[group.components.length-1].equals("SG")) {
                actualGroup = SG(gl, group.components);
            } else if(group.components[group.components.length-1].equals("PG")) {
                actualGroup = PG(gl, group.components);
            }

            if(group.dim.equals("H")) {
                gl.setHorizontalGroup(actualGroup);
            } else if(group.dim.equals("V")) {
                gl.setVerticalGroup(actualGroup);
            }
        }

        cont.setLayout(gl);
        return resolvePanel(target, cont, hints);
    }

    public static Object[] SG(Object... args) {
        Object[] seqArgList = new Object[args.length + 1];
        for(int i = 0; i < args.length; i++) {
            seqArgList[i] = args[i];
        }
        seqArgList[args.length] = "SG";
        return seqArgList;
    }

    private static SequentialGroup SG(GroupLayout gl, Object... args) {
        SequentialGroup sg = gl.createSequentialGroup();
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof Component){
                sg.addComponent((Component)arg);
            } else if (arg instanceof Group) {
                sg.addGroup((Group) arg);
            } else if (arg instanceof Object[]){
                Object[] argArr = (Object[]) arg;
                if(argArr[argArr.length-1].equals("SG")) {
                    sg.addGroup(SG(gl, argArr));
                } else if(argArr[argArr.length-1].equals("PG")) {
                    sg.addGroup(PG(gl, argArr));
                }
            } else if(arg instanceof String) {
                String str = (String) arg;

                hints.addHints(LayHints.parseHints(str));
            }
        }
        return sg;
    }

    public static Object[] PG(Object... args) {
        Object[] parArgList = new Object[args.length + 1];
        for(int i = 0; i < args.length; i++) {
            parArgList[i] = args[i];
        }
        parArgList[args.length] = "PG";
        return parArgList;
    }

    private static ParallelGroup PG(GroupLayout gl, Object... args) {
        GroupLayout.Alignment alignment = GroupLayout.Alignment.LEADING;
        for(Object arg : args) {
            if(arg instanceof String) {
                String str = (String) arg;
                if(str.equals("baseline")) {
                    alignment = GroupLayout.Alignment.BASELINE;
                } else if(str.equals("trailing")) {
                    alignment = GroupLayout.Alignment.TRAILING;
                } else if(str.equals("center")) {
                    alignment = GroupLayout.Alignment.CENTER;
                }
            }
        }

        ParallelGroup pg = gl.createParallelGroup(alignment);
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof Component){
                pg.addComponent((Component)arg);
            } else if (arg instanceof Group) {
                pg.addGroup((Group) arg);
            } else if (arg instanceof Object[]){
                Object[] argArr = (Object[]) arg;
                if(argArr[argArr.length-1].equals("SG")) {
                    pg.addGroup(SG(gl, argArr));
                } else if(argArr[argArr.length-1].equals("PG")) {
                    pg.addGroup(PG(gl, argArr));
                }
            } else if(arg instanceof String) {
                String str = (String) arg;
                hints.addHints(LayHints.parseHints(str));
            }
        }
        return pg;
    }

    /////////////////
    // CARD LAYOUT //
    /////////////////

    public static <T extends JPanel> T CL(Object... args) {
        return CLtg((Container) null, args);
    }
    public static <T extends JPanel> T CLtg(Container target, Object... args) {
        HintList hints = new HintList();
        List<Object[]> pairs = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(int a = 0; a < args.length; a++) {
            Object arg = args[a];

            // Save components with their associated positions
            if(arg instanceof Component) {
                Component cmp = (Component) arg;
                String title = pairs.size() + "";
                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof String) {
                        title = (String) obj2;
                        a++;
                    }
                }
                pairs.add(new Object[] {title, cmp});

            // Save components with their associated positions, or hints
            } else if(arg instanceof String) {
                String title = (String) arg;
                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof Component) {
                        Component cmp = (Component) obj2;
                        pairs.add(new Object[] {title, cmp});
                        a++;
                    } else {
                        hints.addHints(LayHints.parseHints(title));
                    }
                } else {
                    hints.addHints(LayHints.parseHints(title));
                }

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            // Set frame/dialog icon if target is one
            } else if(arg instanceof ImageIcon) {
                checkSetIcon(target, arg);
            }

            // Else ignore
        }

        Container cont = chooseContainer(target, hints);
        CardLayout cl;
        if(hints.contains("animated")) {
            String type = hints.get("animated");
            Animation anim = null;
            if(type.equalsIgnoreCase("fade")) {
                anim = new FadeAnimation();
            } else if(type.equalsIgnoreCase("slide")) {
                anim = new SlideAnimation();
            } else if(type.equalsIgnoreCase("cube")) {
                anim = new CubeAnimation();
            } else if(type.equalsIgnoreCase("iris")) {
                anim = new IrisAnimation();
            } else if(type.equalsIgnoreCase("radial")) {
                anim = new RadialAnimation();
            } else if(type.equalsIgnoreCase("dashboard")) {
                anim = new DashboardAnimation();
            }
            cl = new AnimatingCardLayout();
            if(anim != null) {
                ((AnimatingCardLayout) cl).setAnimation(anim);
            }
        } else {
            cl = new CardLayout();
        }
        cont.setLayout(cl);
        for(Object[] pair : pairs) {
            cont.add((Component) pair[1], (String) pair[0]);
        }

        addListeners(cont, listeners);

        return resolvePanel(target, cont, hints);
    }


    //////////////////
    // JLayeredPane //
    //////////////////

    public static JLayeredPane lay(Object... args) {
        return laytg((Container) null, args);
    }

    public static JLayeredPane laytg(Container target, Object... args) {
        if(target != null && !(target instanceof JLayeredPane)) {
            return null;
        }
        HintList hints = new HintList();
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                String str = (String) arg;
                hints.addHints(LayHints.parseHints(str));

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }

            // Else ignore
        }

        JLayeredPane pnlLayered = (target == null) ? new JLayeredPane() : (JLayeredPane) target;
        int layer = 0;
        for(Component c : cmpsChosen) {
            pnlLayered.add(c, layer++, 0);
        }

        addListeners(pnlLayered, listeners);

        return applyHints(pnlLayered, hints);
    }



    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
    //                       LAYOUTS                       //
    // =================================================== //



    ////////////////
    // SPLIT PANE //
    ////////////////

    // Shorthand Literals
    private static Map<String, Integer> splOrien = new HashMap<>();
    static {
        splOrien.put("X",      JSplitPane.HORIZONTAL_SPLIT);
        splOrien.put("X_AXIS", JSplitPane.HORIZONTAL_SPLIT);
        splOrien.put("H",      JSplitPane.HORIZONTAL_SPLIT);
        splOrien.put("HORIZ",  JSplitPane.HORIZONTAL_SPLIT);
        splOrien.put("Y",      JSplitPane.VERTICAL_SPLIT);
        splOrien.put("Y_AXIS", JSplitPane.VERTICAL_SPLIT);
        splOrien.put("V",      JSplitPane.VERTICAL_SPLIT);
        splOrien.put("VERT",   JSplitPane.VERTICAL_SPLIT);
    }

    // Layout Methods
    public static RSplitPane SPL(Object... args) {
        return SPL(JSplitPane.HORIZONTAL_SPLIT, args);
    }

    public static RSplitPane SPL(int orien, Object... args) {
        HintList hints = new HintList();
        int orienChosen = orien;
        List<Component> cmpsChosen = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints or orientation
            if(arg instanceof String) {
                String str = (String) arg;

                // String orientation override
                if(splOrien.get(str.toUpperCase()) != null) {
                    orienChosen = splOrien.get(str.toUpperCase());

                    // Add any hints
                } else {
                    hints.addHints(LayHints.parseHints(str));
                }

            // Integer orientation override
            } else if(arg instanceof Integer) {
                orienChosen = (Integer) arg;

            // Save components
            } else if(arg instanceof Component) {
                cmpsChosen.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }

            // Else ignore
        }

        // Add components and set hints on container.
        Component c1 = cmpsChosen.size() > 0 ? cmpsChosen.get(0) : null;
        Component c2 = cmpsChosen.size() > 1 ? cmpsChosen.get(1) : null;
        RSplitPane spl = new RSplitPane(orienChosen, c1, c2);

        addListeners(spl, listeners);

        return applyHints(spl, hints);
    }


    //////////////////
    // DESKTOP PANE //
    //////////////////

    // Layout Methods
    public static JDesktopPane DP(Object... args) {
        HintList hints = new HintList();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                String str = (String) arg;
                hints.addHints(LayHints.parseHints(str));
            }

        }

        JDesktopPane pane = new JDesktopPane();

        return applyHints(pane, hints);
    }



    // =================================================== //
    //                       BORDERS                       //
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv //



    /////////////////////////////
    // AUGMENT/COMPOUND BORDER //
    /////////////////////////////

    public static Border brd(String code) {
        if(code.startsWith("eb=")) {
            return eb(code.substring(3));
         } else if(code.startsWith("mb=")) {
             String[] parts = StringUtil.extractCaptures(code, "mb=\\[([^,\\]]+)(?:,([^\\]]+))?\\]");
             return mb(parts[0], Lay.clr(parts[1]));
        }
        return null;
    }

    public static JComponent augb(JComponent c, String code) {
        return augb(c, brd(code));
    }

    public static JComponent augb(JComponent c, Border outer) {
        c.setBorder(BorderFactory.createCompoundBorder(outer, c.getBorder()));
        return c;
    }

    public static Border cb(Border outer, Border inner) {
        return BorderFactory.createCompoundBorder(outer, inner);
    }


    //////////////////
    // EMPTY BORDER //
    //////////////////

    public static Border eb() {
        return BorderFactory.createEmptyBorder();
    }

    public static Border eb(String code) {
        Sides sides = new Sides(code);
        Border border = BorderFactory.createEmptyBorder(
            sides.top, sides.left, sides.bottom, sides.right);
        return border;
    }

    public static JComponent eb(JComponent cmp, String code, Object... args) {
        Sides sides = new Sides(code);
        Border border = BorderFactory.createEmptyBorder(
            sides.top, sides.left, sides.bottom, sides.right);
        cmp.setBorder(border);
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(cmp, hints);
    }


    //////////////////
    // MATTE BORDER //
    //////////////////

    public static Border mb(String code, Color clr) {
        Sides sides = new Sides(code);
        Border border = BorderFactory.createMatteBorder(
            sides.top, sides.left, sides.bottom, sides.right, clr);
        return border;
    }

    public static JComponent mb(JComponent cmp, String code, Color clr, Object... args) {
        Sides sides = new Sides(code);
        Border border = BorderFactory.createMatteBorder(
            sides.top, sides.left, sides.bottom, sides.right, clr);
        cmp.setBorder(border);
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(cmp, hints);
    }



    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
    //                       BORDERS                       //
    // =================================================== //



    // =================================================== //
    //                      CONTROLS                       //
    // vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv //



    ///////////
    // FRAME //
    ///////////

    public static EscapeFrame fr() {
        return fr("Frame");
    }
    public static EscapeFrame fr(String title, Object... args) {
        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();
        LayoutManager mgr = null;
        Component cmp = null;
        ImageIcon icon = null;
        ImageModelConcept concept = null;

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save layout manager
            } else if(arg instanceof LayoutManager) {
                mgr = (LayoutManager) arg;

            } else if(arg instanceof Component) {
                cmp = (Component) arg;

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            } else if(arg instanceof ImageIcon) {
                icon = (ImageIcon) arg;

            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        EscapeFrame target;
        if(hints.contains("notif") || hints.contains("notification")) {
            target = new NotificationFrame(title);
        } else {
            target = new EscapeFrame(title);
        }

        Container cont = chooseContainer(target, hints);
        cont.setLayout(mgr == null ? new BorderLayout() : mgr);
        if(cmp != null) {
            target.add(cmp, BorderLayout.CENTER);
        }
        if(icon != null) {
            target.setIconImage(icon.getImage());
        }
        if(concept != null) {
            target.setIcon(concept);
        }
        addListeners(cont, listeners);
        resolvePanel(target, cont, hints);
        return target;
    }


    ////////////
    // DIALOG //
    ////////////

    public static EscapeDialog dlg(Component c, String title, Object... args) {
        Window win = GuiUtil.win(c);
        EscapeDialog target = new EscapeDialog(win, title, true);

        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();
        LayoutManager mgr = null;
        Component cmp = null;
        ImageIcon icon = null;
        ImageModelConcept concept = null;

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save layout manager
            } else if(arg instanceof LayoutManager) {
                mgr = (LayoutManager) arg;

            } else if(arg instanceof Component) {
                cmp = (Component) arg;

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            } else if(arg instanceof ImageIcon) {
                icon = (ImageIcon) arg;

            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        Container cont = chooseContainer(target, hints);
        cont.setLayout(mgr == null ? new BorderLayout() : mgr);
        if(cmp != null) {
            target.add(cmp, BorderLayout.CENTER);
        }
        if(icon != null) {
            target.setIconImage(icon.getImage());
        }
        if(concept != null) {
            target.setIcon(concept);
        }
        addListeners(cont, listeners);
        resolvePanel(target, cont, hints);
        return target;
    }


    /////////////////
    // BASIC PANEL //
    /////////////////

    public static RPanel p(Object... args) {
        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();
        LayoutManager mgr = null;

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save layout manager
            } else if(arg instanceof LayoutManager) {
                mgr = (LayoutManager) arg;

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }
        }

        Container cont = chooseContainer(null, hints);
        cont.setLayout(mgr == null ? new BorderLayout() : mgr);

        addListeners(cont, listeners);

        return (RPanel) applyHints(cont, hints);
    }
    public static RPanel p(JComponent c, Object... args) {
        // return BL("C", c, args); Would have been nice but args gets converted into Object[1]
        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }
        }

        Container cont = chooseContainer(null, hints);
        BLtg(cont, "C", c);

        addListeners(cont, listeners);

        return (RPanel) applyHints(cont, hints);
    }


    ////////////////////////////////////////////
    // EDITOR (SCROLL PANE / TEXT PANE COMBO) //
    ////////////////////////////////////////////

    public static REditor ed() {
        return ed("");
    }
    public static REditor ed(String content, Object... args) {
        HintList hints = new HintList();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        RTextPane txt = txp(content, args);
        txt.setAllowHorizScroll(true);
        RScrollPane jsp = Lay.sp(txt, hints, "vsb=always");
        REditor pnlEditor = new REditor(txt, jsp);
        Lay.hn(pnlEditor, args);
        return pnlEditor;
    }


    /////////////////
    // SCROLL PANE //
    /////////////////

    // More possibilities here.
    public static RScrollPane sp(Component cmp) {
        return sp(cmp, new Object[0]);
    }

    public static RScrollPane sp(Component cmp, Object... args) {
        RScrollPane scr = new RScrollPane(cmp);
        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Hint List
            } else if(arg instanceof HintList) {
                HintList otherHints = (HintList) arg;
                hints.addHints(otherHints);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }
        }

        addListeners(scr, listeners);

        return applyHints(scr, hints);
    }


    //////////////////
    // PROGRESS BAR //
    //////////////////

    public static JProgressBar pgb() {
        /*
         * Create a standard JProgressBar.
         */
        return new JProgressBar();
    }

    public static JProgressBar pgb(Object... args) {
        HintList hints = new HintList();
        List<EventListener> listeners = new ArrayList<>();
        boolean indeterminate = false;

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Whether or not indeterminate
            } else if(arg instanceof Boolean) {
                indeterminate = (Boolean) arg;

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }
        }

        JProgressBar pgb;
        if(hints.contains("rainbow")) {
            pgb = new RainbowProgressBar();
        } else {
            pgb = new JProgressBar();
        }
        pgb.setIndeterminate(indeterminate);

        addListeners(pgb, listeners);

        return applyHints(pgb, hints);
    }


    ///////////////
    // CHECK BOX //
    ///////////////

    public static RCheckBox chk() {
        return chk(null);
    }
    public static RCheckBox chk(Object text, Object... args) {
        HintList hints = new HintList();
        boolean selected = false;
        Icon icon = null;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Boolean) {
                selected = (Boolean) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }


        String resolvedText = null;
        Icon resolvedIcon = null;

        if(text instanceof Icon) {
            resolvedIcon = (Icon) text;
        } else if(text instanceof ImageModelConcept) {
            resolvedIcon = ImageLib.get((ImageModelConcept) text);
        } else {
            resolvedText = text == null ? null : text.toString();
            if(icon != null) {
                resolvedIcon = icon;
            } else if(concept != null) {
                resolvedIcon = ImageLib.get(concept);
            }
        }

        RCheckBox chk;
        if(hints.contains("tristate")) {
            chk = new TristateCheckBox(resolvedText, selected);
        } else {
            chk = new RCheckBox(resolvedText, selected);
        }
        if(resolvedIcon != null) {
            chk.setIcon(new CheckBoxIcon(chk, resolvedIcon));
        }

        addListeners(chk, listeners);

        return applyHints(chk, hints);
    }

    public static TristateCheckBox2 chk3() {
        return chk3(null);
    }
    public static TristateCheckBox2 chk3(Object text, Object... args) {
        HintList hints = new HintList();
        TristateValue value = TristateValue.UNSELECTED;
        Icon userIcon = null;
        ImageModelConcept userConcept = null;
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                userIcon = (Icon) arg;
            } else if(arg instanceof TristateValue) {
                value = (TristateValue) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                userConcept = (ImageModelConcept) arg;
            }
        }


        String resolvedText = null;
        Icon resolvedIcon = null;

        if(text instanceof Icon) {
            resolvedIcon = (Icon) text;
        } else if(text instanceof ImageModelConcept) {
            resolvedIcon = ImageLib.get((ImageModelConcept) text);
        } else {
            resolvedText = text == null ? null : text.toString();
            if(userIcon != null) {
                resolvedIcon = userIcon;
            } else if(userConcept != null) {
                resolvedIcon = ImageLib.get(userConcept);
            }
        }

        TristateCheckBox2 chk = new TristateCheckBox2(resolvedText, value);
        if(resolvedIcon != null) {
            chk.setUserIcon(resolvedIcon);
        }

        addListeners(chk, listeners);

        return applyHints(chk, hints);
    }


    //////////////////
    // RADIO BUTTON //
    //////////////////

    public static RRadioButton opt() {
        return opt(null);
    }
    public static RRadioButton opt(Object text, Object... args) {
        HintList hints = new HintList();
        boolean selected = false;
        Icon icon = null;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Boolean) {
                selected = (Boolean) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        String resolvedText = null;
        Icon resolvedIcon = null;

        if(text instanceof Icon) {
            resolvedIcon = (Icon) text;
        } else if(text instanceof ImageModelConcept) {
            resolvedIcon = ImageLib.get((ImageModelConcept) text);
        } else {
            resolvedText = text == null ? null : text.toString();
            if(icon != null) {
                resolvedIcon = icon;
            } else if(concept != null) {
                resolvedIcon = ImageLib.get(concept);
            }
        }

        RRadioButton opt = new RRadioButton(resolvedText, selected);
        if(resolvedIcon != null) {
            opt.setIcon(new RadioButtonIcon(opt, resolvedIcon));
        }

        addListeners(opt, listeners);

        return applyHints(opt, hints);
    }


    ///////////////////
    // TOGGLE BUTTON //
    ///////////////////

    public static JToggleButton tgl() {
        return tgl("");
    }
    public static JToggleButton tgl(Object text, Object... args) {
        HintList hints = new HintList();
        boolean selected = false;
        Icon icon = null;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();
        int insets = -1;

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Boolean) {
                selected = (Boolean) arg;
            } else if(arg instanceof Integer) {
                insets = (Integer) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        // TODO: insets??
        JToggleButton opt;
        if(text instanceof Icon) {
            opt = new JToggleButton((Icon) text, selected);
        } else if(text instanceof ImageModelConcept) {
            opt = new JToggleButton(ImageLib.get((ImageModelConcept) text), selected);
        } else if(icon != null) {
            opt = new JToggleButton(text == null ? null : text.toString(), icon, selected);
        } else if(concept != null) {
            opt = new JToggleButton(text == null ? null : text.toString(), ImageLib.get(concept), selected);
        } else {
            opt = new JToggleButton(text == null ? null : text.toString(), selected);
        }

        addListeners(opt, listeners);

        return applyHints(opt, hints);
    }


    ////////////
    // BUTTON //
    ////////////

    public static RButton btn() {
        return Lay.btn("");
    }
    public static RButton btn(Object text, Object... args) {
        HintList hints = new HintList();
        Icon icon = null;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();
        int insets = -1;

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Integer) {
                insets = (Integer) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        RButton btn;
        if(text instanceof Icon) {
            btn = new IconButton((Icon) text, insets);
        } else if(text instanceof ImageModelConcept) {
            btn = new IconButton((ImageModelConcept) text, insets);
        } else if(icon != null) {
            btn = new RButton(text == null ? null : text.toString(), icon);
        } else if(concept != null) {
            btn = new RButton(text == null ? null : text.toString(), ImageLib.get(concept));
        } else {
            btn = new RButton(text == null ? null : text.toString());
        }

        addListeners(btn, listeners);

        return applyHints(btn, hints);
    }

    public static RToggleButton btnt() {
        return btnt("");
    }
    public static RToggleButton btnt(Object text, Object... args) {
        boolean selected = false;
        HintList hints = new HintList();
        Icon icon = null;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();
        int insets = -1;

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Boolean) {
                selected = (Boolean) arg;
            } else if(arg instanceof Integer) {
                insets = (Integer) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        RToggleButton btn;
        if(text instanceof Icon) {
            btn = new IconToggleButton((Icon) text, insets);
        } else if(text instanceof ImageModelConcept) {
            btn = new IconToggleButton(ImageLib.get((ImageModelConcept) text), insets);
        } else if(icon != null) {
            btn = new RToggleButton(text == null ? null : text.toString(), icon);
        } else if(concept != null) {
            btn = new RToggleButton(text == null ? null : text.toString(), ImageLib.get(concept));
        } else {
            btn = new RToggleButton(text == null ? null : text.toString());
        }

        btn.setSelected(selected);

        addListeners(btn, listeners);

        return applyHints(btn, hints);
    }


    //////////
    // GLUE //
    //////////

    public static Component bxgl(String dim, Object... args) {
        Component gl = null;
        HintList hints = new HintList();
        if(dim.equalsIgnoreCase("v")){
            gl = Box.createVerticalGlue();
        } else if(dim.equalsIgnoreCase("h")){
            gl = Box.createHorizontalGlue();
        } else if(dim.equalsIgnoreCase("")){
            gl = Box.createGlue();
        }
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(gl, hints);
    }

    ///////////
    // STRUT //
    ///////////

    public static Component bxst(String dim, Integer dimValue, Object... args) {
        Component c = null;
        HintList hints = new HintList();
        if(dim.equalsIgnoreCase("v")){
            c = Box.createVerticalStrut(dimValue);
        } else if(dim.equalsIgnoreCase("h")){
            c = Box.createHorizontalStrut(dimValue);
        }
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(c, hints);
    }

    ///////////
    //  BOX  //
    ///////////

    public static Box bx(String dim, Object... args) {
        Box b = null;
        HintList hints = new HintList();
        if(dim.equalsIgnoreCase("v")){
            b = Box.createVerticalBox();
        } else if(dim.equalsIgnoreCase("h")){
            b = Box.createHorizontalBox();
        }
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(b, hints);
    }

    //////////////////
    //  RIGID AREA  //
    //////////////////

    public static Component bxra(Object... args) {
        Component ra = Box.createRigidArea(new Dimension(0, 0));
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }

        return applyHints(ra, hints);
    }

    //////////
    // MENU //
    //////////

    public static RMenu mn(Object text, Object... args) {
        HintList hints = new HintList();
        Icon icon = null;
        ActionListener al = null;
        ImageModelConcept concept = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof ActionListener) {
                al = (ActionListener) arg;
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }
        RMenu mnu;
        if(text instanceof Icon) {
            mnu = new RMenu((Icon) text);
        } else if(text instanceof ImageModelConcept) {
            mnu = new RMenu((ImageModelConcept) text);
        } else if(icon != null) {
            mnu = new RMenu(text.toString(), icon);
        } else if(concept != null) {
            mnu = new RMenu(text.toString(), concept);
        } else {
            mnu = new RMenu(text.toString());
        }
        if(al != null) {
            mnu.addActionListener(al);
        }

        return applyHints(mnu, hints);
    }


    //////////
    // MENU //
    //////////

    public static RMenuItem mi(Object text, Object... args) {
        HintList hints = new HintList();
        Icon icon = null;
        ActionListener al = null;
        ImageModelConcept concept = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof ActionListener) {
                al = (ActionListener) arg;
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        RMenuItem mnu;
        if(text instanceof Icon) {
            mnu = new RMenuItem((Icon) text);
        } else if(text instanceof ImageModelConcept) {
            mnu = new RMenuItem((ImageModelConcept) text);
        } else if(icon != null) {
            mnu = new RMenuItem(text.toString(), icon);
        } else if(concept != null) {
            mnu = new RMenuItem(text.toString(), concept);
        } else {
            mnu = new RMenuItem(text.toString());
        }
        if(al != null) {
            mnu.addActionListener(al);
        }

        return applyHints(mnu, hints);
    }


    ///////////
    // LABEL //
    ///////////

    public static <T extends RLabel> T lb() {
        return lb(null);
    }
    public static <T extends RLabel> T lb(Object text, Object... args) {
        HintList hints = new HintList();
        Icon icon = null;
        int horizAlignText = SwingConstants.LEADING;
        int horizAlignImage = SwingConstants.CENTER;
        ImageModelConcept concept = null;
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            } else if(arg instanceof Integer) {
                horizAlignText = (Integer) arg;
                horizAlignImage = (Integer) arg;
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            } else if(arg instanceof ImageModelConcept) {
                concept = (ImageModelConcept) arg;
            }
        }

        String resolvedText = null;
        Icon resolvedIcon = null;

        if(text instanceof Icon) {
            resolvedIcon = (Icon) text;
        } else if(text instanceof ImageModelConcept) {
            resolvedIcon = ImageLib.get((ImageModelConcept) text);
        } else {
            resolvedText = text == null ? null : text.toString();
            if(icon != null) {
                resolvedIcon = icon;
            } else if(concept != null) {
                resolvedIcon = ImageLib.get(concept);
            }
        }

        RLabel lbl;
        if(hints.contains("gradient")) {
            lbl = new GradientLabel(resolvedText, resolvedIcon, horizAlignText);
        } else if(hints.contains("bordered")) {
            lbl = new BorderedLabel(resolvedText, resolvedIcon/*, horizAlignText TODO*/);
        } else {
            lbl = new RLabel(resolvedText, resolvedIcon, horizAlignText);
        }

        addListeners(lbl, listeners);

        return (T) applyHints(lbl, hints);
    }
    public static JLabel lbtg(JLabel lbl, Object... args) {
        HintList hints = new HintList();
        Icon icon = null;
        String text = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                if(text == null) {
                    text = (String) arg;
                } else {
                    hints.addHints(LayHints.parseHints((String) arg));
                }
            } else if(arg instanceof Icon) {
                icon = (Icon) arg;
            }
        }
        if(icon != null) {
            lbl.setIcon(icon);
        }
        if(text != null) {
            lbl.setText(text);
        }

        return applyHints(lbl, hints);
    }


    ////////////////
    // TEXT FIELD //
    ////////////////

    public static <T extends RTextField> T tx() {
        return tx("");
    }
    public static <T extends RTextField> T tx(Object text, Object... args) {  // TODO: don't require 1st argument to be text, if a blank text field is desired
        HintList hints = new HintList();
        Document doc = null;
        int width = -1;
        Validator validator = null;
        ChangeListener validListener = null;
        ActionListener actionListener = null;     // TODO: Use more generic listener attaching
        KeyListener keyListener = null;
        DocumentListener docListener = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof Document) {
                doc = (Document) arg;
            } else if(arg instanceof Integer) {
                width = (Integer) arg;
            } else if(arg instanceof Validator) {
                validator = (Validator) arg;
            } else if(arg instanceof ChangeListener) {
                validListener = (ChangeListener) arg;
            } else if(arg instanceof ActionListener) {
                actionListener = (ActionListener) arg;
            } else if(arg instanceof KeyListener) {
                keyListener = (KeyListener) arg;
            } else if(arg instanceof DocumentListener) {
                docListener = (DocumentListener) arg;
            }
        }
        boolean g = hints.contains("glowing");
        boolean v = g || hints.contains("validating") || validator != null;
        RTextField txt;
        if(v) {
            if(width != -1) {
                if(g) {
                    txt = new GlowingValidatingTextField("" + text, width);
                } else {
                    txt = new ValidatingTextField("" + text, width);
                }
            } else {
                if(g) {
                    txt = new GlowingValidatingTextField("" + text);
                } else {
                    txt = new ValidatingTextField("" + text);
                }
            }
            if(validator != null) {
                ((ValidatingTextField) txt).setValidator(validator);
            }
            if(validListener != null) {
                ((ValidatingTextField) txt).addValidUnvalidatableTimeoutListener(validListener);
            }
        } else {
            if(width != -1) {
                txt = new RTextField("" + text, width);
            } else {
                txt = new RTextField("" + text);
            }
        }
        if(doc != null) {
            txt.setDocument(doc);
        }
        if(actionListener != null) {
            txt.addActionListener(actionListener);
        }
        if(keyListener != null) {
            txt.addKeyListener(keyListener);
        }
        if(docListener != null) {
            txt.getDocument().addDocumentListener(docListener);
        }

        return (T) applyHints(txt, hints);
    }


    //////////////
    // LIST BOX //
    //////////////

    public static RList lst() {
        return lst(new Object[0]);
    }
    public static RList lst(Object... args) {
        HintList hints = new HintList();
        ListModel model = null;
        Object[] modelArr = null;
        Vector<?> modelVec = null;
        List<?> modelList = null;
        ListCellRenderer renderer = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof ListModel) {
                model = (ListModel) arg;
            } else if(arg instanceof Object[]) {
                modelArr = (Object[]) arg;
            } else if(arg instanceof Vector) {
                modelVec = (Vector<?>) arg;
            } else if(arg instanceof List) {
                modelList = (List) arg;
            } else if(arg instanceof ListCellRenderer) {
                renderer = (ListCellRenderer) arg;
            }
        }

        RList lst;
        boolean empty = hints.contains("empty");
        boolean checkboxes = hints.contains("checkboxes");
        if(empty) {
            if(model != null) {
                lst = new EmptyMessageList(model);
            } else if(modelArr != null) {
                lst = new EmptyMessageList(modelArr);
            } else if(modelVec != null) {
                lst = new EmptyMessageList(modelVec);
            } else if(modelList != null) {
                lst = new EmptyMessageList(modelList.toArray());
            } else {
                lst = new EmptyMessageList();
            }
        } else if(checkboxes) {
            if(model != null) {
                lst = new RCheckBoxList(model);
            } else if(modelArr != null) {
                lst = new RCheckBoxList(modelArr);
            } else if(modelVec != null) {
                lst = new RCheckBoxList(modelVec);
            } else if(modelList != null) {
                lst = new RCheckBoxList(modelList.toArray());
            } else {
                lst = new RCheckBoxList();
            }
        } else {
            if(model != null) {
                lst = new RList(model);
            } else if(modelArr != null) {
                lst = new RList(modelArr);
            } else if(modelVec != null) {
                lst = new RList(modelVec);
            } else if(modelList != null) {
                lst = new RList(modelList.toArray());
            } else {
                lst = new RList();
            }
        }
        if(renderer != null) {
            lst.setCellRenderer(renderer);
        }

        return applyHints(lst, hints);
    }


    ///////////
    // TABLE //
    ///////////

    public static RTable tbl() {
        return tbl(new Object[0]);
    }
    public static RTable tbl(Object... args) {
        HintList hints = new HintList();
        TableModel model = null;
        TableCellEditor editor = null;
        Object[] headers = null;
        Object[][] data = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof TableModel) {
                model = (TableModel) arg;
            } else if(arg instanceof Object[][]) {
                data = (Object[][]) arg;
            } else if(arg instanceof Object[]) {
                headers = (Object[]) arg;
            } else if(arg instanceof TableCellEditor) {
                editor = (TableCellEditor) arg;
            }
        }
        RTable tbl;
        if(model != null) {
            tbl = new RTable(model);
        } else if(headers != null && data != null) {
            tbl = new RTable(data, headers);
        } else {
            tbl = new RTable();
        }
        if(editor != null) {
            tbl.setDefaultEditor(Object.class, editor);
        }

        // This can also be a distracting feature to have.  Eventually
        // we want to make all of this column/row sorting and enabling/
        // disabling configurable by the user in the RTablePanel.
        tbl.getTableHeader().setReorderingAllowed(false);

        return applyHints(tbl, hints);
    }

    public static RTablePanel tblp(Object... args) {
        HintList hints = new HintList();
        TableModel model = null;
        TableCellEditor editor = null;
        Object[] headers = null;
        Object[][] data = null;
        RTable tbl = null;
        boolean optionsExpanded = RTablePanel.DEFAULT_EXPANDED;
        int optionsPosition = RTablePanel.DEFAULT_POSITION;
        for(Object arg : args) {
            if(arg instanceof RTable) {
                tbl = (RTable) arg;
            } else if(arg instanceof String) {
                if(arg.equals("N")) {
                    optionsPosition = SwingConstants.NORTH;
                } else if(arg.equals("S")) {
                    optionsPosition = SwingConstants.SOUTH;
                } else {
                    hints.addHints(LayHints.parseHints((String) arg));
                }
            } else if(arg instanceof TableModel) {
                model = (TableModel) arg;
            } else if(arg instanceof Object[][]) {
                data = (Object[][]) arg;
            } else if(arg instanceof Object[]) {
                headers = (Object[]) arg;
            } else if(arg instanceof TableCellEditor) {
                editor = (TableCellEditor) arg;
            } else if(arg instanceof Integer) {
                optionsPosition = (Integer) arg;
            } else if(arg instanceof Boolean) {
                optionsExpanded = (Boolean) arg;
            }
        }

        if(tbl == null) {
            if(model != null) {
                tbl = new RTable(model);
            } else if(headers != null && data != null) {
                tbl = new RTable(data, headers);
            } else {
                tbl = new RTable();
            }
            if(editor != null) {
                tbl.setDefaultEditor(Object.class, editor);
            }
        }

        tbl.setAutoCreateRowSorter(true);

        // This can also be a distracting feature to have.  Eventually
        // we want to make all of this column/row sorting and enabling/
        // disabling configurable by the user in the RTablePanel.
        tbl.getTableHeader().setReorderingAllowed(false);

        RTablePanel pnlTable = new RTablePanel(tbl, optionsPosition, optionsExpanded);

        return applyHints(pnlTable, hints);
    }


    //////////
    // TREE //
    //////////

    public static <T extends RTree> T tr() {
        return tr(new Object[0]);
    }
    public static <T extends RTree> T tr(Object... args) {
        HintList hints = new HintList();
        TreeModel model = null;
        RTreeNode nRoot = null;
        NodeBase uRoot = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof TreeModel) {
                model = (TreeModel) arg;
            } else if(arg instanceof RTreeNode) {
                nRoot = (RTreeNode) arg;
            } else if(arg instanceof NodeBase) {
                uRoot = (NodeBase) arg;
            }
        }
        RTree tre;
        boolean vss = hints.contains("vss");
        if(vss) {
            if(model != null) {
                tre = new VisualStateSavingNoFireTree(model);
            } else if(nRoot != null) {
                tre = new VisualStateSavingNoFireTree(nRoot);
            } else if(uRoot != null) {
                tre = new VisualStateSavingNoFireTree(uRoot);
            } else {
                tre = VisualStateSavingNoFireTree.empty();
            }
        } else {
            if(model != null) {
                tre = new RTree(model);
            } else if(nRoot != null) {
                tre = new RTree(nRoot);
            } else if(uRoot != null) {
                tre = new RTree(uRoot);
            } else {
                tre = RTree.empty();
            }
        }

        return (T) applyHints(tre, hints);
    }


    ///////////////
    // TEXT AREA //
    ///////////////

    public static RTextArea txa() {
        return txa("");
    }
    public static RTextArea txa(Object text, Object... args) {
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }
        String t = text instanceof String ? (String) text : "";
        RTextArea txt = new RTextArea(t);

        return applyHints(txt, hints);
    }
    public static CopiableLabel lbc() {         // JLabel/JTextArea hybrid
        return lbc(null);
    }
    public static CopiableLabel lbc(Object text, Object... args) {         // JLabel/JTextArea hybrid
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }
        CopiableLabel lbl;
        if(text == null) {
            lbl = new CopiableLabel();
        } else {
            lbl = new CopiableLabel(text.toString());
        }

        return applyHints(lbl, hints);
    }


    ///////////////
    // TEXT PANE //
    ///////////////

    public static RTextPane txp() {
        return txp("");
    }
    public static RTextPane txp(Object text, Object... args) {
        HintList hints = new HintList();
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            }
        }
        String t = text instanceof String ? (String) text : "";
        RTextPane txt = new RTextPane(t);

        return applyHints(txt, hints);
    }


    ///////////////
    // COMBO BOX //
    ///////////////

    // "!strelems" is a SPECIAL ARGUMENT which causes all subsequent
    // arguments to be interpreted as elements for the combo box.
    public static <T extends JComboBox> T cb() {
        return cb(new Object[0]);
    }
    public static <T extends JComboBox> T cb(Object... args) {
        HintList hints = new HintList();
        List aModel = new ArrayList();
        ComboBoxModel cModel = null;
        boolean strElems = false;
        ListCellRenderer<?> renderer = null;
        List<EventListener> listeners = new ArrayList<>();
        for(Object arg : args) {
            if(arg instanceof String) {
                if(((String)arg).equalsIgnoreCase("!strelems")) {
                    strElems = true;
                } else {
                    if(strElems) {
                        aModel.add(arg);
                    } else {
                        hints.addHints(LayHints.parseHints((String) arg));
                    }
                }
            } else if(arg instanceof ListCellRenderer) {
                renderer = (ListCellRenderer) arg;

            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);

            } else if(arg instanceof ComboBoxModel) {
                cModel = (ComboBoxModel) arg;

            } else if(arg != null) {
                if(arg.getClass().isArray()) {
                    for(int a = 0; a < Array.getLength(arg); a++) {
                        aModel.add(Array.get(arg, a));
                    }
                } else {
                    aModel.add(arg);   // Not sure if this should be here, probably needs to come after "!strelems",
                }                      // but we can rename "!strelems" to something else and make it a constant
            }
        }

        boolean recent = hints.contains("recent");
        NoFireComboBox cbo;
        if(recent) {
            if(cModel != null) {
                cbo = new RecentComboBox(cModel);
            } else {
                cbo = new RecentComboBox(aModel.toArray());
            }
        } else {
            if(cModel != null) {
                cbo = new NoFireComboBox(cModel);
            } else {
                cbo = new NoFireComboBox(aModel.toArray());
            }
        }
        if(renderer != null) {
            cbo.setRenderer(renderer);
        }
        addListeners(cbo, listeners);

        return (T) applyHints(cbo, hints);
    }


    /////////////
    // SPINNER //
    /////////////

    public static <T extends JSpinner> T spn() {
        return spn(new Object[0]);
    }
    public static <T extends JSpinner> T spn(Object... args) {
        HintList hints = new HintList();
        SpinnerModel mdl = null;
        for(Object arg : args) {
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));
            } else if(arg instanceof SpinnerModel) {
                mdl = (SpinnerModel) arg;
            }
        }

        JSpinner spn;
        if(mdl == null) {
            spn = new JSpinner();
        } else {
            spn = new JSpinner(mdl);
        }

        return (T) applyHints(spn, hints);
    }


    //////////
    // TABS //
    //////////

    public static RTabbedPane TBL(Object... args) {
        return TBLtg((RTabbedPane) null, args);
    }
    public static RTabbedPane TBLtg(RTabbedPane tabs, Object... args) {
        HintList hints = new HintList();
        List<Object[]> tabParts = new ArrayList<>();
        int tabPlacement = SwingConstants.TOP;

        for(int a = 0; a < args.length; a++) {
            Object arg = args[a];
            if(arg instanceof Component) {
                Component cmp = (Component) arg;

                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof String) {
                        String title = (String) obj2;
                        tabParts.add(new Object[] {title, cmp, null});
                        a++;
                        if(a < args.length - 1) {
                            Object obj3 = args[a + 1];
                            if(obj3 instanceof Icon || obj3 instanceof ImageModelConcept) {
                                Icon icon =
                                    obj3 instanceof ImageModelConcept ?
                                        ImageLib.get((ImageModelConcept) obj3) :
                                        (Icon) obj3;
                                tabParts.get(tabParts.size() - 1)[2] = icon;
                                a++;
                            }
                        }
                    } else if(obj2 instanceof Icon || obj2 instanceof ImageModelConcept) {
                        Icon icon =
                            obj2 instanceof ImageModelConcept ?
                                ImageLib.get((ImageModelConcept) obj2) :
                                (Icon) obj2;
                        tabParts.add(new Object[] {null, cmp, icon});
                        a++;
                        if(a < args.length - 1) {
                            Object obj3 = args[a + 1];
                            if(obj3 instanceof String) {
                                String title = (String) obj3;
                                tabParts.get(tabParts.size() - 1)[0] = title;
                                a++;
                            }
                        }
                    }
                }

            } else if(arg instanceof String) {
                String title = (String) arg;
                if(a < args.length - 1) {
                    Object obj2 = args[a + 1];
                    if(obj2 instanceof Component) {
                        Component cmp = (Component) obj2;
                        tabParts.add(new Object[] {title, cmp, null});
                        a++;
                        if(a < args.length - 1) {
                            Object obj3 = args[a + 1];
                            if(obj3 instanceof Icon || obj3 instanceof ImageModelConcept) {
                                Icon icon =
                                    obj3 instanceof ImageModelConcept ?
                                        ImageLib.get((ImageModelConcept) obj3) :
                                        (Icon) obj3;
                                tabParts.get(tabParts.size() - 1)[2] = icon;
                                a++;
                            }
                        }

                    } else if((obj2 instanceof Icon || obj2 instanceof ImageModelConcept) &&
                            a + 1 < args.length - 1 && args[a + 2] instanceof Component) {
                        Icon icon =
                            obj2 instanceof ImageModelConcept ?
                                ImageLib.get((ImageModelConcept) obj2) :
                                (Icon) obj2;
                        Component cmp = (Component) args[a + 2];
                        tabParts.add(new Object[] {title, cmp, icon});
                        a += 2;

                    } else {
                        hints.addHints(LayHints.parseHints(title));
                    }
                } else {
                    hints.addHints(LayHints.parseHints(title));
                }

            } else if(arg instanceof Integer) {
                tabPlacement = (Integer) arg;
            }

            // Else ignore
        }

        if(tabs == null) {
            tabs = new RTabbedPane(tabPlacement, hints.contains("dc"));
        } else {
            tabs.setTabPlacement(tabPlacement);
            tabs.getDefaultTabCreationDescriptor().setCloseable(hints.contains("dc"));
        }
        int t = 0;
        for(Object[] tabPart : tabParts) {
            tabs.addTab((String) tabPart[0], (Component) tabPart[1]);
            if(tabPart[2] != null) {
                Icon icon = (Icon) tabPart[2];
                tabs.setIconAt(t, icon);
            }
            t++;
        }

        return applyHints(tabs, hints);
    }



    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ //
    //                      CONTROLS                       //
    // =================================================== //



    // --------------------------------------------//
    // ------------ SUPPORTING METHODS ------------//
    // --------------------------------------------//


    ///////////////////
    // LAYOUT HELPER //
    ///////////////////

    private static Container chooseContainer(Container target, HintList hints) {
        if(target == null) {
            if(hints.contains("gradient") || LayHints.getGlobalHints().getMap().containsKey("gradient")) {
                if(debugOn) {
                    System.out.println("Chose gradient panel");
                }
                return new GradientPanel();
            } else if(hints.contains("bordered") || LayHints.getGlobalHints().getMap().containsKey("bordered")) {
                return new BorderedPanel();
            }
            RPanel pnl = new RPanel();
            if(hints.contains("nodebug") || LayHints.getGlobalHints().getMap().containsKey("nodebug")) {
                pnl.setDebugColorEnabled(false);
                pnl.setDebugTicksEnabled(false);
                pnl.setDebugMouseEnabled(false);
            }
            return pnl;
        }

        // We need layout / hierarchy operations to happen on the
        // content pane of dialogs & frames.
        if(isContentPaneHolder(target)) {
            return ((RootPaneContainer) target).getContentPane();
        }

        return target;
    }

    private static <T extends JPanel> T resolvePanel(Container target, Container cont, HintList hints) {
        applyHints(cont, hints);
        if(target == null) {
            return (T) cont;
        }
        if(target instanceof JPanel) {
            return (T) target;
        }
        if(target instanceof RootPaneContainer) {
            return (T) ((RootPaneContainer) target).getContentPane();
        }
        return null;
    }


    //////////////////
    // LAYOUT HINTS //
    //////////////////

    private static boolean debugOn = false;

    public static void debug() {
        debugOn = true;
        UiDebugUtil.enableColor();
    }
    public static class HintPair {
        public String key;
        public String value;

        public HintPair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + (value != null ? " = " + value : "");
        }
    }

    public static class HintList extends ArrayList<HintPair> {
        private Map<String, String> map = new HashMap<>();
        public void add(String key, String value) {
            map.put(key, value);
            super.add(new HintPair(key, value));
        }
        public String get(String key) {
            return map.get(key);
        }
        public boolean contains(String key) {
            return map.containsKey(key);
        }
        public void addHints(HintList lst) {
            map.putAll(lst.getMap());
            super.addAll(lst);
        }
        public Map<String, String> getMap() {
            return map;
        }
    }

    private static <T extends Component> T applyHints(T cmp, HintList hints) {
        HintList finalHints = new HintList();
        finalHints.addHints(LayHints.getGlobalHints());
        finalHints.addHints(hints);
        for(HintPair hint : finalHints) {
            String key = hint.key;
            String value = hint.value;
            String lowerKey = key.toLowerCase();
            if(LayHints.getProcessors().containsKey(lowerKey)) {
                LayHint hp = LayHints.getProcessors().get(lowerKey);
                Component cmpSelected = null;
                try {

                    // If this hint is for windows only, then
                    // make sure that if the original component is a
                    // window's content pane, we choose the window
                    // itself to which to apply the hint.
                    if(hp.applicableTo == LayHint.WIN) {
                        cmpSelected = cmp;
                        if(isContentPane(cmpSelected)) {
                            while(!(cmpSelected instanceof Window)) {
                                cmpSelected = cmpSelected.getParent();
                            }
                        }

                    // If this hint is allowed to be applied to either
                    // windows or non-windows, make sure content panes
                    // are replaced with their parent windows. This
                    // assumes however that a developer would never
                    // explicitly send in a content pane panel wanting
                    // these types of hints applied (this could be
                    // thought out better). However, right now this
                    // meets the need of 'visible' being applied to
                    // both Windows and non-content pane panels inside
                    // contained by the window.
                    } else if(hp.applicableTo == LayHint.BOTH) {
                        cmpSelected = cmp;
                        if(isContentPane(cmpSelected)) {
                            while(!(cmpSelected instanceof Window)) {
                                cmpSelected = cmpSelected.getParent();
                            }
                        }

                    // If this hint is for non-window components only,
                    // Make sure we apply it to the window's content pane
                    // IF original component is a window.
                    } else {
                        cmpSelected = cmp;
                        if(isContentPaneHolder(cmpSelected)) {
                            cmpSelected = ((RootPaneContainer) cmpSelected).getContentPane();
                        }
                    }

                    hp.processor.process(value, cmpSelected, hints);
                    if(debugOn) {
                        System.out.println(
                            "Applied hint (" + lowerKey + "=" + value + ") to [" + cmpSelected + "]");
                    }
                } catch(Exception e) {
                    // Soft fail (ClassCastException, NumberFormatException most common)
                    if(debugOn) {
                        System.err.println(
                            "Hint error (" + lowerKey + "=" + value + ") to [" + cmpSelected + "]");
                        e.printStackTrace();
                    }
                }
            }
        }

        return hints.contains("nullify") ? null : cmp;
    }

    private static boolean isContentPane(Component c) {
        return c.getParent() instanceof JLayeredPane;
    }

    private static boolean isContentPaneHolder(Component c) {
        return c instanceof RootPaneContainer;
    }

    public static Component hn(Object... args) {
        HintList hints = new HintList();
        List<Component> comps = new ArrayList<>();
        List<EventListener> listeners = new ArrayList<>();

        for(Object arg : args) {

            // Save hints
            if(arg instanceof String) {
                hints.addHints(LayHints.parseHints((String) arg));

            // Save components
            } else if(arg instanceof Component) {
                comps.add((Component) arg);

            // Save event listeners
            } else if(arg instanceof EventListener) {
                listeners.add((EventListener) arg);
            }
        }

        for(Component comp : comps) {
            applyHints(comp, hints);
            addListeners(comp, listeners);
        }
        if(comps.size() == 0) {
            return null;
        }
        return comps.get(0);
    }


    //////////
    // MISC //
    //////////

    private static void checkSetIcon(Container target, Object arg) {
        if(target instanceof RFrame) {
            RFrame frame = (RFrame) target;
            if(arg instanceof ImageIcon) {
                frame.setIcon((ImageIcon) arg);
            } else {
                frame.setIcon((ImageModelConcept) arg);
            }
        } else if(target instanceof RDialog) {
            RDialog dlg = (RDialog) target;
            if(arg instanceof ImageIcon) {
                dlg.setIcon((ImageIcon) arg);
            } else {
                dlg.setIcon((ImageModelConcept) arg);
            }
        }
    }

    public static void match(Component argRef, Component... args) {
        for(Component arg : args) {
            arg.setMinimumSize(argRef.getMinimumSize());
            arg.setMaximumSize(argRef.getMaximumSize());
            arg.setPreferredSize(argRef.getPreferredSize());
        }
    }
    public static void matchMaxPrefWidth(Component... args) {
        int maxWidth = -1;
        for(Component arg : args) {
            if(arg.getPreferredSize().width > maxWidth) {
                maxWidth = arg.getPreferredSize().width;
            }
        }
        for(Component arg : args) {
            arg.setPreferredSize(new Dimension(maxWidth, arg.getPreferredSize().height));
        }
    }
    public static void matchMaxPrefWidth(JButton btn, String... captions) {
        String current = btn.getText();
        int maxWidth = -1;
        for(String caption : captions) {
            btn.setText(caption);
            int prefW = btn.getPreferredSize().width;
            if(prefW > maxWidth) {
                maxWidth = prefW;
            }
        }
        btn.setText(current);
        btn.setPreferredSize(new Dimension(maxWidth, btn.getPreferredSize().height));
    }

    public static ButtonGroup grp(Object... args) {
        List<AbstractButton> btns = new ArrayList<>();
        ButtonGroup group = null;

        for(Object arg : args) {
            if(arg instanceof AbstractButton) {
                btns.add((AbstractButton) arg);
            } else if(arg instanceof ButtonGroup) {
                group = (ButtonGroup) arg;
            }
        }
        if(group == null) {
            group = new ButtonGroup();
        }
        for(AbstractButton btn : btns) {
            group.add(btn);
        }
        return group;
    }

    public static Color clrdlft() {
        return new JPanel().getBackground();
    }

    public static String clrhex(Color clr) {
        String hexString = Integer.toHexString(clr.getRGB() & 0x00FFFFFF);
        while(hexString.length() < 6) {
            hexString = "0" + hexString;
        }
        return hexString;
    }

    public static String clr(Color clr) {
        return "[" + clr.getRed() + "," + clr.getGreen() + "," + clr.getBlue() + "]";
    }

    public static Color clr(String value) {
        Color clr = null;
        int[] nums = LayUtil.parseNumberList(value);    // TODO: Can capture "000099" (hex) as {99} instead of {}
        try {
            if(nums.length == 3) {
                int red = nums[0];
                int green = nums[1];
                int blue = nums[2];
                clr = new Color(red, green, blue);
            } else if(nums.length == 1 || nums.length == 2) {
                int gray = nums[0];
                clr = new Color(gray, gray, gray);
            } else if(ReflectionUtil.hasField(Color.class, value)) {
                clr = ReflectionUtil.get(Color.class, value);
            } else if(ReflectionUtil.hasField(Color.class, value.toUpperCase())) {
                clr = ReflectionUtil.get(Color.class, value.toUpperCase());
            } else if(ReflectionUtil.hasField(ColorLib.class, value)) {
                clr = ReflectionUtil.get(ColorLib.class, value);
            } else if(ReflectionUtil.hasField(ColorLib.class, value.toUpperCase())) {
                clr = ReflectionUtil.get(ColorLib.class, value.toUpperCase());
            }
        } catch(Exception ee) {}
        if(clr == null) {
            clr = getColorFromHex(value);
        }
        if(clr == null) {
            throw new RuntimeException("Value '" + value + "' not recognized as a color");
        }
        return clr;
    }

    public static Color getColorFromHex(String code) {
        String clrPat = "^#?([0-9A-Fa-f]{3}|[0-9A-Fa-f]{6})$";
        Pattern p = Pattern.compile(clrPat);
        Matcher m = p.matcher(code);
        if(m.find()) {
            String grp = m.group(1);
            int red, green, blue;
            if(grp.length() == 3) {
                red = Integer.parseInt("" + grp.charAt(0) + grp.charAt(0), 16);
                green = Integer.parseInt("" + grp.charAt(1) + grp.charAt(1), 16);
                blue = Integer.parseInt("" + grp.charAt(2) + grp.charAt(2), 16);
            } else {
                red = Integer.parseInt(grp.substring(0, 2), 16);
                green = Integer.parseInt(grp.substring(2, 4), 16);
                blue = Integer.parseInt(grp.substring(4, 6), 16);
            }
            return new Color(red, green, blue);
        }
        return null;
    }

    private static void addListeners(Component cmp, List<EventListener> listeners) {
        for(EventListener listener : listeners) {
            try {
                if(listener instanceof ActionListener) {
                    ReflectionUtil.invoke(cmp, "addActionListener", listener);
                } else if(listener instanceof KeyListener) {
                    cmp.addKeyListener((KeyListener) listener);
                } else if(listener instanceof MouseListener) {
                    cmp.addMouseListener((MouseListener) listener);
                } else if(listener instanceof MouseMotionListener) {
                    cmp.addMouseMotionListener((MouseMotionListener) listener);
                } else if(listener instanceof MouseWheelListener) {
                    cmp.addMouseWheelListener((MouseWheelListener) listener);
                } else if(listener instanceof FocusListener) {
                    cmp.addFocusListener((FocusListener) listener);
                }
            } catch(Exception e) {

            }
        }
    }

    public static Component hs(int width) {
        return Box.createHorizontalStrut(width);
    }
    public static Component vs(int height) {
        return Box.createVerticalStrut(height);
    }

    public static void useFlatSplitPanes() {
        // https://stackoverflow.com/questions/12799640/why-does-jsplitpane-add-a-border-to-my-components-and-how-do-i-stop-it
        UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.out.println(Lay.btn("Asdfsa", "nullify"));
        if(true) {
            return;
        }
//        JFrame f2 = new JFrame();
//        JPanel p = new JPanel();
//        Lay.BLtg(f2, p);
//        p.setLayout(new BorderLayout());
//        p.add(new JButton("hi"), BorderLayout.NORTH);
//        p.add(new JButton("center"), BorderLayout.CENTER);
//        System.out.println(p.getComponentCount());
//        p.setLayout(new BorderLayout());
//        System.out.println(p.getComponentCount());
//        Lay.hn(f2, "size=[500,500],visible,center=2");
//
//        System.out.println(Lay.clr(Lay.clr("yellow")));
//        if(true) {
//            return;
//        }

//        Color c = Color.white;
//        System.out.println(clrhex(c));
//        if(true) {
//            return;
//        }

        System.out.println(Lay.clr("235,100,0"));

//        Lay.debug();
        EscapeFrame f = new EscapeFrame("VISIBLE");
        Lay.BLtg(f,
            "N", Lay.FL(
                Lay.lb("adfasf"),
                new JButton("xx")
                ),
            "C", temp("AAAAA"),
            "size=[400,400],center,visible=true"
            );
         f.setVisible(true);

         if(true) {
             return;
         }

        // for(HintProcessor p : hintProcessors) {
        // System.out.println(p.key);
        // }
        // String code = "9AF";
        // System.out.println(Lay.clr(getColorFromHex(code)));

        final JFrame d = new EscapeFrame("EXIT ON CLOSE");
        d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        d.addWindowListener(new DebugWindowListener());
        JButton btn = new RButton("&Close");
        btn.addActionListener(e -> {
            System.out.println(
                "dco=" + d.getDefaultCloseOperation() + " (exit=" + JFrame.EXIT_ON_CLOSE + ")"
            );
            d.dispose();
        });
        Lay.FLtg(d, btn);
        d.setSize(300, 300);
        d.setVisible(true);

        // System.out.println(new Sides("9t 10b 5b"));
        // Component c = null, c2 = null;
        // FL(10, 10, "R", c, c2);
        // System.out.println(LayHints.parseHints("    a  =  \t fido, x   =  982372  ,  t =  [j  k h ]   \t"));
        JFrame frame = new EscapeFrame("ORIGINAL");

        Lay.eb("10t");

        Lay.BLtg(frame,
            "N", Lay.eb(Lay.FL(new JButton("hi")), "10t5l"),
            "S", Lay.GL(2, 2,
                Lay.FL(new JLabel("dude")), new JButton("B"),
                new JButton("C"), new JButton("D")
            )
        );

        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static RPanel temp(String label, String... hints) {
        Object[] args = new Object[hints.length + 1];
        args[0] = Lay.lb("<html><i>(" + label + ")</i></html>");
        System.arraycopy(hints, 0, args, 1, hints.length);
        return Lay.GBL(args);
    }
}
