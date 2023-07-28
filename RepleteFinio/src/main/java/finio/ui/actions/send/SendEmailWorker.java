package finio.ui.actions.send;

import finio.core.NonTerminal;
import finio.core.impl.FMap;
import finio.renderers.map.StandardAMapRenderer;
import finio.ui.actions.FWorker;
import finio.ui.actions.send.addl.SendEmailDetails;
import finio.ui.actions.send.addl.SendEmailDialog;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.mail.Mailer;

public class SendEmailWorker extends FWorker<SendEmailDetails, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public SendEmailWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected SendEmailDetails gather() {
        SendEmailDialog dlg = new SendEmailDialog(ac.getWindow(), ac);
        StandardAMapRenderer renderer = new StandardAMapRenderer();
        String S = renderer.render(null, getValidSelected().get(0).getV());
        dlg.setBody(S);
//        WorldContext wc = new WorldContext(ac)
//            .setWorld((World) Cs[0].getV())
//            .setDirty(false)
//        ;
        dlg.setVisible(true);
        return dlg.getResult() == SendEmailDialog.SEND ? dlg.getDetails() : null;
    }

    @Override
    protected boolean proceed(SendEmailDetails gathered) {
        return gathered != null;
    }

    @Override
    protected Void background(SendEmailDetails details) throws Exception {
        if(!ac.getConfig().isSuppressEmails()) {
            Mailer.sendEmail(
                details.getFrom(),
                details.getTo(),
                details.getCc(),
                details.getBcc(),
                details.getSubject(),
                details.getBody(),
                details.getMimeType()
            );
        }
        long sentDate = System.currentTimeMillis();
        NonTerminal W = wc.getW();
        NonTerminal Mws = (NonTerminal) W.getAndSet("Finio Client Workspace", FMap.A());
        NonTerminal Mem = (NonTerminal) Mws.getAndSet("Sent Emails", FMap.A());
        Object K = Mem.getNextAvailableKey("");
        NonTerminal Mem2 = (NonTerminal) Mem.getAndSet(K, FMap.A());
        Mem2.put("Date Sent", sentDate);
        Mem2.put("From", details.getFrom());
        Mem2.put("To", details.getTo().toString());
        if(details.getCc() != null) {
            Mem2.put("CC", details.getCc().toString());
        }
        if(details.getBcc() != null) {
            Mem2.put("BCC", details.getBcc().toString());
        }
        Mem2.put("Subject", details.getSubject());
        Mem2.put("Body", details.getBody());
        Mem2.put("Body MIME Type", details.getMimeType());
        return null;
    }

    @Override
    public String getActionVerb() {
        return "sending the message";
    }
}
