package replete.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.DescriptorException;
import replete.pipeline.errors.InputDescriptorException;
import replete.pipeline.errors.InputException;
import replete.pipeline.errors.InvalidInputIndexException;
import replete.pipeline.errors.InvalidInputTypeException;
import replete.pipeline.errors.OutputDescriptorException;
import replete.pipeline.errors.OutputException;
import replete.pipeline.errors.OutputUnsetException;
import replete.pipeline.errors.UnrecognizedInputDescriptorException;
import replete.pipeline.errors.UnrecognizedInputException;
import replete.pipeline.errors.UnrecognizedOutputDescriptorException;
import replete.pipeline.errors.UnrecognizedOutputException;
import replete.pipeline.events.InputChangeEvent;
import replete.pipeline.events.OutputChangeEvent;
import replete.progress.ProgressMessage;
import replete.ttc.DefaultTransparentTaskContext;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TransparentTaskStopException;

public abstract class AbstractAtomicStage extends AbstractStage {


    ////////////
    // FIELDS //
    ////////////

    private boolean dirty = true;
    private boolean regComplete = false;
    private Map<String, OutputDescriptor> outputDescriptors = new LinkedHashMap<>();
    private Map<String, InputDescriptor> inputDescriptors = new LinkedHashMap<>();
    private Map<String, List<Object>> inputs = new HashMap<>();
    private Map<String, Object> outputs = new HashMap<>();
    protected transient TransparentTaskContext ttContext = new DefaultTransparentTaskContext();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AbstractAtomicStage() {
        this(null);
    }
    public AbstractAtomicStage(String name) {
        super(name);
        init();
        regComplete = true;   // No more descriptor registrations past this point.

        ttContext.addPauseRequestedListener(pauseRequestedListener);
        ttContext.addStopRequestedListener(stopRequestedListener);
        ttContext.addPauseListener(pauseListener);
        ttContext.addStopListener(stopListener);
        ttContext.addProgressListener(progressListener);
    }


    //////////////
    // ABSTRACT //
    //////////////

    // Subclasses should initialize their input/output descriptors
    // in this method.
    protected abstract void init();

