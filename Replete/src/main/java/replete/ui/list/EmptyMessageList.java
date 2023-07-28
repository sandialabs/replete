package replete.ui.list;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import replete.ui.windows.escape.EscapeFrame;


/**
 * A list which shows a message centered within it
 * when there are no items in the list.
 *
 * @author Derek Trumbo
 */

public class EmptyMessageList<T> extends RList<T> {


    ////////////
    // FIELDS //
    ////////////

    protected String emptyMessage;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // No message

    public EmptyMessageList() {
        this((String) null);
    }
    public EmptyMessageList(ListModel<T> listData) {
        this(listData, null);
    }
    public EmptyMessageList(T[] listData) {
        this(listData, null);
    }
    public EmptyMessageList(Iterable<T> listData) {
        this(listData, null);
    }
    public EmptyMessageList(Vector<T> listData) {
        this(listData, null);
    }

    // Message

    public EmptyMessageList(String message) {
        super();
        emptyMessage = message;
    }
    public EmptyMessageList(ListModel<T> dataModel, String message) {
        super(dataModel);
        emptyMessage = message;
    }
    public EmptyMessageList(T[] listData, String message) {
        super(listData);
        emptyMessage = message;
    }
    public EmptyMessageList(Iterable<T> listData, String message) {
        super(listData);
        emptyMessage = message;
    }
    public EmptyMessageList(Vector<T> listData, String message) {
        super(listData);
        emptyMessage = message;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String msg) {
        emptyMessage = msg;
        repaint();
    }


    ///////////
    // PAINT //
    ///////////

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(getModel().getSize() == 0 && emptyMessage != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            Font font = getFont();
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            int textWidth = (int) font.getStringBounds(emptyMessage, frc).getWidth();

            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() / 2 - 20;

            g2.drawString(emptyMessage, x, y);
        }
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        final JFrame frame = new EscapeFrame("EmptyMessageList Test");
        frame.setLayout(new BorderLayout());
        final EmptyMessageList lst1 = new EmptyMessageList("Empty!");
        final EmptyMessageList lst2 = new EmptyMessageList(new String[]{"Red", "Blue"},"Empty!");
        JScrollPane scr1 = new JScrollPane(lst1);
        JScrollPane scr2 = new JScrollPane(lst2);
        JButton btnDo = new JButton("Do Something");
        btnDo.setMnemonic('D');
        btnDo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lst1.setListData(new Object[]{"Yellow", "Green"});
                lst2.setListData(new Object[0]);
                JPanel p = (JPanel)frame.getContentPane();
                p.updateUI();
            }
        });
        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnDo);
        frame.add(scr1, BorderLayout.WEST);
        frame.add(scr2, BorderLayout.EAST);
        frame.add(pnlButtons, BorderLayout.SOUTH);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
