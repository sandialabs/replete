package replete.plugins.ui;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import replete.plugins.AbstractPlugin;
import replete.plugins.Plugin;
import replete.plugins.ValidationResult;
import replete.plugins.state.ExtensionPointState;
import replete.plugins.state.ExtensionState;
import replete.plugins.state.PluginState;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.tree.NodeSimpleLabel;
import replete.ui.tree.RTree;
import replete.ui.tree.RTreeNode;
import replete.ui.windows.escape.EscapeDialog;

public class PluginDetailsDialog extends EscapeDialog {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PluginDetailsDialog(EscapeDialog parent, PluginState pluginState) {
        super(parent, "Plug-in Details", true);
        setIcon(RepleteImageModel.PROPERTIES);

        JButton btnClose = Lay.btn("&Close", CommonConcepts.CANCEL, (ActionListener) e -> close());

        RTreeNode nERoot = new RTreeNode();
        List<ExtensionState> exts = pluginState.getExtensions();
        if(exts != null) {
            for(ExtensionState ext : exts) {
                boolean valid = true;
                String errorMsg = null;
                ValidationResult vResult = ext.getValidationResult();
                if(vResult != null) {
                    valid = vResult.isValid();
                    errorMsg = vResult.getErrorMsg();
                }
                RTreeNode nExt = new RTreeNode(new NodeExt(ext, valid));
                nERoot.add(nExt);
                if(errorMsg != null) {
                    nExt.add(new NodeSimpleLabel(errorMsg, ImageLib.get(CommonConcepts.ERROR)));
                }
                ExtensionPointState extPointState = ext.getExtPointState();
                RTreeNode nExtPoint = new RTreeNode(new NodeExtPoint(extPointState));
                nExt.add(nExtPoint);
            }
        }

        RTreeNode nEPRoot = new RTreeNode();
        List<ExtensionPointState> extPointStates = pluginState.getExtensionPoints();
        if(extPointStates != null) {
            for(ExtensionPointState extPointState : extPointStates) {
                RTreeNode nExtPoint = new RTreeNode(new NodeExtPoint(extPointState));
                nEPRoot.add(nExtPoint);
            }
        }

        RTree treExts = new RTree(nERoot);
        treExts.setRootVisible(false);
        treExts.expandAll();

        RTree treExtPoints = new RTree(nEPRoot);
        treExtPoints.setRootVisible(false);

        Lay.BLtg(this,
            "C", Lay.GL(2, 1,
                Lay.BL(
                    "N", Lay.lb("<html><u>Extensions:</u> Instances where this plug-in provides additional functionality to the platform or other plug-ins.</html>"),
                    "C", Lay.sp(treExts),
                    "hgap=5,vgap=5"
                ),
                Lay.BL(
                    "N", Lay.lb("<html><u>Extension Points:</u> Instances where this plug-in defines a place where additional functionality can be provided by other plug-ins.</html>"),
                    "C", Lay.sp(treExtPoints),
                    "hgap=5,vgap=5"
                ),
                "eb=5tlr,hgap=5,vgap=5"
            ),
            "S", Lay.FL("R", btnClose),
            "size=[775,500],center"
        );
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        Plugin plugin = new AbstractPlugin() {
            public String getName() {
                return "Test Name";
            }
            @Override
            public String getVersion() {
                return "Test Version";
            }
            @Override
            public String getProvider() {
                return "Test Provider";
            }
            @Override
            public ImageIcon getIcon() {
                return null;
            }
            @Override
            public String getDescription() {
                return "Test Description";
            }
        };
        PluginState pluginState = new PluginState(plugin);
        PluginDetailsDialog dlg = new PluginDetailsDialog(null, pluginState);
        dlg.setVisible(true);
    }
}
