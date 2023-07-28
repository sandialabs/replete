package replete.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import org.javadev.AnimatingCardLayout;
import org.javadev.effects.SlideAnimation;

import replete.ui.form.RFormPanel;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.lay.Lay;


public class AccordianDetailPanel extends JPanel {
    public static final String STACK_NONE = null;
    public static final String STACK_NORTH = "N";
    public static final String STACK_SOUTH = "S";
    public static final String STACK_EAST  = "E";
    public static final String STACK_WEST  = "W";

    private DetailTitlePanel pnlTitle;
    private JPanel pnlContent;
    private StackPanel pnlStack;
    private List<DetailPanelInfo> detailPanelInfos = new ArrayList<>();
    private CardLayout cardLayout;

    public boolean useAnimatedPanels;

    public AccordianDetailPanel(boolean animated) {
        this(animated, false, null);
    }
    public AccordianDetailPanel(boolean animated, boolean titleBar) {
        this(animated, titleBar, null);
    }
    public AccordianDetailPanel(boolean animated, boolean titleBar, String stackLocation) {
        useAnimatedPanels = animated;
        if(useAnimatedPanels) {
            cardLayout = new AnimatingCardLayout(new SlideAnimation());
            ((AnimatingCardLayout)cardLayout).setAnimationDuration(200);
        } else {
            cardLayout = new CardLayout();
        }

        pnlTitle = new DetailTitlePanel();

        pnlContent = new JPanel();
        pnlContent.setOpaque(false);
        pnlContent.setLayout(cardLayout);

        setLayout(new BorderLayout());
        if(titleBar) {
            add(pnlTitle, BorderLayout.NORTH);
        }
        if(stackLocation != null) {
            if(stackLocation.equals(STACK_EAST)) {
                pnlStack = new StackPanel(StackPanel.WEST);
                add(pnlStack, BorderLayout.EAST);

            } else if(stackLocation.equals(STACK_WEST)) {
                pnlStack = new StackPanel(StackPanel.EAST);
                add(pnlStack, BorderLayout.WEST);

            } else if(stackLocation.equals(STACK_NORTH)) {
                pnlStack = new StackPanel(StackPanel.SOUTH);
                add(pnlStack, BorderLayout.NORTH);

            } else if(stackLocation.equals(STACK_SOUTH)) {
                pnlStack = new StackPanel(StackPanel.NORTH);
                add(pnlStack, BorderLayout.SOUTH);
            } else {
                throw new IllegalArgumentException("Stack location must be one of AccordianDetailPane.STACK_*");
            }
        } else {
            // Else just make so it will be used, just not shown.
            pnlStack = new StackPanel(StackPanel.NORTH);
        }
        add(pnlContent, BorderLayout.CENTER);
    }

    public boolean isDirty() {
        for(DetailPanelInfo pair : detailPanelInfos) {
            if(pair.panel instanceof RFormPanel && ((RFormPanel) pair.panel).isDirty()) {
                return true;
            }
        }
        return false;
    }

    public int getPanelCount() {
        return pnlContent.getComponentCount();
    }

    public String topTitle() {
        if(detailPanelInfos.size() > 0) {
            return detailPanelInfos.get(detailPanelInfos.size() - 1).title;
        }

        return null;
    }

    public JPanel topPanel() {
        if(detailPanelInfos.size() > 0) {
            return detailPanelInfos.get(detailPanelInfos.size() - 1).panel;
        }

        return null;
    }

