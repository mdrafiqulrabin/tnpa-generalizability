class C {

    boolean contains(Object target) {
        for (Object elem : this.elements) {
            if (elem.equals(target)) {
                return true;
            }
        }
        return false;
    }
}

