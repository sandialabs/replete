package replete.ui.sdplus.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeListener;

import replete.event.ChangeNotifier;
import replete.ui.sdplus.ScaleSetPanelModel;
import replete.ui.sdplus.panels.ContScalePanelModel;
import replete.ui.sdplus.panels.DateScalePanelModel;
import replete.ui.sdplus.panels.EnumScaleMultiPanelModel;
import replete.ui.sdplus.panels.EnumScaleSinglePanelModel;
import replete.ui.sdplus.panels.LongScalePanelModel;
import replete.ui.sdplus.panels.ScalePanelModel;


/**
 * Demo model for the scale set panel.
 *
 * Note that this ScaleSetPanelModel is essentially backed
 * by three completely different data sources:
 *    List<DataElement> data
 *    String[] zSpecialValues
 *    String[] rbSpecialValues
 * This is enabled since the ScaleSetPanelModel interface
 * requires that the number of data elements for each
 * key be returned (getDataElementCount(String key)).
 * For many applications this method may return the same
 * value for all keys - thus making the model a standard
 * rows x columns rectangular data table.
 *
 * @author Derek Trumbo
 */

public class DemoModel implements ScaleSetPanelModel {
    protected List<DataElement> data;
    protected List<DataScale> scales;
    protected String[] zSpecialValues;
    protected String[] rbSpecialValues;
    protected Map<String, ScalePanelModel> modelMap = new HashMap<String, ScalePanelModel>();

    protected ChangeNotifier modelNotifier = new ChangeNotifier(this);
    public void addScaleSetPanelModelListener(ChangeListener listener) {
        modelNotifier.addListener(listener);
    }
    public void fireScaleSetPanelModelChanged() {
        modelNotifier.fireStateChanged();
    }

    public DemoModel(List<DataElement> d, List<DataScale> scl, String[] zVals, String[] rbVals) {
        data = d;
        scales = scl;

        // Custom models.
        zSpecialValues = zVals;
        rbSpecialValues = rbVals;
    }

    public int getDataElementCount(String key) {

        // Custom count / values for these keys.
        if(key.equals("z")) {
            return zSpecialValues.length;
        } else if(key.equals("rb")) {
            return rbSpecialValues.length;
        }

        return data.size();
    }

    public String[] getScaleKeys() {
        List<String> keyNames = new ArrayList<String>();
        for(DataScale scl : scales) {
            keyNames.add(scl.key);
        }
        return keyNames.toArray(new String[0]);
    }

    public ScalePanelModel getScalePanelModel(String key) {
        if(modelMap.get(key) == null) {
            modelMap.put(key, buildInitialScalePanelModel(key));
        }
        return modelMap.get(key);
    }

    public ScalePanelModel buildInitialScalePanelModel(String key) {
        if(key.equals("fn")) {
            return new ScalePanelModel(key, getName(key), getUnits(key), getNote(key));
        } else if(key.equals("bd")) {
            return new DateScalePanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
        } else if(key.equals("ts")) {
            DateScalePanelModel ds = new DateScalePanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
            ds.setDateFormat(new SimpleDateFormat("yyyy/M/d HH:mm:ss"));
            return ds;
        } else if(key.equals("age")) {
            return new LongScalePanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
        } else if(key.equals("height") || key.equals("weight") || key.equals("age") || key.equals("length")) {
            return new ContScalePanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
        } else if(key.equals("rb")) {
            return new EnumScaleSinglePanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
        }
        EnumScaleMultiPanelModel emodel = new EnumScaleMultiPanelModel(key, getName(key), getUnits(key), getNote(key), getAllValues(key));
        emodel.getForceChecked().add("Johnson");
        return emodel;
    }

    private List<Object> getAllValues(String key) {
        int elemCount = getDataElementCount(key);
        List<Object> allValues = new ArrayList<Object>(elemCount);
        for(int elem = 0; elem < elemCount; elem++) {
            allValues.add(getValue(key, elem));
        }
        return allValues;
    }

    public String getName(String key) {
        for(DataScale scl : scales) {
            if(scl.key.equals(key)) {
                return scl.name;
            }
        }
        return null;
    }

    public String getUnits(String key) {
        for(DataScale scl : scales) {
            if(scl.key.equals(key)) {
                return scl.units;
            }
        }
        return null;
    }

    public String getNote(String key) {
        for(DataScale scl : scales) {
            if(scl.key.equals(key)) {
                return scl.note;
            }
        }
        return null;
    }

    public Object getValue(String key, int elementIndex) throws IllegalArgumentException {

        List<String> keyNames = new ArrayList<String>();
        for(DataScale scl : scales) {
            keyNames.add(scl.key);
        }
        if(!keyNames.contains(key)) {
            throw new IllegalArgumentException("Invalid scale key '" + key + "'.");
        }

        // Custom count / values for these key.
        if(key.equals("z")) {

            if(elementIndex >= zSpecialValues.length) {
                throw new IllegalArgumentException("Invalid element index '" + elementIndex + "' for scale key '" + key + "'.");
            }

            return zSpecialValues[elementIndex];

        } else if(key.equals("rb")) {

            if(elementIndex >= rbSpecialValues.length) {
                throw new IllegalArgumentException("Invalid element index '" + elementIndex + "' for scale key '" + key + "'.");
            }

            return rbSpecialValues[elementIndex];
        }

        if(elementIndex >= data.size()) {
            throw new IllegalArgumentException("Invalid element index for key '" + key + "'.");
        }

        return data.get(elementIndex).values.get(key);
    }
}
