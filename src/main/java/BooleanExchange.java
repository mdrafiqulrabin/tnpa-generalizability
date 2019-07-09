import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class BooleanExchange extends VoidVisitorAdapter<Object> {
    private ArrayList<Node> mBooleanList = new ArrayList<>();

    BooleanExchange(String codePath) {
        Common.printLog("BooleanExchange(): " + codePath);
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
                Node booleanNode = hasBooleanVariable(node, com);
                if (booleanNode != null)
                    mBooleanList.add(booleanNode);

            }
        }.visitPreOrder(com);
        System.out.println("BooleanVariable : " + mBooleanList);
    }

    private void applyBooleanExchange(CompilationUnit com, Object obj) {
        mBooleanList.forEach((bolNode) -> {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    if (node instanceof BooleanLiteralExpr) {
                        // i.e. true/false -> false/true
                        Node curNode = node.getParentNode().orElse(null).findFirst(SimpleName.class).orElse(null);
                        if (curNode != null && curNode.toString().equals(bolNode.toString())) {
                            ((BooleanLiteralExpr) node).setValue(!((BooleanLiteralExpr) node).getValue());
                        }
                    } else if (node instanceof NameExpr && node.toString().equals(bolNode.toString())) {
                        if (node.getParentNode().orElse(null) instanceof UnaryExpr) {
                            // i.e. !x -> !!x
                            //((NameExpr) node).setName("!" + node.toString());
                            // i.e. !x -> x
                            node.getParentNode().orElse(null).replace(node);
                        } else if (node.getParentNode().orElse(null) instanceof BinaryExpr
                                && !(((BinaryExpr) node.getParentNode().orElse(null)).getOperator().equals(BinaryExpr.Operator.EQUALS)
                                || (((BinaryExpr) node.getParentNode().orElse(null)).getOperator().equals(BinaryExpr.Operator.NOT_EQUALS)))) {
                            // i.e. x && y -> !x && !y
                            ((NameExpr) node).setName("!" + node.toString());
                        } else if (node.getParentNode().orElse(null) instanceof Statement) {
                            // i.e. x -> !x
                            ((NameExpr) node).setName("!" + node.toString());
                        }
                    }
                }
            }.visitPreOrder(com);
        });
    }

    private Node hasBooleanVariable(Node node, CompilationUnit com) {
        if (node instanceof PrimitiveType
                && node.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())
                && (node.getParentNode().orElse(null) instanceof Parameter
                || node.getParentNode().orElse(null) instanceof VariableDeclarator)) {
            return node.getParentNode().orElse(null).findFirst(SimpleName.class).orElse(null);
        }
        return null;
    }
}
