import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LogStatement extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private ArrayList<Node> mDummyNodes = new ArrayList<>();

    LogStatement() {
        //System.out.println("\n[ LogStatement ]\n");
    }

    public void inspectSourceCode(File javaFile) {
        this.mJavaFile = javaFile;
        Common.setOutputPath(this, mJavaFile);
        CompilationUnit root = Common.getParseUnit(mJavaFile);
        if (root != null) {
            this.visit(root.clone(), null);
        }
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        mDummyNodes.add(new EmptyStmt());
        Common.applyToPlace(this, com, mJavaFile, mDummyNodes);
        super.visit(com, obj);
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node unused) {
        BlockStmt blockStmt = new BlockStmt();
        for (Statement statement : com.findFirst(MethodDeclaration.class)
                .flatMap(MethodDeclaration::getBody).get().getStatements()) {
            blockStmt.addStatement(statement);
        }
        blockStmt.addStatement(0, getLogStatement()); //beginning of stmt
        if (com.findFirst(MethodDeclaration.class).isPresent()) {
            MethodDeclaration md = com.findFirst(MethodDeclaration.class).get();
            md.setBody(blockStmt);
        }
        return com;
    }

    private Statement getLogStatement() {
        String logStr = "System.out.println(\"dummy log\");";
        return StaticJavaParser.parseStatement(logStr);
    }
}