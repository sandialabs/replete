package replete.util;

public abstract class AppMain {

    // This class serves as a marker class for those
    // classes that have main methods AND serve as
    // formal ENTRY POINTS into an application.
    //
    // This class exists for a couple of reasons:
    //
    //  1) [Promoting Consistent Entry Points]
    //     As standard practices evolve for these main methods that
    //     serve as entry points, we end up with a lot of main methods
    //     that have very similar patterns (e.g. command line parsing,
    //     replacement of developer arguments).  Over time patterns
    //     will evolve and we'll want an easy way to keep track of all
    //     classes that have such main methods.  In Eclipse, you can pull
    //     the type hierarchy (e.g. Ctrl+T) for this class and quickly
    //     see and access all such classes.
    //
    //  2) [Consistent Naming Convention for Quick Eclipse Access]
    //     One outstanding, persistent question in any Java app is what
    //     to name the class that serves as the formal entry point to that
    //     software.  Is it 'Finio' or 'Main'?  'Orbweaver' or 'Main'?
    //     We *could* (and have) name large numbers of these classes 'Main'.
    //     But this gets a little complicated when you're trying to use
    //     Ctrl+Shift+T in Eclipse because then you have a ton of Main
    //     classes to sort through.  If we name the class after the app
    //     only (e.g. Subtext, Http, etc.) we also get inundated with
    //     all the classes from that app that might start with the
    //     app's name (e.g. SubtextFrame, SubtextDataModel, etc.).  Thus
    //     the solution seems to be to COMBINE both the app name and some
    //     other MARKER characters.  If we were to choose 'App' then
    //     HttpApp would be really easy to find now, but it's still hard
    //     to use Ctrl+Shift+T to find ALL 'App' classes because when you
    //     type '*App' into that dialog you are presented with hundreds
    //     of unrelated classes.  Thus a slightly longer name, like
    //     'AppMain', solves this problem.  Now it's easy to quickly access
    //     any of these classes via a variety of filter keywords in the
    //     Ctrl+Shift+T dialog:
    //
    //         OrbweaverAppMain
    //         Orbweaver*Main
    //         Orb*Main
    //         OrbweaverApp
    //         *AppMain
    //
    // Currently this class is empty, but if somehow we start
    // to see that these entry point classes do have similar
    // functionality and needs, this class can be augmented
    // to serve as a centralized place for that (realizing of
    // course that since we're talking about static methods
    // here, so polymorphism isn't an option but inheritance
    // could still be used to make static fields and methods
    // (that are essentially 'final' in nature) available to
    // subclasses.

}
