class C {

    int count(String var0, ArrayList<String> var1) {
        int var2 = 0;
        for (String var3 : var1) {
            if (var0.equals(var3)) {
                var2++;
            }
        }
        return var2;
    }
}

