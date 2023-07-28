package replete.ui.sp;

import java.awt.Component;

import javax.swing.JScrollPane;

// Scroll pane that uses an EnhancedScrollPaneLayout.

public class EnhancedScrollPane extends JScrollPane {

    public EnhancedScrollPane() {
        super();
        init();
    }

    public EnhancedScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        init();
    }

    public EnhancedScrollPane(Component view) {
        super(view);
        init();
    }

    public EnhancedScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
        init();
    }

    private void init() {
        setLayout(new EnhancedScrollPaneLayout.UIResource());
    }

}
