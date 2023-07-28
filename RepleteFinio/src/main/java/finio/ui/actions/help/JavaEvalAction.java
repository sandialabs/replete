package finio.ui.actions.help;

import finio.plugins.extpoints.DefaultFinioUiAction;
import finio.ui.actions.FWorker;
import finio.ui.actions.FWorkerActionListener;
import finio.ui.app.AppContext;
import finio.ui.worlds.WorldContext;
import replete.ui.eval.JavaEvalFrame;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.uiaction.MenuBarActionDescriptor;
import replete.ui.uiaction.UIActionListener;
import replete.ui.uiaction.UIActionMap;

public class JavaEvalAction extends DefaultFinioUiAction {

    @Override
    public void register(AppContext ac) {

        UIActionMap map = ac.getActionMap();

        UIActionListener listener = new FWorkerActionListener(ac) {
            @Override
            public FWorker create() {
                return new LocalWorker(ac, ac.getSelectedWorld(), "Java Eval");
            }
        };

        map.createAction("java-eval", listener)
            .addDescriptor(
                new MenuBarActionDescriptor()
                    .setPath("help")
                    .setText("&Java Eval...")
                    .setIcon(CommonConcepts.PLUGIN));

    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class LocalWorker extends FWorker<Void, Void> {


        /////////////////
        // CONSTRUCTOR //
        /////////////////

        public LocalWorker(AppContext ac, WorldContext wc, String name) {
            super(ac, wc, name);
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        protected Void gather() {
            JavaEvalFrame fraEval = new JavaEvalFrame();
            fraEval.setVisible(true);
            return null;
        }
    }
}
