package finio.extractors;

import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import finio.core.FConst;
import finio.core.NonTerminal;

public class PdfExtractor extends NonTerminalExtractor {

                    // NOT USED YET //
    ///////////
    // FIELD //
    ///////////

    private File file;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PdfExtractor(File file) {
        this.file = file;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        try {
            PDDocument pd = PDDocument.load(file);

            NonTerminal Minfo = createBlankNonTerminal();
            PDDocumentInformation info = pd.getDocumentInformation();
            Minfo.put("id",           pd.getDocumentId());
            Minfo.put("author",       info.getAuthor());
            Minfo.put("created-date", info.getCreationDate());
            Minfo.put("creator",      info.getCreator());
            Minfo.put("keywords",     info.getKeywords());
            Minfo.put("mod-date",     info.getModificationDate());
            Minfo.put("producer",     info.getProducer());
            Minfo.put("subject",      info.getSubject());
            Minfo.put("title",        info.getTitle());
            Minfo.put("trapped",      info.getTrapped());
            Minfo.put("cos-obj",      info.getCOSObject());
            NonTerminal MinfoMd = createBlankNonTerminal();
            for(String mdKey : info.getMetadataKeys()) {
                MinfoMd.put(mdKey, info.getPropertyStringValue(mdKey));
                MinfoMd.put(mdKey + ":C", info.getCustomMetadataValue(mdKey));
            }
            Minfo.put("metadata", MinfoMd);
            Minfo.putSysMeta(FConst.JAVA_REF_KEY, info);

            PDDocumentCatalog cat = pd.getDocumentCatalog();
            NonTerminal Mcat = createBlankNonTerminal();
            Mcat.put("language", cat.getLanguage());
            Mcat.put("mark-info", cat.getMarkInfo());      // more
            Mcat.put("metadata", cat.getMetadata());       // more
            Mcat.put("names", cat.getNames());             // more
            Mcat.put("page-labels", cat.getPageLabels());  // more
            Mcat.put("page-layout", cat.getPageLayout());
            Mcat.put("page-mode", cat.getPageMode());
            Mcat.put("page-node", cat.getPages());         // more
            Mcat.put("root", cat.getStructureTreeRoot());  // more
            Mcat.put("threads", cat.getThreads());         // more
            Mcat.put("uri", cat.getURI());                 // more
            Mcat.put("version", cat.getVersion());         // more
            Mcat.put("vpref", cat.getViewerPreferences()); // more

            NonTerminal Mpages = createBlankNonTerminal();
            int i = 0;
            for(Object o : cat.getPages()) {
                PDPage page = (PDPage) o;

                NonTerminal Mpage = createBlankNonTerminal();
                Mpage.put("crop-box",       page.getCropBox());
                Mpage.put("media-box",      page.getMediaBox());
                Mpage.put("resos",          page.getResources());
                Mpage.put("rotation",       page.getRotation());
                Mpage.put("annot",          page.getAnnotations());
                Mpage.put("art-box",        page.getArtBox());
                Mpage.put("bleed-box",      page.getBleedBox());
                Mpage.put("trim-box",       page.getTrimBox());
                Mpage.put("thread-beads",   page.getThreadBeads());
                Mpage.put("struct-parents", page.getStructParents());
                // Could also do: page.getMetadata()

                NonTerminal Mannot = createBlankNonTerminal();
                int a = 0;
                for(PDAnnotation annot : page.getAnnotations()) {
                    Mannot.put(a++, convert(annot));
                }
                Mpage.put("annotations", Mannot);
                Mpage.putSysMeta(FConst.JAVA_REF_KEY, page);

                Mpages.put(i++, Mpage);
            }
            Mpages.putSysMeta(FConst.JAVA_REF_KEY, cat.getPages());

            Mcat.put("pages", Mpages);
            Mcat.putSysMeta(FConst.JAVA_REF_KEY, cat);

            NonTerminal M = createBlankNonTerminal();
            M.put("catalog", Mcat);
            M.put("info", Minfo);
            M.put("access-perm", pd.getCurrentAccessPermission());
            M.put("pages", pd.getNumberOfPages());
            M.put("all-security-to-be-removed", pd.isAllSecurityToBeRemoved());
            M.put("encrypted", pd.isEncrypted());
            M.putSysMeta(FConst.JAVA_REF_KEY, pd);

            return M;

        } catch(Exception e) {
            throw new RuntimeException("PDF Extraction Failed", e);
        }
    }

    private NonTerminal convert(PDAnnotation annot) {
        NonTerminal M = createBlankNonTerminal();

        M.put("flags", annot.getAnnotationFlags());
        M.put("name", annot.getAnnotationName());
        M.put("app-dict", annot.getAppearance());
        M.put("app-stream", annot.getNormalAppearanceStream());
        M.put("color", annot.getColor());
        M.put("contents", annot.getContents());
        M.put("mod-date", annot.getModifiedDate());
        M.put("rectangle", annot.getRectangle());
        M.put("struct-parent", annot.getStructParent());
        M.put("subtype", annot.getSubtype());

        return M;
    }

    @Override
    protected String getName() {
        return "PDF Extractor";
    }
}
