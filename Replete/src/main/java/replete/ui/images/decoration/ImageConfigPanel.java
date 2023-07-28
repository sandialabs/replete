package replete.ui.images.decoration;

import java.awt.Image;

import javax.swing.JPanel;

import replete.collections.RLinkedHashMap;
import replete.ui.combo.RComboBox;
import replete.ui.images.RepleteImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.decoration.images.DecorationImageModel;
import replete.ui.lay.Lay;
import replete.ui.text.RTextField;

public class ImageConfigPanel extends JPanel {


    ////////////
    // FIELDS //
    ////////////

    private static RLinkedHashMap<String, ImageModelConcept> nameImageMap = new RLinkedHashMap<>(
        "(NONE)",            null,
        "job-loaded",        RepleteImageModel.JOB_LOADED,
        "job-unloaded",      RepleteImageModel.JOB_UNLOADED,
        "job-crashed",       RepleteImageModel.JOB_CRASHED,
        "job-error",         CommonConcepts.ERROR_DECORATOR,
        "job-finished",      CommonConcepts.COMPLETE_DECORATOR,
        "job-inprog",        CommonConcepts.INPROG_DECORATOR,
        "job-invalid",       RepleteImageModel.JOB_INVALID,
        "job-new",           CommonConcepts.NEW_DECORATOR,
        "job-paused",        CommonConcepts.PAUSED_DECORATOR,
        "job-running",       CommonConcepts.RUNNING_DECORATOR,
        "job-stopped",       CommonConcepts.STOPPED_DECORATOR,

        "arrest dagon",      DecorationImageModel.ARREST,
        "close",             DecorationImageModel.CLOSE,
        "friendly bear",     DecorationImageModel.FRIENDLY,
        "icon",              DecorationImageModel.ICON,
        "krogan",            DecorationImageModel.KROGAN,
        "mordiggian ghouls", DecorationImageModel.GHOULS,
        "patient bear",      DecorationImageModel.PATIENT,
        "quarian",           DecorationImageModel.QUARIAN,
        "play",              DecorationImageModel.PLAY,
        "slough",            DecorationImageModel.SLOUGH
    );

    // Core
    private Double scale  = 1.0;
    private Double alpha  = 1.0;

    // UI
    private RComboBox<String> cboName;
    private RComboBox<Anchor> cboAnchor;
    private RTextField        txtXY;
    private RTextField        txtScaling;
    private RTextField        txtAlpha;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ImageConfigPanel(int identifier) {

        Lay.FLtg(this, "L",
            Lay.lb("Image " + (identifier + 1) + ":"),
            cboName = Lay.cb("strelems", nameImageMap.keySet().toArray()),
            Lay.lb("Anchor"),  cboAnchor  = Lay.cb("strelems", Anchor.values()),
            Lay.lb("X, Y"),    txtXY      = Lay.tx("0, 0", 5, "selectall"),
            Lay.lb("Scaling"), txtScaling = Lay.tx("" + scale, 3, "selectall"),
            Lay.lb("Alpha"),   txtAlpha   = Lay.tx("" + alpha, 3, "selectall")
        );

        cboName.setMaximumRowCount(20);
        cboAnchor.setMaximumRowCount(20);
    }
    public ImageConfigPanel setAlignmentX(int input) {
        super.setAlignmentX(input);
        return this;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    public Image getImage() {
        ImageModelConcept concept = nameImageMap.get(cboName.getSelected());
        return concept == null ? null : ImageLib.getImg(concept);
    }
    public double getAlpha() {
        double alpha = 1.0;
        String entered = txtAlpha.getText();
        if(!entered.equals("")) {
            if(!entered.equals("1.0")) {
                try {
                    Integer testThis = Integer.parseInt(entered);
                    alpha = testThis / 255.0;
                } catch(Exception e1) {
                    alpha = Double.parseDouble(entered);
                }
            } else {
                alpha = 1.0;
            }
        }
        return alpha;
    }
    public ImageConfigPanel setAlpha(int alpha) {
        this.alpha = alpha / 255.0;
        txtAlpha.setText(alpha);
        return this;
    }
    public ImageConfigPanel setAlpha(Double alpha) {
        this.alpha = alpha;
        txtAlpha.setText(alpha);
        return this;
    }
    public Double getScale() {
        if(!txtScaling.getText().isEmpty()) {
            return Double.parseDouble(txtScaling.getText());
        }
        return null;
    }
    public ImageConfigPanel setScale(Double scale) {
        this.scale = scale;
        txtScaling.setText(scale);
        return this;
    }
    public Integer getXPos() {
        if(!txtXY.isBlank()) {
            try {
                String[] split = txtXY.getTrimmed().split("\\s*,\\s*");
                if(split.length > 1) {
                    return Integer.parseInt(split[0]);
                }
            } catch(Exception e) {

            }
        }
        return 0;
    }
    public Integer getYPos() {
        if(!txtXY.isBlank()) {
            try {
                String[] split = txtXY.getTrimmed().split("\\s*,\\s*");
                if(split.length > 1) {
                    return Integer.parseInt(split[1]);
                }
            } catch(Exception e) {

            }
        }
        return 0;
    }
    public Anchor getAnchor() {
        return cboAnchor.getSelected();
    }
    public ImageConfigPanel setAnchor(Anchor anchor) {
        cboAnchor.setSelectedItem(anchor);
        return this;
    }
}
