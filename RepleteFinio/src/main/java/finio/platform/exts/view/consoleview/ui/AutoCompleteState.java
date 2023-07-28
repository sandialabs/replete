package finio.platform.exts.view.consoleview.ui;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteState {


    ////////////
    // FIELDS //
    ////////////

    private String fixedPrefix;
    private String matchedPrefix;
    private String singleMatchSep;
    private List<String> matching = new ArrayList<String>();
    private String userMessage;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AutoCompleteState(String fixedPrefix, String matchedPrefix, String singleMatchSep, List<String> matching, String userMessage) {
        this.fixedPrefix = fixedPrefix;
        this.matchedPrefix = matchedPrefix;
        this.matching = matching;
        this.singleMatchSep = singleMatchSep;
        this.userMessage = userMessage;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public String getFixedPrefix() {
        if(fixedPrefix == null) {
            return "";
        }
        return fixedPrefix;
    }
    public String getMatchedPrefix() {
        if(matchedPrefix == null) {
            return "";
        }
        return matchedPrefix;
    }
    public String getSingleMatchSep() {
        return singleMatchSep;
    }
    public List<String> getMatching() {
        return matching;
    }
    public String getUserMessage() {
        return userMessage;
    }
}
