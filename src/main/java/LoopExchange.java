import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class LoopExchange extends VoidVisitorAdapter<Object> {
    private ArrayList<Node> mLoopNodes = new ArrayList<>();

    LoopExchange(String codePath) {
        Common.printLog("LoopExchange(): " + codePath);
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateLoops(com, obj);
        applyLoopExchange();
        super.visit(com, obj);
    }

    private void locateLoops(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof WhileStmt || node instanceof ForStmt) {
                    mLoopNodes.add(node);
                }
            }
        }.visitPreOrder(com);
        System.out.println("LoopNodes : " + mLoopNodes.size());
    }

    private void applyLoopExchange() {
        mLoopNodes.forEach((loopNode) -> {
            if (loopNode instanceof WhileStmt) {
                ForStmt nodeForStmt = new ForStmt();
                nodeForStmt.setCompare(((WhileStmt) loopNode).getCondition());
                nodeForStmt.setBody(((WhileStmt) loopNode).getBody());
                loopNode.replace(nodeForStmt);
            } else if (loopNode instanceof ForStmt) {
                BlockStmt outerBlockStmt = new BlockStmt();

                for (Expression exp : ((ForStmt) loopNode).getInitialization()) {
                    outerBlockStmt.addStatement(exp);
                }

                WhileStmt nodeWhileStmt = new WhileStmt();
                nodeWhileStmt.setCondition(((ForStmt) loopNode).getCompare().orElse(new BooleanLiteralExpr(true)));
                BlockStmt innerBlockStmt = new BlockStmt();
                for (Expression exp : ((ForStmt) loopNode).getUpdate()) {
                    innerBlockStmt.addStatement(exp);
                }
                innerBlockStmt.addStatement(0, ((ForStmt) loopNode).getBody());
                nodeWhileStmt.setBody(innerBlockStmt);

                outerBlockStmt.addStatement(nodeWhileStmt);
                loopNode.replace(outerBlockStmt);
            }
        });
    }

}
