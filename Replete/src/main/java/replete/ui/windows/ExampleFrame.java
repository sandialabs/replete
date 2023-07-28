package replete.ui.windows;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class ExampleFrame extends EscapeFrame {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ExampleFrame() {
        super("Example Frame");
        init();
    }
    public ExampleFrame(String title) {
        super(title);
        init();
    }


    //////////
    // INIT //
    //////////

    private void init() {
        Lay.hn(this, "size=[600,600],center,dco=dispose");
    }
}
