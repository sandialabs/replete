package finio.extractors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import finio.core.FConst;
import finio.core.NonTerminal;
import replete.text.FieldListParser;

public class CsvExtractor extends NonTerminalExtractor {


    ////////////
    // FIELDS //
    ////////////

    private File file;
    private String content;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public CsvExtractor(File file) {
        this.file = file;
    }
    public CsvExtractor(String content) {
        this.content = content;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        try {
            if(file != null) {
                return populateFromFile();
            }
            return populateFromContent();
        } catch(Exception e) {
            throw new RuntimeException("CSV Extraction Failed", e);
        }
    }
    private NonTerminal populateFromFile() throws Exception {
        NonTerminal M = createBlankNonTerminal();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            parseLines(M, reader);

        } catch(Exception e) {
            throw e;

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    throw e;
                }
            }
        }

        return M;
    }

    private NonTerminal populateFromContent() throws Exception {
        NonTerminal M = createBlankNonTerminal();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new StringReader(content));
            parseLines(M, reader);

        } catch(Exception e) {
            throw e;

        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(Exception e) {
                    throw e;
                }
            }
        }

        return M;
    }

    private void parseLines(NonTerminal M, BufferedReader reader) throws IOException {
        StringBuilder buffer = null;
        String line;
        int l = 0;
        if(content == null) {
            buffer = new StringBuilder();
        }
        while((line = reader.readLine()) != null) {
            if(buffer != null) {
                buffer.append(line);
                buffer.append('\n');
            }
            List<String> fields = FieldListParser.parseLine(line, ',', '"');
            NonTerminal Mline = createBlankNonTerminal();
            M.put(l++, Mline);
            int f = 0;
            for(String field : fields) {
                Mline.put(f++, field);
            }
        }
        String alt = buffer != null ? buffer.toString() : content;
        M.putSysMeta(FConst.SYS_ALT_KEY, alt);
    }

    @Override
    protected String getName() {
        return "CSV Extractor";
    }
}
