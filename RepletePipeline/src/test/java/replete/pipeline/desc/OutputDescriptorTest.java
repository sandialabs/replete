package replete.pipeline.desc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import replete.pipeline.Stage;
import replete.pipeline.test.BasicStage;

// This tests OutputDescriptor functionality that
// exists in the base class PortDescriptor.

public class OutputDescriptorTest {

    @Test(expected=IllegalArgumentException.class)
    public void nullParent() {
        new OutputDescriptor(null, "OD", "OD", "OD", Object.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullName() {
        new OutputDescriptor(new BasicStage("0"), null, "OD", "OD", Object.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void emptyName() {
        new OutputDescriptor(new BasicStage("0"), "", "OD", "OD", Object.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void blankName() {
        new OutputDescriptor(new BasicStage("0"), "  ", "OD", "OD", Object.class);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullType() {
        new OutputDescriptor(new BasicStage("0"), "OD", "OD", "OD", null);
    }

    @Test
    public void ok() {
        new OutputDescriptor(new BasicStage("0"), "OD", null, null, Object.class);
    }

    @Test
    public void qualifiedName() {
        Stage stage = new BasicStage("Test Stage");
        OutputDescriptor od = new OutputDescriptor(stage, "OD", null, null, Object.class);
        assertEquals("Test Stage/OD", od.getQualifiedName());
    }
}
