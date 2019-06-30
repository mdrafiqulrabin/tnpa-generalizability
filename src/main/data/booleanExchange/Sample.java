class C {

    void f(int[] array) {
        boolean swapped = false;
        for (int i = 0; i < array.length && !swapped; i++) {
            swapped = true;
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = false;
                }
            }
        }
    }
}

