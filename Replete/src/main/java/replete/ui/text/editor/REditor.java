package replete.ui.text.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;

import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.sp.RScrollPane;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.text.RTextPane;

public class REditor extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private RTextPane   txt;
    private RScrollPane scr;
    private RTextField  txQuery;
    private RButton     btnPrev;
    private RButton     btnNext;
    private JLabel      lblStatus;
    private JLabel      lblCount;
    private JPanel      pnlStatus;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public REditor(RTextPane txt, RScrollPane scr) {

        int counts[] = {0,0};
        this.txt = txt;
        this.scr = scr;

        txt.addPropertyChangeListener("font", e -> udpateRulerLineHeight());

        Font font = new Font("Courier New", Font.PLAIN, 12);
        setFont(font);
        txt.setTextualUneditable(true);
        txt.setShowCurrentLine(true);
        txt.setCurrentLineColor(Lay.clr("FFF18E"));

        Lay.BLtg(this,
            "C", scr,
            "S", pnlStatus = Lay.FL("R",
                lblStatus = Lay.lb(),
                lblCount = Lay.lb(CommonConcepts.SEARCH, "Search:"),
                btnPrev = Lay.btn(CommonConcepts.COLLAPSE_UP),
                btnNext = Lay.btn(CommonConcepts.COLLAPSE_DOWN),
                txQuery = Lay.tx("")
            )
        );

        txQuery.setPreferredSize(new Dimension(80, 23));

        btnNext.addActionListener(e -> {
            search(txQuery.getText(), counts, true);
            lblCount.setText("Search: " + counts[0] + "/" + counts[1]);
        });
        btnPrev.addActionListener(e -> {
            search(txQuery.getText(), counts, false);
            lblCount.setText("Search: " + counts[0] + "/" + counts[1]);
        });
        txQuery.addActionListener(e -> {
            search(txQuery.getText(), counts, true);
            lblCount.setText("Search: " + counts[0] + "/" + counts[1]);
        });
        txt.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                scr.setRowCount(getLines());
                updateStatusLine();
            }
        });
        txt.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
                updateStatusLine();
            }
        });
        updateStatusLine();

    }

    private void search(String query, int counts[], boolean forward) {
        int tmpIndex = 0;
        int lastIndex = 0;
        DefaultHighlightPainter hlRed = new DefaultHighlightPainter(Lay.clr("FF9632"));
        DefaultHighlightPainter hlYellow = new DefaultHighlightPainter(Lay.clr("ffff6e"));
        Highlighter hilite = txt.getHighlighter();
        txt.getHighlighter().removeAllHighlights();

        if(query == null || query.equals("")) {
            txt.getHighlighter().removeAllHighlights();
            counts[0] = 0;
            counts[1] = 0;
            return;
        }

        String content;
        try {
            Document d = txt.getDocument();
            content = d.getText(0, d.getLength()).toLowerCase();
        } catch(Exception e) {
            return;
        }

        int firstOffset = -1;
        query = query.toLowerCase();
        counts[1] = 0;
        while((lastIndex = content.indexOf(query, lastIndex)) != -1) {
            counts[1]++;
            if(firstOffset == -1) {
                firstOffset = lastIndex;
            }
            lastIndex += query.length();
        }

        if(forward) {
            counts[0] += 1;
            if(counts[0] > counts[1]) {
                counts[0] = 1;
            }
        } else {
            counts[0] -= 1;
            if(counts[0] < 1) {
                counts[0] = counts[1];
            }
        }


        while((lastIndex = content.indexOf(query, lastIndex)) != -1) {
            tmpIndex++;

            try {
                if (tmpIndex == counts[0]) {
                    hilite.addHighlight(lastIndex, lastIndex + query.length(), hlRed);
                    txt.getCaret().setDot(lastIndex);           //This is used to scroll to the selection
                } else {
                    hilite.addHighlight(lastIndex, lastIndex + query.length(), hlYellow);
                }
            } catch(BadLocationException e) {
                e.printStackTrace();
            }

            if(firstOffset == -1) {
              firstOffset = lastIndex;
            }
            lastIndex += query.length();
        }

        if(counts[1] == 0) {
            counts[0] = 0;
        }
    }

    private void updateStatusLine() {
        int chars = txt.getText().length();
        int lines = -1;
        int words = -1;
        lblStatus.setText(
            "Lines: " + lines +
            ", Words: " + words +
            ", Chars: " + chars +
            ", Cursor: " + txt.getCaret().getDot() +
            ", Cursor-Mark: " + txt.getCaret().getMark() +
            ", Sel Chars: " + Math.abs(txt.getCaret().getDot() - txt.getCaret().getMark())
        );
        // txt.search();     //triggered every click.
    }

    private int getLines(){
        int caretPosition = txt.getDocument().getLength();
        Element root = txt.getDocument().getDefaultRootElement();
        int lines = 1;
        for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++) {
            lines++;
        }
        return lines;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public RTextPane getTextPane() {
        return txt;
    }
    public RScrollPane getScrollPane() {
        return scr;
    }
    public boolean isShowStatusLine() {
        return pnlStatus != null;
    }
    public boolean isEditable() {
        return txt.isEditable();
    }
    public int getStartLineNumber() {
        return scr.getRulerPanel().getStartLineNumber();
    }

    // Mutators

    // Pass-throughs
    public String getText() {
        return txt.getText();
    }
    public void setText(String text) {
        txt.setText(text);
    }
    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if(txt != null) {
            txt.setFont(font);
        }
    }
    private void udpateRulerLineHeight() {
        if(scr != null) {  // Because setFont sometimes called by Swing framework before safe.
            FontMetrics fm = new JTextPane().getFontMetrics(txt.getFont());
            scr.getRulerPanel().setLineHeight(fm.getHeight());
        }
    }
    public void setShowStatusLine(boolean show) {
        if(show != isShowStatusLine()) {
            if(show) {
                pnlStatus = Lay.FL("R", lblStatus);
                add(pnlStatus, BorderLayout.SOUTH);
            } else {
                remove(pnlStatus);
                pnlStatus = null;
            }
            updateUI();
        }
    }
    public void setEditable(boolean editable) {
        txt.setEditable(editable);
    }
    public void setStartLineNumber(int startLineNumber) {
        scr.getRulerPanel().setStartLineNumber(startLineNumber);
    }
    public void setCaret(int position) {
        txt.setCaretPosition(position);
    }
    public void setShowRuler(boolean enabled) {
        if(scr != null) {
            scr.setShowRuler(enabled);
        }
    }

    public void selectAll() {
        txt.selectAll();
    }

    public void focus() {
        txt.focus();
    }


    public static void main(String[] args) {
        String text =
            "Call me Ishmael. Some years ago--never mind how long precisely--having\n" +
            "little or no money in my purse, and nothing particular to interest me on\n" +
            "shore, I thought I would sail about a little and see the watery part of\n" +
            "the world. It is a way I have of driving off the spleen and regulating\n" +
            "the circulation. Whenever I find myself growing grim about the mouth;\n" +
            "whenever it is a damp, drizzly November in my soul; whenever I find\n" +
            "myself involuntarily pausing before coffin warehouses, and bringing up\n" +
            "the rear of every funeral I meet; and especially whenever my hypos get\n" +
            "such an upper hand of me, that it requires a strong moral principle to\n" +
            "prevent me from deliberately stepping into the street, and methodically\n" +
            "knocking people's hats off--then, I account it high time to get to sea\n" +
            "as soon as I can. This is my substitute for pistol and ball. With a\n" +
            "philosophical flourish Cato throws himself upon his sword; I quietly\n" +
            "take to the ship. There is nothing surprising in this. If they but knew\n" +
            "it, almost all men in their degree, some time or other, cherish very\n" +
            "nearly the same feelings towards the ocean with me.";
        Lay.BLtg(Lay.fr("REditor Test"),
            "C", Lay.ed(text, "ruler"),
            "size=800,center,visible"
        );
    }
}
