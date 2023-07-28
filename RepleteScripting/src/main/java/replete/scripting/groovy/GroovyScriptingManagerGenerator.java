package replete.scripting.groovy;

import java.util.List;

import javax.swing.ImageIcon;

import replete.errors.RuntimeConvertedException;
import replete.scripting.ScriptExecutionResult;
import replete.scripting.ScriptingManagerGenerator;
import replete.scripting.groovy.images.GroovyImageModel;
import replete.scripting.rscript.evaluation.EvaluationEnvironment;
import replete.scripting.rscript.evaluation.EvaluationResult;
import replete.scripting.rscript.evaluation.RScriptEvaluator;
import replete.scripting.rscript.parser.RScript;
import replete.scripting.rscript.parser.RScriptParser;
import replete.scripting.rscript.parser.gen.ParseException;
import replete.ui.images.concepts.ImageLib;

public class GroovyScriptingManagerGenerator extends ScriptingManagerGenerator {


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String getName() {
        return "Groovy";
    }

    @Override
    public String getDescription() {
        return "This xxx";
    }

    @Override
    public ImageIcon getIcon() {
        return ImageLib.get(GroovyImageModel.GROOVY_LOGO);
    }

    @Override
    public Class<?>[] getCoordinatedClasses() {
        return new Class[] {
        };
    }

    @Override
    public void validate(String scriptText) {
        RScriptParser parser = new RScriptParser();
        try {
            parser.parse(scriptText);
        } catch(ParseException e) {
            throw new RuntimeConvertedException(e);
        }
    }

    @Override
    public Object parse(String scriptText) {
        RScriptParser parser = new RScriptParser();
        try {
            return parser.parse(scriptText);
        } catch(ParseException e) {
            throw new RuntimeConvertedException(e);
        }
    }

    @Override
    public ScriptExecutionResult execute(Object script, List<Object> layers) {
        RScriptEvaluator evaluator = new RScriptEvaluator();
        if(layers != null) {
            for(Object layer : layers) {
                evaluator.setInitialEnvironment(
                    new EvaluationEnvironment()
                        .addLayer(layer)
                );
            }
        }
        EvaluationResult eResult = evaluator.evaluate((RScript) script);
        ScriptExecutionResult result = new ScriptExecutionResult();
        result.setResultValues(eResult);
        result.setRootValue(eResult.getRootValue());
        return result;
    }
}
