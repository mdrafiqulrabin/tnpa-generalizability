class C {

    void sort(int[] var0) {
        boolean var1 = true;
        for (int var2 = 0; var2 < var0.length && var1; var2++) {
            var1 = false;
            for (int var3 = 0; var3 < var0.length - 1 - var2; var3++) {
                if (var0[var3] > var0[var3 + 1]) {
                    int var4 = var0[var3];
                    var0[var3] = var0[var3 + 1];
                    var0[var3 + 1] = var4;
                    var1 = true;
                }
            }
        }
    }
}

