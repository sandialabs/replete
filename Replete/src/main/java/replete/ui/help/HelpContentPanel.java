package replete.ui.help;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;

import replete.io.FileUtil;
import replete.ui.BeanPanel;
import replete.ui.ColorLib;
import replete.ui.button.RButton;
import replete.ui.help.model.HelpDataModel;
import replete.ui.help.model.HelpPage;
import replete.ui.help.model.HelpPageContent;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.label.DashboardDataPanel;
import replete.ui.label.DataDescriptor;
import replete.ui.label.DatumDescriptor;
import replete.ui.lay.Lay;
import replete.ui.panels.RPanel;
import replete.ui.text.RTextArea;
import replete.ui.web.JavaFxBrowserPanel;
import replete.ui.windows.Dialogs;

public class HelpContentPanel extends BeanPanel<HelpPage> {


    ////////////
    // FIELDS //
    ////////////

    private HelpUiController uiController;
    private HelpDataModel dataModel;
    private HelpContentHeaderPanel pnlHeader;
    private HelpContentBodyPanel pnlBody;
    private HelpContentFooterPanel pnlFooter;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public HelpContentPanel(HelpUiController uiController, HelpDataModel dataModel) {
        this.uiController = uiController;
        this.dataModel = dataModel;

        Lay.CLtg(this,
            "normal", Lay.BL(
                "N", pnlHeader = new HelpContentHeaderPanel(),
                "C", pnlBody   = new HelpContentBodyPanel(),
                "S", pnlFooter = new HelpContentFooterPanel()
            ),
            "empty", Lay.p()
        );

        uiController.addPageSelectionListener(e -> set(e.getPage()));
    }

    @Override
    public void set(HelpPage page) {
        super.set(page);
        if(page == null) {
            showCard("empty");
        } else {
            if(page.getContent() == null) {
                page.setContent(new HelpPageContent());
            }
            pnlHeader.set(page);
            pnlBody.set(page);
            pnlFooter.set(page);
            showCard("normal");
        }
    }


    ///////////////////
    // INNER CLASSES //
    ///////////////////

    private class HelpContentHeaderPanel extends BeanPanel<HelpPage> {


        ////////////
        // FIELDS //
        ////////////

        private DashboardDataPanel pnl;
        private JButton btnNavPrev, btnNavNext;


        //////////////////
        // CONSTRUCTORS //
        //////////////////

        public HelpContentHeaderPanel() {
            Lay.BLtg(this,
                "C", pnl = new DashboardDataPanel(),
                "E", Lay.FL("L",
                    btnNavPrev = Lay.btn(CommonConcepts.PREV, "icon", (ActionListener) e -> uiController.navigatePagePrevious()),
                    btnNavNext = Lay.btn(CommonConcepts.NEXT, "icon", (ActionListener) e -> uiController.navigatePageNext())
                )
            );

            dataModel.addPageRenameListener(e -> set(e.getPage()));
            uiController.addHistoryNavListener(e -> updateNavButtons());
        }


        //////////////
        // MUTATORS //
        //////////////

        public void updateNavButtons() {
            btnNavPrev.setEnabled(uiController.getPageHistoryIndex() > 0);
            btnNavNext.setEnabled(uiController.getPageHistoryIndex() < uiController.getPageHistory().size() - 1);
        }

        @Override
        public void set(HelpPage page) {
            super.set(page);
            DataDescriptor dataDesc = new DataDescriptor()
                .setDatumSeparatorCreator((dl, dr) -> {
                    return dl == null || dr == null ? null : Lay.lb(CommonConcepts.PLAY);
                })
            ;
            for(HelpPage p : page.getPagePath()) {
                dataDesc.addDatum(
                    new DatumDescriptor()
                         .setText(p.getName())
                         .setListener(e -> uiController.select(p))
                );
            }
            pnl.setData(dataDesc);
        }
    }

    private class HelpContentBodyPanel extends BeanPanel<HelpPage> {
        private RTextArea txtHtml;
        private JavaFxBrowserPanel pnlBrowser;
        RButton btnEditSave, btnCancel, btnSetInline, btnSetPath;
        RPanel pnlDocument;
        JLabel lblPage;

