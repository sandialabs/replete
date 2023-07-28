package replete.flux;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import replete.flux.streams.DataStream;
import replete.flux.streams.FluxDataStreamModel;
import replete.flux.viz.VisualizerGenerator;
import replete.flux.viz.VisualizerParams;
import replete.flux.viz.VisualizerParamsAddEditDialog;
import replete.flux.viz.VisualizerType;
import replete.plugins.ExtensionPoint;
import replete.plugins.Generator;
import replete.plugins.HumanDescriptor;
import replete.plugins.PluginManager;
import replete.plugins.SystemUserDescriptorEditDialog;
import replete.plugins.ui.GeneratorWrapper;
import replete.text.StringUtil;
import replete.ui.BeanPanel;
import replete.ui.combo.RComboBox;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.list.cb.RCheckBoxList;
import replete.ui.validation.ValidationContext;
import replete.ui.windows.Dialogs;

public class FluxPanelParamsPanel extends BeanPanel<FluxPanelParams> {


    ////////////
    // FIELDS //
    ////////////

    private RComboBox<GeneratorWrapper> cboAvailableVis;
    private DefaultListModel<VisualizerGlob> mdlVisualizations;
    private RCheckBoxList<VisualizerGlob> lstVisualizations;
    private DefaultListModel<DataStreamGlob> mdlDataStreams;
    private RCheckBoxList<DataStreamGlob> lstDataStreams;
    private RComboBox<VisualizerCombineMethod> cboCombineMethods;
    private JLabel lbl;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FluxPanelParamsPanel(FluxPanelContext context) {

        DefaultComboBoxModel<GeneratorWrapper<VisualizerGenerator>> mdlGenerators =
            new DefaultComboBoxModel<>();
        List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(VisualizerGenerator.class);
        for(ExtensionPoint ext : exts) {
            VisualizerGenerator gen = (VisualizerGenerator) ext;
            mdlGenerators.addElement(new GeneratorWrapper<>(gen));
        }

        JButton btnAdd, btnRemove, btnEdit, btnClone, btnDescribe, btnHelp, btnHelp2, btnMoveUp, btnMoveDown;
        Lay.BLtg(this,
            "C", Lay.GL(1, 2,
                Lay.BL(
                    "N", Lay.BL(
                        "W", Lay.lb("Visualizations", "italic"),
                        "E", Lay.FL(
                            cboAvailableVis = Lay.cb(mdlGenerators),
                            Lay.hs(5),
                            btnHelp = Lay.btn(CommonConcepts.INFO, 2, "ttt=Info"),
                            Lay.hs(5),
                            btnAdd = Lay.btn(CommonConcepts.ADD, 2, "ttt=Add..."),
                            "hgap=0"
                        )
                    ),
                    "C", Lay.BL(
                        "C", Lay.sp(
                            lstVisualizations = new RCheckBoxList(
                                mdlVisualizations = new DefaultListModel<>()
                            )
                        ),
                        "E", Lay.BxL(
                            btnRemove = Lay.btn(CommonConcepts.REMOVE, 2, "ttt=Remove"),
                            Lay.vs(5),
                            btnEdit = Lay.btn(CommonConcepts.EDIT, 2, "ttt=Configure..."),
                            Lay.vs(5),
                            btnClone = Lay.btn(CommonConcepts.CLONE, 2, "ttt=Clone..."),
                            Lay.vs(5),
                            btnDescribe = Lay.btn(CommonConcepts.TEXT_INPUT, 2, "ttt=Describe..."),
                            Lay.vs(5),
                            btnMoveUp = Lay.btn(CommonConcepts.MOVE_UP, 2, "ttt=Move-Up"),
                            Lay.vs(5),
                            btnMoveDown = Lay.btn(CommonConcepts.MOVE_DOWN, 2, "ttt=Move-Down"),
                            Box.createVerticalGlue(),
                            "eb=5l"
                        )
                    ),
                    "eb=10b7r"
                ),
                Lay.BL(
                    "N", Lay.BL(
                        "W", Lay.lb("Available Data Streams", "italic,prefh=36")
                    ),
                    "C", Lay.sp(
                        lstDataStreams = new RCheckBoxList(
                            mdlDataStreams = new DefaultListModel<>()
                        )
                    ),
                    "E", Lay.BxL(
                        btnHelp2 = Lay.btn(CommonConcepts.INFO, 2, "ttt=Info"),
                        Box.createVerticalGlue(),
                        "eb=5l"
                    ),
                    "eb=10b7l"
                )
            ),
            "S", Lay.FL("L",
                Lay.lb("Visualization Combine Method:"),
                Lay.hs(5),
                cboCombineMethods = Lay.cb(VisualizerCombineMethod.values()),
                Lay.hs(5),
                lbl = Lay.lb(),
                "nogap"
            ),
            "eb=10lrb5t"
        );
        lstDataStreams.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cboCombineMethods.addItemListener(e -> {
            lbl.setText(
                cboCombineMethods.getSelected() == VisualizerCombineMethod.INTEGRATED ?
                    "<html><i>(only if all visualizations are Java2D)</i></html>" : "");
        });
        lstDataStreams.addListCheckedListener(e -> {
            List<String> newDsIds = new ArrayList<>();
            for(DataStreamGlob glob : lstDataStreams.getCheckedValuesList()) {
                newDsIds.add(glob.id);
            }
            int[] indices = lstVisualizations.getSelectedIndices();
            for(int index : indices) {
                VisualizerGlob glob = mdlVisualizations.getElementAt(index);
                glob.compoundParams.setDataStreamIds(newDsIds);
            }
        });

        FluxDataStreamModel model = context.getDataStreamModel();
        for(String id : model.getDataStreams().keySet()) {
            DataStream stream = model.getDataStreams().get(id);
            DataStreamGlob glob = new DataStreamGlob(id, stream);
            mdlDataStreams.addElement(glob);
        }
        lstDataStreams.setItemsEnabled(false);        // Start all items off as not enabled

        lstVisualizations.addSelectionListener(e -> {
            int[] indices = lstVisualizations.getSelectedIndices();
            if(indices.length == 0) {
                lstDataStreams.setItemsEnabled(false);
                return;
            }
            Set<Class<? extends VisualizerGenerator>> visTypes = new HashSet<>();
            for(int index : indices) {
                VisualizerGlob glob = mdlVisualizations.getElementAt(index);
                visTypes.add(glob.generator.getClass());
            }
            if(visTypes.size() > 1) {
                lstDataStreams.setItemsEnabled(false);
                return;
            }
            lstDataStreams.setItemsEnabled(true);
            for(int i = 0; i < mdlDataStreams.size(); i++) {
                DataStreamGlob glob = mdlDataStreams.get(i);
                boolean check = false;
                for(VisualizerGlob vGlob : lstVisualizations.getSelectedValuesList()) {
                    if(vGlob.compoundParams.getDataStreamIds().contains(glob.id)) {
                        check = true;
                    }
                }
                lstDataStreams.setCheckedIndex(i, check);
            }
            for(int i = 0; i < mdlDataStreams.size(); i++) {
                DataStreamGlob glob = mdlDataStreams.get(i);
                boolean appliesTo =
                    lstVisualizations.getSelectedValuesList().get(0).generator.appliesToDataStream(glob.stream);
                lstDataStreams.setItemEnabled(i, appliesTo);
            }
            // TODO: Show unrecognized streams?  The user can't use them obviously
            // and the way it's coded right now, any change to any check box on the
            // right would remove those ID's anyway.  So a decision should be made
            // one way or another to leave super trivial design point purgatory.
        });

        btnHelp.addActionListener(e -> {
            GeneratorWrapper<VisualizerGenerator> wrapper = cboAvailableVis.getSelected();
            VisualizerGenerator generator = wrapper.getGenerator();
            showHelpMessage(
                (Component) e.getSource(), "Visualizer", generator, null);
        });

        btnAdd.addActionListener(e -> {
            GeneratorWrapper<VisualizerGenerator> wrapper = cboAvailableVis.getSelected();
            VisualizerGenerator generator = wrapper.getGenerator();
            VisualizerParams params = generator.createParams();

            VisualizerParamsAddEditDialog dlg =
                new VisualizerParamsAddEditDialog(
                    getWindow(), params, generator.getName(),
                    VisualizerParamsAddEditDialog.Action.ADD);
            dlg.setVisible(true);

            if(dlg.getResult() == VisualizerParamsAddEditDialog.ACCEPT) {
                VisualizerParams paramsAfter = dlg.getParams();
                VisualizerCompoundParams cParams =                                // v true here just to be overridden later
                    new VisualizerCompoundParams(paramsAfter, new HumanDescriptor(), true, new ArrayList<>());
                mdlVisualizations.addElement(new VisualizerGlob(cParams, generator));
                int newIndex = mdlVisualizations.size() - 1;
                lstVisualizations.setSelectedIndex(newIndex);
                lstVisualizations.setCheckedIndex(newIndex, true);
            }
        });

        btnRemove.addActionListener(e -> lstVisualizations.removeSelected());

        btnEdit.addActionListener(e -> {
            int index = lstVisualizations.getSelectedIndex();
            if(index == -1) {
                return;
            }
            VisualizerGlob glob = mdlVisualizations.getElementAt(index);
            VisualizerGenerator generator = glob.generator;
            VisualizerParams params = glob.compoundParams.getParams();

            VisualizerParamsAddEditDialog dlg =
                new VisualizerParamsAddEditDialog(
                    getWindow(), params, generator.getName(),
                    VisualizerParamsAddEditDialog.Action.EDIT);
            dlg.setVisible(true);

            if(dlg.getResult() == VisualizerParamsAddEditDialog.ACCEPT) {
                VisualizerParams paramsAfter = dlg.getParams();
                glob.compoundParams.setParams(paramsAfter);
                lstVisualizations.updateUI();
            }
        });

        btnDescribe.addActionListener(e -> {
            int index = lstVisualizations.getSelectedIndex();
            if(index == -1) {
                return;
            }
            VisualizerGlob glob = mdlVisualizations.getElementAt(index);
            VisualizerGenerator generator = glob.generator;
            HumanDescriptor systemDescriptor = new HumanDescriptor(generator.getName(), generator.getDescription());
            HumanDescriptor userDescriptor = glob.compoundParams.getUserDescriptor();
            SystemUserDescriptorEditDialog dlg =
                new SystemUserDescriptorEditDialog(
                    getWindow(),
                    "Describe Visualizer: " + systemDescriptor.getName(),
                    "Change the name and description of this visualizer.",
                    systemDescriptor, userDescriptor,
                    "<html><i>(optional - a short label describing this view's current configuration and purpose)</i></html>",
                    "<html><i>(optional - a longer description of this view's current configuration and purpose)</i></html>"
                );
            dlg.setVisible(true);
            if(dlg.getResult() == SystemUserDescriptorEditDialog.ACCEPT) {
                HumanDescriptor descAfter = dlg.getUserDescriptor();
                glob.compoundParams.setUserDescriptor(descAfter);
                lstVisualizations.updateUI();
            }
        });

        btnClone.addActionListener(e -> {
            int index = lstVisualizations.getSelectedIndex();
            if(index == -1) {
                return;
            }
            VisualizerGlob glob = mdlVisualizations.getElementAt(index);
            VisualizerGenerator generator = glob.generator;
            VisualizerParams params = glob.compoundParams.getParams();
            boolean checked = lstVisualizations.isCheckedIndex(index);

            VisualizerParamsAddEditDialog dlg =
                new VisualizerParamsAddEditDialog(
                    getWindow(), params, generator.getName(),
                    VisualizerParamsAddEditDialog.Action.CLONE);
            dlg.setVisible(true);

            if(dlg.getResult() == VisualizerParamsAddEditDialog.ACCEPT) {
                VisualizerParams paramsAfter = dlg.getParams();
                paramsAfter.setTrackedId(UUID.randomUUID());
                HumanDescriptor userDescriptor = new HumanDescriptor(glob.compoundParams.getUserDescriptor());
                VisualizerCompoundParams cParams =                                // v checked here just to be overridden later
                    new VisualizerCompoundParams(paramsAfter, userDescriptor, checked,
                        new ArrayList<>(glob.compoundParams.getDataStreamIds()));
                mdlVisualizations.addElement(new VisualizerGlob(cParams, generator));
                int newIndex = mdlVisualizations.size() - 1;
                lstVisualizations.setSelectedIndex(newIndex);
                lstVisualizations.setCheckedIndex(newIndex, checked);
            }
        });

        btnMoveUp.addActionListener(e -> lstVisualizations.moveSelectedUp());
        btnMoveDown.addActionListener(e -> lstVisualizations.moveSelectedDown());

        btnHelp2.addActionListener(e -> {
            int selected = lstDataStreams.getSelectedIndex();
            if(selected != -1) {
                DataStreamGlob glob = mdlDataStreams.get(selected);
                String msg =
                    glob.id + "\n\n" +
                    glob.stream.getSystemDescriptor().getName() + "\n\n" +
                    glob.stream.getSystemDescriptor().getDescription() + "\n\n" +
                    "Current Size: " + glob.stream.size();
                Dialogs.showQuestion(getWindow(), msg.toString().trim(), "Data Stream Information");
            }
        });
    }

