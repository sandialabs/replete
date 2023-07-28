package finio.ui.actions.send.addl;

import java.util.List;

public class SendEmailDetails {


    ////////////
    // FIELDS //
    ////////////

    private String from;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private String mimeType;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public String getFrom() {
        return from;
    }
    public List<String> getTo() {
        return to;
    }
    public List<String> getCc() {
        return cc;
    }
    public List<String> getBcc() {
        return bcc;
    }
    public String getSubject() {
        return subject;
    }
    public String getBody() {
        return body;
    }
    public String getMimeType() {
        return mimeType;
    }

    // Mutators

    public SendEmailDetails setFrom(String from) {
        this.from = from;
        return this;
    }
    public SendEmailDetails setTo(List<String> to) {
        this.to = to;
        return this;
    }
    public SendEmailDetails setCc(List<String> cc) {
        this.cc = cc;
        return this;
    }
    public SendEmailDetails setBcc(List<String> bcc) {
        this.bcc = bcc;
        return this;
    }
    public SendEmailDetails setSubject(String subject) {
        this.subject = subject;
        return this;
    }
    public SendEmailDetails setBody(String body) {
        this.body = body;
        return this;
    }
    public SendEmailDetails setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
}
