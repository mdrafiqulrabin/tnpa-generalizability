class C {

    boolean contains(Object var0) {
        for (Object var1 : this.elements) {
            if (var1.equals(var0)) {
                return true;
            }
        }
        return false;
    }
}

