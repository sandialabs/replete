package finio.ui.actions.transform;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import finio.core.NonTerminal;
import finio.ui.actions.FWorker;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectAction;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;

public class ExtractFileBytesWorker extends FWorker<Void, Void> {


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExtractFileBytesWorker(AppContext ac, WorldContext wc, String name) {
        super(ac, wc, name);
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    protected Void background(Void gathered) throws Exception {

        for(SelectionContext C : getValidSelected()) {
            NonTerminal Mparent = (NonTerminal) C.getParentV();
            Object V = C.getV();

            File F = (File) V;

            Path path = F.toPath();
            byte[] Fb = Files.readAllBytes(path);

            Object K = C.getK();
            Mparent.put(C.getK(), Fb);

            expand(
                new ExpandRequest()
                    .setContext(C)
                    .setAction(SelectAction.SIBLING)
                    .setArgs(K)
            );
        }

        return null;
    }

    @Override
    public String getActionVerb() {
        return "extracting the bytes";
    }
}
