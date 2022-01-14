import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MethodExtractor implements Callable<Void> {
    private String mInputPath;
    private String mOutputPath;

    MethodExtractor(String inpPath, String outPath) {
        if (!inpPath.endsWith("/")) {
            inpPath = inpPath + "/";
        }
        this.mInputPath = inpPath;

        if (!outPath.endsWith("/")) {
            outPath = outPath + "/";
        }
        this.mOutputPath = outPath;
    }

    @Override
    public Void call() {
        inspectDataset();
        return null;
    }

    private void inspectDataset() {
        String input_dir = mInputPath;
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(input_dir),
                        new String[]{"java"},
                        true)
        );
        System.out.println(input_dir + " : " + javaFiles.size());

        javaFiles.forEach((javaFile) -> {
            try {
                CompilationUnit root;
                try {
                    root = StaticJavaParser.parse(javaFile);
                } catch (Exception ex) {
                    String codeText = new String(Files.readAllBytes(javaFile.toPath()));
                    codeText = "class C { \n" + codeText + "\n}";
                    root = StaticJavaParser.parse(codeText);
                }
                if (root == null) return;
                root.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(MethodDeclaration md, Void arg) {
                        md = handleParseProblemException(md);
                        List<Statement> mdStatements = md.findAll(Statement.class);
                        if (mdStatements.size() > Common.STATEMENTS_PER_METHOD) {
                            String output_dir_part1 = mOutputPath;
                            String output_dir_part2 = javaFile.getPath().replaceFirst(input_dir, "");
                            output_dir_part2 = output_dir_part2.substring(0, output_dir_part2.lastIndexOf(".java"));
                            output_dir_part2 += "_" + md.getName() + ".java";
                            String output_dir = output_dir_part1 + output_dir_part2;
                            Common.writeSourceCode(md, output_dir);
                        }
                    }
                }, null);
            } catch (Exception ex) {
                try {
                    System.out.println("\n" + "Exception: " + javaFile.getPath());
                    String error_dir = mOutputPath + "java_parser_error.txt";
                    File targetFile = new File(error_dir);
                    if (targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs()) {
                        if (targetFile.exists() || targetFile.createNewFile()) {
                            Files.write(Paths.get(error_dir),
                                    (javaFile.getPath() + "\n").getBytes(),
                                    StandardOpenOption.APPEND);
                        }
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
                ex.printStackTrace();
            }
        });
    }

    private MethodDeclaration handleParseProblemException(MethodDeclaration md) {
        //ParseProblemException: 'default' is not allowed here.
        md = md.removeModifier(Modifier.Keyword.DEFAULT);
        return md;
    }
}