    public void pushPanel(JPanel pnl, String title, Icon icon, ChangeListener animationFinishedCallback) {
        if(pnl == topPanel()) {
            repaint();
            return;
        }

        DetailPanelInfo glob = new DetailPanelInfo();
        glob.panel = pnl;
        glob.title = title;
        glob.icon = icon;

        // Content panel
        pnlContent.add(pnl, title);  //unique?
        if(useAnimatedPanels) {
            try {
                ((AnimatingCardLayout) cardLayout).show(pnlContent, title, animationFinishedCallback);
            } catch(IllegalStateException e) {
                pnlContent.remove(pnl);
                throw e;
            }
        } else {
            cardLayout.show(pnlContent, title);
            if(animationFinishedCallback != null) {
                animationFinishedCallback.stateChanged(null);
            }
        }
        pnlContent.repaint();

        // Title panel
        pnlTitle.pushTitle(title);
        if(getPanelCount() > 1) {
            String prevTitle = detailPanelInfos.get(detailPanelInfos.size() - 1).title;
            Icon prevIcon = detailPanelInfos.get(detailPanelInfos.size() - 1).icon;
            final JPanel pnlNew = pnlStack.push(Lay.lb(prevTitle, prevIcon));
            pnlNew.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pnlNew.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if(pnlNew instanceof GradientPanel) {
                        ((GradientPanel) pnlNew).setColors(GradientPanel.INIT_COLOR, GradientPanel.INIT_COLOR.darker());
                    } else {
                        Lay.hn(pnlNew, "bg=" + Lay.clr(new JPanel().getBackground()));
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    Color c = Lay.clr("255, 240, 230");
                    if(pnlNew instanceof GradientPanel) {
                        ((GradientPanel) pnlNew).setColors(c, c.darker());
                    } else {
                        Lay.hn(pnlNew, "bg=[255, 240, 230]");
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        popPanel();
                    } catch(Exception e1) {}
                }
            });
        }

        // Internal storage
        detailPanelInfos.add(glob);
    }

    public void popPanel() {
        if(detailPanelInfos.size() > 0) {

            // Content panel
            if(detailPanelInfos.size() > 1) {
                try {
                    cardLayout.show(pnlContent, detailPanelInfos.get(detailPanelInfos.size() - 2).title);
                } catch(IllegalStateException e) {
                    throw e;
                }
            }
            pnlContent.remove(detailPanelInfos.get(detailPanelInfos.size() - 1).panel);
            pnlContent.repaint();

            // Title
            pnlTitle.popTitle();
            pnlStack.pop();

            // Internal storage
            detailPanelInfos.remove(detailPanelInfos.size() - 1);
        }
    }

    public void popAll() {

        // Content panel
        pnlContent.removeAll();
        pnlContent.updateUI();

        // Title
        pnlTitle.popAll();

        pnlStack.popAll();

        // Internal storage
        detailPanelInfos.clear();
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class DetailPanelInfo {
        private JPanel panel;
        private String title;
        private Icon icon;
    }

    private class DetailTitlePanel extends GradientPanel {
        public List<String> titles = new ArrayList<>();
        private JLabel lblTitles = new JLabel(" ");

        public DetailTitlePanel() {
            super(new FlowLayout(FlowLayout.LEFT), true);
            setOpaque(true);
            add(lblTitles);
            setColors(INIT_COLOR, INIT_COLOR);
        }

        public void pushTitle(String title) {
            titles.add(title);
            lblTitles.setText(buildTitle());
            setColor(INIT_COLOR);
        }

        public void popTitle() {
            titles.remove(titles.size() - 1);
            lblTitles.setText(buildTitle());
            if(titles.size() == 0) {
                setColors(INIT_COLOR, INIT_COLOR);
            }
        }

        public void popAll() {
            titles.clear();
            lblTitles.setText(buildTitle());
            setColors(INIT_COLOR, INIT_COLOR);
        }

        protected String buildTitle() {
            String sep = " > ";
            String ret = "";
            for(String title : titles) {
                ret += "<i>" + title + "</i>" + sep;
            }
            if(ret.equals("")) {
                ret = " ";
            } else {
                ret = ret.substring(0, ret.length() - sep.length());
            }
            return "<html>" + ret + "</html>";
        }
    }

    private static int sIdx = 0;
    private static String[] strs = new String[] {
        "Mercury", "Venus", "Earth", "Mars", "Jupiter",
        "Saturn", "Neptune", "Uranus", "Pluto"
    };
    private static ImageModelConcept[] concepts = new ImageModelConcept[] {
        RepleteImageModel.NORTH, RepleteImageModel.EAST,
        RepleteImageModel.SOUTH, RepleteImageModel.WEST,
        CommonConcepts.CANCEL, CommonConcepts.PLAY,
        CommonConcepts.TARGET, CommonConcepts._PLACEHOLDER,
        CommonConcepts.PROGRESS
    };
    public static void main(String[] args) {

        final AccordianDetailPanel adp = new AccordianDetailPanel(true, true, AccordianDetailPanel.STACK_WEST);

        JButton btnAdd = Lay.btn("&Add Panel", (ActionListener) e -> {
            if(sIdx < strs.length) {
                try {
                    adp.pushPanel(createSubPanel(strs[sIdx]), strs[sIdx], ImageLib.get(concepts[sIdx]), null);
                    sIdx++;
                } catch(Exception e1) {
                }
            }
        });

        JButton btnRemove = Lay.btn("&Remove Panel", (ActionListener) e -> {
            if(sIdx >= 2) {
                try {
                    adp.popPanel();
                    sIdx--;
                } catch(IllegalStateException e1) {
                }
            }
        });

        adp.pushPanel(createSubPanel(strs[sIdx]), strs[sIdx], ImageLib.get(RepleteImageModel.NORTH), null);
        sIdx++;

        Lay.BLtg(Lay.fr("AccordianDetailPanel"),
            "N", Lay.FL(btnAdd, btnRemove),
            "C", adp,
            "size=600,center,visible"
        );
    }

    private static JPanel createSubPanel(String s) {
        JPanel p = new RPanel();
        Lay.FLtg(p, Lay.lb(s));
        return p;
    }
}
