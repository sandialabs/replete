package replete.diff;

public class SubObjDiffer extends Differ<SubObjDifferParams, SubObj> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public SubObjDiffer(SubObjDifferParams params) {
        super(params);
    }

    @Override
    public DiffResult diff(SubObj o1, SubObj o2) {
        DiffResult result = new DiffResult();
        if(o1.s != o2.s) {
            result.getComparison().addDifference(true, "s field", "left 's' was " + o1.s + " but right 's' was " + o2.s);
        } else {
            result.getComparison().addDifference(false, "s field", (String) null);
        }
        if(params.isIncludeT()) {
            if(o1.t != o2.t) {
                result.getComparison().addDifference(true, "t field", "left 't' was " + o1.t + " but right 't' was " + o2.t);
            } else {
                result.getComparison().addDifference(false, "t field", (String) null);
            }
        }
        ListMapComparison listDiff = new ListMapComparison(o1.words.size(), o2.words.size());
        for(int i = 0; i < Math.min(o1.words.size(), o2.words.size()); i++) {
            String s1 = o1.words.get(i);
            String s2 = o2.words.get(i);
            if(!s1.equals(s2)) {
                listDiff.addDifference(Importance.MEDIUM, "Element " + i, new SimpleComparison(true, s1, s2));
            }
        }
        result.getComparison().addDifference("words", listDiff);
        return result;
    }
}
