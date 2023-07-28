package replete.scripting.ui;

import javax.swing.DefaultComboBoxModel;

import replete.plugins.PluginManager;
import replete.plugins.UiGeneratorUtil;
import replete.plugins.ui.GeneratorWrapper;
import replete.scripting.DynamicScriptDefinition;
import replete.scripting.ScriptingManagerGenerator;
import replete.scripting.plugin.RepleteScriptingPlugin;
import replete.ui.BeanPanel;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.IconableRenderer;
import replete.ui.text.editor.REditor;

public class ScriptDefinitionPanel extends BeanPanel<DynamicScriptDefinition> {


    ////////////
    // FIELDS //
    ////////////

    private RComboBox<GeneratorWrapper> cboGenerators;
    private REditor txtEditor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ScriptDefinitionPanel() {
        DefaultComboBoxModel<GeneratorWrapper<ScriptingManagerGenerator>> mdlGenerators =
            UiGeneratorUtil.createExtensionComboModel(ScriptingManagerGenerator.class);

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb("Language:"),
                cboGenerators = Lay.cb(mdlGenerators, new IconableRenderer(), "visrows=20"),
                Lay.lb("Configure...", "bordered,cursor=hand", CommonConcepts.CONFIGURATION)
            ),
            "C", Lay.BL(
                "N", Lay.BL(
                    "W", Lay.FL("L", Lay.lb("Script Text:"), "nogap,eb=5b"),
                    "E", Lay.FL(Lay.lb("Valid", CommonConcepts.ACCEPT))
                ),
                "C", txtEditor = Lay.ed("code", "ruler"),
                "eb=5rlb"
            )
        );
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    @Override
    public DynamicScriptDefinition get() {
        return new DynamicScriptDefinition()
            .setLanguagePluginId("test")
            .setScriptText(txtEditor.getText())
        ;
    }

    // Mutators

    @Override
    public void set(DynamicScriptDefinition bean) {
        super.set(bean);

        //bean.getLanguagePluginId();
        txtEditor.setText(bean.getScriptText());
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PluginManager.initialize(new RepleteScriptingPlugin());

        Lay.BLtg(Lay.fr(),
            "C", new ScriptDefinitionPanel(),
            "size=700,center,visible=true"
        );
    }
}
