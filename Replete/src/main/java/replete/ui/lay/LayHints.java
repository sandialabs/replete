package replete.ui.lay;

import static replete.ui.lay.Lay.augb;
import static replete.ui.lay.Lay.clr;
import static replete.ui.lay.Lay.eb;
import static replete.ui.lay.Lay.mb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeSelectionModel;

import replete.extensions.ui.WrapLayout;
import replete.numbers.NumUtil;
import replete.text.StringLib;
import replete.text.StringUtil;
import replete.ui.ColorLib;
import replete.ui.GuiUtil;
import replete.ui.button.IconButton;
import replete.ui.combo.RComboBox;
import replete.ui.label.BorderedLabel;
import replete.ui.label.GradientLabel;
import replete.ui.lay.Lay.HintList;
import replete.ui.list.EmptyMessageList;
import replete.ui.panels.BorderedPanel;
import replete.ui.panels.GradientPanel;
import replete.ui.panels.RPanel;
import replete.ui.sp.RScrollPane;
import replete.ui.tabbed.RTabbedPane;
import replete.ui.table.RTable;
import replete.ui.table.RTablePanel;
import replete.ui.text.RTextField;
import replete.ui.text.RTextPane;
import replete.ui.text.bubble.BubbleTextField;
import replete.ui.text.editor.REditor;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.tree.RTree;
import replete.ui.windows.common.RWindow;
import replete.ui.windows.notifications.NotificationWindow;


public class LayHints {


    ///////////
    // FIELD //
    ///////////

    private static Map<String, LayHint> hints = new TreeMap<>();

    private static void addN(String name, String description, ParamDescriptor params, LayHintProcessor processor, Class<?>... applicableTargetClasses) {
        addAny(name, LayHint.NONWIN, description, params, processor, applicableTargetClasses);
    }
    private static void addW(String name, String description, ParamDescriptor params, LayHintProcessor processor, Class<?>... applicableTargetClasses) {
        addAny(name, LayHint.WIN, description, params, processor, applicableTargetClasses);
    }
    private static void addB(String name, String description, ParamDescriptor params, LayHintProcessor processor, Class<?>... applicableTargetClasses) {
        addAny(name, LayHint.BOTH, description, params, processor, applicableTargetClasses);
    }

    private static void addAny(String name, int applicableTo, String description, ParamDescriptor params, LayHintProcessor processor, Class<?>[] applicableTargetClasses) {
        LayHint hint = new LayHint()
            .setName(name)
            .setApplicableTo(applicableTo)
            .setDescription(description)
            .setParams(params)
            .setProcessor(processor)
            .setApplicableTargetClasses(applicableTargetClasses)
        ;
        hints.put(name, hint);
    }

    private static ParamDescriptor b() {
        return b(null);
    }
    private static ParamDescriptor b(String msg) {
        return new ParamDescriptor(ParamType.BOOL, msg);
    }
    private static ParamDescriptor i() {
        return i(null);
    }
    private static ParamDescriptor i(String msg) {
        return new ParamDescriptor(ParamType.INT, msg);
    }
    private static ParamDescriptor f() {
        return f(null);
    }
    private static ParamDescriptor f(String msg) {
        return new ParamDescriptor(ParamType.FLOAT, msg);
    }
    private static ParamDescriptor d() {
        return d(null);
    }
    private static ParamDescriptor d(String msg) {
        return new ParamDescriptor(ParamType.DBL, msg);
    }
    private static ParamDescriptor s() {
        return s(null);
    }
    private static ParamDescriptor s(String msg) {
        return new ParamDescriptor(ParamType.STR, msg);
    }
    private static ParamDescriptor c(String msg) {
        return new ParamDescriptor(ParamType.CUSTOM, msg);
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    static {


        ////////////////////
        // All Components //
        ////////////////////

        addB("visible", "Visibility", b(), (value, cmp, allHints) -> {
            cmp.setVisible(boolVal(value));
        }, Window.class, Component.class);

        addN("eml", "Message displayed when list is empty", s(), (value, cmp, allHints) -> {
            ((EmptyMessageList) cmp).setEmptyMessage(textVal(value));
        }, EmptyMessageList.class);

        addN("enabled", "Enabled-ness", b(), (value, cmp, allHints) -> {
            cmp.setEnabled(boolVal(value));
        }, Component.class);

        addN("focusable", "Focusable or not-focusable", b(), (value, cmp, allHints) -> {
            cmp.setFocusable(boolVal(value));
        }, Component.class);

        addN("cursor", "Mouse cursor", c("ENUM: <CURSOR>"), (value, cmp, allHints) -> {
            Map<String, Integer> cursors = new HashMap<>();
            cursors.put("default", Cursor.DEFAULT_CURSOR);
            cursors.put("hand",    Cursor.HAND_CURSOR);
            cursors.put("text",    Cursor.TEXT_CURSOR);

            for(String key : cursors.keySet()) {
                if(value.equalsIgnoreCase(key)) {
                    Integer which = cursors.get(key);
                    cmp.setCursor(Cursor.getPredefinedCursor(which));
                }
            }
        }, Component.class);

        addN("name", "Name", s(), (value, cmp, allHints) -> {
            cmp.setName(textVal(value));
        }, Component.class);

        // -- Font --

        addB("font", "Font name", s(), (value, cmp, allHints) -> {
            cmp.setFont(
                new Font(textVal(value),
                    cmp.getFont().getStyle(),
                    cmp.getFont().getSize()
                )
            );
        }, Window.class, Component.class);

        addN("plain", "Plain font", null, (value, cmp, allHints) -> {
            cmp.setFont(cmp.getFont().deriveFont(Font.PLAIN));
        }, Component.class);

        addN("italic", "Italic font", null, (value, cmp, allHints) -> {
            if(boolVal(value)) {
                cmp.setFont(cmp.getFont().deriveFont(cmp.getFont().getStyle() | Font.ITALIC));
            } else {
                cmp.setFont(cmp.getFont().deriveFont(cmp.getFont().getStyle() & ~Font.ITALIC));
            }
        }, Component.class);

        addN("bold", "Bold font", null, (value, cmp, allHints) -> {
            if(boolVal(value)) {
                cmp.setFont(cmp.getFont().deriveFont(cmp.getFont().getStyle() | Font.BOLD));
            } else {
                cmp.setFont(cmp.getFont().deriveFont(cmp.getFont().getStyle() & ~Font.BOLD));
            }
        }, Component.class);

        addN("underline", "Underline font", null, (value, cmp, allHints) -> {
            Font original = cmp.getFont();
            Map attributes = original.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            cmp.setFont(original.deriveFont(attributes));
        }, Component.class);

        // -- Location --

        addN("bounds", "Bounds (x, y, w, h)", c("[INT,INT,INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            if(nums.length == 4) {
                int x = nums[0];
                int y = nums[1];
                int w = nums[2];
                int h = nums[3];
                cmp.setBounds(x, y, w, h);
            }
        }, Component.class);

        addB("loc", "Location (x, y)", c("[INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            nums = force2(nums);
            cmp.setLocation(nums[0], nums[1]);
        }, Window.class, Component.class);

        // -- Dimensions --

        addN("dim", "Min, max, and preferred size (w, h)", c("[INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            nums = force2(nums);
            Dimension d = new Dimension(nums[0], nums[1]);
            cmp.setMinimumSize(d);
            cmp.setMaximumSize(d);
            cmp.setPreferredSize(d);
        }, Component.class);

        addN("dimw", "Min, max, and preferred width", i(), (value, cmp, allHints) -> {
            int w = intVal(value);
            Dimension dim = new Dimension(w, cmp.getMinimumSize().height);
            cmp.setMinimumSize(dim);
            dim = new Dimension(w, cmp.getMaximumSize().height);
            cmp.setMaximumSize(dim);
            dim = new Dimension(w, cmp.getPreferredSize().height);
            cmp.setPreferredSize(dim);
        }, Component.class);

        addN("dimh", "Min, max, and preferred height", i(), (value, cmp, allHints) -> {
            int h = intVal(value);
            Dimension dim = new Dimension(cmp.getMinimumSize().width, h);
            cmp.setMinimumSize(dim);
            dim = new Dimension(cmp.getMaximumSize().width, h);
            cmp.setMaximumSize(dim);
            dim = new Dimension(cmp.getPreferredSize().width, h);
            cmp.setPreferredSize(dim);
        }, Component.class);

        addN("pref", "Preferred size (w, h)", c("[INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            nums = force2(nums);
            Dimension d = new Dimension(nums[0], nums[1]);
            cmp.setPreferredSize(d);
        }, Component.class);

        addN("prefw", "Preferred width", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(intVal(value), cmp.getPreferredSize().height);
            cmp.setPreferredSize(dim);
        }, Component.class);

