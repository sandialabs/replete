package finio.ui.extractors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import finio.extractors.jo.DefaultPopulateParamsCreator;
import finio.extractors.jo.HostNonTerminalCreator;
import finio.extractors.jo.KeyValueIteratorCreator;
import finio.extractors.jo.KeyValueRegistrar;
import finio.extractors.jo.NonTerminalExpansionDecider;
import finio.extractors.jo.ObjectResolver;
import finio.extractors.jo.PopulateParams;
import finio.extractors.jo.PopulateParamsProvider;
import finio.extractors.jo.PostFieldsModifier;
import finio.extractors.jo.ReflectionDefaultPopulateParamsCreator;
import finio.extractors.jo.RevisitPolicy;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.ui.button.RCheckBox;
import replete.ui.combo.RComboBox;
import replete.ui.form.RFormPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class PopulateParamsProviderDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int OK = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private JButton btnExpand;
    private JButton btnCancel;

    private RComboBox<HostNonTerminalCreator> cboNtCreator;
    private RComboBox<KeyValueIteratorCreator> cboItCreator;
    private RComboBox<ObjectResolver> cboKeyResolver;
    private RComboBox<ObjectResolver> cboValResolver;
    private RComboBox<KeyValueRegistrar> cboRegistrar;
    private RComboBox<NonTerminalExpansionDecider> cboDecider;
    private RComboBox<RevisitPolicy> cboRevisit;
    private RComboBox<PostFieldsModifier> cboModifier;
    private RCheckBox chkRecord;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PopulateParamsProviderDialog(JFrame parent) {
        super(parent, "Choose Expansion Parameters", true);

        JButton[] btnDfCreators;

        List<ExtensionPoint> dfCreators = PluginManager.getExtensionsForPoint(
            DefaultPopulateParamsCreator.class);
        btnDfCreators = new JButton[dfCreators.size()];
        int b = 0;
        for(ExtensionPoint dfCreator : dfCreators) {
            DefaultPopulateParamsCreator dfCreator2 =
                (DefaultPopulateParamsCreator) dfCreator;
            final PopulateParams params = dfCreator2.create();
            btnDfCreators[b] = Lay.btn(dfCreator.toString());
            btnDfCreators[b].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setParams(params);
                }
            });
            b++;
        }

        JPanel pnl;

        String content =
            "Select the parameters by which this object will be expanded.  " +
            "These parameters represent the default parameters by which all " +
            "objects and sub-objects will be expanded into a generic non-terminal.  " +
            "These parameters correspond loosely to this expansion process: " +
            "<BR>&nbsp;&nbsp;&nbsp;1) EXTRACTION, 2) NT CREATION, 3) POPULATION, 4) KEY-VALUE ITERATOR CREATION," +
            "<BR>&nbsp;&nbsp;&nbsp;5) RESOLUTION, 6) REGISTRATION, 7) EXPANSION CHECK & RECURSION," +
            "<BR>&nbsp;&nbsp;&nbsp;8) REVISIT CHECK, 9) MODIFICATION, 10) RECORDING";
        Lay.BLtg(this,
            "N", Lay.lb("<html>" + content + "</html>",
                "bg=220,eb=5,augb=mb(1b,black)"),
            "C", Lay.BL(
                "N", pnl = Lay.FL("R", Lay.lb("Set To Default:")),
                "C", new InnerPanel()
            ),
            "S", Lay.FL("R",
                btnExpand = Lay.btn("&Expand", CommonConcepts.RUN),
                btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer"),
                "bg=100,mb=[1t,black]"
            ),
            "size=[570,590],center"
        );

        setDefaultButton(btnExpand);

        for(JButton btn : btnDfCreators) {
            pnl.add(btn);
        }

        btnExpand.addActionListener(e -> {
            result = OK;
            close();
        });

        setParams(new ReflectionDefaultPopulateParamsCreator().create());
    }

    public int getResult() {
        return result;
    }

    public PopulateParamsProvider getProvider() {
        // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
        PopulateParams params = new PopulateParams()
            .setHostNonTerminalCreator(cboNtCreator.getSelected())
            .setKeyValueIteratorCreator(cboItCreator.getSelected())
            .setKeyResolver(cboKeyResolver.getSelected())
            .setValueResolver(cboValResolver.getSelected())
            .setKeyValueRegistrar(cboRegistrar.getSelected())
            .setNonTerminalExpansionDecider(cboDecider.getSelected())
            .setRevisitPolicy(cboRevisit.getSelected())
            .setPostFieldsModifier(cboModifier.getSelected())
            .setRecordJavaSource(chkRecord.isSelected());

        PopulateParamsProvider populate = new PopulateParamsProvider();
        populate.getRuleTree().getRoot().setParams(params);
        return populate;
    }

    private void setParams(PopulateParams params) {
        // TODO: Not working because different instances...
        // NOTE: Populate Parameters Change Area - have to update this if a parameter is added.
        cboNtCreator.setSelectedItem(params.getHostNonTerminalCreator());
        cboItCreator.setSelectedItem(params.getKeyValueIteratorCreator());
        cboKeyResolver.setSelectedItem(params.getKeyResolver());
        cboValResolver.setSelectedItem(params.getValueResolver());
        cboRegistrar.setSelectedItem(params.getKeyValueRegistrar());
        cboDecider.setSelectedItem(params.getNonTerminalExpansionDecider());
        cboRevisit.setSelectedItem(params.getRevisitPolicy());
        cboModifier.setSelectedItem(params.getPostFieldsModifier());
        chkRecord.setSelected(params.isRecordJavaSource());
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class InnerPanel extends RFormPanel {
        private InnerPanel() {
            super(110);
            init();
        }
        @Override
        protected void addFields() {
            List<ExtensionPoint> ntCreators   = PluginManager.getExtensionsForPoint(HostNonTerminalCreator.class);
            List<ExtensionPoint> itCreators   = PluginManager.getExtensionsForPoint(KeyValueIteratorCreator.class);
            List<ExtensionPoint> resolvers    = PluginManager.getExtensionsForPoint(ObjectResolver.class);
            List<ExtensionPoint> registrars   = PluginManager.getExtensionsForPoint(KeyValueRegistrar.class);
            List<ExtensionPoint> modifiers    = PluginManager.getExtensionsForPoint(PostFieldsModifier.class);
            List<ExtensionPoint> deciders     = PluginManager.getExtensionsForPoint(NonTerminalExpansionDecider.class);

            addField("Main", "NT Creator",       cboNtCreator   = Lay.cb(ntCreators.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Iterator Creator", cboItCreator   = Lay.cb(itCreators.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Key Resolver",     cboKeyResolver = Lay.cb(resolvers.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Value Resolver",   cboValResolver = Lay.cb(resolvers.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Registrar",        cboRegistrar   = Lay.cb(registrars.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Decider",          cboDecider     = Lay.cb(deciders.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Revisit Policy",   cboRevisit     = Lay.cb(RevisitPolicy.values()), 40, false);
            addField("Main", "Modifier",         cboModifier    = Lay.cb(modifiers.toArray(new ExtensionPoint[0])), 40, false);
            addField("Main", "Record Native?",   chkRecord      = Lay.chk(), 40, false);
        }
        @Override
        protected boolean showSaveButton() {
            return false;
        }
        @Override
        protected boolean showCancelButton() {
            return false;
        }
    }
}
