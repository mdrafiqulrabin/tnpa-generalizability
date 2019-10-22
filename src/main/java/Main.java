public class Main {
    public static void main(String[] args) {
        String inpPath = args[0];
        String outPath = args[1];
        new ASTExplorer(inpPath,outPath).call();
    }
}
