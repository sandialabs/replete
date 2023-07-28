package finio.platform.exts.view.consoleview.ui;

import finio.core.KeyPath;
import finio.core.events.KeyAddedEvent;
import finio.core.events.KeyAddedListener;
import finio.core.events.KeyChangedEvent;
import finio.core.events.KeyChangedListener;
import finio.core.events.KeyRemovedEvent;
import finio.core.events.KeyRemovedListener;
import finio.core.events.MapChangedEvent;
import finio.core.events.MapChangedListener;
import finio.core.events.MapClearedEvent;
import finio.core.events.MapClearedListener;
import finio.core.events.MapEvent;
import finio.core.events.ValueChangedEvent;
import finio.core.events.ValueChangedListener;
import replete.ui.GuiUtil;

public class ConsoleMapSuperListener
                        implements MapChangedListener,
                                   MapClearedListener,
                                   KeyAddedListener,
                                   ValueChangedListener,
                                   KeyRemovedListener,
                                   KeyChangedListener {


    ////////////
    // FIELDS //
    ////////////

    private ConsoleContext context;
    private ListenType type;
    private KeyPath P;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ConsoleMapSuperListener(ConsoleContext context, ListenType type, KeyPath P) {
        this.context = context;
        this.type = type;
        this.P = P;
    }


    ////////////
    // EVENTS //
    ////////////

    @Override
    public void mapChanged(MapChangedEvent e) {
        MapEvent cause = e.getCause();
        String eventName;
        String extra = null;
        if(cause instanceof MapClearedEvent) {
            eventName = ListenType.MAP_CLEARED.getName();

        } else if(cause instanceof KeyAddedEvent) {
            eventName = ListenType.KEY_ADDED.getName();
            extra = "key added=" + ((KeyAddedEvent) cause).getK();

        } else if(cause instanceof KeyRemovedEvent) {
            eventName = ListenType.KEY_REMOVED.getName();
            extra = "key removed=" + ((KeyRemovedEvent) cause).getK();

        } else if(cause instanceof KeyChangedEvent) {
            eventName = ListenType.KEY_CHANGED.getName();
            extra = "key changed=" + ((KeyChangedEvent) cause).getK();

        } else if(cause instanceof ValueChangedEvent) {
            eventName = ListenType.VALUE_CHANGED.getName();
            extra = "key of changed value=" + ((ValueChangedEvent) cause).getK();

        } else {
            eventName = e.getClass().getName();
        }
        print("cause=[" + eventName + "]" + (extra != null ? " (" + extra + ")" : ""));
    }
    @Override
    public void mapCleared(MapClearedEvent e) {
        print(null);
    }
    @Override
    public void keyAdded(KeyAddedEvent e) {
        print("key added=" + e.getK());
    }
    @Override
    public void keyRemoved(KeyRemovedEvent e) {
        print("key removed=" + e.getK());
    }
    @Override
    public void keyChanged(KeyChangedEvent e) {
        print("key changed=" + e.getK());
    }
    @Override
    public void valueChanged(ValueChangedEvent e) {
        print("key of changed value=" + e.getK());
    }

    private void print(final String extra) {
        GuiUtil.safe(new Runnable() {
            public void run() {
                context.appendlnMgmt("Map event occurred [" + type.getName() + "]: " + P.toUnixString() + (extra != null ? " (" + extra + ")" : ""));
                context.showPrompt();
            }
        });
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public ConsoleContext getContext() {
        return context;
    }
    public ListenType getType() {
        return type;
    }
    public KeyPath getP() {
        return P;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "Console " + type.getName() + " Listener";
    }
}
