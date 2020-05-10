import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PermuteStatement extends VoidVisitorAdapter<Object> {
    private ArrayList<ArrayList<Node>> mBasicBlockNodes = new ArrayList<>();
    private final ArrayList<Node> mDummyNodes = new ArrayList<>();

    PermuteStatement() { }

    public void inspectSourceCode(CompilationUnit cu) {
        Common.setOutputPath(this);
        this.visit(cu, null);
    }

    @Override
    public void visit(CompilationUnit cu, Object obj) {
        mBasicBlockNodes = locateBasicBlockStatements(cu);
        mDummyNodes.add(new EmptyStmt());
        Common.applyToPlace(this, cu, mDummyNodes);
        super.visit(cu, obj);
    }

    private ArrayList<ArrayList<Node>> locateBasicBlockStatements(CompilationUnit cu) {
        ArrayList<Node> innerStatementNodes = new ArrayList<>();
        ArrayList<ArrayList<Node>> basicBlockNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof ExpressionStmt
                        && node.findAll(MethodCallExpr.class).size() == 0
                        && !Common.isNotPermeableStatement(node)) {
                    innerStatementNodes.add(node);
                } else {
                    if (innerStatementNodes.size() > 1) {
                        basicBlockNodes.add(new ArrayList<>(innerStatementNodes));
                    }
                    innerStatementNodes.clear();
                }
            }
        }.visitBreadthFirst(cu);
        return basicBlockNodes;
    }

    public CompilationUnit applyTransformation(CompilationUnit cu, Node ignore) {
        int cnt = 0;
        for (int k = 0; k < mBasicBlockNodes.size(); k++) {
            ArrayList<Node> basicBlockNodes = mBasicBlockNodes.get(k);
            for (int i = 0; i < basicBlockNodes.size(); i++) {
                for (int j = i + 1; j < basicBlockNodes.size(); j++) {
                    Statement stmt_i = (Statement) basicBlockNodes.get(i);
                    Statement stmt_j = (Statement) basicBlockNodes.get(j);
                    if (stmt_i.getParentNode().equals(stmt_j.getParentNode())) {
                        List<SimpleName> iIdentifiers = stmt_i.findAll(SimpleName.class);
                        List<SimpleName> jIdentifiers = stmt_j.findAll(SimpleName.class);
                        List<SimpleName> ijIdentifiers = iIdentifiers.stream()
                                .filter(jIdentifiers::contains).collect(Collectors.toList());
                        if (ijIdentifiers.size() == 0) { //dependency check between i & j statement
                            List<SimpleName> bIdentifiers = new ArrayList<>();
                            for (int b = i + 1; b < j; b++) {
                                Statement stmtb = (Statement) basicBlockNodes.get(b);
                                bIdentifiers.addAll(stmtb.findAll(SimpleName.class));
                            }
                            List<SimpleName> ibIdentifiers = iIdentifiers.stream()
                                    .filter(bIdentifiers::contains).collect(Collectors.toList());
                            if (ibIdentifiers.size() == 0) { //dependency check among i & internal statements
                                List<SimpleName> jbIdentifiers = jIdentifiers.stream()
                                        .filter(bIdentifiers::contains).collect(Collectors.toList());
                                if (jbIdentifiers.size() == 0) { //dependency check among j & internal statements
                                    swapStatementNodes(cu, k, i, j, ++cnt);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void swapStatementNodes(CompilationUnit cu, int k, int i, int j, int cnt) {
        CompilationUnit newCom = cu.clone();
        ArrayList<ArrayList<Node>> statementNodes = locateBasicBlockStatements(newCom);
        Statement stmt_i = (Statement) statementNodes.get(k).get(i);
        Statement stmt_j = (Statement) statementNodes.get(k).get(j);
        stmt_i.replace(stmt_j.clone());
        stmt_j.replace(stmt_i.clone());
        Common.saveTransformation(newCom, String.valueOf(cnt));
    }
}
