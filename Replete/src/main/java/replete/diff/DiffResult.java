package replete.diff;

public class DiffResult {


    ////////////
    // FIELDS //
    ////////////

    private ContainerComparison comparison = new ObjectComparison();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public ContainerComparison getComparison() {
        return comparison;
    }

    // Accessors (Computed)

    public boolean isDiff() {
        return comparison.isDiff();
    }

    // Mutators

    public DiffResult setComparison(ContainerComparison comparison) {
        this.comparison = comparison;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comparison == null) ? 0 : comparison.hashCode());
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
        DiffResult other = (DiffResult) obj;
        if(comparison == null) {
            if(other.comparison != null) {
                return false;
            }
        } else if(!comparison.equals(other.comparison)) {
            return false;
        }
        return true;
    }
}
