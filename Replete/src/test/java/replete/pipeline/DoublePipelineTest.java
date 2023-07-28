package replete.pipeline;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import replete.pipeline.stages.AddStage;
import replete.pipeline.stages.AdditiveInvertStage;
import replete.pipeline.stages.DivideStage;
import replete.pipeline.stages.ExponentiateStage;
import replete.pipeline.stages.LogBaseEStage;
import replete.pipeline.stages.LogBaseTenStage;
import replete.pipeline.stages.LogStage;
import replete.pipeline.stages.MultiplicativeInvertStage;
import replete.pipeline.stages.MultiplyStage;
import replete.pipeline.stages.RootStage;
import replete.pipeline.stages.SquareRootStage;
import replete.pipeline.stages.SquareStage;
import replete.pipeline.stages.SubtractStage;

public class DoublePipelineTest {

    private static final Double tolerance = .00001;

    @Test
    public void simplePipeLine() {
        DoublePipeline simple = new DoublePipeline();
        simple.addStage(new AddStage(2.0));
        simple.addStage(new SubtractStage(6.0));
        assertEquals(-2.0, simple.execute(2.0), tolerance);
    }
    @Test
    public void complexPipeLine() {
        DoublePipeline pipe = new DoublePipeline();
        pipe.addStage(new SquareStage());
        assertEquals(16.0, pipe.execute(4.0), tolerance);
        pipe.addStage(new MultiplicativeInvertStage());
        assertEquals(1.0 / 16.0, pipe.execute(4.0), tolerance);
        pipe.addStage(new AdditiveInvertStage());
        assertEquals(-1.0 / 16.0, pipe.execute(4.0), tolerance);
        pipe.addStage(new SquareRootStage());
        assertEquals(Double.NaN, pipe.execute(4.0), tolerance);
    }
    @Test
    public void add() {
        AddStage add = new AddStage(0.0);
        assertEquals(2.0, add.execute(2.0), .01);
        add.setSummand(-2.0);
        assertEquals(0.0, add.execute(2.0), .01);
        add.setSummand(1.0);
        assertEquals(3.0, add.execute(2.0), .01);
        add.setSummand(-3.0);
        assertEquals(-2.0, add.execute(1.0), .01);
    }
    @Test
    public void additiveInvert() {
        AdditiveInvertStage additiveInvert = new AdditiveInvertStage();
        assertEquals(-2.0, additiveInvert.execute(2.0), .01);
        assertEquals(0.0, additiveInvert.execute(0.0), .01);
        assertEquals(3.0, additiveInvert.execute(-3.0), .01);
    }
    @Test
    public void divide() {
        DivideStage divideStage = new DivideStage(2.0);
        assertEquals(1.0, divideStage.execute(2.0), .01);
        divideStage.setDivisor(-3.0);
        assertEquals(-3.33333, divideStage.execute(10.0), .01);
        assertEquals(0.0, divideStage.execute(0.0), .01);
        divideStage.setDivisor(0.0);
        assertEquals(Double.NEGATIVE_INFINITY, divideStage.execute(-3.0), .01);
        assertEquals(Double.POSITIVE_INFINITY, divideStage.execute(3.0), .01);
    }
    @Test
    public void exponentiate() {
        ExponentiateStage expo = new ExponentiateStage(3.0);
        assertEquals(8.0, expo.execute(2.0), .01);
        expo.setExponent(-1.0);
        assertEquals((1.0 / 8.0), expo.execute(8.0), .01);
        expo.setExponent(0.0);
        assertEquals(1.0, expo.execute(100.0), .01);
        expo.setExponent(.5);
        assertEquals(2.0, expo.execute(4.0), .01);
    }
    @Test
    public void log() {
        LogStage logStage = new LogStage(2.0);
        assertEquals(0.0, logStage.execute(1.0), .01);
        assertEquals(Double.NEGATIVE_INFINITY, logStage.execute(0.0), .01);
        assertEquals(1.0, logStage.execute(2.0), .01);
        assertEquals(Double.NaN, logStage.execute(-3.0), .01);
        logStage.setBase(5.0);
        assertEquals(2.431, logStage.execute(50.0), .01);
    }
    @Test
    public void logTen() {
        LogBaseTenStage log = new LogBaseTenStage();
        assertEquals(0.0, log.execute(1.0), .01);
        assertEquals(Double.NEGATIVE_INFINITY, log.execute(0.0), .01);
        assertEquals(1.0, log.execute(10.0), .01);
        assertEquals(Double.NaN, log.execute(-3.0), .01);
        assertEquals(1.70, log.execute(50.0), .01);
    }
    @Test
    public void multiplicativeInvert() {
        MultiplicativeInvertStage invert = new MultiplicativeInvertStage();
        assertEquals(Double.POSITIVE_INFINITY, invert.execute(0.0), tolerance);
        assertEquals(0.25, invert.execute(4.0), .01);
        assertEquals(4.0, invert.execute(0.25), .01);
        assertEquals(0.0, invert.execute(Double.POSITIVE_INFINITY), .01);
        assertEquals(0.0, invert.execute(Double.NEGATIVE_INFINITY), .01);
    }
    @Test
    public void multiply() {
        MultiplyStage mult = new MultiplyStage(0.0);
        assertEquals(0.0, mult.execute(4.0), 0.0);
        mult.setMulitplicand(1.0);
        assertEquals(4.0, mult.execute(4.0), 0.0);
        mult.setMulitplicand(-5.0);
        assertEquals(-20.0, mult.execute(4.0), 0.0);
        assertEquals(20.0, mult.execute(-4.0), 0.0);
        mult.setMulitplicand(10.0);
        assertEquals(100.0, mult.execute(10.0), 0.0);
    }
    @Test
    public void naturalLog() {
        LogBaseEStage log = new LogBaseEStage();
        assertEquals(0.0, log.execute(1.0), .01);
        assertEquals(Double.NEGATIVE_INFINITY, log.execute(0.0), .01);
        assertEquals(1.0, log.execute(2.7182818284590452), .01);
        assertEquals(Double.NaN, log.execute(-3.0), .01);
        assertEquals(3.912, log.execute(50.0), .01);
    }
    @Test
    public void root() {
        RootStage rootStage = new RootStage(1.0);
        assertEquals(1.0, rootStage.execute(1.0), .01);
        rootStage.setIndex(3.0);
        assertEquals(3.0, rootStage.execute(27.0), .01);
        rootStage.setIndex(4.0);
        assertEquals(2.0, rootStage.execute(16.0), .01);
    }
    @Test
    public void square() {
        SquareStage squareStage = new SquareStage();
        assertEquals(1.0, squareStage.execute(1.0), .01);
        assertEquals(0.0, squareStage.execute(0.0), .01);
        assertEquals(1.0, squareStage.execute(-1.0), .01);
        assertEquals(4.0, squareStage.execute(2.0), .01);
        assertEquals(100.0, squareStage.execute(-10.0), .01);
    }
    @Test
    public void squareRoot() {
        SquareRootStage square = new SquareRootStage();
        assertEquals(1.0, square.execute(1.0), .01);
        assertEquals(0.0, square.execute(0.0), .01);
        assertEquals(Double.NaN, square.execute(-1.0), .01);
        assertEquals(2.0, square.execute(4.0), .01);
        assertEquals(10.0, square.execute(100.0), .01);
    }
    @Test
    public void subtract() {
        SubtractStage subtractStage = new SubtractStage(0.0);
        assertEquals(2.0, subtractStage.execute(2.0), .01);
        subtractStage.setSubtrahend(-2.0);
        assertEquals(4.0, subtractStage.execute(2.0), .01);
        subtractStage.setSubtrahend(1.0);
        assertEquals(1.0, subtractStage.execute(2.0), .01);
        subtractStage.setSubtrahend(-3.0);
        assertEquals(1.0, subtractStage.execute(-2.0), .01);
    }
}
