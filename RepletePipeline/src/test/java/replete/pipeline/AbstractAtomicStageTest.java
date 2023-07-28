package replete.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Test;

import replete.collections.MapUtil;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.DescriptorException;
import replete.pipeline.errors.InputCardinalityException;
import replete.pipeline.errors.InputNullException;
import replete.pipeline.errors.InputValidationException;
import replete.pipeline.errors.InvalidInputIndexException;
import replete.pipeline.errors.InvalidInputTypeException;
import replete.pipeline.errors.MissingRequiredInputException;
import replete.pipeline.errors.OutputDescriptorException;
import replete.pipeline.errors.OutputUnsetException;
import replete.pipeline.errors.UnrecognizedInputDescriptorException;
import replete.pipeline.errors.UnrecognizedInputException;
import replete.pipeline.errors.UnrecognizedOutputDescriptorException;
import replete.pipeline.errors.UnrecognizedOutputException;
import replete.pipeline.events.InputChangeEvent;
import replete.pipeline.events.InputChangeListener;
import replete.pipeline.events.OutputChangeEvent;
import replete.pipeline.events.OutputChangeListener;
import replete.pipeline.test.BlankStage;
import replete.pipeline.test.CardinalityStage;
import replete.pipeline.test.ComplexStage;
import replete.pipeline.test.MisregisterStage;

public class AbstractAtomicStageTest {

    @Test
    public void blank() {
        BlankStage stage = new BlankStage();

        assertTrue(stage.getId().startsWith("replete.pipeline.test.BlankStage#"));
        assertEquals(Stage.DEFAULT_STAGE_NAME, stage.getName());
        assertNull(stage.getParent());
        assertFalse(stage.hasError());
        assertNull(stage.getError());
        assertTrue(stage.isDirty());
        assertFalse(stage.isExecuted());
        ExecuteSummary summary = stage.getExecuteSummary();
        assertEquals(ExecuteSummary.UNSET, summary.getDuration().getLast());
        assertEquals(0, summary.getDuration().getCount());
        assertEquals(0, summary.getDuration().getTotal());
        assertNull(summary.getError());
        assertEquals(0, summary.getExecuteAttemptedCount());
        assertEquals(0, summary.getExecuteSuccessCount());
        assertEquals(0, summary.getExecuteFailedCount());
        assertEquals(new ArrayList<StageWarning>(), stage.getWarnings());
        testUnmodifiableList(stage.getWarnings());

        // Input and output descriptors must be empty and unmodifiable.
        testBlankDescriptors(stage);
        Map<String, Object> blankMap = testBlankInputs(stage);
        // Sorted, Input Type, Dirty for another type of stage
        testBlankOutputs(stage, blankMap);
        // Sorted, Output change event for another type of stage
        // Cannot test output unset with blank
        // Cannot test the get*Output* methods further with Blank (values)

        stage.execute();
        // Validate inputs for another type of stage

        // vv Should not have changed vv
        assertTrue(stage.getId().startsWith("replete.pipeline.test.BlankStage#"));
        assertEquals(Stage.DEFAULT_STAGE_NAME, stage.getName());
        assertNull(stage.getParent());
        assertFalse(stage.hasError());
        assertNull(stage.getError());
        // vv Should have changed vv
        assertFalse(stage.isDirty());
        assertTrue(stage.isExecuted());
        summary = stage.getExecuteSummary();
        assertTrue(timingGood(summary.getDuration().getLast(), 1));
        assertEquals(1, summary.getDuration().getCount());
        assertTrue(timingGood(summary.getDuration().getTotal(), 1));
        assertNull(summary.getError());
        assertEquals(1, summary.getExecuteAttemptedCount());
        assertEquals(1, summary.getExecuteSuccessCount());
        assertEquals(0, summary.getExecuteFailedCount());
        assertEquals(new ArrayList<StageWarning>(), stage.getWarnings());

        // Input and output descriptors must be empty and unmodifiable.
        testBlankDescriptors(stage);
        testBlankInputs(stage);
        // Sorted, Input Type, Dirty for another type of stage
        testBlankOutputs(stage, blankMap);
        // Sorted, Output change event for another type of stage
        // Cannot test output unset with blank
        // Cannot test the get*Output* methods further with Blank (values)
    }

