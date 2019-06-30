import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    private String mSourceCode = null;
    private CompilationUnit mCompilationUnit = null;

    @Override
    public Void call() {
        readSourceCode();
        inspectSourceCode();
        return null;
    }

    private void readSourceCode() {
        Common.printLog("ASTExplorer -> readSourceCode():");
        try {
            Path codePath = Paths.get(Common.SRC_PATH_ORIGINAL);
            mSourceCode = new String(Files.readAllBytes(codePath));
            System.out.println(mSourceCode);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void inspectSourceCode() {
        //Common.printLog("ASTExplorer -> inspectSourceCode():");
        variableRenaming();
        booleanExchange();
    }

    private void variableRenaming() {
        mCompilationUnit = JavaParser.parse(mSourceCode);
        new VariableRenaming().visit(mCompilationUnit, null);
        writeSourceCode(Common.SRC_PATH_VARIABLE_RENAMING);
    }

    private void booleanExchange() {
        mCompilationUnit = JavaParser.parse(mSourceCode);
        new BooleanExchange().visit(mCompilationUnit, null);
        writeSourceCode(Common.SRC_PATH_BOOLEAN_EXCHANGE);
    }

    private void writeSourceCode(String filename) {
        Common.printLog("ASTExplorer -> writeSourceCode():");
        String tfSourceCode = mCompilationUnit.toString();
        try (PrintStream ps = new PrintStream(filename)) {
            ps.println(tfSourceCode);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println(tfSourceCode);
    }
}
