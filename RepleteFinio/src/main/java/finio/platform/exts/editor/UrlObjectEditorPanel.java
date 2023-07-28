package finio.platform.exts.editor;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

import javax.swing.JPopupMenu;

import finio.ui.actions.validation.AActionValidator;
import finio.ui.actions.validation.SpecificTypeValueActionValidator;
import finio.ui.app.AppContext;
import gov.sandia.orbweaver.OrbweaverAppMain;
import gov.sandia.orbweaver.ui.OrbweaverFrame;
import gov.sandia.orbweaver.ui.images.OrbweaverImageModel;
import replete.ui.button.RButton;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.uiaction.PopupMenuActionDescriptor;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;
import replete.ui.uiaction.UIActionPopupMenu;
import replete.util.OsUtil;
import replete.web.UrlUtil;

public class UrlObjectEditorPanel extends StringObjectEditorPanel {


    ////////////
    // FIELDS //
    ////////////

    // Model

    private URL U;
    private URI Ui;

    // UI

    private AppContext ac;
    private RButton btnOptions;
    private ROActionMap actionMap;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public UrlObjectEditorPanel(AppContext ac) {
        this.ac = ac;

        actionMap = new ROActionMap();

        btnOptions = Lay.btn(CommonConcepts.OPTIONS, "icon");
        btnOptions.setToolTipText("URL Actions");
        add(Lay.p(btnOptions, "eb=2l"));

        btnOptions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                JPopupMenu mnuPopup = new UIActionPopupMenu(actionMap);
                mnuPopup.show(btnOptions, e.getX(), e.getY());
            }
        });
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void setObject(Object O) {
        super.setObject(O);

        if(O instanceof URL) {
            U = (URL) O;
            Ui = null;
        } else {
            U = null;
            Ui = (URI) O;
        }
    }

    @Override
    protected String convertObjectToString(Object O) {
        return O.toString();
    }

    @Override
    public Object getObject() {
        if(U != null) {
            return UrlUtil.url(txt.getText());
        }
        return UrlUtil.uri(txt.getText());
    }

    @Override
    public boolean isValidState() {
        return true;                       // TODO: No validation performed here yet
    }

    @Override
    public boolean isReturnsNewObject() {
        return true;                       // File objects are immutable
    }

    @Override
    public boolean allowsEdit() {
        return true;
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    public class ATreeActionListener implements UIActionListener {
        private Runnable runnable;
        public ATreeActionListener(Runnable runnable) {
            this.runnable = runnable;
        }
        @Override
        public void actionPerformed(ActionEvent e, UIAction action) {
            runnable.run();
        }
    }

    private class ROActionMap extends UIActionMap {
        public ROActionMap() {

            // Open Actions

            createAction("open")
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setText("&Open")
                        .setLabelMenu(true));

            Runnable runnable = new Runnable() {
                public void run() {
                    if(getObject() instanceof URL) {
                        OsUtil.openWebpage((URL) getObject());
                    } else {
                        OsUtil.openWebpage((URI) getObject());
                    }
                }
            };
            AActionValidator validator = new SpecificTypeValueActionValidator(ac, URL.class, URI.class);
            createAction("sys-browser", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("With System Browser")
                        .setIcon(CommonConcepts.SYSTEM));

            runnable = new Runnable() {
                public void run() {
                    OrbweaverFrame frame = OrbweaverAppMain.getOrbFrame();
                    if(getObject() instanceof URL) {
                        frame.getModel().addBrowserTab((URL) getObject());
                    }
                    frame.setVisible(true);
                }
            };
            validator = new SpecificTypeValueActionValidator(ac, URL.class, URI.class);
            createAction("orbweaver-browser", new ATreeActionListener(runnable), validator)
                .addDescriptor(
                    new PopupMenuActionDescriptor()
                        .setPath("open")
                        .setText("With Orbweaver")
                        .setIcon(OrbweaverImageModel.ORBWEAVER_LOGO));
        }
    }
}
