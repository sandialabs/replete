package replete.pipeline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import replete.collections.Pair;
import replete.event.ExtChangeNotifier;
import replete.event.ProgressEvent;
import replete.event.ProgressListener;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.InputException;
import replete.pipeline.errors.InvalidNameException;
import replete.pipeline.errors.OutputDescriptorException;
import replete.pipeline.errors.OutputException;
import replete.pipeline.errors.PipelineExecutionException;
import replete.pipeline.errors.PipelineValidationException;
import replete.pipeline.errors.StageNotExecutedException;
import replete.pipeline.errors.UnrecognizedInputDescriptorException;
import replete.pipeline.errors.UnrecognizedInputException;
import replete.pipeline.errors.UnrecognizedOutputDescriptorException;
import replete.pipeline.errors.UnrecognizedOutputException;
import replete.pipeline.errors.graph.InvalidLinkException;
import replete.pipeline.events.LinkEvent;
import replete.pipeline.events.LinkListener;
import replete.pipeline.events.OutputChangeEvent;
import replete.pipeline.events.OutputChangeListener;
import replete.pipeline.events.StageContainerListener;
import replete.pipeline.events.StageEvent;
import replete.pipeline.graph.InputDescriptorNodeStatus;
import replete.pipeline.graph.StageGraph;
import replete.progress.FractionProgressMessage;
import replete.progress.ProgressMessage;
import replete.text.StringUtil;
import replete.ttc.TransparentTaskStopException;

public class Pipeline extends AbstractStage implements StageContainer {


    ////////////
    // FIELDS //
    ////////////

    // Constants

    public static final String PIPELINE_NAME_SEPARATOR = "/";

    // Core

    private StageGraph graph = new StageGraph();
    private List<OutputDescriptor> publishedOutputDescriptors = new ArrayList<>();
    private PipelineExecuteParams params;
    private boolean aggregateProgressMode = false;
    private Map<Stage, Exception> lastErrors = new ConcurrentHashMap<>();


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    // Having multiple constructors here enables a parameter
    // object to be set at time of pipeline construction
    // so that the execute method can be called with arbitrary
    // parameters if the pipeline is used as a Java Runnable.
    // This is why the execute method checks for this first.
    public Pipeline() {
        this(null, null);
    }
    public Pipeline(String name) {
        this(name, null);
    }
    public Pipeline(PipelineExecuteParams params) {
        this(null, params);
    }
    public Pipeline(String name, PipelineExecuteParams params) {
        super(name);
        this.params = params;
        graph.addLinkAddedListener(new LinkListener() {
            @Override
            public void stateChanged(LinkEvent e) {
                fireLinkAddedNotifier(e);
            }
        });
        graph.addLinkRemovedListener(new LinkListener() {
            @Override
            public void stateChanged(LinkEvent e) {
                fireLinkRemovedNotifier(e);
            }
        });
    }


    ////////////
    // STAGES //
    ////////////

    @Override
    public int getStageCount() {
        return graph.getStageCount();
    }
    @Override
    public List<Stage> getStages() {
        return graph.getStages();
    }
    @Override
    public boolean containsStage(Stage stage) {
        return graph.hasStage(stage);
    }
    @Override
    public void addStage(Stage stage) throws PipelineValidationException {
        validateStageAddition(stage);
        stage.addOutputChangeListener(new OutputChangeListener() {
            @Override
            public void stateChanged(OutputChangeEvent e) {
                // TODO: need to copy over a lot more here actually...
                for(InputDescriptor input : graph.getInputLinks(e.getSource().getOutputDescriptor(e.getName()))) {
                    input.getParent().setInput(input.getName(), e.getSource().getOutput(e.getName()));
                }
            }
        });
        graph.addStage(stage);
        stage.setParent(this);
        stage.addProgressListener(allStagesProgressListener);
        fireStageAddedNotifier(new StageEvent(stage));
    }
    @Override
    public void removeStage(Stage stage) {
        if(graph.removeStage(stage)) {
            stage.setParent(null);
            stage.removeProgressListener(allStagesProgressListener);
            fireStageRemovedNotifier(new StageEvent(stage));
        }
    }
    private void validateStageAddition(Stage stage) throws PipelineValidationException {
        if(containsStage(stage)) {
            throw new PipelineValidationException("Stage '" + stage.getId() + "' already exists in this pipeline.");
        }
        if(stage.getParent() != null) {
            throw new PipelineValidationException("Parent of stage is already assigned.");
        }
        for(InputDescriptor id : stage.getInputDescriptors()) {
            if(id.getName().contains(PIPELINE_NAME_SEPARATOR)) {
                throw new PipelineValidationException("Input descriptor name '" + id.getName() + "' contains invalid characters.");
            }
            if(id.getParent() != stage) {
                throw new PipelineValidationException("Input descriptor '" + id.getName() + "' has an invalid parent.");
            }
        }
        for(OutputDescriptor od : stage.getOutputDescriptors()) {
            if(od.getName().contains(PIPELINE_NAME_SEPARATOR)) {
                throw new PipelineValidationException("Output descriptor name '" + od.getName() + "' contains invalid characters.");
            }
            if(od.getParent() != stage) {
                throw new PipelineValidationException("Output descriptor '" + od.getName() + "' has an invalid parent.");
            }
        }
    }


