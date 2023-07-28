package replete.diff;

import replete.text.StringUtil;

// Used for lists and maps and other collections

public class ListMapComparison extends ContainerComparison {


    ////////////
    // FIELDS //
    ////////////

    private SimpleComparison sizeComparison;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ListMapComparison() {

    }
    public ListMapComparison(int leftSize, int rightSize) {
        setSizeComparison(leftSize, rightSize);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public SimpleComparison getSizeComparison() {
        return sizeComparison;
    }

    // Mutators

    public void setSizeComparison(int leftSize, int rightSize) {
        sizeComparison = new SimpleComparison(leftSize != rightSize, leftSize + "", rightSize + "");
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isDiff() {
        if(sizeComparison != null && sizeComparison.isDiff()) {
            return true;
        }
        return super.isDiff();
    }

    @Override
    public void render(StringBuilder buffer, int level) {
        String sp = StringUtil.spaces(level * 4);
        buffer.append(sp + "Size => ");
        if(sizeComparison == null) {
            buffer.append("(NO SIZE INFO)");
        } else {
            sizeComparison.render(buffer, level);
        }
        buffer.append("\n");
        super.render(buffer, level);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((sizeComparison == null) ? 0 : sizeComparison.hashCode());
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
        ListMapComparison other = (ListMapComparison) obj;
        if(sizeComparison == null) {
            if(other.sizeComparison != null) {
                return false;
            }
        } else if(!sizeComparison.equals(other.sizeComparison)) {
            return false;
        }
        return true;
    }
}
