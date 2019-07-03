import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    @Override
    public Void call() {
        inspectSourceCode();
        return null;
    }

    private void inspectSourceCode() {
        ArrayList<Path> codePaths = Common.getCodePaths(Common.SRC_PATH_ORIGINAL_CODE);
        codePaths.forEach((codePath) -> {
            try {
                System.out.println(codePath);
                String codeText = new String(Files.readAllBytes(codePath));
                if(!codeText.startsWith("class")) codeText = "class C { \n" + codeText + "\n}";
                variableRenaming(JavaParser.parse(codeText), codePath);
                booleanExchange(JavaParser.parse(codeText), codePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void variableRenaming(CompilationUnit comUnit, Path codePath) {
        String codeSavePath = Common.SRC_PATH_VARIABLE_RENAMING + "/" + codePath.getFileName();
        new VariableRenaming().visit(comUnit, null);
        Common.writeSourceCode(comUnit, codeSavePath);
    }

    private void booleanExchange(CompilationUnit comUnit, Path codePath) {
        String codeSavePath = Common.SRC_PATH_BOOLEAN_EXCHANGE + "/" + codePath.getFileName();
        new BooleanExchange().visit(comUnit, null);
        Common.writeSourceCode(comUnit, codeSavePath);
    }

}
