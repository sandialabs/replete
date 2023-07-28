package replete.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.numbers.AveragedLong;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.errors.InputCardinalityException;
import replete.pipeline.errors.InputException;
import replete.pipeline.errors.InputNullException;
import replete.pipeline.errors.InputValidationException;
import replete.pipeline.errors.MissingRequiredInputException;
import replete.pipeline.events.InputChangeEvent;
import replete.pipeline.events.InputChangeListener;
import replete.pipeline.events.OutputChangeEvent;
import replete.pipeline.events.OutputChangeListener;
import replete.pipeline.events.ParameterChangeEvent;
import replete.pipeline.events.ParameterChangeListener;
import replete.progress.ProgressMessage;

public abstract class AbstractStage implements Stage {


    ////////////
    // FIELDS //
    ////////////

    // Static

    private static int nextId = 0;  // Could use AtomicInteger

    // Instance

    protected String id;
    protected String name;
    protected Stage parent;
    protected boolean executed = false;

    // Summary

    protected ExecuteSummary summary;
    protected AveragedLong duration = new AveragedLong();
    protected Exception lastError = null;
    protected int executeAttemptedCount = 0;
    protected int executeSuccessCount = 0;
    protected int executeFailedCount = 0;

    // Other

    // List of warnings for just the last execution.  Could be
    // extended to save previous executions' warnings.
    public List<StageWarning> warnings = new ArrayList<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AbstractStage() {
        this(null);
    }
    public AbstractStage(String name) {
        id = getClass().getName() + "#" + getNextId();
        if(name == null) {
            this.name = DEFAULT_STAGE_NAME;
        } else {
            this.name = name;
        }

        duration.setLast(ExecuteSummary.UNSET);
        summary = new DefaultExecuteSummary(
            duration,
            lastError,
            executeAttemptedCount,
            executeSuccessCount,
            executeFailedCount
        );
    }


    ////////////
    // STATIC //
    ////////////

    private static synchronized int getNextId() {
        return nextId++;
    }


    /////////////
    // EXECUTE //
    /////////////

    // This method is analogous to Pipeline.executeWorker
    @Override
    public void execute() {

        // Set up
        executeAttemptedCount++;
        lastError = null;
        duration.setLast(ExecuteSummary.UNSET);  // In case inner does not execute
        warnings.clear();
        // TODO [DMT] In future if we implement the 'pending inputs'
        // feature to support the changing of outputs any time
        // during stage execution, I believe pending inputs would be
        // transferred to live inputs at this point in the process.
        // This implies that there is no public setInput, only getInput.
        // Also implies that there is a pending get/setPendingInput from
        // which stages are not supposed to read from during their
        // execution.
        fireStartNotifier();         // Above initialized state available to inspect

        try {

            // Run
            executeOuter();
            executeSuccessCount++;

        } catch(Exception e) {

            // Error
            lastError = e;
            executeFailedCount++;
            throw e;

        } finally {

            // Clean up
            ExecuteSummary summary = new DefaultExecuteSummary(
                new AveragedLong(duration),
                lastError,
                executeAttemptedCount,
                executeSuccessCount,
                executeFailedCount
            );
            setExecuteSummary(summary);
            fireCompleteNotifier();         // Above summary available to inspect
        }
    }

    protected void executeOuter() {

        // Pre-actions
        checkRequiredInputs();
        validateInputs();

        // Execute inner
        long T = System.currentTimeMillis();
        try {
            executeInner();
        } finally {
            T = System.currentTimeMillis() - T;

            // Note this does not get called if input validation fails above.
            // It records timings of just executeInner methods.
            duration.setAndIncrement(T);
        }

        // Post-actions (these actions will not happen if there was an exception)
        setDirty(false);       // Ignored by in Pipeline.executeWorker
        setExecuted(true);
    }

