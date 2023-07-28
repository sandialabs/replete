package replete.plugins.ui;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import replete.plugins.PluginInitializationResults;
import replete.plugins.PluginManager;
import replete.plugins.state.PluginManagerState;
import replete.plugins.state.PluginState;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.validation.ValidationContext;
import replete.ui.validation.ValidationContextPanel;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;
import replete.util.JarUtil;

public class PluginDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    // UI

    private JTable tblPlugins;
    private PluginTableModel mdlPlugins;
    private JLabel lblDesc;
    private JLabel lblIcon;
    private JButton btnDetails;

    // Core

    private PluginManagerState curState;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PluginDialog(final JFrame parent) {
        this(parent, PluginManager.getSummaryState(), true);
    }
    public PluginDialog(final JFrame parent, final PluginManagerState piState, boolean allowInstall) {
        super(parent, "Plug-ins", true);
        setIcon(CommonConcepts.PLUGIN);
        curState = piState;

        mdlPlugins = new PluginTableModel(piState);

        Lay.BLtg(this,
            "N", Lay.lb("<html>These are the plug-ins that are currently installed.  Select a plug-in to view its description.</html>", "eb=5"),
            "C", Lay.BL(
                "C", Lay.p(Lay.sp(tblPlugins = new JTable(mdlPlugins)), "eb=5tlr"),
                "S", Lay.BL(
                    "C", Lay.BL(
                        "W", lblIcon = Lay.lb("", "opaque=false,valign=top"),
                        "C", lblDesc = Lay.lb("", "opaque=false,valign=top"),
                        "bg=white,eb=4,augb=mb(1,black),hgap=5"
                    ),
                    "prefh=100,eb=5tlr"
                )
            ),
            "S", Lay.BL(
                "W", Lay.FL(
                    btnDetails = Lay.btn("&Details...", RepleteImageModel.PROPERTIES, (ActionListener) e -> showDetails(), "enabled=false"),
                    allowInstall ? Box.createHorizontalStrut(5) : null,
                    allowInstall ? Lay.btn("&Install...", RepleteImageModel.PLUGIN_INSTALL, (ActionListener) e -> install()) : null,
                    Box.createHorizontalStrut(5),
                    Lay.btn("&Summary...", CommonConcepts.FAVORITE, (ActionListener) e -> showSummary()),
                    "gap=0"
                ),
                "E", Lay.FL(
                    Lay.btn("&Close", CommonConcepts.CANCEL, (ActionListener) e -> close()),
                    "gap=0"
                ),
                "eb=5"
            ),
            "size=[800,400],center"
        );

        tblPlugins.setFillsViewportHeight(true);
        tblPlugins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPlugins.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) &&
                        e.getClickCount() == 2 &&
                        tblPlugins.rowAtPoint(e.getPoint()) != -1 &&
                        tblPlugins.getSelectedRowCount() != 0) {
                    showDetails();
                }
            }
        });

        tblPlugins.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(tblPlugins.getSelectedRowCount() != 0) {
                    PluginState[] pluginStates =
                        curState.getGlobalPlugins().values().toArray(
                            new PluginState[0]);
                    PluginState plugin = pluginStates[tblPlugins.getSelectedRow()];
                    ImageIcon icon = plugin.getIcon();
                    if(icon == null) {
                        icon = ImageLib.get(CommonConcepts.PLUGIN);
                    }
                    lblIcon.setIcon(icon);
                    String desc = plugin.getDesc();
                    if(desc == null) {
                        desc = "(no description provided)";
                    }
                    lblDesc.setText(desc);
                    btnDetails.setEnabled(true);
                } else {
                    lblIcon.setIcon(null);
                    lblDesc.setText("");
                    btnDetails.setEnabled(false);
                }
            }
        });
    }

    protected void install() {
        RFileChooser chooser = RFileChooser.getChooser("Select JAR");
        chooser.setCurrentDirectory(new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\AvondaleBundler\\build\\deploy"));
        RFilterBuilder builder = new RFilterBuilder(chooser, false);
        builder.append("JAR Files (*.jar)", "jar");
        if(chooser.showOpen(this)) {
            File jarFile = chooser.getSelectedFileResolved();
            if(!JarUtil.isJar(jarFile)) {
                Dialogs.showError(this, "The selected file was not a JAR file.", "Install Error");
            } else {
                try {
                    PluginInitializationResults results = PluginManager.initialize(jarFile);
                    mdlPlugins.setState(curState = PluginManager.getSummaryState());
                    String sp = "      ";
                    String msg;
                    if(results.getPluginsLoaded() >= 1) {
                        msg = "One or more plug-ins were successfully loaded.\n";
                    } else {
                        msg = "No plug-ins were successfully loaded.\n";
                    }
                    msg += sp + "Classes Inspected: " + results.getClassesInspected() + "\n";
                    msg += sp + "Plug-ins Found: " + results.getPluginsFound() + "\n";
                    msg += sp + "Plug-ins Loaded: " + results.getPluginsLoaded() + "\n";
                    msg += sp + "Extension Points Defined: " + results.getExtensionPointsDefined() + "\n";
                    msg += sp + "Extensions Provided: " + results.getExtensionsProvided() + "\n";

                    ValidationContext context = results.getValidationContext();

                    if(!context.hasMessage()) {
                        if(results.getPluginsLoaded() >= 1) {
                            Dialogs.showMessage(this, msg, "Plug-in Install Result");
                        } else {
                            Dialogs.showWarning(this, msg, "Plug-in Install Result");
                        }
                    } else {
                        ValidationContextPanel pnl;
                        JDialog dlg = Lay.dlg(this, "Plug-in Install Result");
                        Lay.BLtg(dlg,
                            "N", Lay.p(Lay.lbc(msg, "bold"), "prefh=105,eb=5tlr"),
                            "C", Lay.hn(pnl = new ValidationContextPanel(false), "eb=5tlr"),
                            "S", Lay.FL("R", Lay.btn("&Close", CommonConcepts.CANCEL, "closer")),
                            "size=600,center"
                        );
                        pnl.set(context);
                        dlg.setVisible(true);
                    }

                } catch(Exception e) {
                    Dialogs.showDetails(this, "There was an error processing the selected JAR file.", "Install Error", e);
                }
            }
        }
    }

    protected void showDetails() {
        PluginState pluginState = mdlPlugins.getPlugin(tblPlugins.getSelectedRow());
        PluginDetailsDialog dlg = new PluginDetailsDialog(PluginDialog.this, pluginState);
        dlg.setVisible(true);
    }

    private void showSummary() {
        PluginInitializationResults results = curState.getInitializationResults();
        String sp = "      ";
        String msg;
        if(results.getPluginsLoaded() >= 1) {
            msg = "One or more plug-ins were successfully loaded.\n";
        } else {
            msg = "No plug-ins were successfully loaded.\n";
        }
        msg += sp + "Classes Inspected: " + results.getClassesInspected() + "\n";
        msg += sp + "Plug-ins Found: " + results.getPluginsFound() + "\n";
        msg += sp + "Plug-ins Loaded: " + results.getPluginsLoaded() + "\n";
        msg += sp + "Extension Points Defined: " + results.getExtensionPointsDefined() + "\n";
        msg += sp + "Extensions Provided: " + results.getExtensionsProvided() + "\n";
        ValidationContext context = results.getValidationContext();
        ValidationContextPanel pnl;
        JDialog dlg = Lay.dlg(this, "Plug-in Initialization Summary");
        Lay.BLtg(dlg,
            "N", Lay.p(Lay.lbc(msg, "bold"), "prefh=105,eb=5tlr"),
            "C", Lay.hn(pnl = new ValidationContextPanel(false), "eb=5tlr"),
            "S", Lay.FL("R", Lay.btn("&Close", CommonConcepts.CANCEL, "closer")),
            "size=[800,660],center"
        );
        pnl.set(context);
        dlg.setVisible(true);
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        PluginManager.initialize("replete.plugins.test.TestPlugin");
        System.out.println(PluginManager.getInitializationResults().getValidationContext());
        PluginDialog dlg = new PluginDialog(null);
        dlg.setVisible(true);
    }
}
