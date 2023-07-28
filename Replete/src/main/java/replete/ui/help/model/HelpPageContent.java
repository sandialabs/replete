package replete.ui.help.model;

import replete.text.StringUtil;

public class HelpPageContent {


    ////////////
    // FIELDS //
    ////////////

    private String htmlContent = "";    // Only one of these can be non-null at any given time (basically a "union")
    private String htmlPath;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getHtmlContent() {
        return htmlContent;
    }
    public String getHtmlPath() {
        return htmlPath;
    }

    // Accessors (Computed)

    public boolean isBlank() {
        return StringUtil.isBlank(htmlContent) && StringUtil.isBlank(htmlPath);
    }

    // Mutators

    public HelpPageContent setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
        htmlPath = null;
        return this;
    }
    public HelpPageContent setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
        htmlContent = null;
        return this;
    }
}
