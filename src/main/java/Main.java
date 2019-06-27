public class Main {
    public static void main(String[] args) throws Exception {
        String testPath = "src/main/data/Sample.java";
        ASTExplorer astExplorer = new ASTExplorer(testPath);
        astExplorer.call();
    }
}
