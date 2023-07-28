package replete.ui.label;

import javax.swing.Icon;
import javax.swing.JLabel;

import replete.ui.lay.Lay;
import replete.ui.windows.ExampleFrame;


public class HLabel extends JLabel {

    public HLabel() {
    }

    public HLabel(String text) {
        super(text);
    }

    public HLabel(Icon image) {
        super(image);
    }

    public HLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public HLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public HLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }

    @Override
    public void setText(String text) {
        super.setText("<html>" + text + "</html>");
    }

    public static void main(String[] args) {
        ExampleFrame f = new ExampleFrame();
        Lay.FLtg(f, new HLabel("<u>hello</u>"));
        f.setVisible(true);
    }
}
