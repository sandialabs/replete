package finio.ui.multidlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import replete.text.StringUtil;
import replete.ui.GuiUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.windows.escape.EscapeDialog;

public class FileInputSourcePanel extends InputSourcePanel {


    ////////////
    // FIELDS //
    ////////////

    private EscapeDialog parent;
    private RFileChooser chooser;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileInputSourcePanel(EscapeDialog parent) {
        this.parent = parent;
    }

    // TODO: CommonFileChooser can be enhanced to also be useful to
    // modes of usage not in an actual dialog.  Should not have to
    // replicate calls in client code that are currently inside the
    // class.  Also, there probably still is functionality that is
    // missing from this code (like filters, filter labels) because
    // of the need for a refactor.
    private RFileChooser configureChooser(RFileChooser chooser) {
        if(chooser == null) {
            chooser = RFileChooser.getChooser();
            RFileChooser.whiteCombos(chooser);
            GuiUtil.addMnemonics(chooser, RFileChooser.basicCaptions);
        }
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.addActionListener(multiInputActionListener);
        GuiUtil.traverse(chooser, cmp -> {
            if(cmp instanceof JButton) {
                JButton btn = (JButton) cmp;
                String text = StringUtil.cleanNull(btn.getText());
                if(text.equals("Open")) {
                    btn.setIcon(ImageLib.get(CommonConcepts.ACCEPT));
                    btn.setText("Accept");
                    btn.setMnemonic('A');
                } else if(text.equals("Cancel")) {
                    btn.setIcon(ImageLib.get(CommonConcepts.CANCEL));
                }
            }
        });
        return chooser;
    }

    private ActionListener multiInputActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals(JFileChooser.CANCEL_SELECTION)) {
                fireCancelNotifier();
            } else if(e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                fireAcceptNotifier();
            }
        }
    };


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected void postActivate() {
        if(chooser != null) {
            parent.setDefaultButton(chooser.getUI().getDefaultButton(chooser));
        }
    }

    @Override
    protected InputBundle[] getDataBundles() {
        List<InputBundle> bundles = new ArrayList<>();
        for(File file : chooser.getAllSelectedFiles()) {
            bundles.add(new InputBundle().setFile(file));
        }
        return bundles.toArray(new InputBundle[0]);
    }

    @Override
    protected void cleanUp() {
        chooser.removeActionListener(multiInputActionListener);
    }

    @Override
    public String getTitle() {
        return "File";
    }

    @Override
    public ImageIcon geIcon() {
        return ImageLib.get(CommonConcepts.FILE);
    }

    public void setChooser(RFileChooser chooser) {
        this.chooser = configureChooser(chooser);
        chooser.fixLabel();
        removeAll();
        Lay.BLtg(this,
            "C", this.chooser
        );
        chooser.setRecentContainerPanel(this);
        updateUI();
    }
    public RFileChooser getChooser() {
        return chooser;
    }
}
