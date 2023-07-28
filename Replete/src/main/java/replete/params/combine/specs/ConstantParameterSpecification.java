/*
Copyright 2013 Sandia Corporation.
Under the terms of Contract DE-AC04-94AL85000 with Sandia Corporation,
the U.S. Government retains certain rights in this software.
Distributed under the BSD-3 license. See the file LICENSE for details.
*/

package replete.params.combine.specs;


public class ConstantParameterSpecification extends ParameterSpecification {


    ///////////
    // FIELD //
    ///////////

    private Object value;  // Usually Number, String, or Boolean
                           // but could be any object that the target
                           // software needs (e.g. a Person object).


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConstantParameterSpecification() {}  // TODO: Figure out better hierarchy so can access getShortName w/o this ctor
    public ConstantParameterSpecification(Object v) {
        value = v;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // These are not used by the ensemble framework, which treats
    // parameter specifications as immutable, but adjacent
    // software might have needs for these methods.

    public Object getValue() {
        return value;
    }
    public void setValue(Object v) {
        value = v;
    }


    ///////////
    // VALUE //
    ///////////

    @Override
    public Object getValue(int groupRunCount, int idx) {
        return value;
    }
    @Override
    public boolean isStable() {
        return true;
    }

    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        ConstantParameterSpecification other = (ConstantParameterSpecification) obj;
        if(value == null) {
            if(other.value != null) {
                return false;
            }
        } else if(!value.equals(other.value)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "ConstantParameterSpecification [value=" + value + "]";
    }
    @Override
    public String getShortName() {
        return "Constant ";
    }
    @Override
    public String getParamString() {
        return "(value=" + value + ")";
    }
    @Override
    public String getDescription() {
        return "A constant parameter specification will produce the same value on each iteration through the group.";
    }
}
