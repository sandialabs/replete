package finio.core.managed;

import javax.swing.event.ChangeListener;

import replete.plugins.ExtensionPoint;

public interface ManagedValueManager extends ExtensionPoint {

    public String getName();

    // Should not return a different value on subsequent calls unless
    // the notifier has fired or will be immediately fired to indicate
    // the change.  This is to prevent client code from having to perform
    // any caching itself, as it knows that the value is only one call away
    // but also knows that the value hasn't changed.
    public Object get();

    public boolean canSet();
    public void set(Object V);
    // 1 method


    ////////////
    // PARAMS //
    ////////////

//    public ManagedParameters getParams();
//    public void setParams(ManagedParameters params);
    // 2 methods


    //////////////
    // NOTIFIER //
    //////////////

    public void addChangeListener(ChangeListener listener);
}
