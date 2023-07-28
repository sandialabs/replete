package replete.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import replete.collections.ExtArrayList;
import replete.collections.TrackedBean;
import replete.profiler.RProfiler;

// Alternate method names:
//   startAll -> start
//   pauseAll -> pause
//   resetAll -> reset
//   disposeAll -> dispose

// NOTE: If PersistentController were turned into an interface,
// then this list of PersistentControllers could itself BE a
// PersistentController.  We wouldn't have methods called
// "xxAll" and instead just use the PC's method names (e.g. start).
// The current PersistentController class would have to be
// renamed to something like AtomicPersistentController or
// BasicPersistentController and it would implement this new
// PersistentController interface.

public class TrackedPersistentControllerList
        <C extends TrackedPersistentController,
            P extends TrackedBean, S extends TrackedBean>
                extends ExtArrayList<C> {                  // There's no PersistentControllerList yet


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TrackedPersistentControllerList() {}
    public TrackedPersistentControllerList(boolean requireUniqueClasses) {
        super(requireUniqueClasses);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Computed

    public synchronized <B extends C> B get(UUID id) {
        for(C controller : this) {
            if(controller.getTrackedId().equals(id)) {
                return (B) controller;
            }
        }
        return null;
    }

    public synchronized <B extends C> B get(Class<? extends TrackedPersistentController> clazz) {
        return (B) getElementByType(clazz);
    }


    ////////////////////////////////////////////////
    // PERSISTENT CONTROLLER AGGREGATE OPERATIONS //
    ////////////////////////////////////////////////

    public synchronized void readyAll() {
        for(C controller : this) {
            controller.ready();
        }
    }

    public synchronized void startAll() {
        RProfiler R = RProfiler.get();
        for(C controller : this) {
            R.block("Start: " + controller.getClass().getSimpleName());
            try {
                controller.start();
            } finally {
                R.end();
            }
        }
    }

    public synchronized void pauseAll() {
        for(C controller : this) {
            if(controller.isPauseable()) {
                controller.pause();
            }
        }
    }

    public synchronized boolean isPaused() {
        for(C controller : this) {
            if(controller.isPauseable()) {
                if(!controller.isPaused()) {
                    return false;
                 }
            }
        }
        return true;
    }

    public synchronized void resetAll() {
        for(C controller : this) {
            if(controller.isResettable()) {
                controller.reset();
            }
        }
    }

    public synchronized void disposeAll() {
        for(C controller : this) {
            controller.dispose();
        }
    }

    public synchronized List<TrackedPersistentControllerStatus> getStatuses() {
        List<TrackedPersistentControllerStatus> statuses = new ArrayList<>();
        for(C controller : this) {
            statuses.add(new TrackedPersistentControllerStatus(controller));
        }
        return statuses;
    }

    public synchronized void update(List<? extends P> params, boolean startCreated, Function<P, ? extends C> controllerCreator) {
        if(isRequireUniqueClasses()) {
            if(isEmpty()) {
                updateWithFullReconcile(params, startCreated, controllerCreator);
            } else {
                updateWithParamTypeCheckOnly(params);
            }
        } else {
            updateWithFullReconcile(params, startCreated, controllerCreator);
        }
    }
    private void updateWithParamTypeCheckOnly(List<? extends P> params) {
        if(size() != params.size()) {
            throw new IllegalStateException("the number of params provided do not match the number of controllers that need to be updated");
        }
        RProfiler R = RProfiler.get();
        for(C controller : this) {
            boolean found = false;
            P currentParams = (P) controller.getParams();
            R.block("Update/1: " + currentParams.getClass().getSimpleName());
            try {
                for(P newParams : params) {
                    if(newParams.getClass().equals(currentParams.getClass())) {
                        if(controller.isUpdatable()) {
                            controller.setParams(newParams);
                            controller.update();
                        }
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    throw new IllegalStateException("the types of params provided do not match the controllers that need to be updated");
                }
            } finally {
                R.end();
            }
        }
    }
    private void updateWithFullReconcile(List<? extends P> params, boolean startCreated,
                                         Function<P, ? extends C> controllerCreator) {
        RProfiler R = RProfiler.get();
        List<C> toRemove = new ArrayList<>(this);

        for(P newParams : params) {
            R.block("Update/2: " + newParams.getClass().getSimpleName());
            try {
                UUID newId = newParams.getTrackedId();
                C controller = get(newId);

                if(controller != null) {
                    if(controller.isUpdatable()) {
                        controller.setParams(newParams);
                        controller.update();
                    }
                    toRemove.remove(controller);  // Remove from remove list = keep
                } else {
                    C newController = controllerCreator.apply(newParams);
                    if(newController != null) {    // Null means please don't add this PC to list
                        add(newController);
                        if(startCreated) {
                            newController.start();
                        }
                    }
                }
            } finally {
                R.end();
            }
        }

        for(C removeController : toRemove) {
            this.remove(removeController);
            removeController.dispose();
        }
    }

    public synchronized List<S> createSummaryStates() {
        RProfiler R = RProfiler.get();
        List<S> summaryStates = new ArrayList<>();
        for(C controller : this) {
            R.block("CreateSS: " + controller.getClass().getSimpleName());
            try {
                S summaryState = (S) controller.createSummaryState();
                summaryStates.add(summaryState);
            } finally {
                R.end();
            }
        }
        return summaryStates;
    }
}
