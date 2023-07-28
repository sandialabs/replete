package replete.scripting;

import java.util.List;

import replete.plugins.ExtensionPoint;
import replete.plugins.UiGenerator;

public abstract class ScriptingManagerGenerator//<P extends AnalyzerParams>
        extends UiGenerator /*ParamsAndPanelUiGenerator<P>*/ implements ExtensionPoint {


    //////////////
    // ABSTRACT //
    //////////////

    public abstract void validate(String scriptText);  // can make a joint exception if needed
    public abstract Object parse(String scriptText);   // can make a wrapper return object
    public abstract ScriptExecutionResult execute(Object script, List<Object> layers);


    ////////////////
    // OVERRIDDEN //
    ////////////////

    // Generics Nuance: Since this class has placed further restrictions on
    // the generic parameter, these overrides propagate that change to these
    // methods' return type, eliminating need for some casts in client code.
//    @Override
//    public abstract P createParams();
//    @Override
//    public abstract PropertyParamsPanel<P> createParamsPanel(Object... args);
}
