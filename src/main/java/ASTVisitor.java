import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;

public class ASTVisitor extends VoidVisitorAdapter<Object> {
    @Override
    public void visit(MethodDeclaration m_node, Object obj) {
        System.out.println("All variables:");
        VariableCollector variableCollector = new VariableCollector();
        variableCollector.visitDepthFirst(m_node);
        System.out.println("-------------");
        checkVariables(m_node, variableCollector.getVariableNodes());
        super.visit(m_node, obj);
    }

    private void checkVariables(MethodDeclaration m_node, ArrayList<Node> variableNodes) {
        for (Node v_node : variableNodes) {
            System.out.println("Current variable: ");
            System.out.println(v_node.toString() + " : " + v_node.getClass());
            System.out.println("Usage: ");
            VariableChecker variableChecker = new VariableChecker(v_node);
            variableChecker.visit(m_node, null);
            System.out.println("-------------");
        }
    }
}

class VariableCollector extends TreeVisitor {
    private ArrayList<Node> mVariableNodes = new ArrayList<>();

    @Override
    public void process(Node node) {
        if (node.getChildrenNodes().size() == 0
                && node instanceof VariableDeclaratorId) {
            mVariableNodes.add(node);
            System.out.println(node.toString() + " : " + node.getClass());
        }
    }

    ArrayList<Node> getVariableNodes() {
        return mVariableNodes;
    }
}

class VariableChecker extends VoidVisitorAdapter<Object> {
    private Node v_node = null;
    VariableChecker(Node node) {
        v_node = node;
    }
    @Override
    public void visit(MethodDeclaration m_node, Object obj) {
        VariableRename variableRename = new VariableRename(v_node);
        variableRename.visitDepthFirst(m_node);
        super.visit(m_node, obj);
    }
}

class VariableRename extends TreeVisitor {
    private Node v_node = null;
    VariableRename(Node node) {
        v_node = node;
    }
    @Override
    public void process(Node node) {
        if (node.toString().equalsIgnoreCase(v_node.toString())) {
            System.out.println(node.toString() + " : " + node.getClass());
        }
    }
}
