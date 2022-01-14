public class Main {
    public static void main(String[] args) {
        if (args.length == 2) {
            String inpPath = args[0];
            String outPath = args[1];
            new MethodExtractor(inpPath, outPath).call();
        } else {
            String argMsg = "Invalid number of arguments:\n" +
                    "args[0] = Input directory to original files.\n" +
                    "args[1] = Output directory to extracted methods.\n";
            System.out.println(argMsg);
        }
    }
}
