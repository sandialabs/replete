package replete.ui.text;

import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeFrame;

public class CopiableLabel extends RTextArea {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CopiableLabel() {
        this("");
    }
    public CopiableLabel(String text) {
        Lay.hn(this, "bg=238,wrap,editable=false,cursor=text");
        setText(text);
    }

    public static void main(String[] args) {
        EscapeFrame fr;
        Lay.BLtg(fr = Lay.fr("size=[400,400]"), "N", new CopiableLabel("http://myawesomedomainname.com/this/is/the/coolest/site/ever"));
        fr.setVisible(true);
    }
}
