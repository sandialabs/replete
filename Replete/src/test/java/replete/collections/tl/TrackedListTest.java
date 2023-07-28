package replete.collections.tl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;

import org.junit.Before;
import org.junit.Test;

import replete.collections.tl.events.TLAddEvent;
import replete.collections.tl.events.TLChangeEvent;
import replete.collections.tl.events.TLClearEvent;
import replete.collections.tl.events.TLElementChangeEvent;
import replete.collections.tl.events.TLEvent;
import replete.collections.tl.events.TLMoveEvent;
import replete.collections.tl.events.TLRemoveEvent;
import replete.collections.tl.events.TLReplaceEvent;
import replete.event.ChangeNotifier;

public class TrackedListTest {


    ////////////
    // FIELDS //
    ////////////

    private ExtTrackedList<Object> list;    // Slightly extended TrackedList

    private int addCount;
    private int removeCount;
    private int moveCount;
    private int replaceCount;
    private int clearCount;
    private int changeCount;
    private int elemChangeCount;
    private int eventCount; // Should always be equal
    private int anyCount;   // Should always be equal

    private TLAddEvent<Object> prevAddEvent;
    private TLRemoveEvent<Object> prevRemoveEvent;
    private TLMoveEvent<Object> prevMoveEvent;
    private TLReplaceEvent<Object> prevReplaceEvent;
    private TLClearEvent<Object> prevClearEvent;
    private TLChangeEvent<Object> prevChangeEvent;
    private TLElementChangeEvent<Object> prevElemChangeEvent;
    private TLEvent<Object> prevEvent;

    private List<TrackableObject> trackables;

    ////////////////
    // TEST SETUP //
    ////////////////

    @Before
    public void setUp() {
        list = new ExtTrackedList<Object>();
        list.addAddListener(new TLAddListener<Object>() {
            @Override
            public void stateChanged(TLAddEvent<Object> e) {
                addCount++;
                prevAddEvent = e;
                eventCount++;
            }
        });
        list.addRemoveListener(new TLRemoveListener<Object>() {
            @Override
            public void stateChanged(TLRemoveEvent<Object> e) {
                removeCount++;
                prevRemoveEvent = e;
                eventCount++;
            }
        });
        list.addMoveListener(new TLMoveListener<Object>() {
            @Override
            public void stateChanged(TLMoveEvent<Object> e) {
                moveCount++;
                prevMoveEvent = e;
                eventCount++;
            }
        });
        list.addReplaceListener(new TLReplaceListener<Object>() {
            @Override
            public void stateChanged(TLReplaceEvent<Object> e) {
                replaceCount++;
                prevReplaceEvent = e;
                eventCount++;
            }
        });
        list.addClearListener(new TLClearListener<Object>() {
            @Override
            public void stateChanged(TLClearEvent<Object> e) {
                clearCount++;
                prevClearEvent = e;
                eventCount++;
            }
        });
        list.addChangeListener(new TLChangeListener<Object>() {
            @Override
            public void stateChanged(TLChangeEvent<Object> e) {
                changeCount++;
                prevChangeEvent = e;
                eventCount++;
            }
        });
        list.addElementChangeListener(new TLElementChangeListener<Object>() {
            @Override
            public void stateChanged(TLElementChangeEvent<Object> e) {
                elemChangeCount++;
                prevElemChangeEvent = e;
                eventCount++;
            }
        });
        list.addAnyEventListener(new TLAnyEventListener<Object>() {
            @Override
            public void stateChanged(TLEvent<Object> e) {
                prevEvent = e;
                anyCount++;
            }
        });

        addCount = 0;
        removeCount = 0;
        moveCount = 0;
        replaceCount = 0;
        clearCount = 0;
        changeCount = 0;
        elemChangeCount = 0;
        eventCount = 0;
        anyCount = 0;

        prevAddEvent = null;
        prevRemoveEvent = null;
        prevMoveEvent = null;
        prevReplaceEvent = null;
        prevClearEvent = null;
        prevChangeEvent = null;
        prevElemChangeEvent = null;
        prevEvent = null;

        trackables = new ArrayList<TrackableObject>();
    }

