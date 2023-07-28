package replete.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import replete.text.StringUtil;
import replete.ui.lay.Lay;
import replete.ui.panels.UiDebuggable;
import replete.ui.uidebug.UiDebugUtil;
import replete.ui.windows.common.RWindow;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.ClassUtil;
import replete.util.ReflectionUtil;

public class GuiUtil {

    // For most cases, images are a known and integral part of the software, and
    // we want to alert developers as soon as possible that an image was not found.
    private static boolean DEFAULT_PROVIDE_MISSING_IMAGE = true;

    public static void enableTabsHighlighted() {
        UIDefaults def = UIManager.getLookAndFeelDefaults();
        def.put("TabbedPane.unselectedBackground", Lay.clr("210") );
        def.put("TabbedPane.selected", Lay.clr("FFFFBC"));
    }
    private static boolean safeDisabled = false;
    public static void disableSafe() {
        safeDisabled = true;
    }

    public static String checkFont(String fontFamily) {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = g.getAvailableFontFamilyNames();
        for(int i = 0; i < fonts.length; i++) {
            if(fonts[i].equalsIgnoreCase(fontFamily)) {
                return fonts[i];
            }
        }
        return null;
    }

    public static void safe(final Runnable runObj) {
        if(EventQueue.isDispatchThread() || safeDisabled) {
            runObj.run();
        } else {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    runObj.run();
                }
            });
        }
    }
    public static void safeSync(final Runnable runObj) {
        if(EventQueue.isDispatchThread() || safeDisabled) {
            runObj.run();
        } else {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        runObj.run();
                    }
                });
            } catch(Exception e) {
                throw new RuntimeException("GuiUtil.safeSync: EventQueue.invokeAndWait failed.", e);
            }
        }
    }

    public static int stringWidth(Graphics g, String s) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.stringWidth(s);
    }
    public static int stringHeight(Graphics g) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.getHeight();
    }
    public static int stringAscent(Graphics g) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        return fm.getAscent();
    }

    public static <C extends Container> C getAncestorContainer(Component cmp, Class<? extends Container> type) {
        if(cmp == null) {
            return null;
        }
        Container c = cmp.getParent();
        while(c != null && !type.isAssignableFrom(c.getClass())) {
            c = c.getParent();
        }
        return (C) c;
    }

    public static JFrame getOwnerFrame(JComponent component) {
        return (JFrame) component.getTopLevelAncestor();
    }

    public static ImageIcon createMissingImage() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, 16, 16);
        g.setColor(Color.red);

        g.drawRect(0, 0, 15, 15);
        g.drawRect(1, 1, 13, 13);

        g.drawLine(1, 0, 15, 14);
        g.drawLine(0, 0, 15, 15);
        g.drawLine(0, 1, 14, 15);

        g.drawLine(14, 0, 0, 14);
        g.drawLine(15, 0, 0, 15);
        g.drawLine(15, 1, 1, 15);

        return new ImageIcon(bi);
    }
    public static ImageIcon createBlankImage() {
        BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.getGraphics();
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, 16, 16);
        return new ImageIcon(bi);
    }

    private static Map<URL, ImageIcon> loadedImages = new HashMap<>();
    private static ImageIcon missingImage = createMissingImage();

    private static ImageIcon loadImage(String name, URL imageUrl, boolean provideMissingImage) {
        if(imageUrl == null) {
            if(provideMissingImage) {
                System.err.println("Could not find image '" + name + "'.");
                return missingImage;
            }
            return null;
        }

        ImageIcon icon = loadedImages.get(imageUrl);
        if(icon == null) {
            icon = new ImageIcon(imageUrl);
            loadedImages.put(imageUrl, icon);
        }
        return icon;
    }

    /**
     * Returns an image specified by the path given when
     * that path is an absolute path in relation to the
     * class files.
     */

    public static ImageIcon getImage(String imgPath) {
        URL imageURL = ClassLoader.getSystemClassLoader().getResource(imgPath);
        return loadImage(imgPath, imageURL, DEFAULT_PROVIDE_MISSING_IMAGE);
    }

    /**
     * Returns an image specified by the path given when
     * that path is relative to the class that is calling
     * this method.
     */

    public static ImageIcon getImage(Class<?> clazz, String imgPath) {
        URL imageUrl = clazz.getResource(imgPath);
        return loadImage(imgPath, imageUrl, DEFAULT_PROVIDE_MISSING_IMAGE);
    }

    public static ImageIcon getImageLocal(String imgPath) {
        return getImageLocal(imgPath, DEFAULT_PROVIDE_MISSING_IMAGE);
    }
    public static ImageIcon getImageLocal(String imgPath, boolean provideMissingImage) {
        Class<?> classCallingMe = ClassUtil.getCallingClass(new Class<?>[] {GuiUtil.class});
        URL imageURL = classCallingMe.getResource(imgPath);
        return loadImage(imgPath, imageURL, provideMissingImage);
    }

    public static void showAboutBox(JComponent comp, String imgPath, String title) {
        showAboutBox(comp, imgPath, null, title);
    }

    public static void showAboutBox(JComponent comp, String imgPath, String iconPath, String title) {
        JFrame owner = GuiUtil.getOwnerFrame(comp);
        EscapeDialog dialog = new EscapeDialog(owner, title, true);

        JButton btnAbout = new JButton();
        btnAbout.setIcon(getImage(imgPath));
        btnAbout.setMargin(new Insets(0, 0, 0, 0));
        btnAbout.setContentAreaFilled(false);
        btnAbout.setBorderPainted(false);

        if(iconPath != null) {
            //dialog.setIconImage(GuiUtil.getImage(iconPath).getImage());
        }

        dialog.add(btnAbout);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Add a component to the container with the given border and color
    // and return the JPanel constructed in case caller wants it.
    // NOTE: This method is one of earliest Replete methods to attempt to make
    // GUI creation simpler (at least in one small way).  Eventually after
    // all uses of this method are converted to Lay patterns, this method
    // can be removed.
    public static JPanel addBorderedComponent(Container cc, JComponent comp, Border b) {
        return addBorderedComponent(cc, comp, b, null);
    }

    // Add a component to the container with the given border and color
    // and return the JPanel constructed in case caller wants it.
    // NOTE: This method is one of earliest Replete methods to attempt to make
    // GUI creation simpler (at least in one small way).  Eventually after
    // all uses of this method are converted to Lay patterns, this method
    // can be removed.
    public static JPanel addBorderedComponent(Container cc, JComponent comp, Border b, String where) {
        JPanel newPanel = new JPanel(new GridLayout(1, 1));
        newPanel.add(comp);
        newPanel.setBorder(b);
        if(where == null) {
            cc.add(newPanel);
        } else {
            cc.add(newPanel, where);
        }
        return newPanel;
    }

    public static String getClockString( long totalTime ){
        if(totalTime != 0) {
            long millisec = totalTime;
            long sec = millisec / 1000L;
            long min = sec / 60;
            millisec -= sec * 1000;
            sec -= min * 60;
            String z = "";
            String m = "";
            if(sec < 10) {
                z = "0";
            }
            if(millisec < 100) {
                m = "0";
            } else if(millisec < 10) {
                m = "0";
            }
            return ( min + ":" + z + sec + "." + m + millisec );
        }
        return "0:00.000";
    }


    ////////////
    // COLORS //
    ////////////

    public static Color deriveColor(Color c, int rdel, int gdel, int bdel) {
        int newR = Math.max(0, Math.min(255, c.getRed() + rdel));
        int newG = Math.max(0, Math.min(255, c.getGreen() + gdel));
        int newB = Math.max(0, Math.min(255, c.getBlue() + bdel));
        return new Color(newR, newG, newB);
    }


    ///////////
    // SIZES //
    ///////////

    public static void setSize(JComponent comp, Dimension d) {
        comp.setPreferredSize(d);
        comp.setMinimumSize(d);
        comp.setMaximumSize(d);
    }

    public static String getSize(Component c) {
        Dimension min = c.getMinimumSize();
        Dimension pref = c.getPreferredSize();
        Dimension max = c.getMaximumSize();
        return
            "min=[" + min.width + "x" + min.height +
            "],pref=[" + pref.width + "x" + pref.height +
            "],max=[" +
                (max.width == Integer.MAX_VALUE ? "MV" : max.width) + "x" +
                (max.height == Integer.MAX_VALUE ? "MV" : max.height) + "]"
        ;
    }


    ///////////////
    // MNEMONICS //
    ///////////////

    // Methods for decorating a dialog box's buttons with mnemonics.
    // This is a utility method for other classes in this framework.
    // When choosing custom text for dialog buttons, supports "&OK"
    // syntax.  Use && to show an & (eg. "&Save && Close").  This
    // is VB6 syntax.

    public static void addMnemonics(JDialog dialog, List<String> basicCaptions) {
        addMnemonics(dialog.getContentPane(), basicCaptions);
    }
    public static void addMnemonics(Container c, List<String> basicCaptions) {
        Component[] cmps = c.getComponents();
        for(Component cmp : cmps) {
            if(cmp instanceof JButton) {
                JButton btn = (JButton) cmp;
                String text = btn.getText();

                if(basicCaptions.contains(text)) {

                    btn.setMnemonic(text.charAt(0));

                    // Prevent UI from undoing this setting.
                    btn.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent arg0) {
                            if(arg0.getPropertyName().equals("mnemonic")) {
                                if(arg0.getNewValue().equals(new Integer(0))) {
                                    JButton btn = (JButton) arg0.getSource();
                                    btn.setMnemonic(btn.getText().charAt(0));
                                }
                            }
                        }
                    });

                } else {

                    resolveMnemonics(btn);

                    // Prevent UI from undoing this setting.
                    btn.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent arg0) {
                            if(arg0.getPropertyName().equals("mnemonic")) {
                                if(arg0.getNewValue().equals(new Integer(0))) {
                                    JButton btn = (JButton) arg0.getSource();
                                    resolveMnemonics(btn);
                                }
                            }
                        }
                    });

                }

                // Recursively searching containers for more buttons.
            } else if(cmp instanceof Container) {
                addMnemonics((Container) cmp, basicCaptions);
            }
        }
    }

    // Custom button texts support arbitrary mnemonics using
    // the & character.  The character after the first single
    // & will become the mnemonic.  Other single & will be
    // discarded.  Use a && to show an ampersand.

    // Tried to do this with regular expressions (String.replaceAll),
    // but no initially obvious answer.
    public static void resolveMnemonics(JButton btn) {
        String text = btn.getText();
        if(text == null) {
            return;
        }

        int mnIdx = -1;
        int fewerCh = 0;
        StringBuilder newText = new StringBuilder(text.length());
        for(int cIdx = 0; cIdx < text.length(); cIdx++) {
            char ch = text.charAt(cIdx);
            if(ch == '&') {
                if(cIdx == text.length() - 1) {
                    // Don't add any characters if ends with &.
                } else {
                    if(text.charAt(cIdx + 1) == '&') {
                        newText.append("&");        // Add a single &.
                        cIdx++;                     // To move past both &.
                        fewerCh++;                  // Number of ch removed.
                    } else {

                        // If we haven't found a single & before, save
                        // this location as the mnemonic location.
                        if(mnIdx == -1) {
                            mnIdx = cIdx - fewerCh;
                        }

                        // Don't add this &.
                    }
                }
            } else {
                newText.append(ch);
            }
        }

        btn.setText(newText.toString());

        if(mnIdx != -1) {
            btn.setMnemonic(newText.charAt(mnIdx));
            btn.setDisplayedMnemonicIndex(mnIdx);
        }
    }

    public static void printContents(Container c) {
        printContents(c, 0);
    }
    private static void printContents(Container c, int level) {
        System.out.println(StringUtil.spaces(level * 3) + renderComponent(c));
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                printContents((Container) cc, level + 1);
            } else {
                System.out.println(StringUtil.spaces((level + 1) * 3) + renderComponent(cc));
            }
        }
    }
    private static String renderComponent(Component cc) {
        String extra = "";

        if(cc instanceof JLabel) {
            JLabel lbl = (JLabel) cc;
            extra = lbl.getText();
        } else if(cc instanceof JButton) {
            JButton btn = (JButton) cc;
            extra = btn.getText();
        } else if(cc instanceof JPanel) {
            JPanel pnl = (JPanel) cc;
            extra = pnl.getLayout().getClass().getName();
        }

        return cc.getClass().getSimpleName() + " (" + extra + ") " + (cc.getMinimumSize() != null ? cc.getMinimumSize().getWidth() : "NULL");
    }

    public static JButton findButton(Container c, String caption) {
        JButton btn = null;

        for(Component cc : c.getComponents()) {
            if(cc instanceof JButton) {
                JButton btn2 = (JButton) cc;
                if(btn2.getText().equals(caption)) {
                    return btn2;
                }
            } else if(cc instanceof Container) {
                btn = findButton((Container) cc, caption);
                if(btn != null) {
                    break;
                }
            }
        }

        return btn;
    }

    public static void setDebugColors(Container c, boolean enabled) {
        setDebugColors(c, enabled, 0);
        ((JComponent) c).updateUI();
    }
    private static void setDebugColors(Container c, boolean enabled, int level) {
        if(c instanceof UiDebuggable) {
            ((UiDebuggable) c).setDebugColorEnabled(enabled);
        }
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                setDebugColors((Container) cc, enabled, level + 1);
            }
        }
    }
    public static void setDebugTicks(Container c, boolean enabled) {
        setDebugTicks(c, enabled, 0);
        ((JComponent) c).updateUI();
    }
    private static void setDebugTicks(Container c, boolean enabled, int level) {
        if(c instanceof UiDebuggable) {
            ((UiDebuggable) c).setDebugTicksEnabled(enabled);
        }
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                setDebugTicks((Container) cc, enabled, level + 1);
            }
        }
    }
    public static void setDebugMouse(Container c, boolean enabled) {
        setDebugMouse(c, enabled, 0);
        ((JComponent) c).updateUI();
    }
    private static void setDebugMouse(Container c, boolean enabled, int level) {
        if(c instanceof UiDebuggable) {
            ((UiDebuggable) c).setDebugMouseEnabled(enabled);
        }
        for(Component cc : c.getComponents()) {
            if(cc instanceof Container) {
                setDebugMouse((Container) cc, enabled, level + 1);
            }
        }
    }

    // Alternate methods to using SelectAllTextField.

    public static void makeSelectAllOnFocus(final JTextField txt) {
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txt.selectAll();
            }
        });
    }

    // Alternate methods to using EscapeFrame and EscapeDialog.

    public static void makeEscapeClose(final RootPaneContainer win) {
        makeEscapeClose(win, null);
    }
    public static void makeEscapeClose(final RootPaneContainer win, ActionListener listener) {
        JRootPane rp = win.getRootPane();

        if(listener == null) {
            listener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if(win instanceof Window) {
                        ((Window)win).dispose();
                    }
                }
            };
        }

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rp.registerKeyboardAction(listener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Returns the preferred size to set a component at in order to render
     * an HTML string.  You can specify the size of one dimension.
     * A Swing annoyance is that laying out a JLabel with an HTML string
     * inside is not easily accomplished with precision.  This method
     * helps change that.  Typical use would be:
     *    lbl = new JLabel("<html>something</html>");
     *    lbl.setPreferredSize(getHTMLJLabelPreferredSize(lbl, 500, true));
     * Reference:
     *    http://www.nobel-joergensen.com/roller/java/entry/changing_preferred_size_of_a
     * */
    public static Dimension getHTMLJLabelPreferredSize(JComponent lbl, //JLabel lbl,
                         int prefSize, boolean sizeIsWidth) {

        View view = (View) lbl.getClientProperty(BasicHTML.propertyKey);

        // If the label doesn't have any HTML in it...
        if(view == null) {
            return null;
        }

        view.setSize(sizeIsWidth ? prefSize : 0,sizeIsWidth ? 0 : prefSize);

        float w = view.getPreferredSpan(View.X_AXIS);
        float h = view.getPreferredSpan(View.Y_AXIS);

        return new Dimension((int) Math.ceil(w), (int) Math.ceil(h));
    }

    public static void color(JComponent cmp, Color c) {
        cmp.setOpaque(true);
        cmp.setBackground(c);
    }

    public static Dimension scaleContainedDim(Dimension target, Dimension bounds) {
        int tWidth = target.width;
        int tHeight = target.height;
        int bWidth = bounds.width;
        int bHeight = bounds.height;
        int sWidth;
        int sHeight;

        double maxRatio = (double) bWidth / bHeight;
        double srcRatio = (double) tWidth / tHeight;

        if(srcRatio > maxRatio) {
            sWidth = bWidth;
            sHeight = (int)((double) bWidth * tHeight / tWidth);
        } else {
            sHeight = bHeight;
            sWidth = (int)((double) bHeight * tWidth / tHeight);
        }

        if(tWidth <= sWidth && tHeight <= sHeight) {
            return new Dimension(tWidth, tHeight);
        }

        return new Dimension(sWidth, sHeight);
    }

    public static Point centerContainedDim(Dimension target, Dimension bounds) {
        int tWidth = target.width;
        int tHeight = target.height;
        int bWidth = bounds.width;
        int bHeight = bounds.height;

        if(tWidth <= bWidth && tHeight <= bHeight) {
            return new Point((bWidth - tWidth) / 2, (bHeight - tHeight) / 2);
        } else if(tWidth <= bWidth) {
            return new Point((bWidth - tWidth) / 2, 0);
        } else if(tHeight <= bHeight) {
            return new Point(0, (bHeight - tHeight) / 2);
        }
        return new Point(0, 0);
    }

    public static void drawCenteredShape(Graphics g, boolean oval, boolean fill, int x, int y, int w, int h) {
        if(w <= 0 || h <= 0) {
            return;
        }
        if(w == 1 || h == 1) {
            oval = false;    // Weirdness where ovals wont draw a pixel if w=1, h=1
        }
        int cx = x - w / 2;
        int cy = y - h / 2;
        int cw = w - 1;
        int ch = h - 1;
        if(fill) {
            if(oval) {
                g.fillOval(cx, cy, cw + 1, ch + 1);   // Fix for known Java bug
            } else {
                g.fillRect(cx, cy, cw + 1, ch + 1);   // Fix for known Java bug
            }
        } else {
            if(oval) {
                g.drawOval(cx, cy, cw, ch);
            } else {
                g.drawRect(cx, cy, cw, ch);
            }
        }
    }

    public static void ensureOnScreen(RWindow win, boolean completely) {
        int[] bounds = getCurrentScreenBounds();
        int bLeft   = bounds[0];
        int bRight  = bounds[1];
        int bTop    = bounds[2];
        int bBottom = bounds[3];
        int bWidth  = bounds[4];
        int bHeight = bounds[5];

        int w = win.getWidth();
        int h = win.getHeight();

        // For both cases, never let the dimensions be greater
        // than the screen bounds
        if(w > bWidth) {
            w = bWidth;
        }
        if(h > bHeight) {
            h = bHeight;
        }

        int x = win.getX();
        int y = win.getY();
        int r = x + w - 1;
        int b = y + h - 1;

        // Make sure that the window is completely on the screen.
        if(completely) {
            if(r > bRight) {
                x = bRight - w + 1; // Move so entire window is just inside the right-hand side.
            } else if(x < bLeft) {
                x = bLeft;
            }
            if(b > bBottom) {
                y = bBottom - h + 1; // Move so entire window is just inside the bottom side.
            } else if(y < bTop) {
                y = bTop;
            }

        // Else !completely, so only move if the window is
        // entirely off the screen.  In other words this
        // allows the window to NOT be COMPLETELY on the screen,
        // as long as some of it is on the screen.
        } else {
            if(x > bRight) {
                x = bRight - w + 1; // Move so entire window is just inside the right-hand side.
            } else if(r < bLeft) {
                x = bLeft;
            }
            if(y > bBottom) {
                y = bBottom - h + 1; // Move so entire window is just inside the bottom side.
            } else if(b < bTop) {
                y = bTop;
            }
        }

        win.setLocation(x, y);
        win.setSize(w, h);
    }

    protected static int[] getCurrentScreenBounds() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] dvs = ge.getScreenDevices();

        int bLeft = Integer.MAX_VALUE;
        int bRight = Integer.MIN_VALUE;
        int bTop = Integer.MAX_VALUE;
        int bBottom = Integer.MIN_VALUE;

        for(GraphicsDevice dv : dvs) {
            GraphicsConfiguration dgc = dv.getDefaultConfiguration();
            Rectangle r = dgc.getBounds();
            if(r.x < bLeft) {
                bLeft = r.x;
            }
            if(r.x + r.width - 1 > bRight) {
                bRight = r.x + r.width - 1;
            }
            if(r.y < bTop) {
                bTop = r.y;
            }
            if(r.y + r.height - 1 > bBottom) {
                bBottom = r.y + r.height - 1;
            }
        }

        int bWidth = bRight - bLeft + 1;
        int bHeight = bBottom - bTop + 1;

        return new int[] {bLeft, bRight, bTop, bBottom, bWidth, bHeight};
    }

    // New one.
    public static void ensureOnScreen(RWindow win) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<Disp> disps = new ArrayList<>();
        int d = 0;
        for(GraphicsDevice gdv : ge.getScreenDevices()) {
            GraphicsConfiguration cfg = gdv.getDefaultConfiguration();
            Rectangle b = cfg.getBounds();
            disps.add(new Disp(d++, b.x, b.y, b.width, b.height));
        }

        // Sort the displays based on their X coordinate
        // (left-hand side of the screen)
        Collections.sort(disps, (o1, o2) -> o1.x - o2.x);

        int x = win.getX();  // Get this window's current position

        // Haven't actually seen this condition - but here just in
        // case until we can better analyze the code.
        if(disps.isEmpty()) {
            return;
        }

        // Make sure the left of window is at least the left-most monitor's X position
        Disp first = disps.get(0);
        if(x < first.x) {
            x = first.x;
        }

        // Make sure the left of the window is at least inside the right-side
        // of the right-most monitor.
        Disp last = disps.get(disps.size() - 1);
        if(x >= last.x + last.w) {
            x = last.x + last.w - win.getWidth();
        }

        // Find all the displays that overlap with the window's
        // x coordinate.
        List<Disp> vDisps = new ArrayList<>();
        for(Disp disp : disps) {
            if(x >= disp.x && x < disp.x + disp.w) {
                vDisps.add(disp);
            }
        }

        // Haven't actually seen this condition - but here just in
        // case until we can better analyze the code.
        if(vDisps.isEmpty()) {
            return;
        }

        // Sort these windows according to their y coordinate.
        Collections.sort(vDisps, (o1, o2) -> o1.y - o2.y);

        int y = win.getY();  // Get this window's current Y position

        // Make sure the top of window is at least the top-most monitor's Y position
        first = vDisps.get(0);
        if(y < first.y) {
            y = first.y;
        }

        // Make sure the top of the window is at least inside the bottom-edge
        // of the bottom-most monitor.
        last = vDisps.get(vDisps.size() - 1);
        if(y >= last.y + last.h) {
            y = last.y + last.h - win.getHeight();
        }

        win.setLocation(x, y);
    }

    private static class Disp {
        public int which;
        public int x;
        public int y;
        public int w;
        public int h;
        public Disp(int which, int x, int y, int w, int h) {
            this.which = which;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    public static JFrame fra(Component c) {
        return (JFrame) SwingUtilities.getRoot(c);
    }
    public static JDialog dlg(Component c) {
        return (JDialog) SwingUtilities.getRoot(c);
    }
    public static Window win(Component c) {
        return (Window) SwingUtilities.getRoot(c);
    }


    public static void traverse(Component cmp, Consumer<Component> consumer) {
        traverseInner(cmp, consumer);
    }
    private static void traverseInner(Component cmp, Consumer<Component> consumer) {
        consumer.accept(cmp);
        if(cmp instanceof Container) {
            Container cont = (Container) cmp;
            Component[] children = cont.getComponents();
            for(Component child : children) {
                traverseInner(child, consumer);
            }
        }
    }


    //////////
    // TEST //
    //////////

    public static void mainx(String[] args) {
        JTextField txt = new JTextField("test", 10);
        makeSelectAllOnFocus(txt);
        JFrame f = new JFrame();
        f.setLayout(new FlowLayout());
        makeEscapeClose(f);
        f.add(txt);
        f.add(new JButton("something"));
        f.setSize(300, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void printBorderLayout(BorderLayout layout) {
        System.out.println("north  = " + ReflectionUtil.get(layout, "north"));
        System.out.println("east   = " + ReflectionUtil.get(layout, "east"));
        System.out.println("south  = " + ReflectionUtil.get(layout, "south"));
        System.out.println("west   = " + ReflectionUtil.get(layout, "west"));
        System.out.println("center = " + ReflectionUtil.get(layout, "center"));
    }
    public static void paint(Container c, int level) {
        for(Component cc : c.getComponents()) {
            GuiUtil.color((JComponent) cc, GuiUtil.deriveColor(UiDebugUtil.nextColor(), 0, 0, 0));
            if(cc instanceof Container) {
                paint((Container) cc, level + 1);
            }
        }
    }
    public static void drawCenteredString(Component cmp, Graphics g, String msg, int y) {
        int w = cmp.getWidth();
        int x = (w - stringWidth(g, msg)) / 2;
        g.drawString(msg, x, y);
    }

    public static void setAntiAlias(Graphics g, Boolean antiAlias, Boolean textAntiAlias) {
        setAntiAlias((Graphics2D) g, antiAlias, textAntiAlias);
    }
    public static void setAntiAlias(Graphics2D g, Boolean antiAlias, Boolean textAntiAlias) {
        if(antiAlias != null) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    (antiAlias) ? RenderingHints.VALUE_ANTIALIAS_ON
                            : RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        if(textAntiAlias != null) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    (textAntiAlias) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                            : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }
}
