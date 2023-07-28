package replete.pipeline.test;

import java.awt.Color;

import org.junit.Ignore;

import replete.pipeline.AbstractAtomicStage;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.InputValidationException;
import replete.threads.ThreadUtil;

@Ignore
public class ComplexStage extends AbstractAtomicStage {

    public ComplexStage() {
        super("Complex");
    }

    @Override
    protected void init() {
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "dir",
                "Direction",
                "The direction of the wind.",
                String.class,
                false,
                true
            )
        );
        registerInputDescriptor(
            new InputDescriptor(
                this,
                "angle",
                "Elevation Angle",
                "The angle of elevation from the horizon.",
                Double.class,
                false
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "mag",
                "Magnitude of Wind Vector",
                "The magnitude of the wind vector, or the wind speed.",
                Long.class
            )
        );
        registerOutputDescriptor(
            new OutputDescriptor(
                this,
                "color",
                "Air Color",
                "The color of the air during wind examination",
                Color.class
            )
        );
    }

    @Override
    protected void executeInner() {
        String dir = (String) getInput("dir");
        Double angle = (Double) getInput("angle");

        // Default value for optional input
        if(angle == null) {
            angle = 0.0;
        }

        // Error condition
        if(angle < 0.0) {
            throw new IllegalArgumentException("angle < 0.0");
        }

        // Modify angle
        if(dir.equalsIgnoreCase("north")) {
            angle += 0;
        } else if(dir.equalsIgnoreCase("east")) {
            angle += 90;
        } else if(dir.equalsIgnoreCase("south")) {
            angle += 180;
        } else {
            angle += 270;
        }

        addWarning("warning! " + dir);

        // Pause thread
        ThreadUtil.sleep(BlankStage.WAIT);

        long mag = (long) (angle * 2);
        setOutput("color", choose(mag));  // Reversed order to test sorting
        setOutput("mag", mag);
    }

    private Color choose(long mag) {
        switch((int) mag % 5) {
            case 0: return Color.black;
            case 1: return Color.blue;
            case 2: return Color.red;
            case 3: return Color.green;
            case 4: return Color.yellow;
        }
        return null;
    }

    @Override
    protected void validateInputs() throws InputValidationException {
        String dir = (String) getInput("dir");
        if(dir.equalsIgnoreCase("north")) {
        } else if(dir.equalsIgnoreCase("east")) {
        } else if(dir.equalsIgnoreCase("south")) {
        } else if(dir.equalsIgnoreCase("west")) {
        } else {
            throw new InputValidationException("Invalid direction '" + dir + "'.");
        }
    }
}
