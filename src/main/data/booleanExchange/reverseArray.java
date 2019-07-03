class C {

    String[] reverseArray(final String[] array) {
        final String[] newArray = new String[array.length];
        for (int index = 0; index < array.length; index++) {
            newArray[array.length - index - 1] = array[index];
        }
        return newArray;
    }
}

