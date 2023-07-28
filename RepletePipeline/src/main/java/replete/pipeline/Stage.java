package replete.pipeline;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;

import replete.event.ProgressListener;
import replete.pipeline.desc.InputDescriptor;
import replete.pipeline.desc.OutputDescriptor;
import replete.pipeline.errors.InputException;
import replete.pipeline.errors.OutputDescriptorException;
import replete.pipeline.errors.OutputException;
import replete.pipeline.errors.UnrecognizedInputDescriptorException;
import replete.pipeline.errors.UnrecognizedOutputDescriptorException;
import replete.pipeline.events.InputChangeListener;
import replete.pipeline.events.OutputChangeListener;
import replete.pipeline.events.ParameterChangeListener;
import replete.progress.ProgressMessage;
import replete.ttc.TransparentTaskContext;
import replete.ttc.TransparentTaskStopException;

public interface Stage extends Serializable, Runnable, TransparentTaskContext {


    ////////////
    // FIELDS //
    ////////////

    public static final String DEFAULT_STAGE_NAME = "Default Stage";


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    String getId();          // This stage's unique identifier
    String getName();        // This stage's friendly, readable name
    Stage getParent();       // This stage's parent as it pertains to a containment hierarchy
    boolean isDirty();       // Whether or not the stage's inputs or parameters have changed since execution
    boolean isExecuted();    // Whether or not stage has executed at least once
    boolean hasError();      // Whether or not previous execution had an error [OPTION] not have this
    Exception getError();    // The previous execution's error, if it had one [OPTION] not have this
    boolean hasWarning();    // Whether the previous execution has at least one warning
    List<StageWarning> getWarnings();   // The previous execution's warnings
    ExecuteSummary getExecuteSummary(); // The previous execution's summary

    // Mutator

    void setParent(Stage parent);   // [OPTION] Could also have hierarchy listeners


    /////////////
    // EXECUTE //
    /////////////

    void execute();


    /////////////////////
    // I/O DESCRIPTORS //
    /////////////////////

    List<InputDescriptor> getInputDescriptors();     // These return lists but could
    List<OutputDescriptor> getOutputDescriptors();   // potentially return maps instead

    InputDescriptor getInputDescriptor(String name) throws UnrecognizedInputDescriptorException;
    OutputDescriptor getOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException;

    List<OutputDescriptor> getPublishedOutputDescriptors();
    OutputDescriptor getPublishedOutputDescriptor(String name) throws UnrecognizedOutputDescriptorException;
    void addPublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException;
    void removePublishedOutputDescriptor(OutputDescriptor descriptor) throws OutputDescriptorException;

    // Description of various get*OutputDescriptor methods:
    //  - getOutputDescriptors describes the computed outputs available
    //    for another stage or actor to consume.
    //    * For an atomic stage these are simply all of the outputs it
    //      says it provides.
    //    * For a pipeline these are all of the pipeline's stages'
    //      "published" outputs.  This is because it is NOT allowed to
    //      simply dive into ALL (including UNpublished) output
    //      descriptors.  That is a violation of stage privacy! :)
    //  - getPublishedOutputDescriptors describes EXPLICITLY set outputs
    //    that the developer or user has said are the "official" outputs
    //    of this stage.  Other UNpublished outputs are considered to be
    //    "private" to the pipeline and should not generally be referred
    //    to from outside the pipeline.
    //    * For an atomic stage, all outputs are considered published
    //      outputs.  You cannot modify an atomic stage's list of
    //      published outputs.
    //    * For a pipeline, its published stages is initially an empty
    //      list.  These must be explicitly set by a developer.  A
    //      published output is similar to the "return" values of
    //      method/function.  If a pipeline has no published outputs
    //      it's similar to a "void" method.  These are the outputs
    //      you are designating to be the "final products" of the
    //      pipeline.
    //  - getAllOutputDescriptors ONLY exists in the pipeline class.
    //      This is an advanced administration method used to bypass
    //      the "published" characteristic of descriptors.  This will
    //      return all outputs at all levels of a pipeline-of-pipelines
    //      hierarchy.  This violates a pipeline stage's privacy! :)


    ////////////////
    // I/O VALUES //
    ////////////////

    // Inputs

    Map<String, Object> getInputs();
    Map<String, List<Object>> getInputsMulti();
    Object getInput(String name) throws InputException;
    List<Object> getInputMulti(String name) throws InputException;
    boolean hasInput(String name);

    void setInputs(Map<String, Object> inputs, boolean clearPrevious) throws InputException;
    void setInputsMulti(Map<String, List<Object>> inputs, boolean clearPrevious) throws InputException;
    void setInput(String name, Object input) throws InputException;
    void setInputMulti(String name, List<Object> inputs) throws InputException;
    void addInputMulti(String name, Object input) throws InputException;
    void removeInputMulti(String name, int index) throws InputException;
    void removeInput(String name) throws InputException;
    void clearInputs();

    // Outputs

    Map<String, Object> getOutputs();
    Object getOutput(String name) throws OutputException;
    boolean hasOutput(String name);
    Map<String, Object> getPublishedOutputs();
    Object getPublishedOutput(String name) throws OutputException;
    boolean hasPublishedOutput(String name);


    ///////////////
    // NOTIFIERS //
    ///////////////

    void addStartListener(ChangeListener listener);
    void removeStartListener(ChangeListener listener);

    void addCompleteListener(ChangeListener listener);
    void removeCompleteListener(ChangeListener listener);

    void addDirtyListener(ChangeListener listener);
    void removeDirtyListener(ChangeListener listener);

    void addInputChangeListener(InputChangeListener listener);
    void removeInputChangeListener(InputChangeListener listener);

    void addOutputChangeListener(OutputChangeListener listener);
    void removeOutputChangeListener(OutputChangeListener listener);

    // [OPTION] Add a addParameterChangeListener(String parameterName, ParameterChangeListener listener)
    void addParameterChangeListener(ParameterChangeListener listener);
    void removeParameterChangeListener(ParameterChangeListener listener);


    ////////////////////////////////
    // TRANSPARENT THREAD CONTEXT //
    ////////////////////////////////

    // (Copying these here for clarity)

    // Accessors
    boolean canPause();
    boolean canStop();
    boolean canNotify();
    boolean isPaused();
    boolean isStopped();
    boolean isPauseRequested();
    boolean isStopRequested();

    // Mutators
    void setCanPause(boolean canPause);
    void setCanStop(boolean canStop);
    void setCanNotify(boolean canNotify);

    // Actions
    void pause();
    void unpause();
    void stopContext();    // Named this instead of stop due to conflict with Thread.stop() in TransparentThreadContextThread

    // Client
    void checkPause();
    void checkStop() throws TransparentTaskStopException;
    void checkPauseAndStop() throws TransparentTaskStopException;
    void publishProgress(ProgressMessage pm);

    // Notifications
    void addPauseListener(ChangeListener listener);
    void removePauseListener(ChangeListener listener);
    void addStopListener(ChangeListener listener);
    void removeStopListener(ChangeListener listener);
    void addProgressListener(ProgressListener listener);     // Need generic, source-less PL to unify API's
    void removeProgressListener(ProgressListener listener);
}
