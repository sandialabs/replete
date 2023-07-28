package replete.ui.list.cb;

import java.awt.Color;
import java.awt.Font;

public class ItemDetails {
    public boolean checked;
    public boolean enabled;
    public Color fg;
    public Color bg;
    public Font font;
    public ItemDetails() {
        checked = false;
        enabled = true;
        fg = null;
        font = null;
    }
}
