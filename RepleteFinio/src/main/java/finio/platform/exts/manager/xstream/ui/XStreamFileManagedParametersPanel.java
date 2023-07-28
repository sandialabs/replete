package finio.platform.exts.manager.xstream.ui;

import javax.swing.JFileChooser;

import finio.manager.ManagedParameters;
import finio.platform.exts.manager.xstream.XStreamFileManagedParameters;
import finio.ui.manager.ManagedParametersPanel;
import replete.ui.form.FileSelectionPanel;
import replete.ui.form.FileSelectionPanel.DialogType;
import replete.ui.form.RFormPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;

public class XStreamFileManagedParametersPanel extends ManagedParametersPanel {


    ////////////
    // FIELDS //
    ////////////

    private FileSelectionPanel pnlFile;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public XStreamFileManagedParametersPanel() {
        Lay.BLtg(this,
            "C", new TrivialFormPanel()
        );
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public ManagedParameters getParameters() {
        return new XStreamFileManagedParameters()
            .setFile(pnlFile.getPath())
        ;
    }
    @Override
    public void setParameters(ManagedParameters params) {
        XStreamFileManagedParameters params2 = (XStreamFileManagedParameters) params;
        pnlFile.setPath(params2.getFile());
    }
    @Override
    public String getValidationMessage() {
        return null;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class TrivialFormPanel extends RFormPanel {
        public TrivialFormPanel() {
            super(40);
            init();
        }
        @Override
        protected void addFields() {
            pnlFile = new FileSelectionPanel(this, "Select XStream File", ImageLib.get(CommonConcepts.SAVE),
                DialogType.SAVE, JFileChooser.FILES_ONLY);
            addField("Main", "File", pnlFile, 40, false);
        }

        @Override
        protected boolean showCancelButton() {
            return false;
        }
        @Override
        protected boolean showSaveButton() {
            return false;
        }
    }
}