    ///////////
    // LINKS //
    ///////////

    public boolean hasLink(InputDescriptor in) {
        return graph.hasLink(in);
    }
    public boolean hasLink(OutputDescriptor out) {
        return graph.hasLink(out);
    }
    public boolean hasLink(OutputDescriptor out, InputDescriptor in) {
        return graph.hasLink(out, in);
    }

    public boolean canLink(OutputDescriptor out, InputDescriptor in) {
        return graph.canLink(out, in);
    }

    public void addLink(OutputDescriptor out, InputDescriptor in) throws InvalidLinkException {
        graph.addLink(out, in);
        if(out.getParent().isExecuted()) {
            in.getParent().setInput(in.getName(), out.getParent().getOutput(out.getName()));
        }
    }

    public void removeLink(OutputDescriptor out, InputDescriptor in) {
        graph.removeLink(out, in);
        removeInput(in.getParent(), in.getName());
    }


    /////////////
    // EXECUTE //
    /////////////

    // Public

    /**
     * Execute all dirty stages.
     */
    @Override
    public void execute() {
        if(params == null) {
            params = new PipelineExecuteParams(null, true, false);
        }
        super.execute();
    }

    /**
     * Execute all dirty stages. If force is true, executes all clean
     * stages as well.
     * @param force
     */
    public void execute(boolean force) {
        params = new PipelineExecuteParams(null, true, force);
        super.execute();
    }

    /**
     * Executes only the given stage, if it is currently marked as dirty.
     * Marks all immediate children as dirty upon completion of execution.
     * @param stageId
     */
    public void execute(String stageId) {
        params = new PipelineExecuteParams(stageId, false, false);
        super.execute();
    }

    /**
     * Executes the given stage, if it is currently marked as dirty.
     * If force is true, executes that current stages even if it is clean.
     * Marks all immediate children as dirty upon completion of execution.
     * @param stageId
     * @param force
     */
    public void execute(String stageId, boolean force) {
        params = new PipelineExecuteParams(stageId, false, force);
        super.execute();
    }

    /**
     * Executes the given stage if it's dirty, and all descendant stages
     * if they are dirty.
     * @param stageId
     */
    public void executeFrom(String stageId) {
        params = new PipelineExecuteParams(stageId, true, false);
        super.execute();
    }

    /**
     * Executes the given stage if it's dirty, and all descendant stages
     * if they are dirty.  If force is true, executes all clean stages as well.
     * @param stageId
     * @param force
     */
    public void executeFrom(String stageId, boolean force) {
        params = new PipelineExecuteParams(stageId, true, force);
        super.execute();
    }

    // Inner

