package replete.scripting.plugin;

import javax.swing.ImageIcon;

import replete.SoftwareVersion;
import replete.plugins.ExtensionPoint;
import replete.plugins.Plugin;
import replete.scripting.ScriptingManagerGenerator;
import replete.scripting.beanshell.BeanShellScriptingManagerGenerator;
import replete.scripting.groovy.GroovyScriptingManagerGenerator;
import replete.scripting.jython.JythonScriptingManagerGenerator;
import replete.scripting.rscript.RScriptScriptingManagerGenerator;

public class RepleteScriptingPlugin implements Plugin {
    @Override
    public String getName() {
        return "Replete Scripting Plug-in";
    }
    @Override
    public String getVersion() {
        return SoftwareVersion.get().getFullVersionString();
    }
    @Override
    public String getProvider() {
        return "Sandia National Laboratories";
    }
    @Override
    public ImageIcon getIcon() {
        return null;
    }
    @Override
    public String getDescription() {
        return "This plug-in provides ...";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends ExtensionPoint>[] getExtensionPoints() {
        return new Class[] {
            ScriptingManagerGenerator.class
        };
    }

    @Override
    public ExtensionPoint[] getExtensions() {
        return new ExtensionPoint[] {
            new RScriptScriptingManagerGenerator(),
            new BeanShellScriptingManagerGenerator(),
            new JythonScriptingManagerGenerator(),
            new GroovyScriptingManagerGenerator()
        };
    }

    @Override
    public void start() {}
}
