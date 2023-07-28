package finio.ui.fpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.platform.exts.view.treeview.TreeView;
import finio.platform.exts.view.treeview.ui.FTreePanel;
import finio.ui.actions.FActionMapBuilder;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import replete.collections.RArrayList;
import replete.ui.GuiUtil;
import replete.ui.button.IconButton;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.ui.uiaction.UIActionPopupMenu;
import replete.ui.windows.escape.EscapeDialog;

// A panel which embodies the principal that containers in software
// should not be so static.  They should be able to change as the
// cognitive demands or properties of the user shifts.  This is
// an experimental class which demonstrates only a simple number
// of these principles by allowing a user (developer or otherwise)
// to change at runtime various properties of the panel like
// background color, opacity, etc.

// Thoughts on how ANY UI would actually allow the user to access
// it's config panel given any arbitrary layout.  There may not
// always be the time/desire to add specific buttons/actions
// visible to the user for EVERY possible panel/subpanel/subsubpanel...
// (obviously!).  Some panels won't even be distinguishable from
// subpanels when subpanels take up 100% of the parent panel's
// extent.  Also, UI frameworks don't make just let any PANEL
// (div/frame/region) have keyboard "focus".  Only "interesting"
// components on the screen need to be able to receive keyboard
// strokes, in general.  This makes the hotkey solution a problem.
// Making EVERY panel in a UI focusable is also probably a bad idea
// and would negate the meaning of having a focus in the first place
// as it can be difficult to visually indicate which panels have
// focus given various subpanels' and subcomponents' need for opacity
// and confusion around subpanels that take up 100% of parent panel
// extent.  That leaves the mouse.  It doesn't necessarily fix all
// the problems, but at least you can choose to select a panel WHEN
// you want to select it, instead of having the tabbing system cycle
// you through useless components.  Although immediate choice is
// enabled, panels with "encompassing" components still pose a problem.
// Where do you click to get access to the parent (composition parent)
// of a component that is taking up 100% of the extent of its parent
// (and possibly grandparent, ancestor, etc.)?  Selection MUST happen
// the top-most layer available visually to the user and then they
// must be given a very simple mechanism to select the components
// *under* the component the mouse has access to.  This could be done
// by a special key+mouse combination on ANY component that would
// be bound to a window-level accelerator which would transform the
// window contents into either a 2D or 3D representation of the
// hierarchy.  In 2D this would modify all backgrounds and borders
// so that all components on the screen have some visible real
// estate - allowing a user to mouse over and select ANY component.
// In 3D the containment could happen by showing layers under one
// another.

