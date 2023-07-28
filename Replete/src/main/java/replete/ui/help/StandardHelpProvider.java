package replete.ui.help;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;

import replete.errors.RuntimeConvertedException;
import replete.ui.help.model.HelpAlbum;
import replete.web.UrlUtil;
import replete.xstream.XStreamWrapper;

public abstract class StandardHelpProvider extends HelpProvider {


    ////////////
    // FIELDS //
    ////////////

    private URL currentAlbumUrl;
    private URL currentContentDirUrl;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StandardHelpProvider() {
        loadDefaultPaths();
    }

    public void loadDefaultPaths() {
        currentAlbumUrl      = getDefaultAlbumUrl();
        currentContentDirUrl = getDefaultContentDirUrl();
    }

    public URL getDefaultAlbumUrl() {
        URL thisPackage = getClass().getResource("");
        return UrlUtil.url(thisPackage + getDefaultAlbumName() + ".album");
    }
    public URL getDefaultContentDirUrl() {
        URL thisPackage = getClass().getResource("");
        return UrlUtil.url(thisPackage + getDefaultAlbumName() + "-content");
    }

    public URL getContentResource(String path) {
        return UrlUtil.url(currentContentDirUrl + "/" + path);
    }


    //////////////////////////
    // ACCESSORS / MUTATORS //
    //////////////////////////

    // Accessors

    public URL getCurrentAlbumUrl() {
        return currentAlbumUrl;
    }
    public URL getCurrentContentDirUrl() {
        return currentContentDirUrl;
    }

    // Mutators

    public StandardHelpProvider setCurrentAlbumUrl(URL currentAlbumUrl) {
        this.currentAlbumUrl = currentAlbumUrl;
        return this;
    }
    public StandardHelpProvider setCurrentContentDirUrl(URL currentContentDirUrl) {
        this.currentContentDirUrl = currentContentDirUrl;
        return this;
    }


    //////////////
    // ABSTRACT //
    //////////////

    protected abstract String getDefaultAlbumName();

    protected String getWorkspaceOutputDir() {
        return "bin";
    }
    protected String getWorkspaceSourceDir() {
        return "src";
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public Class<?>[] getCoordinatedClasses() {   // Coordinated classes are not used for these generators
        return null;
    }

    @Override
    public void loadAlbum() {
        try {
            InputStream inputStream = currentAlbumUrl.openStream();
            album = (HelpAlbum) XStreamWrapper.loadTarget(inputStream);
        } catch(IOException e) {
            album = new HelpAlbum(getDefaultAlbumName());
        }
    }

    @Override
    public void saveAlbum() {
        if(currentAlbumUrl.getProtocol().equals("file")) {
            File currentAlbumFileOutputFile = new File(currentAlbumUrl.getFile());
            File currentAlbumFileSourceFile = convertWorkspaceFileOutputToSource(currentAlbumFileOutputFile);

            try {
                XStreamWrapper.writeToFile(album, currentAlbumFileOutputFile);
                XStreamWrapper.writeToFile(album, currentAlbumFileSourceFile);
            } catch(IOException e) {
                throw new RuntimeConvertedException(e);
            }
        }

        if(currentContentDirUrl.getProtocol().equals("file")) {
            File currentContentDirOutputFile = new File(currentContentDirUrl.getFile());
            File currentContentDirSourceFile = convertWorkspaceFileOutputToSource(currentContentDirOutputFile);

            currentContentDirOutputFile.mkdirs();
            currentContentDirSourceFile.mkdirs();
        }
    }

    public File convertWorkspaceFileOutputToSource(File outputFile) {
        String out = getWorkspaceOutputDir();
        String src = getWorkspaceSourceDir();
        String outPattern = out.replaceAll("[\\\\/]", "(.)");
        String srcPattern = src.replaceAll("[\\\\/]", Matcher.quoteReplacement("$1"));
        if(!outPattern.startsWith("(.)")) {
            outPattern = "(.)" + outPattern;
        }
        if(!srcPattern.startsWith("$1")) {
            srcPattern = "$1" + srcPattern;
        }
        return new File(outputFile.getAbsolutePath().replaceAll(outPattern, srcPattern));
    }
}
