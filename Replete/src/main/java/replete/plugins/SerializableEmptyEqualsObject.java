package replete.plugins;

import java.io.Serializable;

// This is a simple utility class, the need for which arose during
// the time we were experimenting with and employing a lot of extensible
// software designs.  When code and data is strictly separated, often
// times the data really is just the data.  When you want to compare
// any two data objects, you want them to be considered equals on the
// merits of their internal fields, not the identity of the object
// instances.  The object identity (a reference, pointer) has nothing
// to do with the data itself.  That is combined with the utility of
// having "empty" classes that simply serve as markers for classes
// that don't yet have any parameters yet, for example.  Instead of
// having no class or using the null value to indicate that some
// component doesn't yet have any fields in its parameters, using an
// empty class can make the implementation a little cleaner.  These
// empty classes can also essentially stand on their own as markers
// in lists of other parameter objects when serialized to external
// destinations, so that the intent of the configuration is easily
// retained.  That highlights more of the logic behind this class -
// when an object is serialized, almost never is its internal identity
// (e.g. System.identityHashCode(), memory location) serialized with
// an object - so that must imply that piece of information has little
// to no purpose with regards to the equality of two objects from
// the same class - only the fields matter!
//
// Thus, since new Object().equals(new Object()) returns false, and
// and two objects from class X extends Object, new X().equals(new X())
// also returns false, it's really handy to have methods that treat
// any two instances of a class with no fields as equal no matter what.
// Simply extending this class instead of Object will allow this to
// happen.  The implements Serializable is simply a handy add-on as
// all current uses of this class are for classes that are meant to be
// serialized in some form, be it to disk or across the network.

public abstract class SerializableEmptyEqualsObject implements Serializable {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // This code looks as it does because it is in the form
    // generated by Eclipse, more or less.  Eclipse won't let
    // you generate these methods for a class with no fields,
    // so for some traceability of where this code came from,
    // the code was transplanted from a class with fields and
    // modified.

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        return true;
    }
}
