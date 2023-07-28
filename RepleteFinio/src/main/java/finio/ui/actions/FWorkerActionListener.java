package finio.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import finio.ui.actions.validation.AActionValidator;
import finio.ui.app.AppContext;
import finio.ui.view.ExpandRequest;
import finio.ui.view.SelectRequest;
import finio.ui.view.SelectionContext;
import finio.ui.view.ViewPanel;
import replete.ui.uiaction.ActionValidator;
import replete.ui.uiaction.UIAction;
import replete.ui.uiaction.UIActionListener;
import replete.ui.windows.notifications.msg.NotificationTask;
import replete.ui.worker.RWorkerStatus;
import replete.ui.worker.events.RWorkerStatusEvent;
import replete.ui.worker.events.RWorkerStatusListener;

public abstract class FWorkerActionListener implements UIActionListener {


    ///////////
    // FIELD //
    ///////////

    protected AppContext ac;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FWorkerActionListener(AppContext ac) {
        this.ac = ac;
    }


    //////////////
    // ABSTRACT //
    //////////////

    public abstract FWorker create();


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public void actionPerformed(ActionEvent e, UIAction action) {
        final FWorker worker = create();
        final ViewPanel pnlView = ac.getSelectedWorld() == null ? null :
            ac.getSelectedWorld().getWorldPanel().getSelectedView();

        if(ac.getWindow() != null) {
            NotificationTask prog = new NotificationTask()
                .setTitle(worker.getName())
                .setAction(worker)
                .setUseWaitCursor(true)
                .setAddError(true);
            ac.getWindow().getNotificationModel().getTasks().add(prog);
        }

        worker.addStatusListener(new RWorkerStatusListener() {
            public void stateChanged(RWorkerStatusEvent e) {

                if(e.getCurrent() == RWorkerStatus.PRE_FINISHED) {
                    if(pnlView != null) {

                        // Old API
//                        List<ExpandDescriptor> expands = worker.getNodesToExpand();
//                        if(expands != null) {
//                            for(ExpandDescriptor expand : expands) {
//                                if(expand.getLevel() != -1) {
//                                    pnlView.expandToLevel(expand.getNode(), expand.getLevel());
//                                } else {
//                                    pnlView.expand(expand.getNode());
//                                }
//                            }
//                        }

                        List<ExpandRequest> expandRequests = worker.getExpandRequests();
                        if(expandRequests != null) {
                            for(ExpandRequest expandRequest : expandRequests) {
                                pnlView.addExpand(expandRequest);
                            }
                        }


                        // Need to prevent our classes from responding to every single
                        // selection update request for this block...

                        pnlView.beginBulkSelection();
//                        System.out.println("HI2");
                        List<SelectRequest> selectionRequests = worker.getSelectRequests();
                        if(selectionRequests != null) {
                            pnlView.clearSelection();
                            for(SelectRequest selectionRequest : selectionRequests) {
                                pnlView.addSelection(selectionRequest);
//                                System.out.println(selectionRequest);
                            }
                        }
                        pnlView.endBulkSelection();
//                        System.out.println("STOP2");




                        if(worker.shouldEdit()) {
                            pnlView.setShiftForEditValue(worker.isEditShift());
                            pnlView.startEditing();
                        }
                    }
                    ac.getActionMap().fireAnyActionNotifier();
                }
            }
        });

        if(pnlView != null) {
            pnlView.cancelEditing();
        }

        ActionValidator validator = action.getValidator();
        if(validator instanceof AActionValidator) {
            AActionValidator aValidator = (AActionValidator) action.getValidator();
            List<SelectionContext> validSelected = aValidator.getLastValidResults();
            worker.setValidSelected(validSelected);
        }

        worker.execute();
    }
}
