package replete.ui.mnemonics;

import javax.swing.AbstractButton;

/**
 * @author Derek Trumbo
 */

public class Mnemonics {
    private String resolvedText;
    private int mnemonicIndex;

    private Mnemonics(String text, int index) {
        resolvedText = text;
        mnemonicIndex = index;
    }

    public String getResolvedText() {
        return resolvedText;
    }
    public int getMnemonicIndex() {
        return mnemonicIndex;
    }

    // Tried to do this with regular expressions (String.replaceAll),
    // but no initially obvious answer.
    public static Mnemonics resolve(String text) {
        int mnIdx = -1;
        int fewerCh = 0;
        StringBuilder newText = new StringBuilder(text.length());
        for(int cIdx = 0; cIdx < text.length(); cIdx++) {
            char ch = text.charAt(cIdx);
            if(ch == '&') {
                if(cIdx == text.length() - 1) {
                    // Don't add any characters if ends with &.
                } else {
                    if(text.charAt(cIdx + 1) == '&') {
                        newText.append("&");        // Add a single &.
                        cIdx++;                     // To move past both &.
                        fewerCh++;                  // Number of ch removed.
                    } else {

                        // If we haven't found a single & before, save
                        // this location as the mnemonic location.
                        if(mnIdx == -1) {
                            mnIdx = cIdx - fewerCh;
                        }

                        // Don't add this &.
                    }
                }
            } else {
                newText.append(ch);
            }
        }

        return new Mnemonics(newText.toString(), mnIdx);
    }

    // Tries to handle everything except the setText method,
    // which cannot be called here, as the base class method
    // needs to be called, not the overridden methods special
    // for the mnemonics package.
    public static void apply(Mnemonics mn, AbstractButton btn) {
        String newText = mn.getResolvedText();
        int mnIdx = mn.getMnemonicIndex();
        if(mnIdx != -1) {
            btn.setMnemonic(newText.charAt(mnIdx));
            btn.setDisplayedMnemonicIndex(mnIdx);
        }
    }

    @Override
    public String toString() {
        return "resolvedText=[" + resolvedText + "], mnemonicIndex=" + mnemonicIndex;
    }
}
