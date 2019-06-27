import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    private String mCodePath = null;
    private String mCodeText = null;
    private CompilationUnit mCompilationUnit = null;

    ASTExplorer(String path) {
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
        readSourceCode();
        inspectSourceCode();
        showSourceCode();
        return null;
    }

    private void inspectSourceCode() {
        Common.printLog("ASTExplorer -> inspectSourceCode():");
        try {
            mCompilationUnit = JavaParser.parse(mCodeText);
            callMethodVisitor(mCompilationUnit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showSourceCode() {
        Common.printLog("ASTExplorer -> showSourceCode():");
        System.out.println(mCompilationUnit.toString());
    }

    private void callMethodVisitor(CompilationUnit compUnit) {
        ASTVisitor astVisitor = new ASTVisitor();
        astVisitor.visit(compUnit, null);
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
