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
        writeSourceCode();
        return null;
    }

    private void writeSourceCode() {
        Common.printLog("ASTExplorer -> writeSourceCode():");
        String tfSourceCode = mCompilationUnit.toString();
        try (PrintStream ps = new PrintStream(Common.SRC_PATH_VARIABLE_RENAMING)) {
            ps.println(tfSourceCode);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println(tfSourceCode);
    }

    private void inspectSourceCode() {
        Common.printLog("ASTExplorer -> inspectSourceCode():");
        mCompilationUnit = JavaParser.parse(mSourceCode);
        new VariableRenaming().visit(mCompilationUnit, null);
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
}
