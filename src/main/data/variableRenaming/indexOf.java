class C {

    int indexOf(Object var0) {
        int var1 = 0;
        for (Object var2 : this.elements) {
            if (var2.equals(var0)) {
                return var1;
            }
            var1++;
        }
        return -1;
    }
}

