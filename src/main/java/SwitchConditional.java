import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class SwitchConditional extends VoidVisitorAdapter<Object> {
    private ArrayList<Node> mSwitchNodes = new ArrayList<>();

    SwitchConditional(String codePath) {
        Common.printLog("SwitchConditional(): " + codePath);
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateConditionals(com, obj);
        applySwitchTransform();
        super.visit(com, obj);
    }

    private void locateConditionals(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof SwitchStmt) {
                    mSwitchNodes.add(node);
                }
            }
        }.visitPreOrder(com);
        System.out.println("SwitchNodes : " + mSwitchNodes.size());
    }

    private void applySwitchTransform() {
        mSwitchNodes.forEach((switchNode) -> {
            ArrayList<Object> ifStmts = new ArrayList<>();
            for (SwitchEntry switchEntry : ((SwitchStmt) switchNode).getEntries()) {

                if (switchEntry.getLabels().size() != 0) {
                    // cases
                    ifStmts.add(getIfStmt(switchNode, switchEntry));
                } else {
                    if (((SwitchStmt) switchNode).getEntries().size() == 1) {
                        // default without cases
                        ifStmts.add(getIfStmt(switchNode, switchEntry));
                    } else {
                        // default with cases
                        ifStmts.add(getBlockStmt(switchEntry));
                    }
                }
            }
            for (int i = 0; i < ifStmts.size() - 1; i++) {
                ((IfStmt) ifStmts.get(i)).setElseStmt((Statement) ifStmts.get(i + 1));
            }
            switchNode.replace((IfStmt) ifStmts.get(0));
        });
    }

    private Expression getBinaryExpr(Node switchNode, SwitchEntry switchEntry) {
        BinaryExpr binaryExpr = new BinaryExpr();
        binaryExpr.setLeft(((SwitchStmt) switchNode).getSelector());
        binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
        if (switchEntry.getLabels().size() != 0) {
            binaryExpr.setRight(switchEntry.getLabels().get(0)); // case(?)
        } else {
            binaryExpr.setRight(((SwitchStmt) switchNode).getSelector()); // only default
        }
        return binaryExpr;
    }

    private BlockStmt getBlockStmt(SwitchEntry switchEntry) {
        BlockStmt blockStmt = new BlockStmt();
        switchEntry.getStatements().forEach((stmt) -> {
            if (!(stmt instanceof BreakStmt)) blockStmt.addStatement(stmt);
        });
        return blockStmt;
    }

    private IfStmt getIfStmt(Node switchNode, SwitchEntry switchEntry) {
        IfStmt ifStmt = new IfStmt();
        ifStmt.setCondition(getBinaryExpr(switchNode, switchEntry));
        ifStmt.setThenStmt(getBlockStmt(switchEntry));
        return ifStmt;
    }

}
