package finio.ui.fpanel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.panels.RPanel;

public class FContentPanel extends RPanel {


    ///////////
    // FIELD //
    ///////////

    private List<Image> spriteImages;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FContentPanel() {
        super();
    }
    public FContentPanel(LayoutManager mgr) {
        super(mgr);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Mutators

    public void addSpriteImage(Image image) {
        if(spriteImages == null) {
            spriteImages = new ArrayList<>();
        }
        spriteImages.add(image);
        repaint();
    }
    public void clearSpriteImages() {
        if(spriteImages != null) {
            spriteImages.clear();
            repaint();
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setOpaque(boolean isOpaque) {
        super.setOpaque(isOpaque);
        repaint();
        fireOpaqueNotifier();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(spriteImages != null && isOpaque()) {
            for(Image image : spriteImages) {
                int w = image.getWidth(null);
                int h = image.getHeight(null);
                g.drawImage(image, 0, 0, w, h, null);
            }
        }

    }

//    @Override
//    public void paint(Graphics g) {
//
//        AffineTransform T = new AffineTransform();
//        T.scale(2, 2);
//
//        Graphics2D g2 = (Graphics2D) g;
//
//        AffineTransform X = g2.getTransform();
//        g2.setTransform(T);
//
//        super.paint(g);
//    }

    //////////////
    // NOTIFIER //
    //////////////

    private ChangeNotifier opaqueNotifier = new ChangeNotifier(this);
    public void addOpaqueListener(ChangeListener listener) {
        opaqueNotifier.addListener(listener);
    }
    private void fireOpaqueNotifier() {
        if(opaqueNotifier != null) {
            opaqueNotifier.fireStateChanged();
        }
    }
}
