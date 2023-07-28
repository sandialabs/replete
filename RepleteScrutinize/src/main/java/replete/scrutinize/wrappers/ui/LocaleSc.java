package replete.scrutinize.wrappers.ui;

import java.util.Locale;

import replete.scrutinize.core.BaseSc;

public class LocaleSc extends BaseSc {    // DONE


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?> getHandledClass() {
        return Locale.class;
    }

    @Override
    public String[] getExtractedFields() {
        return new String[] {
            "getDefault",
            "getAvailableLocales",
            "getISOCountries",
            "getISOLanguages",
            "getLanguage",
            "getScript",
            "getCountry",
            "getVariant",
            "hasExtensions",
            "getExtensionKeys",
            "getUnicodeLocaleAttributes",
            "getUnicodeLocaleKeys",
            "getBaseLocale",
            "getLocaleExtensions",
            "toString",
            "toLanguageTag",
            "getISO3Language",
            "getISO3Country",
            "getDisplayLanguage",
            "getDisplayScript",
            "getDisplayCountry",
            "getDisplayVariant",
            "getDisplayName",
        };
    }
}

