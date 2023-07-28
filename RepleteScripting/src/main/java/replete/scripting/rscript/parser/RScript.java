package replete.scripting.rscript.parser;

import java.util.HashMap;
import java.util.Map;

import replete.scripting.rscript.parser.gen.ASTStart;
import replete.scripting.rscript.parser.gen.ASTStatement;

public class RScript {


    ////////////
    // FIELDS //
    ////////////

    private String source;
    private ASTStart start;
    private ASTStatement[] statements;
    private Map<String, Object> metadata = new HashMap<>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RScript(String source, ASTStart start) {
        this.source = source;
        this.start = start;

        statements = new ASTStatement[start.jjtGetNumChildren()];
        for(int c = 0; c < start.jjtGetNumChildren(); c++) {
            statements[c] = (ASTStatement) start.jjtGetChild(c);
        }
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    // Accessors

    public String getSource() {
        return source;
    }
    public ASTStart getStart() {
        return start;
    }
    public ASTStatement[] getStatements() {
        return statements;
    }
    public ASTStatement getFirstStatement() {
        return statements[0];
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    // Mutators

    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    public void copyMetadata(RScript peq) {
        for(String key : peq.getMetadata().keySet()) {
            setMetadata(key, peq.getMetadata(key));
        }
    }
}