    // Subclasses should override executeInner to perform the
    // processing required for the stage.
    // protected abstract void executeInner();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    protected void setDirty(boolean dirty) {
        if(this.dirty != dirty) {
            this.dirty = dirty;
            fireDirtyNotifier();
        }
    }


    /////////////////////
    // I/O DESCRIPTORS //
    /////////////////////

    @Override
    public List<InputDescriptor> getInputDescriptors() {
        return Collections.unmodifiableList(new ArrayList<>(inputDescriptors.values()));
    }
    @Override
    public List<OutputDescriptor> getOutputDescriptors() {
        return Collections.unmodifiableList(new ArrayList<>(outputDescriptors.values()));
    }

    @Override
    public InputDescriptor getInputDescriptor(String name) throws UnrecognizedInputDescriptorException {
        if(!inputDescriptors.containsKey(name)) {
            throw new UnrecognizedInputDescriptorException("Input descriptor '" + name + "' does not exist.");
        }
        return inputDescriptors.get(name);
    }
    @Override
    public OutputDescriptor getOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException {
        if(!outputDescriptors.containsKey(name)) {
            throw new UnrecognizedOutputDescriptorException("Output descriptor '" + name + "' does not exist.");
        }
        return outputDescriptors.get(name);
    }

    @Override
    public List<OutputDescriptor> getPublishedOutputDescriptors() {
        return getOutputDescriptors();
    }
    @Override
    public OutputDescriptor getPublishedOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException {
        try {
            return getOutputDescriptor(name);
        } catch(UnrecognizedOutputDescriptorException e) {
            throw new UnrecognizedOutputDescriptorException("Published output descriptor '" + name + "' does not exist.", e);
        }
    }
    @Override
    public void addPublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException {
        throw new OutputDescriptorException("Cannot modify the published output descriptors of an atomic stage.");
    }
    @Override
    public void removePublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException {
        throw new OutputDescriptorException("Cannot modify the published output descriptors of an atomic stage.");
    }

    // For subclasses to call in the overridden init method.

    protected void registerInputDescriptor(InputDescriptor descriptor) {
        if(regComplete) {
            throw new DescriptorException("Cannot register additional input descriptors after stage creation.");
        }
        if(inputDescriptors.containsKey(descriptor.getName())) {
            throw new InputDescriptorException("Input descriptor '" + descriptor.getName() + "' already registered.");
        }
        inputDescriptors.put(descriptor.getName(), descriptor);
    }
    protected void registerOutputDescriptor(OutputDescriptor descriptor) {
        if(regComplete) {
            throw new DescriptorException("Cannot register additional output descriptors after stage creation.");
        }
        if(outputDescriptors.containsKey(descriptor.getName())) {
            throw new OutputDescriptorException("Output descriptor '" + descriptor.getName() + "' already registered.");
        }
        outputDescriptors.put(descriptor.getName(), descriptor);
    }


    ////////////////
    // I/O VALUES //
    ////////////////

    // Inputs

    @Override
    public synchronized Map<String, Object> getInputs() {
        Map<String, Object> sortedInputs = new LinkedHashMap<>();
        for(InputDescriptor desc : inputDescriptors.values()) {
            if(inputs.containsKey(desc.getName())) {
                List<Object> inputList = inputs.get(desc.getName());
                Object input =
                    (inputList == null || inputList.isEmpty()) ?
                        null : inputList.get(0);
                sortedInputs.put(desc.getName(), input);
            }
        }
        return Collections.unmodifiableMap(sortedInputs);
    }
    public synchronized Map<String, List<Object>> getInputsMulti() {
        Map<String, List<Object>> sortedInputs = new LinkedHashMap<>();
        for(InputDescriptor desc : inputDescriptors.values()) {
            List<Object> inputList = inputs.get(desc.getName());
            if(inputList == null) {
                inputList = new ArrayList<>();
            }
            List<Object> unmodInputList = Collections.unmodifiableList(inputList);
            sortedInputs.put(desc.getName(), unmodInputList);
        }
        return Collections.unmodifiableMap(sortedInputs);
    }
    @Override
    public synchronized Object getInput(String name) throws InputException {
        checkInputDescriptor(name);
        List<Object> inputList = inputs.get(name);
        if(inputList == null || inputList.isEmpty()) {
            return null;
        }
        return inputList.get(0);
    }
    @Override
    public synchronized List<Object> getInputMulti(String name) throws InputException {
        checkInputDescriptor(name);
        List<Object> inputList = inputs.get(name);
        if(inputList == null) {
            inputList = new ArrayList<>();
        }
        // TODO: there is a small discrepancy between this method and
        // getInputsMulti, where this method will return a zero-length
        // list for a valid, but missing input and the other will
        // not return that key in its list because it is missing.
        return Collections.unmodifiableList(inputList);
    }
    @Override
    public synchronized boolean hasInput(String name) {
        checkInputDescriptor(name);
        if(!inputs.containsKey(name)) {
            return false;
        }
        List<Object> inputList = inputs.get(name);
        return inputList != null && !inputList.isEmpty();
    }
    @Override
    public synchronized void setInput(String name, Object input) throws InputException {
        checkInputDescriptor(name);
        checkInputType(name, input);
        List<Object> inputList = new ArrayList<>();
        inputList.add(input);
        inputs.put(name, inputList);
        // Dirty event fires before input change event (basically
        // have to choose one order or the other here and this
        // one is slightly more logical).
        setDirty(true);
        fireInputChangeNotifier(new InputChangeEvent(this, name));
    }
    @Override
    public synchronized void setInputMulti(String name, List<Object> inputList) throws InputException {
        checkInputDescriptor(name);
        List<Object> inputListNew = new ArrayList<>();
        if(inputList != null) {
            for(Object input : inputList) {
                checkInputType(name, input);
                inputListNew.add(input);
            }
        }
        if(inputListNew.isEmpty()) {
            inputs.remove(name);
        } else {
            inputs.put(name, inputListNew);
        }
        setDirty(true);
        fireInputChangeNotifier(new InputChangeEvent(this, name));
    }
    @Override
    public synchronized void addInputMulti(String name, Object input) throws InputException {
        checkInputDescriptor(name);
        checkInputType(name, input);
        List<Object> inputList = inputs.get(name);
        if(inputList == null) {
            inputList = new ArrayList<>();
            inputs.put(name, inputList);
        }
        inputList.add(input);
        setDirty(true);
        fireInputChangeNotifier(new InputChangeEvent(this, name));
    }
    @Override
    public synchronized void removeInputMulti(String name, int index) throws InputException {
        checkInputDescriptor(name);
        List<Object> inputList = inputs.get(name);
        if(inputList == null || index < 0 || index >= inputList.size()) {
            throw new InvalidInputIndexException("Index " + index + " is not valid for the input '" + name + "'.");
        }
        inputList.remove(index);
        if(inputList.isEmpty()) {
            inputs.remove(name);
        }
        setDirty(true);
        fireInputChangeNotifier(new InputChangeEvent(this, name));
    }
    @Override
    public synchronized void removeInput(String name) throws InputException {
        checkInputDescriptor(name);
        inputs.remove(name);
        // Dirty event fires before input change event (basically
        // have to choose one order or the other here and this
        // one is slightly more logical).
        setDirty(true);
        fireInputChangeNotifier(new InputChangeEvent(this, name));
    }
    @Override
    public synchronized void clearInputs() {
        if(inputs.size() != 0) {
            inputs.clear();
            // Dirty event fires before input change event (basically
            // have to choose one order or the other here and this
            // one is slightly more logical).
            setDirty(true);
            fireInputChangeNotifier(new InputChangeEvent(this, null));
        }
    }

    // Outputs

    @Override
    public synchronized Map<String, Object> getOutputs() {
        Map<String, Object> sortedOutputs = new LinkedHashMap<>();
        for(OutputDescriptor desc : outputDescriptors.values()) {
            if(outputs.containsKey(desc.getName())) {
                sortedOutputs.put(desc.getName(), outputs.get(desc.getName()));
            }
        }
        return Collections.unmodifiableMap(sortedOutputs);
    }
    @Override
    public synchronized Object getOutput(String name) throws OutputException {
        checkOutputDescriptor(name);
        checkOutput(name);
        return outputs.get(name);     // Returns null if registered output not actually set.
    }
    @Override
    public synchronized boolean hasOutput(String name) {
        checkOutputDescriptor(name);
        return outputs.containsKey(name);
    }
    @Override
    public synchronized Map<String, Object> getPublishedOutputs() {
        return getOutputs();  // Published output descriptors are output descriptors
    }
    @Override
    public synchronized Object getPublishedOutput(String name) throws OutputException {
        // Can't just call checkOutputDescriptor.
        // Could remove this loop if pub ods stored in map
        for(OutputDescriptor d : getPublishedOutputDescriptors()) {
            if(d.getName().equals(name)) {
                return outputs.get(name);   // Returns null if registered output not actually set.
            }
        }
        throw new UnrecognizedOutputException("Published output name '" + name + "' is not recognized.");
    }
    @Override
    public synchronized boolean hasPublishedOutput(String name) {
        return hasOutput(name);
    }


    //////////
    // MISC //
    //////////

    // For subclasses to call
    protected synchronized void setOutput(String name, Object output) throws UnrecognizedOutputException {
        checkOutputDescriptor(name);
        outputs.put(name, output);
        fireOutputChangeNotifier(new OutputChangeEvent(this, name));
    }

    // Private helper methods
    private void checkInputDescriptor(String name) throws UnrecognizedInputException {
        if(!inputDescriptors.containsKey(name)) {
            throw new UnrecognizedInputException("Input '" + name + "' is not recognized.");
        }
    }
    private void checkInputType(String name, Object input) throws InvalidInputTypeException {
        InputDescriptor id = inputDescriptors.get(name);
        if(input != null && !(id.getType().isAssignableFrom(input.getClass()))) {
            throw new InvalidInputTypeException("Input '" + name + "' is not of the correct type.");
        }
    }
    private void checkOutputDescriptor(String name) throws UnrecognizedOutputException {
        if(!outputDescriptors.containsKey(name)) {
            throw new UnrecognizedOutputException("Output '" + name + "' is not recognized.");
        }
    }
    private synchronized void checkOutput(String name) throws OutputUnsetException {
        if(!outputs.containsKey(name)) {
            throw new OutputUnsetException("Output '" + name + "' has not been set.");
        }
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return name;
    }


    //////////////////
    // PAUSE & STOP //
    //////////////////

    @Override
    public boolean canPause() {
        return ttContext.canPause();
    }
    @Override
    public boolean canStop() {
        return ttContext.canStop();
    }
    @Override
    public boolean canNotify() {
        return ttContext.canNotify();
    }
    @Override
    public boolean isPaused() {
        return ttContext.isPaused();
    }
    @Override
    public boolean isStopped() {
        return ttContext.isStopped();
    }
    @Override
    public boolean isPauseRequested() {
        return ttContext.isPauseRequested();
    }
    @Override
    public boolean isStopRequested() {
        return ttContext.isStopRequested();
    }
    @Override
    public void pause() {
        ttContext.pause();
    }
    @Override
    public void unpause() {
        ttContext.unpause();
    }
    @Override
    public void stopContext() {        // Named this instead of stop due to conflict with Thread.stop() in TransparentThreadContextThread
        ttContext.stopContext();
    }

    public void setCanPause(boolean canPause) {
        ttContext.setCanPause(canPause);
    }
    public void setCanStop(boolean canStop) {
        ttContext.setCanStop(canStop);
    }
    public void setCanNotify(boolean canNotify) {
        ttContext.setCanNotify(canNotify);
    }
    public void clearPauseRequested() {
        ttContext.clearPauseRequested();
    }
    public void clearStopRequested() {
        ttContext.clearStopRequested();
    }

    public void checkPause() {
        ttContext.checkPause();
    }
    public void checkStop() throws TransparentTaskStopException {
        ttContext.checkStop();
    }
    public void checkPauseAndStop() throws TransparentTaskStopException {
        ttContext.checkPauseAndStop();
    }
    @Override
    public void publishProgress(ProgressMessage pm) {
        ttContext.publishProgress(pm);
    }

    private transient ChangeListener pauseRequestedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            firePauseRequestedNotifier();
        }
    };
    private transient ChangeListener stopRequestedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireStopRequestedNotifier();
        }
    };
    private transient ChangeListener pauseListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            firePauseNotifier();
        }
    };
    private transient ChangeListener stopListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            fireStopNotifier();
        }
    };
    private transient ProgressListener progressListener = new ProgressListener() {
        public void stateChanged(ProgressEvent e) {
            fireProgressNotifier(e.getMessage());
        }
    };
}
