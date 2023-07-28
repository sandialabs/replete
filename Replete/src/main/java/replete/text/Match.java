package replete.text;

import java.io.Serializable;

public class Match implements Serializable, Comparable<Match> {


    ////////////
    // FIELDS //
    ////////////

    private String text;
    private int start;
    private int endNonIncl;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public Match(int start, int endNonIncl, String text) {
        this.start = start;
        this.endNonIncl = endNonIncl;
        this.text = text;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getText() {
        return text;
    }
    public int getStart() {
        return start;
    }
    public int getEndNonIncl() {
        return endNonIncl;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int compareTo(Match other) {
        return start - other.start;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + endNonIncl;
        result = prime * result + start;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
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
        Match other = (Match) obj;
        if(endNonIncl != other.endNonIncl) {
            return false;
        }
        if(start != other.start) {
            return false;
        }
        if(text == null) {
            if(other.text != null) {
                return false;
            }
        } else if(!text.equals(other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Match [start=" + start + ", endNonIncl=" + endNonIncl + ", text=" + text + "]";
    }
}
