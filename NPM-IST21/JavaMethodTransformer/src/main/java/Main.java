public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            String argMsg = "Invalid number of arguments:\n" +
                    "args[0] = Input directory to original methods.\n" +
                    "args[1] = Output directory to transformed methods.\n";
            System.out.println(argMsg);
        } else {
            Common.ROOT_INPUT_DIR = args[0];
            Common.ROOT_OUTPUT_DIR = args[1];
            new ASTExplorer().call();
        }
    }
}
