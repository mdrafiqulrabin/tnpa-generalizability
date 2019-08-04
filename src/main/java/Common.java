import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class Common {
    public static final String SRC_PATH_ORIGINAL = "src/main/data/original/";

    public static final DataKey<Integer> VariableId = new DataKey<Integer>() {};
    public static final DataKey<String> VariableName = new DataKey<String>() {};

    public static ArrayList<Path> getFilePaths(String rootPath) {
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

    public static void inspectSourceCode(Object obj) {
        ArrayList<Path> files = Common.getFilePaths(Common.SRC_PATH_ORIGINAL);
        files.forEach((file) -> {
            try {
                System.out.println("File = " + file + "\n");

                String txtCode = new String(Files.readAllBytes(file));
                if(!txtCode.startsWith("class")) txtCode = "class T { \n" + txtCode + "\n}";
                CompilationUnit root = StaticJavaParser.parse(txtCode);
                System.out.println("Original = \n" + root + "\n");

                if (obj instanceof RenameVariable) {
                    ((RenameVariable)obj).visit(root, null);
                } else if (obj instanceof BooleanExchange) {
                    ((BooleanExchange)obj).visit(root, null);
                } else if (obj instanceof LoopExchange) {
                    ((LoopExchange)obj).visit(root, null);
                } else if (obj instanceof SwitchConditional) {
                    ((SwitchConditional)obj).visit(root, null);
                }

                System.out.println("Transformed = \n" + root + "\n");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void writeSourceCode(CompilationUnit comUnit, String codePath) {
        String tfSourceCode = comUnit.toString();
        try (PrintStream ps = new PrintStream(codePath)) {
            ps.println(tfSourceCode);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        System.out.println(tfSourceCode);
    }

}
