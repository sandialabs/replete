package replete.scrutinize.wrappers.sys;

import java.awt.datatransfer.DataFlavor;

import replete.scrutinize.core.BaseSc;

public class DataFlavorSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return DataFlavor.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "defaultRepresentationClass",
            "defaultRepresentationClassAsString",
            "humanPresentableName",
            "mimeType",
            "primaryType",
            "representationClass",
            "subType",
            "textPlainUnicodeFlavor",        // Static
            "flavorJavaFileListType",
            "flavorRemoteObjectType",
            "flavorSerializedObjectType",
            "flavorTextType",
            "mimeTypeSerializedObject",
            "representationClassByteBuffer",
            "representationClassCharBuffer",
            "representationClassInputStream",
            "representationClassReader",
            "representationClassRemote",
            "representationClassSerializable"
        };
    }
}

