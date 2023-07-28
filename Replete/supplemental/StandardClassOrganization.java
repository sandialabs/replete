package replete;

import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import replete.event.ChangeNotifier;
import replete.logging.LogCode;
import replete.logging.LogCodeManager;

// {WHAT} This class describes a standardized layout for Java classes
// that our team commonly uses across large swaths of our code bases.
// It shows both the section headers we use as well as the general
// order for types of code that generally appear in a class.

// {PURPOSE} The purpose of having reasonably well-defined sections is:
//   1. To enable faster navigation within our source code.  If we
//      know generally where things are, the quicker it is to jump
//      to the right spot in a given file.  With the keyboard, Page Up,
//      Page Down, CTRL+Home and CTRL+End help in these pursuits.
//      Additionally, using CTRL+F to find a specific section of code
//      is also an option.
//   2. To promote discussions on consistent software patterns that
//      can ultimately lead to a more readable, reliable, and bug-free
//      code.
//   3. The more templated code patterns are, the faster it should
//      be to produce said code, either through habit or through
//      tools like Eclipse's templates feature.

// {FLEXIBLE} This class organization is proposed as an encouraged
// practice rather than being anything that's enforced.  Also,
// the entire team is encouraged to update and contribute to
// this class template as team practices evolve.

// {PLURAL} All section headers should be plural if possible, as in:
//   FIELD*S*
//   CONSTRUCTOR*S*
//   ACCESSOR*S*
//   INNER CLASS*ES*
// even if there is only 1 item in that section so we don't
// have to ever worry about changing them to match.

// {OPTIONAL} Each section is optional and only exists if some code in
// the class belongs in that section.

public abstract class StandardClassOrganization {


    /////////////    // Logging at the very top as can be used by any part of class.
    // LOGGING //    // Using codes can simplify logs in larger systems.  There
    /////////////    // is also a technique we created to share codes to subclasses.

    private static Logger logger = Logger.getLogger(StandardClassOrganization.class);
    private static LogCode LC_XX = LogCodeManager.create("<Product>", StandardClassOrganization.class, "X!", "Something");


    ///////////    // Although structurally like classes, enums usually in practice
    // ENUMS //    // serve more as global constants that developers want immediate
    ///////////    // access to, so these go at top near the fields (private or public).
          // <--------- 1 BLANK LINE BELOW EACH SECTION HEADER --------->
    private enum InnerEnum {
        VALUE,
        VALUE2
    }
          // <--------- 2 BLANK LINES ABOVE --------->
          // <--------- ALL SECTION HEADERS --------->
    ////////////    // All fields (besides logging and notifiers) go here.  To reiterate,
    // FIELDS //    // this should always be plural (i.e. FIELD*S*) regardless of how
    ////////////    // many fields there are.

    // Constants    // Can optionally subdivide section further if fields can be grouped

    private static final int X;

    // Core

    private int x = X;


    ////////////////////    // Class initialization blocks are like constructors but
    // INITIALIZATION //    // for classes, and logically classes come before instances,
    ////////////////////    // so this is the right spot for class initializers.

