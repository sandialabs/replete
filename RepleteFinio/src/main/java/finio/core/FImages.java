package finio.core;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import finio.core.impl.FList;
import finio.core.impl.FListMap;
import finio.core.impl.FMap;
import finio.core.managed.ManagedNonTerminal;
import finio.core.managed.ManagedValueManager;
import finio.ui.FileSystemImageCache;
import finio.ui.images.FinioImageModel;
import replete.io.FileUtil;
import replete.ui.images.concepts.CommonConcepts;
import replete.ui.images.concepts.ImageLib;
import replete.ui.images.concepts.ImageModelConcept;
import replete.ui.images.shared.SharedImage;

public class FImages {


    ////////////
    // FIELDS //
    ////////////

    // All
    private static final ImageIcon iconAnchor        = ImageLib.get(FinioImageModel.ANCHOR);

    // Expandable
    private static final ImageIcon iconPaused        = ImageLib.get(FinioImageModel.NT_MAP_PAUSED);

    // World
    private static final ImageIcon iconWorld         = ImageLib.get(CommonConcepts.WORLD);

    // Non-Terminal
    private static final ImageIcon iconMap           = ImageLib.get(FinioImageModel.NT_MAP);
    private static final ImageIcon iconMapEmpty      = ImageLib.get(FinioImageModel.NT_MAP_EMPTY);
    private static final ImageIcon iconMapSimplified = ImageLib.get(FinioImageModel.NT_MAP_SIMPLIFIED);  // Something could be both simplified and described
    private static final ImageIcon iconMapDescribed  = ImageLib.get(FinioImageModel.NT_MAP_DESCRIBE);
    private static final ImageIcon iconMapDescribedP = ImageLib.get(FinioImageModel.NT_MAP_DESCRIBE_P);
    private static final ImageIcon iconList          = ImageLib.get(FinioImageModel.NT_LIST);
    private static final ImageIcon iconListMap       = ImageLib.get(FinioImageModel.NT_LISTMAP);
    private static final ImageIcon iconMeta          = ImageLib.get(FinioImageModel.METAMAP);
    private static final ImageIcon iconMetaEmpty     = ImageLib.get(FinioImageModel.METAMAP_EMPTY);
    private static final ImageIcon iconMetaPaused    = ImageLib.get(FinioImageModel.METAMAP_PAUSED);
    private static final ImageIcon iconRealm         = ImageLib.get(FinioImageModel.REALM);
    private static final ImageIcon iconMgdMap        = ImageLib.get(FinioImageModel.NT_MANAGED_MAP);
    private static final ImageIcon iconMgdMapPaused  = ImageLib.get(FinioImageModel.NT_MANAGED_MAP_P);
    private static final ImageIcon iconMgdUnlMap     = ImageLib.get(FinioImageModel.NT_MANAGED_MAP_U);
    private static final ImageIcon iconMgdRealm      = ImageLib.get(FinioImageModel.REALM_MANAGED);
    private static final ImageIcon iconMgdUnlRealm   = ImageLib.get(FinioImageModel.REALM_MANAGED_U);

    // Terminal
    private static final ImageIcon iconTerminal      = ImageLib.get(FinioImageModel.TERMINAL);
    private static final ImageIcon iconMetaTerminal  = ImageLib.get(FinioImageModel.TERMINAL_META);
    private static final ImageIcon iconSemiterminal  = ImageLib.get(FinioImageModel.TERMINAL_EXPANDABLE);
    private static final ImageIcon iconUnexpWarning  = ImageLib.get(FinioImageModel.TERMINAL_UNEXPANDABLE);
    private static final ImageIcon iconBinary        = ImageLib.get(FinioImageModel.TERMINAL_BINARY);
    private static final ImageIcon iconBinaryMeta    = ImageLib.get(FinioImageModel.TERMINAL_BINARY_META);
    private static final ImageIcon iconTerminalMgd   = ImageLib.get(FinioImageModel.TERMINAL_MANAGED);
    private static final ImageIcon iconTerminalMgdNt = ImageLib.get(FinioImageModel.TERMINAL_MANAGED_NT);

    public static Icon createIconForWorld(boolean paused) {
        if(paused) {
            return iconPaused;
        }
        return iconWorld;
    }

