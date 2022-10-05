import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;


public class SwitchToIf extends VoidVisitorAdapter<Object> {
    private final ArrayList<Node> mSwitchNodes = new ArrayList<>();

    SwitchToIf() { }

    public void inspectSourceCode(CompilationUnit cu) {
        Common.setOutputPath(this);
        this.visit(cu, null);
    }

    @Override
    public void visit(CompilationUnit cu, Object obj) {
        locateSwitches(cu);
        Common.applyToPlace(this, cu, mSwitchNodes);
        super.visit(cu, obj);
    }

    private void locateSwitches(CompilationUnit cu) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof SwitchStmt) {
                    mSwitchNodes.add(node);
                }
            }
        }.visitPreOrder(cu);
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node switchNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.equals(switchNode)) {
                    ArrayList<Object> ifStmts = new ArrayList<>();
                    if (((SwitchStmt) node).getEntries().size() == 0) {
                        // empty
                        ifStmts.add(getIfStmt(node, null));
                    } else {
                        BlockStmt defaultBlockStmt = null;
                        for (SwitchEntry switchEntry : ((SwitchStmt) node).getEntries()) {
                            if (switchEntry.getLabels().size() != 0) {
                                // cases
                                ifStmts.add(getIfStmt(node, switchEntry));
                            } else {
                                if (((SwitchStmt) node).getEntries().size() == 1) {
                                    // default without cases
                                    ifStmts.add(getIfStmt(node, switchEntry));
                                } else {
                                    // default with cases
                                    defaultBlockStmt = getBlockStmt(switchEntry);
                                }
                            }
                        }
                        if (defaultBlockStmt != null) ifStmts.add(defaultBlockStmt); // default at end with cases
                        for (int i = 0; i < ifStmts.size() - 1; i++) {
                            ((IfStmt) ifStmts.get(i)).setElseStmt((Statement) ifStmts.get(i + 1));
                        }
                    }
                    node.replace((IfStmt) ifStmts.get(0));
                }
            }
        }.visitPreOrder(com);
        return com;
    }

    private Expression getBinaryExpr(Node switchNode, SwitchEntry switchEntry) {
        BinaryExpr binaryExpr = new BinaryExpr();
        binaryExpr.setLeft(((SwitchStmt) switchNode).getSelector());
        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
        if (switchEntry != null && switchEntry.getLabels().size() != 0) {
            binaryExpr.setRight(switchEntry.getLabels().get(0)); // case(?)
        } else {
            binaryExpr.setRight(((SwitchStmt) switchNode).getSelector()); // only default
        }
        return binaryExpr;
    }

    private BlockStmt getBlockStmt(SwitchEntry switchEntry) {
        BlockStmt blockStmt = new BlockStmt();
        if (switchEntry != null) {
            switchEntry.getStatements().forEach((stmt) -> {
                if (!(stmt instanceof BreakStmt)) blockStmt.addStatement(stmt);
            });
        }
        return blockStmt;
    }

    private IfStmt getIfStmt(Node switchNode, SwitchEntry switchEntry) {
        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(getBinaryExpr(switchNode, switchEntry));
        ifStmt.setThenStmt(getBlockStmt(switchEntry));
        return ifStmt;
    }
}
