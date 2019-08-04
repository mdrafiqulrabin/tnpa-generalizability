import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    @Override
    public Void call() {
        new RenameVariable().inspectSourceCode();
        new BooleanExchange().inspectSourceCode();
        new SwitchConditional().inspectSourceCode();
        new LoopExchange().inspectSourceCode();
        return null;
    }
}
