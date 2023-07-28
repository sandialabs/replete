package replete.ui.help.model;

public class HelpTerm implements Comparable<HelpTerm> {


    ////////////
    // FIELDS //
    ////////////

    private String name;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpTerm(String name) {
        this.name = name;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getName() {
        return name;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        HelpTerm other = (HelpTerm) obj;
        if(name == null) {
            if(other.name != null) {
                return false;
            }
        } else if(!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(HelpTerm other) {
        return name.compareTo(other.name);
    }
}
