import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

import static com.github.javaparser.ast.type.PrimitiveType.BOOLEAN_TYPE;

public class BooleanExchange extends VoidVisitorAdapter<Object> {
    private boolean mIsBoolean;
    private ArrayList<Node> mBooleanList = new ArrayList<>();

    BooleanExchange() {
        Common.printLog("ASTExplorer -> inspectSourceCode() -> BooleanExchange():");
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateBooleanVariables(com, obj);
        applyBooleanExchange(com, obj);
        super.visit(com, obj);
    }

    private void locateBooleanVariables(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                System.out.println(node.toString() + " : " + node.getClass());
                if (node instanceof ExpressionStmt && isBooleanType(node)) {
                    new TreeVisitor() {
                        @Override
                        public void process(Node node) {
                            if (node.getChildrenNodes().size() == 0 && node instanceof VariableDeclaratorId) {
                                // i.e. boolean x = true, y = false; -> [x, y]
                                mBooleanList.add(node);
                            }
                        }
                    }.visitDepthFirst(node);
                }
            }
        }.visitDepthFirst(com);
    }

    private void applyBooleanExchange(CompilationUnit com, Object obj) {
        mBooleanList.forEach((bol_node) -> {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node instanceof BooleanLiteralExpr
                            && node.getParentNode().getChildrenNodes().get(0).toString().equals(bol_node.toString())) {
                        // i.e. true/false -> false/true
                        ((BooleanLiteralExpr) node).setValue(!((BooleanLiteralExpr) node).getValue());
                    } else if (node instanceof NameExpr && node.toString().equals(bol_node.toString())) {
                        if (node.getParentNode() instanceof UnaryExpr) {
                            // i.e. !x -> !!x
                            ((NameExpr) node).setName("!" + node.toString());
                        } else if (node.getParentNode() instanceof BinaryExpr
                                && !(((BinaryExpr) node.getParentNode()).getOperator().equals(BinaryExpr.Operator.equals)
                                || (((BinaryExpr) node.getParentNode()).getOperator().equals(BinaryExpr.Operator.notEquals)))) {
                            // i.e. x && y -> !x && !y
                            ((NameExpr) node).setName("!" + node.toString());
                        } else if (node.getParentNode() instanceof Statement) {
                            // i.e. x -> !x
                            ((NameExpr) node).setName("!" + node.toString());
                        }
                    }
                }
            }.visitDepthFirst(com);
        });
    }

    private boolean isBooleanType(Node node) {
        mIsBoolean = false;
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node.getChildrenNodes().size() == 0 && node instanceof PrimitiveType
                        && node.toString().equals(BOOLEAN_TYPE.toString())) {
                    // i.e. boolean x = true, y = false; -> boolean
                    mIsBoolean = true;
                }
            }
        }.visitDepthFirst(node);
        return mIsBoolean;
    }
}
