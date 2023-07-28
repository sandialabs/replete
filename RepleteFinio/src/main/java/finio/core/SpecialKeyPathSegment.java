package finio.core;

public class SpecialKeyPathSegment {


    ///////////
    // FIELD //
    ///////////

    private String label;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SpecialKeyPathSegment(String lbl) {
        label = lbl;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getLabel() {
        return label;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        SpecialKeyPathSegment other = (SpecialKeyPathSegment) obj;
        if(label == null) {
            if(other.label != null) {
                return false;
            }
        } else if(!label.equals(other.label)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SPECIAL<" + label + ">";
    }
}
