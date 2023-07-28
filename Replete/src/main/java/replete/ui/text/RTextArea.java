package replete.ui.text;

import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import replete.text.NewlineType;
import replete.text.StringUtil;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.panels.SelectionState;
import replete.ui.windows.Dialogs;

public class RTextArea extends JTextArea implements SelectionStateSavable {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RTextArea() {
        super();
    }
    public RTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
    }
    public RTextArea(Document doc) {
        super(doc);
    }
    public RTextArea(int rows, int columns) {
        super(rows, columns);
    }
    public RTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
    }
    public RTextArea(String text) {
        super(text);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public String getTrimmed() {
        return getText().trim();
    }
    public boolean isBlank() {
        return StringUtil.isBlank(getText());
    }
    public boolean isEmpty() {
        return getText().isEmpty();
    }

    // Mutators

    public void clear() {
        setText(null);
    }
    public void appendln(String str) {
        append(str + "\n");
    }
    public void setText(Object o) {   // Can handle all primitives as well
        if(o == null) {
            setText(null);
        } else {
            setText(o.toString());
        }
    }

    public void setText(String t, boolean moveCaretTopAfter) {
        super.setText(t);
        if(moveCaretTopAfter) {
            setCaretPosition(0);
        }
    }


    //////////
    // MISC //
    //////////

    public void focus() {
        requestFocusInWindow();
    }
    public void addChangeListener(DocumentListener listener) {
        getDocument().addDocumentListener(listener);
    }
    public void removeChangeListener(DocumentListener listener) {
        getDocument().removeDocumentListener(listener);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // The following methods all force that the strings given to the
    // JTextArea have NO carriage returns.  This is not just because
    // most of the Java RT uses line feed characters by default to
    // be standard across the various platforms and not because the
    // JTextArea itself only inserts LF's when the ENTER key is pressed,
    // but rather because the JTextArea DOES NOT SUPPORT proper
    // editing when CR's are present.  For example, if you place this
    // text into the JTextArea:
    //
    //     A\r\nB
    //
    // Then place the cursor after the 'A' and type for example an 'x',
    // then the JTextArea will now return this from getText():
    //
    //     A\rx\nB
    //
    // Which is entirely unacceptable.  The methods below should be
    // all the different ways where text can enter the component.
    // These methods all eventually end up editing the component's
    // underlying Document object.
    //
    // However, if the JTextArea is just given a new document after the
    // default one created during construction via
    //
    //     JTextComponent.setDocument(Document)
    //
    // it is unknown what corrective action we can take since we can't
    // really forcibly edit just any arbitrary document given to the
    // component.  Thus, these methods are just a best-effort patch
    // against buggy platform code.

    @Override
    public void append(String str) {
        str = StringUtil.convertNewlines(str, NewlineType.LF);
        super.append(str);
    }
    @Override
    public void insert(String str, int pos) {
        str = StringUtil.convertNewlines(str, NewlineType.LF);
        super.insert(str, pos);
    }
    @Override
    public void replaceRange(String str, int start, int end) {
        str = StringUtil.convertNewlines(str, NewlineType.LF);
        super.replaceRange(str, start, end);
    }
    @Override
    public void replaceSelection(String content) {
        content = StringUtil.convertNewlines(content, NewlineType.LF);
        super.replaceSelection(content);
    }
    @Override
    public void setText(String t) {
        t = StringUtil.convertNewlines(t, NewlineType.LF);
        super.setText(t);
    }
//    @Override
//    public void paste() {   // This is another way text could get into a JTextArea
//        super.paste();      // but I don't know how we'd get access to the text
//    }                       // being pasted to ensure it does not contain CR's
                              // (but testing has shown it probably already gets converted to LF's)

    // TODO: Add "creation methods" for only reselecting
    // the text if the text hasn't changed.  Only
    // behavior right now simply save/restores the
    // start/end indicies without looking at the
    // actual text that was selected.
    @Override
    public SelectionState getSelectionState() {
        return new SelectionState()
            .p("selStart", getSelectionStart())
            .p("selEnd", getSelectionEnd())
        ;
    }

    // NOTE: You won't actually see the text highlight until
    // the text area regains the focus!
    //
    // NOTE: This method may seem to change its parent JScrollPane
    // but it's actually later on a asynchronous repaint event
    // on the EDT where the cursor is required to be visible in
    // the scroll pane and the scroll pane is adjusted accordingly.
    // This is a little unintuitive, since the user may have scrolled
    // the cursor ("mark") out of the view after selecting some text,
    // which is just fine.  But this selection state save/restore
    // doesn't yet fully account for that nuance of the Swing API.
    @Override
    public void setSelectionState(SelectionState state) {
        select(state.getGx("selStart"), state.getGx("selEnd"));
        // ^Will silently modify the indicies internally so they're in bounds.
    }

//    public void printSelectionInfo() {
//        System.out.println("CAR: DOT=" + getCaret().getDot());
//        System.out.println("CAR: MRK=" + getCaret().getMark());
//        System.out.println("CAR: MCP=" + getCaret().getMagicCaretPosition());
//        System.out.println("CAR: SVS=" + getCaret().isSelectionVisible());
//        System.out.println("CAR: VIS=" + getCaret().isVisible());
//
//        System.out.println("CRP: " + getCaretPosition());
//        System.out.println("STX: " + getSelectedText());
//        System.out.println("SST: " + getSelectionStart());    // Math.min(caret.getDot(), caret.getMark());
//        System.out.println("SED: " + getSelectionEnd());      // Math.max(caret.getDot(), caret.getMark());
//    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        String originalText = "A\r\nB";
        JTextArea txt;
        Lay.BLtg(Lay.fr("JTextArea Test"),
            "C", txt = Lay.txa(originalText),
            "S", Lay.btn("Show Text", (ActionListener) e -> {
                Dialogs.showMessage(null, StringUtil.cleanControl(txt.getText()));
            }),
            "size=400,center,visible=true"
        );
    }
}
