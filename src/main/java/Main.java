public class Main {
    public static void main(String[] args) throws Exception {
        String testPath = "src/main/data/original/Sample.java";
        ASTExplorer astExplorer = new ASTExplorer(testPath);
        astExplorer.call();
    }
}