    public void showHelpMessage(Component cmp, String titlePrefix, VisualizerGenerator generator, String extra) {
        String msg = generator.getName() + "\n\n" + generator.getDescription();
        if(!StringUtil.isBlank(extra)) {
            msg += "\n\n" + extra;
        }
        msg += "\n\nType: " + generator.getType();
        Dialogs.showQuestion(getWindow(), msg.toString().trim(), titlePrefix + " Information");
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors (Computed)

    @Override
    public FluxPanelParams get() {
        List<VisualizerCompoundParams> visualizerCompoundParams = new ArrayList<>();
        for(int i = 0; i < mdlVisualizations.size(); i++) {
            VisualizerGlob glob = mdlVisualizations.getElementAt(i);
            boolean checked = lstVisualizations.isCheckedIndex(i);
            glob.compoundParams.setEnabled(checked);
            visualizerCompoundParams.add(glob.compoundParams);
        }
        return new FluxPanelParams()
            .setVisualizerCompoundParams(visualizerCompoundParams)
            .setCombineMethod(cboCombineMethods.getSelected())
        ;
    }

    // Mutators

    @Override
    public void set(FluxPanelParams params) {
        mdlVisualizations.clear();
        int v = 0;
        for(VisualizerCompoundParams cParams : params.getVisualizerCompoundParams()) {
            VisualizerGenerator generator = Generator.lookup(cParams.getParams());
            VisualizerCompoundParams cParams2 = new VisualizerCompoundParams(cParams);
            VisualizerGlob glob = new VisualizerGlob(cParams2, generator);
            mdlVisualizations.addElement(glob);
            if(cParams.isEnabled()) {
                lstVisualizations.setCheckedIndex(v, true);
            }
            v++;
        }
        cboCombineMethods.setSelectedItem(params.getCombineMethod());
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void validateInput(ValidationContext context) {
        if(cboCombineMethods.getSelected() == VisualizerCombineMethod.INTEGRATED) {
            for(int i = 0; i < mdlVisualizations.size(); i++) {
                VisualizerGlob glob = mdlVisualizations.getElementAt(i);
                VisualizerGenerator generator = glob.generator;
                boolean checked = lstVisualizations.isCheckedIndex(i);
                if(checked && generator.getType() == VisualizerType.JAVA_SWING) {
                    context.error("To use the Integrated combine method, all visualizers must be of Java2D type.");
                    break;
                }
            }
        }
        for(int i = 0; i < mdlVisualizations.size(); i++) {
            if(lstVisualizations.isCheckedIndex(i)) {
                VisualizerGlob glob = mdlVisualizations.getElementAt(i);
                VisualizerGenerator generator = glob.generator;
                int[] minMax = generator.getMinMaxDataStreams();
                int sz = glob.compoundParams.getDataStreamIds().size();
                if(sz < minMax[0]) {
                    context.warnFor(glob.toString(), "An enabled visualization does not indicate the recommended minimum number of data streams and may not function properly.");
                }
                if(sz > minMax[1]) {
                    context.warnFor(glob.toString(), "An enabled visualization indicates more than the recommended maximum number of data streams and may not function properly.");
                }
            }
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class VisualizerGlob {
        private VisualizerCompoundParams compoundParams;
        private VisualizerGenerator generator;

        public VisualizerGlob(VisualizerCompoundParams compoundParams, VisualizerGenerator generator) {
            this.compoundParams = compoundParams;
            this.generator = generator;
        }

        private String computeString() {
            String title = compoundParams.getUserDescriptor().getName();
            if(StringUtil.isBlank(title)) {
                title = "(Unnamed)";
            }
            title = generator.getName() + ": " + title;
            return title;
        }

        @Override
        public String toString() {
            return computeString() + " [" + compoundParams.getParams() + "]";
        }
    }

    private class DataStreamGlob {
        private String id;
        private DataStream stream;

        public DataStreamGlob(String id, DataStream stream) {
            this.id = id;
            this.stream = stream;
        }

        @Override
        public String toString() {
            return id + ": " + stream.getClass().getSimpleName() + "/" + stream.getSystemDescriptor().getName();
        }
    }


    //////////
    // TEST //
    //////////

//    public static void main(String[] args) {
//        PluginManager.initialize(
//            RepletePlugin.class,
//            FluxPlugin.class
//        );
//        FluxPanelParamsDialog dlg = new FluxPanelParamsDialog(null, new FluxPanelParams());
//        dlg.setLocation(800, 200);
//        dlg.setVisible(true);
//        if(dlg.getResult() == FluxPanelParamsDialog.ACCEPT) {
//            FluxPanelParams params = dlg.getParams();
//            params.dump();
//        }
//    }
}
