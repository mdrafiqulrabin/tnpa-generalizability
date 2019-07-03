class C {

    boolean isPrime(int var0) {
        if (var0 <= 1) {
            return false;
        }
        for (int var1 = 2; var1 * var1 <= var0; var1++) {
            if (var0 % var1 == 0) {
                return false;
            }
        }
        return true;
    }
}

