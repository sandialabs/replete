package finio.ui.multidlg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;

import gov.sandia.webcomms.http.Http;
import gov.sandia.webcomms.http.options.HttpRequestOptions;
import gov.sandia.webcomms.http.rsc.HttpResource;
import gov.sandia.webcomms.http.ui.HttpRequestOptionsDialogSmall;
import replete.text.StringUtil;
import replete.ui.fc.RecentListContext;
import replete.ui.fc.RecentListHelper;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.lay.Lay;
import replete.ui.text.DocumentChangeListener;
import replete.ui.text.RLabel;
import replete.ui.text.RTextField;
import replete.ui.windows.escape.EscapeDialog;
import replete.ui.worker.RWorker;

public class WebInputSourcePanel extends InputSourcePanel implements RecentListContext<String> {


    ////////////
    // FIELDS //
    ////////////

    public static final int MAX_PREVIEW_CHARS = 20_000;

    private EscapeDialog parent;
    private JButton btnAccept;
    private JButton btnFetch;
    private RTextField txtUrl;
    private JTextArea txtContent;
    private RLabel lblStatus;
    private String fetchedText = "";
    private byte[] fetchedBytes = new byte[0];
    private HttpRequestOptions requestOptions = new HttpRequestOptions();
    private String proxyHost;     // TODO: Remove these some day
    private int proxyPort;

    // Recent

