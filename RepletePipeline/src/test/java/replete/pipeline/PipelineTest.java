package replete.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import replete.collections.Pair;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.InvalidNameException;
import replete.pipeline.errors.PipelineValidationException;
import replete.pipeline.test.BadInputStage;
import replete.pipeline.test.BadOutputStage;
import replete.pipeline.test.BasicStage;
import replete.pipeline.test.TestMultiplierStage;
import replete.pipeline.test.WaitStage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class PipelineTest {

    @Test
    public void testPipeline() {
        Pipeline pipeline = new Pipeline("pipeline name");
        assertEquals("pipeline name", pipeline.getName());
    }

    @Test
    public void testParallelExecute() {
        Pipeline pipeline = new Pipeline("pipeline name");
        pipeline.addStage(new WaitStage(1000));
        pipeline.addStage(new WaitStage(2000));
        pipeline.addStage(new WaitStage(3000));
        pipeline.execute();
        assertEquals("pipeline name", pipeline.getName());

        // TODO [DMT] Although we still need to do some
        // automated validation, here's some positive
        // output from this test for one of the runs
        // (allowing only 2 worker threads at a time)
        //   WaitStage beginning.  Waiting for 3000ms @ 1409877947456  <-- 1. start at same time
        //   WaitStage beginning.  Waiting for 1000ms @ 1409877947456  <-- 1. start at same time
        //   WaitStage ending.  Waited for 1000ms @ 1409877948470      <-- 2. start at same time
        //   WaitStage beginning.  Waiting for 2000ms @ 1409877948470  <-- 2. start at same time
        //   WaitStage ending.  Waited for 3000ms @ 1409877950468      <-- 3. end at same time
        //   WaitStage ending.  Waited for 2000ms @ 1409877950469      <-- 3. end at same time
    }

    @Test
    public void testAddStage() {
        Pipeline pipeline = new Pipeline("name");
        Stage stage0 = new BasicStage("0");
        assertEquals(0, pipeline.getStages().size());
        pipeline.addStage(stage0);
        assertSame(pipeline, stage0.getParent());
        assertEquals(1, pipeline.getStages().size());
        assertTrue(pipeline.getStages().contains(stage0));

        Stage stage1 = new BasicStage("1");
        pipeline.addStage(stage1);
        assertSame(pipeline, stage1.getParent());
        assertEquals(2, pipeline.getStages().size());
        assertTrue(pipeline.getStages().contains(stage0));
        assertTrue(pipeline.getStages().contains(stage1));
    }

    @Test(expected=PipelineValidationException.class)
    public void testAddStageNonNullParent() {
        Pipeline pipeline = new Pipeline("name");
        Stage stage0 = new BasicStage("0");
        stage0.setParent(pipeline);
        pipeline.addStage(stage0);
    }

    @Test(expected=PipelineValidationException.class)
    public void testAddStageBadInput() {
        Pipeline pipeline = new Pipeline("name");
        Stage stage0 = new BadInputStage("0");
        pipeline.addStage(stage0);
    }

    @Test(expected=PipelineValidationException.class)
    public void testAddStageBadOutput() {
        Pipeline pipeline = new Pipeline("name");
        Stage stage0 = new BadOutputStage("0");
        pipeline.addStage(stage0);
    }

    @Test
    public void testRemoveStage() {
        Pipeline pipeline = new Pipeline("name");
        Stage stage0 = new BasicStage("0");
        assertEquals(0, pipeline.getStages().size());
        pipeline.addStage(stage0);
        assertSame(pipeline, stage0.getParent());
        assertEquals(1, pipeline.getStages().size());
        assertTrue(pipeline.getStages().contains(stage0));

        Stage stage1 = new BasicStage("1");
        pipeline.addStage(stage1);
        assertSame(pipeline, stage1.getParent());
        assertEquals(2, pipeline.getStages().size());
        assertTrue(pipeline.getStages().contains(stage0));
        assertTrue(pipeline.getStages().contains(stage1));

        pipeline.removeStage(stage0);
        assertEquals(1, pipeline.getStages().size());
        assertFalse(pipeline.getStages().contains(stage0));
        assertTrue(pipeline.getStages().contains(stage1));
        assertNull(stage0.getParent());
    }

    @Test
    public void testExecuteString() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertEquals(0, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute(stages.get(0).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertTrue(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId(), false);

        ((BasicStage)stages.get(1)).setDirty(true);
        pipeline.execute(stages.get(1).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());
    }

    @Test
    public void testExecuteStringBoolean() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertEquals(0, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute(stages.get(0).getId(), true);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertTrue(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId(), true);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId(), true);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        pipeline.execute(stages.get(1).getId(), false);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());

        ((BasicStage)stages.get(1)).setDirty(true);
        pipeline.execute(stages.get(1).getId(), false);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(0, ((BasicStage)stages.get(5)).getExecutionCount());

        assertFalse(stages.get(0).isDirty());
        assertFalse(stages.get(1).isDirty());
        assertTrue(stages.get(2).isDirty());
        assertTrue(stages.get(3).isDirty());
        assertTrue(stages.get(4).isDirty());
        assertTrue(stages.get(5).isDirty());
    }

    @Test
    public void testExecuteFromString() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertEquals(0, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute();

        for(Stage stage : stages) {
            assertEquals(1, ((BasicStage)stage).getExecutionCount());
        }

       ((BasicStage)stages.get(2)).setDirty(true);

        pipeline.executeFrom(stages.get(2).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(5)).getExecutionCount());

       ((BasicStage)stages.get(3)).setDirty(true);

        pipeline.executeFrom(stages.get(3).getId());

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(5)).getExecutionCount());
    }

    @Test
    public void testExecuteFromStringBoolean() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertEquals(0, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute();

        for(Stage stage : stages) {
            assertEquals(1, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.executeFrom(stages.get(2).getId(), true);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(5)).getExecutionCount());

        pipeline.executeFrom(stages.get(3).getId(), true);

        assertEquals(1, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(1, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(5)).getExecutionCount());
    }

    @Test
    public void testExecuteBoolean() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertEquals(0, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute(false);

        for(Stage stage : stages) {
            assertEquals(1, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute(true);

        for(Stage stage : stages) {
            assertEquals(2, ((BasicStage)stage).getExecutionCount());
        }

        pipeline.execute(false);

        for(Stage stage : stages) {
            assertEquals(2, ((BasicStage)stage).getExecutionCount());
        }

        ((BasicStage)stages.get(3)).setDirty(true);
        pipeline.execute(false);

        assertEquals(2, ((BasicStage)stages.get(0)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(1)).getExecutionCount());
        assertEquals(2, ((BasicStage)stages.get(2)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(3)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(4)).getExecutionCount());
        assertEquals(3, ((BasicStage)stages.get(5)).getExecutionCount());
    }

    @Test
    public void testExecute() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedMultiplierPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(InputDescriptor in : pipeline.getInputDescriptors()) {
            System.out.println(in.getName());
            pipeline.setInput(in.getName(), 2);
        }
        System.out.println(pipeline.render());
        System.out.println(pipeline.getInputDescriptors());
        pipeline.execute();
        Map<String, Object> outputs = pipeline.getOutputs();
        assertEquals(1, outputs.size());
        String expName = pipeline.createInputOutputName(stages.get(3), "int");
        assertTrue(outputs.containsKey(expName));
        assertEquals(65536, outputs.get(expName));

        pair = constructBranchingLinkedMultiplierPipeline();
        pipeline = pair.getValue1();
        stages = pair.getValue2();

        Queue<Integer> inputs = new LinkedList<>();
        inputs.add(3);
        inputs.add(7);
        inputs.add(11);
        inputs.add(13);

        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            pipeline.setInput(input.getName(), inputs.remove());
        }

        pipeline.execute();

        outputs = pipeline.getOutputs();
        assertEquals(1, outputs.size());
        expName = pipeline.createInputOutputName(stages.get(2), "int");
        assertTrue(outputs.containsKey(expName));
        assertEquals(3003, outputs.get(expName));
    }

    @Test
    public void testGetInputDescriptors() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        assertEquals(2, pipeline.getInputDescriptors().size());
        Stage stage0 = stages.get(0);
        for(InputDescriptor in : pipeline.getInputDescriptors()) {
            try {
                Pair<String, String> p = pipeline.parseInputOutputName(in.getName());
                assertTrue(p.getValue1().equals(stage0.getId()));
                assertTrue(p.getValue2().equals("string1") || p.getValue2().equals("string2"));
            } catch (InvalidNameException e) {
                fail("Unexpected name parsing exception.");
            }
        }
    }

    @Test
    public void testGetOutputDescriptors() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stage5 = stages.get(5);
        assertEquals(1, pipeline.getOutputDescriptors().size());
        for(OutputDescriptor out : pipeline.getOutputDescriptors()) {
            try {
                Pair<String, String> p = pipeline.parseInputOutputName(out.getName());
                assertTrue(p.getValue1().equals(stage5.getId()));
                assertTrue(p.getValue2().equals("string"));
            } catch (InvalidNameException e) {
                fail("Unexpected name parsing exception.");
            }
        }
    }

    @Test
    public void testGetInputs() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        Map<String, Object> expInputs = new HashMap<>();
        int count = 0;
        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            expInputs.put(input.getName(), count);
            count++;
        }

        pipeline.setInputs(expInputs, false);

        Map<String, Object> inputs = pipeline.getInputs();
        assertEquals(pipeline.getInputDescriptors().size(), inputs.size());
        count = 0;
        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            assertEquals(expInputs.get(input.getName()), inputs.get(input.getName()));
            count++;
        }
    }

    @Test
    public void testGetInput() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        Map<String, Object> expInputs = new HashMap<>();
        int count = 0;
        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            expInputs.put(input.getName(), count);
            count++;
        }

        pipeline.setInputs(expInputs, false);

        count = 0;
        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            assertEquals(expInputs.get(input.getName()), pipeline.getInput(input.getName()));
            count++;
        }
    }

    @Test
    public void testGetOutput() {
        Pair<Pipeline, List<Stage>> pair = constructSequentialLinkedMultiplierPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(InputDescriptor in : pipeline.getInputDescriptors()) {
            System.out.println(in.getName());
            pipeline.setInput(in.getName(), 2);
        }
        pipeline.execute();
        Map<String, Object> outputs = pipeline.getOutputs();
        assertEquals(1, outputs.size());
        String expName = pipeline.createInputOutputName(stages.get(3), "int");
        assertTrue(outputs.containsKey(expName));
        assertEquals(65536, outputs.get(expName));

        pair = constructBranchingLinkedMultiplierPipeline();
        pipeline = pair.getValue1();
        stages = pair.getValue2();

        Queue<Integer> inputs = new LinkedList<>();
        inputs.add(3);
        inputs.add(7);
        inputs.add(11);
        inputs.add(13);

        for(InputDescriptor input : pipeline.getInputDescriptors()) {
            pipeline.setInput(input.getName(), inputs.remove());
        }

        pipeline.execute();

        expName = pipeline.createInputOutputName(stages.get(2), "int");
        assertEquals(3003, pipeline.getOutput(expName));
    }

    private Pair<Pipeline, List<Stage>> constructUnlinkedPipeline() {
        Pipeline pipeline = new Pipeline("name");
        List<Stage> stages = new ArrayList<>();
        for(int i = 0; i < 6; i++) {
            Stage stage = new BasicStage(Integer.toString(i));
            stages.add(stage);
        }
        for(Stage stage : stages) {
            pipeline.addStage(stage);
        }
        return new Pair<>(pipeline, stages);
    }

    private Pair<Pipeline, List<Stage>> constructUnlinkedMultiplierPipeline(int num) {
        Pipeline pipeline = new Pipeline("pipeline0");
        List<Stage> stages = new ArrayList<>();
        for(int i = 0; i < num; i++) {
            Stage stage = new TestMultiplierStage(Integer.toString(i));
            stages.add(stage);
        }
        for(Stage stage : stages) {
            pipeline.addStage(stage);
        }
        return new Pair<>(pipeline, stages);
    }

    private Pair<Pipeline, List<Stage>> constructSequentialLinkedMultiplierPipeline() {
        Pair<Pipeline, List<Stage>> pair = constructUnlinkedMultiplierPipeline(4);
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(int i = 0; i < stages.size() - 1; i++) {
            Stage stage1 = stages.get(i);
            Stage stage2 = stages.get(i + 1);
            pipeline.addLink(stage1.getOutputDescriptor("int"), stage2.getInputDescriptor("int1"));
            pipeline.addLink(stage1.getOutputDescriptor("int"), stage2.getInputDescriptor("int2"));
        }
        return pair;
    }

    private Pair<Pipeline, List<Stage>> constructBranchingLinkedMultiplierPipeline() {
        Pair<Pipeline, List<Stage>> pair = constructUnlinkedMultiplierPipeline(3);
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stage0 = stages.get(0);
        Stage stage1 = stages.get(1);
        Stage stage2 = stages.get(2);

        pipeline.addLink(stage0.getOutputDescriptor("int"), stage2.getInputDescriptor("int1"));
        pipeline.addLink(stage1.getOutputDescriptor("int"), stage2.getInputDescriptor("int2"));

        return pair;
    }

    private Pair<Pipeline, List<Stage>> constructSequentialLinkedPipeline() {
        Pair<Pipeline, List<Stage>> pair = constructUnlinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(int i = 0; i < stages.size() - 1; i++) {
            Stage stage1 = stages.get(i);
            Stage stage2 = stages.get(i + 1);
            pipeline.addLink(stage1.getOutputDescriptor("string"), stage2.getInputDescriptor("string1"));
            pipeline.addLink(stage1.getOutputDescriptor("string"), stage2.getInputDescriptor("string2"));
        }
        return pair;
    }

    private Pair<Pipeline, List<Stage>> constructBranchingLinkedPipeline() {
        Pair<Pipeline, List<Stage>> pair = constructUnlinkedPipeline();
        Pipeline pipeline = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        pipeline.addLink(stages.get(0).getOutputDescriptor("string"), stages.get(2).getInputDescriptor("string1"));
        pipeline.addLink(stages.get(1).getOutputDescriptor("string"), stages.get(2).getInputDescriptor("string2"));
        pipeline.addLink(stages.get(2).getOutputDescriptor("string"), stages.get(3).getInputDescriptor("string1"));
        pipeline.addLink(stages.get(2).getOutputDescriptor("string"), stages.get(3).getInputDescriptor("string2"));
        pipeline.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(4).getInputDescriptor("string1"));
        pipeline.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(4).getInputDescriptor("string2"));
        pipeline.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(5).getInputDescriptor("string1"));
        pipeline.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(5).getInputDescriptor("string2"));
        return pair;
    }
}
