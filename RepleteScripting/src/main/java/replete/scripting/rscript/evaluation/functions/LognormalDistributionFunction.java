package replete.scripting.rscript.evaluation.functions;

import org.jscience.mathematics.statistics.Distribution;
import org.jscience.mathematics.statistics.LogNormalDistribution;

public class LognormalDistributionFunction extends Function {

    @Override
    public String getName() {
        return "lognormal";
    }

    @Override
    public String getDescription() {
        return "lognormal distribution";
    }

    @Override
    public ParameterSet[] getAllowedParameterSets() {
        return new ParameterSet[] {
                new ParameterSet(
                    "!RET", "val1", "val2",
                    Distribution.class, Number.class, Number.class)
            };
    }

    @Override
    protected Object eval(Object[] args, int parameterSetIndex) {
        return new LogNormalDistribution(((Number) args[0]).doubleValue(), ((Number) args[1]).doubleValue());
    }

//    @Override
//    protected Object eval (Object[] args, int parameterSetIndex)
//    {
//        double location = ((Number) args[0]).doubleValue ();
//        double scale    = ((Number) args[1]).doubleValue ();
//        double r = Math.sqrt (-2 * Math.log (Math.random ()));
//        double theta = 2 * Math.PI * Math.random ();
//        return Math.exp (location + (scale * r * Math.cos (theta)));
//    }

}
