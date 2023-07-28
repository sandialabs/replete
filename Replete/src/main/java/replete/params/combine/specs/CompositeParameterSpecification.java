/*
Copyright 2013 Sandia Corporation.
Under the terms of Contract DE-AC04-94AL85000 with Sandia Corporation,
the U.S. Government retains certain rights in this software.
Distributed under the BSD-3 license. See the file LICENSE for details.
*/

package replete.params.combine.specs;

import java.util.ArrayList;
import java.util.List;

import replete.params.combine.groupset.ParameterSpecGroupSetValidationException;


public class CompositeParameterSpecification extends ParameterSpecification {


    ///////////
    // FIELD //
    ///////////

    private List<SubSpecification> subSpecs = new ArrayList<SubSpecification>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CompositeParameterSpecification() {}  // TODO: Figure out better hierarchy so can access getShortName w/o this ctor


    /////////
    // ADD //
    /////////

    public void addSubSpecification(int howMany, ParameterSpecification spec) {
        int max = spec.getMaxValues();
        if(max != -1 && howMany > max) {
            throw new ParameterSpecGroupSetValidationException(
                "Subspecification within composite parameter specification has a maximum run count less than the run count of the subgroup.");
        }
        SubSpecification glob = new SubSpecification();
        glob.howMany = howMany;
        glob.spec = spec;
        subSpecs.add(glob);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // These are not used by the ensemble framework, which treats
    // parameter specifications as immutable, but adjacent
    // software might have needs for these methods.

    public int getTotalRunCount() {
        return getMaxValues();
    }
    public List<SubSpecification> getValues() {
        return subSpecs;
    }
    public void setValues(List<SubSpecification> s) {
        subSpecs = s;
    }


    ///////////
    // VALUE //
    ///////////

    @Override
    public Object getValue(int groupRunCount, int idx) {
        int total = 0;
        for(SubSpecification sspec : subSpecs) {
            int howMany = sspec.howMany;
            ParameterSpecification spec = sspec.spec;
            if(idx < total + howMany) {
                return spec.getValue(howMany, idx - total);
            }
            total += howMany;
        }
        throw new RuntimeException("Composite parameter specification is misconfigured.");
    }
    @Override
    public boolean isStable() {
        for(SubSpecification sspec : subSpecs) {
            if(!sspec.spec.isStable()) {
                return false;
            }
        }
        return true;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int getMaxValues() {
        int total = 0;
        for(SubSpecification sspec : subSpecs) {
            total += sspec.howMany;
        }
        return total;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subSpecs == null) ? 0 : subSpecs.hashCode());
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
        CompositeParameterSpecification other = (CompositeParameterSpecification) obj;
        if(subSpecs == null) {
            if(other.subSpecs != null) {
                return false;
            }
        } else if(!subSpecs.equals(other.subSpecs)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "CompositeParameterSpecification [subSpecs=" + subSpecs + "]";
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    public class SubSpecification {

        // Fields
        public int howMany;
        public ParameterSpecification spec;

        // Overridden
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + howMany;
            result = prime * result + ((spec == null) ? 0 : spec.hashCode());
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
            SubSpecification other = (SubSpecification) obj;
            if(howMany != other.howMany) {
                return false;
            }
            if(spec == null) {
                if(other.spec != null) {
                    return false;
                }
            } else if(!spec.equals(other.spec)) {
                return false;
            }
            return true;
        }
        @Override
        public String toString() {
            return spec + " @ " + howMany + " iter";
        }
    }

    @Override
    public String getShortName() {
        return "Composite";
    }
    @Override
    public String getParamString() {
        return "(# sub specifications=" + subSpecs.size() + ")";
    }
    @Override
    public String getDescription() {
        return "A composite parameter specification will produce...";
    }
}