        addN("prefh", "Preferred height", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(cmp.getPreferredSize().width, intVal(value));
            cmp.setPreferredSize(dim);
        }, Component.class);

        addN("min", "Minimum size (w, h)", c("[INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            nums = force2(nums);
            Dimension d = new Dimension(nums[0], nums[1]);
            cmp.setMinimumSize(d);
        }, Component.class);

        addN("minw", "Minimum width", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(intVal(value), cmp.getMinimumSize().height);
            cmp.setMinimumSize(dim);
        }, Component.class);

        addN("minh", "Minimum height", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(cmp.getMinimumSize().width, intVal(value));
            cmp.setMinimumSize(dim);
        }, Component.class);

        addN("max", "Maximum size (w, h)", c("[INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            nums = force2(nums);
            Dimension d = new Dimension(nums[0], nums[1]);
            cmp.setMaximumSize(d);
        }, Component.class);

        addN("maxw", "Maximum width", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(intVal(value), cmp.getMaximumSize().height);
            cmp.setMaximumSize(dim);
        }, Component.class);

        addN("maxh", "Maximum height", i(), (value, cmp, allHints) -> {
            Dimension dim = new Dimension(cmp.getMaximumSize().width, intVal(value));
            cmp.setMaximumSize(dim);
        }, Component.class);

        // -- Alignment --

        addN("alignx", "X alignment for BoxLayout", f(), (value, cmp, allHints) -> {
            ((JComponent) cmp).setAlignmentX(floatVal(value));
        }, JComponent.class);

        addN("aligny", "Y alignment for BoxLayout", f(), (value, cmp, allHints) -> {
            ((JComponent) cmp).setAlignmentY(floatVal(value));
        }, JComponent.class);

        // -- Colors --

        addN("fg", "Foreground color", c("COLOR"), (value, cmp, allHints) -> {
            cmp.setForeground(clr(value));
        }, Component.class);

        addN("bg", "Background color", c("COLOR"), (value, cmp, allHints) -> {
            cmp.setBackground(clr(value));
            ((JComponent) cmp).setOpaque(true);
        }, JComponent.class);

        addN("white", "Set background to white", null, (value, cmp, allHints) -> {
            cmp.setBackground(Color.white);
            ((JComponent) cmp).setOpaque(true);
        }, JComponent.class);

        // -- Borders --

        addN("eb", "Set empty border", c("MCODE"), (value, cmp, allHints) -> {
            ((JComponent) cmp).setBorder(eb(value));
        }, JComponent.class);

        addN("mb", "Set matte border", c("[MCODE(,COLOR)]"), (value, cmp, allHints) -> {
            int comma = value.indexOf(",");
            if(comma == -1) {
                String code;
                if(value.indexOf("[") != -1) {
                    code = value.substring(value.indexOf("[") + 1, value.indexOf("]"));
                } else {
                    code = value;
                }
                ((JComponent) cmp).setBorder(mb(code, Color.black));
            } else {
                String code = value.substring(value.indexOf("[") + 1, comma).toString();
                String color = value.substring(comma + 1, value.indexOf("]")).trim();
                ((JComponent) cmp).setBorder(mb(code, clr(color)));
            }
        }, JComponent.class);

        addN("augb", "Augment current border", c("FUNC"), (value, cmp, allHints) -> {
            String[] parts = LayUtil.parseFunctionCall(value);
            if(parts[0].equals("eb")) {
                augb((JComponent) cmp, eb(parts[1]));
            } else if(parts[0].equals("mb")) {
                augb((JComponent) cmp, mb(parts[1], clr(parts[2])));
            }
        }, JComponent.class);

        // -- Other --

        addN("opaque", "Opacity", b(), (value, cmp, allHints) -> {
            ((JComponent) cmp).setOpaque(boolVal(value));
        }, JComponent.class);

        addN("ttt", "Tool tip text", s(), (value, cmp, allHints) -> {
            ((JComponent) cmp).setToolTipText(textVal(value));
        }, JComponent.class);


        /////////////////////////////
        // Various Component Types //
        /////////////////////////////

        addB("size", "Font size (components that have text); size (all others) (w, h)", c("INT; [INT,INT]"), (value, cmp, allHints) -> {
            int[] nums = LayUtil.parseNumberList(value);
            if(cmp instanceof JLabel ||
                    cmp instanceof JTextComponent ||
                    cmp instanceof JList ||
                    cmp instanceof JComboBox ||
                    cmp instanceof JCheckBox ||
                    cmp instanceof JRadioButton ||
                    cmp instanceof JButton ||
                    cmp instanceof BubbleTextField ||
                    cmp instanceof JTable ||
                    cmp instanceof JTree) {
                // TODO: actually, should be non-window components.
                Font f = cmp.getFont();
                if(nums.length == 1) {
                    cmp.setFont(f.deriveFont((float) nums[0]));
                }
                return;
            }
            if(nums.length >= 2) {
                int width = nums[0];
                int height = nums[1];
                cmp.setSize(width, height);
            } else if(nums.length == 1) {
                int width = nums[0];
                int height = nums[0];
                cmp.setSize(width, height);
            }
        }, Window.class, Component.class);

        addW("center", "Center on screen (windows); center text (text field, label); center (all others)", null, (value, cmp, allHints) -> {
            if(cmp instanceof Window) {
                Window w = ((Window) cmp);
                if(value != null) {
                    if(NumUtil.isInt(value)) {
                        int i = intVal(value);
                        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        GraphicsDevice[] gds = ge.getScreenDevices();
                        if(i > 0 && i <= gds.length) {
                            GraphicsDevice gd = gds[i - 1];
                            DisplayMode dm = gd.getDisplayMode();
                            int left = gd.getDefaultConfiguration().getBounds().x + (dm.getWidth() - w.getWidth()) / 2;
                            int top = (dm.getHeight() - w.getHeight()) / 2;
                            w.setLocation(left, top);
                            return;
                        }
                    }
                }
                w.setLocationRelativeTo(cmp.getParent());
            } else if(cmp instanceof JTextField) {
                ((JTextField) cmp).setHorizontalAlignment(SwingConstants.CENTER);
            } else if(cmp instanceof JLabel) {
                ((JLabel) cmp).setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                Component parent = cmp.getParent();
                int x = (parent.getWidth() - cmp.getWidth()) / 2;
                int y = (parent.getHeight() - cmp.getHeight()) / 2;
                cmp.setBounds(x, y, cmp.getWidth(), cmp.getHeight());
            }
        }, Window.class, JTextField.class, JLabel.class, Component.class);

        addN("htext", "Horizontal text position", c("ENUM: <POS>"), (value, cmp, allHints) -> {
            Map<String, Integer> consts = swingAlignmentConsts();
            int alignment = consts.get(value.toLowerCase());

            if(cmp instanceof JLabel) {
                ((JLabel) cmp).setHorizontalTextPosition(alignment);
            } else {
                ((JButton) cmp).setHorizontalTextPosition(alignment);
            }
        }, JLabel.class, JButton.class);

        addN("border", "Whether border is painted (progress bar); turn off border (all others)", b(), (value, cmp, allHints) -> {
            if(cmp instanceof JProgressBar) {
                ((JProgressBar) cmp).setBorderPainted(boolVal(value));
            } else {
                if(!boolVal(value)) {
                    ((JComponent) cmp).setBorder(null);
                }
            }
        }, JProgressBar.class, JComponent.class);


        ///////////////////////////////
        // Containers/Panels/Layouts //
        ///////////////////////////////

        // -- Containers --

        addB("alltransp", "Makes self and all children transparent (recursive)", null, (value, cmp, allHints) -> {
            Container cont = (Container) cmp;
            ((JComponent) cmp).setOpaque(false);
            allTranspChildren(cont);
        }, Window.class, JComponent.class);

        addB("chtransp", "Makes all children transparent (recursive)", null, (value, cmp, allHints) -> {
            Container cont = (Container) cmp;
            allTranspChildren(cont);
        }, Window.class, JComponent.class);

        // -- Panels --

        addN("debugcolor", "UI debug color", null, (value, cmp, allHints) -> {
            RPanel pnl = (RPanel) cmp;
            pnl.setDebugColorEnabled(true);
        }, RPanel.class);

        addN("debugticks", "UI debug ticks", null, (value, cmp, allHints) -> {
            RPanel pnl = (RPanel) cmp;
            pnl.setDebugTicksEnabled(true);
        }, RPanel.class);

        addN("gradient", "Gradient colors", null, (value, cmp, allHints) -> {
            String clr1 = allHints.get("gradclr1");   // TODO: A problem with one of these throwing an
            String clr2 = allHints.get("gradclr2");   // exception below can affect the other one.
            if(cmp instanceof GradientPanel) {
                if(clr1 != null && clr2 == null) {
                    ((GradientPanel) cmp).setColor(clr(clr1));
                } else if(clr2 != null && clr1 == null) {
                    ((GradientPanel) cmp).setColor(clr(clr2));
                } else if(clr2 != null && clr1 != null) {
                    ((GradientPanel) cmp).setColors(clr(clr1), clr(clr2));
                }
            } else {
                if(clr1 != null && clr2 == null) {
                    ((GradientLabel) cmp).setLeftColor(clr(clr1));
                } else if(clr2 != null && clr1 == null) {
                    ((GradientLabel) cmp).setRightColor(clr(clr2));
                } else if(clr2 != null && clr1 != null) {
                    ((GradientLabel) cmp).setColors(clr(clr1), clr(clr2));
                }
            }
        }, GradientPanel.class, GradientLabel.class);

        addN("darkt", "Dark background with black top border", null, (value, cmp, allHints) -> {
            ((JPanel) cmp).setBorder(Lay.mb("1b", Color.black));
            cmp.setBackground(ColorLib.GRAY_DARK);
        }, JPanel.class);

        addN("darkb", "Dark background with black bottom border", null, (value, cmp, allHints) -> {
            ((JPanel) cmp).setBorder(Lay.mb("1t", Color.black));
            cmp.setBackground(ColorLib.GRAY_DARK);
        }, JPanel.class);

        addN("bordered", "Bordered panel or label", null, (value, cmp, allHints) -> {
            String bfg = allHints.get("bfg");   // Same as fg
            String bbg = allHints.get("bbg");
            String bbd = allHints.get("bbd");   // TODO: A problem with one of these throwing an
            String arc = allHints.get("arc");   // exception below can affect all the others.
            String bmg = allHints.get("bmg");

            if(cmp instanceof BorderedPanel) {
                if(bbg != null) {
                    ((BorderedPanel) cmp).setBubbleBackgroundColor(clr(bbg));
                }
//                if(bfg != null) {
//                    ((BorderedPanel) cmp).setBubbleForegroundColor(clr(bfg));     // Labels only
//                }
                if(bbd != null) {
                    ((BorderedPanel) cmp).setBubbleBorderColor(clr(bbd));
                }
                if(arc != null) {
                    ((BorderedPanel) cmp).setCornerArc(Integer.parseInt(arc));
                }
                if(bmg != null) {
                    ((BorderedPanel) cmp).setBorderMargin(Integer.parseInt(bmg));
                }

            } else {
                if(bbg != null) {
                    ((BorderedLabel) cmp).setBubbleBackgroundColor(clr(bbg));
                }
                if(bfg != null) {
                    ((BorderedLabel) cmp).setBubbleForegroundColor(clr(bfg));
                }
                if(bbd != null) {
                    ((BorderedLabel) cmp).setBubbleBorderColor(clr(bbd));
                }
                // Arc not changeable for BorderLabels due to
            }
        }, BorderedPanel.class, BorderedLabel.class);

        // -- Layouts --

        addN("nogap", "Removes horiz and vert gap", null, (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof FlowLayout) {
                ((FlowLayout) mgr).setHgap(0);
                ((FlowLayout) mgr).setVgap(0);
            } else if(mgr instanceof WrapLayout) {
                ((WrapLayout) mgr).setHgap(0);
                ((WrapLayout) mgr).setVgap(0);
            } else if(mgr instanceof GridLayout) {
                ((GridLayout) mgr).setHgap(0);
                ((GridLayout) mgr).setVgap(0);
            } else if(mgr instanceof BorderLayout) {
                ((BorderLayout) mgr).setHgap(0);
                ((BorderLayout) mgr).setVgap(0);
            }
        }, Container.class, LayoutManager.class);

        addN("gap", "Horiz and vert gap", i(), (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof FlowLayout) {
                ((FlowLayout) mgr).setHgap(intVal(value));
                ((FlowLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof WrapLayout) {
                ((WrapLayout) mgr).setHgap(intVal(value));
                ((WrapLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof GridLayout) {
                ((GridLayout) mgr).setHgap(intVal(value));
                ((GridLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof BorderLayout) {
                ((BorderLayout) mgr).setHgap(intVal(value));
                ((BorderLayout) mgr).setVgap(intVal(value));
            }
        }, Container.class, LayoutManager.class);

        addN("hgap", "Horizontal gap", i(), (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof FlowLayout) {
                ((FlowLayout) mgr).setHgap(intVal(value));
            } else if(mgr instanceof WrapLayout) {
                ((WrapLayout) mgr).setHgap(intVal(value));
            } else if(mgr instanceof GridLayout) {
                ((GridLayout) mgr).setHgap(intVal(value));
            } else if(mgr instanceof BorderLayout) {
                ((BorderLayout) mgr).setHgap(intVal(value));
            }
        }, Container.class, LayoutManager.class);

        addN("vgap", "Vertical gap", i(), (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof FlowLayout) {
                ((FlowLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof WrapLayout) {
                ((WrapLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof GridLayout) {
                ((GridLayout) mgr).setVgap(intVal(value));
            } else if(mgr instanceof BorderLayout) {
                ((BorderLayout) mgr).setVgap(intVal(value));
            }
        }, Container.class, LayoutManager.class);

        addN("agaps", "Auto create gaps", b(), (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof GroupLayout) {
                ((GroupLayout) mgr).setAutoCreateGaps(boolVal(value));
            }
        }, Container.class, GroupLayout.class);

        addN("acgaps", "Auto create container gaps", b(), (value, cmp, allHints) -> {
            LayoutManager mgr;
            if(cmp instanceof LayoutManager) {
                mgr = (LayoutManager) cmp;
            } else {
                mgr = ((Container) cmp).getLayout();
            }

            if(mgr instanceof GroupLayout) {
                ((GroupLayout) mgr).setAutoCreateContainerGaps(boolVal(value));
            }
        }, Container.class, GroupLayout.class);


        /////////////
        // Buttons //
        /////////////

        addN("mn", "Mnemonic", s(), (value, cmp, allHints) -> {
            ((AbstractButton) cmp).setMnemonic(textVal(value).charAt(0));
        }, AbstractButton.class);

        addN("closer", "Adds a listener that makes a button close its window", null, (value, cmp, allHints) -> {
            ((AbstractButton) cmp).addActionListener(e -> {
                Window win = GuiUtil.win((Component) e.getSource());
                RWindow rWin = (RWindow) win;
                rWin.close();
            });
        }, AbstractButton.class);

        addN("selected", "Selected-ness", b(), (value, cmp, allHints) -> {
            ((AbstractButton) cmp).setSelected(boolVal(value));
        }, AbstractButton.class);

        addN("nopaint", "Content area not filled", b(), (value, cmp, allHints) -> {
            ((JButton) cmp).setContentAreaFilled(!boolVal(value));
        }, JButton.class);

        addN("icon", "Icon only", b(), (value, cmp, allHints) -> {
            if(boolVal(value)) {
                ((IconButton) cmp).toImageOnly();
            }
        }, IconButton.class);


        ///////////
        // Menus //
        ///////////

        addW("delay", "Popup delay", i(), (value, cmp, allHints) -> {
            ((JMenu) cmp).setDelay(intVal(value));
        }, JMenu.class);


        ////////////////////////////
        // Labels/Text Components //
        ////////////////////////////

        addN("dm", "Displayed mnemonic", s(), (value, cmp, allHints) -> {
            ((JLabel) cmp).setDisplayedMnemonic(textVal(value).charAt(0));
        }, JLabel.class);

        addN("dmi", "Displayed mnemonic index", i(), (value, cmp, allHints) -> {
            if(cmp instanceof JLabel) {
                ((JLabel) cmp).setDisplayedMnemonicIndex(intVal(value));
            } else {
                ((AbstractButton) cmp).setDisplayedMnemonicIndex(intVal(value));
            }
        }, JLabel.class, AbstractButton.class);

        addN("editable", "Editable", b(), (value, cmp, allHints) -> {
            if(cmp instanceof JTextComponent) {
                ((JTextComponent) cmp).setEditable(boolVal(value));
            } else if(cmp instanceof JComboBox) {
                ((JComboBox) cmp).setEditable(boolVal(value));
            } else if(cmp instanceof JCheckBox) {
                if(!boolVal(value)) {    // Can't undo
                    JCheckBox chk = (JCheckBox) cmp;
                    MouseListener[] ml = chk.getListeners(MouseListener.class);
                    for(int i = 0; i < ml.length; i++) {
                        chk.removeMouseListener( ml[i] );
                    }
                    InputMap im = chk.getInputMap();
                    im.put(KeyStroke.getKeyStroke("SPACE"), "none");
                    im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
                } else {
                    // ??
                }
            } else if(cmp instanceof JSpinner) {    // Another inconsistently implemented control...
                JSpinner spn = (JSpinner) cmp;
                boolean editable = boolVal(value);
                ((NumberEditor) spn.getEditor()).getTextField().setEditable(editable);
                ((NumberEditor) spn.getEditor()).getTextField().setBackground(Color.white);
                spn.setEnabled(false);
                ((NumberEditor) spn.getEditor()).getTextField().setForeground(Color.black);
            }
        }, JTextComponent.class, JComboBox.class, JCheckBox.class, JSpinner.class);

        addN("cols", "Columns", i(), (value, cmp, allHints) -> {
            ((JTextField) cmp).setColumns(intVal(value));
        }, JTextField.class);

        addN("wrap", "Line wrap", b(), (value, cmp, allHints) -> {
            ((JTextArea) cmp).setLineWrap(boolVal(value));
        }, JTextArea.class);

        addW("ruler", "Ruler visibility", b(), (value, cmp, allHints) -> {
            if(cmp instanceof REditor) {
                ((REditor) cmp).setShowRuler(boolVal(value));
            } else {
                ((RScrollPane) cmp).setShowRuler(boolVal(value));
            }
        }, REditor.class, RScrollPane.class);

        addN("halign", "Horizontal alignment", c("ENUM: <POS>"), (value, cmp, allHints) -> {
            Map<String, Integer> consts = swingAlignmentConsts();
            int alignment = consts.get(value.toLowerCase());

            if(cmp instanceof JLabel) {
                ((JLabel) cmp).setHorizontalAlignment(alignment);
            } else {
                ((JTextField) cmp).setHorizontalAlignment(alignment);
            }
        }, JLabel.class, JTextField.class);

        addN("valign", "Vertical alignment", c("ENUM: <POS>"), (value, cmp, allHints) -> {
            if(value.equalsIgnoreCase("top")) {
                ((JLabel) cmp).setVerticalAlignment(SwingConstants.TOP);   // TODO use map
            } else if(value.equalsIgnoreCase("center")) {
                ((JLabel) cmp).setVerticalAlignment(SwingConstants.CENTER);
            } else if(value.equalsIgnoreCase("bottom")) {
                ((JLabel) cmp).setVerticalAlignment(SwingConstants.BOTTOM);
            }
        }, JLabel.class);

        addN("right", "Right horizontal alignment", null, (value, cmp, allHints) -> {
            if(cmp instanceof JTextField) {
                ((JTextField) cmp).setHorizontalAlignment(SwingConstants.RIGHT);
            } else if(cmp instanceof JLabel) {
                ((JLabel) cmp).setHorizontalAlignment(SwingConstants.RIGHT);
            }
        }, JLabel.class, JTextField.class);

        addN("validate", "Triggers validation", null, (value, cmp, allHints) -> {
            ((ValidatingTextField) cmp).triggerValidation();
        }, ValidatingTextField.class);

        addN("selectall", "Selects all text", b(), (value, cmp, allHints) -> {
            if(cmp instanceof RTextField) {
                ((RTextField) cmp).setSelectAll(boolVal(value));
            } else if(cmp instanceof RComboBox) {
                ((RComboBox) cmp).setSelectAll(boolVal(value));
            } else if(cmp instanceof RTextPane) {
                ((RTextPane) cmp).setSelectAll(boolVal(value));
            }
        }, RTextField.class, RComboBox.class, RTextPane.class);

        addN("visrows", "Maximum visible row count", i(), (value, cmp, allHints) -> {
            ((RComboBox) cmp).setMaximumRowCount(intVal(value));
        }, RComboBox.class);

        addN("selectable", "Selection changeable", b(), (value, cmp, allHints) -> {
            ((RComboBox) cmp).setSelectionChangeable(boolVal(value));
        }, RComboBox.class);


        ////////////////////////
        // Lists/Tables/Trees //
        ////////////////////////

        addN("seltype", "Selection type", c("ENUM: <SEL>"), (value, cmp, allHints) -> {
            Map<String, Integer> listTypes = new HashMap<>();
            listTypes.put("single", ListSelectionModel.SINGLE_SELECTION);
            listTypes.put("range",  ListSelectionModel.SINGLE_INTERVAL_SELECTION);
            listTypes.put("multi",  ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            Map<String, Integer> treeTypes = new HashMap<>();
            treeTypes.put("single", TreeSelectionModel.SINGLE_TREE_SELECTION);
            treeTypes.put("range",  TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
            treeTypes.put("multi",  TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

            value = value.toLowerCase();

            if(cmp instanceof JList) {
                JList lst = (JList) cmp;
                int sel = listTypes.get(value);
                lst.setSelectionMode(sel);

            } else if(cmp instanceof JTable) {
                JTable tbl = (JTable) cmp;
                int sel = listTypes.get(value);
                tbl.setSelectionMode(sel);

            } else if(cmp instanceof RTablePanel) {
                RTablePanel pnl = (RTablePanel) cmp;
                int sel = listTypes.get(value);
                pnl.getTable().setSelectionMode(sel);

            } else if(cmp instanceof JTree) {
                JTree tre = (JTree) cmp;
                int sel = treeTypes.get(value);
                tre.getSelectionModel().setSelectionMode(sel);
            }
        }, JList.class, JTable.class, RTablePanel.class, JTree.class);

        addN("sortable", "Auto create row sorter", b(), (value, cmp, allHints) -> {
            ((JTable) cmp).setAutoCreateRowSorter(boolVal(value));
        }, JTable.class);

        addN("lasso", "Mouse drag lasso", b(), (value, cmp, allHints) -> {
            ((RTree) cmp).setMouseDragSelection(boolVal(value));
        }, RTree.class);

        addN("rh", "Row height", i(), (value, cmp, allHints) -> {
            ((JTable) cmp).setRowHeight(intVal(value));
        }, JTable.class);

        addN("rowheight", "Row height", i(), (value, cmp, allHints) -> {
            ((JTable) cmp).setRowHeight(intVal(value));
        }, JTable.class);

        addN("insets", "Insets", c("INT; MCODE"), (value, cmp, allHints) -> {
            if(cmp instanceof RTable) {
                if(NumUtil.isInt(value)) {
                    ((RTable) cmp).getCellRenderer().setInsetsRenderer(NumUtil.i(value));
                } else {
                    Sides sides = new Sides(value);
                    Insets insets = new Insets(sides.top, sides.left, sides.bottom, sides.right);
                    ((RTable) cmp).getCellRenderer().setInsetsRenderer(insets);
                }
            } else {
                ((IconButton) cmp).setInsets(NumUtil.i(value));
            }
        }, RTable.class, IconButton.class);

        addN("rootvisible", "Whether root node is shown", b(), (value, cmp, allHints) -> {
            ((JTree) cmp).setRootVisible(boolVal(value));
        }, JTree.class);


        //////////////////
        // Scroll Panes //
        //////////////////

        addN("vspeed", "Vertical scroll bar increment => 16", null, (value, cmp, allHints) -> {
            ((JScrollPane) cmp).getVerticalScrollBar().setUnitIncrement(16);
        }, JScrollPane.class);

        addN("hspeed", "Horizontal scroll bar increment => 16", null, (value, cmp, allHints) -> {
            ((JScrollPane) cmp).getHorizontalScrollBar().setUnitIncrement(16);
        }, JScrollPane.class);

        addN("hsb", "Horizontal scroll bar policy", c("ENUM: <POLICY>"), (value, cmp, allHints) -> {
            if(value.equalsIgnoreCase("always")) {             // TODO: convert to map for readability
                ((JScrollPane) cmp)
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            } else if(value.equalsIgnoreCase("never")) {
                ((JScrollPane) cmp)
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            } else if(value.equalsIgnoreCase("asneeded")) {
                ((JScrollPane) cmp)
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            }
        }, JScrollPane.class);

        addN("vsb", "Vertical scroll bar policy", c("ENUM: <POLICY>"), (value, cmp, allHints) -> {
            if(value.equalsIgnoreCase("always")) {          // TODO: convert to map for readability
                ((JScrollPane) cmp)
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            } else if(value.equalsIgnoreCase("never")) {
                ((JScrollPane) cmp)
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            } else if(value.equalsIgnoreCase("asneeded")) {
                ((JScrollPane) cmp)
                    .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            }
        }, JScrollPane.class);


        /////////////////
        // Split Panes //
        /////////////////

        addN("divpixel", "Divider location by pixel", i(), (value, cmp, allHints) -> {
            ((JSplitPane) cmp).setDividerLocation(intVal(value));
        }, JSplitPane.class);

        addN("divratio", "Divider location by ratio", d(), (value, cmp, allHints) -> {
            ((JSplitPane) cmp).setDividerLocation(Double.parseDouble(value));
        }, JSplitPane.class);

        addN("resizew", "Resize weight", d(), (value, cmp, allHints) -> {
            ((JSplitPane) cmp).setResizeWeight(Double.parseDouble(value));
        }, JSplitPane.class);


        //////////////////
        // Tabbed Panes //
        //////////////////

        addN("tabs", "Tab area placement", c("ENUM: <POS>"), (value, cmp, allHints) -> {
            if(value.equalsIgnoreCase("top")) {
                ((JTabbedPane) cmp).setTabPlacement(JTabbedPane.TOP);  // TODO: convert to map for readability
            } else if(value.equalsIgnoreCase("left")) {
                ((JTabbedPane) cmp).setTabPlacement(JTabbedPane.LEFT);
            } else if(value.equalsIgnoreCase("bottom")) {
                ((JTabbedPane) cmp).setTabPlacement(JTabbedPane.BOTTOM);
            } else if(value.equalsIgnoreCase("right")) {
                ((JTabbedPane) cmp).setTabPlacement(JTabbedPane.RIGHT);
            }
        }, JTabbedPane.class);

        addN("borders", "Use larger header margins or not", b(), (value, cmp, allHints) -> {
            ((RTabbedPane) cmp).setUseBorders(boolVal(value));
        }, RTabbedPane.class);

        addN("closeable", "Tabs closeable by default", b(), (value, cmp, allHints) -> {
            ((RTabbedPane) cmp).getDefaultTabCreationDescriptor().setCloseable(boolVal(value));
        }, RTabbedPane.class);


        /////////////
        // Windows //
        /////////////

        addW("statusbar", "Toggle status bar", b(), (value, cmp, allHints) -> {
            ((NotificationWindow) cmp).setShowStatusBar(boolVal(value));
        }, NotificationWindow.class);

        addW("db", "Default button of a window (responds to ENTER key)", s("CAPTION"), (value, cmp, allHints) -> {
            if(cmp instanceof Dialog) {
                JDialog dlg = (JDialog) cmp;
                dlg.getRootPane().setDefaultButton(GuiUtil.findButton(dlg.getContentPane(), textVal(value)));
            } else {
                JFrame fra = (JFrame) cmp;
                fra.getRootPane().setDefaultButton(GuiUtil.findButton(fra.getContentPane(), textVal(value)));
            }
        }, JFrame.class, JDialog.class);

        addW("undecorated", "Undecorated", b(), (value, cmp, allHints) -> {
            if(cmp instanceof Dialog) {
                ((JDialog) cmp).setUndecorated(boolVal(value));
            } else {
                ((JFrame) cmp).setUndecorated(boolVal(value));
            }
        }, JFrame.class, JDialog.class);

        addW("pack", "Packs window", null, (value, cmp, allHints) -> {
            if(cmp instanceof Window) {
                ((Window) cmp).pack();
            }
        }, Window.class);

        addW("dco", "Default close operation", c("ENUM: <DCO>"), (value, cmp, allHints) -> {
            if(cmp instanceof Dialog) {
                // Exit on close not allowed for dialogs.
                if(value.equalsIgnoreCase("dispose")) {
                    ((JDialog) cmp).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                } else if(value.equalsIgnoreCase("nothing")) {
                    ((JDialog) cmp).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                } else if(value.equalsIgnoreCase("hide")) {
                    ((JDialog) cmp).setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                }
            } else {
                if(value.equalsIgnoreCase("exit")) {
                    ((JFrame) cmp).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                } else if(value.equalsIgnoreCase("dispose")) {
                    ((JFrame) cmp).setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                } else if(value.equalsIgnoreCase("nothing")) {
                    ((JFrame) cmp).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                } else if(value.equalsIgnoreCase("hide")) {
                    ((JFrame) cmp).setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
                }
            }
        }, JFrame.class, JDialog.class);

        addW("resizable", "Resizable", b(), (value, cmp, allHints) -> {
            if(cmp instanceof Dialog) {
                ((Dialog) cmp).setResizable(boolVal(value));
            } else {
                ((Frame) cmp).setResizable(boolVal(value));
            }
        }, JFrame.class, JDialog.class);

        addW("toplevel", "Marks a window as the app's top-level window", null, (value, cmp, allHints) -> {
            ((RWindow) cmp).setShutdownSwingTimersOnClose(true);
        }, RWindow.class);


        ///////////
        // Other //
        ///////////

        addN("label", "Table label", s(), (value, cmp, allHints) -> {
            ((RTablePanel) cmp).setTableLabel(textVal(value));
        }, RTablePanel.class);

        addN("expanded", "Options expanded", b(), (value, cmp, allHints) -> {
            ((RTablePanel) cmp).setOptionsExpanded(boolVal(value));
        }, RTablePanel.class);

        addN("pos", "Options position", c("ENUM: <TPOS>"), (value, cmp, allHints) -> {
            Map<String, Integer> listTypes = new HashMap<>();
            listTypes.put("N",     SwingConstants.NORTH);
            listTypes.put("NORTH", SwingConstants.NORTH);
            listTypes.put("S",     SwingConstants.SOUTH);
            listTypes.put("SOUTH", SwingConstants.SOUTH);

            if(value != null) {
                Integer v = listTypes.get(value.toUpperCase());
                if(v != null) {
                    ((RTablePanel) cmp).setOptionsPosition(v);
                    return;
                }
            }
            ((RTablePanel) cmp).setOptionsPosition(intVal(value));
        }, RTablePanel.class);
    }


    ////////////
    // HELPER //
    ////////////

    private static Map<String, Integer> swingAlignmentConsts() {
        Map<String, Integer> alignments = new HashMap<>();
        alignments.put("left",     SwingConstants.LEFT);
        alignments.put("center",   SwingConstants.CENTER);
        alignments.put("right",    SwingConstants.RIGHT);
        alignments.put("leading",  SwingConstants.LEADING);
        alignments.put("trailing", SwingConstants.TRAILING);
        return alignments;
    }

    private static int[] force2(int[] values) {
        if(values.length == 1) {
            return new int[] {values[0], values[0]};
        } else if(values.length >= 2) {
            return new int[] {values[0], values[1]};
        }
        return null;
    }
    private static String textVal(String value) {
        return value.replaceAll("-", " ");
    }
    private static int intVal(String value) {
        return Integer.parseInt(value);
    }
    private static float floatVal(String value) {
        return Float.parseFloat(value);
    }
    private static boolean boolVal(String value) {
        if(value == null) {
            value = "true";
        }
        value = value.toLowerCase();
        return Boolean.parseBoolean(value);
    }

    private static void allTranspChildren(Container parent) {
        for(int c = 0; c < parent.getComponentCount(); c++) {
            Component cmp = parent.getComponent(c);
            if(cmp instanceof JPanel || cmp instanceof JLabel ||
                            cmp instanceof JCheckBox || cmp instanceof JRadioButton) {
                ((JComponent) cmp).setOpaque(false);
            }
            if(cmp instanceof Container) {
                allTranspChildren((Container) cmp);
            }
        }
    }

    public static Map<String, LayHint> getProcessors() {
        return hints;
    }


    //////////////////
    // GLOBAL HINTS //
    //////////////////

    private static HintList globalHints = new HintList();
    public static void addGlobalHints(String hints) {
        globalHints.addHints(parseHints(hints));
    }
    public static void addGlobalHint(String K, String V) {
        globalHints.add(K, V);
    }
    public static void clearGlobalHints() {
        globalHints.clear();
    }
    public static HintList getGlobalHints() {
        return globalHints;
    }

    // Valid formats supported by the regex:
    // key => For center
    // key=val => For enabled=false, bg=yellow
    // key=[N,N,N] => For bg=[R,G,B], size=[W,H]
    // key=val(val) => For augb=eb(code)
    // key=val(val,val) => For augb=mb(code,clr)
    // key=val(val,[N,N,N]) => For augb=mb(code,[R,G,B])
    public static HintList parseHints(String s) {
        s = s.trim();
        s = StringUtil.leftTrim(s, ',');
        s = StringUtil.rightTrim(s, ',');

        HintList hints = new HintList(); // Order that hints appear in the string is important
        String keyValuePat =
            "!?[a-zA-Z0-9_]+\\s*(?:=\\s*(?:[-_a-zA-Z0-9#\\.]+(?:\\([_a-zA-Z0-9#]+(?:,(?:[_a-zA-Z0-9#]+|\\[[^\\]]*\\]))?\\))?|\\[[^\\]]*\\]))?";
        Pattern p = Pattern.compile("\\s*" + keyValuePat + "(?:\\s*,\\s*" + keyValuePat + ")*\\s*");
        Matcher m = p.matcher(s);
        if(m.matches()) {
            Pattern p2 = Pattern.compile("(" + keyValuePat + ")");
            m = p2.matcher(s);
            while(m.find()) {
                String kv = m.group(1);
                int eq = kv.indexOf('=');
                String key, value;
                if(eq != -1) {
                    key = kv.substring(0, eq).trim();
                    value = kv.substring(eq + 1).trim();
                } else {
                    key = kv;
                    if(key.startsWith("!")) {
                        key = StringUtil.snip(key, 1);
                        value = "false";
                    } else {
                        value = null;
                    }
                }
                hints.add(key, value);
            }
        }
        return hints;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        HintList hl = parseHints("eb=4,");
//        System.out.println(hl);
//        if(true) {
//            return;
//        }

        List<LayHint> hintList = new ArrayList<>(hints.values());
        int nmLen = StringUtil.maxLength(hintList, "name");
        int aLen = 6;
        int pLen = StringUtil.maxLength(hintList, o -> {
            return o.params == null ? StringLib.NONE : o.params.toString();
        });
        int tLen = StringUtil.maxLength(hintList, o -> {
            return typeStr(o);
        });
        int dLen = StringUtil.maxLength(hintList, o -> {
            String d = o.description;
            return d == null ? StringLib.NONE : d;
        });
        String format = "%-" + nmLen  + "s  %-" + aLen + "s  %-" + pLen + "s  %-" + tLen + "s  %-" + dLen + "s%n";
        System.out.printf(format, "Name", "Applic", "Param", "Targets", "Description");
        System.out.printf(format, "====", "======", "=====", "=======", "===========");
        for(LayHint hint : hintList) {
            String d = hint.description;
            d = (d == null ? StringLib.NONE : d);
            String applic;
            if(hint.applicableTo == LayHint.WIN) {
                applic = "WIN";
            } else if(hint.applicableTo == LayHint.NONWIN) {
                applic = "NONWIN";
            } else {
                applic = "BOTH";
            }
            System.out.printf(
                format,
                hint.name,
                applic,
                hint.params == null ? StringLib.NONE : hint.params.toString(),
                typeStr(hint),
                d
            );
        }
    }

    private static String typeStr(LayHint h) {
        Set<String> classNames = new TreeSet<>();
        for(Class<?> clazz : h.applicableTargetClasses) {
            String clName;
            if(clazz.equals(Component.class)) {
                clName = "(Any)";
            } else if(clazz.equals(Window.class)) {
                clName = "(Win)";
            } else {
                clName = clazz.getSimpleName();
            }
            classNames.add(clName);
        }
        StringBuilder buffer = new StringBuilder();
        for(String clName : classNames) {
            buffer.append(clName);
            buffer.append(", ");
        }
        String str = buffer.toString();
        return StringUtil.cut(str, ", ");
    }
}
