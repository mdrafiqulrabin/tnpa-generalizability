import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class VariableRenaming extends VoidVisitorAdapter<Object> {
    private int mVariableCounter = 0;
    private ArrayList<Node> mVariableList = new ArrayList<>();

    @Override
    public void visit(CompilationUnit com, Object obj) {

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

        mVariableList.forEach((v_node) -> {
            new VoidVisitorAdapter<Object>() {
                @Override
                public void visit(MethodDeclaration m_node, Object obj) {
                    Node v_node = (Node) obj;
                    new TreeVisitor() {
                        @Override
                        public void process(Node node) {
                            String oldName = v_node.getUserData(Common.VariableName);
                            if (node.toString().equals(oldName)) {
                                String newName = "var" + v_node.getUserData(Common.VariableId);
                                if (node instanceof VariableDeclaratorId) {
                                    ((VariableDeclaratorId) node).setName(newName);
                                } else if (node instanceof NameExpr) {
                                    ((NameExpr) node).setName(newName);
                                }
                            }
                        }
                    }.visitDepthFirst(m_node);
                    super.visit(m_node, obj);
                }
            }.visit(com, v_node);
        });

        super.visit(com, obj);
    }
}
