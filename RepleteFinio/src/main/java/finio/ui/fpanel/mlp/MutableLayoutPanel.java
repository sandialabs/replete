package finio.ui.fpanel.mlp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import finio.plugins.platform.FinioPlugin;
import finio.ui.fpanel.FContentPanel;
import finio.ui.fpanel.FPanel;
import finio.ui.images.FinioImageModel;
import replete.collections.RArrayList;
import replete.numbers.RandomUtil;
import replete.plugins.PluginManager;
import replete.ui.button.IconButton;
import replete.ui.lay.Lay;

public class MutableLayoutPanel extends FPanel {

    public MutableLayoutPanel() {
        super();
    }
    public MutableLayoutPanel(FContentPanel pnlContent) {
        super(pnlContent);
    }

    @Override
    protected List<IconButton> getMoreConfigButtons() {
        IconButton btnLayout = (IconButton) Lay.btn(FinioImageModel.LAYOUT);
        btnLayout.setToolTipText("Layout...");
        btnLayout.toImageOnly();
        btnLayout.addActionListener(e -> {
            setBL();
        });
        return new RArrayList<>(btnLayout);
    }

    public void setBL() {
        setLayout(new BorderLayout());
        add(new BorderLayoutPortPanel("(North)",  new Dimension(1, 70)), BorderLayout.NORTH);
        add(new BorderLayoutPortPanel("(East)",   new Dimension(70, 1)), BorderLayout.EAST);
        add(new BorderLayoutPortPanel("(South)",  new Dimension(1, 70)), BorderLayout.SOUTH);
        add(new BorderLayoutPortPanel("(West)",   new Dimension(70, 1)), BorderLayout.WEST);
        add(new BorderLayoutPortPanel("(Center)", null),                 BorderLayout.CENTER);
        updateUI();
    }

    private static class BorderLayoutPortPanel extends MutableLayoutPanel {
        private String label;
        private Dimension dim;

        BorderLayoutPortPanel(String label, Dimension dim) {
            super(new BorderLayoutPortContentPanel());
            ((BorderLayoutPortContentPanel) pnlContent).setParent(this);

            this.label = label;
            this.dim = dim;
            setPreferredSize(dim);
            setBackground(new Color(
                RandomUtil.getRandomWithinRange(0, 256),
                RandomUtil.getRandomWithinRange(0, 256),
                RandomUtil.getRandomWithinRange(0, 256)
            ));
            setOpaque(true);
        }

        public String getLabel() {
            return label;
        }
    }

    private static class BorderLayoutPortContentPanel extends FContentPanel {
        private BorderLayoutPortPanel pnlParent;
        public void setParent(BorderLayoutPortPanel pnlParent) {
            this.pnlParent = pnlParent;
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
            g.drawString(pnlParent.getLabel(), 3, 18);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
//        BorderLayoutPortPanel pnlX = new BorderLayoutPortPanel("aaa", null);
//        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
//        pnlX.paintComponent(img.getGraphics());
//        Lay.BLtg(Lay.fr("asdfas"), "C", new BorderLayoutPortPanel("aaaaaa", null), "size=400,center,visible");
//        if(true) {
//            return;
//        }


        PluginManager.initialize(new FinioPlugin());
        JFrame fra = Lay.fr("Test");
        JMenuBar bar = new JMenuBar();
        fra.setJMenuBar(bar);
        JMenu mnu = new JMenu("File");
        JMenuItem mnuX = new JMenuItem("aasdf");
        mnu.add(mnuX);
        bar.add(mnu);
        MutableLayoutPanel pnl;
        Lay.BLtg(fra,
            "C", pnl = new MutableLayoutPanel(),
            "size=800,center,visible"
        );
        pnl.setShowConfigDrawer(true, false);
        mnuX.addActionListener(e -> pnl.setShowConfigDrawer(!pnl.isShowingConfigDrawer(), false));
    }
}
