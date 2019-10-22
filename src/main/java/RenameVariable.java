import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.ArrayList;

@SuppressWarnings({"WeakerAccess", "unused"})
public class RenameVariable extends VoidVisitorAdapter<Object> {
    private int mVariableCounter = 0;
    private ArrayList<Node> mVariableList = new ArrayList<>();

    RenameVariable() {
        //System.out.println("\n[ RenameVariable ]\n");
    }

    public void inspectSourceCode(File javaFile) {
        Common.inspectSourceCode(this, javaFile);
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateVariableRenaming(com, obj);
        applyVariableRenaming(com, obj);
        super.visit(com, obj);
    }

    private void locateVariableRenaming(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (isTargetVariable(node, com)) {
                    node.setData(Common.VariableId, mVariableCounter++);
                    node.setData(Common.VariableName, node.toString());
                    mVariableList.add(node);
                }
            }
        }.visitPreOrder(com);
        //System.out.println("TargetVariable : " + mVariableList);
    }

    private boolean isTargetVariable(Node node, CompilationUnit com) {
        return (node instanceof SimpleName &&
                (node.getParentNode().orElse(null) instanceof Parameter
                        || node.getParentNode().orElse(null) instanceof VariableDeclarator));
    }

    private void applyVariableRenaming(CompilationUnit com, Object obj) {
        mVariableList.forEach((var_node) -> new TreeVisitor() {
            @Override
            public void process(Node node) {
                String oldName = var_node.getData(Common.VariableName);
                if (node.toString().equals(oldName)) {
                    String newName = "var" + var_node.getData(Common.VariableId);
                    if (node instanceof SimpleName
                            && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                            && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                        ((SimpleName) node).setIdentifier(newName);
                    }
                }
            }
        }.visitPreOrder(com));
    }
}
