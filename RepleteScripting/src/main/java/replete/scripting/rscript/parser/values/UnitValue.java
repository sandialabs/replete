package replete.scripting.rscript.parser.values;

import javax.measure.unit.Unit;

public class UnitValue extends NodeValue {
    private Unit unit;
    
    public UnitValue(Unit unit) {
        this.unit = unit;
    }
    
    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return unit.toString();
    }
}
