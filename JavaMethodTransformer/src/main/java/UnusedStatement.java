import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;


public class UnusedStatement extends VoidVisitorAdapter<Object> {
    private final ArrayList<Node> mDummyNodes = new ArrayList<>();

    UnusedStatement() { }

    public void inspectSourceCode(CompilationUnit cu) {
        Common.setOutputPath(this);
        this.visit(cu, null);
    }

    @Override
    public void visit(CompilationUnit cu, Object obj) {
        mDummyNodes.add(new EmptyStmt());
        Common.applyToPlace(this, cu, mDummyNodes);
        super.visit(cu, obj);
    }

    public CompilationUnit applyTransformation(CompilationUnit cu, Node ignore) {
        Statement unusedStmt = getUnusedStatement(cu);
        if (unusedStmt != null) {
            BlockStmt blockStmt = new BlockStmt();
            for (Statement statement : cu.clone().findFirst(MethodDeclaration.class)
                    .flatMap(MethodDeclaration::getBody).get().getStatements()) {
                blockStmt.addStatement(statement);
            }
            int min = 0, max = blockStmt.getStatements().size() - 1;
            int place = new Random().nextInt(max - min + 1) + min;
            blockStmt.addStatement(place, unusedStmt);
            if (cu.findFirst(MethodDeclaration.class).isPresent()) {
                MethodDeclaration md = cu.findFirst(MethodDeclaration.class).get();
                md.setBody(blockStmt);
            }
        }
        return cu;
    }

    private Statement getUnusedStatement(CompilationUnit cu) {
        // check whether timestamp already exits
        List<String> strVariables = cu.findAll(SimpleName.class)
                .stream().map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        if (!strVariables.contains("timestamp")) {
            // set timestamp as unused statement
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            String unusedStr = "String timestamp = \"" + timestamp + "\";";
            return StaticJavaParser.parseStatement(unusedStr);
        }
        return null;
    }
}