    static {
        X = 0;
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StandardClassOrganization() {        // Default constructor always first
        this(0);
    }
    public StandardClassOrganization(int x) {   // Ctors often ordered by # of arguments
        this.x = x;
        init();
    }
    public StandardClassOrganization(int x, int y) {
        this.x = x;
        this.y = y
        init();
    }

    private void init() {     // Private 'init' methods that ONLY serve to share code
                              // within constructors doesn't need its own section and
    }                         // is considered an extension of the constructors.


    //////////////////////////    // Because accessors and mutators are so often ordinary and
    // ACCESSORS / MUTATORS //    // forgettable they go in a section in the middle so they can
    //////////////////////////    // be skipped easily.  Individual ACCESSORS or MUTATORS
                                  // sections are also used if there's only 1 of those types.
    // Accessors              // Accessors that directly provide an internal field
                              // (primitive or reference)
    public int getX() {
        return x;
    }

    // Accessors (Computed)   // Accessors that perform some transform on an internal field
                              // (e.g. make a copy of a field, invoke a method on a field).
    public int getXPlus10() {
        return x + 10;
    }

    // Mutators

    public StandardClassOrganization setX(int x) {  // Modified builder pattern commonly
        this.x = x;                                 // used where possible to simplify
        return this;                                // client code.
    }
    @Override
    public StandardClassOrganization setZ(int z) {         // Not EVERY @Override has to be in the
        return (StandardClassOrganization) super.setZ(z);  // OVERRIDDEN section if makes more sense
    }                                                      // in another section.
    
    // Helper
    
    private void checkValue(int x) {   // Mutators often choose to invoke helper  
                                       // methods for code organization or 
    }                                  // de-duplication purposes so they are
                                       // placed immediately below the mutators.


    //////////    // Usually, the first methods that don't have an obvious
    // MISC //    // home in another section can just be placed here, though
    //////////    // if additional organization is desired, use a custom section.

    public void printX() {

    }


    /////////////////////////    // Obviously this class template is not meant to restrict
    // <MY CUSTOM SECTION> //    // developers' own sense of organization.  If you have
    /////////////////////////    // non-A/M methods that make sense to organize and you
                                 // don't want to use MISC, just create your own section here.
    public void customMethod1() {

    }
    public void customMethod2() {

    }


    //////////////    // Placed above OVERRIDDEN since they are related sections.
    // ABSTRACT //    // These methods are usually very important so exist in a
    //////////////    // section dedicated to them.

    protected abstract void abstractMethod();


    ////////////////    // Often times, overridden methods (except for hashCode/equals/toString)
    // OVERRIDDEN //    // are special enough due to their nature that you want to jump right to
    ////////////////    // them, hence their own section, which is named OVERRIDDEN, not OVERRIDE.

    @Override                     
    public void drawShape() {    // Overridden methods OTHER THAN the standard
                                 // hashCode, equals, and toString methods are
    }                            // placed first in this section.
    @Override
    public int calculateWidth() {
        
    }
    
    // Then the standard overridden methods, hashCode, equals, and toString,
    // are placed at the bottom of this section, in that order.

    @Override
    public int hashCode() {              // hashCode & equals almost always auto-
        final int prime = 31;            // generated by Eclipse
        int result = 1;
        result = prime * result + x;
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
        StandardClassOrganization other = (StandardClassOrganization) obj;
        if(x != other.x) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {           // toString method goes at the bottom
        return "x=" + x;                 // of the OVERRIDDEN section
    }


    ///////////////    // All notifier code goes into the same section so it
    // NOTIFIERS //    // can be more easily managed (as opposed to splitting
    ///////////////    // these 3 pieces among the FIELDS and ACCESSORS /
                       // MUTATORS sections).
    private transient ChangeNotifier changeNotifier = new ChangeNotifier(this);
    public void addChangeListener(ChangeListener listener) {
        changeNotifier.addListener(listener);
    }
    private void fireChangeNotifier() {
        changeNotifier.fireStateChanged();
    }


    ///////////////////    // Non-static inner classes needed by instances.  Since
    // INNER CLASSES //    // inner classes can be somewhat long, we try to keep them
    ///////////////////    // near the bottom of any class.

    private class InnerClass {

        // Inner classes also use section headers for their organization
        
    }
    
    private interface InnerInterface {
        
    }


    //////////      //////////    // "MAIN" is used when the class serves as a formal
    // MAIN //  OR  // TEST //    // ENTRY POINT for the software in question.  "TEST"
    //////////      //////////    // is used to have a quick, local ability to TEST the
                                  // class (NOT a replacement for proper UNIT TESTING).
    private static String[] replaceWithTestArgs(String[] args) {
//        args = new String[] {
//            "A", "B"                 // This method is placed immediately above the
//        };                           // main method and an unlimited amount of
//        args = new String[] {        // commented-out test/example arguments can
//            "X", "Y"                 // be left in this method.
//        };
        return args;
    }

    public static void main(String[] args) {
        if(SoftwareVersion.get().isDevelopment()) {   // Should have these as first few lines
            args = replaceWithTestArgs(args);         // of mains which serve as ENTRY POINTS
        }                                             // to the software.

        // ENTRY POINT main methods usually end up near the TOP of a class
        //  - Because there are fewer constructors, accessors, mutators, etc.
        //    in such a class
        // TEST main methods usually end up near the BOTTOM of a class

        // Use of Replete's CommandLineParser is always recommended for mains
        // that serve as ENTRY POINTS to the software.
        // CommandLineParser parser = new CommandLineParser();
        // etc.

        doSomething();
    }

    // ** HERE ** => Other things to support main method - like more
    // static methods called by main, more inner classes, etc.

    private static void doSomething() {

    }


    ///////////////////    // Static inner classes needed by main.  Since inner
    // INNER CLASSES //    // classes can be somewhat long, we try to keep them
    ///////////////////    // near the bottom of any class.

    private static class InnerClassMain {

    }
    
    private static interface InnerInterfaceMain {
        
    }
}