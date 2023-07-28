package replete.scripting;

public class DynamicScriptDefinition {


    ////////////
    // FIELDS //
    ////////////

    private String languagePluginId;  // One day there could be "params" for the scripting engine chosen
    //language params
    private String scriptText;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getLanguagePluginId() {
        return languagePluginId;
    }
    public String getScriptText() {
        return scriptText;
    }

    // Mutators

    public DynamicScriptDefinition setLanguagePluginId(String languagePluginId) {
        this.languagePluginId = languagePluginId;
        return this;
    }
    public DynamicScriptDefinition setScriptText(String scriptText) {
        this.scriptText = scriptText;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((languagePluginId == null) ? 0 : languagePluginId.hashCode());
        result = prime * result + ((scriptText == null) ? 0 : scriptText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(getClass() != obj.getClass()) {
            return false;
        }
        DynamicScriptDefinition other = (DynamicScriptDefinition) obj;
        if(languagePluginId == null) {
            if(other.languagePluginId != null) {
                return false;
            }
        } else if(!languagePluginId.equals(other.languagePluginId)) {
            return false;
        }
        if(scriptText == null) {
            if(other.scriptText != null) {
                return false;
            }
        } else if(!scriptText.equals(other.scriptText)) {
            return false;
        }
        return true;
    }
}
