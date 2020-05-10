import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class VariableRenaming extends VoidVisitorAdapter<Object> {
    private int mVariableCounter = 0;
    private final ArrayList<Node> mVariableNodes = new ArrayList<>();

    VariableRenaming() { }

    public void inspectSourceCode(CompilationUnit cu) {
        Common.setOutputPath(this);
        this.visit(cu, null);
    }

    @Override
    public void visit(CompilationUnit cu, Object obj) {
        locateVariables(cu);
        Common.applyToPlace(this, cu, mVariableNodes);
        super.visit(cu, obj);
    }

    private void locateVariables(CompilationUnit cu) {
        // collect all variables as String
        List<String> strVariables = cu.findAll(SimpleName.class)
                .stream().map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());

        // set Id and Name to each candidate variables
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (isTargetVariable(node)) {
                    while (strVariables.contains("var" + mVariableCounter)) {
                        mVariableCounter++; // increase when varN already exists
                    }
                    node.setData(Common.KEY_VARIABLE_ID, mVariableCounter++);
                    node.setData(Common.KEY_VARIABLE_NAME, node.toString());
                    mVariableNodes.add(node);
                }
            }
        }.visitPreOrder(cu);
    }

    private boolean isTargetVariable(Node node) {
        return (node instanceof SimpleName &&
                (node.getParentNode().orElse(null) instanceof Parameter
                        || node.getParentNode().orElse(null) instanceof VariableDeclarator));
    }

    public CompilationUnit applyTransformation(CompilationUnit cu, Node varNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                String oldName = varNode.getData(Common.KEY_VARIABLE_NAME);
                if (node.toString().equals(oldName)) {
                    String newName = "var" + varNode.getData(Common.KEY_VARIABLE_ID);
                    if (node instanceof SimpleName
                            && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                            && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                        ((SimpleName) node).setIdentifier(newName);
                    }
                }
            }
        }.visitPreOrder(cu);
        return cu;
    }
}
