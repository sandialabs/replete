package replete.ui.sp;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.GuiUtil;
import replete.ui.SelectionStateCreationMethod;
import replete.ui.SelectionStateSavable;
import replete.ui.lay.Lay;
import replete.ui.list.RList;
import replete.ui.list.RList.ListSelectionStateIdentityMethod;
import replete.ui.panels.SelectionState;
import replete.ui.text.RTextArea;
import replete.ui.text.RTextPane;

public class RScrollPane extends EnhancedScrollPane implements SelectionStateSavable {


    ///////////
    // ENUMS //
    ///////////

    public enum ScrollPaneSelectionStateCreationMethod implements SelectionStateCreationMethod {
        RECORD_INNER,       // (Default) Record scroll pane's viewport location and contained component
        RECORD_SELF_ONLY    // Only record scroll pane's viewport location
    }


    ////////////
    // FIELDS //
    ////////////

    private RulerPanel pnlRuler;
    private Component cmpView;
    private RulerModel rhModel;
    private boolean rulerShown = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RScrollPane() {
        super();
        init(false);
    }
    public RScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        init(false);
    }
    public RScrollPane(Component view) {
        super(view);
        init(false);
    }
    public RScrollPane(int vsbPolicy, int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
        init(false);
    }
    public RScrollPane(boolean ruler) {
        super();
        init(ruler);
    }
    public RScrollPane(Component view, int vsbPolicy, int hsbPolicy, boolean ruler) {
        super(view, vsbPolicy, hsbPolicy);
        init(ruler);
    }
    public RScrollPane(Component view, boolean ruler) {
        super(view);
        init(ruler);
    }
    public RScrollPane(int vsbPolicy, int hsbPolicy, boolean ruler) {
        super(vsbPolicy, hsbPolicy);
        init(ruler);
    }

    private void init(boolean ruler) {
        rhModel = new RulerModel();
        pnlRuler = new RulerPanel(rhModel);
        pnlRuler.setRightToLeft(isFlipRowHeaderSide());
        setShowRuler(ruler);
        getVerticalScrollBar().setUnitIncrement(16);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public RulerModel getRulerModel() {
        return rhModel;
    }
    public RulerPanel getRulerPanel() {
        return pnlRuler;
    }
    public boolean isShowRangesAndIcons() {
        if(pnlRuler == null) {
            return false;   // Save state?
        }
        return pnlRuler.isShowRangesAndIcons();
    }
    public boolean isShowRuler() {
        return rulerShown;
    }

    public void setRowCount(int r) {
        if(pnlRuler != null) {
            pnlRuler.setRowCount(r);
            resizeRowHeader();
        }
    }

    public void setShowRuler(boolean enabled) {
        if(enabled) {
            enableRuler();
        } else {
            disableRuler();
        }
    }

    private void enableRuler() {
        // Turn on range/icons ?
        getViewport().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                if(cmpView == null || cmpView != getViewport().getView()) {
                    if(cmpView != null) {
                        cmpView.removeComponentListener(listener);
                    }
                    cmpView = getViewport().getView();
                    cmpView.addComponentListener(listener);
                    updateRowHeaderSize();
                }
            }
        });
        setRowHeaderView(pnlRuler);
        updateUI();
        getRowHeader().updateUI();
        rulerShown = true;
    }

    private void disableRuler() {
        if(cmpView != null) {
            cmpView.removeComponentListener(listener);
        }
        setRowHeaderView(null);
        updateUI();
        getRowHeader().updateUI();
        rulerShown = false;
    }

    public boolean isFlipRulerSide() {
        return isFlipRowHeaderSide();
    }
    public boolean isFlipRowHeaderSide() {
        EnhancedScrollPaneLayout layout = (EnhancedScrollPaneLayout) getLayout();
        return layout.isFlipRowHeaderSide();
    }

    public void setFlipRulerSide(boolean flip) {
        setFlipRowHeaderSide(flip);
    }
    public void setFlipRowHeaderSide(boolean flip) {
        EnhancedScrollPaneLayout layout = (EnhancedScrollPaneLayout) getLayout();
        layout.setFlipRowHeaderSide(flip);
        pnlRuler.setRightToLeft(flip);
        revalidate();
        repaint();
    }

    public void setShowRangesAndIcons(boolean show) {
        if(pnlRuler != null) {
            pnlRuler.setShowRangesAndIcons(show);
            resizeRowHeader();
        }
    }

    public int getMinRulerDigitColumns() {
        return pnlRuler.getMinRulerDigitColumns();
    }
    public void setMinRulerDigitColumns(int digits) {
        if(pnlRuler != null) {
            pnlRuler.setMinRulerDigitColumns(digits);
            resizeRowHeader();
        }
    }


    //////////
    // MISC //
    //////////

    private ComponentListener listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            updateRowHeaderSize();
        }
    };

    private void updateRowHeaderSize() {
        pnlRuler.setHeight(cmpView.getSize().height + 16);
        resizeRowHeader();
    }
    private void resizeRowHeader() {
        GuiUtil.safe(() -> getRowHeader().updateUI());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public SelectionState getSelectionState(Object... args) {
        ScrollPaneSelectionStateCreationMethod method =
            getDefaultArg(args, ScrollPaneSelectionStateCreationMethod.RECORD_INNER);

        SelectionState state = new SelectionState()
            .p("hsbValue", getHorizontalScrollBar().getValue())
            .p("vsbValue", getVerticalScrollBar().getValue())
        ;

        if(method == ScrollPaneSelectionStateCreationMethod.RECORD_INNER) {
            state.putSsIf("viewComp", getViewport().getView());
        }

        return state;
    }

    @Override
    public void setSelectionState(SelectionState state) {
        getHorizontalScrollBar().setValue(state.getGx("hsbValue"));
        getVerticalScrollBar().setValue(state.getGx("vsbValue"));
        state.setSsIf(getViewport().getView(), "viewComp");
    }


    //////////
    // MISC //
    //////////

    public void print() {
        System.out.println("VWP: VPO=" + getViewport().getViewPosition());
        System.out.println("VWP: VRC=" + getViewport().getViewRect());
        System.out.println("VWP: VSZ=" + getViewport().getViewSize());
        System.out.println("VWP: EXS=" + getViewport().getExtentSize());
        System.out.println("VWP: SCM=" + getViewport().getScrollMode());
        //scrollPane.getViewport().setViewPosition(new java.awt.Point(x, y));
///txtArea.setCaretPosition(0);
//m_ScrollPane.getVerticalScrollBar().setValue(0);

        System.out.println(getViewport().getView());

        System.out.println("VSB: VAL=" + getVerticalScrollBar().getValue());
        System.out.println("VSB: MIN=" + getVerticalScrollBar().getMinimum());
        System.out.println("VSB: MAX=" + getVerticalScrollBar().getMaximum());
        System.out.println("VSB: VIS=" + getVerticalScrollBar().getVisibleAmount());
        System.out.println("VSB: BLK=" + getVerticalScrollBar().getBlockIncrement());
        System.out.println("HSB: VAL=" + getHorizontalScrollBar().getValue());
        System.out.println("HSB: MIN=" + getHorizontalScrollBar().getMinimum());
        System.out.println("HSB: MAX=" + getHorizontalScrollBar().getMaximum());
        System.out.println("HSB: VIS=" + getHorizontalScrollBar().getVisibleAmount());
        System.out.println("HSB: BLK=" + getHorizontalScrollBar().getBlockIncrement());
    }


    //////////
    // TEST //
    //////////

    private static SelectionState ss;
    public static void setSs(SelectionState ss) {
        RScrollPane.ss = ss;
        System.out.println("SS=" + ss);
    }
    public static void main(String[] args) {
        RScrollPane scr00, scr01, scr10, scr11;
        RList lst00;
        RTextArea txt10;
        RTextPane txt11;

        Object[] i = new Object[] {
            "aaa", "bbb", "ccc", "cccasd asdfdsf asdfas df asdfa sddff sadfsd asdf asd", "ccc",
            "ddd", "ddd", "ddd", "eee", "eee", "fff"
        };
        DefaultListModel mdl = new DefaultListModel<>();
        for(Object j : i) {
            mdl.addElement(j);
        }
        Lay.GLtg(Lay.fr(), 2, 2,
            Lay.BL(
                "C", scr00 = Lay.sp(lst00 = Lay.lst(mdl)),
                "S", Lay.WL(
                    Lay.btn("SP PRINT",   (ActionListener) e -> scr00.print()),
                    Lay.btn("SP GET SS",  (ActionListener) e -> setSs(scr00.getSelectionState())),
                    Lay.btn("SP SET SS",  (ActionListener) e -> scr00.setSelectionState(ss)),
                    Lay.btn("GE&T SS",    (ActionListener) e -> setSs(lst00.getSelectionState())),
                    Lay.btn("GE&T SS HC", (ActionListener) e -> setSs(lst00.getSelectionState(ListSelectionStateIdentityMethod.OBJECT_HASH_CODE))),
                    Lay.btn("GE&T SS CN", (ActionListener) e -> setSs(lst00.getSelectionState(ListSelectionStateIdentityMethod.OBJECT_CLASS_NAME))),
                    Lay.btn("&SET SS",    (ActionListener) e -> lst00.setSelectionState(ss)),
                    Lay.btn("CHG SEL MD", (ActionListener) e -> lst00.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)),
                    Lay.btn("CLEAR",      (ActionListener) e -> lst00.setModel(new DefaultListModel())),
                    Lay.btn("ADD",        (ActionListener) e -> {
                        ((DefaultListModel) lst00.getModel()).addElement("qqq");
                        ((DefaultListModel) lst00.getModel()).addElement("qqq");
                        ((DefaultListModel) lst00.getModel()).addElement("qqq");
                        ((DefaultListModel) lst00.getModel()).addElement("rrr");
                        ((DefaultListModel) lst00.getModel()).addElement("rrr");
                        ((DefaultListModel) lst00.getModel()).addElement("sss");
                        ((DefaultListModel) lst00.getModel()).addElement("ttt");
                    }),
                    Lay.btn("REMOVE",     (ActionListener) e -> lst00.removeSelected()),
                    Lay.btn("RESET",      (ActionListener) e -> {
                        DefaultListModel mdl2 = new DefaultListModel();
                        for(Object j : i) {
                            mdl2.addElement(j);
                        }
                        lst00.setModel(mdl2);
                    })
                )
            ),
            Lay.BL(
                "C", scr01 = Lay.sp(Lay.lst((Object) i)),
                "S", Lay.FL("L", Lay.btn("A"))
            ),
            Lay.BL(
                "C", scr10 = Lay.sp(txt10 = Lay.txa("kj lkj l  kj kj j ;ljl;kj l;kj;lk j;k j hkjhlkj hlkjh kjh lkjh lk hljkh l", "font=Monospaced,size=14")),
                "S", Lay.FL("L",
                    Lay.btn("SP PRINT",  (ActionListener) e -> scr10.print()),
                    Lay.btn("SP GET SS", (ActionListener) e -> setSs(scr10.getSelectionState())),
                    Lay.btn("SP SET SS", (ActionListener) e -> scr10.setSelectionState(ss)),
                    Lay.btn("TXT SEL",   (ActionListener) e -> txt10.select(3, 6))
                )
            ),
            Lay.BL(
                "C", scr11 = Lay.sp(txt11 = Lay.txp("kj lkj l  kj kj j ;ljl;kj l;kj;lk j;k j hkjhlkj hlkjh kjh lkjh lk hljkh l", "font=Monospaced,size=14")),
                "S", Lay.FL("L",
                    Lay.btn("SP PRINT",  (ActionListener) e -> scr11.print()),
                    Lay.btn("SP GET SS", (ActionListener) e -> setSs(scr11.getSelectionState())),
                    Lay.btn("SP SET SS", (ActionListener) e -> scr11.setSelectionState(ss)),
                    Lay.btn("TXT SEL",   (ActionListener) e -> txt11.select(3, 6))
                )
            ),
            "size=800,center,visible"
        );

        scr10.getHorizontalScrollBar().addAdjustmentListener(e -> {
            System.out.println("changing to " + scr10.getHorizontalScrollBar().getValue());
        });
    }
}
