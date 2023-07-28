package finio.ui.view;

import java.util.ArrayList;
import java.util.List;

import finio.core.KeyPath;


public class SelectionContext<S extends SelectionContextSegment> {


    ////////////
    // FIELDS //
    ////////////

    private List<S> segments = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    public <T> T getK() {
        return (T) segments.get(0).getK();
    }
    public <T> T getV() {
        return (T) segments.get(0).getV();
    }
    public <T> T  getParentK() {
        return (T) segments.get(1).getK();
    }
    public <T> T getParentV() {
        return (T) segments.get(1).getV();
    }
    public <T> T  getGrandparentK() {
        return (T) segments.get(2).getK();
    }
    public <T> T getGrandparentV() {
        return (T) segments.get(2).getV();
    }
    public KeyPath getP() {
        KeyPath P = KeyPath.KP();
        for(int i = 0; i < segments.size(); i++) {
            Object K = segments.get(i).getK();
            if(K != null) {
                P.prepend(K);
            }
        }
        return P;
    }
    public S getSegment(int s) {
        return segments.get(s);
    }
    public int getSegmentCount() {
        return segments.size();
    }

    // Mutators (Builder)

    public SelectionContext addSegment(S segment) {
        segments.add(segment);
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String ret = "";
        int p = 0;
        for(S segment : segments) {
            ret += "segment-" + (p++) + " = " + segment + "\n";
        }
        return ret.trim();
    }
}
