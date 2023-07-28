package finio.platform.exts.view.textonly.ui;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import finio.plugins.extpoints.View;
import finio.renderers.map.StandardAMapRenderer;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.view.SelectionContextSegment;
import finio.ui.view.ViewPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.lay.Lay;
import replete.ui.text.editor.REditor;

public class TextOnlyViewPanel extends ViewPanel {

    private REditor txt;

    public TextOnlyViewPanel(AppContext ac, WorldContext wc, Object K, Object V, View view) {
        super(ac, wc, K, V, view);

        Lay.BLtg(this,
            "C", txt = Lay.ed("", "ruler=true,editable=false")
        );
        updateFromValue();

        ac.getActionMap().addAnyActionListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateFromValue();
            }
        });
    }

    private void updateFromValue() {
        StandardAMapRenderer renderer = new StandardAMapRenderer();
        String S = renderer.render(null, V);
        txt.setText(S);
    }

    @Override
    public void addAnyActionListener(ChangeListener listener) {
        updateFromValue();
    }

    @Override
    public void addSelectedListener(ChangeListener listner) {
    }

    @Override
    public void removeSelectedListener(ChangeListener listener) {
    }

    @Override
    public SelectionContext[] getSelectedValues(int reverseDepth) {
        return new SelectionContext[] {
            new SelectionContext().addSegment(new SelectionContextSegment(getK(), getV()))
        };
    }
}
