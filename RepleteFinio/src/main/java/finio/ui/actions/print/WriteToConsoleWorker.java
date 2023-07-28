package finio.ui.actions.print;

import finio.renderers.map.StandardAMapRenderer;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class WriteToConsoleWorker extends FWorker<Void, String> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WriteToConsoleWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected String background(Void gathered) throws Exception {
        StandardAMapRenderer renderer = new StandardAMapRenderer();
        StringBuilder buffer = new StringBuilder();
        for(SelectionContext C : getValidSelected()) {
            buffer.append(renderer.render(null, C.getV()));
            buffer.append('\n');
        }
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    @Override
    public String getActionVerb() {
        return "writing to the console";
    }

    @Override
    protected void completeInner(String result) {
        ac.sendToConsole(result);
    }
}
