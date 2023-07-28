package finio.core.errors;

import java.util.ArrayList;
import java.util.List;

public class FMapCompositeException extends RuntimeException {


    ////////////
    // FIELDS //
    ////////////

    private List<Exception> exceptions = new ArrayList<Exception>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FMapCompositeException() {
        super();
    }
    public FMapCompositeException(String message, Throwable cause) {
        super(message, cause);
    }
    public FMapCompositeException(String message) {
        super(message);
    }
    public FMapCompositeException(Throwable cause) {
        super(cause);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public int getExceptionCount() {
        return exceptions.size();
    }
    public List<Exception> getExceptions() {
        return exceptions;
    }

    // Mutator

    public void addException(Exception e) {
        exceptions.add(e);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getMessage() {
        String msg = super.getMessage() + "\n";
        int e = 1;
        for(Exception ex : exceptions) {
            msg += (e++) + ". [" + ex.getClass().getName() + "] " + ex.getMessage() + "\n";
        }
        return msg.trim();
    }
}
