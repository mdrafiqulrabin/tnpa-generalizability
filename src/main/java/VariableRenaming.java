import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class VariableRenaming extends VoidVisitorAdapter<Object> {
    private int mVariableCounter = 0;
    private ArrayList<Node> mVariableList = new ArrayList<>();

    VariableRenaming() {
        Common.printLog("ASTExplorer -> inspectSourceCode() -> VariableRenaming():");
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
                if (node.getChildrenNodes().size() == 0 && node instanceof VariableDeclaratorId) {
                    node.setUserData(Common.VariableId, mVariableCounter++);
                    node.setUserData(Common.VariableName, node.toString());
                    mVariableList.add(node);
                    System.out.println(node.toString() + " : " + node.getClass());
                }
            }
        }.visitDepthFirst(com);
    }

    private void applyVariableRenaming(CompilationUnit com, Object obj) {
        mVariableList.forEach((var_node) -> {
            new TreeVisitor() {
                @Override
                public void process(Node node) {
                    String oldName = var_node.getUserData(Common.VariableName);
                    if (node.toString().equals(oldName)) {
                        String newName = "var" + var_node.getUserData(Common.VariableId);
                        if (node instanceof VariableDeclaratorId) {
                            ((VariableDeclaratorId) node).setName(newName);
                        } else if (node instanceof NameExpr) {
                            ((NameExpr) node).setName(newName);
                        }
                    }
                }
            }.visitDepthFirst(com);
        });
    }
}
