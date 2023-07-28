package finio.core;

import finio.core.managed.ManagedValueManager;
import finio.core.warnings.UnexpandableWarning;
import replete.text.StringUtil;

public class FStrings {

    public static String createBasicValueString(Object V) {

        if(FUtil.isNull(V)) {
            return FUtil.NULL_TEXT;

        } else if(FUtil.isStringOrChar(V)) {
            return "\"" + V.toString() + "\"";

        } else if(FUtil.isNumber(V) || FUtil.isBoolean(V)) {
            return "" + V;

        } else if(FUtil.isUnexpandableWarning(V)) {
            String reason = ((UnexpandableWarning) V).getReason();
            return "<Unexpandable: " + reason + ">";

        } else if(FUtil.isManagedValueManager(V)) {
            ManagedValueManager G = (ManagedValueManager) V;
            String Starget = createBasicValueString(G.get());
            return "[" + G.getName() + "] " + Starget;
                // Not sure why FTreePanel showing blank target sometimes
        }

        return FUtil.toDiagnosticString(V);
    }

    public static String createMarkedUpValueString(Object V) {

        if(FUtil.isNull(V)) {
            return "<i>" + StringUtil.cleanXmlCdata(FUtil.NULL_TEXT) + "</i>";

        } else if(FUtil.isStringOrChar(V)) {
            return StringUtil.cleanXmlCdata("\"" + V.toString() + "\"");

        } else if(FUtil.isNumber(V) || FUtil.isBoolean(V)) {
            return "" + V;

        } else if(FUtil.isUnexpandableWarning(V)) {
            String reason = ((UnexpandableWarning) V).getReason();
            return StringUtil.cleanXmlCdata("<Unexpandable: " + reason + ">");

        } else if(FUtil.isManagedValueManager(V)) {
            ManagedValueManager G = (ManagedValueManager) V;
            String Starget = createMarkedUpValueString(G.get());
            String Sg = StringUtil.cleanXmlCdata("[" + G.getName() + "] ");
            return Sg + Starget;   // Not sure why FTreePanel showing blank target sometimes
        }

        return StringUtil.cleanXmlCdata(FUtil.toDiagnosticString(V));
    }

}
