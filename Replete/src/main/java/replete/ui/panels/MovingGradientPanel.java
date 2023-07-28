package replete.ui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.threads.SwingTimerManager;
import replete.ui.DrawUtil;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class MovingGradientPanel extends RPanel {


    ///////////
    // ENUMS //
    ///////////

    public enum Orientation {
        LEFT_TO_RIGHT,
        UPPER_LEFT_TO_LOWER_RIGHT
    }


    ////////////
    // FIELDS //
    ////////////

    // Constants
    private static final int DEFAULT_TIMER_INTERVAL = 100;
    private static final float ZERO = 0.0F;
    private static final float ONE = 1.0F;

    // Seed information (some of which gets created/modified on set)
    private Color[] seedColors;
    private float[] seedDeltaP;
    private float[] seedCumulP;

    // Offset state
    private float offset = ZERO;
    private float offsetDelta = 0.01F;  // Future parameterizable

    private Orientation orientation = Orientation.LEFT_TO_RIGHT;

    private Color[] activeColors;       // Actual used colors
    private float[] activePositions;    // Actual used positions

    // Optimization fields
    private Color[] colorPrealloc1;
    private float[] posPrealloc1;
    private Color[] colorPrealloc2;
    private float[] posPrealloc2;
    private int s0;
    private int s0m1;
    private int s1;
    private int s2;
    private Paint paint;

    // Made members only for purposes of diagnostic frame
    private OuterPanel pnlOuter = new OuterPanel();
    private int paintLeft;
    private int paintWidth;
    private float totalP;
    private int found;
    private float extraP;

    // Timer
    private Timer timer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public MovingGradientPanel() {
        this(null, null);
    }
    public MovingGradientPanel(Color[] sc, float[] sdp) {
        setParameters(sc, sdp);
        timer = SwingTimerManager.create(DEFAULT_TIMER_INTERVAL, timerAction);
    }

    ActionListener timerAction = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            incrementOffset(offsetDelta);
        }
    };


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public MovingGradientPanel setInterval(int interval) {
        boolean running = timer.isRunning();
        stop();
        SwingTimerManager.remove(timer);
        timer = SwingTimerManager.create(interval, timerAction);
        if(running) {
            start();
        }
        return this;
    }
    public MovingGradientPanel start() {
        if(!timer.isRunning()) {
            timer.start();
        }
        return this;
    }

    public MovingGradientPanel stop() {
        if(timer.isRunning()) {
            timer.stop();
        }
        return this;
    }

    public MovingGradientPanel setOrientation(Orientation orientation) {
        this.orientation = orientation;
        repaint();
        return this;
    }

    public MovingGradientPanel setParameters(Color[] sc, float[] sdp) {
        seedColors = sc;
        seedDeltaP = sdp;

        if(seedColors == null || seedDeltaP == null) {
            return null;
        }

        // Initialize optimization fields
        s0 = seedColors.length;
        s0m1 = s0 - 1;
        s1 = s0 + 1;
        s2 = s1 + 1;
        colorPrealloc1 = new Color[s1];
        posPrealloc1 = new float[s1];
        colorPrealloc2 = new Color[s2];
        posPrealloc2 = new float[s2];

        seedCumulP = new float[s0];
        for(int i = 1; i < s0; i++) {
            seedCumulP[i] = seedCumulP[i - 1] + seedDeltaP[i];
        }

        seedDeltaP[0] = 1 - seedCumulP[s0m1];

        repaint();

        return this;
    }

    public MovingGradientPanel setOffsetDelta(float offsetDelta) {
        this.offsetDelta = offsetDelta;
        repaint();
        pnlOuter.repaint();
        return this;
    }

    public MovingGradientPanel incrementOffset(float offsetDelta2) {
        offset += offsetDelta2;
        if(offset > ONE) {
            offset = offset - ONE;
        } else if(offset < ZERO) {
            offset = ONE + offset;
        }
        repaint();
        pnlOuter.repaint();
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(seedColors == null || seedDeltaP == null) {
            return;
        }

        updateGradientParams();
        pnlOuter.repaint();

        Graphics2D g2 = (Graphics2D) g;

        AffineTransform T = g2.getTransform();
        if(orientation == Orientation.UPPER_LEFT_TO_LOWER_RIGHT) {
            setUlToLr(g2);
        }

        g2.setPaint(paint);  // Must be calculated every time b/c depends on dimensions.
        g2.fillRect(0, 0, getWidth(), getHeight());
        //DrawUtil.grid(g2, getWidth(), getHeight());
        g2.setTransform(T);
    }

    // Technically this method is only dependent on width and height.
    // If neither of these changes, no need to recall this method.
    // Future enhancement if performance a problem.
    private void setUlToLr(Graphics2D g2) {
        double w = getWidth();
        double h = getHeight();
        double theta = Math.atan(w / h);
        double phi = Math.atan(h / w);
        double q = w * Math.cos(theta);
        double r = h * Math.cos(phi);
        double qr = q + r;
        double x = q * Math.cos(theta);
        double y = q * Math.sin(theta);
        AffineTransform T = new AffineTransform();
        double sx = Math.sqrt(w * w + h * h) / w;
        double sy = 2;//qr / h;  2 --> a little extra to cover edge
        T.translate(x, -y);
        T.rotate(phi);
        T.scale(sx, sy);
        g2.setTransform(T);
    }

    private void updateGradientParams() {
        float leftRatio;

        if(offset == ZERO) {
            activeColors = colorPrealloc1;
            activePositions = posPrealloc1;
            int i;
            for(i = 0; i < s0; i++) {
                activePositions[i] = seedCumulP[i];
                activeColors[i] = seedColors[i];
            }
            activeColors[i] = seedColors[0];
            activePositions[i] = ONE;
            paintWidth = getWidth();
            leftRatio = ZERO;

            totalP = ONE;   // temp
            found = -1;
            extraP = ZERO;

        } else {
            activeColors = colorPrealloc2;
            activePositions = posPrealloc2;
            float negP = -1;                    // -1 not used

            found = -1;
            for(int i = 1; i < s0; i++) {
                if(seedCumulP[i] + offset >= ONE) {
                    negP = seedDeltaP[i] - (seedCumulP[i] + offset - ONE);
                    found = i;
                    break;
                }
            }

            // These two blocks could technically be combined because
            // they are so similar.  Not combining might be more efficient.
            if(found == -1) {
                negP = seedDeltaP[0] - offset;
                extraP = seedDeltaP[0];
                activeColors[0] = seedColors[s0m1];
                activeColors[s2 - 1] = seedColors[0];
                for(int i = 1; i < s2 - 1; i++) {
                    activeColors[i] = seedColors[i - 1];
                }

            } else {
                extraP = seedDeltaP[found];
                activeColors[0] = seedColors[found - 1];
                activeColors[s2 - 1] = seedColors[found];
                for(int i = 1; i < s2 - 1; i++) {
                    activeColors[i] = seedColors[(found + i - 1) % s0];
                }
            }

            totalP = 1 + extraP;
            activePositions[0] = ZERO;
            activePositions[s2 - 1] = ONE;
            activePositions[1] = extraP / totalP;
            float xp = extraP;
            for(int p = 2, i = (found == -1) ? 1 : found + 1; p < activePositions.length; i++, p++) {
                xp += seedDeltaP[i % s0];
                activePositions[p] = xp / totalP;
            }

            paintWidth = (int) (getWidth() * totalP);
            leftRatio = negP / totalP;
        }

        paintLeft = (int) (-paintWidth * leftRatio);

        paint = new LinearGradientPaint(paintLeft,
            getHeight(), paintLeft + paintWidth,
            getHeight(), activePositions,
            activeColors);
    }


    //////////
    // TEST //
    //////////

    private static final int MGP_W = 400;
    private static final int MGP_H = 400;
    private static final int MGP_X = 400;
    private static final int MGP_Y = 100;

    public static void main(String[] args) {
        full();
//        diagnostics();
    }

    private static void full() {
        EscapeFrame f = new EscapeFrame("Moving Gradient Panel: Full");

//        final MovingGradientPanel pnl = new MovingGradientPanel();
        final MovingGradientPanel pnl = new MovingGradientPanel(
            new Color[] {Color.green, Color.blue, Color.yellow},
            new float[] {ZERO, 0.25F, 0.25F}
        ).start();

        f.addClosingListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pnl.stop();
            }
        });

        ActionListener start = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.start();
            }
        };
        ActionListener stop = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.stop();
            }
        };
        ActionListener intv = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.setInterval(50);
            }
        };
        ActionListener params = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.setParameters(
                    new Color[] {Color.red, Color.orange, Color.black},
                    new float[] {ZERO, 0.25F, 0.25F}
                );
            }
        };
        ActionListener orien = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pnl.setOrientation(Orientation.UPPER_LEFT_TO_LOWER_RIGHT);
            }
        };
        Lay.BLtg(f,
            "N", Lay.FL("L",
                Lay.btn("start", start),
                Lay.btn("stop", stop),
                Lay.btn("int=50", intv),
                Lay.btn("params change", params),
                Lay.btn("corner-2-corner", orien)
            ),
            "C", pnl,
            "size=800,center,visible=true"
        );
    }

    private static void diagnostics() {
        EscapeFrame f = new EscapeFrame("Moving Gradient Panel: Diagnostics");

        final MovingGradientPanel pnl = new MovingGradientPanel(
            new Color[] {Color.green, Color.blue, Color.yellow},
            new float[] {ZERO, 0.25F, 0.25F}
        ).start();

        f.addClosingListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                pnl.stop();
            }
        });

        pnl.setSize(MGP_W, MGP_H);
        pnl.setLocation(MGP_X, MGP_Y);

        pnl.pnlOuter.setLayout(null);
        pnl.pnlOuter.add(pnl);

        Lay.BLtg(f,
            "C", pnl.pnlOuter,
            "size=[" + (MGP_W * 3 + 20) + ",620],center,visible=true"
        );
    }

    private class OuterPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            DrawUtil.grid(g, getWidth(), getHeight());

            if(paintWidth != 0) {

                g.setColor(Color.red);
                g.fillRect(MGP_X + paintLeft, 90, paintWidth, 10);
                g.fillRect(MGP_X + paintLeft, 500, paintWidth, 10);
                for(int i = 0; i < activeColors.length; i++) {
                    g.setColor(activeColors[i]);
                    int x = MGP_X + paintLeft + (int) (activePositions[i] * paintWidth);
                    g.drawLine(x,
                        0, x, getHeight());
                }

                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("LEFT=" + paintLeft, 50, 20);
                g.drawString("WIDTH=" + paintWidth, 330, 20);
                g.drawString("OFFSET=" + offset, 50, 50);
                g.drawString("SEEDdP=" + Arrays.toString(seedDeltaP), 50, 80);
                g.drawString("SEEDCL=" + cc(seedColors), 330, 50);
                g.drawString("SEEDcP=" + Arrays.toString(seedCumulP), 330, 80);
                g.drawString("ACTPOS=" + Arrays.toString(activePositions), 50, 540);
                g.drawString("ACTCLR=" + cc(activeColors), 50, 570);

                g.drawString("FOUND=" + found, 600, 20);
                g.drawString("EXTRAP=" + extraP, 600, 50);
                g.drawString("TOTALP=" + totalP, 600, 80);

            }
        }

        private String cc(Color[] activeColors) {
            String ret = "";
            for(Color c : activeColors) {
                if(c == Color.GREEN) {
                    ret += "G ";
                } else if(c == Color.blue) {
                    ret += "B ";
                }
            }
            return "[" + ret.trim() + "]";
        }
    }
}
