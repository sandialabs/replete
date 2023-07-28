package replete.diff.generic;

import java.util.ArrayList;
import java.util.List;

import replete.diff.DifferParams;

public class GenericObjectDifferParams extends DifferParams {


    ////////////
    // FIELDS //
    ////////////

    private boolean useFunctionBlacklist;
    private List<String> fieldBlacklist = new ArrayList<>();
    private boolean useFunctionWhitelist;
    private List<String> fieldWhitelist = new ArrayList<>();


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public boolean isUseFunctionBlacklist() {
        return useFunctionBlacklist;
    }
    public List<String> getFieldBlacklist() {
        return fieldBlacklist;
    }
    public boolean isUseFunctionWhitelist() {
        return useFunctionWhitelist;
    }
    public List<String> getFieldWhitelist() {
        return fieldWhitelist;
    }

    // Mutators

    public GenericObjectDifferParams setUseFunctionBlacklist(boolean useFuncionBlacklist) {
        useFunctionBlacklist = useFuncionBlacklist;
        return this;
    }
    public GenericObjectDifferParams setUseFunctionWhitelist(boolean useFuncionWhitelist) {
        useFunctionWhitelist = useFuncionWhitelist;
        return this;
    }

    public GenericObjectDifferParams addFieldToWhitelist(String field) {
        if(!fieldWhitelist.contains(field)) {
            fieldWhitelist.add(field);
        }
        return this;
    }
    public GenericObjectDifferParams addFieldToBlacklist(String field) {
        if(!fieldBlacklist.contains(field)) {
            fieldBlacklist.add(field);
        }
        return this;
    }
    public GenericObjectDifferParams removeFieldFromWhitelist(String field) {
        if(fieldWhitelist.contains(field)) {
            fieldWhitelist.remove(field);
        }
        return this;
    }
    public GenericObjectDifferParams removeFieldFromBlacklist(String field) {
        if(fieldBlacklist.contains(field)) {
            fieldBlacklist.remove(field);
        }
        return this;
    }


    //////////
    // MISC //
    //////////

    public boolean isFieldWhitelist(String field) {
        return fieldWhitelist.contains(field);
    }
    public boolean isFieldBlacklist(String field) {
        return fieldBlacklist.contains(field);
    }

    public boolean isFieldAllowed(String field) {
        if(useFunctionWhitelist && useFunctionBlacklist) {
            return fieldWhitelist.contains(field) && !fieldBlacklist.contains(field);
        }
        if(useFunctionWhitelist) {
            return fieldWhitelist.contains(field);
        }
        if(useFunctionBlacklist) {
            return !fieldBlacklist.contains(field);
        }
        return true;
    }
}