    private void testBlankDescriptors(BlankStage stage) {
        List<InputDescriptor> ids = stage.getInputDescriptors();
        List<OutputDescriptor> ods = stage.getOutputDescriptors();
        assertEquals(new ArrayList<InputDescriptor>(), ids);
        assertEquals(new ArrayList<OutputDescriptor>(), ods);
        testUnmodifiableList(ids);
        testUnmodifiableList(ods);
        // Existence
        try {
            stage.getInputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedInputDescriptorException e) {}
        try {
            stage.getOutputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}
        // Published list is same as the output descriptor list
        List<OutputDescriptor> pods = stage.getPublishedOutputDescriptors();
        assertEquals(pods, ods);
        testUnmodifiableList(pods);
        // Existence
        try {
            stage.getPublishedOutputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}
        // Modification methods
        try {
            stage.addPublishedOutputDescriptor(blankODesc(stage));
            fail();
        } catch(OutputDescriptorException e) {}
        try {
            stage.removePublishedOutputDescriptor(blankODesc(stage));
            fail();
        } catch(OutputDescriptorException e) {}
        try {
            stage.registerInputDescriptor(blankIDesc(stage));
            fail();
        } catch(DescriptorException e) {}
        try {
            stage.registerOutputDescriptor(blankODesc(stage));
            fail();
        } catch(DescriptorException e) {}
    }

    @Test
    public void misregister() {
        MisregisterStage stage = new MisregisterStage();

        // Accessors

        assertTrue(stage.getId().startsWith("replete.pipeline.test.MisregisterStage#"));
        assertEquals("Misregister", stage.getName());
        assertNull(stage.getParent());
        assertTrue(stage.isDirty());
        assertFalse(stage.isExecuted());
        assertFalse(stage.hasError());
        assertNull(stage.getError());

        // I/O Descriptors

        List<InputDescriptor> ids = stage.getInputDescriptors();
        List<OutputDescriptor> ods = stage.getOutputDescriptors();
        assertEquals(1, ids.size());
        InputDescriptor id = ids.get(0);
        assertEquals(stage, id.getParent());
        assertEquals("Name", id.getName());
        assertEquals(1, ods.size());
        OutputDescriptor od = ods.get(0);
        assertEquals(stage, od.getParent());
        assertEquals("XName", od.getName());
        testUnmodifiableList(ids);
        testUnmodifiableList(ods);

        stage.setInput("Name", "Abraham Lincoln");

        stage.execute();  // Tests both input and output descriptor registration

        assertEquals("XAbraham LincolnX", stage.getOutput("XName"));
    }