    @Override
    protected void executeInner() {
        String stageId = params.getStageId();
        boolean from   = params.isFrom();
        boolean force  = params.isForce();

        lastErrors.clear();

        // Ensure any initial stage exists in the graph.
        if(stageId != null) {
            checkStage(stageId);
        }

        // If there are no stages in the pipeline, do nothing.
        if(getStageCount() == 0) {
            return;
        }

        // If there is no specific stage desired or we are
        // attempting to execute all stages from a specific
        // stage, then we potentially have a lot of stages
        // to execute for this pipeline.
        if(stageId == null || from) {

            // Get the maximum depths for each stage.
            Map<Stage, Integer> depths = getMaxDepths(stageId);
            int stageCount = depths.size();

            publishProgress(new FractionProgressMessage(
                "Processing Pipeline '" + getName() + "'", 0, stageCount));

            // Translate the depth map into a sorted map which
            // holds all the stages at each depth level.
            SortedMap<Integer, List<Stage>> executionLevels = createExecutionLevels(depths);

            // Ensure that the top-level sources of the level list
            checkTopLevelSourceParents(executionLevels);

            // Execute all of the levels by level.
            executeStagesByLevel(force, executionLevels, stageCount);

        // If there is a specific stage specified and we're NOT trying
        // to execute all descendant stages starting from that stage...
        } else /* if(stageId != null && !from) */ {
            Stage stage = graph.getStageById(stageId);

            publishProgress(new FractionProgressMessage(
                "Processing Pipeline " + getName() + ": " + stage.getName(), 0, 1));

            // If the stage is dirty, or we're forcing execution regardless
            // of dirty state, then execute the stage.
            if(force || stage.isDirty()) {
                try {
                    stage.execute();
                } catch(Exception e) {
                    // We are going to ignore errors, allowing the background
                    // threads to always end successfully.  The pipeline is
                    // expected to NEVER FAIL, but RECORD ALL ERRORS of its
                    // contained stages (done next).
                }
                if(stage.hasError()) {
                    lastErrors.put(stage, stage.getError());
                    lastError = stage.getError();
                }
                // TODO thread safety
                recordWarnings(stage);
                publishProgress(new FractionProgressMessage(
                    "Processing Pipeline " + getName() + ": " + stage.getName(), 1, 1));
            }
            // Otherwise, the stage's ExecuteSummary will not have
            // its counts incremented and will not have its error
            // status updated because it was not executed.
        }
    }

    // executeInner helper methods

    private Map<Stage, Integer> getMaxDepths(String stageId) {
        if(stageId != null) {
            return graph.getMaxDepths(stageId);
        }
        return graph.getMaxDepthsSources();
    }

    private SortedMap<Integer, List<Stage>> createExecutionLevels(Map<Stage, Integer> depths) {
        SortedMap<Integer, List<Stage>> executionLevels = new TreeMap<>();
        for(Entry<Stage, Integer> entry : depths.entrySet()) {
            List<Stage> level = executionLevels.get(entry.getValue());
            if(level == null) {
                level = new ArrayList<>();
                executionLevels.put(entry.getValue(), level);
            }
            level.add(entry.getKey());
        }
        return executionLevels;
    }

    private void checkTopLevelSourceParents(SortedMap<Integer, List<Stage>> executionLevels) {
        for(Stage stage : executionLevels.get(0)) {
            for(Stage parent : graph.getParents(stage)) {
                if(!parent.isExecuted()) {
                    throw new StageNotExecutedException("Cannot execute pipeline.  A parent of stage '" +
                        stage.getId() + "' has not been executed.");
                }
            }
        }
    }

    private void executeStagesByLevel(boolean force, SortedMap<Integer, List<Stage>> executionLevels, final int stageCount) {
        final AtomicInteger completed = new AtomicInteger(0);
        for(List<Stage> level : executionLevels.values()) {

            // Construct a new executor.  If this is bad for
            // performance reasons to do this for some reason
            // we can try to reuse the same executor, but for
            // level-ed execution we'd still need to clear
            // it of callables after each level.
            int cores = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor svc =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

            // Determine which of the stages should be submitted
            // to the executor for this level
            List<Future<Void>> futures = new ArrayList<>();
            for(final Stage stage : level) {

                // Add a stage if it is dirty or we are forcing execution
                // regardless of its dirty status.  TODO [DMT] Is void
                // the right thing to do here?  Maybe we can return
                // ExecuteSummary or something.
                if(force || stage.isDirty()) {
                    Future<Void> future = svc.submit(new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            try {
                                stage.execute();
                            } catch(Exception e) {
                                // We are going to ignore errors, allowing the background
                                // threads to always end successfully.  The pipeline is
                                // expected to NEVER FAIL, but RECORD ALL ERRORS of its
                                // contained stages (done next).
                            }
                            if(stage.hasError()) {
                                lastErrors.put(stage, stage.getError());
                                lastError = stage.getError();
                            }
                            recordWarnings(stage);
                            int current = completed.incrementAndGet();
                            publishProgress(new FractionProgressMessage("Processing Pipeline " + getName() + ": " + stage.getName(), current, stageCount));
                            return null;
                        }
                    });
                    futures.add(future);
                } else {
                    int current = completed.incrementAndGet();
                    publishProgress(new FractionProgressMessage("Processing Pipeline " + getName() + ": " + stage.getName(), current, stageCount));
                }
            }

