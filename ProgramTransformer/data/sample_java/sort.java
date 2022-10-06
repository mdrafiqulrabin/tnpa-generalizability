void sort(int[] data) {
        boolean check = true;
        for (int i = 0; i < data.length && check; i++) {
                check = false;
                for (int j = 0; j < data.length - 1 - i; j++) {
                        if (data[j] > data[j+1]) {
                                int temp = data[j];
                                data[j] = data[j+1];
                                data[j+1]= temp;
                                check = true;
                        }
                }
        }
}
