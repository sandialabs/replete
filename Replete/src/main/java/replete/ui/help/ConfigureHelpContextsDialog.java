package replete.ui.help;

import java.awt.Window;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;

import replete.io.FileUtil;
import replete.plugins.ExtensionPoint;
import replete.plugins.PluginManager;
import replete.plugins.UiGenerator;
import replete.plugins.ui.GeneratorWrapper;
import replete.ui.ColorLib;
import replete.ui.Iconable;
import replete.ui.combo.RComboBox;
import replete.ui.form.RFormPanel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.label.BorderedLabel;
import replete.ui.lay.Lay;
import replete.ui.list.IconableRenderer;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RTextField;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeDialog;
import replete.web.UrlUtil;
import replete.xstream.XStreamWrapper;

public class ConfigureHelpContextsDialog extends EscapeDialog {


    ////////////
    // FIELDS //
    ////////////

    public static final int SAVE   = 0;
    public static final int CANCEL = 1;

    private int result = CANCEL;

    private RComboBox<CachedConfigEntry> cboGenerators;
    private DefaultComboBoxModel<CachedConfigEntry> mdlGenerators;
    private RTextField txtAlbumFile, txtContentDir;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public ConfigureHelpContextsDialog(Window parent) {
        super(parent, "Configure Help Providers", true);
        setIcon(CommonConcepts.CONFIGURATION);

        JButton btnSelect;
        Lay.BLtg(this,
            "N", Lay.lb("asdfljsalfkasfjslakj", "bg=100,fg=white,eb=5,augb=mb(1b,black)"),
            "C", new FormPanel(),
            "S", Lay.FL("R",
                btnSelect = Lay.btn("&Save", CommonConcepts.SAVE),
                Lay.btn("&Cancel", CommonConcepts.CANCEL, "closer")
            ),
            "size=[800,300],center"
        );

        setDefaultButton(btnSelect);

        cboGenerators.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.DESELECTED) {
                setControlsToEntry((CachedConfigEntry) e.getItem());
            } else {
                setEntryToControls();
            }
        });

        btnSelect.addActionListener(e -> {
            result = SAVE;
            CachedConfigEntry entry = cboGenerators.getSelected();
            setControlsToEntry(entry);
            for(int i = 0; i < mdlGenerators.getSize(); i++) {
                entry = mdlGenerators.getElementAt(i);
                StandardHelpProvider provider = (StandardHelpProvider) entry.wrapper.getGenerator();
                provider.setCurrentAlbumUrl(UrlUtil.url(entry.albumUrl));
                provider.setCurrentContentDirUrl(UrlUtil.url(entry.contentDirUrl));
            }
            close();
        });

        setEntryToControls();
    }

    private void setControlsToEntry(CachedConfigEntry entry) {
        entry.albumUrl      = txtAlbumFile.getText();
        entry.contentDirUrl = txtContentDir.getText();
    }

    private void setEntryToControls() {
        CachedConfigEntry entry = cboGenerators.getSelected();
        txtAlbumFile.setText(entry.albumUrl);
        txtContentDir.setText(entry.contentDirUrl);
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public int getResult() {
        return result;
    }

    public HelpProvider getHelpProvider() {
        CachedConfigEntry entry = cboGenerators.getSelected();
        GeneratorWrapper wrapper = entry.wrapper;
        UiGenerator generator = wrapper.getGenerator();
        return (HelpProvider) generator;
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class FormPanel extends RFormPanel {
        public FormPanel() {
            init();
        }

        @Override
        protected void addFields() {

            mdlGenerators = new DefaultComboBoxModel<>();
            List<ExtensionPoint> exts = PluginManager.getExtensionsForPoint(HelpProvider.class);
            for(ExtensionPoint ext : exts) {
                StandardHelpProvider gen = (StandardHelpProvider) ext;
                GeneratorWrapper wrapper = new GeneratorWrapper<>(gen);
                CachedConfigEntry entry = new CachedConfigEntry(wrapper);
                mdlGenerators.addElement(entry);
            }
            JButton btnHelp;
            JPanel pnlGenerators = Lay.FL("L",
                cboGenerators = Lay.cb(mdlGenerators, new IconableRenderer()), Lay.hs(5),
                btnHelp = Lay.btn(CommonConcepts.HELP, "icon,focusable=false,ttt=Help"),
                "hgap=0"
            );

            btnHelp.addActionListener(e -> {
                CachedConfigEntry entry = cboGenerators.getSelected();
                GeneratorWrapper wrapper = entry.wrapper;
                UiGenerator generator = wrapper.getGenerator();
                Dialogs.showMessage(this, generator.getName() + "\n\n"+ generator.getDescription(), "Help Provider");
            });

            BorderedLabel lblAlbumFileReadable, lblAlbumFileWritable;
            BorderedLabel lblContentDirReadable, lblContentDirWritable;
            JButton btnDefault;
            addField("Help Providers", pnlGenerators);
            addField("Album File",  Lay.BL(
                "C", txtAlbumFile = Lay.tx("", "selectall"),
                "E", Lay.FL(
                    lblAlbumFileReadable = Lay.lb("R", "center,bordered,prefw=30,ttt=Readable"),
                    lblAlbumFileWritable = Lay.lb("W", "center,bordered,prefw=30,ttt=Writable")
                )
            ));
            addField("Content Dir", Lay.BL(
                "C", txtContentDir = Lay.tx("", "selectall"),
                "E", Lay.FL(
                    lblContentDirReadable = Lay.lb("R", "center,bordered,prefw=30,ttt=Readable"),
                    lblContentDirWritable = Lay.lb("W", "center,bordered,prefw=30,ttt=Writable")
                )
            ));
            addField(
                Lay.FL("L",
                    Lay.hs(90), btnDefault = Lay.btn("&Set To Default", CommonConcepts.RESET),
                    "vgap=0"
                ),
                45
            );

            txtAlbumFile.addChangeListener(new DocumentChangeListener() {
                public void documentChanged(DocumentEvent e) {
                    try {
                        URL url = UrlUtil.url(txtAlbumFile.getText());

                        if(url.getProtocol().equals("file")) {
                            File f = FileUtil.toFile(url);
                            if(FileUtil.isReadableFile(f)) {
                                lblAlbumFileReadable
                                    .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                    .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                    .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                            } else {
                                lblAlbumFileReadable.setDefaultColors();
                            }
                        } else {
                            InputStream is = url.openStream();
                            XStreamWrapper.loadTarget(is);       // Is currently an XStream-serialized text file
                            lblAlbumFileReadable
                                .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                        }

                    } catch(Exception ex) {
                        lblAlbumFileReadable.setDefaultColors();
                    }

                    try {
                        URL url = UrlUtil.url(txtAlbumFile.getText());
                        if(url.getProtocol().equals("file")) {
                            File f = FileUtil.toFile(url);
                            if(FileUtil.isWritableFile(f)) {
                                lblAlbumFileWritable
                                    .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                    .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                    .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                            } else {
                                File p = f.getParentFile();
                                if(FileUtil.isWritableDir(p) && !f.isDirectory()) {
                                    lblAlbumFileWritable
                                        .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                        .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                        .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                                } else {
                                    lblAlbumFileWritable.setDefaultColors();
                                }
                            }
                        } else {
                            lblAlbumFileWritable.setDefaultColors();
                        }
                    } catch(Exception ex) {
                        lblAlbumFileWritable.setDefaultColors();
                    }
                }
            });
            txtContentDir.addChangeListener(new DocumentChangeListener() {
                public void documentChanged(DocumentEvent e) {
                    try {
                        URL url = UrlUtil.url(txtContentDir.getText());

                        if(url.getProtocol().equals("file")) {
                            File f = FileUtil.toFile(url);
                            if(FileUtil.isReadableDir(f)) {
                                lblContentDirReadable
                                    .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                    .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                    .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                            } else {
                                lblContentDirReadable.setDefaultColors();
                            }
                        } else {
                            InputStream is = url.openStream();
                            XStreamWrapper.loadTarget(is);       // Is currently an XStream-serialized text file
                            lblContentDirReadable
                                .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                        }

                    } catch(Exception ex) {
                        lblContentDirReadable.setDefaultColors();
                    }

                    try {
                        URL url = UrlUtil.url(txtContentDir.getText());
                        if(url.getProtocol().equals("file")) {
                            File f = FileUtil.toFile(url);
                            if(FileUtil.isWritableDir(f)) {
                                lblContentDirWritable
                                    .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                    .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                    .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                            } else {
                                File p = f.getParentFile();
                                if(FileUtil.isWritableDir(p) && !f.isFile()) {
                                    lblContentDirWritable
                                        .setBubbleForegroundColor(ColorLib.GREEN_STRONG)
                                        .setBubbleBackgroundColor(ColorLib.GREEN_LIGHT)
                                        .setBubbleBorderColor(ColorLib.GREEN_STRONG);
                                } else {
                                    lblContentDirWritable.setDefaultColors();
                                }
                            }
                        } else {
                            lblContentDirWritable.setDefaultColors();
                        }
                    } catch(Exception ex) {
                        lblContentDirWritable.setDefaultColors();
                    }
                }
            });

            btnDefault.addActionListener(e -> {
                CachedConfigEntry entry = cboGenerators.getSelected();
                GeneratorWrapper wrapper = entry.wrapper;
                UiGenerator generator = wrapper.getGenerator();
                StandardHelpProvider manager = (StandardHelpProvider) generator;
                txtAlbumFile.setText(manager.getDefaultAlbumUrl().toString());
                txtContentDir.setText(manager.getDefaultContentDirUrl().toString());
            });
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

    private class CachedConfigEntry implements Iconable {


        ////////////
        // FIELDS //
        ////////////

        public GeneratorWrapper<HelpProvider> wrapper;
        public String albumUrl;
        public String contentDirUrl;


        //////////////////
        // CONSTRUCTORS //
        //////////////////

        public CachedConfigEntry(GeneratorWrapper<HelpProvider> wrapper) {
            this.wrapper = wrapper;
            StandardHelpProvider manager = (StandardHelpProvider) wrapper.getGenerator();

            albumUrl      = manager.getCurrentAlbumUrl().toString();
            contentDirUrl = manager.getCurrentContentDirUrl().toString();
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public Icon getIcon() {
            return wrapper.getIcon();
        }
        @Override
        public String toString() {
            return wrapper.toString();
        }
    }
}
