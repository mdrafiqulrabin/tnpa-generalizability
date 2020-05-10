import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


public final class Common {

    static String ROOT_INPUT_DIR = "";
    static String ROOT_OUTPUT_DIR = "";
    static File CURRENT_JAVA_FILE = null;
    static String OUTPUT_JAVA_FILE = "";

    static final DataKey<Integer> KEY_VARIABLE_ID = new DataKey<Integer>() {};
    static final DataKey<String> KEY_VARIABLE_NAME = new DataKey<String>() {};

    static void setOutputPath(Object obj) {
        Common.OUTPUT_JAVA_FILE = Paths.get(
                Common.ROOT_OUTPUT_DIR,
                obj.getClass().getSimpleName(),
                Common.CURRENT_JAVA_FILE.getPath().replaceFirst(Common.ROOT_INPUT_DIR, "")
        ).toString();
    }

    static CompilationUnit getParseUnit(File javaFile) {
        CompilationUnit root = null;
        try {
            String txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            txtCode = "class T { \n" + txtCode + "\n}";
            root = StaticJavaParser.parse(txtCode);
        } catch (Exception ignore) { }
        return root;
    }

    static void applyToPlace(Object obj, CompilationUnit cu, ArrayList<Node> nodeList) {
        // apply to single place
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            CompilationUnit newCu = applyByObj(obj, cu.clone(), node.clone());
            if (newCu != null && Common.checkTransformation(cu, newCu)) {
                Common.saveTransformation(newCu, String.valueOf(i+1));
            }
        }

        // apply to all place
        if (nodeList.size() > 1 && isAllPlaceApplicable(obj)) {
            CompilationUnit oldCu = cu.clone();
            nodeList.forEach((node) -> applyByObj(obj, cu, node));
            if (Common.checkTransformation(oldCu, cu)) {
                Common.saveTransformation(cu, String.valueOf(0));
            }
        }
    }

    static CompilationUnit applyByObj(Object obj, CompilationUnit cu, Node node) {
        CompilationUnit newCu = null;
        try {
            if (obj instanceof VariableRenaming) {
                newCu = ((VariableRenaming) obj).applyTransformation(cu, node);
            } else if (obj instanceof BooleanExchange) {
                newCu = ((BooleanExchange) obj).applyTransformation(cu, node);
            } else if (obj instanceof LoopExchange) {
                newCu = ((LoopExchange) obj).applyTransformation(cu, node);
            } else if (obj instanceof SwitchToIf) {
                newCu = ((SwitchToIf) obj).applyTransformation(cu, node);
            } else if (obj instanceof PermuteStatement) {
                newCu = ((PermuteStatement) obj).applyTransformation(cu, node);
            } else if (obj instanceof UnusedStatement) {
                newCu = ((UnusedStatement) obj).applyTransformation(cu, node);
            }
        } catch (Exception ignore) { }
        return newCu;
    }

    static Boolean checkTransformation(CompilationUnit oldCu, CompilationUnit newCu) {
        MethodDeclaration oldMd = (MethodDeclaration) (oldCu.getChildNodes().get(0)).getChildNodes().get(1);
        String oldMdStr = oldMd.toString().trim()
                .replaceAll("\n", "").replaceAll("\\s+", "");
        MethodDeclaration newMd = (MethodDeclaration) (newCu.getChildNodes().get(0)).getChildNodes().get(1);
        String newMdStr = newMd.toString().trim()
                .replaceAll("\n", "").replaceAll("\\s+", "");
        return oldMdStr.compareTo(newMdStr) != 0;
    }

    static void saveTransformation(CompilationUnit newCu, String place) {
        String transformFile = Common.OUTPUT_JAVA_FILE;
        transformFile = transformFile.substring(0, transformFile.lastIndexOf(".java")) + "_" + place + ".java";
        MethodDeclaration newMd = (MethodDeclaration) (newCu.getChildNodes().get(0)).getChildNodes().get(1);
        Common.writeSourceCode(newMd, transformFile);
    }

    static void writeSourceCode(MethodDeclaration modMd, String transformFile) {
        File targetFile = new File(transformFile).getParentFile();
        if (targetFile.exists() || targetFile.mkdirs()) {
            try (PrintStream ps = new PrintStream(transformFile)) {
                String tfSourceCode = modMd.toString();
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

    static boolean isAllPlaceApplicable(Object obj) {
        return (obj instanceof VariableRenaming
                || obj instanceof BooleanExchange
                || obj instanceof LoopExchange
                || obj instanceof SwitchToIf);
    }
}
