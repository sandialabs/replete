package replete.ui.sdplus.panels;

import java.util.List;
import java.util.Set;

/**
 * Model for a multi-select enumerated scale.  The
 * base class fulfills all the requirements by maintaining
 * a selected values set.
 *
 * @author Derek Trumbo
 */

public class EnumScaleMultiPanelModel extends EnumScaleBasePanelModel {

    //////////////////
    // Constructors //
    //////////////////

    // Starts with all objects selected.
    public EnumScaleMultiPanelModel(String k, String nm, String un, String nt, List<Object> av) {
        super(k, nm, un, nt, av);
    }

    public EnumScaleMultiPanelModel(String k, String nm, String un, String nt, List<Object> av, Set<Object> sv) {
        super(k, nm, un, nt, av, sv);
    }
}
