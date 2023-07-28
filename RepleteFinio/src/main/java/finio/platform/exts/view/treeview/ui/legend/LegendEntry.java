package finio.platform.exts.view.treeview.ui.legend;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import finio.ui.images.FinioImageModel;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;

public class LegendEntry {

    public enum LegendType {
        NON_TERMINAL("Non Terminal"),
        TERMINAL("Terminal"),
        SEMI_TERMINAL("Semi-Terminal"),
        EITHER("Either Non Terminal or Terminal"),
        UNKNOWN("");
        private String label;
        private LegendType(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
    }

    private static List<LegendEntry> entries = new ArrayList<LegendEntry>();
    public static List<LegendEntry> getEntries() {
        return entries;
    }

    static {
        entries.add(new LegendEntry("World",                  ImageLib.get(CommonConcepts.WORLD), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("Realm",                  ImageLib.get(FinioImageModel.REALM), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("Map",                    ImageLib.get(FinioImageModel.NT_MAP), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("List",                   ImageLib.get(FinioImageModel.NT_LIST), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("ListMap",                ImageLib.get(FinioImageModel.NT_LISTMAP), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("Paused Map",             ImageLib.get(FinioImageModel.NT_MAP_PAUSED), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("System Meta Map",        ImageLib.get(FinioImageModel.METAMAP), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("Paused System Meta Map", ImageLib.get(FinioImageModel.METAMAP_PAUSED), LegendType.NON_TERMINAL, "huh"));
        entries.add(new LegendEntry("Anchor",                 ImageLib.get(FinioImageModel.ANCHOR), LegendType.EITHER, "huh"));
        entries.add(new LegendEntry("Terminal",               ImageLib.get(FinioImageModel.TERMINAL), LegendType.TERMINAL, "huh"));
        entries.add(new LegendEntry("Expandable Terminal",    ImageLib.get(FinioImageModel.TERMINAL_EXPANDABLE), LegendType.SEMI_TERMINAL, "huh"));
        entries.add(new LegendEntry("Byte Array",             ImageLib.get(FinioImageModel.TERMINAL_BINARY), LegendType.SEMI_TERMINAL, "huh"));
        entries.add(new LegendEntry("Unexpandable Terminal",  ImageLib.get(FinioImageModel.TERMINAL_UNEXPANDABLE), LegendType.TERMINAL, "huh"));
        entries.add(new LegendEntry("Systen Meta Terminal",   ImageLib.get(FinioImageModel.TERMINAL_META), LegendType.TERMINAL, "huh"));
    }

    private String title;
    private ImageIcon icon;
    private LegendType type;
    private String description;

    public LegendEntry(String title, ImageIcon icon, LegendType type, String description) {
        this.title = title;
        this.icon = icon;
        this.type = type;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }
    public ImageIcon getIcon() {
        return icon;
    }
    public LegendType getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }
}
