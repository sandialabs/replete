package replete.ui.button;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/**
 * A class to make creating a toggle button with just an icon
 * easy.
 *
 * @author Derek Trumbo
 */

public class ToggleIconButton extends JToggleButton {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ToggleIconButton(Icon icon) {
        this(icon, null, -1, null);
    }
    public ToggleIconButton(Icon icon, String ttt) {
        this(icon, ttt, -1, null);
    }
    public ToggleIconButton(Icon icon, int insets) {
        this(icon, null, insets, null);
    }
    public ToggleIconButton(Icon icon, String ttt, int insets) {
        this(icon, ttt, insets, null);
    }
    public ToggleIconButton(Icon icon, String ttt, int insets, ActionListener initialListener) {
      super(icon);

      // Action listener
      if(initialListener != null) {
          addActionListener(initialListener);
      }

      // Margin
      if(insets < 0) {
          insets = 0;
      }
      Insets margins = new Insets(insets, insets, insets, insets);
      setMargin(margins);

      // Tool tip text
      if(ttt != null) {
          setToolTipText(ttt);
      }

      // Orientation
      setVerticalTextPosition(BOTTOM);
      setHorizontalTextPosition(CENTER);
    }


    //////////
    // MISC //
    //////////

    public void toImageOnly() {
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
