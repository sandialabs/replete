package finio.ui.fpanel.mlp;

import javax.swing.JPanel;

import replete.plugins.StatelessProcess;

public abstract class LayoutCreator<P extends LayoutParams> extends StatelessProcess<P> {


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public LayoutCreator(P params) {
        super(params);
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract void arrange(JPanel pnl);
}
