package finio.ui.worlds;

public class WorldSelectedEvent {


    ////////////
    // FIELDS //
    ////////////

    private int previousIndex;
    private int index;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WorldSelectedEvent(int previousIndex, int index) {
        this.previousIndex = previousIndex;
        this.index = index;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getPreviousIndex() {
        return previousIndex;
    }
    public int getIndex() {
        return index;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "WorldSelectedEvent [previousIndex=" + previousIndex + ", index=" + index + "]";
    }
}
