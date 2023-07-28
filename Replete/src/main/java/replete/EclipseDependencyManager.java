package replete;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import replete.io.FileUtil;
import replete.text.StringUtil;

public class EclipseDependencyManager {

    private static Map<File, Project> allProjects = new HashMap<>();
    private static Map<File, Library> allLibraries = new HashMap<>();
    private static final String DOT_CLASS_PATH = ".classpath";

    public static void main(String[] args) {
        File f = new File("C:\\Users\\dtrumbo\\work\\eclipse-main");
        File aa = new File(f, "Avondale");
        Project proj = processProject(aa);
        System.out.println(proj.print());
    }

    private static Project processProject(File projectPath) {
        Project proj = allProjects.get(projectPath);
        if(proj != null) {
            return proj;
        }
        Project p = new Project(projectPath);
        File cp = new File(projectPath, DOT_CLASS_PATH);
        String content = FileUtil.getTextContent(cp);
        String[] lines = content.split("\\r\\n|\\n|\\r[^\\n]");
        for(String line : lines) {
            if(line.contains("kind=\"lib\"")) {
                String[] captures = StringUtil.extractCaptures(line, ".*path=\"(.*)\".*");
                String path = captures[0];
                File cur = projectPath;
                if(path.startsWith("/")) {
                    cur = cur.getParentFile();
                }
                File libPath = new File(cur, path);
                Library lib = allLibraries.get(libPath);
                if(lib == null) {
                    lib = new Library(libPath, path);
                }
                p.libraryDependencies.add(lib);
            } else if(line.contains("kind=\"src\"")) {
                String[] captures = StringUtil.extractCaptures(line, ".*path=\"(.*)\".*");
                String path = captures[0];
                if(path.startsWith("/")) {
                    File cur = projectPath;
                    cur = cur.getParentFile();
                    File projPath = new File(cur, path);
                    Project proj2 = processProject(projPath);
                    p.projectDependencies.add(proj2);
                } else {
                    File srcFolder = new File(projectPath, path);
                    SourceFolder sf = new SourceFolder(srcFolder, path);
                    p.sourceFolders.add(sf);
                }
            }
        }
        return p;
    }

    private static class Project {
        File path;
        List<SourceFolder> sourceFolders = new ArrayList<>();
        List<Library> libraryDependencies = new ArrayList<>();
        List<Project> projectDependencies = new ArrayList<>();
        public Project(File path) {
            this.path = path;
        }
        public String print() {
            StringBuilder buffer = new StringBuilder();
            PrintContext context = new PrintContext();
            printDep(context, buffer, 0);
            return buffer.toString();
        }
        public void printDep(PrintContext context, StringBuilder buffer, int level) {
            String sp = StringUtil.spaces(level * 4);
            String sp2 = StringUtil.spaces((level + 1) * 4);
            String sp3 = StringUtil.spaces((level + 2) * 4);

            buffer.append(sp + "[Project] " + path + "\n");
            if(context.printedProjects.containsKey(path)) {
                buffer.append(sp2 + "<Previously Shown>\n");
            } else {
                context.printedProjects.put(path, this);
                buffer.append(sp2 + "Source Folders:\n");
                for(SourceFolder sourceFolder  : sourceFolders) {
                    sourceFolder.printDep(context, buffer, level + 2);
                }
                buffer.append(sp2 + "Project Dependencies:\n");
                if(projectDependencies.isEmpty()) {
                    buffer.append(sp3 + "<None>\n");
                } else {
                    for(Project project : projectDependencies) {
                        project.printDep(context, buffer, level + 2);
                    }
                }
                buffer.append(sp2 + "Library Dependencies:\n");
                if(libraryDependencies.isEmpty()) {
                    buffer.append(sp3 + "<None>\n");
                } else {
                    for(Library library : libraryDependencies) {
                        library.printDep(context, buffer, level + 2);
                    }
                }
            }
        }
    }
    private static class PrintContext {
        private Map<File, Project> printedProjects = new HashMap<>();
    }
    private static class SourceFolder {
        File path;
        String shortPath;
        public SourceFolder(File path, String shortPath) {
            this.path = path;
            this.shortPath = shortPath;
        }
        public void printDep(PrintContext context, StringBuilder buffer, int level) {
            String sp = StringUtil.spaces(level * 4);
            buffer.append(sp + "[Source Folder] " + shortPath + "\n"); //" (" + path + ")\n");
        }
    }
    private static class Library {
        File path;
        String shortPath;
        String name;
        String description;
        String notes;
        String url;
        String version;
        public Library(File path, String shortPath) {
            this.path = path;
            this.shortPath = shortPath;
        }
        public void printDep(PrintContext context, StringBuilder buffer, int level) {
            String sp = StringUtil.spaces(level * 4);
            buffer.append(sp + "[Library] " + shortPath + "\n"); //" (" + path + ")\n");
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((path == null) ? 0 : path.hashCode());
            return result;
        }
        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            Library other = (Library) obj;
            if(path == null) {
                if(other.path != null) {
                    return false;
                }
            } else if(!path.equals(other.path)) {
                return false;
            }
            return true;
        }
    }
}
