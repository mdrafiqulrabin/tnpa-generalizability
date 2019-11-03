import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BooleanExchange extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private ArrayList<Node> mBooleanNodes = new ArrayList<>();

    BooleanExchange() {
        //System.out.println("\n[ BooleanExchange ]\n");
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
        locateBooleanVariables(com, obj);
        Common.applyToPlace(this, com, mJavaFile, mBooleanNodes);
        super.visit(com, obj);
    }

    private void locateBooleanVariables(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                Node booleanNode = getBooleanVariable(node, com);
                if (booleanNode != null) {
                    mBooleanNodes.add(booleanNode);
                }
            }
        }.visitPreOrder(com);
        //System.out.println("BooleanVariable : " + mBooleanList);
    }

    public CompilationUnit applyTransformation(CompilationUnit com, Node bolNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof BooleanLiteralExpr
                        && !(node.getParentNode().orElse(null) instanceof BinaryExpr) ) {
                    // i.e. true/false -> false/true
                    //Node curNode = Objects.requireNonNull(node.getParentNode().orElse(null)).findFirst(SimpleName.class).orElse(null);
                    Node curNode = null;
                    for (SimpleName sn : Objects.requireNonNull(node.getParentNode().orElse(null)).findAll(SimpleName.class)) {
                        if (!sn.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())) {
                            curNode = sn;
                        }
                    }
                    if (curNode != null && curNode.toString().equals(bolNode.toString())) {
                        ((BooleanLiteralExpr) node).setValue(!((BooleanLiteralExpr) node).getValue());
                    }
                } else if (node instanceof NameExpr && node.toString().equals(bolNode.toString())) {
                    if (node.getParentNode().orElse(null) instanceof UnaryExpr) {
                        // i.e. !x -> !!x
                        //((NameExpr) node).setName("!" + node.toString());
                        // i.e. !x -> x
                        node.getParentNode().orElse(null).replace(node);
                    } else if (node.getParentNode().orElse(null) instanceof BinaryExpr) {
                        // i.e. x && y -> !x && !y; x == true -> !x == true
                        ((NameExpr) node).setName("!" + node.toString());
                    } else if (node.getParentNode().orElse(null) instanceof Statement
                        || node.getParentNode().orElse(null) instanceof MethodCallExpr) {
                        // i.e. call(x) -> call(!x)
                        ((NameExpr) node).setName("!" + node.toString());
                    } else if (node.getParentNode().orElse(null) instanceof AssignExpr) {
                        if (((AssignExpr) node.getParentNode().orElse(null)).getValue().toString().equals(bolNode.toString())
                                || ((AssignExpr) node.getParentNode().orElse(null)).getTarget().toString().equals(bolNode.toString())) {
                            // i.e. y = x; -> y = !x;
                            AssignExpr parNode = (AssignExpr) node.getParentNode().orElse(null);
                            parNode.setValue(StaticJavaParser.parseExpression("!" + parNode.getValue()));
                        }
                    }
                }
            }
        }.visitPreOrder(com);
        return com;
    }

    private Node getBooleanVariable(Node node, CompilationUnit com) {
        if (node.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())
                && node.getParentNode().orElse(null) instanceof VariableDeclarator) {
            VariableDeclarator parentNode = (VariableDeclarator) node.getParentNode().get();
            if (parentNode.getInitializer().isPresent()) {
                Expression expression = parentNode.getInitializer().get();
                if (expression.toString().equalsIgnoreCase(Boolean.TRUE.toString())
                        || expression.toString().equalsIgnoreCase(Boolean.FALSE.toString())) {
                    for (SimpleName sn : Objects.requireNonNull(node.getParentNode()
                            .orElse(null)).findAll(SimpleName.class)) {
                        if (!sn.toString().equalsIgnoreCase(PrimitiveType.booleanType().asString())) {
                            return sn;
                        }
                    }
                }
            }
        }
        return null;
    }
}