    private List<String> recentList = new ArrayList<>();


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WebInputSourcePanel(EscapeDialog parent) {
        this.parent = parent;
        JButton btnOptions, btnCancel;

        proxyHost = Http.getInstance().getProxyHost();
        proxyPort = Http.getInstance().getProxyPort();

        Lay.BLtg(this,
            "C", Lay.BL(
                "N", Lay.BL(
                    "W", Lay.lb("URL:", CommonConcepts.LINK),
                    "C", txtUrl = Lay.tx("", "selectall"),
                    "E", Lay.FL(
                        Lay.p(btnOptions = Lay.btn(CommonConcepts.OPTIONS, 2), "eb=5r"),
                        btnFetch = Lay.btn("&Fetch", WebInputSourceImageModel.FETCH,
                            "htext=left"
                        ),
                        "nogap"
                    ),
                    "hgap=5"
                ),
                "C", Lay.BL(
                    "C", Lay.sp(txtContent = Lay.txa("", "editable=false")),
                    "S", lblStatus = Lay.lb("", "fg=blue,bold"),
                    "eb=10t"
                ),
                "S", Lay.FL("R",
                    btnAccept = Lay.btn("&Accept", CommonConcepts.ACCEPT),
                    Lay.p(btnCancel = Lay.btn("&Cancel", CommonConcepts.CANCEL), "eb=5l"),
                    "gap=0,eb=10t"
                ),
                "eb=11"
            )
        );

        btnOptions.setToolTipText("Request Options...");
        txtUrl.addChangeListener(new DocumentChangeListener() {
            @Override
            public void documentChanged(DocumentEvent e) {
                parent.setDefaultButton(btnFetch);
            }
        });
        btnFetch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String urlText = txtUrl.getTrimmed();
                if(!urlText.matches("^[a-z]+://.*$")) {
                    urlText = "http://" + urlText;
                    txtUrl.setText(urlText);
                }
                doUrlGet();
            }
        });

        btnAccept.addActionListener(e -> fireAcceptNotifier());
        btnCancel.addActionListener(e -> fireCancelNotifier());

        btnOptions.addActionListener(e -> {
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
                // Dialog's source request options edited in place.
            }
        });
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getUrl() {
        return txtUrl.getText();
    }
    public HttpRequestOptions getRequestOptions() {
        return requestOptions;
    }

    // Mutators

    public void setUrl(String url) {
        txtUrl.setText(url);
    }
    public void setHttpRequestOptions(HttpRequestOptions requestOptions) {
        this.requestOptions = requestOptions;
    }

    private void doUrlGet() {
        parent.waitOn();
        btnFetch.setEnabled(false);

        Lay.hn(lblStatus, "fg=blue");
        lblStatus.setText("Working...");
        lblStatus.setIcon(CommonConcepts.INFO);

        String url = txtUrl.getTrimmed();
        if(!recentList.contains(url)) {
            addRecentLink(url);
        }
        updateRecentPanel();

        RWorker<Void, GetResult> worker = new RWorker<Void, GetResult>() {

            @Override
            protected GetResult background(Void gathered) throws Exception {
                String errorMessage = null;
                HttpResource resource = Http.getInstance().doGet(url, requestOptions);
                if(resource.isError()) {
                    Throwable ex = resource.getException();
                    if(ex != null) {
                        errorMessage = ex.getMessage() + " [" + ex.getClass().getName() + "]";
                        errorMessage = errorMessage.trim();
                    } else {
                        errorMessage = "HTTP Response Code <u>" + resource.getResponseCode() + "</u>";
                    }
                }
                return new GetResult(resource, errorMessage);
            }

            @Override
            protected void complete() {
                GetResult result;
                try {
                    result = get();
                } catch(Exception e) {
                    result = new GetResult(null, e.getMessage() + " [" + e.getClass().getName() + "]");
                }

                HttpResource resource = result.resource;
                if(resource != null && resource.hasContent()) {
                    fetchedText = resource.getContentAsString();
                    fetchedBytes = resource.getContent();
                } else {
                    fetchedText = "";
                    fetchedBytes = new byte[0];
                }
                String errorMessage = result.error;
                if(errorMessage == null) {
                    if(fetchedText.length() > MAX_PREVIEW_CHARS) {
                        Lay.hn(lblStatus, "fg=blue");
                        lblStatus.setText(
                            "<html>Text shown curtailed to <u>" +
                            StringUtil.commas(MAX_PREVIEW_CHARS) + "</u> characters (<u>" +
                            StringUtil.commas(fetchedText.length() - MAX_PREVIEW_CHARS) +
                            "</u> not shown).  Text cannot be edited before acceptance.</html>"
                        );
                        lblStatus.setIcon(CommonConcepts.WARNING);
                        txtContent.setEditable(false);
                        txtContent.setText(
                            fetchedText.substring(0, MAX_PREVIEW_CHARS) +
                            "\n--- CURTAILED ---");
                        txtUrl.focus();
                        txtUrl.selectAll();
                    } else {
                        Lay.hn(lblStatus, "fg=306B3A");
                        lblStatus.setText("<html>All text shown (<u>" +
                            StringUtil.commas(fetchedText.length()) + "</u> characters).  " +
                            "Text may be edited before acceptance.</html>"
                        );
                        txtContent.setEditable(true);
                        txtContent.setText(fetchedText);
                        txtContent.requestFocusInWindow();
                        txtContent.setCaretPosition(0);
                    }
                    parent.setDefaultButton(btnAccept);
                } else {
                    Lay.hn(lblStatus, "fg=red");
                    lblStatus.setText("<html>ERROR: " + errorMessage + "</html>");
                    lblStatus.setIcon(CommonConcepts.EXCEPTION);
                    txtContent.setEditable(false);
                    txtContent.setText(fetchedText);
                }
                parent.waitOff();

                btnFetch.setEnabled(true);
                if(recentList.contains(url)) {
                    addRecentLink(url);
                }
                updateRecentPanel();
            }
        };

        worker.execute();
    }

    @Override
    protected void postActivate() {
        if(txtContent.getText().isEmpty()) {
            parent.setDefaultButton(btnFetch);
        } else {
            parent.setDefaultButton(btnAccept);
        }
    }

    @Override
    protected InputBundle[] getDataBundles() {
        URL url = null;
        try {
            url = new URL(txtUrl.getText());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new InputBundle[] {
            new InputBundle()
                .setUrl(url)
                .setText(
                    txtContent.isEditable() ?
                        txtContent.getText() :
                        fetchedText)
                .setBytes(
                    txtContent.isEditable() ?
                        txtContent.getText().getBytes(StandardCharsets.UTF_8) :
                        fetchedBytes)
        };
    }

    @Override
    protected void cleanUp() {
        // Do nothing
    }

    @Override
    public String getTitle() {
        return "Web";
    }

    @Override
    public ImageIcon geIcon() {
        return ImageLib.get(CommonConcepts.INTERNET);
    }

    @Override
    public boolean isLinkClickable(String url) {
        return btnFetch.isEnabled();
    }
    @Override
    public void linkClicked(String url) {
        txtUrl.setText(url);
        doUrlGet();
    }
    @Override
    public String getLinkNamePlural() {
        return "URLs";
    }
    @Override
    public List<String> getRecentList() {
        return recentList;
    }
    @Override
    public void setRecentList(List<String> urls) {
        recentList = urls;
        updateRecentPanel();
    }
    @Override
    public void addRecentLink(String url) {
        recentList.remove(url);
        recentList.add(0, url);
        updateRecentPanel();
    }
    private void updateRecentPanel() {
        RecentListHelper.update(this, this);
    }

    @Override
    public void focus() {
        txtUrl.focus();
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class GetResult {
        private HttpResource resource;
        private String error;
        public GetResult(HttpResource resource, String error) {
            this.resource = resource;
            this.error = error;
        }
    }
}
