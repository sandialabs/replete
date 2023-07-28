package replete.ui.nofire;

import java.awt.Component;
import java.lang.reflect.Field;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.ui.tabbed.RTabbedPane;
import sun.swing.SwingUtilities2;

public class NoFireTabbedPane extends RTabbedPane {


    ///////////
    // FIELD //
    ///////////

    private boolean suppressFire = false;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NoFireTabbedPane() {
        super();
    }
    public NoFireTabbedPane(boolean dc) {
        super(dc);
    }
    public NoFireTabbedPane(int tabPlacement, boolean dc) {
        super(tabPlacement, dc);
    }
    public NoFireTabbedPane(int tabPlacement, int tabLayoutPolicy, boolean dc) {
        super(tabPlacement, tabLayoutPolicy, dc);
    }
    public NoFireTabbedPane(int tabPlacement, int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
    }
    public NoFireTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }


    //////////////
    // MUTATORS //
    //////////////

    public void setSelectedIndexNoFire(int index) {
        suppressFire = true;
        super.setSelectedIndex(index);
        suppressFire = false;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void fireStateChanged() {
        /* --- Begin code to deal with visibility --- */

        /* This code deals with changing the visibility of components to
         * hide and show the contents for the selected tab. It duplicates
         * logic already present in BasicTabbedPaneUI, logic that is
         * processed during the layout pass. This code exists to allow
         * developers to do things that are quite difficult to accomplish
         * with the previous model of waiting for the layout pass to process
         * visibility changes; such as requesting focus on the new visible
         * component.
         *
         * For the average code, using the typical JTabbedPane methods,
         * all visibility changes will now be processed here. However,
         * the code in BasicTabbedPaneUI still exists, for the purposes
         * of backward compatibility. Therefore, when making changes to
         * this code, ensure that the BasicTabbedPaneUI code is kept in
         * synch.
         */

        Field visCompField;
        Component visComp;
        try {
            visCompField = JTabbedPane.class.getDeclaredField("visComp");
            visCompField.setAccessible(true);
            visComp = (Component) visCompField.get(this);
        } catch(Exception e) {
            e.printStackTrace();
            return;
        }

        int selIndex = getSelectedIndex();

        /* if the selection is now nothing */
        if (selIndex < 0) {
            /* if there was a previous visible component */
            if (visComp != null && visComp.isVisible()) {
                /* make it invisible */
                visComp.setVisible(false);
            }

            /* now there's no visible component */
            visComp = null;
            try {
                visCompField.set(this, visComp);
            } catch(Exception e) {
                e.printStackTrace();
            }

        /* else - the selection is now something */
        } else {
            /* Fetch the component for the new selection */
            Component newComp = getComponentAt(selIndex);

            /* if the new component is non-null and different */
            if (newComp != null && newComp != visComp) {
                boolean shouldChangeFocus = false;

                /* Note: the following (clearing of the old visible component)
                 * is inside this if-statement for good reason: Tabbed pane
                 * should continue to show the previously visible component
                 * if there is no component for the chosen tab.
                 */

                /* if there was a previous visible component */
                if (visComp != null) {
                    shouldChangeFocus =
                        (SwingUtilities.findFocusOwner(visComp) != null);

                    /* if it's still visible */
                    if (visComp.isVisible()) {
                        /* make it invisible */
                        visComp.setVisible(false);
                    }
                }

                if (!newComp.isVisible()) {
                    newComp.setVisible(true);
                }

                if (shouldChangeFocus) {
                    SwingUtilities2.tabbedPaneChangeFocusTo(newComp);
                }

                visComp = newComp;
                try {
                    visCompField.set(this, visComp);
                } catch(Exception e) {
                    e.printStackTrace();
                    return;
                }
            } /* else - the visible component shouldn't changed */
        }

        /* --- End code to deal with visibility --- */

        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if(suppressFire && !listeners[i + 1].getClass().toString().contains("TabbedPaneUI")) {
               continue;
            }
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null) {
                    changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
}
