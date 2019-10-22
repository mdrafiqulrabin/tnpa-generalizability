import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ASTExplorer implements Callable<Void> {

    ASTExplorer(String inpPath, String outPath) {
        if (!inpPath.endsWith("/")) {
            inpPath = inpPath + "/";
        }
        Common.mInputPath = inpPath;

        if (!outPath.endsWith("/")) {
            outPath = outPath + "/";
        }
        Common.mOutputPath = outPath;
    }

    @Override
    public Void call() {
        inspectDataset();
        return null;
    }

    private void inspectDataset() {
        String input_dir = Common.mInputPath;
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(input_dir),
                        new String[]{"java"},
                        true)
        );
        System.out.println(input_dir + " : " + javaFiles.size());

        javaFiles.forEach((javaFile) -> {
            try {
                new RenameVariable().inspectSourceCode(javaFile);
                new BooleanExchange().inspectSourceCode(javaFile);
                new SwitchConditional().inspectSourceCode(javaFile);
                new LoopExchange().inspectSourceCode(javaFile);
                new PermuteStatement().inspectSourceCode(javaFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
