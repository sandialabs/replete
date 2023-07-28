package replete.pipeline.desc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import replete.pipeline.Stage;
import replete.pipeline.test.BasicStage;

// This tests InputDescriptor functionality that
// exists in the base class PortDescriptor plus
// the cardinality functionality added to
// InputDescriptor.

public class InputDescriptorTest {

    @Test(expected=IllegalArgumentException.class)
    public void nullParent() {
        new InputDescriptor(null, "ID", "ID", "ID", Object.class, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullName() {
        new InputDescriptor(new BasicStage("0"), null, "ID", "ID", Object.class, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void emptyName() {
        new InputDescriptor(new BasicStage("0"), "", "ID", "ID", Object.class, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void blankName() {
        new InputDescriptor(new BasicStage("0"), "  ", "ID", "ID", Object.class, true);
    }

    @Test(expected=IllegalArgumentException.class)
    public void nullType() {
        new InputDescriptor(new BasicStage("0"), "ID", "ID", "ID", null, true);
    }

    @Test
    public void ok() {
        new InputDescriptor(new BasicStage("0"), "ID", null, null, Object.class, true);
    }

    @Test
    public void qualifiedName() {
        Stage stage = new BasicStage("Test Stage");
        InputDescriptor id = new InputDescriptor(stage, "ID", null, null, Object.class, true);
        assertEquals("Test Stage/ID", id.getQualifiedName());
    }

    @Test
    public void requiredConstructor() {
        Stage stage = new BasicStage("Test Name");
        InputDescriptor id = new InputDescriptor(stage, "ID", null, null, Object.class, true, true);
        assertEquals(id.getCardinalityMinimum(), 1);
        assertEquals(id.getCardinalityMaximum(), 1);
        assertTrue(id.isNullAllowed());
        assertTrue(id.isRequired());

        InputDescriptor id2 = new InputDescriptor(stage, "ID", null, null, Object.class, true, false);
        assertEquals(id2.getCardinalityMinimum(), 0);
        assertEquals(id2.getCardinalityMaximum(), 1);
        assertTrue(id2.isNullAllowed());
        assertFalse(id2.isRequired());

        InputDescriptor id3 = new InputDescriptor(stage, "ID", null, null, Object.class, false, true);
        assertEquals(id3.getCardinalityMinimum(), 1);
        assertEquals(id3.getCardinalityMaximum(), 1);
        assertFalse(id3.isNullAllowed());
        assertTrue(id3.isRequired());

        InputDescriptor id4 = new InputDescriptor(stage, "ID", null, null, Object.class, false, false);
        assertEquals(id4.getCardinalityMinimum(), 0);
        assertEquals(id4.getCardinalityMaximum(), 1);
        assertFalse(id4.isNullAllowed());
        assertFalse(id4.isRequired());
    }

    @Test
    public void intRangeConstructor() {
        Stage stage = new BasicStage("Test Name");
        InputDescriptor id = new InputDescriptor(stage, "ID", null, null, Object.class, 1, 1);
        assertEquals(id.getCardinalityMinimum(), 1);
        assertEquals(id.getCardinalityMaximum(), 1);
        assertTrue(id.isNullAllowed());
        assertTrue(id.isRequired());

        InputDescriptor id2 = new InputDescriptor(stage, "ID", null, null, Object.class, true, 0, 1);
        assertEquals(id2.getCardinalityMinimum(), 0);
        assertEquals(id2.getCardinalityMaximum(), 1);
        assertTrue(id2.isNullAllowed());
        assertFalse(id2.isRequired());

        InputDescriptor id3 = new InputDescriptor(stage, "ID", null, null, Object.class, 0, 12);
        assertEquals(id3.getCardinalityMinimum(), 0);
        assertEquals(id3.getCardinalityMaximum(), 12);
        assertTrue(id3.isNullAllowed());
        assertFalse(id3.isRequired());

        InputDescriptor id4 = new InputDescriptor(stage, "ID", null, null, Object.class, false, 5, 60);
        assertEquals(id4.getCardinalityMinimum(), 5);
        assertEquals(id4.getCardinalityMaximum(), 60);
        assertFalse(id4.isNullAllowed());
        assertTrue(id4.isRequired());

        try {
            new InputDescriptor(stage, "ID", null, null, Object.class, 0, 0 /*BAD*/);
            fail();
        } catch(IllegalArgumentException e) {}

        try {
            new InputDescriptor(stage, "ID", null, null, Object.class, -2 /*BAD*/, 1);
            fail();
        } catch(IllegalArgumentException e) {}

        try {
            new InputDescriptor(stage, "ID", null, null, Object.class, 12,  5); /*BAD*/
            fail();
        } catch(IllegalArgumentException e) {}
    }
}