    protected void setDirty(boolean dirty) {}     // Unneeded by pipeline
    protected abstract void executeInner();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public String getId() {
        return id;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public Stage getParent() {
        return parent;
    }
    @Override
    public boolean isExecuted() {
        return executed;
    }
    @Override
    public Exception getError() {
        return lastError;
    }
    @Override
    public synchronized List<StageWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    @Override
    public ExecuteSummary getExecuteSummary() {
        return summary;
    }

    // Accessors (Computed)

    @Override
    public boolean hasError() {
        return lastError != null;
    }
    @Override
    public synchronized boolean hasWarning() {
        return !warnings.isEmpty();
    }

    // Mutators

    @Override
    public void setParent(Stage parent) {
        this.parent = parent;               // Used for creating compositional hierarchies
                                            // Could create/fire hierarchy listeners
    }
    @Override
    public void setInputs(Map<String, Object> inputs, boolean clearPrevious) throws InputException {
        if(clearPrevious) {
            clearInputs();      // Will clear even if an exception is thrown later
        }
        if(inputs != null) {
            for(String name : inputs.keySet()) {
                setInput(name, inputs.get(name));
            }
        }
    }
    @Override
    public void setInputsMulti(Map<String, List<Object>> inputs, boolean clearPrevious)
            throws InputException {
        if(clearPrevious) {
            clearInputs();      // Will clear even if an exception is thrown later
        }
        if(inputs != null) {
            for(String name : inputs.keySet()) {
                setInputMulti(name, inputs.get(name));
            }
        }
    }
    protected void setExecuted(boolean executed) {
        this.executed = executed;
    }
    protected void setExecuteSummary(ExecuteSummary summary) {
        this.summary = summary;
    }


    //////////////////
    // PAUSE & STOP //
    //////////////////

    // No need for pause or stop methods in this class as both
    // pipeline and atomic stages will behave considerably
    // different.


    //////////
    // MISC //
    //////////

    protected void checkRequiredInputs() {
        for(InputDescriptor id : getInputDescriptors()) {
            if(id.isRequired()) {
                if(!hasInput(id.getName())) {
                    throw new MissingRequiredInputException("Required input '" + id.getName() + "' was not provided.");
                }
                if(!id.isNullAllowed()) {
                    List<Object> inputList = getInputMulti(id.getName());
                    for(Object input : inputList) {
                        if(input == null) {
                            throw new InputNullException("Input '" + id.getName() + "' is cannot be null.");
                        }
                    }
                }
            }
            List<Object> inputList = getInputMulti(id.getName());
            if(inputList.size() < id.getCardinalityMinimum()) {
                throw new InputCardinalityException("Input '" + id.getName() + "' has too few values.");
            }
            if(inputList.size() > id.getCardinalityMaximum()) {
                throw new InputCardinalityException("Input '" + id.getName() + "' has too many values.");
            }
        }
    }

    // Override, inspect inputs, and throw exceptions if
    // the inputs are not appropriate in any way.  The
    // inputs should have already been checked for type-
    // corrected and required-ness.
    protected void validateInputs() throws InputValidationException {}

    protected synchronized void addWarning(String message) {
        addWarning(message, null);
    }
    protected synchronized void addWarning(Exception exception) {
        addWarning(null, exception);
    }
    protected synchronized void addWarning(String message, Exception exception) {
        long now = System.currentTimeMillis();
        warnings.add(new StageWarning(getName(), now, message, exception));
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // Execution started

    private transient ChangeNotifier startNotifier = new ChangeNotifier(this);
    @Override
    public void addStartListener(ChangeListener listener) {
        startNotifier.addListener(listener);
    }
    @Override
    public void removeStartListener(ChangeListener listener) {
        startNotifier.removeListener(listener);
    }
    protected void fireStartNotifier() {
        startNotifier.fireStateChanged();
    }

    // Execution complete

    private transient ChangeNotifier completeNotifier = new ChangeNotifier(this);
    @Override
    public void addCompleteListener(ChangeListener listener) {
        completeNotifier.addListener(listener);
    }
    @Override
    public void removeCompleteListener(ChangeListener listener) {
        completeNotifier.removeListener(listener);
    }
    protected void fireCompleteNotifier() {
        completeNotifier.fireStateChanged();
    }

    // Dirty

    private transient ChangeNotifier dirtyNotifier = new ChangeNotifier(this);
    @Override
    public void addDirtyListener(ChangeListener listener) {
        dirtyNotifier.addListener(listener);
    }
    @Override
    public void removeDirtyListener(ChangeListener listener) {
        dirtyNotifier.removeListener(listener);
    }
    protected void fireDirtyNotifier() {
        dirtyNotifier.fireStateChanged();
    }

    // Progress

    private transient ExtChangeNotifier<ProgressListener> progNotifier = new ExtChangeNotifier<ProgressListener>();
    @Override
    public void addProgressListener(ProgressListener listener) {
        progNotifier.addListener(listener);
    }
    @Override
    public void removeProgressListener(ProgressListener listener) {
        progNotifier.removeListener(listener);
    }
    protected void fireProgressNotifier(ProgressMessage msg) {
        progNotifier.fireStateChanged(new ProgressEvent(msg));
    }

    // Inputs

    private transient ExtChangeNotifier<InputChangeListener> inputChangeNotifier = new ExtChangeNotifier<InputChangeListener>();
    @Override
    public void addInputChangeListener(InputChangeListener listener) {
        inputChangeNotifier.addListener(listener);
    }
    @Override
    public void removeInputChangeListener(InputChangeListener listener) {
        inputChangeNotifier.removeListener(listener);
    }
    protected void fireInputChangeNotifier(InputChangeEvent e) {
        inputChangeNotifier.fireStateChanged(e);
    }

    // Outputs

    private transient ExtChangeNotifier<OutputChangeListener> outputChangeNotifier = new ExtChangeNotifier<OutputChangeListener>();
    @Override
    public void addOutputChangeListener(OutputChangeListener listener) {
        outputChangeNotifier.addListener(listener);
    }
    @Override
    public void removeOutputChangeListener(OutputChangeListener listener) {
        outputChangeNotifier.removeListener(listener);
    }
    protected void fireOutputChangeNotifier(OutputChangeEvent e) {
        outputChangeNotifier.fireStateChanged(e);
    }

    // Parameters

    private transient ExtChangeNotifier<ParameterChangeListener> parameterChangedNotifier = new ExtChangeNotifier<ParameterChangeListener>();
    public void addParameterChangeListener(ParameterChangeListener listener) {
        parameterChangedNotifier.addListener(listener);
    }
    public void removeParameterChangeListener(ParameterChangeListener listener) {
        parameterChangedNotifier.removeListener(listener);
    }
    protected void fireParameterChangeNotifier(ParameterChangeEvent e) {
        // TODO: Automatically change dirty here?
        parameterChangedNotifier.fireStateChanged(e);
    }

    private transient ChangeNotifier pauseRequestedNotifier = new ChangeNotifier(this);
    public void addPauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.addListener(listener);
    }
    public void removePauseRequestedListener(ChangeListener listener) {
        pauseRequestedNotifier.removeListener(listener);
    }
    protected void firePauseRequestedNotifier() {
        pauseRequestedNotifier.fireStateChanged();
    }

    private transient ChangeNotifier stopRequestedNotifier = new ChangeNotifier(this);
    public void addStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.addListener(listener);
    }
    public void removeStopRequestedListener(ChangeListener listener) {
        stopRequestedNotifier.removeListener(listener);
    }
    protected void fireStopRequestedNotifier() {
        stopRequestedNotifier.fireStateChanged();
    }

    private transient ChangeNotifier pauseNotifier = new ChangeNotifier(this);
    public void addPauseListener(ChangeListener listener) {
        pauseNotifier.addListener(listener);
    }
    public void removePauseListener(ChangeListener listener) {
        pauseNotifier.removeListener(listener);
    }
    protected void firePauseNotifier() {
        pauseNotifier.fireStateChanged();
    }

    private transient ChangeNotifier stopNotifier = new ChangeNotifier(this);
    public void addStopListener(ChangeListener listener) {
        stopNotifier.addListener(listener);
    }
    public void removeStopListener(ChangeListener listener) {
        stopNotifier.removeListener(listener);
    }
    protected void fireStopNotifier() {
        stopNotifier.fireStateChanged();
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void run() {
        execute();
    }
}