    @Test
    public void cardinality() {
        CardinalityStage stage = new CardinalityStage();

        // Blank getInput* methods
        for(int i = 0; i < 4; i++) {
            assertNull(stage.getInput("dir" + i));
            assertFalse(stage.hasInput("dir" + i));
            List<Object> inputList = stage.getInputMulti("dir1");
            testUnmodifiableList(inputList);
            assertEquals(new ArrayList<>(), inputList);
        }
        Map<String, Object> blankMap = new HashMap<String, Object>();
        assertEquals(blankMap, stage.getInputs());
        testUnmodifiableMap(stage.getInputs());
        Map<String, List<Object>> blankMap2 = new HashMap<>();
        for(int i = 0; i < 4; i++) {
            blankMap2.put("dir" + i, new ArrayList<>());
        }
        assertEquals(blankMap2, stage.getInputsMulti());
        testUnmodifiableMap(stage.getInputsMulti());

        try {
            stage.getInput("NonExistent");
        } catch(UnrecognizedInputException e) {}
        try {
            stage.getInputMulti("NonExistent");
        } catch(UnrecognizedInputException e) {}

        try {
            stage.execute();    // dir2 doesn't exist yet
            fail();
        } catch(MissingRequiredInputException e) {}
        stage.setInput("dir2", "east0");
        try {
            stage.execute();    // dir2 not minimum of 4
            fail();
        } catch(InputCardinalityException e) {}
        stage.addInputMulti("dir2", "east1");
        stage.addInputMulti("dir2", "east2");
        stage.addInputMulti("dir2", "east3");
        try {
            stage.execute();    // dir3 doesn't exist yet
            fail();
        } catch(MissingRequiredInputException e) {}
        stage.addInputMulti("dir2", "east4");
        stage.addInputMulti("dir2", "east5");
        stage.addInputMulti("dir2", "east6");
        try {
            stage.execute();    // dir2 not maximum of of 6
            fail();
        } catch(InputCardinalityException e) {}
        stage.removeInputMulti("dir2", 6);
        try {
            stage.execute();    // dir3 doesn't exist yet
            fail();
        } catch(MissingRequiredInputException e) {}
        for(int i = 0; i < 6; i++) {
            stage.addInputMulti("dir3", "north" + i);
        }
        stage.execute();
        stage.setInputMulti("dir0", Arrays.asList(new Object[] {"south0", "south1"}));
        stage.setInputMulti("dir1", Arrays.asList(new Object[] {"west0", "west1", "west2"}));
        try {
            stage.setInputMulti("dir1", Arrays.asList(new Object[] {2, 3, 4}));
            fail();
        } catch(InvalidInputTypeException e) {}
        try {
            stage.addInputMulti("dir1", 5);
            fail();
        } catch(InvalidInputTypeException e) {}
        try {
            stage.setInputs(MapUtil.sToI("dir0=0,dir1=1,dir2=2,dir3=3"), false);
            fail();
        } catch(InvalidInputTypeException e) {}
        try {
            stage.setInputsMulti(MapUtil.sToLofI("dir0=[0],dir1=[1],dir2=[2],dir3=[3]"), false);
            fail();
        } catch(InvalidInputTypeException e) {}
        assertEquals(2, stage.getInputMulti("dir0").size());
        assertEquals(3, stage.getInputMulti("dir1").size());
        assertEquals(6, stage.getInputMulti("dir2").size());
        assertEquals(6, stage.getInputMulti("dir3").size());
        assertEquals(MapUtil.sToS("dir0=south0,dir1=west0,dir2=east0,dir3=north0"), stage.getInputs());
        stage.setInput("dir3", null);
        assertEquals(1, stage.getInputMulti("dir3").size());
        stage.removeInput("dir1");
        assertEquals(0, stage.getInputMulti("dir1").size());
        assertEquals(MapUtil.sToS("dir0=south0,dir2=east0,dir3=null"), stage.getInputs());
        try {
            stage.execute();    // dir0 too many
            fail();
        } catch(InputCardinalityException e) {}
        stage.removeInputMulti("dir0", 1);
        try {
            stage.execute();    // dir3 too few
            fail();
        } catch(InputCardinalityException e) {}
        for(int i = 0; i < 6; i++) {
            stage.addInputMulti("dir3", "north" + i);
        }
        stage.execute();
        try {
            stage.removeInputMulti("dir3", -1);
            fail();
        } catch(InvalidInputIndexException e) {}
        try {
            stage.removeInputMulti("dir3", 7);
            fail();
        } catch(InvalidInputIndexException e) {}
        stage.removeInputMulti("dir3", 6);
        Map<String, List<Object>> map = MapUtil.sToLofS("dir0=[a],dir1=[],dir2=[a;b;c;d],dir3=[a;b;c;d;e;f]");
        stage.setInputsMulti(map, true);
        assertEquals(1, stage.getInputMulti("dir0").size());
        assertEquals(0, stage.getInputMulti("dir1").size());
        assertEquals(4, stage.getInputMulti("dir2").size());
        assertEquals(6, stage.getInputMulti("dir3").size());
        stage.execute();
        map = MapUtil.sToLofS("dir0=null,dir1=null,dir2=[a;b;c;d],dir3=[a;b;c;d;e;f;g;h;i]");
        stage.setInputsMulti(map, true);
        assertEquals(0, stage.getInputMulti("dir0").size());
        assertEquals(0, stage.getInputMulti("dir1").size());
        assertEquals(4, stage.getInputMulti("dir2").size());
        assertEquals(9, stage.getInputMulti("dir3").size());
        stage.execute();
        stage.addInputMulti("dir2", null);
        try {
            stage.execute();
            fail();
        } catch(InputNullException e) {}
        stage.removeInputMulti("dir2", 4);
        stage.execute();
        stage.setInputsMulti(null, false);
        assertEquals(0, stage.getInputMulti("dir0").size());
        assertEquals(0, stage.getInputMulti("dir1").size());
        assertEquals(4, stage.getInputMulti("dir2").size());
        assertEquals(9, stage.getInputMulti("dir3").size());
        assertEquals(2, stage.getInputs().size());
        assertEquals(4, stage.getInputsMulti().size());
        stage.setInput("dir0", null);
        assertEquals(1, stage.getInputMulti("dir0").size());
        stage.setInputsMulti(null, true);
        assertEquals(0, stage.getInputMulti("dir0").size());
        assertEquals(0, stage.getInputMulti("dir1").size());
        assertEquals(0, stage.getInputMulti("dir2").size());
        assertEquals(0, stage.getInputMulti("dir3").size());
        assertNull(stage.getInput("dir0"));
        assertNull(stage.getInput("dir1"));
        assertNull(stage.getInput("dir2"));
        assertNull(stage.getInput("dir3"));
        assertEquals(0, stage.getInputs().size());
        assertEquals(4, stage.getInputsMulti().size());
        stage.removeInput("dir3");
        try {
            stage.removeInputMulti("dir3", 3);
            fail();
        } catch(InvalidInputIndexException e) {}
    }

