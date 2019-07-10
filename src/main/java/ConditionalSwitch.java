import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class ConditionalSwitch extends VoidVisitorAdapter<Object> {
    private ArrayList<Node> mSwitchNodes = new ArrayList<>();

    ConditionalSwitch(String codePath) {
        Common.printLog("ConditionalSwitch(): " + codePath);
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
            ArrayList<IfStmt> ifStmts = new ArrayList<>();
            for (SwitchEntry switchEntry : ((SwitchStmt) switchNode).getEntries()) {
                IfStmt ifStmt = new IfStmt();

                if (switchEntry.getLabels().size() != 0) {
                    BinaryExpr binaryExpr = new BinaryExpr();
                    binaryExpr.setLeft(((SwitchStmt) switchNode).getSelector());
                    binaryExpr.setOperator(BinaryExpr.Operator.EQUALS);
                    binaryExpr.setRight(switchEntry.getLabels().get(0));
                    ifStmt.setCondition(binaryExpr);
                }

                BlockStmt blockStmt = new BlockStmt();
                switchEntry.getStatements().forEach((stmt) -> {
                    if (!(stmt instanceof BreakStmt)) blockStmt.addStatement(stmt);
                });
                ifStmt.setThenStmt(blockStmt);

                ifStmts.add(ifStmt);
            }
            System.out.println(ifStmts);
            for (int i = 0; i < ifStmts.size() - 1; i++) {
                ifStmts.get(i).setElseStmt(ifStmts.get(i + 1));
            }
            switchNode.replace(ifStmts.get(0));
        });
    }

}
