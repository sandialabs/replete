package replete.text.stp;

import java.util.function.Consumer;

public class State {


    ////////////
    // FIELDS //
    ////////////

    public static final State START = new State("<START>", null, null);
    public static final State END   = new State("<END>", null, null);

    private String name;
    private String startPattern;
    private Consumer<String> receiver;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public State(String name, String startPattern, Consumer<String> receiver) {
        this.name = name;
        this.startPattern = startPattern;
        this.receiver = receiver;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getName() {
        return name;
    }
    public String getStartPattern() {
        return startPattern;
    }
    public Consumer<String> getReceiver() {
        return receiver;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return name;
    }
}
