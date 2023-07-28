package replete.ui.help;

import java.awt.Window;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.plugins.UiGenerator;
import replete.plugins.ui.GeneratorWrapper;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.IconableRenderer;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;

public class AddRootHelpSectionDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int ACCEPT = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private RComboBox<GeneratorWrapper> cboGenerators;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AddRootHelpSectionDialog(Window parent) {
        super(parent, "Add New Help Section", true);
        setIcon(HelpImageModel.TOC_PAGE_ROOT);

        DefaultComboBoxModel<GeneratorWrapper<HelpProvider>> mdlGenerators =
            new DefaultComboBoxModel<>();
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(HelpProvider.class);
        for(ExtensionPoint ext : exts) {
            HelpProvider provider = (HelpProvider) ext;
            mdlGenerators.addElement(new GeneratorWrapper<>(provider));
        }

        JButton btnHelp, btnSelect;
        Lay.BLtg(this,
            "C", Lay.FL("L",
                Lay.lb("Help Providers:"),
                cboGenerators = Lay.cb(mdlGenerators, new IconableRenderer()),
                btnHelp = Lay.btn(CommonConcepts.HELP, "icon,focusable=false,ttt=Help")
            ),
            "S", Lay.FL("R",
                btnSelect = Lay.btn("&Select", CommonConcepts.ACCEPT),
                Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer")
            ),
            "size=[400,120],center"
        );

        btnHelp.addActionListener(e -> {
            GeneratorWrapper wrapper = cboGenerators.getSelected();
            UiGenerator generator = wrapper.getGenerator();
            Dialogs.showMessage(this, generator.getName() + "\n\n"+ generator.getDescription(), "Help Provider");
        });

        setDefaultButton(btnSelect);

        btnSelect.addActionListener(e -> {
            result = ACCEPT;
            close();
        });
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }

    public HelpProvider getHelpProvider() {
        GeneratorWrapper wrapper = cboGenerators.getSelected();
        UiGenerator generator = wrapper.getGenerator();
        return (HelpProvider) generator;
    }
}
