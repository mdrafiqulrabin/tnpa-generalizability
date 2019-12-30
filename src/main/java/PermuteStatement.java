import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
public class PermuteStatement extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private ArrayList<Node> mStatementNodes = new ArrayList<>();
    private ArrayList<Node> mDummyNodes = new ArrayList<>();

    PermuteStatement() {
        //System.out.println("\n[ PermuteStatement ]\n");
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
        mStatementNodes = locateTargetStatements(com, obj);
        mDummyNodes.add(new EmptyStmt());
        Common.applyToPlace(this, com, mJavaFile, mDummyNodes);
        super.visit(com, obj);
    }

    private ArrayList<Node> locateTargetStatements(CompilationUnit com, Object obj) {
        ArrayList<Node> statementNodes = new ArrayList<>();
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof Statement
                        && !Common.isNotPermeableStatement(node)) {
                    statementNodes.add(node);
                }
            }
        }.visitBreadthFirst(com);
        return statementNodes;
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node unused) {
        int cnt = 0;
        for (int i=0; i < mStatementNodes.size(); i++) {
            for (int j=i+1; j < mStatementNodes.size(); j++) {
                Statement stmti = (Statement) mStatementNodes.get(i);
                Statement stmtj = (Statement) mStatementNodes.get(j);
                if (stmti.getParentNode().equals(stmtj.getParentNode())) {
                    List<SimpleName> iIdentifiers = stmti.findAll(SimpleName.class);
                    List<SimpleName> jIdentifiers = stmtj.findAll(SimpleName.class);
                    List<SimpleName> ijIdentifiers = iIdentifiers.stream()
                            .filter(jIdentifiers::contains).collect(Collectors.toList());
                    if (ijIdentifiers.size() == 0) { //dependency check between i & j statement
                        List<SimpleName> bIdentifiers = new ArrayList<>();
                        for (int b=i+1; b<j; b++) {
                            Statement stmtb = (Statement) mStatementNodes.get(b);
                            bIdentifiers.addAll(stmtb.findAll(SimpleName.class));
                        }
                        List<SimpleName> ibIdentifiers = iIdentifiers.stream()
                                .filter(bIdentifiers::contains).collect(Collectors.toList());
                        if (ibIdentifiers.size() == 0) { //dependency check among i & internal statements
                            List<SimpleName> jbIdentifiers = jIdentifiers.stream()
                                    .filter(bIdentifiers::contains).collect(Collectors.toList());
                            if (jbIdentifiers.size() == 0) { //dependency check among j & internal statements
                                swapStatementNodes(com, i, j, ++cnt);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void swapStatementNodes(CompilationUnit com, int i, int j, int cnt) {
        CompilationUnit newCom = com.clone();
        ArrayList<Node> statementNodes = locateTargetStatements(newCom, null);
        Statement stmti = (Statement) statementNodes.get(i);
        Statement stmtj = (Statement) statementNodes.get(j);
        stmti.replace(stmtj.clone());
        stmtj.replace(stmti.clone());
        Common.saveTransformation(newCom, mJavaFile, String.valueOf(cnt));
    }

}
