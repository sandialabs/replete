package replete.ui.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.event.ChangeListener;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import replete.event.ChangeNotifier;
import replete.io.FileUtil;
import replete.text.StringUtil;
import replete.util.Temp;

public class EvalRunner {
    protected ChangeNotifier messageNotifier = new ChangeNotifier(this);

    public void addMessageListener(ChangeListener messageListener) {
        messageNotifier.addListener(messageListener);
    }

    protected void message(String msg) {
        messageNotifier.setSource(msg);
        messageNotifier.fireStateChanged();
    }

    public EvalRunResults run(String inputText) {
        EvalRunResults results = new EvalRunResults();
        results.success = false;

        // Append last semi-colon if doesn't exist.
        inputText = StringUtil.ensureEndsWith(inputText.trim(), ";");

        // Replace "@@" tokens.
        inputText = inputText.replaceAll("@@", "System.out.println");
        File tmpDir = Temp.get();
        File tmpFile = Temp.get("EvalTemp.java");

        results.resolvedInput = inputText;

        try {
            StringBuilder builder = new StringBuilder();
            String imports =
                "import java.awt.*;\nimport java.awt.color.*;\nimport java.io.*;\n" +
                "import java.lang.reflect.*;\nimport java.math.*;\nimport java.util.*;\n" +
                "import javax.swing.*;\nimport java.util.regex.*;\n\n";
            builder.append(imports);
            builder.append("public class EvalTemp {\n    public static void main(String[] args) {\n        " + inputText + "\n    }\n}");
            results.sourceCode = builder.toString();
            FileUtil.writeTextContent(tmpFile, builder.toString());
        } catch(Exception e) {
            e.printStackTrace();
            return results;
        }

        message("== COMPILE ==\n");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if(compiler == null) {
            message("COULD NOT GET COMPILER.  ARE YOU USING JRE AND NOT JDK?\n");
            return results;
        }

        int result = compiler.run(null, null, null, tmpFile.getAbsolutePath());
        if(result != 0) {
            message("ERROR - SEE CONSOLE FOR OUTPUT\n\n");
            return results;
        }

        message("SUCCESS\n\n");

        message("== RUN ==\n");

        Runtime r = Runtime.getRuntime();
        try {
            Process p = r.exec("java EvalTemp", null, tmpDir);

            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            String stdout = "";
            while((line = reader.readLine()) != null) {
                stdout += line + "\n";
            }
            stdout = stdout.trim();
            if(!stdout.equals("")) {
                message("--- stdout ---\n");
                message(stdout);
            }

            is = p.getErrorStream();
            reader = new BufferedReader(new InputStreamReader(is));
            String stderr = "";
            while((line = reader.readLine()) != null) {
                stderr += line + "\n";
            }
            stderr = stderr.trim();
            if(!stderr.equals("")) {
                message("--- stderr ---\n");
                message(stderr);
            }

            reader.close();
            results.success = true;
        } catch(IOException e) {
            e.printStackTrace();
        }

        File tmpCls = Temp.get("EvalTemp.class");
        tmpFile.delete();
        tmpCls.delete();

        return results;
    }
}
