import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MethodExtractor implements Callable<Void> {

    @Override
    public Void call() {
        inspectDataset();
        return null;
    }

    private void inspectDataset() {
        String input_dir = "/Users/mdrafiqulrabin/Projects/TNP/dataset/java3/";
        String[] folders = StringUtils.split(input_dir, "/");
        String output_dir_part1 = Common.SRC_PATH_DATA + folders[folders.length - 1];
        String error_dir = Common.SRC_PATH_DATA + "javaparsererror.txt";

        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(input_dir),
                        new String[]{"java"},
                        true)
        );
        System.out.println(output_dir_part1 + " : " + javaFiles.size());

        javaFiles.forEach((javaFile) -> {
            try {
                CompilationUnit root = null;
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
                        List<Statement> mdStatements = md.findAll(Statement.class);
                        if (mdStatements.size() >= Common.Threshold.STATEMENTS_PER_METHOD) {
                            String output_dir_part2 = javaFile.getPath().replaceFirst(input_dir, "");
                            output_dir_part2 = output_dir_part2.substring(0, output_dir_part2.lastIndexOf(".java")) + "_" + md.getName() + ".txt";
                            String output_dir = output_dir_part1 + "/" + output_dir_part2;
                            Common.writeSourceCode(md, output_dir);
                        }
                    }
                }, null);
            } catch (Exception ex) {
                try {
                    System.out.println(javaFile.getPath());
                    Files.write(Paths.get(error_dir), (javaFile.getPath()+"\n").getBytes(), StandardOpenOption.APPEND);
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
                //ex.printStackTrace();
            }
        });
    }
}
