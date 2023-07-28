package replete.flux.demo;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.flux.FluxPanel;
import replete.flux.FluxPanelParams;
import replete.flux.FluxPanelParamsDialog;
import replete.flux.streams.DataStream;
import replete.flux.streams.FluxDataStreamModel;
import replete.ui.fc.RFileChooser;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.menu.RCheckBoxMenuItem;
import replete.ui.tabbed.RNotifPanel;
import replete.ui.windows.Dialogs;
import replete.ui.worker.RWorker;
import replete.util.User;
import replete.xstream.MetadataXStream;

// This panel demonstrates some ways to interact with a flux panel.

public class FluxWrapperPanel extends RNotifPanel {


    ////////////
    // FIELDS //
    ////////////

    // The data stream model passed in here is not being used directly as any
    // one flux panel's data stream model, but rather it is a convenience
    // to the demo to also store all of the data streams its using for all
    // of the flux panels in a FluxDataStreamModel, because it is just
    // a Map<String, DataStream> with some useful notifiers.  The demo's
    // data stream model essentially simulates all the data streams that
    // a piece of software could attach to
    private FluxDataStreamModel demoDataStreamModel;
    private FluxPanel pnlFlux;

    public FluxPanel getPnlFlux() {
        return pnlFlux;
    }


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public FluxWrapperPanel(FluxDataStreamModel demoDataStreamModel) {
        this.demoDataStreamModel = demoDataStreamModel;

        JButton btnOptions, btnLoad, btnSave;
        Lay.BLtg(this,
            "N", Lay.FL("L",
                btnOptions = Lay.btn("&Options...", CommonConcepts.OPTIONS),
                btnLoad = Lay.btn("&Load...", CommonConcepts.OPEN),
                btnSave = Lay.btn("&Save...", CommonConcepts.SAVE),
                Lay.btn("Data Streams", new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        Component btn = (Component) e.getSource();

                        JPopupMenu mnuPopup = createDataStreamPopup();

                        int x = 0;                          // Rare case where e.getX() & e.getY() are not used since we
                        int y = btn.getHeight();            // want the popup menu to appear in same, fixed spot regardless
                        mnuPopup.show(btn, x, y);           // of where mouse is on the icon.
                    }
                }),
                Lay.btn("&Remove", CommonConcepts.REMOVE, (ActionListener) e -> fireRemoveNotifier()),
                "bg=white"
            ),
            "C", pnlFlux = new FluxPanel(),
            "size=800,center"
        );

        btnOptions.addActionListener(e -> {
            FluxPanelParamsDialog dlg =
                new FluxPanelParamsDialog(getWindow(), pnlFlux.getParams(), pnlFlux.getDataStreamModel(), pnlFlux);
            dlg.setVisible(true);
            if(dlg.getResult() == FluxPanelParamsDialog.ACCEPT) {
                FluxPanelParams params = dlg.getParams();
                pnlFlux.setParams(params);
            }
        });

        btnLoad.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Load Flux Panel Params")
                .setChooserSize(700, 500)
                .setMainFilter(true, "Flux Panel Parameter Files (*.xml)", "xml")
                .setCurrentDirectoryNew(User.getDesktop())
            ;
            if(chooser.showOpen(getWindow())) {
                RWorker<Void, Object> worker = new RWorker<Void, Object>() {
                    @Override
                    protected Object background(Void gathered) throws Exception {
                        MetadataXStream xStream = new MetadataXStream();
                        return xStream.fromXML(chooser.getSelectedFile());
                    }
                    @Override
                    protected void complete() {
                        try {
                            Object result = getResult();
                            if(result instanceof FluxPanelParams) {
                                pnlFlux.setParams((FluxPanelParams) result);
                            } else {
                                Dialogs.showError(getWindow(),
                                    "Invalid object contained within selected file.  " +
                                    "The selected file must contain flux panel parameters.",
                                    "Flux Panel Parameters Error"
                                );
                            }
                        } catch(Exception e) {
                            Dialogs.showDetails(getWindow(),
                                "An error has occurred loading these flux panel parameters.",
                                "Load Flux Panel Parameters Error", e
                            );
                        }
                    }
                };
                addTaskAndExecuteFg("Loading Flux Panel Parameters from File", worker);
            }
        });

        btnSave.addActionListener(e -> {
            RFileChooser chooser = RFileChooser.getChooser("Save Flux Panel Params to File")
                .setChooserSize(700, 500)
                .setMainFilter(true, "Flux Panel Parameter Files (*.xml)", "xml")
                .setCurrentDirectoryNew(User.getDesktop())
            ;
            if(chooser.showSave(getWindow())) {
                File file = chooser.getSelectedFileResolved();
                RWorker<Void, Void> worker = new RWorker<Void, Void>() {
                    @Override
                    protected Void background(Void gathered) throws Exception {
                        MetadataXStream xStream = new MetadataXStream();
                        xStream.toXML(pnlFlux.getParams(), file);
                        return null;
                    }
                    @Override
                    protected void complete() {
                        try {
                            getResult();
                        } catch(Exception e) {
                            Dialogs.showDetails(getWindow(),
                                "An error has occurred saving these flux panel parameters.",
                                "Save Flux Panel Parameters Error", e
                            );
                        }
                    }
                };
                addTaskAndExecuteFg("Saving Flux Panel Parameters to File", worker);
            }
        });
    }

    protected JPopupMenu createDataStreamPopup() {
        JPopupMenu mnuPopup = new JPopupMenu();
        for(String id : demoDataStreamModel.getDataStreams().keySet()) {
            DataStream stream = demoDataStreamModel.getDataStreams().get(id);
            RCheckBoxMenuItem mnuStream = new RCheckBoxMenuItem(id + ": " + stream.getSystemDescriptor().getName());
            boolean alreadyHas = pnlFlux.getDataStreamModel().getDataStreams().containsKey(id);
            mnuStream.setSelected(alreadyHas);
            mnuStream.addActionListener(e -> {
                if(alreadyHas) {
                    pnlFlux.getDataStreamModel().removeDataStream(id);
                } else {
                    pnlFlux.getDataStreamModel().addDataStream(id, stream);
                }
            });
            mnuPopup.add(mnuStream);
        }
        return mnuPopup;
    }

    private transient ChangeNotifier removeNotifier = new ChangeNotifier(this);
    public void addRemoveListener(ChangeListener listener) {
        removeNotifier.addListener(listener);
    }
    private void fireRemoveNotifier() {
        removeNotifier.fireStateChanged();
    }
}
