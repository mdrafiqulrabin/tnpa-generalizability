class C {

    Object get(int var0) {
        for (Object var1 : this.elements) {
            if (var1.hashCode().equals(var0)) {
                return var1;
            }
        }
        return this.defaultValue;
    }
}

