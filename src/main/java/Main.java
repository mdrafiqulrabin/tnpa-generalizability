public class Main {
    public static void main(String[] args) {
        /* root folder for input  -> '~/methods'
         * root folder for output -> '~/transforms'
         *
         * extracted single method of project should be in 'methods' folder
         * separate folder for each refactoring will be created in 'transforms' folder
         */

        String inpPath = args[0];
        String outPath = args[1];
        new ASTExplorer(inpPath,outPath).call();
    }
}
