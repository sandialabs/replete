package replete.diff;

import replete.plugins.Generator;

public class TestObjDiffer extends Differ<TestObjDifferParams, TestObj> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestObjDiffer(TestObjDifferParams params) {
        super(params);
    }


    @Override
    public DiffResult diff(TestObj o1, TestObj o2) {
        DiffResult result = new DiffResult();
        if(o1.x != o2.x) {
            result.getComparison().addDifference(true, "x field", "left 'x' was " + o1.x + " but right 'x' was " + o2.x, o1.x + "", o2.x + "");
        } else {
            result.getComparison().addDifference(false, "x field", (String) null);
        }
        if(params.isIncludeY()) {
            if(o1.y != o2.y) {
                result.getComparison().addDifference(true, Importance.LOW, "y field", "left 'y' was " + o1.y + " but right 'y' was " + o2.y);
            } else {
                result.getComparison().addDifference(false, Importance.LOW, "y field", (String) null);
            }
        }
        if(params.getSubObjDifferParams() != null) {
            SubObjDifferGenerator sg = Generator.lookup(params.getSubObjDifferParams());
            SubObjDiffer differ = sg.createDiffer(params.getSubObjDifferParams());
            if(o1.subObj == null && o2.subObj != null) {
                result.getComparison().addDifference(true, "subObj field", "Left has no subjObj but right does");
            } else if(o1.subObj != null && o2.subObj == null) {
                result.getComparison().addDifference(true, "subObj field", "Right has no subObj but left does");
            } else if(o1.subObj != null && o2.subObj != null) {
                DiffResult sResult = differ.diff(o1.subObj, o2.subObj);
                result.getComparison().addDifference("subObj field", sResult.getComparison());
            }
        }
        return result;
    }
}
