import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class Common {

    static String mRootInputPath = "";
    static String mRootOutputPath = "";
    static String mSavePath = "";

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

    public static void inspectSourceCode(Object obj, File javaFile) {
    }

    static void setOutputPath(Object obj, File javaFile) {
        //assume '/transforms' in output path
        Common.mSavePath = Common.mRootOutputPath.replace("/transforms",
                "/transforms/"+obj.getClass().getSimpleName());
    }

    static CompilationUnit getParseUnit(File javaFile) {
        CompilationUnit root = null;
        try {
            String txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            if(!txtCode.startsWith("class")) txtCode = "class T { \n" + txtCode + "\n}";
            root = StaticJavaParser.parse(txtCode);
        } catch (Exception ex) {
            System.out.println("\n" + "Exception: " + javaFile.getPath());
            ex.printStackTrace();
            String error_dir = Common.mSavePath + "java_parser_error.txt";
            Common.saveErrText(error_dir, javaFile);
        }
        return root;
    }

    static void applyToPlace(Object obj, CompilationUnit com, File javaFile, ArrayList<Node> nodeList) {
        // apply to single place
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            CompilationUnit newCom = applyByObj(obj, javaFile, com.clone(), node.clone());
            if (newCom != null && Common.checkTransformation(com, newCom, javaFile, false)) {
                Common.saveTransformation(newCom, javaFile, String.valueOf(i+1));
            }
        }

        // apply to all place
        if (nodeList.size() > 1) {
            CompilationUnit oldCom = com.clone();
            nodeList.forEach((node) -> applyByObj(obj, javaFile, com, node));
            if (Common.checkTransformation(oldCom, com, javaFile, true)) {
                Common.saveTransformation(com, javaFile, String.valueOf(0));
            }
        }
    }

    static CompilationUnit applyByObj(Object obj, File javaFile, CompilationUnit com, Node node) {
        CompilationUnit newCom = null;
        try {
            if (obj instanceof RenameVariable) {
                newCom = ((RenameVariable) obj).applyTransformation(com, node);
            } else if (obj instanceof BooleanExchange) {
                newCom = ((BooleanExchange) obj).applyTransformation(com, node);
            } else if (obj instanceof LoopExchange) {
                newCom = ((LoopExchange) obj).applyTransformation(com, node);
            } else if (obj instanceof SwitchConditional) {
                newCom = ((SwitchConditional) obj).applyTransformation(com, node);
            } else if (obj instanceof PermuteStatement) {
                newCom = ((PermuteStatement) obj).applyTransformation(com, node);
            } else if (obj instanceof UnusedStatement) {
                newCom = ((UnusedStatement) obj).applyTransformation(com, node);
            } else if (obj instanceof UnreachableStatement) {
                newCom = ((UnreachableStatement) obj).applyTransformation(com, node);
            } else if (obj instanceof TryCatch) {
                newCom = ((TryCatch) obj).applyTransformation(com, node);
            }
        } catch (Exception ex) {
            System.out.println("\n" + "Exception: " + javaFile.getPath());
            ex.printStackTrace();
        }
        return newCom;
    }

    static Boolean checkTransformation(CompilationUnit bRoot, CompilationUnit aRoot,
                                       File javaFile, boolean writeFile) {
        MethodDeclaration mdBefore = (MethodDeclaration) (bRoot.getChildNodes().get(0)).getChildNodes().get(1);
        String mdBeforeStr = mdBefore.toString().replaceAll("\\s+", "");
        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
        String mdAfterStr = mdAfter.toString().replaceAll("\\s+", "");
        if (mdBeforeStr.compareTo(mdAfterStr) == 0) {
            if (writeFile) {
                String no_dir = Common.mSavePath + "no_transformation.txt";
                File targetFile = new File(no_dir);
                Common.saveErrText(no_dir, javaFile);
            }
            return false;
        }
        return true;
    }

    static void saveTransformation(CompilationUnit aRoot, File javaFile, String place) {
        String output_dir = Common.mSavePath + javaFile.getPath().replaceFirst(Common.mRootInputPath, "");
        output_dir = output_dir.substring(0, output_dir.lastIndexOf(".java")) + "_" + place + ".java";
        MethodDeclaration mdAfter = (MethodDeclaration) (aRoot.getChildNodes().get(0)).getChildNodes().get(1);
        Common.writeSourceCode(mdAfter, output_dir);
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

    static boolean isNotPermeableStatement(Node node) {
        return (node instanceof EmptyStmt
                || node instanceof LabeledStmt
                || node instanceof BreakStmt
                || node instanceof ContinueStmt
                || node instanceof ReturnStmt);
    }

}
