/*
Copyright 2013 Sandia Corporation.
Under the terms of Contract DE-AC04-94AL85000 with Sandia Corporation,
the U.S. Government retains certain rights in this software.
Distributed under the BSD-3 license. See the file LICENSE for details.
*/

package replete.params.combine.groups;

import replete.params.combine.specs.ConstantParameterSpecification;
import replete.params.combine.specs.ParameterSpecification;


// Convenience class - nothing is done here that can't
// be done with regular ParameterSpecGroup.
// Could technically be a sibling subclass with
// LatinHypercubeParameterSpecGroup if desired.

public class ConstantParameterSpecGroup extends ParameterSpecGroup {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConstantParameterSpecGroup() {
        super(1);
    }
    public ConstantParameterSpecGroup(int count) {
        super(count);
        // Technically count is going to usually be 1.  However
        // not going to enforce that at this time.  Counts greater
        // than 1 can easily be used to multiply the total number
        // of times the ensemble is run.
    }

    public ConstantParameterSpecGroup(Object... args) {
        for(int a = 0; a < args.length; a += 2) {
            if(a + 1 == args.length) {
                put(args[a], new ConstantParameterSpecification(null));
            } else {
                put(args[a], new ConstantParameterSpecification(args[a + 1]));
            }
        }
    }


    //////////////
    // MUTATORS //
    //////////////

    // Really just a convenience method.
    public void addConstParameter(Object paramKey, Object value) {
        put(paramKey, new ConstantParameterSpecification(value));
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Enforce type of parameter specifications.
    @Override
    public ParameterSpecification put(Object paramKey, ParameterSpecification spec) {
        if(!(spec instanceof ConstantParameterSpecification)) {
            throw new IllegalArgumentException("All constant parameter specifications must be of type '" +
                ConstantParameterSpecification.class.getSimpleName() + "'.");
        }
        return super.put(paramKey, spec);
    }
    @Override
    public ParameterSpecification put(Object paramKey, ParameterSpecification spec, boolean enforceStability) {
        if(!(spec instanceof ConstantParameterSpecification)) {
            throw new IllegalArgumentException("All constant parameter specifications must be of type '" +
                ConstantParameterSpecification.class.getSimpleName() + "'.");
        }
        return super.put(paramKey, spec, enforceStability);
    }
    @Override
    public String toString() {
        return "CPGroup(" + runCount + "|" + keySet() + ")";
    }
}
