package replete.ui.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.text.Document;

import replete.threads.SwingTimerManager;
import replete.ui.lay.Lay;
import replete.ui.text.validating.ValidatingTextField;
import replete.ui.text.validating.Validator;
import replete.ui.windows.escape.EscapeFrame;

public class GlowingValidatingTextField extends ValidatingTextField {


    ////////////
    // FIELDS //
    ////////////

    private static final Color DEFAULT_START_COLOR      = Lay.clr("FFFFFF");
    private static final Color DEFAULT_END_COLOR        = Lay.clr("FFFAB7");    // Light yellow
    private static final int   DEFAULT_GRADIENT_TIMEOUT = 200;
    private static final int   MAX_STEPS                = 10;

    private Color startColor;    // Set later
    private Color endColor = DEFAULT_END_COLOR;
    private int step = 0;
    private int gradientTimeout;
    private Timer tmrGradient;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public GlowingValidatingTextField() {
        super();
    }
    public GlowingValidatingTextField(Validator validator) {
        super(validator);
    }
    public GlowingValidatingTextField(String text) {
        super(text);
    }
    public GlowingValidatingTextField(String text, Validator validator) {
        super(text, validator);
    }
    public GlowingValidatingTextField(int columns) {
        super(columns);
    }
    public GlowingValidatingTextField(int columns, Validator validator) {
        super(columns, validator);
    }
    public GlowingValidatingTextField(String text, int columns) {
        super(text, columns);
    }
    public GlowingValidatingTextField(String text, int columns, Validator validator) {
        super(text, columns, validator);
    }
    public GlowingValidatingTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
    }


    @Override
    protected void init() {
        super.init();

        gradientTimeout = DEFAULT_GRADIENT_TIMEOUT;

        setStateColor(State.VALID, startColor = DEFAULT_START_COLOR);

        tmrGradient = SwingTimerManager.create(gradientTimeout, e -> {
            step++;
            if(step == MAX_STEPS) {
                step = 0;
            }
            setBackground(computeNextColor());
        });

        addStateListener(e -> {
            if(state == State.VALID) {
                step = 0;
                tmrGradient.restart();
            } else {
                tmrGradient.stop();
            }
        });
    }

    public Color getStartColor() {
        return startColor;
    }
    public int getGradientTimeout() {
        return gradientTimeout;
    }

    private Color computeNextColor() {
        int rbase = startColor.getRed();
        int gbase = startColor.getGreen();
        int bbase = startColor.getBlue();

        int rdel = endColor.getRed() - rbase;
        int gdel = endColor.getGreen() - gbase;
        int bdel = endColor.getBlue() - bbase;

        int divisions = MAX_STEPS - 1;

        double rstep = (double) rdel * step / divisions;
        double gstep = (double) gdel * step / divisions;
        double bstep = (double) bdel * step / divisions;

        double rnext = rbase + rstep;
        double gnext = gbase + gstep;
        double bnext = bbase + bstep;

        return new Color((int) rnext, (int) gnext, (int) bnext);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        EscapeFrame fra = new EscapeFrame();
        Validator validator = (txt, text) -> {
            return true;//!StringUtil.isBlank(text); //.contains("x");
        };
        GlowingValidatingTextField txt;
        JButton btnDo;
        Lay.BLtg(fra,
            "N", Lay.FL("L", txt = Lay.tx("", 20, validator, "glowing,validate")),
            "S", Lay.FL(btnDo = Lay.btn("Do")),
            "size=400,center,visible,toplevel"
        );
//        txt.setUnvalidatableDecider(null);
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txt.setValidText("aaxaa");
            }
        });
//        txt.setUnvalidatableDecider(new Predicate<String>() {
//            public boolean test(String t) {
//                return t.equals("orange");
//            }
//        });
//        txt.setStateColor(State.UNVALIDATED, Color.orange);
//        txt.enableDebug();

//        fra.addClosingListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
//                SwingTimerManager.shutdown();
//            }
//        });
    }
}
