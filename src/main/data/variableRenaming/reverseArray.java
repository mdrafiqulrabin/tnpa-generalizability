class C {

    String[] reverseArray(final String[] var0) {
        final String[] var1 = new String[var0.length];
        for (int var2 = 0; var2 < var0.length; var2++) {
            var1[var0.length - var2 - 1] = var0[var2];
        }
        return var1;
    }
}

