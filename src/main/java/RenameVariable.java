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
    private File mJavaFile = null;
    private int mVariableCounter = 0;
    private ArrayList<Node> mVariableNodes = new ArrayList<>();

    RenameVariable() {
        //System.out.println("\n[ RenameVariable ]\n");
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
        locateVariableRenaming(com, obj);
        Common.applyToPlace(this, com, mJavaFile, mVariableNodes);
        super.visit(com, obj);
    }

    private void locateVariableRenaming(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (isTargetVariable(node, com)) {
                    node.setData(Common.VariableId, mVariableCounter++);
                    node.setData(Common.VariableName, node.toString());
                    mVariableNodes.add(node);
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

    public CompilationUnit applyTransformation(CompilationUnit com, Node varNode) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                String oldName = varNode.getData(Common.VariableName);
                if (node.toString().equals(oldName)) {
                    String newName = "var" + varNode.getData(Common.VariableId);
                    if (node instanceof SimpleName
                            && !(node.getParentNode().orElse(null) instanceof MethodDeclaration)
                            && !(node.getParentNode().orElse(null) instanceof ClassOrInterfaceDeclaration)) {
                        ((SimpleName) node).setIdentifier(newName);
                    }
                }
            }
        }.visitPreOrder(com);
        return com;
    }
}
