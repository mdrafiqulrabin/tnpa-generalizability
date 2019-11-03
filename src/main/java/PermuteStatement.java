import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
public class PermuteStatement extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private ArrayList<Node> mStatementNodes = new ArrayList<>();

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
        locateTargetStatements(com, obj);
        Common.applyToPlace(this, com, mJavaFile, mStatementNodes);
        super.visit(com, obj);
    }

    private void locateTargetStatements(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof Statement) {
                    mStatementNodes.add(node);
                }
            }
        }.visitBreadthFirst(com);
        //System.out.println("StatementNodes : " + mStatementNodes.size());
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node stmtNode) {
        ArrayList<Node> statementNodes = mStatementNodes;
        int idx = statementNodes.indexOf((Statement)stmtNode);
        if (idx >= statementNodes.size() - 1  || Common.isNotPermeableStatement(statementNodes.get(idx))
                || Common.isNotPermeableStatement(statementNodes.get(idx+1))){
            return com;
        }

        Statement stmti = (Statement) statementNodes.get(idx);
        Statement stmtj = (Statement) statementNodes.get(idx+1);

        if (stmti.getParentNode().equals(stmtj.getParentNode())) {
            List<SimpleName> iIdentifiers = stmti.findAll(SimpleName.class);
            List<SimpleName> jIdentifiers = stmtj.findAll(SimpleName.class);
            List<SimpleName> ijIdentifiers = iIdentifiers.stream().filter(jIdentifiers::contains).collect(Collectors.toList());
            if (ijIdentifiers.size() == 0) {

                new TreeVisitor() {
                    @Override
                    public void process(Node node) {
                        if (node.equals(stmti)) {
                            node.replace(stmtj.clone());
                            node.setParentNode(stmtj.getParentNode().orElse(null));
                        } else if (node.equals(stmtj)) {
                            node.replace(stmti.clone());
                            node.setParentNode(stmti.getParentNode().orElse(null));
                        }
                    }
                }.visitPreOrder(com);

                if (Common.mApplyAll) {
                    Collections.swap(mStatementNodes, idx, idx + 1);
                }
            }
        }
        return com;
    }

}
