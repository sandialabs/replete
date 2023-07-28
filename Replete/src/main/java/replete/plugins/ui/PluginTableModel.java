package replete.plugins.ui;

import replete.plugins.state.PluginManagerState;
import replete.plugins.state.PluginState;
import replete.ui.table.RTableModel;

public class PluginTableModel extends RTableModel {


    ///////////
    // FIELD //
    ///////////

    private PluginManagerState state;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public PluginTableModel(PluginManagerState state) {
        this.state = state;
    }

    public void setState(PluginManagerState state) {
        this.state = state;
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch(columnIndex) {
            case 0: return "Provider";
            case 1: return "Name";
            case 2: return "Version";
            case 3: return "ID";
        }
        return null;
    }

    @Override
    public int getRowCount() {
        if(state == null) {
            return 0;
        }
        return state.getGlobalPlugins().size();
    }

    public PluginState getPlugin(int index) {
        PluginState pluginState =
            state.getGlobalPlugins().values().toArray(
                new PluginState[0])[index];
        return pluginState;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PluginState plugin =
            state.getGlobalPlugins().values().toArray(
                new PluginState[0])[rowIndex];
        switch(columnIndex) {
            case 0: return plugin.getProvider();
            case 1: return plugin.getName();
            case 2: return plugin.getVersion();
            case 3: return plugin.getId();
        }
        return null;
    }
}
