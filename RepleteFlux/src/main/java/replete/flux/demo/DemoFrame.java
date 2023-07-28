package replete.flux.demo;

import java.awt.GridLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JPanel;

import replete.flux.FluxPanelParams;
import replete.flux.images.FluxImageModel;
import replete.flux.streams.DataStream;
import replete.flux.streams.FluxDataStreamModel;
import replete.io.FileUtil;
import replete.plugins.HumanDescriptor;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.lay.Lay;
import replete.ui.windows.notifications.NotificationFrame;
import replete.util.User;
import replete.xstream.MetadataXStream;

public class DemoFrame extends NotificationFrame {


    ////////////
    // FIELDS //
    ////////////

    private FluxDataStreamModel demoDataStreamModel = new FluxDataStreamModel();
    private Map<UUID, FluxWrapperPanel> panels = new LinkedHashMap<>();
    private JPanel pnlContainer;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DemoFrame() {
        super("Flux Demo", FluxImageModel.FLUX_LOGO);

        demoDataStreamModel.addDataStream("my-stream-0", new DataStream().setSystemDescriptor(new HumanDescriptor("axel", "")));
        demoDataStreamModel.addDataStream("my-stream-1", new DataStream().setSystemDescriptor(new HumanDescriptor("chuck", "")));
        demoDataStreamModel.addDataStream("my-stream-2", new DataStream().setSystemDescriptor(new HumanDescriptor("daniel", "")));
        demoDataStreamModel.addDataStream("my-stream-3", new DataStream().setSystemDescriptor(new HumanDescriptor("steve", "")));

        loadState();

        JButton btnAdd;
        Lay.BLtg(this,
            "N", Lay.FL("L",
                Lay.lb("Flux Demo"),
                btnAdd = Lay.btn("Add Flux Panel", CommonConcepts.ADD),
                "bg=white"
            ),
            "C", pnlContainer = Lay.p(),
            "size=700,center"
        );

        setShowStatusBar(true);
        btnAdd.addActionListener(e -> addPanel());
        rebuild();
        addClosingListener(e -> saveState());
    }

    private void addPanel() {
        FluxWrapperPanel pnl = new FluxWrapperPanel(demoDataStreamModel);
        UUID uuid = UUID.randomUUID();
        pnl.addRemoveListener(e -> {
            panels.remove(uuid);
            rebuild();
        });
        panels.put(uuid, pnl);
        rebuild();
    }

    private void rebuild() {
        pnlContainer.removeAll();
        pnlContainer.setLayout(new GridLayout(panels.size(), 1));
        for(FluxWrapperPanel pnl : panels.values()) {
            pnlContainer.add(pnl);
        }
        pnlContainer.updateUI();
    }

    private void loadState() {
        File f = User.getDesktop("demo.state");
        if(FileUtil.isReadableFile(f)) {
            MetadataXStream xStream = new MetadataXStream();
            DemoState demoState = (DemoState) xStream.fromXML(f);
            for(FluxPanelParams params : demoState.params) {
                FluxWrapperPanel pnl = new FluxWrapperPanel(demoDataStreamModel);
                UUID uuid = UUID.randomUUID();
                pnl.addRemoveListener(e -> {
                    panels.remove(uuid);
                    rebuild();
                });
                pnl.getPnlFlux().setParams(params);
                panels.put(uuid, pnl);
            }
        }
    }

    private void saveState() {
        List<FluxPanelParams> params = new ArrayList<>();
        for(FluxWrapperPanel panel : panels.values()) {
            params.add(panel.getPnlFlux().getParams());
        }
        DemoState demoState = new DemoState()
            .setParams(params)
        ;
        MetadataXStream xStream = new MetadataXStream();
        xStream.toXML(demoState, User.getDesktop("demo.state"));
    }
}
