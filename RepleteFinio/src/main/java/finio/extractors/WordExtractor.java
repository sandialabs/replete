package finio.extractors;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;

import finio.core.NonTerminal;
import replete.util.ReflectionUtil;

public class WordExtractor extends NonTerminalExtractor {


    ///////////
    // FIELD //
    ///////////

    private File file;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WordExtractor(File file) {
        this.file = file;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        NonTerminal M = createBlankNonTerminal();
        try {
            WordprocessingMLPackage doc = WordprocessingMLPackage.load(file);
            MainDocumentPart p = doc.getMainDocumentPart();
            traverse(M, p);
//            JavaObjectExtractor subExtractor = new JavaObjectExtractor(p);
//            return subExtractor.toMap();
            M.compress();
            return M;
        } catch(Exception e) {
            throw new RuntimeException("Word Extraction Failed", e);
        }
    }

    private int x = 0;
    private void traverse(NonTerminal M, Object element) throws Exception {
        traverse(M, element, 0);
    }
    private void traverse(NonTerminal M, Object element, int level) throws Exception {
        x++;
        if(x >= 1000000) {
            return;
        }
        if(element instanceof MainDocumentPart) {
            MainDocumentPart mdp = (MainDocumentPart) element;
            List<Object> children = mdp.getContent();
            int i = 0;
            for(Object child : children) {
                if(child instanceof JAXBElement) {
                    child = ((JAXBElement) child).getValue();
                }
                NonTerminal Mchild = createBlankNonTerminal();
                M.put("[" + i++ + "] " + child.getClass().getSimpleName(), Mchild);
                traverse(Mchild, child, level + 1);
                Mchild.compress();
            }
        } else if(element instanceof P) {
            P p = (P) element;
            M.put("PPr", createPPr(p.getPPr()));
            int i = 0;
            for(Object child : p.getContent()) {
                if(child instanceof JAXBElement) {
                    child = ((JAXBElement) child).getValue();
                }
                NonTerminal Mchild = createBlankNonTerminal();
                M.put("[" + i++ + "] " + child.getClass().getSimpleName(), Mchild);
                traverse(Mchild, child, level + 1);
                Mchild.compress();
            }
        } else if(element instanceof R) {
            R r = (R) element;
            M.put("RPr", createRPr(r.getRPr()));
            int i = 0;
            for(Object child : r.getContent()) {
                if(child instanceof JAXBElement) {
                    child = ((JAXBElement) child).getValue();
                }
                NonTerminal Mchild = createBlankNonTerminal();
                M.put("[" + i++ + "] " + child.getClass().getSimpleName(), Mchild);
                traverse(Mchild, child, level + 1);
                Mchild.compress();
            }
        } else if(element instanceof JAXBElement) {
            // Here we eliminate the phantom JAXBElement objects
            // by recursing only on their payloads.
            JAXBElement j = (JAXBElement) element;
            M.put("Scope", j.getScope());
            M.put("Name", j.getName());
            M.put("DeclType", j.getDeclaredType());
            int i = 0;
            Object child = j.getValue();
            NonTerminal Mchild = createBlankNonTerminal();
            M.put("[" + i++ + "] " + child.getClass().getSimpleName(), Mchild);
            traverse(Mchild, child, level + 1);
            Mchild.compress();
        } else if(element instanceof Text) {
            Text t = (Text) element;
            if(t.getValue() != null) {
                M.put("Value", t.getValue());
            }
            if(t.getSpace() != null) {
                M.put("Space", t.getSpace());
            }
        }
    }

    private NonTerminal createRPr(RPr rpr) throws Exception {
        if(rpr == null) {
            return null;
        }
//        JavaObjectExtractor ex = new JavaObjectExtractor(rpr);
//        return ex.toMap();
        NonTerminal Mlocal = createBlankNonTerminal();
        Method[] ms = ReflectionUtil.getMethods(rpr);
        for(Method m : ms) {
            if(m.getName().equals("getClass") || m.getName().equals("getParent")) {
                continue;
            }
            if(m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                Object value = m.invoke(rpr);
                if(value != null) {
                    Mlocal.put(m.getName().substring(3), value);
                }
            }
        }
        return Mlocal;
    }

    private NonTerminal createPPr(PPr ppr) throws Exception {
        if(ppr == null) {
            return null;
        }
        NonTerminal Mlocal = createBlankNonTerminal();
        Method[] ms = ReflectionUtil.getMethods(ppr);
        for(Method m : ms) {
            if(m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                Object value = m.invoke(ppr);
                if(value != null) {
                    Mlocal.put(m.getName().substring(3), value);
                }
            }
        }
        return Mlocal;
    }

//    private static String getKey(Object obj) {
//        if(obj instanceof P) {
//            P p = (P) obj;
//            return "(Paragraph) " + p.getRsidRDefault() + "/" + obj.hashCode();
//        } else if(obj instanceof R) {
//            R r = (R) obj;
//            return "(Run) " + r.getRsidRPr() + "/" + obj.hashCode() + "/" + System.currentTimeMillis();
//        } else if(obj instanceof Text) {
//            Text t = (Text) obj;
//            return "(Text) \"" + t.getValue() + "\" space=\"" + t.getSpace() + "\" [" + obj.hashCode() + "]";
//        } else if(obj instanceof CTBookmark) {
//            CTBookmark b = (CTBookmark) obj;
//            return "(Bookmark) " + b.getName() + "#" + b.getColFirst() + "." + b.getColLast() + " [" + obj.hashCode() + "]";
//        } else if(obj instanceof CTMarkupRange) {
//            CTMarkupRange r = (CTMarkupRange) obj;
//            return "(MarkupRange) " + r.getId() + " [" + obj.hashCode() + "] " + r.toString();
//        } else if(obj instanceof JAXBElement) {
//            return "(JAXBElement) " + obj.hashCode();
//        }
//        return obj.hashCode() + "[TS:" + obj + "]";
//    }

    @Override
    protected String getName() {
        return "Word Extractor";
    }
}
