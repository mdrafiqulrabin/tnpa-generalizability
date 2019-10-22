import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Common {

    static String mInputPath = "";
    static String mOutputPath = "";

    static final DataKey<Integer> VariableId = new DataKey<Integer>() {};
    static final DataKey<String> VariableName = new DataKey<String>() {};

    static ArrayList<Path> getFilePaths(String rootPath) {
        ArrayList<Path> listOfPaths = new ArrayList<>();
        final FilenameFilter filter = (dir, name) -> dir.isDirectory() && name.toLowerCase().endsWith(".txt");
        File[] listOfFiles = new File(rootPath).listFiles(filter);
        if (listOfFiles == null) return new ArrayList<>();
        for (File file : listOfFiles) {
            Path codePath = Paths.get(file.getPath());
            listOfPaths.add(codePath);
        }
        return listOfPaths;
    }

    static void inspectSourceCode(Object obj, File javaFile) {
        try {
            // parse java file
            String txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            if(!txtCode.startsWith("class")) txtCode = "class T { \n" + txtCode + "\n}";
            CompilationUnit root = StaticJavaParser.parse(txtCode);
            MethodDeclaration mdBefore = (MethodDeclaration)(root.getChildNodes().get(0)).getChildNodes().get(1);
            String mdBeforeStr = mdBefore.toString().replaceAll("\\s+","");

            // visit root
            if (obj instanceof RenameVariable) {
                ((RenameVariable)obj).visit(root, null);
            } else if (obj instanceof BooleanExchange) {
                ((BooleanExchange)obj).visit(root, null);
            } else if (obj instanceof LoopExchange) {
                ((LoopExchange)obj).visit(root, null);
            } else if (obj instanceof SwitchConditional) {
                ((SwitchConditional)obj).visit(root, null);
            } else if (obj instanceof PermuteStatement) {
                ((PermuteStatement)obj).visit(root, null);
            }

            // check whether transform
            MethodDeclaration mdAfter = (MethodDeclaration)(root.getChildNodes().get(0)).getChildNodes().get(1);
            String mdAfterStr = mdAfter.toString().replaceAll("\\s+","");
            if (mdBeforeStr.compareTo(mdAfterStr) == 0) {
                String no_dir = Common.mOutputPath + obj.getClass().getSimpleName() + "/no_transformation.txt";
                File targetFile = new File(no_dir);
                saveErrText(no_dir, javaFile);
                return;
            }

            // save transformed file
            String input_dir = Common.mInputPath;
            String[] folders = StringUtils.split(input_dir, "/");
            String output_dir = Common.mOutputPath + obj.getClass().getSimpleName();
            output_dir = output_dir + "/" + folders[folders.length - 1];
            output_dir = output_dir + "/" + javaFile.getPath().replaceFirst(input_dir, "");
            Common.writeSourceCode(mdAfter, output_dir);
        } catch (Exception ex) {
            System.out.println("\n" + "Exception: " + javaFile.getPath());
            String error_dir = Common.mOutputPath + obj.getClass().getSimpleName() + "/java_parser_error.txt";
            saveErrText(error_dir, javaFile);
            ex.printStackTrace();
        }
    }

    static void saveErrText(String error_dir, File javaFile) {
        try {
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
    }

    static void writeSourceCode(MethodDeclaration md, String codePath) {
        File targetFile = new File(codePath).getParentFile();
        if (targetFile.exists() || targetFile.mkdirs()) {
            try (PrintStream ps = new PrintStream(codePath)) {
                String tfSourceCode = md.toString();
                ps.println(tfSourceCode);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

}