        public HelpContentBodyPanel() {
            Lay.BLtg(this,
                "N", Lay.BL(
                    "W", Lay.GBL(
                        lblPage = Lay.lb("", "eb=5l")
                    ),
                    "E", Lay.FL(
                        btnSetInline = Lay.btn(HelpImageModel.SET_INLINE, 2, "ttt=Switch-To-Inline..."),
                        btnSetPath   = Lay.btn(HelpImageModel.SET_CONTENT_DIR, 2, "ttt=Switch-To-Path..."),
                        Lay.hs(5),
                        btnEditSave  = Lay.btn(CommonConcepts.EDIT, 2, "ttt=Edit-Page"),
                        btnCancel    = Lay.btn(CommonConcepts.CANCEL, 2, "visible=false,ttt=Cancel")
                    ),
                    "chtransp,bg=" + Lay.clr(ColorLib.YELLOW_LIGHT)
                ),
                "C", pnlDocument = Lay.CL(
                    "rendered", pnlBrowser = new JavaFxBrowserPanel(),
                    "source", Lay.sp(txtHtml = Lay.txa("", "font=Monospaced"))
                )
            );
            btnEditSave.addActionListener(e -> {
                if(btnEditSave.getToolTipText().contains("Edit")) {
                    showSource();
                } else {
                    savePage();
                    showRendered();
                }
            });
            btnCancel.addActionListener(e -> showRendered());
            btnSetInline.addActionListener(e -> {
                if(!Dialogs.showConfirm(getWindow(), "Are you sure you want to switch this page to inline?", "Switch Mode")) {
                    return;
                }
                lastSetBean.getContent().setHtmlContent("TEMP");
                set(lastSetBean);
            });
            btnSetPath.addActionListener(e -> {
                String path = Dialogs.showInput(getWindow(), "Enter path:", "Set Content Dir Path");
                if(path != null) {
                    lastSetBean.getContent().setHtmlPath(path);
                    set(lastSetBean);
                }
            });
        }
        public void showSource() {
            pnlDocument.showCard("source");
            btnEditSave.setToolTipText("Save Page");
            btnEditSave.setMnemonic('S');
            btnEditSave.setIcon(CommonConcepts.SAVE);
            btnCancel.setVisible(true);
        }
        public void showRendered() {
            pnlDocument.showCard("rendered");
            btnEditSave.setToolTipText("Edit Page");
            btnEditSave.setMnemonic(0);
            btnEditSave.setIcon(CommonConcepts.EDIT);
            btnCancel.setVisible(false);
        }
        public void savePage() {
            String source = txtHtml.getText();
            dataModel.change(lastSetBean, source);
            set(lastSetBean);
            lastSetBean.save();
        }
        @Override
        public void set(HelpPage bean) {
            super.set(bean);

            if(bean.getContent() == null) {
                bean.setContent(new HelpPageContent());
            }

            if(bean.getContent().getHtmlContent() != null) {
                String html = bean.getContent().getHtmlContent();
                pnlBrowser.setHTMLContent(html);
                txtHtml.setText(html);
                btnSetInline.setVisible(false);
                lblPage.setText("<html>Page: <i>(Inline Content)</i></html>");
            } else {
                StandardHelpProvider provider = (StandardHelpProvider) bean.getAlbum().getProvider();
                URL url = provider.getContentResource(bean.getContent().getHtmlPath());
                if(url.getProtocol().equals("file")) {
                    File fileOutput = new File(url.getFile());
                    File fileSource = provider.convertWorkspaceFileOutputToSource(fileOutput);
                    if(!fileOutput.exists()) {
                        if(!fileOutput.getParentFile().exists()) {
                            fileOutput.getParentFile().mkdirs();
                        }
                        FileUtil.touch(fileOutput);
                    }
                    if(!fileSource.exists()) {
                        if(!fileSource.getParentFile().exists()) {
                            fileSource.getParentFile().mkdirs();
                        }
                        FileUtil.touch(fileSource);
                    }
                }
                try {
                    String html = FileUtil.getTextContent(url.openStream());
                    txtHtml.setText(html);
                } catch(IOException e) {
                    e.printStackTrace();
                    dataModel.change(bean, "");
                }
                pnlBrowser.load(url.toString());
                btnSetInline.setVisible(true);
                lblPage.setText("Page: " + bean.getContent().getHtmlPath());
            }
        }
    }

    private class HelpContentFooterPanel extends BeanPanel<HelpPage> {
        public HelpContentFooterPanel() {
            Lay.BLtg(this, "C", Lay.temp("footer panel"));
        }
    }
}