            // Wait for all of the stage-callables for this level
            // to complete.
            for(Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
//                    e.printStackTrace();   // TODO [DMT] Not done - review
                } catch(ExecutionException e) {
                    // No background threads are allowed to fail
                    // due to the try catch in the Callable.
                }
            }

            svc.shutdown();
        }
    }

    private synchronized void recordWarnings(Stage stage) {
        for(StageWarning warning : stage.getWarnings()) {
            warnings.add(
                new StageWarning(
                    getName() + PIPELINE_NAME_SEPARATOR + warning.getParent(),
                    warning.getWhen(),
                    warning.getMessage(),
                    warning.getException()
                )
            );
        }
    }


    /////////////////////
    // I/O DESCRIPTORS //
    /////////////////////

    private void checkStage(String stageId) {
        if(graph.getStageById(stageId) == null) {
            throw new PipelineExecutionException("Unrecognized initial stage ID '" + stageId + "'.");
        }
    }

    // Could also augment the API to provide the matching input
    // descriptors of the stages with... matching input descriptors...
    @Override
    public List<InputDescriptor> getInputDescriptors() {
        List<InputDescriptor> result = new ArrayList<>();
        for(Stage stage : graph.getSourceStages()) {
            for(InputDescriptor input : stage.getInputDescriptors()) {
                if(graph.getStatus(input) == InputDescriptorNodeStatus.UNSATISFIED) {
                    InputDescriptor descriptor = new InputDescriptor(
                        this,
                        createInputOutputName(stage, input.getName()),
                        input.getFriendlyName(),
                        input.getDescription(),
                        input.getType(),
                        input.isNullAllowed(),
                        input.getCardinalityMinimum(),
                        input.getCardinalityMaximum()
                    );
                    result.add(descriptor);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    @Override
    public List<OutputDescriptor> getOutputDescriptors() {
        List<OutputDescriptor> result = new ArrayList<>();
        for(Stage stage : graph.getStages()) {
            for(OutputDescriptor output : stage.getPublishedOutputDescriptors()) {
                OutputDescriptor descriptor = new OutputDescriptor(
                    this,
                    createInputOutputName(stage, output.getName()),
                    output.getFriendlyName(),
                    output.getDescription(),
                    output.getType());
                result.add(descriptor);
            }
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public InputDescriptor getInputDescriptor(String name) throws UnrecognizedInputDescriptorException {
        Pair<String, String> pair;
        try {
            pair = parseInputOutputName(name);
        } catch (InvalidNameException e) {
            throw new UnrecognizedInputDescriptorException("Input descriptor '" + name + "' does not exist.", e);
        }
        Stage stage = graph.getStageById(pair.getValue1());
        if(stage == null) {
            throw new UnrecognizedInputDescriptorException("Input descriptor '" + name + "' does not exist.");
        }
        try {
            return stage.getInputDescriptor(pair.getValue2());
        } catch(UnrecognizedInputDescriptorException e) {
            throw new UnrecognizedInputDescriptorException("Input descriptor '" + name + "' does not exist.", e);
        }
    }
    @Override
    public OutputDescriptor getOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException {
        Pair<String, String> pair;
        try {
            pair = parseInputOutputName(name);
        } catch(InvalidNameException e) {
            throw new UnrecognizedOutputDescriptorException("Output descriptor '" + name + "' does not exist.", e);
        }
        Stage stage = graph.getStageById(pair.getValue1());
        if(stage == null) {
            throw new UnrecognizedOutputDescriptorException("Output descriptor '" + name + "' does not exist.");
        }
        try {
            return stage.getOutputDescriptor(pair.getValue2());
        } catch(UnrecognizedOutputDescriptorException e) {
            throw new UnrecognizedOutputDescriptorException("Output descriptor '" + name + "' does not exist.", e);
        }
    }

    @Override
    public List<OutputDescriptor> getPublishedOutputDescriptors() {
        return Collections.unmodifiableList(publishedOutputDescriptors);
    }
    @Override
    public OutputDescriptor getPublishedOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException {
        for(OutputDescriptor d : publishedOutputDescriptors) {    // Could remove this loop if pub ods stored in map
            if(d.getName().equals(name)) {
                return d;
            }
        }
        throw new UnrecognizedOutputDescriptorException("Published output descriptor '" + name + "' does not exist.");
    }
    @Override
    public void addPublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException {
        if(!getOutputDescriptors().contains(descriptor)) {
            throw new OutputDescriptorException("Cannot publish output descriptor '" + descriptor.getName() + "' as it is not contained within this stage.");
        }
        // Could also use a set.
        if(!publishedOutputDescriptors.contains(descriptor)) {
            publishedOutputDescriptors.add(descriptor);
        }
    }
    @Override
    public void removePublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException {
        if(!getOutputDescriptors().contains(descriptor)) {
            throw new OutputDescriptorException("Cannot unpublish output descriptor '" + descriptor.getName() + "' as it is not contained within this stage.");
        }
        publishedOutputDescriptors.remove(descriptor);
    }

    // Specific to Pipelines: ignore published characteristic of
    // descriptors and return every stage contained recursively
    // within this pipeline.
    public List<OutputDescriptor> getAllOutputDescriptors() {
        List<OutputDescriptor> result = new ArrayList<>();
        for(Stage stage : graph.getStages()) {
            List<OutputDescriptor> stageDescriptors;
            if(stage instanceof Pipeline) {
                stageDescriptors = ((Pipeline) stage).getAllOutputDescriptors();
            } else {
                stageDescriptors = stage.getOutputDescriptors();
            }
            for(OutputDescriptor output : stageDescriptors) {
                OutputDescriptor descriptor = new OutputDescriptor(
                    this,
                    createInputOutputName(stage, output.getName()),
                    output.getFriendlyName(),
                    output.getDescription(),
                    output.getType());
                result.add(descriptor);
            }
        }
        return Collections.unmodifiableList(result);
    }


    ////////////////
    // I/O VALUES //
    ////////////////

    // Inputs

    @Override
    public Map<String, Object> getInputs() {
        Map<String, Object> result = new LinkedHashMap<>();
        for(Stage source : graph.getSourceStages()) {
            for(Entry<String, Object> entry : source.getInputs().entrySet()) {
                String inputName = createInputOutputName(source, entry.getKey());
                result.put(inputName, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }
    @Override
    public Map<String, List<Object>> getInputsMulti() {
        Map<String, List<Object>> result = new LinkedHashMap<>();
        for(Stage source : graph.getSourceStages()) {
            for(Entry<String, List<Object>> entry : source.getInputsMulti().entrySet()) {
                String inputName = createInputOutputName(source, entry.getKey());
                result.put(inputName, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(result);
    }
    @Override
    public Object getInput(String name) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        return getInput(pair.getValue1(), pair.getValue2());
    }
    @Override
    public List<Object> getInputMulti(String name) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        return getInputMulti(pair.getValue1(), pair.getValue2());
    }
    @Override
    public boolean hasInput(String name) {
        try {
            Pair<Stage, String> pair = splitInput(name);
            return hasInput(pair.getValue1(), pair.getValue2());
        } catch(Exception e) {
            return false;
        }
    }
    @Override
    public void setInput(String name, Object input) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        setInput(pair.getValue1(), pair.getValue2(), input);
    }
    @Override
    public void setInputMulti(String name, List<Object> inputList) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        setInputMulti(pair.getValue1(), pair.getValue2(), inputList);
    }
    @Override
    public void addInputMulti(String name, Object input) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        addInputMulti(pair.getValue1(), pair.getValue2(), input);
    }
    @Override
    public void removeInputMulti(String name, int index) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        removeInputMulti(pair.getValue1(), pair.getValue2(), index);
    }
    @Override
    public void removeInput(String name) throws InputException {
        Pair<Stage, String> pair = splitInput(name);
        removeInput(pair.getValue1(), pair.getValue2());
    }
    @Override
    public void clearInputs() {
        for(Stage stage : graph.getStages()) {
            clearInputs(stage);
        }
    }
    // Pipeline-specific
    public Object getInput(Stage stage, String name) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        return stage.getInput(name);
    }
    public List<Object> getInputMulti(Stage stage, String name) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        return stage.getInputMulti(name);
    }
    public boolean hasInput(Stage stage, String name) {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        return stage.hasInput(name);
    }
    public void setInput(Stage stage, String name, Object input) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.setInput(name, input);
    }
    public void setInputMulti(Stage stage, String name, List<Object> inputList) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.setInputMulti(name, inputList);
    }
    public void addInputMulti(Stage stage, String name, Object input) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.addInputMulti(name, input);
    }
    public void removeInputMulti(Stage stage, String name, int index) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.removeInputMulti(name, index);
    }
    public void removeInput(Stage stage, String inName) throws InputException {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.removeInput(inName);
    }
    public void clearInputs(Stage stage) {
        if(!graph.hasStage(stage)) {
            // TODO
        }
        stage.clearInputs();
    }

    // Outputs

    @Override
    public Map<String, Object> getOutputs() {
        Map<String, Object> result = new LinkedHashMap<>();
        for(OutputDescriptor od : getOutputDescriptors()) {
            Stage stage = od.getParent();
            Object output = stage.getOutput(od.getName());
            String outputName = createInputOutputName(stage, od.getName());
            result.put(outputName, output);
        }
//        for(Stage stage : graph.getStages()) {
//            for(Entry<String, Object> entry : stage.getOutputs().entrySet()) {
//                String outputName = createInputOutputName(stage, entry.getKey());
//                result.put(outputName, entry.getValue());
//            }
//        }
        return Collections.unmodifiableMap(result);
    }
    @Override
    public Object getOutput(String name)  throws OutputException {
        Pair<Stage, String> pair = splitOutput(name);
        return getOutput(pair.getValue1(), pair.getValue2());
    }
    @Override
    public boolean hasOutput(String name) {
        Pair<Stage, String> pair = splitOutput(name);
        return hasOutput(pair.getValue1(), pair.getValue2());
    }
    // Pipeline-specific
    public Object getOutput(Stage stage, String name) throws OutputException {
        if(!graph.hasStage(stage)) {
            // TODO:
        }
        return stage.getOutput(name);
    }
    public boolean hasOutput(Stage stage, String name) throws OutputException {
        if(!graph.hasStage(stage)) {
            // TODO:
        }
        return stage.hasOutput(name);
    }
    @Override
    public Map<String, Object> getPublishedOutputs() {
        Map<String, Object> result = new LinkedHashMap<>();
        for(OutputDescriptor od : getPublishedOutputDescriptors()) {
            Stage stage = od.getParent();
            Object output = stage.getPublishedOutput(od.getName());
            String outputName = createInputOutputName(stage, od.getName());
            result.put(outputName, output);
        }
        return result;
    }
    @Override
    public Object getPublishedOutput(String name) throws OutputException {
        // TODO DMT Finish this?
        return null;
    }
    @Override
    public boolean hasPublishedOutput(String name) {
        // TODO
        return false;
    }

    // TODO: Could these two methods be combined someday?
    private Pair<Stage, String> splitInput(String name) {
        Pair<String, String> pair;
        try {
            pair = parseInputOutputName(name);
        } catch(InvalidNameException e) {
            throw new UnrecognizedInputException("Invalid input name.", e);
        }
        Stage stage = graph.getStageById(pair.getValue1());
        if(stage == null) {
            throw new UnrecognizedInputException("Invalid input name.  Stage '" + pair.getValue1() + "' is unrecognized.");
        }
        return new Pair<>(stage, pair.getValue2());
    }
    private Pair<Stage, String> splitOutput(String name) {
        Pair<String, String> pair;
        try {
            pair = parseInputOutputName(name);
        } catch(InvalidNameException e) {
            throw new UnrecognizedOutputException("Invalid output name.", e);
        }
        Stage stage = graph.getStageById(pair.getValue1());
        if(stage == null) {
            throw new UnrecognizedOutputException("Invalid output name.  Stage '" + pair.getValue1() + "' is unrecognized.");
        }
        return new Pair<>(stage, pair.getValue2());
    }


    //////////
    // MISC //
    //////////

    protected Pair<String, String> parseInputOutputName(String name) throws InvalidNameException {
        String[] strings = name.split(PIPELINE_NAME_SEPARATOR, 2);
        if(strings.length != 2) {
            throw new InvalidNameException("Input or output name '" + name + "' is invalid.");
        }
        return new Pair<>(strings[0], strings[1]);
    }
    protected String createInputOutputName(Stage stage, String descriptorName) {
        return stage.getId() + PIPELINE_NAME_SEPARATOR + descriptorName;
    }
    public void addDependency(Stage from, Stage to) {
        graph.addDependency(from, to);
    }

    /**
     * Returns true if any stage contained in this pipeline is marked as dirty.
     */
    @Override
    public boolean isDirty() {
        for(Stage stage : graph.getStages()) {
            if(stage.isDirty()) {
                return true;
            }
        }
        return false;
    }

    public Map<Stage, Exception> getErrors() {
        return lastErrors;
    }

    public boolean isAggregateProgressMode() {
        return aggregateProgressMode;
    }
    public void setAggregateProgressMode(boolean aggregateProgressMode) {
        this.aggregateProgressMode = aggregateProgressMode;
    }


    ///////////////
    // NOTIFIERS //
    ///////////////

    // Stage added

    private ExtChangeNotifier<StageContainerListener> stageAddedNotifier = new ExtChangeNotifier<>();
    @Override
    public void addStageAddedListener(StageContainerListener listener) {
        stageAddedNotifier.addListener(listener);
    }
    @Override
    public void removeStageAddedListener(StageContainerListener listener) {
        stageAddedNotifier.removeListener(listener);
    }
    protected void fireStageAddedNotifier(StageEvent e) {
        stageAddedNotifier.fireStateChanged(e);
    }

    // Stage removed

    private ExtChangeNotifier<StageContainerListener> stageRemovedNotifier = new ExtChangeNotifier<>();
    @Override
    public void addStageRemovedListener(StageContainerListener listener) {
        stageRemovedNotifier.addListener(listener);
    }
    @Override
    public void removeStageRemovedListener(StageContainerListener listener) {
        stageRemovedNotifier.removeListener(listener);
    }
    protected void fireStageRemovedNotifier(StageEvent e) {
        stageRemovedNotifier.fireStateChanged(e);
    }

    // Link added

    private ExtChangeNotifier<LinkListener> linkAddedNotifier = new ExtChangeNotifier<>();
    public void addLinkAddedListener(LinkListener listener) {
        linkAddedNotifier.addListener(listener);
    }
    public void removeLinkAddedListener(LinkListener listener) {
        linkAddedNotifier.removeListener(listener);
    }
    protected void fireLinkAddedNotifier(LinkEvent e) {
        linkAddedNotifier.fireStateChanged(e);
    }

    // Link removed

    private ExtChangeNotifier<LinkListener> linkRemovedNotifier = new ExtChangeNotifier<>();
    public void addLinkRemovedListener(LinkListener listener) {
        linkRemovedNotifier.addListener(listener);
    }
    public void removeLinkRemovedListener(LinkListener listener) {
        linkRemovedNotifier.removeListener(listener);
    }
    protected void fireLinkRemovedNotifier(LinkEvent e) {
        linkRemovedNotifier.fireStateChanged(e);
    }


    ////////////
    // RENDER //
    ////////////

    public void walkGraph() {
        graph.walkGraph();
    }
    public String render() {
        StringBuilder buffer = new StringBuilder();
        render(this, buffer, 0);
        return buffer.toString();
    }
    private void render(Pipeline pipeline, StringBuilder buffer, int level) {
        String sp = StringUtil.spaces(level * 4);
        String sp2 = StringUtil.spaces((level + 1) * 4);
        for(Stage stage : graph.getStages()) {
            if(stage instanceof Pipeline) {
                render(pipeline, buffer, level + 1);
            } else {
                buffer.append(sp + "Stage: " + stage.getName() + "\n");
                for(InputDescriptor id : stage.getInputDescriptors()) {
                    List<OutputDescriptor> ods = graph.getOutputLinks(id);
                    buffer.append(sp2 + "[I] " + id.getQualifiedName() + " => [");
                    for(OutputDescriptor od : ods) {
                        buffer.append(od.getQualifiedName() + ", ");
                    }
                    if(ods.size() != 0) {
                        buffer.delete(buffer.length() - 2, buffer.length());
                    }
                    buffer.append("]\n");
                }
                for(OutputDescriptor od : stage.getOutputDescriptors()) {
                    List<InputDescriptor> ids = graph.getInputLinks(od);
                    buffer.append(sp2 + "[O] " + od.getQualifiedName() + " => [");
                    for(InputDescriptor id : ids) {
                        buffer.append(id.getQualifiedName() + ", ");
                    }
                    if(ids.size() != 0) {
                        buffer.delete(buffer.length() - 2, buffer.length());
                    }
                    buffer.append("]\n");
                }
            }
        }
    }


    //////////////////
    // PAUSE & STOP //
    //////////////////

    // Request Flags (Thread Synchronization)
    protected boolean pauseRequested = false;
    protected boolean stopRequested = false;
    protected boolean canPause = true;       // Initially assumed pipeline can pause or stop
    protected boolean canStop = true;
    protected boolean canNotify = true;

    // A conscious choice: All Pipeline objects are pausable and stoppable,
    // because even if the current stage(s) are not, we can at least pause/stop
    // after that stage (those stages) end(s) and before subsequent stages
    // begin.
    @Override
    public boolean canPause() {
        return canPause;
    }
    @Override
    public boolean canStop() {
        return canStop;
    }
    @Override
    public boolean canNotify() {
        return canNotify;
    }
    @Override
    public boolean isPaused() {
        // TODO: Is this the proper behavior ??????  Need to consider only active stages?
        for(Stage stage : graph.getStages()) {
            if(!stage.isPaused()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isStopped() {
        // TODO: Is this the proper behavior ??????  Need to consider only active stages?
        for(Stage stage : graph.getStages()) {
            if(!stage.isStopped()) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean isPauseRequested() {
        return pauseRequested;
    }
    @Override
    public boolean isStopRequested() {
        return stopRequested;
    }
    @Override
    public void pause() {
        if(canPause) {
            pauseRequested = true;         // When/how does get set back to false?
            firePauseRequestedNotifier();

            // TODO: Is this the proper behavior ??????  Need to pause only active stages?
            for(Stage stage : graph.getStages()) {
                stage.pause();
            }
        }
    }
    @Override
    public void unpause() {
        //pauseRequested = true;         // When/how does get set back to false?
        // TODO: Is this the proper behavior ??????  Need to unpause only active stages?
        for(Stage stage : graph.getStages()) {
            stage.unpause();
        }
    }
    @Override
    public void stopContext() {
        stopRequested = true;         // When/how does get set back to false?
        fireStopRequestedNotifier();

        // TODO: Is this the proper behavior ??????  Need to stop only active stages?
        for(Stage stage : graph.getStages()) {
            stage.stopContext();
        }
    }
    @Override
    public void setCanPause(boolean canPause) {
        //?????
    }
    @Override
    public void setCanStop(boolean canStop) {
        //?????
    }
    @Override
    public void setCanNotify(boolean canNotify) {
        this.canNotify = canNotify;
    }
    @Override
    public void clearPauseRequested() {

    }
    @Override
    public void clearStopRequested() {
        //
    }

    // client stuff
    @Override
    public void checkPause() {
        // might not need to do anything
    }
    @Override
    public void checkStop() throws TransparentTaskStopException {
        // might not need to do anything
    }
    @Override
    public void checkPauseAndStop() throws TransparentTaskStopException {
        // might not need to do anything
    }
    @Override
    public void publishProgress(ProgressMessage pm) {
        fireProgressNotifier(pm, true);
    }

    // Firehose approach.  May one day have a summary approach for highly
    // parallel, multi-stage pipelines.
    private ProgressListener allStagesProgressListener = new ProgressListener() {
        @Override
        public void stateChanged(ProgressEvent e) {
            fireProgressNotifier(e.getMessage(), false);
        }
    };

    private void fireProgressNotifier(ProgressMessage m, boolean aggregate) {
        if(canNotify) {
//            System.out.println(m + " " + aggregate + " " + aggregateProgressMode);
//            if(aggregateProgressMode == aggregate) {
                fireProgressNotifier(m);
//            }
        }
    }
}
