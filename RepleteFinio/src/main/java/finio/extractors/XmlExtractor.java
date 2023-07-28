package finio.extractors;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import finio.core.FConst;
import finio.core.NonTerminal;
import replete.io.FileUtil;

public class XmlExtractor extends NonTerminalExtractor {


    ////////////
    // FIELDS //
    ////////////

    private static final String CH = "^children";
    private static final String AT = "^attributes";
    private static final String TY = "^type";

    private File file;
    private String content;

    private List<NonTerminal> maps = new ArrayList<>(); //?


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public XmlExtractor(File file) {
        this.file = file;
    }
    public XmlExtractor(String content) {
        this.content = content;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        try {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            Handler handler = new Handler();
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            InputStream iStr;
            if(file != null) {
                iStr = new FileInputStream(file);
            } else {
                iStr = new ByteArrayInputStream(content.getBytes());
            }
            InputSource is = new InputSource(iStr);
            xr.parse(is);
            NonTerminal M = maps.get(0);
            String altString = file == null ? content : FileUtil.getTextContent(file);
            M.putSysMeta(FConst.SYS_ALT_KEY, altString);
            return M;
        } catch(Exception e) {
            throw new RuntimeException("XML Extraction Failed", e);
        }
    }

    @Override
    protected String getName() {
        return "XML Extractor";
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class Handler extends DefaultHandler {
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
//            System.out.println("resolveEntity: publicId=" + publicId + ", systemId=" + systemId);
            return null;
        }
        @Override
        public void notationDecl(String name, String publicId, String systemId) throws SAXException {
//            System.out.println("notationDecl: name=" + name + ", publicId=" + publicId + ", systemId=" + systemId);
        }
        @Override
        public void unparsedEntityDecl(String name, String publicId,
                                       String systemId, String notationName) throws SAXException {
//            System.out.println("unparsedEntityDecl: name=" + name + ", publicId=" + publicId + ", systemId=" + systemId + ", notationName=" + notationName);
        }
        @Override
        public void setDocumentLocator(Locator locator) {
//            System.out.println("setDocumentLocator: locator=" + locator);
        }
        @Override
        public void startDocument() throws SAXException {
//            System.out.println("startDocument");

        }
        @Override
        public void endDocument() throws SAXException {
//            System.out.println("endDocument");
        }
        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
//            System.out.println("startPrefixMapping: prefix=" + prefix + ", uri=" + uri);
        }
        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
//            System.out.println("endPrefixMapping: prefix=" + prefix);
        }
        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) throws SAXException {
//            System.out.println("startElement: uri=" + uri + ", localName=" + localName + ", qName=" + qName +", attributes=" + attributes);
            NonTerminal M = createBlankNonTerminal();
//            M.put("uri", uri);
//            M.put("localName", localName);
//            M.put("qName", qName);
            M.put(TY, qName);
            if(maps.size() != 0) {
                Object V = maps.get(maps.size() - 1).get(CH);
                NonTerminal Mparentchildren = (NonTerminal) V;
                Mparentchildren.put(Mparentchildren.size(), M);
            }
            NonTerminal Mattr;
            if(attributes.getLength() != 0) {
                M.put(AT, Mattr = createBlankNonTerminal());
                for(int a = 0; a < attributes.getLength(); a++) {
                    Mattr.put(attributes.getQName(a), attributes.getValue(a));
                }
            }
            M.put(CH, createBlankNonTerminal());
            maps.add(M);
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
//            System.out.println("endElement: uri=" + uri + ", localName=" + localName + ", qName=" + qName);
            NonTerminal Mcur = maps.get(maps.size() - 1);
            Object V = Mcur.get(CH);
            NonTerminal Mchildren = (NonTerminal) V;
            if(Mchildren.size() == 0) {
                Mcur.removeByKey(CH);
            }
            if(maps.size() != 1) {
                maps.remove(maps.size() - 1);
            }
        }
        @Override
        public void characters(char ch[], int start, int length) throws SAXException {
//            System.out.println("characters: ch=" + new String("1") + ", start=" + start + ", length=" + length);
            String text = "";
            for(int c = start; c < start + length; c++) {
                text += ch[c];
            }
//            System.out.println("  " + text);
            if(maps.size() != 0 && !text.trim().isEmpty()) {
                NonTerminal Mcur = maps.get(maps.size() - 1);
                Object V = Mcur.get(CH);
                NonTerminal McurChildren = (NonTerminal) V;
                McurChildren.put(McurChildren.size(), text);
            }
        }
        @Override
        public void ignorableWhitespace(char ch[], int start, int length) throws SAXException {
//            System.out.println("ignorableWhitespace: ch=" + Arrays.toString(ch) + ", start=" + start + ", length=" + length);
        }
        @Override
        public void processingInstruction(String target, String data) throws SAXException {
//            System.out.println("processingInstruction: target=" + target + ", data=" + data);
        }
        @Override
        public void skippedEntity(String name) throws SAXException {
//            System.out.println("skippedEntity: name=" + name);
        }
        @Override
        public void warning(SAXParseException e) throws SAXException {
            System.out.println("warning: e=" + e);
        }
        @Override
        public void error(SAXParseException e) throws SAXException {
            System.out.println("error: e=" + e);
        }
        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            System.out.println("fatalError: e=" + e);
            throw e;
        }
    }
}
