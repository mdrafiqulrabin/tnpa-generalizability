import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PermuteStatement extends VoidVisitorAdapter<Object> {
    private ArrayList<Node> mStatementNodes = new ArrayList<>();

    PermuteStatement() {
        System.out.println("\n[ PermuteStatement ]\n");
    }

    public void inspectSourceCode() {
        Common.inspectSourceCode(this);
    }

    @Override
    public void visit(CompilationUnit com, Object obj) {
        locateTargetStatements(com, obj);
        applyPermuteStatement(com, obj);
        super.visit(com, obj);
    }

    private void locateTargetStatements(CompilationUnit com, Object obj) {
        new TreeVisitor() {
            @Override
            public void process(Node node) {
                if (node instanceof Statement) {
                    mStatementNodes.add(node);
                }
            }
        }.visitBreadthFirst(com);
        //System.out.println("StatementNodes : " + mStatementNodes.size());
    }

    private void applyPermuteStatement(CompilationUnit com, Object obj) {
        for (int i = 0, j = 1; i < mStatementNodes.size() - 1; i++, j++)
        {
            if (mStatementNodes.get(i).getParentNode().equals(mStatementNodes.get(j).getParentNode())) {
                List<SimpleName> iIdentifiers = mStatementNodes.get(i).findAll(SimpleName.class);
                List<SimpleName> jIdentifiers = mStatementNodes.get(j).findAll(SimpleName.class);
                List<SimpleName> ijIdentifiers = iIdentifiers.stream().filter(jIdentifiers::contains).collect(Collectors.toList());
                if (ijIdentifiers.size() == 0) {
                    Statement tmpStmt = new BlockStmt();
                    mStatementNodes.get(i).replace(tmpStmt);
                    mStatementNodes.get(j).replace(mStatementNodes.get(i));
                    tmpStmt.replace(mStatementNodes.get(j));
                    Collections.swap(mStatementNodes, i, j);
                    i++; j++;
                }

                System.out.println("\n");
            }
        }
    }

}
