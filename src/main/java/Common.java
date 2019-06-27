public final class Common {
    public static void printLog(Object obj) {
        if (Flagger.showLog) {
            System.out.print(System.lineSeparator() + "[ ");
            System.out.print(obj);
            System.out.print(" ]" + System.lineSeparator());
        }
    }
}
