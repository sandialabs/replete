package replete.ui.text;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;

import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;

public class NullDisplayTextField extends RTextField {


    ////////////
    // FIELDS //
    ////////////

    private boolean showNull = false;
    private boolean suppressShowNullChange = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NullDisplayTextField() {
        init();
    }
    public NullDisplayTextField(Document doc, String text, int columns) {
        super(doc, text, columns);
        init();
    }
    public NullDisplayTextField(int columns) {
        super(columns);
        init();
    }
    public NullDisplayTextField(String text) {
        super(text);
        init();
    }
    public NullDisplayTextField(String text, int columns) {
        super(text, columns);
        init();
    }

    private void init() {
        setSelectAll(true);

        addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if(!suppressShowNullChange) {
                    showNull = false;
                    repaint();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE && e.isControlDown()) {
                    setShowNull(true);
                }
            }
        });
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessor

    public boolean isShowNull() {
        return showNull;
    }

    // Mutator

    public void setShowNull(boolean showNull) {
        this.showNull = showNull;
        if(showNull) {
            suppressShowNullChange = true;
            setText("");
            suppressShowNullChange = false;
        }
        repaint();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(showNull) {
            int leftOver = getHeight() - 16;
            g.drawImage(ImageLib.getImg(CommonConcepts.NULL), 3, leftOver / 2, 16, 16, null);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final NullDisplayTextField txt;
        Lay.BLtg(Lay.fr("test"),
            "N", Lay.hn(txt = new NullDisplayTextField("hi"), "size=24"),
            "S", Lay.FL(
                Lay.btn("null", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txt.setShowNull(true);
                    }
                }),
                Lay.btn("not null", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txt.setShowNull(false);
                    }
                }),
                Lay.btn("type text", new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txt.setText("hi");
                    }
                })
            ),
            "size=400,visible,center"
        );
    }
}
