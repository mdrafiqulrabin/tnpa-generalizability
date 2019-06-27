import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    String mCodePath = null;
    String mCodeText = null;

    public ASTExplorer(String path) {
        this.mCodePath = path;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Void call() throws Exception {
        Common.printLog("ASTExplorer -> call():");
        readSourceCode();
        inspectSourceCode();
        return null;
    }

    private void inspectSourceCode() {
        Common.printLog("ASTExplorer -> inspectSourceCode():");
        try {
            CompilationUnit compUnit = JavaParser.parse(mCodeText);
            System.out.println(compUnit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readSourceCode() {
        Common.printLog("ASTExplorer -> readSourceCode():");
        try {
            mCodeText = new String(Files.readAllBytes(Paths.get(this.mCodePath)));
            System.out.println(mCodeText);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
