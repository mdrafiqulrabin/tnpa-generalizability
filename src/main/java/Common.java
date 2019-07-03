import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.UserDataKey;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class Common {
    public static final String SRC_PATH_ORIGINAL_CODE = "src/main/data/originalCode/";
    public static final String SRC_PATH_VARIABLE_RENAMING = "src/main/data/variableRenaming/";
    public static final String SRC_PATH_BOOLEAN_EXCHANGE = "src/main/data/booleanExchange/";

    public static final UserDataKey<Integer> VariableId = new UserDataKey<Integer>() {
    };
    public static final UserDataKey<String> VariableName = new UserDataKey<String>() {
    };

    public static void printLog(Object obj) {
        if (Flagger.showLog) {
            System.out.print(System.lineSeparator() + "[ ");
            System.out.print(obj);
            System.out.print(" ]" + System.lineSeparator());
        }
    }

    public static ArrayList<Path> getCodePaths(String rootPath) {
        ArrayList<Path> listOfPaths = new ArrayList<>();
        final FilenameFilter filter = (dir, name) -> dir.isDirectory() && name.toLowerCase().endsWith(".java");
        File[] listOfFiles = new File(rootPath).listFiles(filter);
        if (listOfFiles == null) return new ArrayList<>();
        for (File file : listOfFiles) {
            Path codePath = Paths.get(file.getPath());
            listOfPaths.add(codePath);
        }
        return listOfPaths;
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
