package replete.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.SwingWorker;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import replete.io.FileUtil;
import replete.ui.lay.Lay;
import replete.ui.text.RTextPane;
import replete.ui.windows.escape.EscapeFrame;

public class GradientTextPaneExample extends EscapeFrame {

    private Map<String, Double> quality = new HashMap<>();
    private RTextPane txt;

    public GradientTextPaneExample() {
        super("Gradient Text Pane Example");

        String content = FileUtil.getTextContent(GradientTextPaneExample.class.getResourceAsStream("gradient-tp-ex.txt"));
        final String[] parts = content.split("\\s+");
        Random R = new Random();
        for(int i = 0; i < 100; i++) {
            int partNum = R.nextInt(parts.length);
            double value = R.nextDouble();
            quality.put(parts[partNum], value);
        }

        Lay.BLtg(this,
            "C", Lay.sp(Lay.hn(txt = new RTextPane(), "editable=false")),
            "size=[700,700],center=2,visible"
        );

        waitOn();
        SwingWorker<StyledDocument, Void> worker = new SwingWorker<StyledDocument, Void>() {
            @Override
            protected StyledDocument doInBackground() throws Exception {
                StyledDocument doc = new DefaultStyledDocument();
                for(String part : parts) {
                    Double value = quality.get(part);
                    if(value == null) {
                        RTextPane.appendDoc(doc, part + " ");
                    } else {
                        Color clr = Color.getHSBColor(0.0F, 1F, value.floatValue());
                        Font fnt = txt.getFont().deriveFont(Font.BOLD);
                        RTextPane.appendDoc(doc, part, fnt, clr);
                        RTextPane.appendDoc(doc, " ");
                    }
                }
                return doc;
            }
            @Override
            protected void done() {
                try {
                    txt.setDocument(get());
                } catch(Exception e) {
                    e.printStackTrace();
                }
                waitOff();
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        new GradientTextPaneExample();
    }
/*
    private class ColorPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int phase = 0;
            int center = 128;
            int width = 127;
            double frequency = Math.PI*2/100;
            for(int i = 0; i < 100; i++) {
                System.out.println(Math.sin(frequency*i + 2));
                System.out.println(Math.sin(frequency*i + 0));
                System.out.println(Math.sin(frequency*i + 4));
                double red   = Math.sin(frequency*i + 2) * width + center;
                double green = Math.sin(frequency*i + 0) * width + center;
                double blue  = Math.sin(frequency*i + 4) * width + center;
                g.setColor(new Color((int) red, (int) green, (int) blue));
                g.drawLine(i, 0, i, 500);
            }
        }
    }*/
}
