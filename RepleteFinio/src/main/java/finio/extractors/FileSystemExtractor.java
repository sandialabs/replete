package finio.extractors;

import static finio.core.impl.FMap.A;

import java.io.File;

import finio.core.FConst;
import finio.core.NonTerminal;
import finio.renderers.map.FMapRenderer;
import finio.renderers.map.StandardAMapRenderer;
import replete.util.OsUtil;

public class FileSystemExtractor extends NonTerminalExtractor {


    ////////////
    // FIELDS //
    ////////////

    private File file;
    private boolean recurse;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileSystemExtractor(File file, boolean recurse) {
        this.file = file;
        this.recurse = recurse;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public NonTerminal extractInner() {
        if(file.exists() && file.isDirectory() && recurse) {
            return fromDirectoryRecurseHierarhcy(file);
        }
        return fromSingleFileCopyHierarhcy(file);
    }

    @Override
    protected String getName() {
        return "File System Extractor";
    }

    private NonTerminal fromDirectoryRecurseHierarhcy(File f) {
        NonTerminal Morig = fromSingleFileCopyHierarhcy(f);
        NonTerminal M = Morig;

        // TODO: better way to find the deepest key?
        while(true) {
            Object fileKey = null;
            for(Object K : M.K()) {
                if(!K.equals(FConst.SYS_META_KEY)) {
                    fileKey = K;
                }
            }
            Object V = M.get(fileKey);
            if(V instanceof NonTerminal) {
                M = (NonTerminal) V;
            } else {
                NonTerminal newM = createBlankNonTerminal();
                populateMapFromDir(newM, (File) V);
                M.put(fileKey, newM);
                break;
            }
        }
        return Morig;
    }

    // TODO: need a way to combine to maps at a certain level.
//private static int lev = 0;
    private void populateMapFromDir(NonTerminal M, File dir) {
//        File ff = new File("C:\\Users\\dtrumbo\\Music");
//        System.out.println(ff + " " + ff.exists() + " " + ff.isDirectory() + " " + ff.isFile() + " " + ff.listFiles());
//        ff = new File("C:\\Users\\dtrumbo\\My Music");
//        System.out.println(ff + " " + ff.exists() + " " + ff.isDirectory() + " " + ff.isFile() + " " + ff.listFiles());
//        System.out.println("[D]"+StringUtil.spaces(lev * 4) + dir + " file?" + dir.isFile() + " dir? " + dir.isDirectory()) ;
        File[] children = dir.listFiles();

        // It's possible at least on windows for a File to represent a directory and also return
        // null for listFiles (something about the magic "My XYZ" directories in User's home/Documents
        // directories).
        if(children != null) {
            for(File file : children) {
    //            System.out.println("[F]"+StringUtil.spaces((lev+1) * 4) + file);
                if(file.isFile()) {
                    M.put(file.getName(), file);
                } else {
                    NonTerminal childM = createBlankNonTerminal();
//                    lev++;
                    populateMapFromDir(childM, file);
//                    lev--;
                    M.put(file.getName(), childM);
                }
            }
            markupMapRepresentingDir(M, dir);
        }
    }

    private void markupMapRepresentingDir(NonTerminal M, File dir)  {
        // Could potentially populate file/dir metadata
        M.putSysMeta("source", dir);
    }

    private NonTerminal fromSingleFileCopyHierarhcy(File f) {
        NonTerminal M = createBlankNonTerminal();
        boolean firstTime = true;
        while(f != null) {
            if(firstTime) {
                M.put(f.getName(), f);   // Could potentially be a "remote storage map", f may not even exist at this point.
            } else {
                String name = f.getName();
                if(name.equals("")) {  // Why does his return ""  for "C:\" ??
                    name = f.getAbsolutePath();
                    if(OsUtil.isWindows() && name.endsWith("\\")) {   // Unix?
                        name = name.substring(0, name.length() - 1);
                    }
                }
                markupMapRepresentingDir(M, f);
                M = A(name, M);      // Could technically put all files/folders into a "children[]"...  so many options!
            }
            f = f.getParentFile();
            firstTime = false;
        }
        return M;
    }


    //////////
    // TEST //
    //////////

    public static void main(String[] args) {
        FMapRenderer renderer = new StandardAMapRenderer();
        File f = new File("C:\\Users\\dtrumbo\\work\\eclipse-main\\Finio\\src\\finio\\Syntax.java");
        FileSystemExtractor ex = new FileSystemExtractor(f, false);
        NonTerminal M = ex.extract();

        System.out.println(M);
        System.out.println(renderer.render("hi", M));
        File f2 = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
        System.out.println(renderer.renderValue(new FileSystemExtractor(f2, false).extract()));
        File f3 = new File("C:\\Octave\\3.2.4_gcc-4.4.0\\tools");
        NonTerminal M3 = new FileSystemExtractor(f3, true).extract();
        pl(renderer.renderValue(M3));
    }
    private static void p(Object o) {
        p(null, o);
    }
    private static void p(String printKey, Object o) {
        System.out.print((printKey != null ? printKey + " :: " : "") + o);
    }
    private static void pl(Object o) {
        pl(null, o);
    }
    private static void pl(String printKey, Object o) {
        System.out.println((printKey != null ? printKey + " :: " : "") + o);
    }
}
