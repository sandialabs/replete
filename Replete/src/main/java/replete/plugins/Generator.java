package replete.plugins;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import replete.util.ClassUtil;

// The Generator class does not implement ExtensionPoint itself,
// only subclasses whose direct subclasses themselves will be the
// classes whose instances will be extension points.  In other words:
//
//   Generator <- ... <- YyyXxxGenerator <- ZzzYyyXxxGenerator [where]
//   YyyXxxGenerator implements ExtensionPoint and has the method:
//
//     public static YyyXxxGenerator lookup(Object objOrClass) {
//         return (YyyXxxGenerator)
//             Generator.lookup(YyyXxxGenerator.class, objOrClass);
//     }
//
// For convenient look ups of all the extensions that correspond
// to this class.
//
// This class, YyyXxxGenerator, may also define abstract methods
// that all its subclasses must implement to be considered generators
// of this base type.  These extension point classes are registered
// in some plugin's 'getExtensionPoints' method.  For example:
//
//    @Override
//    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
//        return new Class[] {
//            YyyXxxGenerator.class
//        };
//    }
//
// The class ZzzYyyXxxGenerator (and other subclasses of YyyXxxGenerator,
// e.g. AaaYyyXxxGenerator, BbbYyyXxxGenerator) implements all the methods
// required by all its super classes, Generator to YyyXxxGenerator,
// that are specific to the functionality being added to the
// platform.  Instances of ZzzYyyXxxGenerator (and AaaYyyXxxGenerator, etc.)
// are the actual extensions.  Generally speaking, there will only
// be one extension of each subclass of the extension point class
// with this pattern added to the platform.  In other words, the
// platform will only have one instance of ZzzYyyXxxGenerator, one
// instance of AaaYyyXxxGenerator, etc.  These instances are registered
// in some plugin's  'getExtensions' method.  For example:
//
//     @Override
//     public ExtensionPoint[] getExtensions() {
//         return new ExtensionPoint[] {
//             new ZzzYyyXxxGenerator(),
//             new AaaYyyXxxGenerator(),
//             new BbbYyyXxxGenerator()
//         };
//     }
//
// The plug-in that provides the instances of extensions does not
// have to be the same plug-in that defines the extension point
// classes.  They could be the same plug-in, for example, if the
// package defining the extension points also wanted to provide
// default or base functionality for those points.
//
// This class could have also been called 'Coordinator'.  These
// classes have two main responsibilities:
//
//   1. Generation
//
//     Generators themselves should only be added to the platform
//     one instance per type.  But they are easily and globally
//     (statically) accessible to generate new objects related
//     to the functionality they are adding to the system.
//     They provide methods to create objects of any type.  Often,
//     these may be parameter objects, UI panel objects,  or
//     core functionality like persistent controllers or stateless
//     processes.  For example, subclasses might have methods
//     like 'createParams' or 'createParamsPanel'.  Each time
//     one of these methods is called, a new instance of that
//     object type is created and returned.
//
//   2. Coordination
//
//     Because the generators are the gateways to produce as many
//     object instances as desired by the system, a mechanism is
//     also needed to tie all these object types together so when
//     core components receive an instance of one of these objects
//     they can backtrack to/lookup the generator that produced them,
//     usually to get access to some other type of related object
//     via that generator.  All these classes are tied together via
//     subclasses overriding the 'getCoordinatedClasses' method.
//     For example:
//
//       @Override
//       public Class<?>[] getCoordinatedClasses() {
//           return new Class[] {
//               WeatherDataAnalyzerParams.class,
//               WeatherDataAnalyzerParamsPanel.class,
//               WeatherDataAnalyzer.class
//           };
//       }
//
//     This base class contains static memory and methods to facilitate
//     the lookups between classes generated by generators and the
//     generator classes themselves.
public abstract class Generator {


    ////////////
    // FIELDS //
    ////////////

    private static Map<Class<?>, Generator> allCoordinatedClasses;


    ////////////
    // LOOKUP //
    ////////////

    public static synchronized <T extends Generator> T lookup(Object lookupObject) {
        checkInitialLoad();
        if(lookupObject == null) {
            return null;
        }
        Class<?> lookupClass;
        if(lookupObject instanceof Class) {
            lookupClass = (Class) lookupObject;
        } else if(lookupObject instanceof String) {
            lookupClass = ClassUtil.forName((String) lookupObject);
        } else {
            lookupClass = lookupObject.getClass();
        }
        return (T) allCoordinatedClasses.get(lookupClass);
    }


    ////////////////////
    // INITIALIZATION //
    ////////////////////

    private static synchronized void checkInitialLoad() {
        if(allCoordinatedClasses == null) {
            reloadFromPluginManager();
            PluginManager.addInitializationListener(e -> reloadFromPluginManager());
        }
    }

    private static synchronized void reloadFromPluginManager() {
        allCoordinatedClasses = new ConcurrentHashMap<>();
        for(ExtensionPoint ext : PluginManager.getAllExtensions().values()) {
            if(ext instanceof Generator) {
                Generator generator = (Generator) ext;
                addCoordinatedClass(generator, generator.getClass());
                Class<?>[] coordinatedClasses = generator.getCoordinatedClasses();
                if(coordinatedClasses != null) {
                    for(Class<?> coordinatedClass : coordinatedClasses) {
                        addCoordinatedClass(generator, coordinatedClass);
                    }
                }
            }
        }
    }


    public static void addCoordinatedClass(Generator generator, Class<?> coordinatedClass) {
        if(allCoordinatedClasses.containsKey(coordinatedClass)) {
            throw new IllegalStateException("Found the same coordinated class more than once.");
        }
        allCoordinatedClasses.put(coordinatedClass, generator);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Computed

    public <T> Class<? extends T> getCoordinatedClass(Class<T> baseType) {
        for(Class<?> clazz : getCoordinatedClasses()) {
            if(baseType.isAssignableFrom(clazz)) {
                return (Class<? extends T>) clazz;
            }
        }
        return null;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract Class<?>[] getCoordinatedClasses();
}
