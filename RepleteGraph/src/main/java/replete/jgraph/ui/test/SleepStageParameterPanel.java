package replete.jgraph.ui.test;

import javax.swing.event.DocumentEvent;

import replete.jgraph.test.SleepStage;
import replete.jgraph.test.StageWrapper;
import replete.numbers.NumUtil;
import replete.pipeline.events.ParameterChangeEvent;
import replete.pipeline.events.ParameterChangeListener;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;

public class SleepStageParameterPanel extends StageParameterPanel {
    private SleepStage sleepStage;
    private RTextField txtDuration;
    private boolean ignore;

    // QUESTION: are there TWO layers of "DIRTY" ? one when you edit
    // a stagewrapperpanel's contents, and then when it gets applied
    // to the stage, and the stage becomes dirty?  Check how Wave works.

    // QUESTION: Do we need "validation" on the parameter panels
    // before they can be set into the stage?

    public SleepStageParameterPanel(StageWrapper wrapper) {
        super(wrapper);
        sleepStage = (SleepStage) stage;

        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("Duration: "),
                "C", txtDuration = Lay.tx("" + sleepStage.getDuration(), "selectall"),
                "E", Lay.lb("ms"),
                "eb=5,opaque=false"
            )
        );

        txtDuration.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                if(NumUtil.isInt(txtDuration.getText())) {
                    // NOTE: Is there a framework / API solution to all this ignoring business????
                    ignore = true;
                    sleepStage.setDuration(Integer.parseInt(txtDuration.getText()));
                    ignore = false;
                }
            }
        });
        stage.addParameterChangeListener(new ParameterChangeListener() {
            public void stateChanged(ParameterChangeEvent e) {
                if(ignore) {
                    return;    // Some other way to do this?
                }
                if(e.getParameterName().equals("duration")) {
                    txtDuration.setText("" + e.getNewValue());
                }
            }
        });
    }
}

//Lay.BLtg(this,
//    "C", Lay.SPL("X",
//        Lay.GL(3, 1,
//            new JButton("A"),
//            new JButton("B"),
//            new JButton("C")
//        ),
//        Lay.sp(new JTable(
//            new Object[][] {{1, 2}, {3, 4}},
//            new Object[]{"Col1", "Col2"}))
//    )
//);
