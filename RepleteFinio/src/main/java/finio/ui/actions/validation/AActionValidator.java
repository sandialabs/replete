package finio.ui.actions.validation;

import java.util.ArrayList;
import java.util.List;

import finio.core.FUtil;
import finio.ui.app.AppContext;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import finio.ui.world.WorldPanel;
import finio.ui.worlds.WorldContext;
import replete.ui.uiaction.ActionValidator;

public class AActionValidator implements ActionValidator {


    ////////////
    // FIELDS //
    ////////////

    private AppContext ac;
    private int minSel = 1;
    private int maxSel = Integer.MAX_VALUE;
    private boolean viewRequired = true;
    private boolean worldAllowed = true;
    private boolean nonTerminalAllowed = true;
    private boolean managedNonTerminalAllowed = false;   // default off, only few commands need this
    private boolean terminalAllowed = true;
    private boolean allMustBeGood = false;
    private List<SelectionContext> lastValidResults;
    private int selectionReverseDepth = 1;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AActionValidator(AppContext ac) {
        this.ac = ac;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public List<SelectionContext> getLastValidResults() {
        return lastValidResults;
    }

    // Mutators

    public AActionValidator setSelect(int minSel, int maxSel) {
        this.minSel = minSel;
        this.maxSel = maxSel;
        return this;
    }
    public AActionValidator setSingleSelect() {
        minSel = maxSel = 1;
        return this;
    }
    public AActionValidator setWorldAllowed(boolean worldAllowed) {
        this.worldAllowed = worldAllowed;
        return this;
    }
    public AActionValidator setNonTerminalAllowed(boolean nonTerminalAllowed) {
        this.nonTerminalAllowed = nonTerminalAllowed;
        return this;
    }
    public AActionValidator setManagedNonTerminalAllowed(boolean managedNonTerminalAllowed) {
        this.managedNonTerminalAllowed = managedNonTerminalAllowed;
        return this;
    }
    public AActionValidator setTerminalAllowed(boolean terminalAllowed) {
        this.terminalAllowed = terminalAllowed;
        return this;
    }
    public AActionValidator setAllMustBeValid(boolean allMustBeGood) {
        this.allMustBeGood = allMustBeGood;
        return this;
    }
    public AActionValidator setViewRequired(boolean viewRequired) {
        this.viewRequired = viewRequired;
        return this;
    }
    public AActionValidator setSelectionReverseDepth(int selectionReverseDepth) {
        this.selectionReverseDepth = selectionReverseDepth;
        return this;
    }


    /////////////
    // ALLOWED //
    /////////////

    public boolean isActionAllowed(AppContext ac, SelectionContext[] Cs) {
        boolean ok = true;
        int selCount = Cs.length;

        if(selCount < minSel || selCount > maxSel) {
            ok = false;

        } else if(!acceptView(ac)) {
            ok = false;

        } else {
            lastValidResults = new ArrayList<>();
            for(SelectionContext C : Cs) {
                if(accept(ac, C)) {
                    lastValidResults.add(C);
                }
            }

            if(allMustBeGood) {
                if(lastValidResults.size() != Cs.length) {
                    ok = false;
                }

            } else {
                if(lastValidResults.size() == 0 && minSel > 0) {
                    ok = false;
                }
            }
        }
        return ok;
    }
    protected boolean acceptView(AppContext ac) {
        return true;
    }


    protected boolean accept(AppContext ac, SelectionContext C) {
        Object V = C.getV();
        if(V == ac.getSelectedWorld().getW() && worldAllowed ||
                        V != ac.getSelectedWorld().getW() &&
           FUtil.isNonTerminal(V) && nonTerminalAllowed ||
           FUtil.isManagedNonTerminal(V) && managedNonTerminalAllowed ||
           FUtil.isTerminal(V) && terminalAllowed) {
            return true;
        }
        return false;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public boolean isValid(String actionId) {
        WorldContext wc = ac.getSelectedWorld();
        if(wc == null) {
            return false;
        }
        if(!viewRequired) {
            return true;
        }
        WorldPanel pnlWorld = wc.getWorldPanel();              // Cannot be null
        ViewPanel pnlView = pnlWorld.getSelectedView();
        if(pnlView == null) {
            return false;
        }
        SelectionContext[] Cs = pnlView.getSelectedValues(selectionReverseDepth);
        return isActionAllowed(ac, Cs);
    }
}
