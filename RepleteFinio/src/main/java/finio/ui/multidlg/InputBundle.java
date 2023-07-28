package finio.ui.multidlg;

import java.io.File;
import java.net.URL;

import finio.core.NonTerminal;
import replete.io.FileUtil;
import replete.text.StringUtil;

public class InputBundle {


    ////////////
    // FIELDS //
    ////////////

    private File file;
    private URL url;
    private String text;
    private String extractedText;
    private byte[] bytes;
    private NonTerminal nt;


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public File getFile() {
        return file;
    }
    public URL getUrl() {
        return url;
    }
    public String getText() {
        if(text == null && file != null) {
            text = FileUtil.getTextContent(file);
        }
        return text;
    }
    public String getExtractedText() {
        return extractedText;
    }
    public byte[] getBytes() {
        return bytes;
    }
    public NonTerminal getNT() {
        return nt;
    }

    // Mutators (Builder)

    public InputBundle setFile(File file) {
        this.file = file;
        return this;
    }
    public InputBundle setUrl(URL url) {
        this.url = url;
        return this;
    }
    public InputBundle setText(String text) {
        this.text = text;
        return this;
    }
    public InputBundle setExtractedText(String extractedText) {
        this.extractedText = extractedText;
        return this;
    }
    public InputBundle setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }
    public InputBundle setNonTerminal(NonTerminal nt) {
        this.nt = nt;
        return this;
    }


    //////////
    // MISC //
    //////////

    public String getLabel() {
        if(file != null) {
            return file.toString();
        }
        if(url != null) {
            return url.toString();
        }
        return "Free Entry Text";
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String toString() {
        String res = getClass().getSimpleName() + "{";
        boolean first = true;
        if(file != null) {
            res += "File=" + file + " [" + (FileUtil.isReadableFile(file)?file.length() + " bytes":"N/A") + "]";
            first = false;
        }
        if(url != null) {
            if(!first) {
                res += ", ";
            }
            res += "URL=" + url;
            first = false;
        }
        if(text != null) {
            if(!first) {
                res += ", ";
            }
            String sp = text.isEmpty() ? "" : " ";
            res += "Text=" + StringUtil.max(text, 10) + sp + "[" + StringUtil.commas(text.length()) + " chars]";
            first = false;
        }
        if(extractedText != null) {
            if(!first) {
                res += ", ";
            }
            String sp = text.isEmpty() ? "" : " ";
            res += "ExtractedText=" + StringUtil.max(extractedText, 10) + sp + "[" + StringUtil.commas(extractedText.length()) + " chars]";
            first = false;
        }
        if(bytes != null) {
            if(!first) {
                res += ", ";
            }
            res += "Bytes=[" + StringUtil.commas(bytes.length) + " bytes]";
            first = false;
        }
        res += "}";
        return res;
    }
}