    private int dirtyCalls = 0;
    private int startCalls = 0;
    private int completeCalls = 0;
    private int inputCalls = 0;
    private int outputCalls = 0;

    @Test
    public void complex() {
        final ComplexStage stage = new ComplexStage();
        stage.addDirtyListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirtyCalls++;
                // System.out.println(stage.isDirty());
                // Outputs -> False, True, False, True, False, True, False, True
            }
        });
        stage.addStartListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                startCalls++;
            }
        });
        stage.addCompleteListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                completeCalls++;
            }
        });
        stage.addInputChangeListener(new InputChangeListener() {
            @Override
            public void stateChanged(InputChangeEvent e) {
                inputCalls++;
            }
        });
        stage.addOutputChangeListener(new OutputChangeListener() {
            @Override
            public void stateChanged(OutputChangeEvent e) {
                outputCalls++;
            }
        });

        assertTrue(stage.getId().startsWith("replete.pipeline.test.ComplexStage#"));
        assertEquals("Complex", stage.getName());
        assertNull(stage.getParent());
        assertFalse(stage.hasError());
        assertNull(stage.getError());
        assertTrue(stage.isDirty());
        assertFalse(stage.isExecuted());
        ExecuteSummary summary = stage.getExecuteSummary();
        assertEquals(ExecuteSummary.UNSET, summary.getDuration().getLast());
        assertEquals(0, summary.getDuration().getCount());
        assertEquals(0L, summary.getDuration().getTotal());
        assertNull(summary.getError());
        assertEquals(0, summary.getExecuteAttemptedCount());
        assertEquals(0, summary.getExecuteSuccessCount());
        assertEquals(0, summary.getExecuteFailedCount());
        assertEquals(0, dirtyCalls);
        assertEquals(0, startCalls);
        assertEquals(0, completeCalls);
        assertEquals(0, inputCalls);
        assertEquals(0, outputCalls);
        assertEquals(new ArrayList<StageWarning>(), stage.getWarnings());

        testComplexDescriptors(stage);
        Map<String, Object> blankMap = testBlankInputs(stage);
        testBlankOutputs(stage, blankMap);

        try {
            stage.getOutput("mag");
            fail();
        } catch(OutputUnsetException e) {}
        try {
            stage.getOutput("color");
            fail();
        } catch(OutputUnsetException e) {}

        try {
            stage.execute();
            fail();
        } catch(MissingRequiredInputException e) {}

        try {
            stage.getOutput("mag");
            fail();
        } catch(OutputUnsetException e) {}
        try {
            stage.getOutput("color");
            fail();
        } catch(OutputUnsetException e) {}

        // vv Should not have changed vv
        assertTrue(stage.getId().startsWith("replete.pipeline.test.ComplexStage#"));
        assertEquals("Complex", stage.getName());
        assertNull(stage.getParent());
        // vv Should have changed vv
        assertTrue(stage.hasError());
        assertTrue(stage.getError() instanceof MissingRequiredInputException);
        assertTrue(stage.isDirty());
        assertFalse(stage.isExecuted());
        summary = stage.getExecuteSummary();
        assertEquals(ExecuteSummary.UNSET, summary.getDuration().getLast());
        assertEquals(0, summary.getDuration().getCount());
        assertEquals(0L, summary.getDuration().getTotal());
        assertTrue(summary.getError() instanceof MissingRequiredInputException);
        assertEquals(1, summary.getExecuteAttemptedCount());
        assertEquals(0, summary.getExecuteSuccessCount());
        assertEquals(1, summary.getExecuteFailedCount());
        assertEquals(0, dirtyCalls);
        assertEquals(1, startCalls);
        assertEquals(1, completeCalls);
        assertEquals(0, inputCalls);
        assertEquals(0, outputCalls);
        assertEquals(new ArrayList<StageWarning>(), stage.getWarnings());

        testComplexDescriptors(stage);
        testBlankInputs(stage);
        testBlankOutputs(stage, blankMap);

        try {
            stage.setInput("dir", 12);  // Only required input
            fail();
        } catch(InvalidInputTypeException e) {}
        stage.setInput("dir", "north"); // Stays dirty

        stage.execute();                // OK to execute now, now clean
        assertEquals(0L, stage.getOutput("mag"));
        assertSame(Color.black, stage.getOutput("color"));

        try {
            stage.setInput("angle", 12);
            fail();
        } catch(InvalidInputTypeException e) {}
        stage.setInput("angle", 12.0);  // Back to dirty

        stage.execute();                // Back to clean
        assertEquals(24L, stage.getOutput("mag"));
        assertSame(Color.yellow, stage.getOutput("color"));

        // vv Should not have changed vv
        assertTrue(stage.getId().startsWith("replete.pipeline.test.ComplexStage#"));
        assertEquals("Complex", stage.getName());
        assertNull(stage.getParent());
        // vv Should have changed vv
        assertFalse(stage.hasError());
        assertNull(stage.getError());
        assertFalse(stage.isDirty());
        assertTrue(stage.isExecuted());
        summary = stage.getExecuteSummary();
