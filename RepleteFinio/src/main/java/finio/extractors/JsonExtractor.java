package finio.extractors;

import org.json.modified.JSONArray;
import org.json.modified.JSONObject;

import finio.core.NonTerminal;
import finio.core.impl.FList;

public class JsonExtractor extends NonTerminalExtractor /* implements NonTerminalPopulator POSSIBLY */ {


    ////////////
    // FIELDS //
    ////////////

    private Object J;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JsonExtractor(Object J) {
        if(J instanceof String) {
            String text = (String) J;
            if(text.trim().startsWith("[")) {
                J = new JSONArray(text, true);
            } else {
                J = new JSONObject(text, true);
            }
        }
        if(!(J instanceof JSONObject) && !(J instanceof JSONArray)) {
            throw new RuntimeException("Invalid JSON object.");
        }
        this.J = J;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        try {
            NonTerminal M = createBlankNonTerminal();
            if(J instanceof JSONObject) {
                populateJsonObject(M, (JSONObject) J);
            } else {
                populateJsonArray(M, (JSONArray) J);
            }
            return M;
        } catch(Exception e) {
            throw new RuntimeException("JSON Extraction Failed", e);
        }
    }

    private void populateJsonObject(NonTerminal M, JSONObject J) {
        for(String JK : JSONObject.getNames(J)) {
            Object JV = J.get(JK);
            Object V;
            if(JV instanceof JSONObject) {
                V = createBlankNonTerminal();
                populateJsonObject((NonTerminal) V, (JSONObject) JV);
            } else if(JV instanceof JSONArray) {
                V = createBlankNonTerminal();
                populateJsonArray((NonTerminal) V, (JSONArray) JV);
            } else {
                V = JV;
            }
            M.put(JK, V);
        }
    }

    private void populateJsonArray(NonTerminal M, JSONArray J) {
        for(int i = 0; i < J.length(); i++) {
            Object JV = J.get(i);
            Object V;
            if(JV instanceof JSONObject) {
                V = createBlankNonTerminal();
                populateJsonObject((NonTerminal) V, (JSONObject) JV);
            } else if(JV instanceof JSONArray) {
                V = new FList();
                populateJsonArray((NonTerminal) V, (JSONArray) JV);
            } else {
                V = JV;
            }
            M.put(i + "", V);  // TODO: (+"") This is kinda a hack right now, cuz we don't handle non-string keys well at the moment.
        }
    }

    @Override
    protected String getName() {
        return "JSON Extractor";
    }
}
