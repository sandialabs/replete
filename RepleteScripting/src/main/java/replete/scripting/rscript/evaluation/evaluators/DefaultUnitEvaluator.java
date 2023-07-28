package replete.scripting.rscript.evaluation.evaluators;

import javax.measure.unit.Unit;

import org.jscience.physics.amount.Amount;

import replete.scripting.rscript.evaluation.ASTEvaluationContext;
import replete.scripting.rscript.evaluation.ASTNodeEvaluator;
import replete.scripting.rscript.evaluation.functions.EvaluationException;
import replete.scripting.rscript.parser.gen.ASTUnit;
import replete.scripting.rscript.parser.values.UnitValue;

public class DefaultUnitEvaluator implements ASTNodeEvaluator<ASTUnit> {
    @Override
    public Object evaluate(ASTUnit node, ASTEvaluationContext context) throws EvaluationException {
        Object childValue = context.evaluate(node.getChild(0));
        UnitValue value = node.getValue();
        Unit thisUnit = value.getUnit();

        if(childValue instanceof Double) {
            return Amount.valueOf((Double) childValue, thisUnit);
        } else if(childValue instanceof Long) {
            return Amount.valueOf((Long) childValue, thisUnit);
        } else if(childValue instanceof Amount) {
            Amount<?> a = (Amount<?>) childValue;
            if(a.getUnit().isCompatible(thisUnit)) {
                return a.to(thisUnit);
            }
            throw new EvaluationException("The units '" + thisUnit +
                "' are not compatible with child expression's units of '" + a.getUnit() + "'.");
        }

        throw new EvaluationException(
            "Units cannot be used on any constant other than a double or long (" +
            (childValue == null ? "null" : childValue.getClass().getSimpleName()) +
            ")."
        );
    }
}
