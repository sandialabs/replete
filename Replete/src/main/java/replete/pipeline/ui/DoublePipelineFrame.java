package replete.pipeline.ui;

import java.awt.Color;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import replete.pipeline.DoublePipeline;
import replete.pipeline.stages.AddStage;
import replete.pipeline.stages.AdditiveInvertStage;
import replete.pipeline.stages.DivideStage;
import replete.pipeline.stages.ExponentiateStage;
import replete.pipeline.stages.LogBaseEStage;
import replete.pipeline.stages.LogBaseTenStage;
import replete.pipeline.stages.LogBaseTwoStage;
import replete.pipeline.stages.LogStage;
import replete.pipeline.stages.MultiplicativeInvertStage;
import replete.pipeline.stages.MultiplyStage;
import replete.pipeline.stages.ParameterStage;
import replete.pipeline.stages.RootStage;
import replete.pipeline.stages.SquareRootStage;
import replete.pipeline.stages.SquareStage;
import replete.pipeline.stages.Stage;
import replete.pipeline.stages.SubtractStage;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.shared.SharedImage;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RTextArea;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class DoublePipelineFrame extends EscapeFrame{

    private static final long serialVersionUID = -2531909128189582619L;

    private static final int     columns = 5;
    private static final Stage[] stages = {
        new AdditiveInvertStage(),
        new AddStage(),
        new DivideStage(),
        new ExponentiateStage(),
        new LogBaseEStage(),
        new LogBaseTenStage(),
        new LogBaseTwoStage(),
        new LogStage(),
        new MultiplicativeInvertStage(),
        new MultiplyStage(),
        new RootStage(),
        new SquareRootStage(),
        new SquareStage(),
        new SubtractStage()
    };

    JList               lstAvailableStages;
    RTextArea           txtStageDescription;
    JPanel              pnlPickedStages;
    JFormattedTextField txtPipelineInput;
    List<StagePanel>    pickedStages  = new ArrayList<>();

    private static final String invalidInputErrorMessage = " is not a valid value. Please enter an integer or a double";



    public DoublePipelineFrame() {
        super("Double Pipeline");
        RPanel pipeline= new RPanel();
        //background.setBackground(new Color(100,100,100));
//        JPanel stageLabels = Lay.BxL("size=1000");
//        stageLabels.add(Lay.btn("Add Selected", CommonConcepts.ADD));
//        for(int i = 0; i < stages.length; i++) {
//            Stage stage = stages[i];
//            JPanel stagePanel = Lay.BL(
//                //"W", Lay.lb(stage.getImage()),
//                "W", Lay.lb(stage.getShortDescription()),
//                "C", Lay.lb(stage.getName())
//            );
//            stagePanel.setToolTipText(stages[i].getDescription());
//            stagePanel.setBackground(Color.WHITE);
//            stageLabels.add(stagePanel);
//        }

        DefaultListModel<StageWrapper> mdlAvailableStages = new DefaultListModel<>();
        for(Stage stage : stages) {
            mdlAvailableStages.addElement(new StageWrapper(stage));
        }
        RButton   btnAdd;
        RPanel    pnlStageDescription = null;
        RButton   btnExecute;
        Lay.BLtg(this,
            "N", Lay.BL(
                "W", Lay.lb("PIPELINES!"),
                "E", Lay.FL(
                    Lay.lb("Pipeline initial value:"),
                    txtPipelineInput = createIntegerField(null, columns)
                )
            ),
            "C", Lay.SPL(
                Lay.BL(
                    "N", Lay.FL(
                        Lay.lb("Available Operations"),
                        btnAdd = Lay.btn("Add Selected", CommonConcepts.ADD)
                    ),
                    "C", Lay.sp(
                        lstAvailableStages = Lay.lst(mdlAvailableStages, "seltype=single")
                    ),
                    "S", Lay.BLtg(pnlStageDescription,
                        "C", Lay.p(
                             txtStageDescription = Lay.txa("Stage Description", "wrap")
                        ),
                        "dimh=55"
                    )
                ),
                pnlPickedStages = Lay.BxL()
            ),
            "S", Lay.FL(btnExecute = Lay.btn("Execute", CommonConcepts.RUN)),
            "size=1000"
        );
        // Region called description region, write description to it
        btnAdd.addActionListener(e -> addSelectedItem());
        btnExecute.addActionListener(e -> run());
        lstAvailableStages.addListSelectionListener(e -> {
            displayStageDescription();
        });
    }

    private void run() {
        Double input = null;
        try {
            input = Double.parseDouble(txtPipelineInput.getText());
            txtPipelineInput.setBackground(Color.WHITE);
        } catch (NumberFormatException e) {
            txtPipelineInput.setBackground(Color.RED);
            JOptionPane.showMessageDialog(this, txtPipelineInput.getText() + invalidInputErrorMessage);
            return;
        }
        DoublePipeline pipe = new DoublePipeline();
        for(StagePanel stage: pickedStages) {
            Stage<Double> stageLocal = stage.getExecutionStage();
            if(stageLocal != null) {
                pipe.addStage(stageLocal);
            } else {
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Result is " + pipe.execute(input));
    }

    private void displayStageDescription() {
        StageWrapper wrapper = (StageWrapper) lstAvailableStages.getSelectedValue();
        Stage stage = wrapper.stage;
        txtStageDescription.setText(stage.getDescription());
    }

    private void addSelectedItem() {
        StageWrapper wrapper = (StageWrapper) lstAvailableStages.getSelectedValue();
        Stage<Double> stage = wrapper.stage;
        StagePanel panel = new StagePanel(stage);
        pnlPickedStages.add(panel);
        pickedStages.add(panel);
        validate();
    }

//    private JPanel makeStagePanel(Stage stage) {
//        JPanel pnlRet = Lay.FL(
//                         Lay.lb(stage.getName()),
//                         Lay.lb(stage.getDescription())
//                     );
//        if (stage instanceof ParameterStage) {
//            pnlRet.add(Lay.lb("FIELD GOES HERE"));
//        }
//        return pnlRet;
//    }

    private class StageWrapper {
        private Stage<Double> stage;

        public StageWrapper(Stage<Double> stage) {
            this.stage = stage;
        }

        @Override
        public String toString() {
            return stage.getName();
        }

    }

    private class StagePanel extends RPanel {

        private static final long serialVersionUID = 5365918061338034983L;
        private Stage<Double>       stage;
        private JFormattedTextField txtStageInput;
        private RButton             btnRemove;
        private RButton             btnUp;
        private RButton             btnDown;

        public StagePanel(Stage<Double> stage) {
            this.setStage(stage);
            JPanel pnlOptions;
            btnRemove = Lay.btn("Remove Stage", CommonConcepts.REMOVE);
            btnRemove.addActionListener(e -> removeStage());
            btnUp = Lay.btn("Move Up", SharedImage.ARROW_UP_YELLOW);
            btnUp.addActionListener(e -> moveStageUp());
            btnDown = Lay.btn("Move Down", SharedImage.ARROW_DOWN_YELLOW);
            btnDown.addActionListener(e -> moveStageDown());
            JPanel pnlUpDown = Lay.FL(btnUp, btnDown);
            if (stage instanceof ParameterStage) {
                pnlOptions = Lay.FL(
                    txtStageInput = createIntegerField(null, columns),
                    pnlUpDown,
                    btnRemove
                );
            }
            else {
                pnlOptions = Lay.FL(
                    pnlUpDown,
                    btnRemove
                );
            }
            Lay.BLtg(this,
                "W", Lay.lb(stage.getName(), "dimw=150"),
                "C", Lay.lb(stage.getShortDescription()),
                "E", pnlOptions,
                "dimh=40"
            );
        }


        private void moveStageDown() {
            int index = pickedStages.indexOf(this);
            reArrangeStages(index, index + 1);
        }


        private void moveStageUp() {
            int index = pickedStages.indexOf(this);
            reArrangeStages(index, index - 1);
        }


        private void reArrangeStages(int originalIndex, int newIndex) {
            if(0 <= newIndex && newIndex < pickedStages.size()) {
                pnlPickedStages.removeAll();
                Collections.swap(pickedStages, originalIndex, newIndex);
                for(StagePanel panel : pickedStages) {
                    pnlPickedStages.add(panel);
                }
                pnlPickedStages.validate();
                pnlPickedStages.repaint();
                validate();
            }
        }


        public Stage<Double> getStage() {
            return stage;
        }
        public void setStage(Stage<Double> stage) {
            this.stage = stage;
        }
        public Double getFieldValue() {
            if(!(stage instanceof ParameterStage)) {
                return null;
            } else {
                try {
                    txtStageInput.setBackground(Color.WHITE);
                    return Double.parseDouble((String) txtStageInput.getText());
                } catch(NumberFormatException e) {
                    txtStageInput.setBackground(Color.RED);
                    JOptionPane.showMessageDialog(this, txtStageInput.getText() + invalidInputErrorMessage);
                    return null;
                }
            }
        }

        public Stage<Double> getExecutionStage() {
            if(stage instanceof ParameterStage) {
                Double fieldValue = getFieldValue();
                if(fieldValue != null) {
                    return ((ParameterStage<Double>) stage).spawnCopy(fieldValue);
                } else {
                    return null;
                }
            }
            return stage;
        }

        private void removeStage() {
            pnlPickedStages.remove(this);

            pnlPickedStages.validate();
            pnlPickedStages.repaint();
            validate();
            pickedStages.remove(this);
        }
    }

    private JFormattedTextField createIntegerField(Integer def, int columns) {
        JFormattedTextField field = new JFormattedTextField();
        field.setColumns(columns);
        if(def != null) {
            field.setValue(String.valueOf(def));
        }
        return field;
    }

    public static void main(String[] args) {
        DoublePipelineFrame test = new DoublePipelineFrame();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                Dialogs.showDetails(test, "There was an unexpected error in thread " + t, "Error", e);
            }
        });
        test.setVisible(true);
    }
}