//        assertTrue(timingGood(summary.getDuration().getLast(), 1));
        assertEquals(2, summary.getDuration().getCount());
//        assertTrue(timingGood(summary.getDuration().getTotal(), 2));
        assertNull(summary.getError());
        assertEquals(3, summary.getExecuteAttemptedCount());
        assertEquals(2, summary.getExecuteSuccessCount());
        assertEquals(1, summary.getExecuteFailedCount());
        assertEquals(3, dirtyCalls);
        assertEquals(3, startCalls);
        assertEquals(3, completeCalls);
        assertEquals(2, inputCalls);
        assertEquals(4, outputCalls);
        assertEquals(1, stage.getWarnings().size());
        assertEquals(stage.getName(), stage.getWarnings().get(0).getParent());
        assertEquals("warning! north", stage.getWarnings().get(0).getMessage());

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("dir", "cheese");     // Will fail input validation
        stage.setInputs(inputs, false);
        assertTrue(stage.hasInput("angle"));
        stage.setInputs(inputs, true);
        assertFalse(stage.hasInput("angle"));

        try {
            stage.execute();
            fail();
        } catch(InputValidationException e) {}

        inputs = new HashMap<>();
        inputs.put("dir", "south");
        inputs.put("angle", 75.0);
        stage.setInputs(inputs, true);

        stage.execute();

        assertEquals(510L, stage.getOutput("mag"));
        assertSame(Color.black, stage.getOutput("color"));

        assertFalse(stage.hasError());
        assertNull(stage.getError());
        assertFalse(stage.isDirty());
        assertTrue(stage.isExecuted());
        summary = stage.getExecuteSummary();
