package finio.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;

import finio.core.NonTerminal;
import finio.ui.app.AppContext;
import finio.ui.images.FinioImageModel;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.worlds.WorldContext;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.windows.Dialogs;
import replete.ui.worker.RWorker;

public abstract class FWorker<Tgath, Tres> extends RWorker<Tgath, Tres> {


    ////////////
    // FIELDS //
    ////////////

    protected AppContext ac;
    protected WorldContext wc;
    protected String name;
    private List<SelectRequest> selectRequests;
    private List<ExpandRequest> expandRequests;
    protected boolean shouldEdit;
    private boolean shift;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FWorker(AppContext ac, WorldContext wc, String name) {
        this.ac = ac;
        this.wc = wc;
        this.name = name;
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public AppContext getAppContext() {
        return ac;
    }
    public String getName() {
        return name;
    }
    public List<SelectRequest> getSelectRequests() {
        return selectRequests;
    }
    public List<ExpandRequest> getExpandRequests() {
        return expandRequests;
    }
    public boolean shouldEdit() {
        return shouldEdit;
    }
    public boolean isEditShift() {
        return shift;
    }

    // Mutators

    // Select
    protected void select(SelectRequest selectRequest) {
        if(selectRequests == null) {
            selectRequests = new ArrayList<>();
        }
        selectRequests.add(selectRequest);
    }

    // Expand
    protected void expand(ExpandRequest expandRequest) {
        if(expandRequests == null) {
            expandRequests = new ArrayList<>();
        }
        expandRequests.add(expandRequest);
    }

    // Edit
    protected void setShouldEdit(boolean s) {
        shouldEdit = true;
        shift = s;
    }


    //////////
    // MISC //
    //////////

    protected String getDesiredKeyName(String Kproposed, NonTerminal Mcontext) {
        if(true) {
            return Kproposed;
        }

        // Initialize input loop.
        boolean reEnter = true;
        boolean cancel = false;

        // Continue to gather input from the user until either
        // a valid key name has been chosen or the process
        // is canceled.
        while(reEnter && !cancel) {
            Kproposed = Dialogs.showInput(ac.getWindow(),
                "Enter the desired key for this import within the selected context:",
                getName() + ": Key Selection", Kproposed);

            // If they canceled the dialog box, break the loop.
            if(Kproposed == null) {
                cancel = true;
                break;
            }

            // Check whether or not the key already exists in the context map.
            if(Mcontext != null && Mcontext.containsKey(Kproposed)) {
                String YES = "&Yes, Overwrite";
                String NO = "&No, Re-enter";
                String CANCEL = "Cancel";
                Map<String, Icon> buttonIcons = new HashMap<>();
                buttonIcons.put(YES, ImageLib.get(CommonConcepts.ACCEPT));
                buttonIcons.put(NO, ImageLib.get(FinioImageModel.REENTER));
                buttonIcons.put(CANCEL, ImageLib.get(CommonConcepts.CANCEL));

                // Ask the user
                int response = Dialogs.showMulti(ac.getWindow(),
                    "The desired key already exists in the selected context.  Do you wish to overwrite its value?",
                    getName() + ": Key Already Exists",
                    new String[] {YES, NO, CANCEL}, -1, buttonIcons);

                if(response == 0) {
                    reEnter = false;         // Overwrite chosen, no re-entering is required.
                } else if(response == 1) {
                    reEnter = true;          // Re-entering chosen.
                } else {
                    cancel = true;           // Cancel the process.
                }

            // No re-entering is required.
            } else {
                reEnter = false;
            }
        }

        return cancel ? null : Kproposed;
    }


    protected List<SelectionContext> validSelected;
    public List<SelectionContext> getValidSelected() {
        return validSelected;
    }
    public void setValidSelected(List<SelectionContext> validSelected) {
        this.validSelected = validSelected;
    }

    public String getActionVerb() {
        return null;
    }

    @Override
    protected void complete() {
        String verb = getActionVerb();
        if(verb == null) {
            verb = "completing the action";
        }

        try {
            Tres result = getResult();

            // This mimics how getResult is implemented in RWorker.
            try {
                completeInner(result);
            } catch(Exception e) {
                error = e;
                throw e;
            }

        } catch(ExecutionException e) {
            Dialogs.showDetails(ac.getWindow(),
                "An error occurred " + verb + ".", name + " Error", e.getCause());

        } catch(Exception e) {
            Dialogs.showDetails(ac.getWindow(),
                "An error occurred " + verb + ".", name + " Error", e);
        }
    }

    protected void completeInner(Tres result) {}
}