public class FPanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    // Proxy panel as required by using Swing.  In a custom windowing
    // environment this would not be required.  Each panel could come
    // with its own integrated configuration drawer.  As doing so
    // in Swing would be simply too complex, we use basic Swing to
    // subclass JPanel and add both a configuration panel and a
    // regular "content" panel in a BorderLayout.  We will not
    // attempt to surpress the existence of this panel in component
    // hierachies as this would also unnecessarily complicate the
    // component.
    protected FContentPanel pnlContent;

    // The configuration panel which allows the end user, at runtime,
    // to customize this panel.
    private JPanel pnlConfigDrawer;

    // Whether or not the configuration drawer panel is showing.
    private boolean showingConfigDrawer = false;

    // Whether or not this panel is attempting to modify the top-level
    // FPanel itself (this) and not the proxy content panel.  As
    // appropriate actions will be delegated to the proxy panel so as
    // to still interact with the Swing environment as seamlessly as
    // possible.  However, since there are indeed two panels working
    // together here, there are times when we want to affect the
    // top level panel and not the content panel.
    private boolean rootModify = false;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FPanel() {
        this(new FContentPanel());
    }
    public FPanel(FContentPanel pnlContent) {
        this.pnlContent = pnlContent;

        // We want to modify "this" panel for a bit...
        rootModify = true;

        // Top-level FPanel should always be transparent so as to
        // appear as non-existent to the user as possible.
        setOpaque(false);

        // Use a simple BorderLayout with the configuration panel
        // always appearing on the left for now.
        setLayout(new BorderLayout());

        // Place the proxy content panel into the center of the panel.
        add(pnlContent, BorderLayout.CENTER);

        // Stop modifying "this" panel.
        rootModify = false;
    }

    // Not meant to be overridden, since FPanel is a Frankenstein
    // panel to allow for the config tray, any custom painting
    // needs to be done in a custom "FContentPanel".  An FPanel should
    // just always be transparent and hold the config and content
    // panels.
    @Override
    protected void paintComponent(Graphics g) {
        rootModify = true;
        super.paintComponent(g);
        rootModify = false;
    }


    //////////////////////////
    // CONFIGURATION DRAWER //
    //////////////////////////

    public boolean isShowingConfigDrawer() {
        return showingConfigDrawer;
    }

    public void setShowConfigDrawer(boolean vis) {
        setShowConfigDrawerRecursive(this, vis, 1, false);
    }
    public void setShowConfigDrawer(boolean vis, boolean recursive) {
        setShowConfigDrawerRecursive(this, vis, recursive ? Integer.MAX_VALUE : 1, false);
    }
    public void setShowConfigDrawer(boolean vis, int levels) {
        setShowConfigDrawerRecursive(this, vis, levels, false);
    }
    public void setShowConfigDrawerChildren(boolean vis, boolean recursive) {
        setShowConfigDrawerRecursive(this, vis, recursive ? Integer.MAX_VALUE : 1, true);
    }
    public void setShowConfigDrawerChildren(boolean vis, int levels) {
        setShowConfigDrawerRecursive(this, vis, levels, true);       // Still need to pass in 2 for levels currently
    }
    private void setShowConfigDrawerRecursive(Container cnt, boolean vis, int levels, boolean skipFirst) {

        // Configuration drawers should never themselves be FPanel's
        // (need to draw the line somewhere! Though, philosophically
        // there room to argue that FConfigPanel's could be FPanel's
        // as well, though most likely harder to accomplish without
        // a custom windowing framework).
        if(cnt instanceof FConfigPanel) {
            return;
        }

        // If this panel is an FPanel, then it has the ability to
        // have its configuration drawer panel shown or hidden so
        // do so now.
        if(levels > 0 && !skipFirst) {
            if(cnt instanceof FPanel) {
                FPanel pnlSpecial = (FPanel) cnt;
                if(vis) {
                    pnlSpecial.showDrawer();
                } else {
                    pnlSpecial.hideDrawer();
                }
            }
        }

        if(!(cnt instanceof FContentPanel)) {
            levels--;
        }

        if(levels > 0) {
            for(int c = 0; c < cnt.getComponentCount(); c++) {
                Component cmpChild = cnt.getComponent(c);
                if(cmpChild instanceof Container) {
                    setShowConfigDrawerRecursive((Container) cmpChild, vis, levels, false);
                }
            }
        }
    }

    private void showDrawer() {
        if(!showingConfigDrawer) {
            rootModify = true;
            if(pnlConfigDrawer == null) {
                pnlConfigDrawer = new FConfigPanel();
            }
            add(pnlConfigDrawer, BorderLayout.WEST);
            rootModify = false;

            updateUI();
            showingConfigDrawer = true;
        }
    }
    private void hideDrawer() {
        if(showingConfigDrawer) {
            rootModify = true;
            if(pnlConfigDrawer != null) {   // Unnecessary
                remove(pnlConfigDrawer);
            }
            rootModify = false;

            updateUI();
            showingConfigDrawer = false;
        }
    }

    public void toggleShowConfig() {
        if(showingConfigDrawer) {
            hideDrawer();
        } else {
            showDrawer();
        }
    }


    ///////////////////
    // PASS-THROUGHS //
    ///////////////////

    @Override
    public Color getBackground() {
        if(rootModify) {
            return super.getBackground();
        }
        if(pnlContent != null) {
            return pnlContent.getBackground();
        }
        return super.getBackground();
    }

    @Override
    public boolean isOpaque() {
        if(rootModify) {
            return super.isOpaque();
        }
        return pnlContent.isOpaque();
    }

    @Override
    public LayoutManager getLayout() {
        if(rootModify) {
            return super.getLayout();
        }
        return pnlContent.getLayout();
    }

    @Override
    public void setBackground(Color bg) {
        if(rootModify) {
            super.setBackground(bg);
        } else {
            if(pnlContent != null) {
                pnlContent.setBackground(bg);
            } else {
                super.setBackground(bg);
            }
        }
    }

    @Override
    public void setOpaque(boolean isOpaque) {
        if(rootModify) {
            super.setOpaque(isOpaque);
        } else {
            if(pnlContent != null) {
                pnlContent.setOpaque(isOpaque);
            } else {
                super.setOpaque(isOpaque);
            }
        }
    }

    @Override
    public void updateUI() {
        if(pnlContent != null) {
            pnlContent.updateUI();
        }
        super.updateUI();
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        if(rootModify) {
            super.setLayout(mgr);
        } else {
            if(pnlContent != null) {
                pnlContent.setLayout(mgr);
            } else {
                super.setLayout(mgr);
            }
        }
    }

    @Override
    public Component add(Component comp) {
        if(rootModify) {
            return super.add(comp);
        }
        return pnlContent.add(comp);
    }
    @Override
    public Component add(Component comp, int index) {
        if(rootModify) {
            return super.add(comp, index);
        }
        return pnlContent.add(comp, index);
    }
    @Override
    public void add(Component comp, Object constraints) {
        if(rootModify) {
            super.add(comp, constraints);
        } else {
            pnlContent.add(comp, constraints);
        }
    }
    @Override
    public void add(Component comp, Object constraints, int index) {
        if(rootModify) {
            super.add(comp, constraints, index);
        } else {
            pnlContent.add(comp, constraints, index);
        }
    }
    @Override
    public void add(PopupMenu popup) {
        if(rootModify) {
            super.add(popup);
        } else {
            pnlContent.add(popup);
        }
    }
    @Override
    public Component add(String name, Component comp) {
        if(rootModify) {
            return super.add(name, comp);
        }
        return pnlContent.add(name, comp);
    }
    @Override
    public void remove(Component comp) {
        if(rootModify) {
            super.remove(comp);
        } else {
            pnlContent.remove(comp);
        }
    }
    @Override
    public void remove(int index) {
        if(rootModify) {
            super.remove(index);
        } else {
            pnlContent.remove(index);
        }
    }
    @Override
    public void removeAll() {
        if(rootModify) {
            super.removeAll();
        } else {
            pnlContent.removeAll();
        }
    }

    protected List<IconButton> getMoreConfigButtons() {
        return new ArrayList<>();
    }
    private List<IconButton> getDefaultConfigButtons() {
        IconButton btnBg = (IconButton) Lay.btn(FinioImageModel.PAINT);
        btnBg.setToolTipText("Background Color...");
        btnBg.toImageOnly();
        btnBg.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                FPanel.this,
                "Choose Background Color",
                pnlContent.getBackground());
            if(newColor != null) {
                pnlContent.setBackground(newColor);
            }
        });

        IconButton btnFg = (IconButton) Lay.btn(FinioImageModel.FONT_COLOR);
        btnFg.setToolTipText("Foreground Color...");
        btnFg.toImageOnly();
        btnFg.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(
                FPanel.this,
                "Choose Foreground Color",
                pnlContent.getForeground());
            if(newColor != null) {
                pnlContent.setForeground(newColor);
            }
        });

        IconButton btnBgPic = (IconButton) Lay.btn(FinioImageModel.PICTURE_SQUARE);
        btnBgPic.setToolTipText("Background Image...");
        btnBgPic.toImageOnly();
        btnBgPic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                UIActionMap map = new UIActionMap();

                UIActionListener changeAction = (e2, action) -> {
                    RFileChooser chooser = RFileChooser.getChooser("Choose Background Picture");
                    RFilterBuilder builder = new RFilterBuilder(chooser, true);
                    builder
                        .append("GIF", "gif")
                        .append("PNG", "png")
                        .append("JPG", "jpg", "jpeg")
                        .append("TIFF", "tif", "tiff");
                    if(chooser.showOpen(FPanel.this)) {
                        try {
                            BufferedImage img = ImageIO.read(chooser.getSelectedFile());
                            pnlContent.addSpriteImage(img);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                map.createAction("change", changeAction)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Change...")
                        .setIcon(CommonConcepts.CHANGE)
                );

                UIActionListener clearAction = (e2, action) -> pnlContent.clearSpriteImages();
                map.createAction("clear", clearAction)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Clear")
                        .setIcon(CommonConcepts.CLEAR)
                );

                UIActionPopupMenu mnuPopup = new UIActionPopupMenu(map);
                mnuPopup.show(btnBgPic, e.getX(), e.getY());
            }
        });

        IconButton btnOpaque = (IconButton) Lay.btn(FinioImageModel.OPAQUE_ON);
        btnOpaque.setToolTipText("Toggle Opaqueness");
        btnOpaque.toImageOnly();
        btnOpaque.addActionListener(e -> {
            pnlContent.setOpaque(!pnlContent.isOpaque());
            FPanel.this.updateUI();
        });
        updateOpaqueButton(btnOpaque);
        pnlContent.addOpaqueListener(e -> updateOpaqueButton(btnOpaque));

        IconButton btnHier = (IconButton) Lay.btn(FinioImageModel.UI_HIERARCHY);
        btnHier.setToolTipText("Show Component Hierarchy...");
        btnHier.toImageOnly();
        btnHier.addActionListener(e -> {
//            String c = createComponentHierarchyString(FPanel.this);
            Window win = GuiUtil.win(FPanel.this);
            final EscapeDialog dlg = new EscapeDialog(win, "Content Panel Contents", true);
            dlg.setIcon(FinioImageModel.UI_HIERARCHY);

            NonTerminal M = createComponentHierarchyMap(FPanel.this);
            AppContext ac = new AppContext();
            ac.getConfig().setNodeInfoEnabled(false);
            FActionMapBuilder builder = new FActionMapBuilder();
            builder.build(ac);
            WorldContext wc = new WorldContext(ac).setW(M);
            ac.addWorld(wc);
            WorldPanel pnlWorld = wc.getWorldPanel();
//            pnlWorld.setExpandSingleView(true);
            FTreePanel pnlTree = new FTreePanel(ac, wc, null, M, new TreeView());
            pnlTree.setShowWorkingScope(false);
            pnlTree.setRootVisible(false);
            pnlWorld.addViewPanel(pnlTree);

            JButton btnClose;
            Lay.BLtg(dlg,
                "N", Lay.lb(
                    "<html>The following describes the hierarchy of components contained in this panel (*).</html>",
                    "eb=5tlrb"),
                "C", pnlWorld, //Lay.p(Lay.sp(Lay.txa(c, "editable=false")), "eb=5tlr"),
                "S", Lay.FL("R",
                    btnClose = Lay.btn("&Close", CommonConcepts.CANCEL)
                ),
                "size=500,center"
            );
            btnClose.addActionListener(ev -> dlg.close());
            dlg.setVisible(true);
        });

        IconButton btnConfig = (IconButton) Lay.btn(CommonConcepts.CONFIGURATION);
        btnConfig.setToolTipText("Configuration");
        btnConfig.toImageOnly();
        btnConfig.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                UIActionMap map = new UIActionMap();

                UIActionListener A7 = (e2, action) -> setShowConfigDrawer(false);
                map.createAction("a7", A7)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Hide (This)")
                        .setIcon(FinioImageModel.CONFIG_HIDE)
                );
                UIActionListener A0 = (e2, action) -> setShowConfigDrawerChildren(true, 2);
                map.createAction("a0", A0)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Show (Immediate Children)")
                        .setIcon(CommonConcepts.CONFIGURATION)
                );
                UIActionListener A1 = (e2, action) -> setShowConfigDrawerChildren(false, 2);
                map.createAction("a1", A1)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Hide (Immediate Children)")
                        .setIcon(FinioImageModel.CONFIG_HIDE)
                );

                UIActionListener A2 = (e2, action) -> setShowConfigDrawerChildren(true, true);
                map.createAction("a2", A2)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Show (All Descendants)")
                        .setIcon(CommonConcepts.CONFIGURATION)
                );
                UIActionListener A3 = (e2, action) -> setShowConfigDrawerChildren(false, true);
                map.createAction("a3", A3)
                    .addDescriptor(new PopupMenuActionDescriptor()
                        .setText("Hide (All Descendants)")
                        .setIcon(FinioImageModel.CONFIG_HIDE)
                );

                UIActionPopupMenu mnuPopup = new UIActionPopupMenu(map);
                mnuPopup.show(btnConfig, e.getX(), e.getY());
            }
        });

        return new RArrayList<>(btnBg, btnFg, btnBgPic, btnOpaque, btnHier, btnConfig);
    }

    private void updateOpaqueButton(final IconButton btnOpaque) {
        boolean op = pnlContent.isOpaque();
        btnOpaque.setIcon(op ? FinioImageModel.OPAQUE_ON : FinioImageModel.OPAQUE_OFF);
        btnOpaque.setToolTipText(op ? "Make Transparent" : "Make Opaque");
    }
    private NonTerminal createComponentHierarchyMap(Component cmp) {
        NonTerminal M = new FMap();
        createComponentHierarchyMap(M, cmp, 0);
        return M;
    }

    private void createComponentHierarchyMap(NonTerminal M, Component cmp, int level) {
        if(cmp instanceof FConfigPanel) {
            return;
        }

        String nm = cmp.getName() != null ? " [" + cmp.getName() + "]" : "";
        String p = " (" + cmp.getLocation().x + "," + cmp.getLocation().y + ")";
        String sz = " {" + cmp.getSize().width + "x" + cmp.getSize().height + "}";
        String as = level == 0 ? "*" : "";
        String ly = "";
        if(cmp instanceof Container) {
            LayoutManager mgr = ((Container) cmp).getLayout();
            if(mgr != null) {
                ly = " L:" + mgr.getClass().getSimpleName() + "";
            }
        }
        String key = as + cmp.getClass().getSimpleName() + nm + p + sz + ly;

        NonTerminal Mchildren = new FMap();
        if(cmp instanceof Container) {
            Container cnt = (Container) cmp;
            for(int c = 0; c < cnt.getComponentCount(); c++) {
                Component cmpChild = cnt.getComponent(c);
                createComponentHierarchyMap(Mchildren, cmpChild, level + 1);
            }
        }
        M.put(key, Mchildren);
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class FConfigPanel extends JPanel {


        ///////////
        // FIELD //
        ///////////

        private final Color lightGray = new Color(200, 200, 200);
        private final Color lightOrange = Lay.clr("FFDAA8");
        private final Color darkOrange = Lay.clr("FFA41C");
        private final Color gray = Lay.clr("120");
        private final Image hingeImage;
        private final Image hingeSmlImage;


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public FConfigPanel() {
            hingeImage = ImageLib.getImg(FinioImageModel.HINGE);
            hingeSmlImage = ImageLib.getImg(FinioImageModel.HINGE);

            List<IconButton> configButtons = getDefaultConfigButtons();
            configButtons.addAll(getMoreConfigButtons());

            Lay.BxLtg(this, "Y",
                "opaque=false,prefw=30"
            );

            boolean first = true;
            for(IconButton btn : configButtons) {
                String border = first ? "eb=5btl" : "eb=5bl";
                add(Lay.BL(
                    "W", btn,
                    border + ",alignx=0.5,maxH=20,opaque=false"
                ));
                first = false;
            }

            add(Box.createVerticalGlue());
        }


        //////////
        // MISC //
        //////////

//        private String createComponentHierarchyString(Component cmp) {
//            StringBuilder builder = new StringBuilder();
//            createComponentHierarchyString(builder, cmp, 0);
//            return builder.toString().trim();
//        }
//
//        private void createComponentHierarchyString(StringBuilder b, Component cmp, int level) {
//            if(cmp instanceof FConfigPanel) {
//                return;
//            }
//
//            String sp = StringUtil.spaces(level * 6);
//            String nm = cmp.getName() != null ? " [" + cmp.getName() + "]" : "";
//            String p = " (" + cmp.getLocation().x + "," + cmp.getLocation().y + ")";
//            String sz = " {" + cmp.getSize().width + "x" + cmp.getSize().height + "}";
//            String as = level == 0 ? "*" : "";
//            String ly = "";
//            if(cmp instanceof Container) {
//                LayoutManager mgr = ((Container) cmp).getLayout();
//                if(mgr != null) {
//                    ly = " L:" + mgr.getClass().getSimpleName() + "";
//                }
//            }
//            b.append(sp + as + cmp.getClass().getSimpleName() + nm + p + sz + ly + "\n");
//
//            if(cmp instanceof Container) {
//                Container cnt = (Container) cmp;
//                for(int c = 0; c < cnt.getComponentCount(); c++) {
//                    Component cmpChild = cnt.getComponent(c);
//                    createComponentHierarchyString(b, cmpChild, level + 1);
//                }
//            }
//        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            Paint paint = new LinearGradientPaint(
                new Point(-100, 0),
                new Point(getWidth() - 1  + 100, getHeight() - 1),
                new float[] {0.4F, 0.5F, 0.6F},
                new Color[] {Color.white, lightGray, Color.white}
            );

            int w = getWidth();
            int wd = w + 20;
            int h = getHeight();

            // Draw the gradient background
            g2.setPaint(paint);
            g.fillRect(0, 0, wd, h);

            // Draw the inner orange lines
            g.setColor(lightOrange);
            g.drawLine(28, 0, 28, getHeight());
            g.setColor(darkOrange);
            g.drawLine(29, 0, 29, getHeight());

            // Draw the outer border
            g.setColor(Color.black);
            g.drawRect(0, 0, wd - 1, h - 1);
            g.setColor(gray);
            g.drawRect(1, 1, wd - 3, h - 3);

            // Draw the hinges
            int wr = w - 6;
            if(h > 70) {
                g.drawImage(hingeImage, wr, 5, 12, 26, null);
                if(h > 100) {
                    g.drawImage(hingeImage, wr, h / 2 - 26 / 2, 12, 26, null);
                }
                g.drawImage(hingeImage, wr, h - 31, 12, 26, null);
            } else if(h > 32) {
                g.drawImage(hingeImage, wr, h / 2 - 26 / 2, 12, 26, null);
            } else {
                g.drawImage(hingeSmlImage, wr, h / 2 - 16 / 2, 12, 16, null);
            }
        }
    }
}
