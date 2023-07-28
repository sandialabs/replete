package replete.equality;

public enum RelationalOperator {


    ////////////
    // FIELDS //
    ////////////

    LESS_THAN("<", "Less Than"),
    GREATER_THAN(">", "Greater Than"),
    LESS_THAN_OR_EQUAL_TO("<=", "Less Than or Equal To"),
    GREATER_THAN_OR_EQUAL_TO(">=", "Greater Than or Equal To"),
    EQUAL("=", "Equal"),
    NOT_EQUAL("!=", "Not Equal");

    private final String symbol;
    private final String name;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    private RelationalOperator(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
       return name + " (" + symbol + ")";
    }

}
