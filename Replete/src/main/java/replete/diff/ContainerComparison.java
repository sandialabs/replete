package replete.diff;

import java.util.ArrayList;
import java.util.List;

import replete.collections.Triple;
import replete.text.StringUtil;

public abstract class ContainerComparison extends Comparison {


    ////////////
    // FIELDS //
    ////////////

    // Value0: Importance; How important is this difference
    // Value1: Context; What is being compared (field name, or arbitrary name)
    // Value2: Difference; What is different about what is being compared
    private List<Triple<Importance, String, Comparison>> comparisons = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<Triple<Importance, String, Comparison>> getComparisons() {
        return comparisons;
    }

    // Mutators

    public void addDifference(boolean different, String context, String comment) {
        addDifferenceInner(different, Importance.HIGH, context, comment, null, null);
    }
    public void addDifference(boolean different, String context, String leftClue, String rightClue) {
        addDifferenceInner(different, Importance.HIGH, context, null, leftClue, rightClue);
    }
    public void addDifference(boolean different, String context, String comment, String leftClue, String rightClue) {
        addDifferenceInner(different, Importance.HIGH, context, comment, leftClue, rightClue);
    }
    public void addDifference(boolean different, Importance imp, String context, String comment) {
        addDifferenceInner(different, imp, context, comment, null, null);
    }
    public void addDifference(boolean different, Importance imp, String context, String leftClue, String rightClue) {
        addDifferenceInner(different, imp, context, null, leftClue, rightClue);
    }
    public void addDifference(boolean different, Importance imp, String context, String comment, String leftClue, String rightClue) {
        addDifferenceInner(different, imp, context, comment, leftClue, rightClue);
    }

    private void addDifferenceInner(boolean different, Importance imp, String context, String comment, String leftClue, String rightClue) {
        checkContext(context);
        Comparison diff = new SimpleComparison(different, comment, leftClue, rightClue);
        Triple<Importance, String, Comparison> triple =
            new Triple<>(imp, context, diff);
        comparisons.add(triple);
    }

    public void addDifference(String context, Comparison diff) {                    // For adding arbitrary diff object
        addDifferenceInner(Importance.HIGH, context, diff);
    }
    public void addDifference(Importance imp, String context, Comparison diff) {    // For adding arbitrary diff object
        addDifferenceInner(imp, context, diff);
    }

    private void addDifferenceInner(Importance imp, String context, Comparison diff) {    // For adding arbitrary diff object
        checkContext(context);
        Triple<Importance, String, Comparison> triple =
            new Triple<>(imp, context, diff);
        comparisons.add(triple);
    }

    private void checkContext(String context) throws NullPointerException, DuplicateContextException {
        if(context == null) {
            throw new NullPointerException("Context cannot be null");
        }
        for(Triple<Importance, String, Comparison> diff : comparisons) {
            if(diff.getValue2().equals(context)) {
                throw new DuplicateContextException(context);
            }
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void render(StringBuilder buffer, int level) {
        String sp = StringUtil.spaces(level * 4);
        for(Triple<Importance, String, Comparison> triple : comparisons) {
            buffer.append(sp + "[" + triple.getValue1().name() + "] " + triple.getValue2() + " => ");
            Comparison diff = triple.getValue3();
            if(diff instanceof SimpleComparison) {
                diff.render(buffer, level);
                buffer.append("\n");
            } else {
                if(!diff.isDiff()) {
                    buffer.append("(NO DIFFERENCE)");
                }
                buffer.append("\n");
                diff.render(buffer, level + 1);
            }
        }
    }

    @Override
    public boolean isDiff() {
        for(Triple<Importance, String, Comparison> triple : comparisons) {
            Comparison diff = triple.getValue3();
            if(diff.isDiff()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comparisons == null) ? 0 : comparisons.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        ContainerComparison other = (ContainerComparison) obj;
        if(comparisons == null) {
            if(other.comparisons != null) {
                return false;
            }
        } else if(!comparisons.equals(other.comparisons)) {
            return false;
        }
        return true;
    }
}

