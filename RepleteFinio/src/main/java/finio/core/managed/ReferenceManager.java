package finio.core.managed;

import finio.core.KeyPath;
import finio.core.NonTerminal;



// This class is just to spark some ideas.  It intentionally has
// two nearly identical methods of specifying a "reference".
// This class could be an AMap, but that might be API overkill.
// Immutable at this point.
public class ReferenceManager extends AbstractManagedValueManager {


    ////////////
    // FIELDS //
    ////////////

    // Two types of references.
    public NonTerminal M;
    public KeyPath P;

//    public Object M;              // Assumes the scope is the current executing JVM process's heap space.
//    public Object K;              // Assumes that the M is an AMap and that K is a key in that map.


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ReferenceManager(NonTerminal M, KeyPath P) {
        this.M = M;
        this.P = P;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public Object getM() {
        return M;
    }
    public KeyPath getP() {
        return P;
    }

    @Override
    public Object get() {
        return M.getByPath(P);
    }

    @Override                     //
    public String getName() {     //
        return null;              //
    }                             //
    @Override                     //
    public boolean canSet() {     //
        return false;             //
    }                             //

    // Uses the key path.  A key path itself is not the source map of information,
    // but rather an address within that map identifying some value.  Thus we
    // need to provide the path's scope.
//    public Object getValue(NonTerminal Mscope) {  // This AMap could actually be made a field of this object as well.
//        return Mscope.get(P);
//    }

    // Uses the M/K pair.  This method is more similar to a Java object reference.
    // We maintain a reference to the "map" we want at the RAM level.  This is
    // essentially all object references inside of software.  However, we go
    // a step further here to allow the specification of a key within that map.
    // This is not possible in Java.  You can't specifically address an int
    // inside of an object in Java, only the object itself, and then you have
    // to know that that int exists.  This is overcome in Java with interfaces
    // that provide guaranteed semantic capability.
//    public Object getValue() {
//        return (K == null) ? M : M.get(K);
//    }
}
