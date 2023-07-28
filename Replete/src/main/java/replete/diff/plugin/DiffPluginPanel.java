package replete.diff.plugin;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;

import replete.diff.DiffResult;
import replete.diff.DiffTreePanel;
import replete.diff.Differ;
import replete.diff.DifferGenerator;
import replete.diff.DifferParams;
import replete.diff.DifferParamsPanel;
import replete.diff.generic.GenericObjectDifferGenerator;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.plugins.UiGeneratorUtil;
import replete.plugins.ui.GeneratorWrapper;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.IconableRenderer;
import replete.ui.panels.RPanel;
import replete.ui.windows.Dialogs;

public class DiffPluginPanel extends RPanel {


    ////////////
    // FIELDS //
    ////////////

    private Object o1;
    private Object o2;

    private Map<DifferGenerator, DifferParams> allParams = new HashMap<>();

    private DiffTreePanel pnlDiff;
    private RComboBox<GeneratorWrapper<DifferGenerator>> cboDiffers;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DiffPluginPanel(Object o1, Object o2) {

        this.o1 = o1;
        this.o2 = o2;

        DefaultComboBoxModel<GeneratorWrapper<DifferGenerator>> mdlGenerators =
            UiGeneratorUtil.createExtensionComboModel(
                DifferGenerator.class,
                g -> g.canDiff(o1.getClass()) && g.canDiff(o2.getClass()),
                null,
                GenericObjectDifferGenerator.class
            );

        pnlDiff = new DiffTreePanel();
        cboDiffers = Lay.cb(mdlGenerators, new IconableRenderer());
        cboDiffers.setSelectedIndex(0);

        for(ExtensionPoint extensionPoint : PluginManager.getExtensionsForPoint(DifferGenerator.class)) {
            DifferGenerator differGenerator = (DifferGenerator) extensionPoint;
            if(differGenerator.canDiff(o1.getClass()) && differGenerator.canDiff(o2.getClass())) {
                DifferParamsPanel pnlDifferParams = differGenerator.createParamsPanel();
                allParams.put(differGenerator, pnlDifferParams.get());
            }
        }

        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb("Differ:"),
                cboDiffers,
                Lay.btn(CommonConcepts.EDIT, "icononly", (ActionListener) e -> showEditDifferParamsDialog(), "dim=[25,25]"),
                "vgap=0,eb=5t"
            ),
            "C", pnlDiff,
            "size=800,center,visible"
        );

        cboDiffers.addItemListener(e -> refreshDiff());

        refreshDiff();
    }

    private void showEditDifferParamsDialog() {
        DifferGenerator currentDifferGenerator = cboDiffers.getSelected().getGenerator();
        DifferParams currentParams = allParams.get(currentDifferGenerator);
        EditDifferParamsDialog dlg = new EditDifferParamsDialog(getWindow(), currentParams);
        dlg.setVisible(true);
        if(dlg.getResult() == EditDifferParamsDialog.OK) {
            DifferParams newParams = dlg.getParams();
            allParams.put(currentDifferGenerator, newParams);
            refreshDiff();
        }
    }


    /////////////
    // HELPERS //
    /////////////

    private void refreshDiff() {
        DifferGenerator currentDifferGenerator = cboDiffers.getSelected().getGenerator();
        DifferParams currentParams = allParams.get(currentDifferGenerator);
        Differ differ = currentDifferGenerator.createDiffer(currentParams);
        try {
            DiffResult result = differ.diff(o1, o2);
            pnlDiff.setCurrentResult(result, "Head", "Left Object", "Right Object");
        } catch(Exception e) {
            Dialogs.showDetails(getWindow(), e, "An error has occurred diffing these objects.", "Error");
        }
    }
}
