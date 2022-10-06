void Sort(int[] data) {
    bool check = true;
    for (int i = 0; i < data.Length && check; i++) {
        check = false;
        for (int j = 0; j < data.Length - 1 - i; j++) {
            if (data[j] > data[j+1]) {
                int temp = data[j];
                data[j] = data[j+1];
                data[j+1]= temp;
                check = true;
            }
        }
    }
}