//        assertTrue(timingGood(summary.getDuration().getLast(), 1));
        assertEquals(3, summary.getDuration().getCount());
//        assertTrue(timingGood(summary.getDuration().getTotal(), 3));
        assertNull(summary.getError());
        assertEquals(5, summary.getExecuteAttemptedCount());
        assertEquals(3, summary.getExecuteSuccessCount());
        assertEquals(2, summary.getExecuteFailedCount());
        assertEquals(5, dirtyCalls);
        assertEquals(5, startCalls);
        assertEquals(5, completeCalls);
        assertEquals(8, inputCalls);    // Includes clearInputs calls in setInputs
        assertEquals(6, outputCalls);
        assertEquals(1, stage.getWarnings().size());
        assertEquals(stage.getName(), stage.getWarnings().get(0).getParent());
        assertEquals("warning! south", stage.getWarnings().get(0).getMessage());

        // Test clear & input / output sorting
        stage.clearInputs();
        assertEquals(0, stage.getInputs().size());
        stage.setInput("angle", 13.0);
        stage.setInput("dir", "east");
        inputs = stage.getInputs();
        boolean first = true;
        for(String name : inputs.keySet()) {
            if(first && !name.equals("dir")) {
                fail();
            } else if(!first && !name.equals("angle")) {
                fail();
            }
            first = false;
        }
        Map<String, Object> outputs = stage.getOutputs();
        first = true;
        for(String name : outputs.keySet()) {
            if(first && !name.equals("mag")) {
                fail();
            } else if(!first && !name.equals("color")) {
                fail();
            }
            first = false;
        }

        stage.removeInput("angle");  // Default of 0 is used in stage
        stage.execute();
        assertEquals(180L, stage.getOutput("mag"));
        assertSame(Color.black, stage.getOutput("color"));

        stage.removeInput("dir");
        try {
            stage.execute();
        } catch(MissingRequiredInputException e) {}

        // Outputs have not changed
        assertEquals(180L, stage.getOutput("mag"));
        assertSame(Color.black, stage.getOutput("color"));

        assertTrue(stage.hasError());
        assertTrue(stage.getError() instanceof MissingRequiredInputException);
        assertTrue(stage.isDirty());
        assertTrue(stage.isExecuted());
        summary = stage.getExecuteSummary();
        assertEquals(ExecuteSummary.UNSET, summary.getDuration().getLast());
        assertEquals(4, summary.getDuration().getCount());
