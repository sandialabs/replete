package replete.ui.label;

import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class DatumMouseClickEvent {


    ////////////
    // FIELDS //
    ////////////

    private MouseEvent event;
    private JLabel label;
    private DataDescriptor dataDescriptor;
    private DatumDescriptor datumDescriptor;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DatumMouseClickEvent(MouseEvent event, JLabel label, DataDescriptor dataDescriptor,
                                DatumDescriptor datumDescriptor) {
        this.event = event;
        this.label = label;
        this.dataDescriptor = dataDescriptor;
        this.datumDescriptor = datumDescriptor;
    }


    ///////////////
    // ACCESSORS //
    ///////////////

    public MouseEvent getEvent() {
        return event;
    }
    public JLabel getLabel() {
        return label;
    }
    public DataDescriptor getDataDescriptor() {
        return dataDescriptor;
    }
    public DatumDescriptor getDatumDescriptor() {
        return datumDescriptor;
    }
}
