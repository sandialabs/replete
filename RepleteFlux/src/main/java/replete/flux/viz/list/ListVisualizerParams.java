package replete.flux.viz.list;

import java.util.UUID;

import replete.flux.viz.VisualizerParams;

public class ListVisualizerParams extends VisualizerParams {


    ////////////
    // FIELDS //
    ////////////

    private int value = 200;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getValue() {
        return value;
    }

    // Mutators

    public ListVisualizerParams setValue(int value) {
        this.value = value;
        return this;
    }

    @Override
    public ListVisualizerParams setTrackedId(UUID trackedId) {       // Just exists for return type promotion
        return (ListVisualizerParams) super.setTrackedId(trackedId);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(!super.equals(obj)) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        ListVisualizerParams other = (ListVisualizerParams) obj;
        if(value != other.value) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "value=" + value;
    }
}
