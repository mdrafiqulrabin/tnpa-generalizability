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
public class UnreachableStatement extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private ArrayList<Node> mDummyNodes = new ArrayList<>();

    UnreachableStatement() {
        //System.out.println("\n[ UnreachableStatement ]\n");
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
        int min = 0, max = blockStmt.getStatements().size() - 1;
        int place = new Random().nextInt(max - min + 1) + min;
        blockStmt.addStatement(place, getUnreachableStatement());
        if (com.findFirst(MethodDeclaration.class).isPresent()) {
            MethodDeclaration md = com.findFirst(MethodDeclaration.class).get();
            md.setBody(blockStmt);
        }
        return com;
    }

    private Statement getUnreachableStatement() {
        String unreachableStr = "if(false){System.out.println(\"an unreachable statement\");}";
        return StaticJavaParser.parseStatement(unreachableStr);
    }
}