    @Test
    public void addRemoveMoveReplaceClear() {

        checkBlankEvents();

        list.clear();                              // No-Op
        checkCounts(0, 0, 0, 0, 0, 0, 0, 0);
        checkAddEvent();
        checkRemoveEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkChangeEvent();
        checkClearEvent();
        checkEvent();
        checkSize(0);

        list.remove("Non-Existent");               // No-Op
        checkCounts(0, 0, 0, 0, 0, 0, 0, 0);
        checkAddEvent();
        checkRemoveEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkChangeEvent();
        checkClearEvent();
        checkEvent();
        checkSize(0);

        list.add("Mercury");
        checkCounts(1, 0, 0, 0, 0, 1, 0, 2);
        checkAddEvent(ItoE(0, "Mercury"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(1);

        list.add("Jupiter");
        list.add("Saturn");
        list.add("Pluto");
        checkCounts(4, 0, 0, 0, 0, 4, 0, 8);
        checkAddEvent(ItoE(3, "Pluto"));
        checkEvent(checkChangeEvent(true));
        checkSize(4);

        list.add(1, "Venus");
        checkCounts(5, 0, 1, 0, 0, 5, 0, 11);
        checkAddEvent(ItoE(1, "Venus"));
        checkMoveEvent(ItoE(2, "Jupiter", 3, "Saturn", 4, "Pluto"), ItoPrevI(2, 1, 3, 2, 4, 3));
        checkEvent(checkChangeEvent(true));
        checkSize(5);

        list.addAll(2, Arrays.asList(new String[] {"Earth", "Mars"}));
        checkCounts(6, 0, 2, 0, 0, 6, 0, 14);
        checkAddEvent(ItoE(2, "Earth", 3, "Mars"));
        checkMoveEvent(ItoE(4, "Jupiter", 5, "Saturn", 6, "Pluto"), ItoPrevI(4, 2, 5, 3, 6, 4));
        checkEvent(checkChangeEvent(true));
        checkReplaceEvent();
        checkSize(7);

        resetPrevious();
        list.set(list.size() - 1, "SadPluto");
        checkCounts(6, 0, 2, 1, 0, 7, 0, 16);
        checkAddEvent();
        checkRemoveEvent();
        checkReplaceEvent(6, "Pluto", "SadPluto");
        checkEvent(checkChangeEvent(true));
        checkSize(7);

        checkList("Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "SadPluto");

        resetPrevious();
        list.add(list.size(), "TheBeyond");
        checkCounts(7, 0, 2, 1, 0, 8, 0, 18);
        checkAddEvent(ItoE(7, "TheBeyond"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(8);

        list.addAll(Arrays.asList(new String[0]));
        checkCounts(7, 0, 2, 1, 0, 8, 0, 18);
        checkAddEvent(ItoE(7, "TheBeyond"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(8);

        list.addAll(list.size(), Arrays.asList(new String[] {"TheBeyond2", "TheBeyond3"}));
        checkCounts(8, 0, 2, 1, 0, 9, 0, 20);
        checkAddEvent(ItoE(8, "TheBeyond2", 9, "TheBeyond3"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(10);

        list.remove("TheBeyond3");
        checkCounts(8, 1, 2, 1, 0, 10, 0, 22);
        checkRemoveEvent(ItoE(9, "TheBeyond3"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(9);

        list.removeAll(Arrays.asList(new String[] {"TheBeyond", "TheBeyond2"}));
        checkCounts(8, 2, 2, 1, 0, 11, 0, 24);
        checkRemoveEvent(ItoE(7, "TheBeyond", 8, "TheBeyond2"));
        checkMoveEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(7);

        list.removeAll(Arrays.asList(new String[] {"Venus", "Earth"}));
        checkCounts(8, 3, 3, 1, 0, 12, 0, 27);
        checkRemoveEvent(ItoE(1, "Venus", 2, "Earth"));
        checkMoveEvent(ItoE(1, "Mars", 2, "Jupiter", 3, "Saturn", 4, "SadPluto"), ItoPrevI(1, 3, 2, 4, 3, 5, 4, 6));
        checkEvent(checkChangeEvent(true));
        checkSize(5);

        checkList("Mercury", "Mars", "Jupiter", "Saturn", "SadPluto");

        list.removeAll(Arrays.asList(new String[] {"Mars", "Saturn"}));
        checkCounts(8, 4, 4, 1, 0, 13, 0, 30);
        checkRemoveEvent(ItoE(1, "Mars", 3, "Saturn"));
        checkMoveEvent(ItoE(1, "Jupiter", 2, "SadPluto"), ItoPrevI(1, 2, 2, 4));
        checkClearEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(3);

        resetPrevious();
        list.remove(2);
        checkCounts(8, 5, 4, 1, 0, 14, 0, 32);
        checkRemoveEvent(ItoE(2, "SadPluto"));
        checkMoveEvent();
        checkClearEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(2);

        list.remove(0);
        checkCounts(8, 6, 5, 1, 0, 15, 0, 35);
        checkRemoveEvent(ItoE(0, "Mercury"));
        checkMoveEvent(ItoE(0, "Jupiter"), ItoPrevI(0, 1));
        checkClearEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(1);

        checkList("Jupiter");

        resetPrevious();
        list.clear();
        checkCounts(8, 7, 5, 1, 1, 16, 0, 38);
        checkAddEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkRemoveEvent(ItoE(0, "Jupiter"));
        checkClearEvent(true);
        checkEvent(checkChangeEvent(true));
        checkSize(0);
        checkList();

        resetPrevious();
        list.clear();
        checkCounts(8, 7, 5, 1, 1, 16, 0, 38);
        checkAddEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkRemoveEvent();
        checkClearEvent();
        checkChangeEvent();
        checkEvent();
        checkSize(0);
        checkList();

        list.add("Jupiter");
        list.remove(0);
        checkCounts(9, 8, 5, 1, 2, 18, 0, 43);
        checkAddEvent(ItoE(0, "Jupiter"));
        checkRemoveEvent(ItoE(0, "Jupiter"));
        checkMoveEvent();
        checkReplaceEvent();
        checkClearEvent(true);
        checkEvent(checkChangeEvent(true));
        checkSize(0);

        resetPrevious();
        list.add("Jupiter");
        list.retainAll(Arrays.asList(new String[] {"Jupiter"}));
        checkCounts(10, 8, 5, 1, 2, 19, 0, 45);
        checkAddEvent(ItoE(0, "Jupiter"));
        checkRemoveEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkClearEvent();
        checkEvent(checkChangeEvent(true));
        checkSize(1);

        resetPrevious();
        list.retainAll(Arrays.asList(new String[] {"Pluto"}));
        checkCounts(10, 9, 5, 1, 3, 20, 0, 48);
        checkAddEvent();
        checkRemoveEvent(ItoE(0, "Jupiter"));
        checkMoveEvent();
        checkReplaceEvent();
        checkClearEvent(true);
        checkEvent(checkChangeEvent(true));
        checkSize(0);

        checkElementChangeEvent();
    }

    @Test
    public void addRemoveMoveReplaceSuppress() {

        list.setSuppressEvents(true);

        checkBlankEvents();

        list.clear();
        checkBlankEvents();

        list.remove("Non-Existent");
        checkBlankEvents();

        list.add("Mercury");
        checkBlankEvents();

        list.add("Jupiter");
        list.add("Saturn");
        list.add("Pluto");
        checkBlankEvents();

        list.add(1, "Venus");
        checkBlankEvents();

        list.addAll(2, Arrays.asList(new String[] {"Earth", "Mars"}));
        checkBlankEvents();

        list.set(list.size() - 1, "SadPluto");
        checkBlankEvents();

        list.add(list.size(), "TheBeyond");
        checkBlankEvents();

        list.addAll(Arrays.asList(new String[0]));
        checkBlankEvents();

        list.addAll(list.size(), Arrays.asList(new String[] {"TheBeyond2", "TheBeyond3"}));
        checkBlankEvents();

        list.remove("TheBeyond3");
        checkBlankEvents();

        list.removeAll(Arrays.asList(new String[] {"TheBeyond", "TheBeyond2"}));
        checkBlankEvents();

        list.removeAll(Arrays.asList(new String[] {"Venus", "Earth"}));
        checkBlankEvents();

        list.removeAll(Arrays.asList(new String[] {"Mars", "Saturn"}));
        checkBlankEvents();

        list.remove(2);
        checkBlankEvents();

        list.remove(0);
        checkBlankEvents();

        list.clear();
        checkBlankEvents();

        list.clear();
        checkBlankEvents();

        list.add("Jupiter");
        list.remove(0);
        checkBlankEvents();

        list.add("Jupiter");
        list.retainAll(Arrays.asList(new String[] {"Jupiter"}));
        checkBlankEvents();

        list.retainAll(Arrays.asList(new String[] {"Pluto"}));
        checkBlankEvents();
    }

    @Test
    public void elementChange() {

        list.addAll(Arrays.asList(
            new Object[] {
                "Red", TO("Alpha"),
                "Blue", TO("Beta"),
                "Green", TO("Gamma")
            }
        ));

        checkTrackedListeners();

        list.setTrackElements(true);

        checkTrackedListeners(1, 3, 5);

        list.setTrackElements(false);

        checkTrackedListeners();

        list.setTrackElements(true);

        checkTrackedListeners(1, 3, 5);

        list.add("Yellow");

        checkTrackedListeners(1, 3, 5);

        list.add(0, "Black");

        checkTrackedListeners(2, 4, 6);

        list.add(TO("Delta"));

        checkTrackedListeners(2, 4, 6, 8);

        list.add(TO("Epsilon"));

        checkTrackedListeners(2, 4, 6, 8, 9);

        list.set(4, "Pink");

        checkTrackedListeners(2, 6, 8, 9);

        list.set(7, TO("Zeta"));

        checkTrackedListeners(2, 6, 7, 8, 9);

        list.remove(2);

        checkTrackedListeners(5, 6, 7, 8);

        for(TrackableObject to : trackables) {
            to.setValue(to.getValue() + " <NEWSTUFF>");
        }
    }

    @Test
    public void elemChangeFail() {

        list.addAll(Arrays.asList(
            new Object[] {
                "Red",
                "Blue",
                "Green",
            }
        ));

        list.setTrackElements(true);

        list.add(0, TO("Alpha"));
    }


    /////////////////
    // LIST CHECKS //
    /////////////////

    private void checkList(Object... expected) {
        assertEquals("{LIST}", Arrays.asList(expected), list);
    }
    private void checkSize(int expected) {
        assertEquals("{LIST-SIZE}", expected, list.size());
    }


    //////////////////
    // EVENT CHECKS //
    //////////////////

    // Add

    private TLEvent<Object> checkAddEvent() {
        return checkAddEvent(null);
    }
    private TLEvent<Object> checkAddEvent(Map<Integer, Object> ae) {
        TLAddEvent<Object> expected = null;
        if(ae != null) {
            expected = new TLAddEvent<Object>(list, ae);
        }
        assertEquals("{ADD-EVENT}", expected, prevAddEvent);
        return expected;
    }

    // Remove

    private TLEvent<Object> checkRemoveEvent() {
        return checkRemoveEvent(null);
    }
    private TLEvent<Object> checkRemoveEvent(Map<Integer, Object> re) {
        TLRemoveEvent<Object> expected = null;
        if(re != null) {
            expected = new TLRemoveEvent<Object>(list, re);
        }
        assertEquals("{REMOVE-EVENT}", expected, prevRemoveEvent);
        return expected;
    }

    // Move

    private TLEvent<Object> checkMoveEvent() {
        return checkMoveEvent(null, null);
    }
    private TLEvent<Object> checkMoveEvent(Map<Integer, Object> itoE, Map<Integer, Integer> itoPrevI) {
        TLMoveEvent<Object> expected = null;
        if(itoE != null) {
            expected = new TLMoveEvent<Object>(list, itoE, itoPrevI);
        }
        assertEquals("{MOVE-EVENT}", expected, prevMoveEvent);
        return expected;
    }

    // Replace

    private TLEvent<Object> checkReplaceEvent() {
        return checkReplaceEvent(-1, null, null);
    }
    private TLEvent<Object> checkReplaceEvent(int index, String previous, String current) {
        TLReplaceEvent<Object> expected = null;
        if(index != -1) {
            expected = new TLReplaceEvent(list, index, previous, current);
        }
        assertEquals("{REPLACE-EVENT}", expected, prevReplaceEvent);
        return expected;
    }

    // Clear

    private TLEvent<Object> checkClearEvent() {
        return checkClearEvent(false);
    }
    private TLEvent<Object> checkClearEvent(boolean notNull) {
        TLClearEvent<Object> expected = null;
        if(notNull) {
            expected = new TLClearEvent<Object>(list);
        }
        assertEquals("{CLEAR-EVENT}", expected, prevClearEvent);
        return expected;
    }

    // Change

    private TLEvent<Object> checkChangeEvent() {
        return checkChangeEvent(false);
    }
    private TLEvent<Object> checkChangeEvent(boolean notNull) {
        TLChangeEvent<Object> expected = null;
        if(notNull) {
            expected = new TLChangeEvent<Object>(list);
        }
        assertEquals("{CHANGE-EVENT}", expected, prevChangeEvent);
        return expected;
    }

    // Element Change

    private TLEvent<Object> checkElementChangeEvent() {
        return checkElementChangeEvent(-1, null);
    }
    private TLEvent<Object> checkElementChangeEvent(int index, String element) {
        TLElementChangeEvent<Object> expected = null;
        if(index != -1) {
            expected = new TLElementChangeEvent(list, index, element);
        }
        assertEquals("{ELEM-CHANGE-EVENT}", expected, prevElemChangeEvent);
        return expected;
    }

    // Any

    private void checkEvent() {
        checkEvent(null);
    }
    private void checkEvent(TLEvent<Object> evt) {
        assertEquals("{EVENT}", evt, prevEvent);
    }

    private void checkCounts(int expectedAddCount, int expectedRemoveCount,
                             int expectedMoveCount, int expectedReplaceCount,
                             int expectedClearCount, int expectedChangeCount,
                             int expectedElemChangeCount, int expectedEventCount) {
        assertEquals("{ADD-COUNT}", expectedAddCount, addCount);
        assertEquals("{REMOVE-COUNT}", expectedRemoveCount, removeCount);
        assertEquals("{MOVE-COUNT}", expectedMoveCount, moveCount);
        assertEquals("{REPLACE-COUNT}", expectedReplaceCount, replaceCount);
        assertEquals("{CLEAR-COUNT}", expectedClearCount, clearCount);
        assertEquals("{CHANGE-COUNT}", expectedChangeCount, changeCount);
        assertEquals("{ELEM-CHANGE-COUNT}", expectedElemChangeCount, elemChangeCount);
        assertEquals("{EVENT-COUNT}", expectedEventCount, eventCount);
        assertEquals("{ANY-COUNT}", expectedEventCount, anyCount);
    }

    private void checkBlankEvents() {
        checkCounts(0, 0, 0, 0, 0, 0, 0, 0);
        checkAddEvent();
        checkMoveEvent();
        checkReplaceEvent();
        checkRemoveEvent();
        checkClearEvent();
        checkChangeEvent();
        checkElementChangeEvent();
        checkEvent();
        checkTrackedListeners();
    }

    private void checkTrackedListeners(Integer... indices) {
        List<Integer> indexList = Arrays.asList(indices);
        List<ElementChangeListener<Object>> listeners = list.getElementListeners();
        if(list.isTrackElements()) {
            assertEquals("{ELEM-LISTENERS-SIZE}", list.size(), listeners.size());
        } else {
            assertEquals("{ELEM-LISTENERS-SIZE}", 0, listeners.size());
        }
        for(int i = 0; i < listeners.size(); i++) {
            ElementChangeListener<Object> listener = listeners.get(i);
            if(indexList.contains(i)) {
                assertNotNull("{ELEM-LISTENER-" + i + "}", listener);
                assertEquals("{ELEM-LISTENER-" + i + "-INDEX}", listener.getIndex(), i);
            }else {
                assertEquals("{ELEM-LISTENER-" + i + "}", null, listener);
            }
        }
    }


    ////////////
    // HELPER //
    ////////////

    private Map<Integer, Object> ItoE(Object... args) {
        Map<Integer, Object> map = new LinkedHashMap<Integer, Object>();
        for(int a = 0; a < args.length; a += 2) {
            map.put((Integer) args[a], args[a + 1]);
        }
        return map;
    }

    private Map<Integer, Integer> ItoPrevI(Object... args) {
        Map<Integer, Integer> map = new LinkedHashMap<Integer, Integer>();
        for(int a = 0; a < args.length; a += 2) {
            map.put((Integer) args[a], (Integer) args[a + 1]);
        }
        return map;
    }

    private void resetPrevious() {
        prevAddEvent = null;
        prevRemoveEvent = null;
        prevMoveEvent = null;
        prevReplaceEvent = null;
        prevClearEvent = null;
        prevChangeEvent = null;
        prevEvent = null;
    }

    private TrackableObject TO(String value) {
        TrackableObject trackable = new TrackableObject(value);
        trackables.add(trackable);
        return trackable;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private static class TrackableObject implements ChangeTrackable {


        ////////////
        // FIELDS //
        ////////////

        public String value;


        //////////////////
    // CONSTRUCTORS //
    //////////////////

        public TrackableObject(String v) {
            value = v;
        }


        //////////////////////////
        // ACCESSORS / MUTATORS //
        //////////////////////////

        public String getValue() {
            return value;
        }
        public void setValue(String v) {
            value = v;
            fireChangeNotifier();
        }


        ///////////////
        // NOTIFIERS //
        ///////////////

        private ChangeNotifier changeNotifier = new ChangeNotifier(this);
        @Override
        public void addChangeListener(ChangeListener listener) {
            changeNotifier.addListener(listener);
        }
        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeNotifier.removeListener(listener);
        }
        protected void fireChangeNotifier() {
            changeNotifier.fireStateChanged();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public String toString() {
            return "TO[" + value + "]";
        }
    }

    private class ExtTrackedList<T> extends TrackedList<T> {
        private Map<Object, ElementChangeListener<T>> elementListenerMap =
            new HashMap<Object, ElementChangeListener<T>>();

        @Override
        protected void addElementChangeListenerToElement(int index, T element, int which) {
            if(trackElements) {
                ElementChangeListener<T> listener = null;
                if(element instanceof ChangeTrackable) {
                    listener =
                        new ElementChangeListener(this, index);
                    ((ChangeTrackable) element).addChangeListener(listener);
                    elementListenerMap.put(element, listener);  // Line added
                }
                if(which == REPL) {
                    elementListeners.set(index, listener);
                } else {
                    elementListeners.add(index, listener);
                }
            }
        }

        @Override
        protected void removeElementChangeListenerFromElement(int index, T element, int which) {
            if(trackElements) {
                if(element instanceof ChangeTrackable) {
                    ElementChangeListener<T> listener = elementListeners.get(index);
                    if(listener != null) {
                        if(listener.getIndex() != index) {
                            throw new IllegalStateException("Listener identifying itself as [" + listener.getIndex() + "] was found to be in position [" + index +  "]");
                        }
                        ((ChangeTrackable) element).removeChangeListener(listener);
                        listener.invalidate();

                        // Lines added
                        if(!elementListenerMap.containsKey(element)) {
                            throw new IllegalStateException("Element did not have an associated listener.");
                        }
                        elementListenerMap.remove(element);
                    }
                }
                if(which == REPL) {
                    elementListeners.set(index, null);
                } else {
                    elementListeners.remove(index);
                }
            }
        }

        @Override
        protected void listenerCallback(ElementChangeListener<T> listener, boolean invalid, int index) {
            if(!invalid) {
                assertEquals("{ELEM-LISTENER-INSTANCE}", elementListenerMap.get(get(index)), listener);
                TLElementChangeEvent<T> event = new TLElementChangeEvent<T>(
                    this, index, get(index));
                fireElementChangeNotifier(event);
            } else {
                //System.out.println("! TrackedObject attempted to call an invalid element change listener [was index " + index + "]");
            }
        }
    }
}
