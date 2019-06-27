import com.github.javaparser.ast.UserDataKey;

public final class Common {
    public static final UserDataKey<Integer> VariableId = new UserDataKey<Integer>() {
    };
    public static final UserDataKey<String> VariableName = new UserDataKey<String>() {
    };

    public static void printLog(Object obj) {
        if (Flagger.showLog) {
            System.out.print(System.lineSeparator() + "[ ");
            System.out.print(obj);
            System.out.print(" ]" + System.lineSeparator());
        }
    }
}
