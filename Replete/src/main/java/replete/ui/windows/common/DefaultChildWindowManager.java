package replete.ui.windows.common;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;


public class DefaultChildWindowManager implements ChildWindowManager {


    ////////////
    // FIELDS //
    ////////////

    private ChangeNotifier childWindowNotifier = new ChangeNotifier(this);
    private Map<String, ChildWindowCreationHandler> childWindowHandlerMap = new TreeMap<String, ChildWindowCreationHandler>();
    private Map<String, Map<String, RWindow>> childWindowMap = new TreeMap<String, Map<String, RWindow>>();

    private void fireChildWindowNotifier() {
        childWindowNotifier.fireStateChanged();
    }

    public void addChildWindowListener(ChangeListener listener) {
        childWindowNotifier.addListener(listener);
    }
    public void addChildWindowCreationHandler(String typeId, ChildWindowCreationHandler handler) {
        childWindowHandlerMap.put(typeId, handler);
    }

    public RWindow openChildWindow(String typeId, String uniqueId, Object... args) {
        RWindow childWin = createChildWindow(typeId, uniqueId, args);
        showChildWindowInternal(childWin);
        return childWin;
    }

    private Map<String, RWindow> createNewTypeMap() {
        return new TreeMap<String, RWindow>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if(o1 == null && o2 == null) {
                    return 0;
                } else if(o1 == null && o2 != null) {
                    return -1;
                } else if(o1 != null && o2 == null) {
                    return 1;
                }
                return o1.compareTo(o2);
            }

        });
    }

    public RWindow createChildWindow(String typeId, String uniqueId, Object... args) {
        if(!childWindowHandlerMap.containsKey(typeId)) {
            throw new RuntimeException("Child frame type '" + typeId + "' unregistered.");
        }
        Map<String, RWindow> childWins = childWindowMap.get(typeId);  // All the non-destroyed frames in existence for this type.
        if(childWins == null) {
            childWins = createNewTypeMap();
            childWindowMap.put(typeId, childWins);
        }
        RWindow retWin = childWins.get(uniqueId);
        if(retWin == null) {
            ChildWindowCreationHandler handler = childWindowHandlerMap.get(typeId);
            retWin = handler.create(args);
            if(retWin == null) {
                throw new RuntimeException("Invalid return value from create method.");
            }
            retWin.addClosingListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    fireChildWindowNotifier();
                }
            });
            final RWindow x = retWin;
            retWin.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentShown(ComponentEvent e) {
                    fireChildWindowNotifier();
                }
                @Override
                public void componentHidden(ComponentEvent e) {
                    fireChildWindowNotifier();
                }
            });
            childWins.put(uniqueId, retWin);
        }
        return retWin;
    }

    // Show

    public void showChildWindow(String typeId) {
        showChildWindow(typeId, null);
    }
    public void showChildWindow(String typeId, String uniqueId) {
        RWindow childWin = getChildWindow(typeId, uniqueId);
        showChildWindowInternal(childWin);
    }
    public void showChildWindow(RWindow win) {
        for(Map<String, RWindow> childWins : childWindowMap.values()) {
            for(RWindow childWin : childWins.values()) {
                if(childWin == win) {
                    showChildWindowInternal(childWin);
                    return;
                }
            }
        }
        throw new RuntimeException("Supplied frame is not a child frame.");
    }
    private void showChildWindowInternal(RWindow childWin) {
        if(!childWin.isVisible()) {
            childWin.setVisible(true);
        }
        childWin.requestFocus();
        childWin.toFront();
    }

    // Hide
    public void hideChildWindow(String typeId) {
        hideChildWindow(typeId, null);
    }
    public void hideChildWindow(String typeId, String uniqueId) {
        RWindow childWin = getChildWindow(typeId, uniqueId);
        hideChildWindowInternal(childWin);
    }
    public void hideChildWindow(RWindow win) {
        for(Map<String, RWindow> childWins : childWindowMap.values()) {
            for(RWindow childWin : childWins.values()) {
                if(childWin == win) {
                    hideChildWindowInternal(childWin);
                    return;
                }
            }
        }
        throw new RuntimeException("Supplied frame is not a child frame.");
    }
    private void hideChildWindowInternal(RWindow childWin) {
        childWin.close();
    }

    // Destroy

    public void destroyChildWindow(String typeId) {
        destroyChildWindow(typeId, null);
    }
    public void destroyChildWindow(String typeId, String uniqueId) {
        RWindow childWin = getChildWindow(typeId, uniqueId);
        destroyChildWindowInternal(childWin);
    }
    public void destroyChildWindow(RWindow win) {
        for(Map<String, RWindow> childWins : childWindowMap.values()) {
            for(RWindow childWin : childWins.values()) {
                if(childWin == win) {
                    destroyChildWindowInternal(childWin);
                    return;
                }
            }
        }
        throw new RuntimeException("Supplied frame is not a child frame.");
    }
    private void destroyChildWindowInternal(RWindow childWin) {
        String[] keys = getKeysFromWindow(childWin);
        String typeId = keys[0];
        String uniqueId = keys[1];

        // Remove from map in case close is successful so that when
        // childFrameNotifier is fired, map is correctly updated.
        childWindowMap.get(typeId).remove(uniqueId);
        if(childWindowMap.get(typeId).size() == 0) {
            childWindowMap.remove(typeId);
        }
        if(!childWin.close()) {
            // If it was not successful, put it back in the map.
            Map<String, RWindow> childWins = childWindowMap.get(typeId);
            if(childWins == null) {
                childWins = createNewTypeMap();
                childWindowMap.put(typeId, childWins);
            }
            childWins.put(uniqueId, childWin);
        }
    }

    private String[] getKeysFromWindow(RWindow win) {
        for(String typeId : childWindowMap.keySet()) {
            Map<String, RWindow> childWins = childWindowMap.get(typeId);
            for(String uniqueId : childWins.keySet()) {
                RWindow childWin = childWins.get(uniqueId);
                if(childWin == win) {
                    return new String[] {typeId, uniqueId};
                }
            }
        }
        throw new RuntimeException("Supplied frame is not a child frame.");
    }

    public RWindow getChildWindow(String typeId) {
        return getChildWindow(typeId, null);
    }
    public RWindow getChildWindow(String typeId, String uniqueId) {
        Map<String, RWindow> childWins = childWindowMap.get(typeId);
        if(childWins == null) {
            throw new RuntimeException("Unknown child frame type '" + typeId + "' with ID '" + uniqueId + "'.");
        }
        RWindow childWin = childWins.get(uniqueId);
        if(childWin == null) {
            throw new RuntimeException("Unknown child frame type '" + typeId + "' with ID '" + uniqueId + "'.");
        }
        return childWin;
    }
    public boolean existsChildWindow(String typeId) {
        return existsChildWindow(typeId, null);
    }
    public boolean existsChildWindow(String typeId, String uniqueId) {
        try {
            getChildWindow(typeId, uniqueId);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public String getTypeIdOfWindow(RWindow win) {
        try {
            String[] keys = getKeysFromWindow(win);
            return keys[0];
        } catch(Exception e) {
            return null;
        }
    }

    public String getUniqueIdOfWindow(RWindow win) {
        try {
            String[] keys = getKeysFromWindow(win);
            return keys[1];
        } catch(Exception e) {
            return null;
        }
    }

    public String[] getRegisteredTypeIds() {
        return childWindowHandlerMap.keySet().toArray(new String[0]);
    }
    public String[] getAllTypeIds() {
        return childWindowMap.keySet().toArray(new String[0]);
    }
    public Map<String, String[]> getAllUniqueIds() {
        Map<String, String[]> ret = new TreeMap<String, String[]>();
        for(String typeId : childWindowMap.keySet()) {
            Map<String, RWindow> childWins = childWindowMap.get(typeId);
            ret.put(typeId, childWins.keySet().toArray(new String[0]));
        }
        return ret;
    }

    public List<RWindow> getAllChildWindows() {
        List<RWindow> all = new ArrayList<RWindow>();
        for(Map<String, RWindow> childWins : childWindowMap.values()) {
            for(RWindow childWin : childWins.values()) {
                all.add(childWin);
            }
        }
        return all;
    }
    public List<RWindow> getVisibleChildWindows() {
        List<RWindow> all = new ArrayList<RWindow>();
        for(Map<String, RWindow> childWins : childWindowMap.values()) {
            for(RWindow childWin : childWins.values()) {
                if(childWin.isVisible()) {
                    all.add(childWin);
                }
            }
        }
        return all;
    }

    public <T extends RWindow> T getOrCreate(String typeId, String uniqueId, Object... args) {
        RWindow win;
        if(existsChildWindow(typeId, uniqueId)) {
            win = getChildWindow(typeId, uniqueId);
        } else {
            win = createChildWindow(typeId, uniqueId, args);
        }
        return (T) win;
    }
}
