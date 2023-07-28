package replete.ui.windows;

import java.util.Arrays;

public class ExceptionSubmissionDetails {


    ////////////
    // FIELDS //
    ////////////

    private String smtpHostIp;
    private String from;
    private String[] to;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getSmtpHostIp() {
        return smtpHostIp;
    }
    public String getFrom() {
        return from;
    }
    public String[] getTo() {
        return to;
    }

    // Accessors (Computed)

    public boolean hasAllFields() {
        return smtpHostIp != null && from != null && to != null;
    }

    // Mutators

    public ExceptionSubmissionDetails setSmtpHostIp(String smtpHostIp) {
        this.smtpHostIp = smtpHostIp;
        return this;
    }
    public ExceptionSubmissionDetails setFrom(String from) {
        this.from = from;
        return this;
    }
    public ExceptionSubmissionDetails setTo(String[] to) {
        this.to = to;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        return "ExceptionSubmissionDetails [smtpHostIp=" + smtpHostIp + ", from=" + from + ", to=" +
                        Arrays.toString(to) + "]";
    }
}