    // Not sure where this goes yet, but just extracting from tree so other
    // views can use it.
    public static Icon createIconFromNonTerminal(Object V) {
        return createIconForNonTerminal(null, V, false, false, false, false);
    }
    public static Icon createIconForNonTerminal(Object K, Object V, boolean anchor,
                                                 boolean paused, boolean realm, boolean simplified) {

        NonTerminal M = (NonTerminal) V;
        Object iconObj = M.getSysMeta(FConst.SYS_IMAGES);

        if(iconObj instanceof String) {
            String iconStr = (String) iconObj;
            ImageIcon icon;
            try {
                icon = SharedImage.get(iconStr);
                if(icon != null) {
                    return icon;
                }
            } catch(Exception e) {

            }

            File file = new File(iconStr);
            if(FileUtil.isReadableFile(file)) {
                icon = FileSystemImageCache.get(file);
                if(icon != null) {
                    return icon;
                }
            }

        } else if(iconObj instanceof Icon) {
            return (Icon) iconObj;

        } else if(iconObj instanceof ImageModelConcept) {
            return ImageLib.get((ImageModelConcept) iconObj);

        } else if(iconObj instanceof File) {
            File file = (File) iconObj;
            if(FileUtil.isReadableFile(file)) {
                ImageIcon icon = FileSystemImageCache.get(file);
                if(icon != null) {
                    return icon;
                }
            }
        }

        if(anchor) {
            return iconAnchor;

        } else if(K != null && FUtil.isSysMetaKey(K)) {
            if(paused) {
                return iconMetaPaused;
            }
            if(M.isEmpty()) {
                return iconMetaEmpty;
            }
            return iconMeta;

        } else if(paused) {
            if(FUtil.isManagedNonTerminal(M)) {
                return iconMgdMapPaused;
            } else if(M instanceof FMap && ((FMap) M).containsKey(FConst.SYS_VALUE_KEY)) {
                // What about FList and FListMap?
                return iconMapDescribedP;
            }
            return iconPaused;

        } else if(realm) {
            if(FUtil.isManagedNonTerminal(M)) {
                ManagedNonTerminal G = (ManagedNonTerminal) M;
                if(G.isLoaded()) {
                    return iconMgdRealm;
                }
                return iconMgdUnlRealm;
            }
            return iconRealm;

        } else if(M instanceof FListMap) {
            // Could have combos of being described too.
            return iconListMap;

        } else if(M instanceof FList) {
            // Could have combos of being described too.
            return iconList;

        } else if(M instanceof FMap && ((FMap) M).containsKey(FConst.SYS_VALUE_KEY)) {
            return iconMapDescribed;

        } else if(FUtil.isManagedNonTerminal(M)) {
            ManagedNonTerminal G = (ManagedNonTerminal) M;
            if(G.isLoaded()) {
                ImageIcon icon = G.getManager().getIconForManagedNonTerminal(G);
                if(icon != null) {
                    return icon;
                }
                return iconMgdMap;
            }
            return iconMgdUnlMap;

        } else if(simplified) {
            return iconMapSimplified;
        }
        if(M.isEmpty()) {
            return iconMapEmpty;
        }
        return iconMap;
    }

    public static Icon createIconForTerminal(Object K, Object V, boolean anchor) {
        if(anchor) {
            return iconAnchor;
        }
        if(FUtil.isSysMetaKey(K)) {
            if(FUtil.isBinary(V)) {
                return iconBinaryMeta;
            }
            return iconMetaTerminal;
        }
        if(FUtil.isUnexpandableWarning(V)) {
            return iconUnexpWarning;
        }
        if(FUtil.isManagedValueManager(V)) {
            ManagedValueManager G = (ManagedValueManager) V;
            Object Vg = G.get();
            if(FUtil.isNonTerminal(Vg)) {
                return iconTerminalMgdNt;
            }
            return iconTerminalMgd;
        }
        if(FUtil.isBinary(V)) {
            return iconBinary;
        }
        if(FUtil.isUnrecognizedNativeObject(V)) {
            return iconSemiterminal;
        }
        return iconTerminal;
    }
}
