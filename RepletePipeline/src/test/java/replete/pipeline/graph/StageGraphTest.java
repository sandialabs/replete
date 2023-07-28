package replete.pipeline.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import replete.collections.MapUtil;
import replete.collections.Pair;
import replete.pipeline.Stage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.UnrecognizedInputDescriptorException;
import replete.pipeline.errors.graph.DuplicateLinkException;
import replete.pipeline.errors.graph.GraphException;
import replete.pipeline.errors.graph.InvalidLinkException;
import replete.pipeline.errors.graph.LinkCycleException;
import replete.pipeline.test.BasicStage;
import replete.pipeline.test.MultiInputStage;
import replete.pipeline.test.MultiTypeStage;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StageGraphTest {

    private final int STAGE_COUNT = 6;

    // Test: stagegraph with cardinality in addition: link already exists between out and in, card max

    ////////////
    // STAGES //
    ////////////

    @Test
    public void testAddStage() {
        // [0], [1], [2], [3], [4], [5]
        Pair<StageGraph, List<Stage>> pair = constructUnlinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertTrue(graph.getStages().contains(stage));
            assertTrue(graph.getSourceStages().contains(stage));
        }

        assertEquals(STAGE_COUNT, graph.getStageCount());

        String name = "NewOne";
        Stage stage = new BasicStage(name);
        assertFalse(graph.hasStage(stage));
        assertNull(graph.getStageById(stage.getId()));
        graph.addStage(stage);
        assertTrue(graph.hasStage(stage));
        assertEquals(STAGE_COUNT + 1, graph.getStageCount());
        assertEquals(stage, graph.getStageById(stage.getId()));
    }

    @Test
    public void testRemoveStage() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(Stage stage : stages) {
            assertTrue(graph.getStages().contains(stage));
        }
        assertTrue(graph.getSourceStages().contains(stages.get(0)));
        assertEquals(1, graph.getSourceStages().size());

        // Construct a sequential graph over and over and
        // remove each of the stages inside.
        int size = stages.size();
        for(int i = 0; i < size; i++) {

            // Construct graph
            // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
            pair = constructSequentialLinkedGraph();
            graph = pair.getValue1();
            stages = pair.getValue2();

            Stage stage = stages.get(i);

            // Check the number of children (just tests
            // constructSequentialLinkedGraph really).
            List<Stage> children = graph.getChildren(stage);
            assertEquals(i != size - 1 ? 1 : 0, children.size());

            // Remove stage
            graph.removeStage(stage);

            // Check basic removal aspects
            assertFalse(graph.getStages().contains(stage));
            assertFalse(graph.getSourceStages().contains(stage));

            // There should be only 1 source if the head or tail
            // stage was removed, and two sources other wise.
            if(i == 0 || i == size - 1) {
                assertEquals(1, graph.getSourceStages().size());
            } else {
                assertEquals(2, graph.getSourceStages().size());
                assertTrue(graph.getSourceStages().contains(stages.get(0)));
                assertTrue(graph.getSourceStages().contains(children.get(0)));
            }

            // Make sure the stage no longer exists in any of the
            // remaining stages' child or parent lists.
            for(int j = 0; j < size; j++) {
                if(j != i) {
                    Stage other = stages.get(j);
                    assertFalse(graph.getChildren(other).contains(stage));
                    assertFalse(graph.getParents(other).contains(stage));
                }
            }
        }

        // Construct a branching graph over and over and
        // remove each of the stages inside.
        for(int i = 0; i < size; i++) {

            // Construct graph
            // ([0], [1]) -> [2] -> [3] -> ([4], [5])
            pair = constructBranchingLinkedGraph();
            graph = pair.getValue1();
            stages = pair.getValue2();

            assertEquals(2, graph.getSourceStages().size());

            Stage stage = stages.get(i);
            graph.removeStage(stage);

            // Check basic removal aspects
            assertFalse(graph.getStages().contains(stage));
            assertFalse(graph.getSourceStages().contains(stage));

            // Verify the sources after removal
            if(i == 0 || i == 1) {
                assertEquals(2, graph.getSourceStages().size());
                assertTrue(graph.getSourceStages().contains(stages.get(1 - i)));
                assertTrue(graph.getSourceStages().contains(stages.get(2)));

            } else if(i == 2) {
                assertEquals(3, graph.getSourceStages().size());
                assertTrue(graph.getSourceStages().contains(stages.get(0)));
                assertTrue(graph.getSourceStages().contains(stages.get(1)));
                assertTrue(graph.getSourceStages().contains(stages.get(3)));

            } else if(i == 3) {
                assertEquals(4, graph.getSourceStages().size());
                assertTrue(graph.getSourceStages().contains(stages.get(0)));
                assertTrue(graph.getSourceStages().contains(stages.get(1)));
                assertTrue(graph.getSourceStages().contains(stages.get(4)));
                assertTrue(graph.getSourceStages().contains(stages.get(5)));

            } else {
                assertEquals(2, graph.getSourceStages().size());
                assertTrue(graph.getSourceStages().contains(stages.get(0)));
                assertTrue(graph.getSourceStages().contains(stages.get(1)));

            }

            // Make sure the stage no longer exists in any of the
            // remaining stages' child or parent lists.
            for(int j = 0; j < size; j++) {
                if(j != i) {
                    Stage other = stages.get(j);
                    assertFalse(graph.getChildren(other).contains(stage));
                    assertFalse(graph.getParents(other).contains(stage));
                }
            }
        }
    }

    @Test(expected=GraphException.class)
    public void testAddExistingStage() {
        StageGraph graph = new StageGraph();
        BasicStage stage = new BasicStage("0");
        graph.addStage(stage);
        graph.addStage(stage);
        graph.walkGraph();
    }


    ///////////
    // LINKS //
    ///////////

    // Add

    @Test
    public void testAddLinks() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();

        for(int i = 0; i < stages.size() - 1; i++) {
            Stage stage1 = stages.get(i);
            Stage stage2 = stages.get(i + 1);
            assertEquals(1, graph.getChildren(stage1).size());
            assertTrue(graph.getChildren(stage1).contains(stage2));
        }
        assertEquals(0, graph.getChildren(stages.get(stages.size() - 1)).size());

        // ([0], [1]) -> [2] -> [3] -> ([4], [5])
        pair = constructBranchingLinkedGraph();
        graph = pair.getValue1();
        stages = pair.getValue2();

        Stage stage0 = stages.get(0);
        Stage stage1 = stages.get(1);
        Stage stage2 = stages.get(2);
        Stage stage3 = stages.get(3);
        Stage stage4 = stages.get(4);
        Stage stage5 = stages.get(5);

        assertEquals(1, graph.getChildren(stage0).size());
        assertTrue(graph.getParents(stage0).isEmpty());
        assertEquals(1, graph.getChildren(stage1).size());
        assertTrue(graph.getParents(stage1).isEmpty());
        assertTrue(graph.getChildren(stage0).contains(stage2));
        assertTrue(graph.getChildren(stage1).contains(stage2));
        assertEquals(2, graph.getParents(stage2).size());
        assertTrue(graph.getParents(stage2).contains(stage0));
        assertTrue(graph.getParents(stage2).contains(stage1));

        assertEquals(1, graph.getChildren(stage2).size());
        assertTrue(graph.getChildren(stage2).contains(stage3));
        assertEquals(1, graph.getParents(stage3).size());
        assertTrue(graph.getParents(stage3).contains(stage2));

        assertEquals(2, graph.getChildren(stage3).size());
        assertEquals(0, graph.getChildren(stage4).size());
        assertEquals(0, graph.getChildren(stage5).size());
        assertTrue(graph.getChildren(stage3).contains(stage4));
        assertTrue(graph.getChildren(stage3).contains(stage5));
    }

    @Test(expected=InvalidLinkException.class)
    public void testInvalidLinkExceptionBadOutStage() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        graph.removeStage(stages.get(0));
        graph.addLink(stages.get(0).getOutputDescriptor("string"),
            stages.get(2).getInputDescriptor("string2"));
    }

    @Test(expected=InvalidLinkException.class)
    public void testInvalidLinkExceptionBadInStage() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        graph.removeStage(stages.get(2));
        graph.addLink(stages.get(0).getOutputDescriptor("string"),
            stages.get(2).getInputDescriptor("string2"));
    }

    @Test(expected=InvalidLinkException.class)
    public void testInvalidLinkExceptionBadOutDescriptor() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        OutputDescriptor newOut = new OutputDescriptor(stages.get(0), "name",
            "friendlyName", "description", String.class);
        graph.addLink(newOut, stages.get(2).getInputDescriptor("string2"));
    }

    @Test(expected=InvalidLinkException.class)
    public void testInvalidLinkExceptionBadInDescriptor() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        InputDescriptor newIn = new InputDescriptor(stages.get(2), "name",
            "friendlyName", "description", String.class, true);
        graph.addLink(stages.get(0).getOutputDescriptor("string"), newIn);
    }

    @Test(expected=InvalidLinkException.class)
    public void testInvalidLinkExceptionTypeMismatch() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage newStage = new MultiTypeStage("7");
        graph.addStage(newStage);
        graph.addLink(stages.get(5).getOutputDescriptor("string"),
            newStage.getInputDescriptor("int"));
    }

    @Test(expected=LinkCycleException.class)
    public void testInvalidLinkExceptionCycle() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        graph.addLink(stages.get(5).getOutputDescriptor("string"),
            stages.get(0).getInputDescriptor("string1"));
    }

    @Test(expected=LinkCycleException.class)
    public void testInvalidLinkExceptionCycleViaDependencies() {
        StageGraph graph = new StageGraph();
        Stage stage0 = new BasicStage("0");
        Stage stage1 = new BasicStage("1");
        graph.addStage(stage0);
        graph.addStage(stage1);
        graph.addDependency(stage0, stage1);
        graph.addDependency(stage1, stage0);
    }

    @Test(expected=DuplicateLinkException.class)
    public void duplicate() {
        StageGraph graph = new StageGraph();
        Stage stage0 = new BasicStage("0");
        Stage stage1 = new BasicStage("1");
        graph.addStage(stage0);
        graph.addStage(stage1);
        graph.addLink(stage0.getOutputDescriptor("string"), stage1.getInputDescriptor("string1"));
        graph.addLink(stage0.getOutputDescriptor("string"), stage1.getInputDescriptor("string1"));
    }

    // Remove

    @Test
    public void testRemoveLinks() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stageA = stages.get(2);
        Stage stageB = stages.get(3);
        assertEquals(1, graph.getSourceStages().size());

        graph.removeLink(stageA.getOutputDescriptor("string"), stageB.getInputDescriptor("string1"));
        assertEquals(1, graph.getParents(stageB).size());   // A is still B's parent due to 1 input
        assertTrue(graph.getParents(stageB).contains(stageA));
        assertEquals(1, graph.getChildren(stageA).size());  // B is still B's child due to 1 input
        assertTrue(graph.getChildren(stageA).contains(stageB));
        assertEquals(2, graph.getSourceStages().size());         // B is now a source due to open input!
        assertTrue(graph.getSourceStages().contains(stages.get(0)));
        assertTrue(graph.getSourceStages().contains(stageB));

        graph.removeLink(stageA.getOutputDescriptor("string"), stageB.getInputDescriptor("string2"));
        assertEquals(0, graph.getParents(stageB).size());   // A no longer B's parent
        assertFalse(graph.getParents(stageB).contains(stageA));
        assertEquals(0, graph.getChildren(stageA).size());
        assertFalse(graph.getChildren(stageA).contains(stageB));
        assertEquals(2, graph.getSourceStages().size());

        // ([0], [1]) -> [2] -> [3] -> ([4], [5])
        pair = constructBranchingLinkedGraph();
        graph = pair.getValue1();
        stages = pair.getValue2();
        stageA = stages.get(0);
        stageB = stages.get(1);
        Stage stageC = stages.get(2);
        assertEquals(2, graph.getSourceStages().size());
        assertEquals(2, graph.getParents(stageC).size());
        assertTrue(graph.getParents(stageC).contains(stageA));
        assertTrue(graph.getParents(stageC).contains(stageB));

        graph.removeLink(stageA.getOutputDescriptor("string"), stageC.getInputDescriptor("string1"));
        assertEquals(1, graph.getParents(stageC).size());
        assertFalse(graph.getParents(stageC).contains(stageA));
        assertTrue(graph.getParents(stageC).contains(stageB));
        assertTrue(graph.getChildren(stageB).contains(stageC));
        assertEquals(3, graph.getSourceStages().size());        // C is now a source due to open input!
        assertTrue(graph.getSourceStages().contains(stageA));
        assertTrue(graph.getSourceStages().contains(stageB));
        assertTrue(graph.getSourceStages().contains(stageC));

        graph.removeLink(stageB.getOutputDescriptor("string"), stageC.getInputDescriptor("string2"));
        assertEquals(0, graph.getParents(stageC).size());
        assertEquals(0, graph.getChildren(stageA).size());
        assertEquals(0, graph.getChildren(stageB).size());
        assertEquals(3, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stageA));
        assertTrue(graph.getSourceStages().contains(stageB));
        assertTrue(graph.getSourceStages().contains(stageC));
    }

    // Inspect

    @Test
    public void testGetLinks() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stage0 = stages.get(0);
        Stage stage1 = stages.get(1);
        Stage stage2 = stages.get(2);
        Stage stage3 = stages.get(3);
        Stage stage4 = stages.get(4);
        Stage stage5 = stages.get(5);

        OutputDescriptor out = stage0.getOutputDescriptor("string");
        InputDescriptor in1 = stage0.getInputDescriptor("string1");
        InputDescriptor in2 = stage0.getInputDescriptor("string2");
        assertEquals(0, graph.getOutputLinks(in1).size());
        assertEquals(0, graph.getOutputLinks(in2).size());

        in1 = stage1.getInputDescriptor("string1");
        in2 = stage1.getInputDescriptor("string2");
        assertEquals(2, graph.getInputLinks(out).size());
        assertTrue(graph.getInputLinks(out).contains(in1));
        assertTrue(graph.getInputLinks(out).contains(in2));
        assertTrue(graph.getOutputLinks(in1).contains(out));
        assertTrue(graph.getOutputLinks(in2).contains(out));

        out = stage1.getOutputDescriptor("string");
        in1 = stage2.getInputDescriptor("string1");
        in2 = stage2.getInputDescriptor("string2");
        assertEquals(2, graph.getInputLinks(out).size());
        assertTrue(graph.getInputLinks(out).contains(in1));
        assertTrue(graph.getInputLinks(out).contains(in2));
        assertTrue(graph.getOutputLinks(in1).contains(out));
        assertTrue(graph.getOutputLinks(in2).contains(out));

        out = stage2.getOutputDescriptor("string");
        in1 = stage3.getInputDescriptor("string1");
        in2 = stage3.getInputDescriptor("string2");
        assertEquals(2, graph.getInputLinks(out).size());
        assertTrue(graph.getInputLinks(out).contains(in1));
        assertTrue(graph.getInputLinks(out).contains(in2));
        assertTrue(graph.getOutputLinks(in1).contains(out));
        assertTrue(graph.getOutputLinks(in2).contains(out));

        out = stage3.getOutputDescriptor("string");
        in1 = stage4.getInputDescriptor("string1");
        in2 = stage4.getInputDescriptor("string2");
        assertEquals(2, graph.getInputLinks(out).size());
        assertTrue(graph.getInputLinks(out).contains(in1));
        assertTrue(graph.getInputLinks(out).contains(in2));
        assertTrue(graph.getOutputLinks(in1).contains(out));
        assertTrue(graph.getOutputLinks(in2).contains(out));

        out = stage4.getOutputDescriptor("string");
        in1 = stage5.getInputDescriptor("string1");
        in2 = stage5.getInputDescriptor("string2");
        assertEquals(2, graph.getInputLinks(out).size());
        assertTrue(graph.getInputLinks(out).contains(in1));
        assertTrue(graph.getInputLinks(out).contains(in2));
        assertTrue(graph.getOutputLinks(in1).contains(out));
        assertTrue(graph.getOutputLinks(in2).contains(out));

        out = stage5.getOutputDescriptor("string");
        assertEquals(0, graph.getInputLinks(out).size());

        // Errors

        Stage stage = new BasicStage("Test");
        try {
            graph.getInputLinks(stage.getOutputDescriptor("string"));
            fail();
        } catch(InvalidLinkException e) {}
        try {
            graph.getOutputLinks(stage.getInputDescriptor("string1"));
            fail();
        } catch(InvalidLinkException e) {}
        OutputDescriptor newOut = new OutputDescriptor(stages.get(0), "name",
            "friendlyName", "description", String.class);
        try {
            graph.getInputLinks(newOut);
            fail();
        } catch(InvalidLinkException e) {}
        InputDescriptor newIn = new InputDescriptor(stages.get(2), "name",
            "friendlyName", "description", String.class, true);
        try {
            graph.getOutputLinks(newIn);
            fail();
        } catch(InvalidLinkException e) {}

        // TODO [DMT] Branching stage graph not yet tested, but might be overkill
    }

    @Test
    public void testGetMaxDepths() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stage0 = stages.get(0);
        Stage stage1 = stages.get(1);
        Stage stage2 = stages.get(2);
        Stage stage3 = stages.get(3);
        Stage stage4 = stages.get(4);
        Stage stage5 = stages.get(5);
        Map<Stage, Integer> depths = graph.getMaxDepthsSources();
        assertEquals((Integer) 0, depths.get(stage0));
        assertEquals((Integer) 1, depths.get(stage1));
        assertEquals((Integer) 2, depths.get(stage2));
        assertEquals((Integer) 3, depths.get(stage3));
        assertEquals((Integer) 4, depths.get(stage4));
        assertEquals((Integer) 5, depths.get(stage5));

        pair = constructBranchingLinkedGraph();
        graph = pair.getValue1();
        stages = pair.getValue2();
        stage0 = stages.get(0);
        stage1 = stages.get(1);
        stage2 = stages.get(2);
        stage3 = stages.get(3);
        stage4 = stages.get(4);
        stage5 = stages.get(5);
        depths = graph.getMaxDepthsSources();
        assertEquals((Integer) 0, depths.get(stage0));
        assertEquals((Integer) 0, depths.get(stage1));
        assertEquals((Integer) 1, depths.get(stage2));
        assertEquals((Integer) 2, depths.get(stage3));
        assertEquals((Integer) 3, depths.get(stage4));
        assertEquals((Integer) 3, depths.get(stage5));

        graph.addDependency(stage0, stage1);
        depths = graph.getMaxDepthsSources();
        assertEquals((Integer) 0, depths.get(stage0));
        assertEquals((Integer) 1, depths.get(stage1));
        assertEquals((Integer) 2, depths.get(stage2));
        assertEquals((Integer) 3, depths.get(stage3));
        assertEquals((Integer) 4, depths.get(stage4));
        assertEquals((Integer) 4, depths.get(stage5));

        pair = construct2ProngedArrowGraph(2);
        graph = pair.getValue1();
        stages = pair.getValue2();
        depths = graph.getMaxDepthsSources();
        assertEquals(MapUtil.sToI("0=0,1=1,2=2,3=3,4=4,5=5,6=0,7=1"), changeToNames(depths));

        pair = construct2ProngedArrowGraph(STAGE_COUNT - 1);
        graph = pair.getValue1();
        stages = pair.getValue2();
        depths = graph.getMaxDepthsSources();
        assertEquals(MapUtil.sToI("0=0,1=1,2=2,3=3,4=4,5=5,6=0,7=1,8=2,9=3,10=4"), changeToNames(depths));

        pair = construct2ProngedArrowGraph(STAGE_COUNT + 2);
        graph = pair.getValue1();
        stages = pair.getValue2();
        depths = graph.getMaxDepthsSources();
        assertEquals(MapUtil.sToI("0=0,1=1,2=2,3=3,4=4,5=8,6=0,7=1,8=2,9=3,10=4,11=5,12=6,13=7"),
            changeToNames(depths));

    }

    private Map<String, Integer> changeToNames(Map<Stage, Integer> depths) {
        Map<String, Integer> map = new HashMap<>();
        for(Stage stage : depths.keySet()) {
            map.put(stage.getName(), depths.get(stage));
        }
        return map;
    }

    @Test
    public void testGetMaxDepthsStartAtStage() {
        // [0] -> [1] -> [2] -> [3] -> [4] -> [5]
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage stage0 = stages.get(0);
        Stage stage1 = stages.get(1);
        Stage stage2 = stages.get(2);
        Stage stage3 = stages.get(3);
        Stage stage4 = stages.get(4);
        Stage stage5 = stages.get(5);
        Map<Stage, Integer> depths = graph.getMaxDepths(stage2.getId());
        assertFalse(depths.containsKey(stage0));   // Not in map
        assertFalse(depths.containsKey(stage1));   // Not in map
        assertEquals((Integer)0, depths.get(stage2));
        assertEquals((Integer)1, depths.get(stage3));
        assertEquals((Integer)2, depths.get(stage4));
        assertEquals((Integer)3, depths.get(stage5));
        graph.addDependency(stage2, stage3);
        graph.addDependency(stage2, stage4);    // These should have no effect
        graph.addDependency(stage2, stage5);
        depths = graph.getMaxDepths(stage2.getId());
        assertFalse(depths.containsKey(stage0));   // Not in map
        assertFalse(depths.containsKey(stage1));   // Not in map
        assertEquals((Integer)0, depths.get(stage2));
        assertEquals((Integer)1, depths.get(stage3));
        assertEquals((Integer)2, depths.get(stage4));
        assertEquals((Integer)3, depths.get(stage5));

        pair = constructBranchingLinkedGraph();
        graph = pair.getValue1();
        stages = pair.getValue2();
        stage0 = stages.get(0);
        stage1 = stages.get(1);
        stage2 = stages.get(2);
        stage3 = stages.get(3);
        stage4 = stages.get(4);
        stage5 = stages.get(5);
        depths = graph.getMaxDepths(stage2.getId());
        assertFalse(depths.containsKey(stage0));   // Not in map
        assertFalse(depths.containsKey(stage1));   // Not in map
        assertEquals((Integer)0, depths.get(stage2));
        assertEquals((Integer)1, depths.get(stage3));
        assertEquals((Integer)2, depths.get(stage4));
        assertEquals((Integer)2, depths.get(stage5));
        graph.addDependency(stage5, stage4);
        depths = graph.getMaxDepths(stage2.getId());
        assertFalse(depths.containsKey(stage0));   // Not in map
        assertFalse(depths.containsKey(stage1));   // Not in map
        assertEquals((Integer)0, depths.get(stage2));
        assertEquals((Integer)1, depths.get(stage3));
        assertEquals((Integer)3, depths.get(stage4));
        assertEquals((Integer)2, depths.get(stage5));
    }


    /////////////////
    // MULTI INPUT //
    /////////////////

    @Test
    public void multi() {
        StageGraph graph = new StageGraph();
        MultiInputStage stage0a = new MultiInputStage("0a");
        MultiInputStage stage0b = new MultiInputStage("0b");
        MultiInputStage stage0c = new MultiInputStage("0c");
        MultiInputStage stage0d = new MultiInputStage("0d");
        MultiInputStage stage1 = new MultiInputStage("1");

        graph.addStage(stage0a);
        graph.addStage(stage1);

        assertEquals(2, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage1));

        assertEquals(0, graph.getParents(stage0a).size());
        assertEquals(0, graph.getInputLinks(stage0a.getOutputDescriptor("dir")).size());
        try {
            assertEquals(0, graph.getOutputLinks(stage1.getInputDescriptor("dir")).size());
            fail();
        } catch(UnrecognizedInputDescriptorException e) {}
        assertEquals(0, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());
        assertEquals(0, graph.getChildren(stage0a).size());
        assertEquals(0, graph.getParents(stage1).size());
        assertEquals(0, graph.getChildren(stage1).size());

        graph.addLink(stage0a.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
        assertEquals(0, graph.getParents(stage0a).size());
        assertEquals(1, graph.getChildren(stage0a).size());
        assertEquals(1, graph.getParents(stage1).size());
        assertEquals(0, graph.getChildren(stage1).size());
        assertEquals(1, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());

        try {
            graph.addLink(stage0a.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
            fail();
        } catch(DuplicateLinkException e) {}

        try {
            graph.addLink(stage0b.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
            fail();
        } catch(InvalidLinkException e) {}

        graph.addStage(stage0b);

        assertEquals(2, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage0b));
        assertFalse(graph.getSourceStages().contains(stage1));
        assertEquals(0, graph.getInputLinks(stage0b.getOutputDescriptor("dir")).size());

        graph.addLink(stage0b.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));

        assertEquals(0, graph.getParents(stage0a).size());
        assertEquals(1, graph.getChildren(stage0a).size());
        assertEquals(0, graph.getParents(stage0b).size());
        assertEquals(1, graph.getChildren(stage0b).size());
        assertEquals(1, graph.getInputLinks(stage0b.getOutputDescriptor("dir")).size());
        assertEquals(2, graph.getParents(stage1).size());
        assertEquals(0, graph.getChildren(stage1).size());
        assertEquals(2, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());

        graph.addStage(stage0c);
        assertEquals(3, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage0b));
        assertTrue(graph.getSourceStages().contains(stage0c));
        assertFalse(graph.getSourceStages().contains(stage1));
        graph.addLink(stage0c.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
        assertEquals(3, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage0b));
        assertTrue(graph.getSourceStages().contains(stage0c));

        assertEquals(3, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());
        graph.addStage(stage0d);

        try {
            graph.addLink(stage0d.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
            fail();
        } catch(InvalidLinkException e) {}
        assertEquals(3, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());
        assertEquals(4, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage0b));
        assertTrue(graph.getSourceStages().contains(stage0c));
        assertTrue(graph.getSourceStages().contains(stage0d));
        assertTrue(graph.getOutputLinks(stage1.getInputDescriptor("files")).contains(stage0a.getOutputDescriptor("dir")));
        assertTrue(graph.getOutputLinks(stage1.getInputDescriptor("files")).contains(stage0b.getOutputDescriptor("dir")));
        assertTrue(graph.getOutputLinks(stage1.getInputDescriptor("files")).contains(stage0c.getOutputDescriptor("dir")));

        graph.removeLink(stage0b.getOutputDescriptor("dir"), stage1.getInputDescriptor("files"));
        assertEquals(2, graph.getOutputLinks(stage1.getInputDescriptor("files")).size());
        assertEquals(4, graph.getSourceStages().size());
        assertTrue(graph.getSourceStages().contains(stage0a));
        assertTrue(graph.getSourceStages().contains(stage0b));
        assertTrue(graph.getSourceStages().contains(stage0c));
        assertTrue(graph.getSourceStages().contains(stage0d));
        assertFalse(graph.getSourceStages().contains(stage1));

        graph.addLink(stage0a.getOutputDescriptor("dir"), stage0b.getInputDescriptor("files"));
        assertEquals(3, graph.getSourceStages().size());
        assertTrue(graph.getOutputLinks(stage0b.getInputDescriptor("files")).contains(stage0a.getOutputDescriptor("dir")));
    }

    //////////
    // MISC //
    //////////

    @Test
    public void testGetStage() {
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(Stage stage : stages) {
            assertSame(stage, graph.getStageById(stage.getId()));
        }
        assertNull(graph.getStageById("12345"));
    }


    //////////////////
    // CONSTRUCTION //
    //////////////////

    private Pair<StageGraph, List<Stage>> constructUnlinkedGraph() {
        StageGraph graph = new StageGraph();
        List<Stage> stages = new ArrayList<Stage>();
        for(int i = 0; i < STAGE_COUNT; i++) {
            Stage stage = new BasicStage(Integer.toString(i));
            stages.add(stage);
        }
        for(Stage stage : stages) {
            graph.addStage(stage);
        }
        return new Pair<StageGraph, List<Stage>>(graph, stages);
    }

    private Pair<StageGraph, List<Stage>> constructSequentialLinkedGraph() {
        Pair<StageGraph, List<Stage>> pair = constructUnlinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        for(int i = 0; i < stages.size() - 1; i++) {
            Stage stage1 = stages.get(i);
            Stage stage2 = stages.get(i + 1);
            graph.addLink(stage1.getOutputDescriptor("string"), stage2.getInputDescriptor("string1"));
            graph.addLink(stage1.getOutputDescriptor("string"), stage2.getInputDescriptor("string2"));
        }
        return pair;
    }

    private Pair<StageGraph, List<Stage>> construct2ProngedArrowGraph(int secondLegLength) {
        Pair<StageGraph, List<Stage>> pair = constructSequentialLinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        Stage bottom = stages.get(stages.size() - 1);
        for(int i = 0; i < secondLegLength; i++) {
            Stage stageLeft = new BasicStage("" + (STAGE_COUNT + i));
            stages.add(stageLeft);
            graph.addStage(stageLeft);
            if(i != 0) {
                graph.addLink(stages.get(stages.size() - 2).getOutputDescriptor("string"),
                    stageLeft.getInputDescriptor("string1"));
                graph.addLink(stages.get(stages.size() - 2).getOutputDescriptor("string"),
                    stageLeft.getInputDescriptor("string2"));
            }
        }
        graph.removeLink(stages.get(STAGE_COUNT - 2).getOutputDescriptor("string"),
            bottom.getInputDescriptor("string1"));
        graph.addLink(stages.get(stages.size() - 1).getOutputDescriptor("string"),
            bottom.getInputDescriptor("string1"));
        return pair;
    }

    private Pair<StageGraph, List<Stage>> constructBranchingLinkedGraph() {
        Pair<StageGraph, List<Stage>> pair = constructUnlinkedGraph();
        StageGraph graph = pair.getValue1();
        List<Stage> stages = pair.getValue2();
        graph.addLink(stages.get(0).getOutputDescriptor("string"), stages.get(2).getInputDescriptor("string1"));
        graph.addLink(stages.get(1).getOutputDescriptor("string"), stages.get(2).getInputDescriptor("string2"));
        graph.addLink(stages.get(2).getOutputDescriptor("string"), stages.get(3).getInputDescriptor("string1"));
        graph.addLink(stages.get(2).getOutputDescriptor("string"), stages.get(3).getInputDescriptor("string2"));
        graph.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(4).getInputDescriptor("string1"));
        graph.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(4).getInputDescriptor("string2"));
        graph.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(5).getInputDescriptor("string1"));
        graph.addLink(stages.get(3).getOutputDescriptor("string"), stages.get(5).getInputDescriptor("string2"));
        return pair;
    }
}