//        assertTrue(timingGood(summary.getDuration().getTotal(), 4));
        assertTrue(summary.getError() instanceof MissingRequiredInputException);
        assertEquals(7, summary.getExecuteAttemptedCount());
        assertEquals(4, summary.getExecuteSuccessCount());
        assertEquals(3, summary.getExecuteFailedCount());
        assertEquals(8, dirtyCalls);
        assertEquals(7, startCalls);
        assertEquals(7, completeCalls);
        assertEquals(13, inputCalls);
        assertEquals(8, outputCalls);
        assertEquals(0, stage.getWarnings().size());

        // Internal stage error

        stage.setInput("angle", -1.0);
        stage.setInput("dir", "east");
        try {
            stage.execute();
            fail();
        } catch(IllegalArgumentException e) {}

        assertTrue(stage.hasError());
        assertTrue(stage.getError() instanceof IllegalArgumentException);
        assertTrue(stage.isDirty());
        assertTrue(stage.isExecuted());
        summary = stage.getExecuteSummary();
        assertTrue(summary.getDuration().getLast() < 10);  // Fails before ThreadUtil.sleep.
        assertEquals(5, summary.getDuration().getCount());
//        assertTrue(timingGood(summary.getDuration().getTotal(), 4));  // 4 because fails before ThreadUtil.sleep
        assertTrue(summary.getError() instanceof IllegalArgumentException);
        assertEquals(8, summary.getExecuteAttemptedCount());
        assertEquals(4, summary.getExecuteSuccessCount());
        assertEquals(4, summary.getExecuteFailedCount());
        assertEquals(8, dirtyCalls);
        assertEquals(8, startCalls);
        assertEquals(8, completeCalls);
        assertEquals(15, inputCalls);
        assertEquals(8, outputCalls);
        assertEquals(0, stage.getWarnings().size());

        // Null Allowed Test
        stage.setInput("dir", null);
        try {
            stage.execute();
            fail();
        } catch(InputNullException e) {}
    }

    private void testComplexDescriptors(ComplexStage stage) {
        List<InputDescriptor> ids = stage.getInputDescriptors();
        List<OutputDescriptor> ods = stage.getOutputDescriptors();
        List<OutputDescriptor> pods = stage.getPublishedOutputDescriptors();
        assertEquals(pods, ods);

        assertEquals(2, ids.size());
        InputDescriptor id0 = ids.get(0);
        assertEquals(stage, id0.getParent());
        assertEquals("dir", id0.getName());
        InputDescriptor id1 = ids.get(1);
        assertEquals(stage, id1.getParent());
        assertEquals("angle", id1.getName());

        assertEquals(2, ods.size());
        OutputDescriptor od0 = ods.get(0);
        assertEquals(stage, od0.getParent());
        assertEquals("mag", od0.getName());
        OutputDescriptor od1 = ods.get(1);
        assertEquals(stage, od1.getParent());
        assertEquals("color", od1.getName());

        assertEquals(2, pods.size());
        OutputDescriptor pod0 = pods.get(0);
        assertEquals(stage, pod0.getParent());
        assertEquals("mag", pod0.getName());
        OutputDescriptor pod1 = pods.get(1);
        assertEquals(stage, pod1.getParent());
        assertEquals("color", pod1.getName());

        testUnmodifiableList(ids);
        testUnmodifiableList(ods);
        testUnmodifiableList(pods);

        // Existence
        assertSame(id0, stage.getInputDescriptor("dir"));
        assertSame(id1, stage.getInputDescriptor("angle"));
        try {
            stage.getInputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedInputDescriptorException e) {}
        try {
            stage.getInputDescriptor("Angle");
            fail();
        } catch(UnrecognizedInputDescriptorException e) {}
        assertSame(od0, stage.getOutputDescriptor("mag"));
        assertSame(od1, stage.getOutputDescriptor("color"));
        try {
            stage.getOutputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}
        try {
            stage.getOutputDescriptor("Color");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}
        assertSame(pod0, stage.getPublishedOutputDescriptor("mag"));
        assertSame(pod1, stage.getPublishedOutputDescriptor("color"));
        try {
            stage.getPublishedOutputDescriptor("NonExistent");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}
        try {
            stage.getPublishedOutputDescriptor("Mag");
            fail();
        } catch(UnrecognizedOutputDescriptorException e) {}

        // Modification methods
        try {
            stage.addPublishedOutputDescriptor(blankODesc(stage));
            fail();
        } catch(OutputDescriptorException e) {}
        try {
            stage.removePublishedOutputDescriptor(blankODesc(stage));
            fail();
        } catch(OutputDescriptorException e) {}
        try {
            stage.registerInputDescriptor(blankIDesc(stage));
            fail();
        } catch(DescriptorException e) {}
        try {
            stage.registerOutputDescriptor(blankODesc(stage));
            fail();
        } catch(DescriptorException e) {}
    }


    //////////
    // MISC //
    //////////

    private Map<String, Object> testBlankInputs(Stage stage) {
        Map<String, Object> blankMap = new HashMap<String, Object>();
        Map<String, Object> inputs = stage.getInputs();
        assertEquals(blankMap, inputs);
        testUnmodifiableMap(inputs);
        try {
            stage.getInput("NonExistent");
            fail();
        } catch(UnrecognizedInputException e) {}
        try {
            stage.hasInput("NonExistent");
            fail();
        } catch(UnrecognizedInputException e) {}
        try {
            Map<String, Object> newInputs = new HashMap<String, Object>();
            newInputs.put("NonExistent", 0);
            stage.setInputs(newInputs, false);  // Cannot test setInputs further with blank
            fail();
        } catch(UnrecognizedInputException e) {}
        try {
            stage.setInput("NonExistent", 0);   // Cannot test setInput further with blank
            fail();
        } catch(UnrecognizedInputException e) {}
        try {
            stage.removeInput("NonExistent");
            fail();
        } catch(UnrecognizedInputException e) {}
        return blankMap;
    }
    private void testBlankOutputs(Stage stage, Map<String, Object> blankMap) {
        Map<String, Object> outputs = stage.getOutputs();
        assertEquals(blankMap, outputs);
        testUnmodifiableMap(outputs);
        try {
            stage.getOutput("NonExistent");
            fail();
        } catch(UnrecognizedOutputException e) {}
        try {
            stage.hasOutput("NonExistent");
            fail();
        } catch(UnrecognizedOutputException e) {}
        // No setOutputs
        if(stage instanceof AbstractAtomicStage) {
            try {
                ((AbstractAtomicStage) stage).setOutput("NonExistent", 0);
                fail();
            } catch(UnrecognizedOutputException e) {}
        }
        Map<String, Object> pOutputs = stage.getPublishedOutputs();
        assertEquals(blankMap, pOutputs);
        testUnmodifiableMap(pOutputs);
        try {
            stage.getPublishedOutput("NonExistent");
            fail();
        } catch(UnrecognizedOutputException e) {}
        try {
            stage.hasPublishedOutput("NonExistent");
            fail();
        } catch(UnrecognizedOutputException e) {}
    }

    private void testUnmodifiableList(List list) {
        try {
            list.add("test");
            fail();
        } catch(UnsupportedOperationException e) {}
        try {
            list.clear();
            fail();
        } catch(UnsupportedOperationException e) {}
    }
    private void testUnmodifiableMap(Map map) {
        try {
            map.put("test", 0);
            fail();
        } catch(UnsupportedOperationException e) {}
        try {
            map.clear();
            fail();
        } catch(UnsupportedOperationException e) {}
    }

    private OutputDescriptor blankODesc(Stage stage) {
        return new OutputDescriptor(stage, "OD", null, null, Object.class);
    }
    private InputDescriptor blankIDesc(Stage stage) {
        return new InputDescriptor(stage, "OD", null, null, Object.class, false);
    }

    private boolean timingGood(long last, int i) {
        int LEFT_THRESH = 0;
        int RIGHT_THRESH = 40;    // Cutting it pretty close but works on old laptop.
        return
            last >= BlankStage.WAIT * i - LEFT_THRESH * i &&
            last <= BlankStage.WAIT * i + RIGHT_THRESH * i;
    }
}
