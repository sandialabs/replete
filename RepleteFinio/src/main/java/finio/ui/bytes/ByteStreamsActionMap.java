
package finio.ui.bytes;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import finio.ui.actions.imprt.ImportTextWorker;
import finio.ui.actions.imprt.ImportTextWorker.Format;
import finio.ui.multidlg.FileInputSourcePanel;
import finio.ui.multidlg.InputBundle;
import finio.ui.multidlg.InputBundleValidationProblem;
import finio.ui.multidlg.InputBundleValidator;
import finio.ui.multidlg.MultiInputChooserDialog;
import finio.ui.multidlg.WebInputSourcePanel;
import gov.sandia.webcomms.http.Http;
import gov.sandia.webcomms.http.options.HttpRequestOptions;
import gov.sandia.webcomms.http.ui.HttpRequestOptionsDialogSmall;
import replete.io.FileUtil;
import replete.ui.GuiUtil;
import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class ByteStreamsActionMap extends UIActionMap {


    ////////////
    // FIELDS //
    ////////////

    // Parameters that could be persistence on app close.
    private HttpRequestOptions requestOptions = new HttpRequestOptions();
    private String proxyHost;
    private int proxyPort;
    private List<String> webRecentUrls = new ArrayList<>();
    private String lastUrl = "";


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ByteStreamsActionMap(ByteStreamsFrame parent) {
        proxyHost = Http.getInstance().getProxyHost();
        proxyPort = Http.getInstance().getProxyPort();

        createAction("file")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&File")
            );

        UIActionListener listener = new UIActionListener() {
            public void doStuff(String name, String filterName, String filterExt,
                                Format format, InputBundleValidator validator) {

                MultiInputChooserDialog dialog =
                    new MultiInputChooserDialog(parent, name);
                dialog.setIcon(CommonConcepts.BINARY);
                dialog.setValidator(validator);

                RFileChooser chooser = RFileChooser.getChooser(true);
                RFileChooser.whiteCombos(chooser);
                GuiUtil.addMnemonics(chooser, RFileChooser.basicCaptions);
                RFilterBuilder builder = new RFilterBuilder(chooser, false);
                if(filterName != null) {
                    builder.append(filterName, filterExt);
                }
                builder.appendAcceptAllFilter();

                FileInputSourcePanel pnlFileInput = dialog.getInputPanel(FileInputSourcePanel.class);
                pnlFileInput.setChooser(chooser);

                WebInputSourcePanel pnlWebInput = dialog.getInputPanel(WebInputSourcePanel.class);
                pnlWebInput.setRecentList(webRecentUrls);      // TODO: why is web recent strings not persisting?
                pnlWebInput.setUrl(lastUrl);

                dialog.setVisible(true);
                if(dialog.getResult() == MultiInputChooserDialog.ACCEPT) {
                    for(InputBundle bundle : dialog.getDataBundles()) {
                        if(bundle.getFile() != null &&
                                FileUtil.isReadableFile(bundle.getFile()) &&
                                bundle.getBytes() == null) {
                            bundle.setBytes(FileUtil.readBytes(bundle.getFile()));
                        }
                        parent.addStream(
                            bundle.getBytes(),
                            bundle.getFile(),
                            bundle.getUrl(),
                            bundle.getText()
                        );
                    }
                }
                lastUrl = pnlWebInput.getUrl();
            }
            @Override
            public void actionPerformed(ActionEvent e, UIAction action) {
                doStuff("Import Byte Stream",
                    null, null, ImportTextWorker.Format.JSON,
                    new InputBundleValidator() {
                        public InputBundleValidationProblem[] validate(InputBundle[] bundles) {
                            List<InputBundleValidationProblem> problems = new ArrayList<>();
                            for(InputBundle bundle : bundles) {
                                // Nothing to validate for now, any bytes are welcome.
                            }
                            return problems.toArray(new InputBundleValidationProblem[0]);
                        }
                    }
                );
            }
        };

        createAction("import", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("file")
                    .setText("&Import...")
                    .setIcon(CommonConcepts.IMPORT)
                    .setSepGroup("import"));

        createAction("exit", (e, a) -> parent.close())
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("file")
                    .setText("E&xit")
                    .setIcon(CommonConcepts.EXIT)
                    .setSepGroup("exit"));

        createAction("tools")
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setText("&Tools")
            );

        listener = new UIActionListener() {
            @Override
            public void actionPerformed(ActionEvent e, UIAction action) {
                HttpRequestOptionsDialogSmall dlg = new HttpRequestOptionsDialogSmall(
                    parent, requestOptions, proxyHost, proxyPort);
                dlg.setVisible(true);
                if(dlg.getResult() == HttpRequestOptionsDialogSmall.SET) {
                    proxyHost = dlg.getProxyHost();
                    proxyPort = dlg.getProxyPort();
                    if(proxyHost == null) {
                        Http.getInstance().clearProxy();
                    } else {
                        Http.getInstance().setProxy(proxyHost, proxyPort);
                    }
                    // Dialog's source request options dialog edited in place.
                }
            }
        };

        createAction("http-options", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("tools")
                    .setText("HTTP Request &Options...")
                    .setIcon(CommonConcepts.OPTIONS));
    }

}
