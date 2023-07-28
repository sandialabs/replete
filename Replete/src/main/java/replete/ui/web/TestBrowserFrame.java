package replete.ui.web;

import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JTextField;

import replete.ui.fc.RFileChooser;
import replete.ui.fc.RFilterBuilder;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.Dialogs;
import replete.ui.windows.escape.EscapeFrame;

public class TestBrowserFrame extends EscapeFrame {


    ////////////
    // FIELDS //
    ////////////

    private JTextField txtUrl;
    private JavaFxBrowserPanel pnlBrowser;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public TestBrowserFrame() {
        super("Browser Test");

        Lay.BLtg(this,
            "N", Lay.BL(
                "C", txtUrl = Lay.tx("https://google.com", "selectall"),
                "E", Lay.FL(
                    Lay.btn("Toggle JavaScript", (ActionListener) e -> toggleJavaScript()),
                    Lay.btn("&Set Inline HTML", (ActionListener) e -> setInlineHtml()),
                    Lay.btn("&Open HTML File...", CommonConcepts.OPEN, (ActionListener) e -> openHtmlFile())
                )
            ),
            "C", pnlBrowser = new JavaFxBrowserPanel(),
            "size=[800,800],bg=100,center=2"
        );

        txtUrl.requestFocusInWindow();
        txtUrl.addActionListener(e ->
            pnlBrowser.navigate(txtUrl.getText())
        );

        setVisible(true);
    }

    private void setInlineHtml() {
        String input = Dialogs.showInput(this, "Enter your HTML:");
        if(input != null) {
            pnlBrowser.setHTMLContent(input);
        }
    }

    // https://stackoverflow.com/questions/35703884/trying-to-load-a-local-page-into-javafx-webengine
    private void openHtmlFile() {
        RFileChooser chooser = RFileChooser.getChooser("Select HTML File");
chooser.setCurrentDirectory(new File("C:\\Users\\dtrumbo\\Desktop\\Avondale Help"));
        RFilterBuilder builder = new RFilterBuilder(chooser, false);
        builder.append("HTML Web Page (*.html, *.htm)", "html", "htm");
        if(chooser.showOpen(this)) {
            File selected = chooser.getSelectedFileResolved();
            pnlBrowser.load(selected.toURI().toString());
        }
    }

    private void toggleJavaScript() {
        pnlBrowser.setJavaScriptEnabled(!pnlBrowser.isJavaScriptEnabled());
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "proxy.sandia.gov");
        System.setProperty("http.proxyPort", "80");
        System.setProperty("https.proxyHost", "proxy.sandia.gov");
        System.setProperty("https.proxyPort", "80");
        TestBrowserFrame f = new TestBrowserFrame();
        f.setVisible(true);
    }
}
